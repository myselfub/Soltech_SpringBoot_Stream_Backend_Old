package kr.co.soltech.stream.schdl.controller;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.soltech.stream.commons.model.CustomExceptionDTO;
import kr.co.soltech.stream.commons.service.CustomResponseAPI;
import kr.co.soltech.stream.commons.service.JwtProvider;
import kr.co.soltech.stream.commons.utils.SoltechStreamUtils;
import kr.co.soltech.stream.schdl.model.SchdlModel;
import kr.co.soltech.stream.schdl.model.SchdlParamDTO;
import kr.co.soltech.stream.schdl.service.SchdlService;
import lombok.RequiredArgsConstructor;

/***
 * 일정 컨트롤러 클래스
 */
@RestController
@RequestMapping("/schdl")
@RequiredArgsConstructor
public class SchdlController {
	/***
	 * 일정 서비스 인터페이스
	 */
	private final SchdlService schdlService;

	/***
	 * JWT 인증 프로바이더 클래스
	 */
	private final JwtProvider jwtProvider;

	/***
	 * 공개 일정 목록 조회
	 * 
	 * @param schdlParamDTO : 일정 조회 파라메터 DTO 클래스
	 * @param pageable      : 페이징 정보
	 * @param bindingResult : 검증 결과
	 * @return 일정 목록
	 * @throws Exception
	 */
	@GetMapping
	@CustomResponseAPI
	public Page<SchdlModel> inqSchdl(@ModelAttribute SchdlParamDTO schdlParamDTO, Pageable pageable,
			BindingResult bindingResult) throws Exception {
		schdlParamDTO.setSchdl_rls_yn('Y');
		schdlParamDTO.deserialize();

		return schdlService.inqSchdl(schdlParamDTO, pageable);
	}

