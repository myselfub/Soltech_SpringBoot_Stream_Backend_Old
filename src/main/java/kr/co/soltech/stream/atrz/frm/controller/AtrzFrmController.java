package kr.co.soltech.stream.atrz.frm.controller;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
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
import kr.co.soltech.stream.atrz.frm.model.AtrzFrmModel;
import kr.co.soltech.stream.atrz.frm.model.AtrzFrmParamDTO;
import kr.co.soltech.stream.atrz.frm.service.AtrzFrmService;
import kr.co.soltech.stream.commons.model.CustomExceptionDTO;
import kr.co.soltech.stream.commons.service.CustomResponseAPI;
import lombok.RequiredArgsConstructor;

/***
 * 결재 양식 컨트롤러 클래스
 */
@RestController
@RequestMapping("/atrz/frm")
@RequiredArgsConstructor
public class AtrzFrmController {
	/***
	 * 결재 양식 서비스 인터페이스
	 */
	private final AtrzFrmService atrzFrmService;

	/***
	 * 내 결재 양식 목록 조회
	 * 
	 * @param atrzFrmParamDTO : 결재 양식 조회 파라메터 DTO 클래스
	 * @param pageable        : 페이징 정보
	 * @param bindingResult   : 검증 결과
	 * @param request         : HttpServletRequest
	 * @return 결재 양식 목록
	 * @throws Exception
	 */
	@GetMapping
	@CustomResponseAPI
	public Slice<AtrzFrmModel> inqAtrzFrm(@ModelAttribute @Validated AtrzFrmParamDTO atrzFrmParamDTO, Pageable pageable,
			BindingResult bindingResult, HttpServletRequest request) throws Exception {
		return atrzFrmService.inqAtrzFrm(atrzFrmParamDTO, pageable);
	}

	/***
	 * 결재 양식 상세 조회
	 * 
	 * @param atrzFrmParamDTO : 결재 양식 조회 파라메터 DTO 클래스
	 * @param bindingResult   : 검증 결과
	 * @param request         : HttpServletRequest
	 * @return 결재 양식 상세
	 * @throws Exception
	 */
	@GetMapping("/dtl")
	@CustomResponseAPI
	public AtrzFrmModel getAtrzFrm(@ModelAttribute @Validated AtrzFrmParamDTO atrzFrmParamDTO,
			BindingResult bindingResult, HttpServletRequest request) throws Exception {
		if (bindingResult.hasErrors()) {
			throw CustomExceptionDTO.of(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}
		if (ObjectUtils.isEmpty(atrzFrmParamDTO.getDocSeCd())) {
			throw CustomExceptionDTO.of("ERROR_0001", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}

		return atrzFrmService.getAtrzFrm(atrzFrmParamDTO);
	}

	/***
	 * 결재 양식 등록/수정
	 * 
	 * @param atrzFrmModel  : 결재 양식 모델 클래스
	 * @param bindingResult : 검증 결과
	 * @param request       : HttpServletRequest
	 * @return 성공/에러
	 * @throws Exception
	 */
	@PostMapping
	@CustomResponseAPI(status = HttpStatus.CREATED)
	public String upsertAtrzFrm(@RequestBody @Validated AtrzFrmModel atrzFrmModel, BindingResult bindingResult,
			HttpServletRequest request) throws Exception {
		if (bindingResult.hasErrors()) {
			throw CustomExceptionDTO.of(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}

		atrzFrmModel.prePersist();

		int updatedRows = atrzFrmService.upsertAtrzFrm(atrzFrmModel);
		if (updatedRows == 0) {
			throw CustomExceptionDTO.of("ERROR_0000", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("No matching results."));
		}

		return "SUCCESS_0000";
	}

	/***
	 * 결재 양식 삭제
	 * 
	 * @param atrzFrmParamDTO : 결재 양식 조회 파라메터 DTO 클래스
	 * @param bindingResult   : 검증 결과
	 * @param request         : HttpServletRequest
	 * @return 성공/에러
	 * @throws Exception
	 */
	@DeleteMapping
	@CustomResponseAPI(status = HttpStatus.NO_CONTENT)
	public String deleteAtrzFrm(@ModelAttribute @Validated AtrzFrmParamDTO atrzFrmParamDTO, BindingResult bindingResult,
			HttpServletRequest request) throws Exception {
		if (ObjectUtils.isEmpty(atrzFrmParamDTO.getDocSeCd())) {
			throw CustomExceptionDTO.of("ERROR_0001", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}

		AtrzFrmModel atrzFrmModel = AtrzFrmModel.builder().docSeCd(atrzFrmParamDTO.getDocSeCd()).build();
		atrzFrmModel.preUpdate();

		int updatedRows = atrzFrmService.deleteAtrzFrm(atrzFrmModel);
		if (updatedRows == 0) {
			throw CustomExceptionDTO.of("ERROR_0000", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("No matching results."));
		}

		return "SUCCESS_0000";
	}
}