package sst.global.files.core;

import java.util.List;
import java.util.function.Consumer;

import org.springframework.web.multipart.MultipartFile;

import sst.global.files.dto.FileActionContext;
import sst.global.files.dto.FileUploadResult;

/**
 * 파일 업로드 및 관리를 위한 유연한 옵션과 작업 메서드를 제공하는 인터페이스입니다.
 */
public interface FileOptions {

    /** 
     * 허용할 파일 확장자를 지정합니다. (기본값: jpg, jpeg, png, gif, webp) 
     */
    FileOptions allow(List<String> extensions);

    /** 
     * 최대 파일 크기를 바이트 단위로 지정합니다. 
     */
    FileOptions maxSize(long bytes);

    /** 
     * 최대 파일 크기를 문자열 형식(예: "10MB", "500KB")으로 지정합니다. 
     */
    FileOptions maxSize(String sizeExpression); 

    /** 
     * 저장 시 날짜별 폴더(/yyyyMMdd/) 생성 여부를 지정합니다. (기본값: true) 
     */
    FileOptions useDateFolder(boolean use);

    /** 
     * 도메인 경로 하위에 추가할 세부 경로를 지정합니다. 
     */
    FileOptions subPath(String path);

    /** 
     * 작업 성공 시 실행할 콜백을 지정합니다. 추가/삭제된 파일 정보를 포함하는 컨텍스트를 제공합니다. 
     */
    FileOptions onSuccess(Consumer<FileActionContext> action);

    /** 
     * 작업 실패 시 실행할 콜백을 지정합니다. 에러 정보와 작업 중단 시점의 컨텍스트를 제공합니다. 
     */
    FileOptions onFailure(Consumer<FileActionContext> action);

    // --- [STORE / ADD] 신규 추가 전용 ---

    /** 단일 파일을 저장합니다. */
    FileUploadResult storeFileOnce(MultipartFile file);

    /** 다중 파일 리스트를 저장합니다. */
    List<FileUploadResult> storeFileAll(List<MultipartFile> files);

    /** 파일(MultipartFile) 또는 리스트(List) 형태의 입력을 자동 판단하여 저장합니다. */
    List<FileUploadResult> storeFile(Object fileOrList);

    // --- [REPLACE] 엄격한 교체 (기존 파일 삭제 필수 : 기존 파일이 없다면 진행되지 않습니다.) ---

    /** 기존 파일을 삭제하고 새 파일을 업로드합니다. (기존 경로가 유효해야 함) */
    FileUploadResult replaceFileOnce(MultipartFile newFile, String oldPath);

    /** 여러 개의 기존 파일을 삭제하고 새 파일 리스트를 업로드합니다. */
    List<FileUploadResult> replaceFileAll(List<MultipartFile> newFiles, List<String> oldPaths);

    /** 단일/다중 파일 및 경로 입력을 자동 판단하여 교체 작업을 수행합니다. */
    List<FileUploadResult> replaceFile(Object fileOrList, Object pathOrList);

    // --- [UPDATE / SAVE] 조건부 교체 (없으면 신규 추가) ---

    /** 기존 경로가 있으면 교체(삭제 후 저장)하고, 없으면 신규로 추가합니다. */
    FileUploadResult updateFileOnce(MultipartFile newFile, String oldPath);

    /** 다중 파일에 대해 교체 또는 추가 작업을 수행합니다. */
    List<FileUploadResult> updateFileAll(List<MultipartFile> newFiles, List<String> oldPaths);

    /** 단일/다중 파일 및 경로 입력을 자동 판단하여 업데이트 작업을 수행합니다. */
    List<FileUploadResult> updateFile(Object fileOrList, Object pathOrList);

    // --- [DELETE] 삭제 전용 ---

    /** 단일 파일을 물리적으로 삭제합니다. */
    void deleteFileOnce(String path);

    /** 다중 파일 리스트를 물리적으로 삭제합니다. */
    void deleteFileAll(List<String> paths);

    /** 단일 또는 리스트 형태의 경로를 자동 판단하여 삭제합니다. */
    void deleteFile(Object pathOrList);
}