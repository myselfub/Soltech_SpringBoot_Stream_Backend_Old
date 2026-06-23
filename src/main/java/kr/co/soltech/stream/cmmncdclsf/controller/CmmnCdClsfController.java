package kr.co.soltech.stream.cmmncdclsf.controller;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.soltech.stream.cmmncd.model.CmmnCdParamDTO;
import kr.co.soltech.stream.cmmncdclsf.model.CmmnCdClsfModel;
import kr.co.soltech.stream.cmmncdclsf.service.CmmnCdClsfService;
import kr.co.soltech.stream.commons.model.CustomExceptionDTO;
import kr.co.soltech.stream.commons.service.CustomResponseAPI;
import lombok.RequiredArgsConstructor;

/***
 * 공통코드 분류 컨트롤러 클래스
 */
@RestController
@RequestMapping("/cmmn-cd-clsf")
@RequiredArgsConstructor
public class CmmnCdClsfController {
	/***
	 * 공통코드 분류 서비스 인터페이스
	 */
	private final CmmnCdClsfService cmmnCdClsfService;

	/***
	 * 공통코드 분류 목록 조회
	 * 
	 * @param cmmnCdParamDTO : 공통코드 분류 조회 파라메터 DTO 클래스
	 * @param pageable       : 페이징 정보
	 * @param bindingResult  : 검증 결과
	 * @return 공통코드 분류 목록
	 * @throws Exception
	 */
	@GetMapping
	@CustomResponseAPI
	public Page<CmmnCdClsfModel> inqCmmnCdClsf(@ModelAttribute @Validated CmmnCdParamDTO cmmnCdParamDTO,
			Pageable pageable, BindingResult bindingResult) throws Exception {
		return cmmnCdClsfService.inqCmmnCdClsf(cmmnCdParamDTO, pageable);
	}

	/***
	 * 공통코드 분류 상세 조회
	 * 
	 * @param cmmnCdParamDTO : 공통코드 분류 조회 파라메터 DTO 클래스
	 * @param bindingResult  : 검증 결과
	 * @return 공통코드 분류 상세
	 * @throws Exception
	 */
	@GetMapping("/dtl")
	@CustomResponseAPI
	public CmmnCdClsfModel getCmmnCdClsf(@ModelAttribute @Validated CmmnCdParamDTO cmmnCdParamDTO,
			BindingResult bindingResult) throws Exception {
		if (bindingResult.hasErrors()) {
			throw CustomExceptionDTO.of(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}
		if (ObjectUtils.isEmpty(cmmnCdParamDTO.getCmmnCdClsfId())) {
			throw CustomExceptionDTO.of("ERROR_0001", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}

		return cmmnCdClsfService.getCmmnCdClsf(cmmnCdParamDTO);
	}

	/***
	 * 공통코드 분류 등록/수정
	 * 
	 * @param cmmnCdClsfModel : 공통코드 분류 모델 클래스
	 * @param bindingResult   : 검증 결과
	 * @return 성공/에러
	 * @throws Exception
	 */
	@PostMapping
	@PutMapping
	@PatchMapping
	@CustomResponseAPI(status = HttpStatus.CREATED)
	public String upsertCmmnCdClsf(@RequestBody @Validated CmmnCdClsfModel cmmnCdClsfModel, BindingResult bindingResult)
			throws Exception {
		if (bindingResult.hasErrors()) {
			throw CustomExceptionDTO.of(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}
		cmmnCdClsfModel.prePersist();
		int updatedRows = cmmnCdClsfService.upsertCmmnCdClsf(cmmnCdClsfModel);
		if (updatedRows == 0) {
			throw CustomExceptionDTO.of("ERROR_0000", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("No matching results."));
		}

		return "SUCCESS_0000";
	}

	/***
	 * 공통코드 분류 삭제
	 * 
	 * @param cmmnCdParamDTO : 공통코드 분류 조회 파라메터 DTO 클래스
	 * @param bindingResult  : 검증 결과
	 * @return 성공/에러
	 * @throws Exception
	 */
	@DeleteMapping
	@CustomResponseAPI(status = HttpStatus.NO_CONTENT)
	public String deleteCmmnCdClsf(@ModelAttribute @Validated CmmnCdParamDTO cmmnCdParamDTO,
			BindingResult bindingResult) throws Exception {
		if (ObjectUtils.isEmpty(cmmnCdParamDTO.getCmmnCdClsfId())) {
			throw CustomExceptionDTO.of("ERROR_0001", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}

		CmmnCdClsfModel cmmnCdClsfModel = CmmnCdClsfModel.builder().cmmnCdClsfId(cmmnCdParamDTO.getCmmnCdClsfId())
				.build();
		cmmnCdClsfModel.preUpdate();
		int updatedRows = cmmnCdClsfService.deleteCmmnCdClsf(cmmnCdClsfModel);
		if (updatedRows == 0) {
			throw CustomExceptionDTO.of("ERROR_0000", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("No matching results."));
		}

		return "SUCCESS_0000";
	}
}