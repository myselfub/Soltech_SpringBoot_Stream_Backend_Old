package kr.co.soltech.stream.commons.handlers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.soltech.stream.commons.model.CustomUserDetails;
import kr.co.soltech.stream.commons.service.JwtProvider;
import kr.co.soltech.stream.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/***
 * JWT 인증 필터 클래스
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	/***
	 * 헤더의 시작 문자
	 */
	private final String HEADER_START_STR = "Bearer ";

	/***
	 * 사용자 서비스 인터페이스
	 */
	private final UserService userService;

	/***
	 * JWT 인증 프로바이더 클래스
	 */
	private final JwtProvider jwtProvider;

	/***
	 * 커스텀 인증/인가 에러 핸들러 클래스
	 */
	private final CustomAuthExceptionHandler customAuthExceptionHandler;

	/***
	 * 개발모드 여부
	 */
	private final String isDevMode;

	/***
	 * JWT 인증 필터 메소드
	 */
	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {
		String token = request.getHeader("Authorization");
		try {
			if (!ObjectUtils.isEmpty(token) && token.startsWith(HEADER_START_STR)) {
				boolean isFail = true;
				token = token.substring(HEADER_START_STR.length());
				if (jwtProvider.validateToken(token, request.getRemoteAddr())) {
					String userId = jwtProvider.getUserId(token);
					if (!ObjectUtils.isEmpty(userId)) {
						UserDetails userDetails = userService.loadUserByUsername(userId.toString());

						if (userDetails != null) {
							((CustomUserDetails) userDetails).getUserModel().updateUserIp(request.getRemoteAddr());
							Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
									userDetails.getAuthorities());
							SecurityContextHolder.getContext().setAuthentication(authentication);
							isFail = false;
							// @PreAuthorize("hasRole('ADMIN')")
						}
					}
				}

				if (isFail) {
					throw new InsufficientAuthenticationException("Invalid token information.");
				}
			} else {
				// DB에서 URL을 불러와 인증이 필요한 경로면 실패 처리(DB에 없으면 인증필요 없음)
				Map<String, List<String>> authrtMap = jwtProvider.getAuthrtMap();
				boolean isAuthenticated = authrtMap.keySet().stream()
						.filter(authSeCd -> !ObjectUtils.isEmpty(authSeCd) && !"anonymousUser".equals(authSeCd)
								&& !"00000".equals(authSeCd)) // 누구나
						.anyMatch(authSeCd -> authrtMap.get(authSeCd).contains(request.getRequestURI()));
				if (isAuthenticated) {
					throw new AuthenticationCredentialsNotFoundException("No authentication.");
				}
			}
		} catch (AuthenticationException ae) {
			if (ObjectUtils.isEmpty(isDevMode) || !"true".equals(isDevMode.toLowerCase())) {
				response.setStatus(HttpStatus.UNAUTHORIZED.value());
				customAuthExceptionHandler.commence(request, response, ae);

				return;
			}
		} catch (Exception e) {
			if (ObjectUtils.isEmpty(isDevMode) || !"true".equals(isDevMode.toLowerCase())) {
				response.setStatus(HttpStatus.UNAUTHORIZED.value());
				customAuthExceptionHandler.commence(request, response,
						new AuthenticationException("No authentication.") {
							private static final long serialVersionUID = 1L;
						});

				return;
			}
		}
		filterChain.doFilter(request, response);
	}
}
