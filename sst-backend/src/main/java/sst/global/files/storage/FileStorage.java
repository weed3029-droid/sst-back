package sst.global.files.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sst.global.config.FileStorageConfig;
import sst.global.exception.CustomException;
import sst.global.exception.ErrorCode;
import sst.global.files.core.FileOptions;
import sst.global.files.core.FileProvider;
import sst.global.files.dto.FileUploadResult;
import sst.global.files.option.FileOptionsImpl;

/**
 * 로컬 디스크를 저장소로 사용하는 파일 관리 구현체입니다.
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class FileStorage implements FileProvider {
	
	private final FileStorageConfig config;

    @Override
    public FileOptions setup(String domainName) {
        return new FileOptionsImpl(this, config, sanitizePath(domainName));
    }

    @Override
    public void delete(String virtualPath) {
        if (virtualPath == null || virtualPath.isBlank()) return;

        try {
            Path baseDir = config.resolveBaseDir();
            String prefix = config.getVirtualPrefix();
            
            // 동적 프리픽스 제거 (ex: /attachment/ 제거)
            String relative = virtualPath.replaceFirst("^" + prefix + "/", "");
            Path target = baseDir.resolve(relative).normalize();

            if (!target.startsWith(baseDir)) {
                log.warn("[보안] 허용 범위 외 삭제 시도 차단: {}", target);
                return;
            }

            if (Files.isRegularFile(target)) {
                Files.delete(target);
                log.info("[파일삭제] 성공: {}", target);
            } else {
                log.warn("[파일삭제] 대상 없음: {}", target);
            }

        } catch (IOException e) {
            log.error("[파일삭제] 실패: {}", virtualPath, e);
            throw new CustomException(ErrorCode.FILE_DELETE_ERROR);
        }
    }

    public FileUploadResult processUpload(MultipartFile file, String relativePath) {
        Path baseDir   = config.resolveBaseDir();
        Path targetDir = baseDir.resolve(relativePath).normalize();
        
        if (!targetDir.startsWith(baseDir)) {
        	log.error("[보안] 허용 범위 외 저장 경로 차단: {}", targetDir);
        	throw new CustomException(ErrorCode.FILE_UPLOAD_ERROR);
        }

        String originalName = file.getOriginalFilename();
        String extension    = getExtension(originalName);
        String saveName     = UUID.randomUUID() + "." + extension;
        Path   targetFile   = targetDir.resolve(saveName);
        
        try {
            Files.createDirectories(targetDir);
            file.transferTo(targetFile.toFile());
            log.info("[파일저장] 성공: {}", targetFile);
        } catch (IOException e) {
            log.error("[파일저장] 실패: {}", targetFile, e);
            throw new CustomException(ErrorCode.FILE_UPLOAD_ERROR);
        }

        // 가상 경로 생성: 하드코딩 제거
        String prefix = config.getVirtualPrefix();
        String subPath = baseDir.relativize(targetFile).toString().replace("\\", "/");
        String virtualPath = (prefix + "/" + subPath).replaceAll("/{2,}", "/");

        return FileUploadResult.builder()
			                   .fileOrgNm(originalName)
			                   .fileSaveNm(saveName)
			                   .filePath(virtualPath)
			                   .fileExt(extension)
			                   .fileSize(file.getSize())
			                   .contentType(file.getContentType())
			                   .build();
    }

    private static String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
        	log.error("지원하지 않는 확장자: {}", filename);
        	throw new CustomException(ErrorCode.INVALID_FILE_TYPE);
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
    
    private static String sanitizePath(String path) {
        if (path == null || path.isBlank()) return "";
        return path.replace("\\", "/")
                   .replaceAll("/{2,}", "/")
                   .replaceAll("^/|/$", "");
    }
}