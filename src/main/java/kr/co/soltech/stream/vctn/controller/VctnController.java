package kr.co.soltech.stream.vctn.controller;

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
import kr.co.soltech.stream.commons.model.CustomExceptionDTO;
import kr.co.soltech.stream.commons.service.CustomResponseAPI;
import kr.co.soltech.stream.commons.service.JwtProvider;
import kr.co.soltech.stream.vctn.model.VctnModel;
import kr.co.soltech.stream.vctn.model.VctnModelId;
import kr.co.soltech.stream.vctn.model.VctnParamDTO;
import kr.co.soltech.stream.vctn.service.VctnService;
import lombok.RequiredArgsConstructor;

/***
 * 휴가 컨트롤러 클래스
 */
@RestController
@RequestMapping("/vctn")
@RequiredArgsConstructor
public class VctnController {
	/***
	 * 휴가 서비스 인터페이스
	 */
	private final VctnService vctnService;

	/***
	 * JWT 인증 프로바이더 클래스
	 */
	private final JwtProvider jwtProvider;

	/***
	 * 휴가 목록 조회
	 * 
	 * @param vctnParamDTO  : 휴가 조회 파라메터 DTO 클래스
	 * @param pageable      : 페이징 정보
	 * @param bindingResult : 검증 결과
	 * @return 휴가 목록
	 * @throws Exception
	 */
	@GetMapping
	@CustomResponseAPI
	public Page<VctnModel> inqVctn(@ModelAttribute @Validated VctnParamDTO vctnParamDTO, Pageable pageable,
			BindingResult bindingResult) throws Exception {
		vctnParamDTO.deserialize();

		return vctnService.inqVctn(vctnParamDTO, pageable);
	}

	/***
	 * 휴가 상세 조회
	 * 
	 * @param vctnModelId   : 휴가 모델 ID 클래스
	 * @param bindingResult : 검증 결과
	 * @return 휴가 상세
	 * @throws Exception
	 */
	@GetMapping("/dtl")
	@CustomResponseAPI
	public VctnModel getVctn(@ModelAttribute @Validated VctnModelId vctnModelId, BindingResult bindingResult)
			throws Exception {
		if (bindingResult.hasErrors()) {
			throw CustomExceptionDTO.of(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}
		vctnModelId.deserialize();

		return vctnService.getVctn(vctnModelId);
	}

	/***
	 * 휴가 등록/수정
	 * 
	 * @param vctnModel     : 휴가 모델 클래스
	 * @param bindingResult : 검증 결과
	 * @return 성공/에러
	 * @throws Exception
	 */
	@PostMapping
	@CustomResponseAPI(status = HttpStatus.CREATED)
	public String upsertVctn(@RequestBody @Validated VctnModel vctnModel, BindingResult bindingResult)
			throws Exception {
		if (bindingResult.hasErrors()) {
			throw CustomExceptionDTO.of(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}

		vctnModel.deserialize();
		vctnModel.preModel();
		vctnModel.prePersist();
		int updatedRows = vctnService.upsertVctn(vctnModel);
		if (updatedRows == 0) {
			throw CustomExceptionDTO.of("ERROR_0000", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("No matching results."));
		}

		return "SUCCESS_0000";
	}

	/***
	 * 휴가 삭제
	 * 
	 * @param vctnModelId   : 휴가 모델 ID 클래스
	 * @param bindingResult : 검증 결과
	 * @return 성공/에러
	 * @throws Exception
	 */
	@DeleteMapping
	@CustomResponseAPI(status = HttpStatus.NO_CONTENT)
	public String deleteVctn(@ModelAttribute @Validated VctnModelId vctnModelId, BindingResult bindingResult)
			throws Exception {
		vctnModelId.deserialize();
		VctnModel vctnModel = VctnModel.builder().vctnUserId(vctnModelId.getVctnUserId())
				.vctnSeCd(vctnModelId.getVctnSeCd()).vctnBgngYmd(vctnModelId.getVctnBgngYmd()).build();
		vctnModel.deserialize();
		vctnModel.preModel();
		vctnModel.preUpdate();

		int updatedRows = vctnService.deleteVctn(vctnModel);
		if (updatedRows == 0) {
			throw CustomExceptionDTO.of("ERROR_0000", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("No matching results."));
		}

		return "SUCCESS_0000";
	}

	/***
	 * 내 휴가 목록 조회
	 * 
	 * @param vctnParamDTO  : 휴가 조회 파라메터 DTO 클래스
	 * @param pageable      : 페이징 정보
	 * @param bindingResult : 검증 결과
	 * @param request       : HttpServletRequest
	 * @return 휴가 목록
	 * @throws Exception
	 */
	@GetMapping("/my")
	@CustomResponseAPI
	public Page<VctnModel> inqMyVctn(@ModelAttribute @Validated VctnParamDTO vctnParamDTO, Pageable pageable,
			BindingResult bindingResult, HttpServletRequest request) throws Exception {
		String tokenUserId = jwtProvider.getUserId(request);
		if (ObjectUtils.isEmpty(tokenUserId)) {
			throw CustomExceptionDTO.of("ERROR_0002", HttpStatus.FORBIDDEN.value(),
					new AccessDeniedException("Unallowed user."));
		}

		vctnParamDTO.setVctn_user_id(tokenUserId);
		vctnParamDTO.deserialize();

		return vctnService.inqVctn(vctnParamDTO, pageable);
	}

	/***
	 * 휴가 정보 목록 조회
	 * 
	 * @param vctnParamDTO  : 휴가 조회 파라메터 DTO 클래스
	 * @param pageable      : 페이징 정보
	 * @param bindingResult : 검증 결과
	 * @return 휴가 정보 목록
	 * @throws Exception
	 */
	@GetMapping("/info")
	@CustomResponseAPI
	public Page<VctnModel> inqVctnInfo(@ModelAttribute @Validated VctnParamDTO vctnParamDTO, Pageable pageable,
			BindingResult bindingResult) throws Exception {
		vctnParamDTO.deserialize();

		return vctnService.inqVctnInfo(vctnParamDTO, pageable);
	}

	/***
	 * 내 휴가 정보 목록 조회
	 * 
	 * @param vctnParamDTO  : 휴가 조회 파라메터 DTO 클래스
	 * @param pageable      : 페이징 정보
	 * @param bindingResult : 검증 결과
	 * @param request       : HttpServletRequest
	 * @return 휴가 정보 목록
	 * @throws Exception
	 */
	@GetMapping("/my/info")
	@CustomResponseAPI
	public Page<VctnModel> inqMyVctnInfo(@ModelAttribute @Validated VctnParamDTO vctnParamDTO, Pageable pageable,
			BindingResult bindingResult, HttpServletRequest request) throws Exception {
		String tokenUserId = jwtProvider.getUserId(request);
		tokenUserId = "[USER_ID]";
		if (ObjectUtils.isEmpty(tokenUserId)) {
			throw CustomExceptionDTO.of("ERROR_0002", HttpStatus.FORBIDDEN.value(),
					new AccessDeniedException("Unallowed user."));
		}

		vctnParamDTO.setVctn_user_id(tokenUserId);
		vctnParamDTO.deserialize();

		return vctnService.inqVctnInfo(vctnParamDTO, pageable);
	}
}
