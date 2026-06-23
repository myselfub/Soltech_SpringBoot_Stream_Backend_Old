package kr.co.soltech.stream.commons.service;

import java.security.Key;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.soltech.stream.authrt.service.AuthrtService;
import kr.co.soltech.stream.commons.utils.SoltechStreamUtils;
import kr.co.soltech.stream.user.model.UserModel;
import kr.co.soltech.stream.user.service.UserService;
import lombok.extern.slf4j.Slf4j;

/***
 * JWT 인증 프로바이더 클래스
 */
@Slf4j
@Component
public class JwtProvider {
	/***
	 * 사용자 ID를 가지고 있는 claims의 키
	 */
	private final String KEY_USER_ID = "user_id";

	/***
	 * 사용자 명을 가지고 있는 claims의 키
	 */
	private final String KEY_USER_NM = "user_nm";

	/***
	 * 사용자 IP를 가지고 있는 claims의 키
	 */
	private final String KEY_USER_IP = "user_ip";

	/***
	 * 사용자 권한를 가지고 있는 claims의 키
	 */
	private final String KEY_AUTHRT_SE_CD = "user_authrt_se_cd";

	/***
	 * 헤더 Authorization의 시작 문자
	 */
	private final String HEADER_START_STR = "Bearer ";

	/***
	 * 사용자 서비스 인터페이스
	 */
	private final UserService userService;

	/***
	 * 암호화에 사용될 실제 키
	 */
	private final Key key;

	/***
	 * 토큰 만료 시간(분)
	 */
	private final long expireTime;

	/***
	 * 타임존
	 */
	private final String timeZone;

	/***
	 * 권한 서비스 인터페이스
	 */
	private final AuthrtService authrtService;

	/***
	 * 권한 업데이트 주기
	 */
	@Value("${soltech.stream.authrt.interval:3600}")
	private long updateInterval;

	/***
	 * 마지막 권한 업데이트 시간
	 */
	private LocalDateTime authrtLastUpdateDateTime = LocalDateTime.now();

	/***
	 * 권한별 메뉴 목록
	 */
	private Map<String, List<String>> authrtMap = new HashMap<String, List<String>>();

	/***
	 * 개발모드 여부
	 */
	@Value("${soltech.stream.is-dev-mode:false}")
	private String isDevMode;

