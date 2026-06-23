package kr.co.soltech.stream.atdc.controller;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
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
import kr.co.soltech.stream.atdc.model.AtdcModel;
import kr.co.soltech.stream.atdc.model.AtdcModelId;
import kr.co.soltech.stream.atdc.model.AtdcParamDTO;
import kr.co.soltech.stream.atdc.service.AtdcService;
import kr.co.soltech.stream.commons.model.CustomExceptionDTO;
import kr.co.soltech.stream.commons.service.CustomResponseAPI;
import kr.co.soltech.stream.commons.service.JwtProvider;
import lombok.RequiredArgsConstructor;

/***
 * 근태 컨트롤러 클래스
 */
@RestController
@RequestMapping("/atdc")
@RequiredArgsConstructor
public class AtdcController {
	/***
	 * 근태 서비스 인터페이스
	 */
	private final AtdcService atdcService;

	/***
	 * JWT 인증 프로바이더 클래스
	 */
	private final JwtProvider jwtProvider;

	/***
	 * 근태 목록 조회
	 * 
	 * @param atdcParamDTO  : 근태 조회 파라메터 DTO 클래스
	 * @param pageable      : 페이징 정보
	 * @param bindingResult : 검증 결과
	 * @return 근태 목록
	 * @throws Exception
	 */
	@GetMapping
	@CustomResponseAPI
	public Page<AtdcModel> inqAtdc(@ModelAttribute @Validated AtdcParamDTO atdcParamDTO, Pageable pageable,
			BindingResult bindingResult) throws Exception {
		atdcParamDTO.deserialize();

		return atdcService.inqAtdc(atdcParamDTO, pageable);
	}

	/***
	 * 근태 상세 조회
	 * 
	 * @param atdcModelId   : 근태 모델 ID 클래스
	 * @param bindingResult : 검증 결과
	 * @return 근태 상세
	 * @throws Exception
	 */
	@GetMapping("/dtl")
	@CustomResponseAPI
	public AtdcModel getAtdc(@ModelAttribute @Validated AtdcModelId atdcModelId, BindingResult bindingResult)
			throws Exception {
		if (bindingResult.hasErrors()) {
			throw CustomExceptionDTO.of(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}

		atdcModelId.deserialize();

		return atdcService.getAtdc(atdcModelId);
	}

	/***
	 * 근태 등록/수정
	 * 
	 * @param atdcModel     : 근태 모델 클래스
	 * @param bindingResult : 검증 결과
	 * @return 성공/에러
	 * @throws Exception
	 */
	@PostMapping
	@CustomResponseAPI(status = HttpStatus.CREATED)
	public String upsertAtdc(@RequestBody @Validated AtdcModel atdcModel, BindingResult bindingResult)
			throws Exception {
		if (bindingResult.hasErrors()) {
			throw CustomExceptionDTO.of(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}

		atdcModel.deserialize();
		atdcModel.preModel();
		atdcModel.prePersist();

		int updatedRows = atdcService.upsertAtdc(atdcModel);
		if (updatedRows == 0) {
			throw CustomExceptionDTO.of("ERROR_0000", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("No matching results."));
		}

		return "SUCCESS_0000";
	}

	/***
	 * 근태 삭제
	 * 
	 * @param atdcModelId   : 근태 모델 ID 클래스
	 * @param bindingResult : 검증 결과
	 * @return 성공/에러
	 * @throws Exception
	 */
	@DeleteMapping
	@CustomResponseAPI(status = HttpStatus.NO_CONTENT)
	public String deleteAtdc(@ModelAttribute @Validated AtdcModelId atdcModelId, BindingResult bindingResult)
			throws Exception {
		if (bindingResult.hasErrors()) {
			throw CustomExceptionDTO.of(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}

		atdcModelId.deserialize();

		AtdcModel atdcModel = AtdcModel.builder().atdcUserId(atdcModelId.getAtdcUserId())
				.atdcYmd(atdcModelId.getAtdcYmd()).atdcTm(atdcModelId.getAtdcTm()).atdcSeCd(atdcModelId.getAtdcSeCd())
				.build();
		atdcModel.deserialize();
		atdcModel.preModel();
		atdcModel.preUpdate();

		int updatedRows = atdcService.deleteAtdc(atdcModel);
		if (updatedRows == 0) {
			throw CustomExceptionDTO.of("ERROR_0000", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("No matching results."));
		}

		return "SUCCESS_0000";
	}

	/***
	 * 내 근태 목록 조회
	 * 
	 * @param atdcParamDTO  : 근태 조회 파라메터 DTO 클래스
	 * @param pageable      : 페이징 정보
	 * @param bindingResult : 검증 결과
	 * @param request       : HttpServletRequest
	 * @return 근태 목록
	 * @throws Exception
	 */
	@GetMapping("/my")
	@CustomResponseAPI
	public Page<AtdcModel> inqMyAtdc(@ModelAttribute @Validated AtdcParamDTO atdcParamDTO, Pageable pageable,
			BindingResult bindingResult, HttpServletRequest request) throws Exception {
		String tokenUserId = jwtProvider.getUserId(request);
		if (ObjectUtils.isEmpty(tokenUserId)) {
			throw CustomExceptionDTO.of("ERROR_0002", HttpStatus.FORBIDDEN.value(),
					new AccessDeniedException("Unallowed user."));
		}
		atdcParamDTO.setAtdc_user_id(tokenUserId);
		atdcParamDTO.deserialize();

		return atdcService.inqAtdc(atdcParamDTO, pageable);
	}

	/***
	 * 근태 정보 목록 조회
	 * 
	 * @param atdcParamDTO  : 근태 조회 파라메터 DTO 클래스
	 * @param pageable      : 페이징 정보
	 * @param bindingResult : 검증 결과
	 * @return 근태 정보 목록
	 * @throws Exception
	 */
	@GetMapping("/info")
	@CustomResponseAPI
	public Page<AtdcModel> inqAtdcInfo(@ModelAttribute @Validated AtdcParamDTO atdcParamDTO, Pageable pageable,
			BindingResult bindingResult) throws Exception {
		return atdcService.inqAtdcInfo(atdcParamDTO, pageable);
	}

	/***
	 * 내 근태 정보 목록 조회
	 * 
	 * @param atdcParamDTO  : 근태 조회 파라메터 DTO 클래스
	 * @param pageable      : 페이징 정보
	 * @param bindingResult : 검증 결과
	 * @param request       : HttpServletRequest
	 * @return 근태 정보 목록
	 * @throws Exception
	 */
	@GetMapping("/my/info")
	@CustomResponseAPI
	public Page<AtdcModel> inqMyAtdcInfo(@ModelAttribute @Validated AtdcParamDTO atdcParamDTO, Pageable pageable,
			BindingResult bindingResult, HttpServletRequest request) throws Exception {
		String tokenUserId = jwtProvider.getUserId(request);
		if (ObjectUtils.isEmpty(tokenUserId)) {
			throw CustomExceptionDTO.of("ERROR_0002", HttpStatus.FORBIDDEN.value(),
					new AccessDeniedException("Unallowed user."));
		}
		atdcParamDTO.setAtdc_user_id(tokenUserId);
		atdcParamDTO.deserialize();

		return atdcService.inqAtdcInfo(atdcParamDTO, pageable);
	}
}