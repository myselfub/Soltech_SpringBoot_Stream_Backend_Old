package kr.co.soltech.stream.hldy.controller;

import java.time.Duration;
import java.time.LocalDateTime;

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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.soltech.stream.commons.configs.SchedulingConfig;
import kr.co.soltech.stream.commons.model.CustomExceptionDTO;
import kr.co.soltech.stream.commons.service.CustomResponseAPI;
import kr.co.soltech.stream.commons.service.JwtProvider;
import kr.co.soltech.stream.hldy.model.HldyModel;
import kr.co.soltech.stream.hldy.model.HldyModelId;
import kr.co.soltech.stream.hldy.model.HldyParamDTO;
import kr.co.soltech.stream.hldy.service.HldyService;
import lombok.RequiredArgsConstructor;

/***
 * 휴일 컨트롤러 클래스
 */
@RestController
@RequestMapping("/hldy")
@RequiredArgsConstructor
public class HldyController {
	/***
	 * 휴일 서비스 인터페이스
	 */
	private final HldyService hldyService;

	/***
	 * 스케쥴러 관련 설정 클래스
	 */
	private final SchedulingConfig schedulingConfig;

	/***
	 * JWT 인증 프로바이더 클래스
	 */
	private final JwtProvider jwtProvider;

	/***
	 * 휴일 목록 조회
	 * 
	 * @param hldyParamDTO  : 휴일 조회 파라메터 DTO 클래스
	 * @param pageable      : 페이징 정보
	 * @param bindingResult : 검증 결과
	 * @return 휴일 목록
	 * @throws Exception
	 */
	@GetMapping
	@CustomResponseAPI
	public Page<HldyModel> inqHldy(@ModelAttribute @Validated HldyParamDTO hldyParamDTO, Pageable pageable,
			BindingResult bindingResult) throws Exception {
		hldyParamDTO.deserialize();

		return hldyService.inqHldy(hldyParamDTO, pageable);
	}

	/***
	 * 휴일 상세 조회
	 * 
	 * @param hldyModelId   : 휴일 모델 ID 클래스
	 * @param bindingResult : 검증 결과
	 * @return 휴일 상세
	 * @throws Exception
	 */
	@GetMapping("/dtl")
	@CustomResponseAPI
	public HldyModel getHldy(@ModelAttribute @Validated HldyModelId hldyModelId, BindingResult bindingResult)
			throws Exception {
		if (bindingResult.hasErrors()) {
			throw CustomExceptionDTO.of(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}
		if (ObjectUtils.isEmpty(hldyModelId.getHldyYmd())) {
			throw CustomExceptionDTO.of("VALID_0001", "hldyId", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}

		return hldyService.getHldy(hldyModelId);
	}

	/***
	 * 휴일 등록/수정
	 * 
	 * @param hldyModel     : 휴일 모델 클래스
	 * @param bindingResult : 검증 결과
	 * @return 성공/에러
	 * @throws Exception
	 */
	@PostMapping
	@PutMapping
	@PatchMapping
	@CustomResponseAPI(status = HttpStatus.CREATED)
	public String upsertHldy(@RequestBody @Validated HldyModel hldyModel, BindingResult bindingResult)
			throws Exception {
		if (bindingResult.hasErrors()) {
			throw CustomExceptionDTO.of(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}
		hldyModel.deserialize();
		hldyModel.preModel();
		hldyModel.prePersist();
		int updatedRows = hldyService.upsertHldy(hldyModel);
		if (updatedRows == 0) {
			throw CustomExceptionDTO.of("ERROR_0000", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("No matching results."));
		}

		return "SUCCESS_0000";
	}

	/***
	 * 휴일 삭제
	 * 
	 * @param hldyModelId   : 휴일 모델 ID 클래스
	 * @param bindingResult : 검증 결과
	 * @return 성공/에러
	 * @throws Exception
	 */
	@DeleteMapping
	@CustomResponseAPI(status = HttpStatus.NO_CONTENT)
	public String deleteHldy(@ModelAttribute @Validated HldyModelId hldyModelId, BindingResult bindingResult)
			throws Exception {
		if (ObjectUtils.isEmpty(hldyModelId.getHldyYmd())) {
			throw CustomExceptionDTO.of("VALID_0001", "hldyId", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}

		HldyModel hldyModel = HldyModel.builder().hldyYmd(hldyModelId.getHldyYmd())
				.hldyYmdSn(hldyModelId.getHldyYmdSn()).build();
		hldyModel.deserialize();
		hldyModel.preModel();
		hldyModel.preUpdate();
		int updatedRows = hldyService.deleteHldy(hldyModel);
		if (updatedRows == 0) {
			throw CustomExceptionDTO.of("ERROR_0000", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("No matching results."));
		}

		return "SUCCESS_0000";
	}

	/***
	 * 휴일 API 호출
	 * 
	 * @return 성공/에러
	 * @throws Exception
	 */
	@GetMapping("/api")
	@CustomResponseAPI
	public String runHldyApiScheduling(@RequestParam(name = "year") String year, HttpServletRequest request)
			throws Exception {
		String tokenUserId = jwtProvider.getUserId(request);
		if (ObjectUtils.isEmpty(tokenUserId)) {
			throw CustomExceptionDTO.of("ERROR_0002", HttpStatus.FORBIDDEN.value(),
					new AccessDeniedException("Unallowed user."));
		}
		try {
			LocalDateTime lastHldyApiDateTime = schedulingConfig.getLastHldyApiDateTime();
			if (!ObjectUtils.isEmpty(lastHldyApiDateTime)
					&& Duration.between(lastHldyApiDateTime, LocalDateTime.now()).toMinutes() / 60.0 > 8.0) {
				// TODO : 메세지 변경
				throw CustomExceptionDTO.of("쿨타임", HttpStatus.INTERNAL_SERVER_ERROR.value(), new Exception(""));
			}
			schedulingConfig.runHldyApiScheduling(tokenUserId, year);
		} catch (CustomExceptionDTO ce) {
			throw ce;
		} catch (Exception e) {
			throw CustomExceptionDTO.of("ERROR_0000", HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
		}

		return "SUCCESS_0000";
	}
}