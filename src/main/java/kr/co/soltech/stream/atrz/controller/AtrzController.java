package kr.co.soltech.stream.atrz.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.soltech.stream.atrz.model.AtrzModel;
import kr.co.soltech.stream.atrz.model.AtrzParamDTO;
import kr.co.soltech.stream.atrz.service.AtrzService;
import kr.co.soltech.stream.commons.model.CustomExceptionDTO;
import kr.co.soltech.stream.commons.service.CustomResponseAPI;
import kr.co.soltech.stream.commons.service.JwtProvider;
import lombok.RequiredArgsConstructor;

/***
 * 결재 컨트롤러 클래스
 */
@RestController
@RequestMapping("/atrz")
@RequiredArgsConstructor
public class AtrzController {
	/***
	 * 결재 서비스 인터페이스
	 */
	private final AtrzService atrzService;

	/***
	 * JWT 인증 프로바이더 클래스
	 */
	private final JwtProvider jwtProvider;

	/***
	 * 내 결재 목록 조회
	 * 
	 * @param atrzParamDTO  : 결재 조회 파라메터 DTO 클래스
	 * @param pageable      : 페이징 정보
	 * @param bindingResult : 검증 결과
	 * @param request       : HttpServletRequest
	 * @return 결재 목록
	 * @throws Exception
	 */
	@GetMapping
	@CustomResponseAPI
	public Page<AtrzModel> inqAtrz(@ModelAttribute @Validated AtrzParamDTO atrzParamDTO, BindingResult bindingResult,
			Pageable pageable, HttpServletRequest request) throws Exception {
		String tokenUserId = jwtProvider.getUserId(request);
		if (ObjectUtils.isEmpty(tokenUserId)) {
			throw CustomExceptionDTO.of("ERROR_0002", HttpStatus.FORBIDDEN.value(),
					new AccessDeniedException("Unallowed user."));
		}
		atrzParamDTO.setDrftr_id(tokenUserId);
		atrzParamDTO.deserialize();

		return atrzService.inqAtrz(atrzParamDTO, pageable);
	}

