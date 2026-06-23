package kr.co.soltech.stream.commons.configs;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import kr.co.soltech.stream.commons.handlers.CustomAuthExceptionHandler;
import kr.co.soltech.stream.commons.handlers.JwtAuthenticationFilter;
import kr.co.soltech.stream.commons.service.CustomAuthorizationManager;
import kr.co.soltech.stream.commons.service.JwtProvider;
import kr.co.soltech.stream.user.service.UserService;
import lombok.extern.slf4j.Slf4j;

/***
 * 스프링 시큐리티 설정 클래스
 */
@Slf4j
@EnableWebSecurity
@Configuration
public class SecurityConfig {
	/***
	 * 인가 없는 URL 목록
	 */
	private final String[] PERMIT_URI = { "/user/login", "/user/sign-in" };

	/***
	 * 사용자 서비스 인터페이스
	 */
	private final UserService userService;

	/***
	 * 커스텀 인증/인가 에러 핸들러 클래스
	 */
	private final CustomAuthExceptionHandler customAuthExceptionHandler;

	/***
	 * JWT 인증 프로바이더 클래스
	 */
	private final JwtProvider jwtProvider;

	/***
	 * 커스텀 인가 매니저
	 */
	private final CustomAuthorizationManager customAuthorizationManager;

	/***
	 * 개발모드 여부
	 */
	@Value("${soltech.stream.is-dev-mode:false}")
	private String isDevMode;

	/***
	 * 생성자
	 * 
	 * @param userService                : 사용자 서비스 인터페이스
	 * @param customAuthExceptionHandler : 커스텀 인증/인가 에러 핸들러 클래스
	 * @param jwtProvider                : JWT 인증 프로바이더 클래스
	 */
	public SecurityConfig(UserService userService, CustomAuthExceptionHandler customAuthExceptionHandler,
			JwtProvider jwtProvider, CustomAuthorizationManager customAuthorizationManager) {
		this.userService = userService;
		this.customAuthExceptionHandler = customAuthExceptionHandler;
		this.jwtProvider = jwtProvider;
		this.customAuthorizationManager = customAuthorizationManager;
	}

	/***
	 * 스프링 시큐리티 필터 설정(CSRF, CORS, AUTH, JWT 등)
	 * 
	 * @param httpSecurity
	 * @return 시큐리티 필터 체인 클래스
	 * @throws Exception
	 */
	@Bean
	SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf(csrf -> csrf.disable());
		// httpSecurity.cors(Customizer.withDefaults());
		// httpSecurity.cors(cors -> cors.disable());
		httpSecurity.cors(cors -> cors.configurationSource(corsConfigurationSource()));

		httpSecurity.formLogin(formLogin -> formLogin.disable());
		httpSecurity.httpBasic(httpBasic -> httpBasic.disable()); // AbstractHttpConfigurer::disable
		httpSecurity.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		httpSecurity.addFilterBefore(
				new JwtAuthenticationFilter(userService, jwtProvider, customAuthExceptionHandler, isDevMode),
				UsernamePasswordAuthenticationFilter.class);

		httpSecurity.authorizeHttpRequests(authorize -> authorize.requestMatchers(PERMIT_URI).permitAll().anyRequest()
				.access(customAuthorizationManager));

		httpSecurity.exceptionHandling(exception -> exception.authenticationEntryPoint(customAuthExceptionHandler)
				.accessDeniedHandler(customAuthExceptionHandler));

		return httpSecurity.build();
	}

	/***
	 * 스프링 시큐리티 인증 처리 설정
	 * 
	 * @param authenticationConfiguration
	 * @return 인증 처리 클래스
	 * @throws Exception
	 */
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	/***
	 * 패스워드 인코딩 설정
	 * 
	 * @return 패스워드 인코더 클래스
	 */
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/***
	 * CORS 설정
	 * 
	 * @return CORS 설정 클래스
	 */
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		// corsConfiguration.addAllowedOrigin("http://[CLIENTIP]:8081");
		corsConfiguration.addAllowedOriginPattern("*");
		corsConfiguration.addAllowedHeader("*");
		corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
		/*
		 * corsConfiguration.addAllowedMethod("GET");
		 * corsConfiguration.addAllowedMethod("POST");
		 * corsConfiguration.addAllowedMethod("DELETE");
		 * corsConfiguration.addAllowedMethod("PUT");
		 */
		corsConfiguration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
		urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

		return urlBasedCorsConfigurationSource;
	}
}
