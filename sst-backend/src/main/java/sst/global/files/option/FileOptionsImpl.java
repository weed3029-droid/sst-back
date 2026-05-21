package sst.global.files.option;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import sst.global.config.FileStorageConfig;
import sst.global.exception.CustomException;
import sst.global.exception.ErrorCode;
import sst.global.files.core.FileOptions;
import sst.global.files.dto.FileActionContext;
import sst.global.files.dto.FileUploadResult;
import sst.global.files.storage.FileStorage;

/**
 * 파일 업로드 및 관리를 위한 상세 옵션을 구현하는 클래스입니다.
 * <p>
 * 본 클래스는 빌더 패턴 스타일의 설정 인터페이스를 제공하며, 모든 파일 작업은 원자적(Atomic)으로 수행됩니다.
 * 작업 중 예외 발생 시 물리적으로 저장된 파일들에 대한 자동 롤백 기능을 지원하며,
 * 콜백 함수(onSuccess, onFailure) 실행 시 내부적인 예외 래핑을 통해 Spring의 트랜잭션 관리를 완벽하게 지원합니다.
 * </p>
 */
@Slf4j
public class FileOptionsImpl implements FileOptions {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final List<String> DEFAULT_EXT = List.of("jpg", "jpeg", "png", "gif", "webp");

    private final FileStorage storage;
    private final FileStorageConfig config;
    private final String domainName;

    private List<String> allowedExtensions = DEFAULT_EXT;
    private long customMaxSize = -1;
    private boolean useDateFolder = true;
    private String subPath = "";
    
    private Consumer<FileActionContext> successAction;
    private Consumer<FileActionContext> failureAction;

    /**
     * @param storage 파일 저장소 엔진
     * @param config 서버 전역 파일 설정
     * @param domainName 도메인 구분자 (상위 루트 폴더명)
     */
    public FileOptionsImpl(FileStorage storage, FileStorageConfig config, String domainName) {
        this.storage = storage;
        this.config = config;
        this.domainName = domainName;
    }

    // --- 설정 메서드 (Method Chaining) ---

    @Override
    public FileOptions allow(List<String> extensions) {
        this.allowedExtensions = extensions;
        return this;
    }

    @Override
    public FileOptions maxSize(long bytes) {
        this.customMaxSize = bytes;
        return this;
    }

    @Override
    public FileOptions maxSize(String sizeExpression) {
        if (sizeExpression != null && !sizeExpression.isBlank()) {
            this.customMaxSize = DataSize.parse(sizeExpression).toBytes();
        }
        return this;
    }

    @Override
    public FileOptions useDateFolder(boolean use) {
        this.useDateFolder = use;
        return this;
    }

    @Override
    public FileOptions subPath(String path) {
        this.subPath = (path == null) ? "" : sanitizePath(path);
        return this;
    }

    /**
     * 성공 시 실행할 로직을 정의합니다. 
     * 내부적으로 try-catch로 래핑되어 있어, 실행 중 발생하는 예외는 RuntimeException으로 전환되어 트랜잭션 롤백을 유도합니다.
     */
    @Override
    public FileOptions onSuccess(Consumer<FileActionContext> action) {
        this.successAction = context -> {
            try {
                if (action != null) action.accept(context);
            } catch (Exception e) {
                log.error("[FileOptions] onSuccess 콜백 실행 중 예외 발생: {}", e.getMessage());
                throw (e instanceof RuntimeException) ? (RuntimeException) e : new RuntimeException(e);
            }
        };
        return this;
    }

    /**
     * 실패 시 실행할 로직을 정의합니다.
     * 내부적으로 try-catch로 래핑되어 있으며, 실패 처리 중 발생한 예외 또한 트랜잭션 관리에 영향을 줍니다.
     */
    @Override
    public FileOptions onFailure(Consumer<FileActionContext> action) {
        this.failureAction = context -> {
            try {
                if (action != null) action.accept(context);
            } catch (Exception e) {
                log.error("[FileOptions] onFailure 콜백 실행 중 예외 발생: {}", e.getMessage());
                throw (e instanceof RuntimeException) ? (RuntimeException) e : new RuntimeException(e);
            }
        };
        return this;
    }

