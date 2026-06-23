package kr.co.soltech.stream.cmmncd.controller;

import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.soltech.stream.cmmncd.model.CmmnCdModel;
import kr.co.soltech.stream.cmmncd.model.CmmnCdModelId;
import kr.co.soltech.stream.cmmncd.model.CmmnCdParamDTO;
import kr.co.soltech.stream.cmmncd.service.CmmnCdService;
import kr.co.soltech.stream.commons.model.CustomExceptionDTO;
import kr.co.soltech.stream.commons.service.CustomResponseAPI;
import lombok.RequiredArgsConstructor;

/***
 * 공통코드 컨트롤러 클래스
 */
@RestController
@RequestMapping("/cmmn-cd")
@RequiredArgsConstructor
public class CmmnCdController {
	/***
	 * 공통코드 서비스 인터페이스
	 */
	private final CmmnCdService cmmnCdService;

	/***
	 * 공통코드 목록 조회
	 * 
	 * @param cmmnCdParamDTO : 공통코드 조회 파라메터 DTO 클래스
	 * @param pageable       : 페이징 정보
	 * @param bindingResult  : 검증 결과
	 * @return 공통코드 목록
	 * @throws Exception
	 */
	@GetMapping
	@CustomResponseAPI(status = HttpStatus.OK)
	public Page<CmmnCdModel> inqCmmnCd(@ModelAttribute @Validated CmmnCdParamDTO cmmnCdParamDTO, Pageable pageable,
			BindingResult bindingResult) throws Exception {
		return cmmnCdService.inqCmmnCd(cmmnCdParamDTO, pageable);
	}

	/***
	 * 공통코드 상세 조회
	 * 
	 * @param cmmnCdModelId : 공통코드 모델 ID 클래스
	 * @param bindingResult : 검증 결과
	 * @return 공통코드 상세
	 * @throws Exception
	 */
	@GetMapping("/dtl")
	@CustomResponseAPI
	public CmmnCdModel getCmmnCd(@ModelAttribute @Validated CmmnCdModelId cmmnCdModelId, BindingResult bindingResult)
			throws Exception {
		if (bindingResult.hasErrors()) {
			throw CustomExceptionDTO.of(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}

		return cmmnCdService.getCmmnCd(cmmnCdModelId);
	}

	/***
	 * 공통코드 등록/수정
	 * 
	 * @param cmmnCdModel   : 공통코드 모델 클래스
	 * @param bindingResult : 검증 결과
	 * @return 성공/에러
	 * @throws Exception
	 */
	@PostMapping
	@PutMapping
	@PatchMapping
	@CustomResponseAPI(status = HttpStatus.CREATED)
	public String upsertCmmnCd(@RequestBody @Validated CmmnCdModel cmmnCdModel, BindingResult bindingResult)
			throws Exception {
		if (bindingResult.hasErrors()) {
			throw CustomExceptionDTO.of(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}
		cmmnCdModel.prePersist();
		int updatedRows = cmmnCdService.upsertCmmnCd(cmmnCdModel);
		if (updatedRows == 0) {
			throw CustomExceptionDTO.of("ERROR_0000", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("No matching results."));
		}

		return "SUCCESS_0000";
	}

	/***
	 * 다중 공통코드 등록/수정
	 * 
	 * @param cmmnCdModel   : 공통코드 모델 클래스
	 * @param bindingResult : 검증 결과
	 * @return 성공/에러
	 * @throws Exception
	 */
	@PostMapping("/all")
	@PutMapping
	@PatchMapping
	@CustomResponseAPI(status = HttpStatus.CREATED)
	public String upsertAllCmmnCd(@RequestBody @Validated List<CmmnCdModel> cmmnCdModelList,
			BindingResult bindingResult) throws Exception {
		if (bindingResult.hasErrors()) {
			throw CustomExceptionDTO.of(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}
		for (CmmnCdModel cmmnCdModel : cmmnCdModelList) {
			cmmnCdModel.prePersist();
		}
		int updatedRows = cmmnCdService.upsertAllCmmnCd(cmmnCdModelList);
		if (updatedRows == 0) {
			throw CustomExceptionDTO.of("ERROR_0000", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("No matching results."));
		}

		return "SUCCESS_0000";
	}

	/***
	 * 공통코드 삭제
	 * 
	 * @param cmmnCdModelId : 공통코드 모델 ID 클래스
	 * @param bindingResult : 검증 결과
	 * @return 성공/에러
	 * @throws Exception
	 */
	@DeleteMapping
	@CustomResponseAPI(status = HttpStatus.NO_CONTENT)
	public String deleteCmmnCd(@ModelAttribute @Validated CmmnCdModelId cmmnCdModelId, BindingResult bindingResult)
			throws Exception {
		if (bindingResult.hasErrors()) {
			throw CustomExceptionDTO.of(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}

		CmmnCdModel cmmnCdModel = CmmnCdModel.builder().cmmnCdClsfId(cmmnCdModelId.getCmmnCdClsfId())
				.cmmnCdId(cmmnCdModelId.getCmmnCdId()).build();
		cmmnCdModel.preUpdate();
		int updatedRows = cmmnCdService.deleteCmmnCd(cmmnCdModel);
		if (updatedRows == 0) {
			throw CustomExceptionDTO.of("ERROR_0000", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("No matching results."));
		}

		return "SUCCESS_0000";
	}

	/***
	 * 사용자용 공통코드 목록 조회
	 * 
	 * @param cmmnCdClsfUserDfnVl1 : 공통코드 분류 사용자정의 값1
	 * @return 공통코드 목록
	 * @throws Exception
	 */
	@GetMapping("/cd/{cmmnCdClsfUserDfnVl1}/")
	@CustomResponseAPI(status = HttpStatus.OK)
	public List<CmmnCdModel> inqUserCmmnCd(@PathVariable(name = "cmmnCdClsfUserDfnVl1") String cmmnCdClsfUserDfnVl1)
			throws Exception {
		if (ObjectUtils.isEmpty(cmmnCdClsfUserDfnVl1)) {
			throw CustomExceptionDTO.of("cmmnCdClsfUserDfnVl1", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}

		return cmmnCdService.inqUserCmmnCd(cmmnCdClsfUserDfnVl1);
	}

	/***
	 * 조직도용 공통코드(부서) 목록 조회
	 * 
	 * @param cmmnCdClsfUserDfnVl1 : 공통코드 분류 사용자정의 값1
	 * @return 공통코드 목록
	 * @throws Exception
	 */
	@GetMapping("/cd/ognz-chrt-dept")
	@CustomResponseAPI(status = HttpStatus.OK)
	public List<Map<String, Object>> inqOgnzChrtDept(@ModelAttribute @Validated CmmnCdParamDTO cmmnCdParamDTO,
			BindingResult bindingResult) throws Exception {
		return cmmnCdService.inqOgnzChrtDept(cmmnCdParamDTO);
	}
}