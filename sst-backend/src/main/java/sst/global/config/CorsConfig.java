package sst.global.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

	private final List<String> allowOrigins = List.of("http://localhost:5173");
	private final List<String> allowMethods = List.of("GET", "POST", "PUT", "PATCH","DELETE", "OPTIONS", "HEADERS");
	
	@Bean
	@Primary
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		
		// 허용 출처
		config.setAllowedOrigins(allowOrigins);
		// 허용 httpmethod
		config.setAllowedMethods(allowMethods);
		// 허용 헤더
		config.setAllowedHeaders(List.of("*"));
		// httpOnly 쿠키 브라우저 포함
		config.setAllowCredentials(true);
		// preflight 1시간유지
		config.setMaxAge(3600L);
		
		// 모든 경로에 설정적용
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		
		return source;
	}
}