    // --- [CORE] 실행 엔진 ---

    /**
     * 파일 작업을 통합 실행하며 실패 시 초기 단계로 완전 롤백을 수행합니다.
     * 1. 기존 파일은 지우지 않고 리스트만 확보합니다.
     * 2. 신규 파일을 물리적으로 저장합니다.
     * 3. onSuccess(DB 처리 등)를 실행합니다. 여기서 예외 발생 시 신규 파일만 지웁니다.
     * 4. 모든 작업이 성공하면 비로소 기존 파일을 실제로 삭제합니다.
     * 
     * @param newFiles 새로 저장할 파일 리스트
     * @param oldPaths 삭제할 기존 파일 경로 리스트
     * @param isUpdateMode true인 경우 교체 대상(oldPaths)이 없어도 신규 저장으로 진행하며, 
     *                     false인 경우 교체 대상이 없으면 삭제 에러를 발생시키고 롤백합니다.
     * @return 저장된 파일 결과 리스트
     * @throws CustomException 파일 처리 중 오류 발생 시
     */
    private List<FileUploadResult> execute(List<MultipartFile> newFiles, List<String> oldPaths, boolean isUpdateMode) {
        List<FileUploadResult> savedResults = new ArrayList<>();
        List<FileUploadResult> deletedResults = new ArrayList<>();

        try {
            // 1. Replace 모드 검증
            if (!isUpdateMode && (oldPaths == null || oldPaths.isEmpty())) {
                throw new CustomException(ErrorCode.FILE_DELETE_ERROR);
            }

            // 2. 삭제 대상 정보만 수집 (실제 삭제는 가장 마지막 단계로 미룸)
            if (oldPaths != null) {
                for (String path : oldPaths) {
                    if (path != null && !path.isBlank()) {
                        deletedResults.add(FileUploadResult.builder()
                                .filePath(path)
                                .build());
                    }
                }
            }

            // 3. 신규 파일 검증 및 저장 (물리 저장 수행)
            if (newFiles != null) {
                for (MultipartFile file : newFiles) {
                    if (file == null || file.isEmpty()) continue;
                    validate(file);
                    savedResults.add(storage.processUpload(file, buildRelativePath()));
                }
            }

            // 작업 결과 컨텍스트 생성
            FileActionContext context = FileActionContext.builder()
                    .savedFiles(savedResults)
                    .deletedFiles(deletedResults)
                    .build();

            // 4. 성공 콜백 실행 (DB 트랜잭션 포함)
            // 만약 DB 저장 중 예외가 발생하면 catch 블록으로 이동합니다.
            if (successAction != null) successAction.accept(context);

            // 5. [Finalize] 모든 로직이 성공했을 때만 기존 파일을 실제로 삭제 (Commit 단계)
            if (oldPaths != null) {
                for (String path : oldPaths) {
                    try {
                        storage.delete(path); 
                    } catch (Exception ex) {
                        // 이미 DB와 신규 저장은 끝났으므로 삭제 실패는 로그만 남기고 전체 흐름에 지장을 주지 않음
                        log.error("[FileOptions] 최종 확정 단계에서 기존 파일 삭제 실패 (경로: {}): {}", path, ex.getMessage());
                    }
                }
            }

            return savedResults;

        } catch (Exception e) {
            // [Full Rollback] 초기 상태로 회귀
            // 기존 파일(oldPaths)은 아직 지우지 않았으므로, 새로 저장된 파일만 지우면 완벽히 초기 상태가 됩니다.
            log.warn("[FileOptions] 작업 실패 - 신규 저장된 {}건의 파일을 삭제하여 초기 상태로 롤백합니다.", savedResults.size());
            
            savedResults.forEach(res -> {
                try {
                    storage.delete(res.getFilePath());
                } catch (Exception ex) {
                    log.error("[Rollback] 신규 파일 삭제 실패: {}", res.getFilePath());
                }
            });

            // 실패 콜백 실행
            FileActionContext errorContext = FileActionContext.builder()
                    .savedFiles(savedResults)
                    .deletedFiles(deletedResults)
                    .exception(e)
                    .build();

            if (failureAction != null) failureAction.accept(errorContext);
            throw (e instanceof RuntimeException) ? (RuntimeException) e : new RuntimeException(e);
        }
    }

