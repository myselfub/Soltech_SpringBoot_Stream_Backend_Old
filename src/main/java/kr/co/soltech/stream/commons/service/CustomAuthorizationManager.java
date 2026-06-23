package kr.co.soltech.stream.commons.service;

import java.util.Map;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import kr.co.soltech.stream.authrt.service.AuthrtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/***
 * 커스텀 인가 매니저
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
	/***
	 * 권한 서비스 인터페이스
	 */
	private final AuthrtService authrtService;

	/***
	 * 개발모드 여부
	 */
	@Value("${soltech.stream.is-dev-mode:false}")
	private String isDevMode;

	/***
	 * 인가(권한) 체크
	 */
	@Override
	public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
		String requestUri = object.getRequest().getRequestURI();
		String method = object.getRequest().getMethod().toUpperCase();
		Authentication auth = authentication.get();
		// List userAuth = auth.getAuthorities();

		if (auth != null && auth.isAuthenticated()) {
			String userId = auth.getName();
			try {
				// DB에서 사용자 ID와 메뉴에 따른 권한을 불러와 설정(DB에 없으면 인증 필요)
				Map<String, String> userAuth = authrtService.getUserAuthrt(userId, requestUri, method);
				if (ObjectUtils.isEmpty(isDevMode) || !"true".equals(isDevMode.toLowerCase())) {
					if (ObjectUtils.isEmpty(userAuth)) {
						return new AuthorizationDecision(false);
					} else if (ObjectUtils.isEmpty(userAuth.get("menu_url"))) {
						SecurityContextHolder.getContext().setAuthentication(null);
						return new AuthorizationDecision(false);
					}
				}
			} catch (Exception e) {
				log.error(e.getMessage());
				return new AuthorizationDecision(false);
			}
		}

		return new AuthorizationDecision(true);
	}
}