	/***
	 * 일정 상세 조회
	 * 
	 * @param schdlParamDTO : 일정 조회 파라메터 DTO 클래스
	 * @param bindingResult : 검증 결과
	 * @param request       : HttpServletRequest
	 * @return 일정 상세
	 * @throws Exception
	 */
	@GetMapping("/dtl")
	@CustomResponseAPI
	public SchdlModel getSchdl(@ModelAttribute SchdlParamDTO schdlParamDTO, BindingResult bindingResult,
			HttpServletRequest request) throws Exception {
		if (bindingResult.hasErrors()) {
			throw CustomExceptionDTO.of(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}
		if (ObjectUtils.isEmpty(schdlParamDTO.getSchdlId())) {
			throw CustomExceptionDTO.of("ERROR_0001", "schdlId", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}
		try {
			SoltechStreamUtils.base62Decode(schdlParamDTO.getSchdlId());
		} catch (Exception e) {
			throw CustomExceptionDTO.of("ERROR_0001", "schdlId", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}

		String tokenUserId = jwtProvider.getUserId(request);
		if (!ObjectUtils.isEmpty(tokenUserId)) {
			schdlParamDTO.setSchdl_user_id(tokenUserId);
		}
		schdlParamDTO.deserialize();

		return schdlService.getSchdl(schdlParamDTO);
	}

	/***
	 * 일정 등록/수정
	 * 
	 * @param schdlModel    : 일정 모델 클래스
	 * @param bindingResult : 검증 결과
	 * @param request       : HttpServletRequest
	 * @return 성공/에러
	 * @throws Exception
	 */
	@PostMapping
	@CustomResponseAPI(status = HttpStatus.CREATED)
	public String upsertSchdl(@RequestBody SchdlModel schdlModel, BindingResult bindingResult,
			HttpServletRequest request) throws Exception {
		if (bindingResult.hasErrors()) {
			throw CustomExceptionDTO.of(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}
		String tokenUserId = jwtProvider.getUserId(request);
		if (ObjectUtils.isEmpty(tokenUserId)) {
			throw CustomExceptionDTO.of("ERROR_0002", HttpStatus.FORBIDDEN.value(),
					new AccessDeniedException("Unallowed user."));
		}
		schdlModel = schdlModel.toBuilder().schdlUserId(tokenUserId).build();

		if (ObjectUtils.isEmpty(schdlModel.getSchdlId()) || "0".equals(schdlModel.getSchdlId())) {
			schdlModel = schdlModel.toBuilder().schdlId(SoltechStreamUtils.createEncodeId()).build();
		} else {
			try {
				SoltechStreamUtils.base62Decode(schdlModel.getSchdlId());
			} catch (Exception e) {
				throw CustomExceptionDTO.of("ERROR_0001", "schdlId", HttpStatus.BAD_REQUEST.value(),
						new BadRequestException("Validation Error"));
			}
		}

		schdlModel.deserialize();
		schdlModel.prePersist();
		int updatedRows = schdlService.upsertSchdl(schdlModel);
		if (updatedRows == 0) {
			throw CustomExceptionDTO.of("ERROR_0000", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("No matching results."));
		}

		return "SUCCESS_0000";
	}

	/***
	 * 일정 삭제
	 * 
	 * @param schdlParamDTO : 일정 조회 파라메터 DTO 클래스
	 * @return 성공/에러
	 * @throws Exception
	 */
	@DeleteMapping
	@CustomResponseAPI(status = HttpStatus.NO_CONTENT)
	public String deleteSchdl(@ModelAttribute SchdlParamDTO schdlParamDTO, HttpServletRequest request)
			throws Exception {
		if (ObjectUtils.isEmpty(schdlParamDTO.getSchdlId())) {
			throw CustomExceptionDTO.of("ERROR_0001", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}
		schdlParamDTO.deserialize();
		SchdlModel schdlModel = SchdlModel.builder().schdlId(schdlParamDTO.getSchdlId()).build();
		schdlModel.deserialize();
		schdlModel.preUpdate();

		int updatedRows = schdlService.deleteSchdl(schdlModel);
		if (updatedRows == 0) {
			throw CustomExceptionDTO.of("ERROR_0000", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("No matching results."));
		}

		return "SUCCESS_0000";
	}

	/***
	 * 내 일정 목록 조회
	 * 
	 * @param schdlParamDTO : 일정 조회 파라메터 DTO 클래스
	 * @param pageable      : 페이징 정보
	 * @param bindingResult : 검증 결과
	 * @param request       : HttpServletRequest
	 * @return 일정 목록
	 * @throws Exception
	 */
	@GetMapping("/my")
	@CustomResponseAPI
	public Page<SchdlModel> inqMySchdl(@ModelAttribute SchdlParamDTO schdlParamDTO, Pageable pageable,
			BindingResult bindingResult, HttpServletRequest request) throws Exception {
		String tokenUserId = jwtProvider.getUserId(request);
		if (ObjectUtils.isEmpty(tokenUserId)) {
			throw CustomExceptionDTO.of("ERROR_0002", HttpStatus.FORBIDDEN.value(),
					new AccessDeniedException("Unallowed user."));
		}
		schdlParamDTO.setSchdl_user_id(tokenUserId);
		schdlParamDTO.deserialize();

		return schdlService.inqSchdl(schdlParamDTO, pageable);
	}

	/***
	 * 내 일정 삭제
	 * 
	 * @param schdlParamDTO : 일정 조회 파라메터 DTO 클래스
	 * @return 성공/에러
	 * @throws Exception
	 */
	@DeleteMapping("/my")
	@CustomResponseAPI(status = HttpStatus.NO_CONTENT)
	public String deleteMySchdl(@ModelAttribute SchdlParamDTO schdlParamDTO, HttpServletRequest request)
			throws Exception {
		if (ObjectUtils.isEmpty(schdlParamDTO.getSchdlId())) {
			throw CustomExceptionDTO.of("ERROR_0001", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}
		try {
			SoltechStreamUtils.base62Decode(schdlParamDTO.getSchdlId());
		} catch (Exception e) {
			throw CustomExceptionDTO.of("ERROR_0001", "schdlId", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}

		String tokenUserId = jwtProvider.getUserId(request);
		if (ObjectUtils.isEmpty(tokenUserId)) {
			throw CustomExceptionDTO.of("ERROR_0002", HttpStatus.FORBIDDEN.value(),
					new AccessDeniedException("Unallowed user."));
		}
		schdlParamDTO.setSchdl_user_id(tokenUserId);
		schdlParamDTO.deserialize();
		SchdlModel schdlModel = SchdlModel.builder().schdlId(schdlParamDTO.getSchdlId())
				.schdlUserId(schdlParamDTO.getSchdlUserId()).build();
		schdlModel.deserialize();
		schdlModel.preUpdate();

		int updatedRows = schdlService.deleteSchdl(schdlModel);
		if (updatedRows == 0) {
			throw CustomExceptionDTO.of("ERROR_0000", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("No matching results."));
		}

		return "SUCCESS_0000";
	}
}