    // --- [STORE / ADD] 신규 추가 전용 ---

    /**
     * 단일 파일을 시스템에 저장합니다.
     * @param file 저장할 MultipartFile
     * @return 업로드 결과 정보
     */
    @Override
    public FileUploadResult storeFileOnce(MultipartFile file) {
        List<FileUploadResult> results = execute(List.of(file), null, true);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * 다중 파일 리스트를 시스템에 저장합니다.
     * @param files 저장할 MultipartFile 리스트
     * @return 업로드 결과 정보 리스트
     */
    @Override
    public List<FileUploadResult> storeFileAll(List<MultipartFile> files) {
        return execute(files, null, true);
    }

    /**
     * 입력 타입(단일 파일 또는 리스트)을 자동 판단하여 저장합니다.
     * @param fileOrList MultipartFile 또는 List&lt;MultipartFile&gt;
     * @return 업로드 결과 정보 리스트
     */
    @Override
    public List<FileUploadResult> storeFile(Object fileOrList) {
        return handleFlexibleInput(fileOrList, null, true);
    }

    // --- [REPLACE] 엄격한 교체 (기존 파일 삭제 필수) ---

    /**
     * 기존 파일을 물리적으로 삭제한 후 새 파일로 교체합니다.
     * @param newFile 새 파일
     * @param oldPath 삭제할 기존 파일의 가상 경로
     * @return 업로드 결과 정보
     */
    @Override
    public FileUploadResult replaceFileOnce(MultipartFile newFile, String oldPath) {
        List<FileUploadResult> results = execute(List.of(newFile), List.of(oldPath), false);
        return results.get(0);
    }

    /**
     * 여러 개의 기존 파일들을 삭제하고 새 파일들로 교체합니다.
     * @param newFiles 새 파일 리스트
     * @param oldPaths 삭제할 기존 파일 가상 경로 리스트
     * @return 업로드 결과 정보 리스트
     */
    @Override
    public List<FileUploadResult> replaceFileAll(List<MultipartFile> newFiles, List<String> oldPaths) {
        return execute(newFiles, oldPaths, false);
    }

    /**
     * 입력 타입(단일/다중 파일 및 경로)을 자동 판단하여 엄격한 교체 작업을 수행합니다.
     * @param fileOrList 새 파일(들)
     * @param pathOrList 삭제할 기존 경로(들)
     * @return 업로드 결과 정보 리스트
     */
    @Override
    public List<FileUploadResult> replaceFile(Object fileOrList, Object pathOrList) {
        return handleFlexibleInput(fileOrList, pathOrList, false);
    }

    // --- [UPDATE / SAVE] 조건부 교체 (없으면 추가) ---

    /**
     * 기존 파일 경로가 제공된 경우 교체하며, 경로가 비어있거나 없는 경우 신규 저장으로 동작합니다.
     * @param newFile 저장 또는 교체할 파일
     * @param oldPath 삭제할 기존 파일의 가상 경로 (Nullable)
     * @return 업로드 결과 정보
     */
    @Override
    public FileUploadResult updateFileOnce(MultipartFile newFile, String oldPath) {
        List<String> paths = (oldPath == null || oldPath.isBlank()) ? null : List.of(oldPath);
        List<FileUploadResult> results = execute(List.of(newFile), paths, true);
        return results.get(0);
    }

    /**
     * 다중 파일에 대해 조건부 업데이트(교체 또는 신규 저장)를 수행합니다.
     * @param newFiles 저장 또는 교체할 파일 리스트
     * @param oldPaths 삭제할 기존 경로 리스트 (Nullable)
     * @return 업로드 결과 정보 리스트
     */
    @Override
    public List<FileUploadResult> updateFileAll(List<MultipartFile> newFiles, List<String> oldPaths) {
        return execute(newFiles, oldPaths, true);
    }

    /**
     * 입력 타입을 자동 판단하여 조건부 업데이트 작업을 수행합니다.
     * @param fileOrList 저장 또는 교체할 파일(들)
     * @param pathOrList 삭제할 기존 경로(들)
     * @return 업로드 결과 정보 리스트
     */
    @Override
    public List<FileUploadResult> updateFile(Object fileOrList, Object pathOrList) {
        return handleFlexibleInput(fileOrList, pathOrList, true);
    }

    // --- [DELETE] 삭제 전용 --- 

    /**
     * 단일 파일을 시스템에서 물리적으로 삭제합니다.
     * @param path 삭제할 파일의 가상 경로
     */
    @Override
    public void deleteFileOnce(String path) {
        execute(null, List.of(path), true);
    }

    /**
     * 여러 파일을 시스템에서 물리적으로 삭제합니다.
     * @param paths 삭제할 파일 경로 리스트
     */
    @Override
    public void deleteFileAll(List<String> paths) {
        execute(null, paths, true);
    }

    /**
     * 단일 또는 리스트 형태의 경로를 받아 물리 삭제를 수행합니다.
     * @param pathOrList 삭제할 경로(String) 또는 경로 리스트(List&lt;String&gt;)
     */
    @Override
    public void deleteFile(Object pathOrList) {
        handleFlexibleInput(null, pathOrList, true);
    }

    // --- 내부 유틸리티 ---

    /**
     * 단일 객체 또는 리스트 형태의 파라미터를 내부 표준 리스트 포맷으로 변환합니다.
     */
    @SuppressWarnings("unchecked")
    private List<FileUploadResult> handleFlexibleInput(Object files, Object paths, boolean isUpdate) {
        List<MultipartFile> fileList = null;
        if (files instanceof List) fileList = (List<MultipartFile>) files;
        else if (files instanceof MultipartFile) fileList = List.of((MultipartFile) files);

        List<String> pathList = null;
        if (paths instanceof List) pathList = (List<String>) paths;
        else if (paths instanceof String) pathList = List.of((String) paths);

        return execute(fileList, pathList, isUpdate);
    }

    /**
     * 파일의 크기 및 확장자 유효성을 검사합니다.
     */
    private void validate(MultipartFile file) {
        long limit = (customMaxSize > 0) ? customMaxSize : config.getUploadMaxSize();
        if (file.getSize() > limit) throw new CustomException(ErrorCode.FILE_SIZE_EXCEEDED);
        
        String ext = getExtension(file.getOriginalFilename());
        if (!allowedExtensions.contains(ext)) throw new CustomException(ErrorCode.INVALID_FILE_TYPE);
    }

    /**
     * 설정된 옵션을 바탕으로 파일이 저장될 상대 경로를 생성합니다.
     */
    private String buildRelativePath() {
        StringBuilder sb = new StringBuilder(domainName);
        if (!subPath.isBlank()) sb.append("/").append(subPath);
        if (useDateFolder) sb.append("/").append(LocalDate.now().format(DATE_FORMATTER));
        return sb.toString();
    }

    /**
     * 파일명에서 확장자를 추출합니다.
     */
    private static String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    /**
     * 경로 문자열을 정제합니다. (슬래시 중복 제거 등)
     */
    private static String sanitizePath(String path) {
        return path.replace("\\", "/").replaceAll("/{2,}", "/").replaceAll("^/|/$", "");
    }
}