	/***
	 * 결재 상세 조회
	 * 
	 * @param atrzParamDTO  : 결재 조회 파라메터 DTO 클래스
	 * @param bindingResult : 검증 결과
	 * @param request       : HttpServletRequest
	 * @return 결재 상세
	 * @throws Exception
	 */
	@GetMapping("/dtl")
	@CustomResponseAPI
	public AtrzModel getAtrz(@ModelAttribute @Validated AtrzParamDTO atrzParamDTO, BindingResult bindingResult,
			HttpServletRequest request) throws Exception {
		if (bindingResult.hasErrors()) {
			throw CustomExceptionDTO.of(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}
		if (ObjectUtils.isEmpty(atrzParamDTO.getDocNo()) || !atrzParamDTO.getDocNo().matches("^\\d{4}-\\d{4}$")) {
			throw CustomExceptionDTO.of("ERROR_0001", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}

		String tokenUserId = jwtProvider.getUserId(request);
		if (ObjectUtils.isEmpty(tokenUserId)) {
			throw CustomExceptionDTO.of("ERROR_0002", HttpStatus.FORBIDDEN.value(),
					new AccessDeniedException("Unallowed user."));
		}
		atrzParamDTO.setAprvr_id(tokenUserId);

		return atrzService.getAtrz(atrzParamDTO);
	}

	/***
	 * 결재 등록/수정
	 * 
	 * @param atrzModel     : 결재 모델 클래스
	 * @param bindingResult : 검증 결과
	 * @param request       : HttpServletRequest
	 * @return 성공/에러
	 * @throws Exception
	 */
	@PostMapping
	@CustomResponseAPI(status = HttpStatus.CREATED)
	public String upsertAtrz(@RequestPart(name = "datas") AtrzModel atrzModel,
			@RequestPart(name = "files", required = false) List<MultipartFile> files, BindingResult bindingResult,
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
		if (!ObjectUtils.isEmpty(files)) {
			atrzModel = atrzModel.toBuilder().drftrId(tokenUserId).atrzSttsSeCd("00000").multipartFileList(files)
					.build();
		} else {
			atrzModel = atrzModel.toBuilder().drftrId(tokenUserId).atrzSttsSeCd("00000").build();
		}
		atrzModel.deserialize();
		atrzModel.preModel();
		atrzModel.prePersist();

		int updatedRows = atrzService.upsertAtrz(atrzModel);
		if (updatedRows == 0) {
			throw CustomExceptionDTO.of("ERROR_0000", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("No matching results."));
		}

		return "SUCCESS_0000";

	}

	/***
	 * 결재 삭제
	 * 
	 * @param atrzParamDTO  : 결재 조회 파라메터 DTO 클래스
	 * @param bindingResult : 검증 결과
	 * @param request       : HttpServletRequest
	 * @return 성공/에러
	 * @throws Exception
	 */
	@DeleteMapping
	@CustomResponseAPI(status = HttpStatus.NO_CONTENT)
	public String deleteAtrz(@ModelAttribute @Validated AtrzParamDTO atrzParamDTO, BindingResult bindingResult,
			HttpServletRequest request) throws Exception {
		if (ObjectUtils.isEmpty(atrzParamDTO.getDocNo()) || !atrzParamDTO.getDocNo().matches("^\\d{4}-\\d{4}$")) {
			throw CustomExceptionDTO.of("ERROR_0001", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}

		String tokenUserId = jwtProvider.getUserId(request);
		tokenUserId = "[USER_ID]";
		if (ObjectUtils.isEmpty(tokenUserId)) {
			throw CustomExceptionDTO.of("ERROR_0002", HttpStatus.FORBIDDEN.value(),
					new AccessDeniedException("Unallowed user."));
		}
		atrzParamDTO.setDrftr_id(tokenUserId);
		atrzParamDTO.deserialize();

		AtrzModel atrzModel = AtrzModel.builder().docNo(atrzParamDTO.getDocNo()).drftrId(atrzParamDTO.getDrftrId())
				.build();
		atrzModel.deserialize();
		atrzModel.preModel();
		atrzModel.preUpdate();

		int updatedRows = atrzService.deleteAtrz(atrzModel);
		if (updatedRows == 0) {
			throw CustomExceptionDTO.of("ERROR_0000", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("No matching results."));
		}

		return "SUCCESS_0000";
	}

	/***
	 * 내 결재 완료 목록 조회(결재할 문서)
	 * 
	 * @param atrzParamDTO  : 결재 조회 파라메터 DTO 클래스
	 * @param pageable      : 페이징 정보
	 * @param bindingResult : 검증 결과
	 * @param request       : HttpServletRequest
	 * @return 내 결재 완료 목록
	 * @throws Exception
	 */
	@GetMapping("/cmptn")
	@CustomResponseAPI
	public Page<AtrzModel> inqAtrzCmptn(@ModelAttribute @Validated AtrzParamDTO atrzParamDTO, Pageable pageable,
			BindingResult bindingResult, HttpServletRequest request) throws Exception {
		String tokenUserId = jwtProvider.getUserId(request);
		if (ObjectUtils.isEmpty(tokenUserId)) {
			throw CustomExceptionDTO.of("ERROR_0002", HttpStatus.FORBIDDEN.value(),
					new AccessDeniedException("Unallowed user."));
		}
		atrzParamDTO.setAprvr_id(tokenUserId);
		atrzParamDTO.deserialize();

		return atrzService.inqAtrzCmptn(atrzParamDTO, pageable);
	}

	/***
	 * 내 결재 미완료 목록 조회(결재할 문서)
	 * 
	 * @param atrzParamDTO  : 결재 조회 파라메터 DTO 클래스
	 * @param pageable      : 페이징 정보
	 * @param bindingResult : 검증 결과
	 * @param request       : HttpServletRequest
	 * @return 내 결재 미완료 목록
	 * @throws Exception
	 */
	@GetMapping("/un-cmptn")
	@CustomResponseAPI
	public Page<AtrzModel> inqAtrzUnCmptn(@ModelAttribute @Validated AtrzParamDTO atrzParamDTO, Pageable pageable,
			BindingResult bindingResult, HttpServletRequest request) throws Exception {
		String tokenUserId = jwtProvider.getUserId(request);
		if (ObjectUtils.isEmpty(tokenUserId)) {
			throw CustomExceptionDTO.of("ERROR_0002", HttpStatus.FORBIDDEN.value(),
					new AccessDeniedException("Unallowed user."));
		}
		atrzParamDTO.setAprvr_id(tokenUserId);
		atrzParamDTO.deserialize();

		return atrzService.inqAtrzUnCmptn(atrzParamDTO, pageable);
	}

	/***
	 * 결재 상태 수정
	 * 
	 * @param atrzModel     : 결재 모델 클래스
	 * @param bindingResult : 검증 결과
	 * @param request       : HttpServletRequest
	 * @return 성공/에러
	 * @throws Exception
	 */
	@PostMapping("/stts")
	@CustomResponseAPI(status = HttpStatus.CREATED)
	public String updateAtrzStts(@RequestBody AtrzModel atrzModel, BindingResult bindingResult,
			HttpServletRequest request) throws Exception {
		if (ObjectUtils.isEmpty(atrzModel.getDocNo()) || "0".equals(atrzModel.getDocNo())
				|| !atrzModel.getDocNo().matches("^\\d{4}-\\d{4}$")
				|| ObjectUtils.isEmpty(atrzModel.getAtrzSttsSeCd())) {
			throw CustomExceptionDTO.of("ERROR_0001", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}

		String tokenUserId = jwtProvider.getUserId(request);
		if (ObjectUtils.isEmpty(tokenUserId)) {
			throw CustomExceptionDTO.of("ERROR_0002", HttpStatus.FORBIDDEN.value(),
					new AccessDeniedException("Unallowed user."));
		}
		atrzModel = atrzModel.toBuilder().aprvrId(tokenUserId)
				.atrzDt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))).build();
		atrzModel.deserialize();
		atrzModel.preModel();
		atrzModel.preUpdate();

		int updatedRows = atrzService.updateAtrzStts(atrzModel);
		if (updatedRows == 0) {
			throw CustomExceptionDTO.of("ERROR_0000", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("No matching results."));
		}

		return "SUCCESS_0000";
	}
}