	/***
	 * 생성자 (jwtKey가 없으면 secretKey로 만듬)
	 * 
	 * @param userService     : 사용자 서비스 인터페이스
	 * @param secretKey       : 암호화에 사용될 문자열 키
	 * @param secretKeyLength : 암호화에 필요한 문자열 키 길이
	 * @param algorithm       : 적용할 암호화 알고리즘
	 * @param jwtKey          : 암호화된 문자열 키
	 * @param expireTime      : 토큰 만료 시간(분)
	 * @param timeZone        : 타임존
	 */
	public JwtProvider(UserService userService, @Value("${soltech.stream.secret.key:[SECRET_KEY]}") String secretKey,
			@Value("${soltech.stream.secret.key.length}") int secretKeyLength,
			@Value("${soltech.stream.secret.algorithm:AES}") String algorithm,
			@Value("${soltech.stream.jwt.key}") String jwtKey,
			@Value("${soltech.stream.jwt.expire:1440}") long expireTime,
			@Value("${spring.jpa.properties.hibernate.jdbc.time_zone:Asia/Seoul}") String timeZone,
			AuthrtService authrtService) {
		this.userService = userService;
		try {
			if (!ObjectUtils.isEmpty(algorithm) && (ObjectUtils.isEmpty(jwtKey)
					|| SoltechStreamUtils.decode(algorithm, secretKey, jwtKey).length() < secretKeyLength)) {
				jwtKey = SoltechStreamUtils.encode(algorithm, secretKey,
						SoltechStreamUtils.repeatString(secretKey, secretKeyLength));
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			this.key = Keys.hmacShaKeyFor(jwtKey.getBytes());
		}
		this.expireTime = expireTime;
		this.timeZone = timeZone;
		this.authrtService = authrtService;
	}

	/***
	 * 토큰 생성
	 * 
	 * @param userModel : 사용자 모델 클래스
	 * @return 생성된 토큰
	 * @throws Exception
	 */
	public String getToken(UserModel userModel) throws Exception {
		return createToken(userModel, expireTime);
	}

	/***
	 * 토큰 생성 실제 메소드
	 * 
	 * @param userModel  : 사용자 모델 클래스
	 * @param expireTime : 토큰 만료 시간(분)
	 * @return 생성된 토큰
	 * @throws Exception
	 */
	private String createToken(UserModel userModel, long expireTime) throws Exception {
		Claims claims = Jwts.claims();
		claims.put(KEY_USER_ID, userModel.getUserId());
		claims.put(KEY_USER_NM, userModel.getUserNm());
		claims.put(KEY_USER_IP, userModel.getUserIp());
		claims.put(KEY_AUTHRT_SE_CD, userModel.getUserAuthrtSeCd());
		LocalDateTime issuedAt = LocalDateTime.now();
		LocalDateTime expiration = issuedAt.plusMinutes(expireTime);

		return Jwts.builder().setClaims(claims).setIssuer("Soltech").setAudience("SoltechStream")
				.setIssuedAt(Date.from(issuedAt.atZone(ZoneId.of(timeZone)).toInstant()))
				.setExpiration(Date.from(expiration.atZone(ZoneId.of(timeZone)).toInstant()))
				.signWith(key, SignatureAlgorithm.HS512).compact();
	}

	/***
	 * 토큰을 파싱하여 사용자 ID 추출
	 * 
	 * @param token : 파싱할 토큰
	 * @return 사용자 ID
	 */
	public String getUserId(String token) {
		String userId = null;
		try {
			userId = parseClaims(token).get(KEY_USER_ID, String.class);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}

		return userId;
	}

	/***
	 * 헤더를 파싱하여 사용자 ID 추출
	 * 
	 * @param request : HttpServletRequest
	 * @return 사용자 ID
	 */
	public String getUserId(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		if (!ObjectUtils.isEmpty(token) && token.startsWith(HEADER_START_STR)) {
			token = token.substring(HEADER_START_STR.length());
			if (validateToken(token)) {
				String tokenUserId = getUserId(token);
				if (!ObjectUtils.isEmpty(tokenUserId)) {
					return tokenUserId;
				}
			}
		}

		return null;
	}

	/***
	 * 토큰을 파싱하여 사용자 권한 구분 코드 추출
	 * 
	 * @param token : 파싱할 토큰
	 * @return 사용자 권한 구분 코드
	 */
	public String getUserAuthrtSeCd(String token) {
		String authrtSeCd = null;
		try {
			authrtSeCd = parseClaims(token).get(KEY_AUTHRT_SE_CD, String.class);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}

		return authrtSeCd;
	}

	/***
	 * 토큰 파싱
	 * 
	 * @param token : 파싱할 토큰
	 * @return 파싱된 토큰
	 * @throws Exception
	 */
	private Claims parseClaims(String token) throws Exception {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
	}

	/***
	 * 토큰 검증
	 * 
	 * @param token : 검증할 토큰
	 * @return 성공 유무
	 */
	public boolean validateToken(String token) {
		boolean valid = true;
		try {
			Claims claims = parseClaims(token);
			valid = valid && Date.from(LocalDateTime.now().atZone(ZoneId.of(timeZone)).toInstant())
					.before(claims.getExpiration());
			valid = valid && userService.existsById(claims.get(KEY_USER_ID, String.class));
		} catch (Exception e) {
			valid = false;
			log.error(e.getMessage());
		}
		return valid;
	}

	/***
	 * 토큰 검증
	 * 
	 * @param token : 검증할 토큰
	 * @param ip    : 검증할 IP
	 * @return 성공 유무
	 */
	public boolean validateToken(String token, String ip) {
		boolean valid = true;
		try {
			Claims claims = parseClaims(token);
			valid = valid && Date.from(LocalDateTime.now().atZone(ZoneId.of(timeZone)).toInstant())
					.before(claims.getExpiration());
			valid = valid && claims.get(KEY_USER_IP, String.class).equals(ip);
			valid = valid && !ObjectUtils.isEmpty(claims.get(KEY_USER_ID, String.class));
			// loadUserByUsername에서 검증
			// valid = valid && userService.existsById(claims.get(KEY_USER_ID,
			// String.class));
		} catch (Exception e) {
			valid = false;
			log.error(e.getMessage());
		}
		return valid;
	}

	/***
	 * 토큰의 ID와 파라메터의 ID가 일치하는지 확인(본인 확인)
	 * 
	 * @param request : HttpServletRequest
	 * @param userId  : 사용자 ID
	 * @return
	 */
	public boolean isSelf(HttpServletRequest request, String userId) {
		if (!ObjectUtils.isEmpty(isDevMode) && "true".equals(isDevMode.toLowerCase())) {
			return true;
		}

		String token = request.getHeader("Authorization");
		if (!ObjectUtils.isEmpty(token) && token.startsWith(HEADER_START_STR)) {
			token = token.substring(HEADER_START_STR.length());
			if (validateToken(token)) {
				String tokenUserId = getUserId(token);
				if (!ObjectUtils.isEmpty(tokenUserId) && tokenUserId.equals(userId)) {
					return true;
				}
			}
		}

		return false;
	}

	/***
	 * 권한 업데이트 주기 변경
	 * 
	 * @param updateInterval : 권한 업데이트 주기
	 */
	public void setUpdateInterval(long updateInterval) {
		this.updateInterval = updateInterval;
	}

	/***
	 * 권한 목록 조회
	 * 
	 * @return 권한 목록
	 */
	public Map<String, List<String>> getAuthrtMap() {
		updateAuthrt();
		return authrtMap;
	}

	/***
	 * 권한 업데이트
	 */
	public void updateAuthrt() {
		Duration duration = Duration.between(authrtLastUpdateDateTime, LocalDateTime.now());
		if (ObjectUtils.isEmpty(authrtMap) || duration.getSeconds() >= updateInterval) {
			try {
				authrtMap = authrtService.inqUrlAuth();
				authrtLastUpdateDateTime = LocalDateTime.now();
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
	}
}
