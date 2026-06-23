package kr.co.soltech.stream.user.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.soltech.stream.commons.model.CustomExceptionDTO;
import kr.co.soltech.stream.commons.model.CustomUserDetails;
import kr.co.soltech.stream.commons.service.CustomResponseAPI;
import kr.co.soltech.stream.commons.service.JwtProvider;
import kr.co.soltech.stream.commons.utils.SoltechStreamUtils;
import kr.co.soltech.stream.user.model.UserModel;
import kr.co.soltech.stream.user.model.UserParamDTO;
import kr.co.soltech.stream.user.service.UserService;
import lombok.RequiredArgsConstructor;

/***
 * 사용자 컨트롤러 클래스
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
	/***
	 * 사용자 서비스 인터페이스
	 */
	private final UserService userService;

	/***
	 * 패스워드 인코더 인터페이스
	 */
	private final PasswordEncoder passwordEncoder;

	/***
	 * JWT 인증 프로바이더 클래스
	 */
	private final JwtProvider jwtProvider;

	/***
	 * 인증 처리 인터페이스
	 */
	private final AuthenticationManager authenticationManager;

	/***
	 * 사용자 목록 조회
	 * 
	 * @param userParamDTO  : 사용자 조회 파라메터 DTO 클래스
	 * @param pageable      : 페이징 정보
	 * @param bindingResult : 검증 결과
	 * @return 사용자 목록
	 * @throws Exception
	 */
	@GetMapping
	@CustomResponseAPI
	public Page<UserModel> inqUser(@ModelAttribute @Validated UserParamDTO userParamDTO, Pageable pageable,
			BindingResult bindingResult) throws Exception {
		userParamDTO.deserialize();

		return userService.inqUser(userParamDTO, pageable);
	}

	/***
	 * 사용자 상세 조회
	 * 
	 * @param userParamDTO  : 사용자 조회 파라메터 DTO 클래스
	 * @param bindingResult : 검증 결과
	 * @return 사용자 상세 조회
	 * @throws Exception
	 */
	@GetMapping("/dtl")
	@CustomResponseAPI
	public UserModel getUser(@ModelAttribute @Validated UserParamDTO userParamDTO, BindingResult bindingResult)
			throws Exception {
		if (bindingResult.hasErrors()) {
			throw CustomExceptionDTO.of(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}
		if (ObjectUtils.isEmpty(userParamDTO.getUserId())) {
			throw CustomExceptionDTO.of("ERROR_0001", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}
		userParamDTO.deserialize();

		return userService.getUser(userParamDTO);
	}

	/***
	 * 내 정보 상세 조회
	 * 
	 * @param userParamDTO  : 사용자 조회 파라메터 DTO 클래스
	 * @param bindingResult : 검증 결과
	 * @param request       : HttpServletRequest
	 * @return 내 정보 상세
	 * @throws Exception
	 */
	@GetMapping("/my")
	@CustomResponseAPI
	public UserModel getMyInfo(@ModelAttribute @Validated UserParamDTO userParamDTO, BindingResult bindingResult,
			HttpServletRequest request) throws Exception {
		String tokenUserId = jwtProvider.getUserId(request);
		if (ObjectUtils.isEmpty(tokenUserId)) {
			throw CustomExceptionDTO.of("ERROR_0002", HttpStatus.FORBIDDEN.value(),
					new AccessDeniedException("Unallowed user."));
		}
		userParamDTO.setUser_id(tokenUserId);
		userParamDTO.deserialize();

		return userService.getUser(userParamDTO);
	}

	/***
	 * 사용자 등록/수정
	 * 
	 * @param userModel     : 사용자 모델 클래스
	 * @param bindingResult : 검증 결과
	 * @return 성공/에러
	 * @throws Exception
	 */
	@PostMapping
	@CustomResponseAPI(status = HttpStatus.CREATED)
	public String upsertUser(@RequestBody @Validated UserModel userModel, BindingResult bindingResult)
			throws Exception {
		if (bindingResult.hasErrors()) {
			throw CustomExceptionDTO.of(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}
		userModel.deserialize();
		userModel.preModel();
		userModel.prePersist();
		int updatedRows = userService.upsertUser(userModel);
		if (updatedRows == 0) {
			throw CustomExceptionDTO.of("ERROR_0000", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("No matching results."));
		}

		return "SUCCESS_0000";
	}

	/***
	 * 사용자 삭제
	 * 
	 * @param userId        : 사용자 ID
	 * @param bindingResult : 검증 결과
	 * @return 성공/에러
	 * @throws Exception
	 */
	@DeleteMapping
	@CustomResponseAPI(status = HttpStatus.NO_CONTENT)
	public String deleteUser(@ModelAttribute @Validated UserParamDTO userParamDTO, BindingResult bindingResult)
			throws Exception {
		if (ObjectUtils.isEmpty(userParamDTO.getUserId())) {
			throw CustomExceptionDTO.of("ERROR_0001", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}

		UserModel userModel = UserModel.builder().userId(userParamDTO.getUserId()).build();
		userModel.deserialize();
		userModel.preModel();
		userModel.preUpdate();

		int updatedRows = userService.deleteUser(userModel);
		if (updatedRows == 0) {
			throw CustomExceptionDTO.of("ERROR_0000", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("No matching results."));
		}

		return "SUCCESS_0000";
	}

	/***
	 * 로그인
	 * 
	 * @param userParamDTO  : 사용자 모델 클래스
	 * @param bindingResult : 검증 결과
	 * @param request       : HttpServletRequest
	 * @return JWT 토큰
	 * @throws Exception
	 */
	@PostMapping("/login")
	@CustomResponseAPI
	public String login(@RequestBody @Validated UserParamDTO userParamDTO, BindingResult bindingResult,
			HttpServletRequest request) throws Exception {
		if (ObjectUtils.isEmpty(userParamDTO) || ObjectUtils.isEmpty(userParamDTO.getUserId())
				|| ObjectUtils.isEmpty(userParamDTO.getUserPw())) {
			throw CustomExceptionDTO.of("ERROR_0001", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}
		UserDetails userDetails = null;

		try {
			Authentication auth = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(userParamDTO.getUserId(), userParamDTO.getUserPw()));
			userDetails = ((CustomUserDetails) auth.getPrincipal());
			// userDetails = userService.loadUserByUsername(userModel.getUserId());
		} catch (AuthenticationException ae) {
			throw CustomExceptionDTO.of("ERROR_0004", HttpStatus.BAD_REQUEST.value(), ae);
		}

		if (userDetails == null) {
			// ID 없음
			throw CustomExceptionDTO.of("ERROR_0004", HttpStatus.BAD_REQUEST.value(),
					new BadCredentialsException("Invalid user information."));
		} else if (!passwordEncoder.matches(userParamDTO.getUserPw(), userDetails.getPassword())) {
			// PW 틀림
			throw CustomExceptionDTO.of("ERROR_0004", HttpStatus.BAD_REQUEST.value(),
					new BadCredentialsException("Invalid user information."));
		}
		UserModel tokenUerModel = ((CustomUserDetails) userDetails).getUserModel();
		tokenUerModel.updateUserIp(SoltechStreamUtils.getClientIP(request));
		String token = jwtProvider.getToken(tokenUerModel);

		return token;
	}

	/***
	 * 조직도 사용자 목록 조회
	 * 
	 * @param userParamDTO  : 사용자 조회 파라메터 DTO 클래스
	 * @param bindingResult : 검증 결과
	 * @return 사용자 목록
	 * @throws Exception
	 */
	@GetMapping("/ognz-chrt")
	@CustomResponseAPI
	public List<UserModel> inqOgnzChrtUser(@ModelAttribute @Validated UserParamDTO userParamDTO,
			BindingResult bindingResult) throws Exception {
		userParamDTO.deserialize();
		List<UserModel> userModelList = userService.inqUser(userParamDTO, null).getContent();
		List<UserModel> resultList = new ArrayList<UserModel>();
		for (UserModel userModel : userModelList) {
			resultList.add(userModel.toBuilder().userPw(null).userAuthrtSeCd(null).userAuthrtSeNm(null).userAtdcId(-1)
					.userEmpNo(null).build());
		}

		return resultList;
	}
}