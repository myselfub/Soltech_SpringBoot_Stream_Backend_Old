package kr.co.soltech.stream.atrz.opnn.controller;

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
import kr.co.soltech.stream.atrz.opnn.model.AtrzOpnnModel;
import kr.co.soltech.stream.atrz.opnn.model.AtrzOpnnParamDTO;
import kr.co.soltech.stream.atrz.opnn.service.AtrzOpnnService;
import kr.co.soltech.stream.commons.model.CustomExceptionDTO;
import kr.co.soltech.stream.commons.service.CustomResponseAPI;
import kr.co.soltech.stream.commons.service.JwtProvider;
import kr.co.soltech.stream.commons.utils.SoltechStreamUtils;
import lombok.RequiredArgsConstructor;

/***
 * 결재 의견 컨트롤러 클래스
 */
@RestController
@RequestMapping("/atrz/opnn")
@RequiredArgsConstructor
public class AtrzOpnnController {
	/***
	 * 결재 의견 서비스 인터페이스
	 */
	private final AtrzOpnnService atrzOpnnService;

	/***
	 * JWT 인증 프로바이더 클래스
	 */
	private final JwtProvider jwtProvider;

	/***
	 * 내 결재 의견 목록 조회
	 * 
	 * @param atrzOpnnParamDTO : 결재 의견 조회 파라메터 DTO 클래스
	 * @param pageable         : 페이징 정보
	 * @param bindingResult    : 검증 결과
	 * @param request          : HttpServletRequest
	 * @return 결재 의견 목록
	 * @throws Exception
	 */
	@GetMapping
	@CustomResponseAPI
	public Page<AtrzOpnnModel> inqAtrzOpnn(@ModelAttribute @Validated AtrzOpnnParamDTO atrzOpnnParamDTO,
			Pageable pageable, BindingResult bindingResult, HttpServletRequest request) throws Exception {
		String tokenUserId = jwtProvider.getUserId(request);
		if (ObjectUtils.isEmpty(tokenUserId)) {
			throw CustomExceptionDTO.of("ERROR_0002", HttpStatus.FORBIDDEN.value(),
					new AccessDeniedException("Unallowed user."));
		}
		atrzOpnnParamDTO.setRgtr_id(tokenUserId);
		atrzOpnnParamDTO.deserialize();

		return atrzOpnnService.inqAtrzOpnn(atrzOpnnParamDTO, pageable);
	}

	/***
	 * 결재 의견 상세 조회
	 * 
	 * @param atrzOpnnParamDTO : 결재 의견 조회 파라메터 DTO 클래스
	 * @param bindingResult    : 검증 결과
	 * @param request          : HttpServletRequest
	 * @return 결재 의견 상세
	 * @throws Exception
	 */
	@GetMapping("/dtl")
	@CustomResponseAPI
	public AtrzOpnnModel getAtrzOpnn(@ModelAttribute @Validated AtrzOpnnParamDTO atrzOpnnParamDTO,
			BindingResult bindingResult, HttpServletRequest request) throws Exception {
		if (bindingResult.hasErrors()) {
			throw CustomExceptionDTO.of(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}
		if (ObjectUtils.isEmpty(atrzOpnnParamDTO.getOpnnId())) {
			throw CustomExceptionDTO.of("ERROR_0001", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}
		try {
			SoltechStreamUtils.base62Decode(atrzOpnnParamDTO.getOpnnId());
		} catch (Exception e) {
			throw CustomExceptionDTO.of("ERROR_0001", "opnnId", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}

		String tokenUserId = jwtProvider.getUserId(request);
		if (ObjectUtils.isEmpty(tokenUserId)) {
			throw CustomExceptionDTO.of("ERROR_0002", HttpStatus.FORBIDDEN.value(),
					new AccessDeniedException("Unallowed user."));
		}
		atrzOpnnParamDTO.setRgtr_id(tokenUserId);
		atrzOpnnParamDTO.deserialize();

		return atrzOpnnService.getAtrzOpnn(atrzOpnnParamDTO);
	}

	/***
	 * 결재 의견 등록/수정
	 * 
	 * @param atrzOpnnModel : 결재 의견 모델 클래스
	 * @param bindingResult : 검증 결과
	 * @param request       : HttpServletRequest
	 * @return 성공/에러
	 * @throws Exception
	 */
	@PostMapping
	@CustomResponseAPI(status = HttpStatus.CREATED)
	public String upsertAtrzOpnn(@RequestBody @Validated AtrzOpnnModel atrzOpnnModel, BindingResult bindingResult,
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
		atrzOpnnModel = atrzOpnnModel.toBuilder().rgtrId(tokenUserId).build();

		if (ObjectUtils.isEmpty(atrzOpnnModel.getOpnnId()) || "0".equals(atrzOpnnModel.getOpnnId())) {
			atrzOpnnModel = atrzOpnnModel.toBuilder().opnnId(SoltechStreamUtils.createEncodeId()).build();
		} else {
			try {
				SoltechStreamUtils.base62Decode(atrzOpnnModel.getOpnnId());
			} catch (Exception e) {
				throw CustomExceptionDTO.of("ERROR_0001", "opnnId", HttpStatus.BAD_REQUEST.value(),
						new BadRequestException("Validation Error"));
			}
		}

		atrzOpnnModel.prePersist();

		int updatedRows = atrzOpnnService.upsertAtrzOpnn(atrzOpnnModel);
		if (updatedRows == 0) {
			throw CustomExceptionDTO.of("ERROR_0000", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("No matching results."));
		}

		return "SUCCESS_0000";
	}

	/***
	 * 결재 의견 삭제
	 * 
	 * @param atrzOpnnParamDTO : 결재 의견 조회 파라메터 DTO 클래스
	 * @param bindingResult    : 검증 결과
	 * @param request          : HttpServletRequest
	 * @return 성공/에러
	 * @throws Exception
	 */
	@DeleteMapping
	@CustomResponseAPI(status = HttpStatus.NO_CONTENT)
	public String deleteAtrzOpnn(@ModelAttribute @Validated AtrzOpnnParamDTO atrzOpnnParamDTO,
			BindingResult bindingResult, HttpServletRequest request) throws Exception {
		if (ObjectUtils.isEmpty(atrzOpnnParamDTO.getOpnnId())) {
			throw CustomExceptionDTO.of("ERROR_0001", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}
		try {
			SoltechStreamUtils.base62Decode(atrzOpnnParamDTO.getOpnnId());
		} catch (Exception e) {
			throw CustomExceptionDTO.of("ERROR_0001", "opnnId", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}

		String tokenUserId = jwtProvider.getUserId(request);
		if (ObjectUtils.isEmpty(tokenUserId)) {
			throw CustomExceptionDTO.of("ERROR_0002", HttpStatus.FORBIDDEN.value(),
					new AccessDeniedException("Unallowed user."));
		}
		atrzOpnnParamDTO.setRgtr_id(tokenUserId);
		atrzOpnnParamDTO.deserialize();

		AtrzOpnnModel atrzOpnnModel = AtrzOpnnModel.builder().opnnId(atrzOpnnParamDTO.getOpnnId())
				.rgtrId(atrzOpnnParamDTO.getRgtrId()).build();
		atrzOpnnModel.preUpdate();

		int updatedRows = atrzOpnnService.deleteAtrzOpnn(atrzOpnnModel);
		if (updatedRows == 0) {
			throw CustomExceptionDTO.of("ERROR_0000", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("No matching results."));
		}

		return "SUCCESS_0000";
	}
}