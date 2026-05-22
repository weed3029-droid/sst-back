package sst.global.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * 파일 저장소의 물리적 경로 및 보안 설정을 담당하는 설정 클래스입니다.
 * OS별 경로 보정 및 디렉토리 권한 검증을 수행합니다.
 */
@Slf4j
@Configuration
public class FileStorageConfig {
	
	/** 
	 * 파일 저장 기준 디렉토리 경로
	 * application.yml에 설정이 없으면 '/home/sst/attachment'를 기본값으로 사용합니다.
	 */
	@Value("${file.upload-dir:/home/sst/attachment}")
	private String uploadDir;
	
	/** 파일 업로드 허용 최대 사이즈 */
	@Value("#{T(org.springframework.util.unit.DataSize).parse('${file.upload-max-size:10MB}').toBytes()}")
	private long uploadMaxSize;

	/**
	 * 서버 기동 시 설정된 경로를 OS 환경에 맞게 보정하고 유효성을 검증합니다.
	 */
	@PostConstruct
	public void init() {
		// 초기 주입된 경로를 OS 특성에 맞춰 최종 보정
		this.uploadDir = getOSSavePath(this.uploadDir);
		
		validateAndPrepare(Paths.get(this.uploadDir));
		log.info("[FileStorageConfig] OS: {} / 업로드 기준 경로: {} / 최종경로: {}", 
				System.getProperty("os.name"), uploadDir, this.uploadDir);
	}

	/**
	 * 저장/삭제 시 기준이 되는 최상위 물리 디렉토리 경로를 반환합니다.
	 */
	public Path resolveBaseDir() {
		return Paths.get(uploadDir).normalize();
	}

	/**
	 * 가상 경로의 프리픽스를 동적으로 추출합니다. (ex: /attachment)
	 */
	public String getVirtualPrefix() {
		Path path = resolveBaseDir().toAbsolutePath();
		String folderName = path.getFileName() != null ? path.getFileName().toString() : "attachment";
		return "/" + folderName;
	}

	/**
	 * 외부 리소스 로딩용 URI 경로를 반환합니다.
	 */
	public String getOSResourceUri() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) {
			return "file:///" + uploadDir.replace("\\", "/");
		}
		return "file://" + uploadDir;
	}

	/**
	 * OS별로 경로 형식을 보정합니다. 
	 * 이미 @Value에서 기본값이 보장되므로 경로 존재 여부 체크만 수행합니다.
	 */
	private String getOSSavePath(String path) {
		String os = System.getProperty("os.name").toLowerCase();
		
		if (os.contains("win")) {
			// 윈도우 환경에서 드라이브 문자가 없으면 D: 권장
			return path.contains(":") ? path : "D:" + path;
		} else if (os.contains("mac")) {
			// 맥 환경 권한 문제 방지를 위한 경로 보정
			return path.startsWith("/Users/Shared") ? path : "/Users/Shared" + path;
		}
		
		// Linux 및 기타 환경은 주입된 경로 그대로 사용
		return path;
	}

	/**
	 * 경로 권한 및 디렉토리 존재 여부를 검증합니다.
	 */
	private void validateAndPrepare(Path base) {
		if (!base.isAbsolute()) {
			log.error("[FileStorageConfig] 서버 기동 실패 - upload-dir은 절대경로여야 합니다. 현재값: {}", uploadDir);
			throw new IllegalStateException("[FileStorageConfig] upload-dir은 절대경로여야 합니다.");
		}

		if (!Files.exists(base)) {
			try {
				Files.createDirectories(base);
				log.info("[FileStorageConfig] 업로드 디렉토리 생성 완료: {}", base);
			} catch (Exception e) {
				log.error("[FileStorageConfig] 서버 기동 실패 - 업로드 디렉토리 생성 실패: {}", uploadDir, e);
				throw new IllegalStateException("[FileStorageConfig] 업로드 디렉토리 생성 실패", e);
			}
		}

		if (!Files.isWritable(base)) {
			log.error("[FileStorageConfig] 서버 기동 실패 - 업로드 디렉토리 쓰기 권한 없음: {}", uploadDir);
			throw new IllegalStateException("[FileStorageConfig] 업로드 디렉토리 쓰기 권한이 없습니다.");
		}
	}

	public long getUploadMaxSize() {
		return uploadMaxSize;
	}
}