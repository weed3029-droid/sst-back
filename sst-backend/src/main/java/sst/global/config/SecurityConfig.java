package sst.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import sst.global.security.filter.JwtAuthenticationFilter;
import sst.global.security.handler.OAuth2SuccessHandler;
import sst.global.security.service.CustomOAuth2UserService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final CorsConfigurationSource corsConfigurationSource;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	
	private final CustomOAuth2UserService customOAuth2UserService;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			// csrf 비활성화
			.csrf(config -> config.disable())
			// rest api stateless 설정
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.cors(cors -> cors.configurationSource(corsConfigurationSource))
			.authorizeHttpRequests( auth -> auth
					// 에러 페이지 포워딩 시 인증 블락(403)을 방지하여 프론트로 401이 무사히 가게 함
					.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
					// 로그아웃시 인증된 사용자만 주소 요청
					.requestMatchers("/api/auth/logout","/api/auth/me").authenticated()
					// 회원가입과 로그인에 관련된 주소는 모든 사용자에게 허용
					.requestMatchers("/api/auth/**").permitAll()
					// 고객지원 조회 API는 비회원도 접근 가능
					.requestMatchers(
					        "/api/customersupport/notice",
					        "/api/customersupport/faq"
					    ).permitAll()
					// 홈/랜딩 카드 조회 API는 비회원도 접근 가능
					.requestMatchers("/api/home/**").permitAll()
					// 그외의 모든 요청은 인증 필요
					.anyRequest().authenticated()
			)
			// ======= OAuth2 로그인 설정 추가 =======
	        .oauth2Login(oauth2 -> oauth2
	                .userInfoEndpoint(userInfo -> userInfo
	                        .userService(customOAuth2UserService) // 사용자 정보 처리
	                )
	                .successHandler(oAuth2SuccessHandler) // 성공 시 토큰 발급 및 리다이렉트
	        )
			.exceptionHandling(exception -> 
					exception
							.authenticationEntryPoint((request, response, authException) ->
								response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
							.accessDeniedHandler((request, response, accessDeniedException) ->
								response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden"))
			)
			// jwt 필터 추가
			.addFilterBefore(jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class);
			
			
		
		
		return http.build();
	}
}









