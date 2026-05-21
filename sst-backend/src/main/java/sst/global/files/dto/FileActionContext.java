package sst.global.files.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

/**
 * 파일 작업 결과의 컨텍스트를 담는 객체입니다.
 * 추가된 파일 및 삭제된 파일의 상세 정보를 포함합니다.
 */
@Getter
@Builder
public class FileActionContext {
    /** 새로 저장된 파일 결과 리스트 */
    private final List<FileUploadResult> savedFiles;
    
    /** 물리적으로 삭제된 기존 파일 정보 리스트 (경로 외에 파일명 등 포함) */
    private final List<FileUploadResult> deletedFiles;
    
    /** 작업 중 발생한 예외 (onFailure 시에만 존재) */
    private final Exception exception;
}