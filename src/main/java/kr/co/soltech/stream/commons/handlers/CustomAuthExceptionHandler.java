package kr.co.soltech.stream.commons.handlers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.soltech.stream.commons.model.CustomExceptionDTO;
import lombok.extern.slf4j.Slf4j;

/***
 * 커스텀 인증/인가 에러 핸들러 클래스
 */
@Slf4j
@Component
public class CustomAuthExceptionHandler implements AccessDeniedHandler, AuthenticationEntryPoint {
	/***
	 * Spring 예외 처리 핸들러 리졸브
	 */
	private final HandlerExceptionResolver handlerExceptionResolver;

	/***
	 * 생성자
	 * 
	 * @param handlerExceptionResolver : Spring 예외 처리 핸들러
	 */
	public CustomAuthExceptionHandler(
			@Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver) {
		this.handlerExceptionResolver = handlerExceptionResolver;
	}

	/***
	 * 인가(권한) 에러 처리 메소드
	 */
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		handlerExceptionResolver.resolveException(request, response, null,
				exceptionByStatus(response, accessDeniedException));
	}

	/***
	 * 인증 에러 처리 메소드
	 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		handlerExceptionResolver.resolveException(request, response, null, exceptionByStatus(response, authException));
	}

	/***
	 * Http 상태 코드에 따른 예외로 변경
	 * 
	 * @param response  : HttpResponse
	 * @param exception : 기존 예외
	 * @return
	 */
	private Exception exceptionByStatus(HttpServletResponse response, Exception exception) {
		int status = response.getStatus();
		if (status == HttpStatus.UNAUTHORIZED.value() || exception instanceof AuthenticationException) {
			exception = CustomExceptionDTO.of("ERROR_0002", HttpStatus.UNAUTHORIZED.value(), exception);
		} else if (status == HttpStatus.FORBIDDEN.value() || exception instanceof AccessDeniedException) {
			exception = CustomExceptionDTO.of("ERROR_0003", HttpStatus.FORBIDDEN.value(), exception);
		}

		return exception;
	}
}