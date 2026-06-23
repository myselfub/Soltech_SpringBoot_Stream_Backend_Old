package kr.co.soltech.stream.authrt.controller;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.soltech.stream.authrt.model.AuthrtModel;
import kr.co.soltech.stream.authrt.model.AuthrtModelId;
import kr.co.soltech.stream.authrt.model.AuthrtParamDTO;
import kr.co.soltech.stream.authrt.service.AuthrtService;
import kr.co.soltech.stream.commons.model.CustomExceptionDTO;
import kr.co.soltech.stream.commons.service.CustomResponseAPI;
import lombok.RequiredArgsConstructor;

/***
 * 권한 컨트롤러 클래스
 */
@RestController
@RequestMapping("/authrt")
@RequiredArgsConstructor
public class AuthrtController {
	/***
	 * 권한 서비스 인터페이스
	 */
	private final AuthrtService authrtService;

	/***
	 * 권한별 메뉴 목록 조회
	 * 
	 * @param authrtModelId : 권한 모델 ID 클래스
	 * @param pageable      : 페이징 정보
	 * @param bindingResult : 검증 결과
	 * @return 권한별 메뉴 목록
	 * @throws Exception
	 */
	@GetMapping
	@CustomResponseAPI
	public Page<AuthrtModel> inqAuthrt(@ModelAttribute @Validated AuthrtParamDTO authrtParamDTO, Pageable pageable,
			BindingResult bindingResult) throws Exception {
		authrtParamDTO.deserialize();

		return authrtService.inqAuthrt(authrtParamDTO, pageable);
	}

	/***
	 * 권한 상세 조회
	 * 
	 * @param authrtModelId : 권한 모델 ID 클래스
	 * @param bindingResult : 검증 결과
	 * @return 권한 상세
	 * @throws Exception
	 */
	@GetMapping("/dtl")
	@CustomResponseAPI
	public AuthrtModel getAuthrt(@ModelAttribute @Validated AuthrtModelId authrtModelId, BindingResult bindingResult)
			throws Exception {
		if (bindingResult.hasErrors()) {
			throw CustomExceptionDTO.of(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}

		return authrtService.getAuthrt(authrtModelId);
	}

	/***
	 * 권한 등록/수정
	 * 
	 * @param authrtModel   : 권한 모델 클래스
	 * @param bindingResult : 검증 결과
	 * @return 성공/에러
	 * @throws Exception
	 */
	@PostMapping
	@CustomResponseAPI(status = HttpStatus.CREATED)
	public String upsertAuthrt(@RequestBody @Validated AuthrtModel authrtModel, BindingResult bindingResult)
			throws Exception {
		if (bindingResult.hasErrors()) {
			throw CustomExceptionDTO.of(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}

		authrtModel.deserialize();
		authrtModel.preModel();
		authrtModel.prePersist();

		int updatedRows = authrtService.upsertAuthrt(authrtModel);
		if (updatedRows == 0) {
			throw CustomExceptionDTO.of("ERROR_0000", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("No matching results."));
		}

		return "SUCCESS_0000";
	}

	/***
	 * 권한 삭제
	 * 
	 * @param authrtModelId : 권한 모델 ID 클래스
	 * @param bindingResult : 검증 결과
	 * @return 성공/에러
	 * @throws Exception
	 */
	@DeleteMapping
	@CustomResponseAPI(status = HttpStatus.NO_CONTENT)
	public String deleteAuthrt(@ModelAttribute @Validated AuthrtModelId authrtModelId, BindingResult bindingResult)
			throws Exception {
		if (bindingResult.hasErrors()) {
			throw CustomExceptionDTO.of(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}

		AuthrtModel authrtModel = AuthrtModel.builder().authrtSeCd(authrtModelId.getAuthrtSeCd())
				.menuId(authrtModelId.getMenuId()).build();
		authrtModel.deserialize();
		authrtModel.preModel();
		authrtModel.preUpdate();
		int updatedRows = authrtService.deleteAuthrt(authrtModel);
		if (updatedRows == 0) {
			throw CustomExceptionDTO.of("ERROR_0000", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("No matching results."));
		}

		return "SUCCESS_0000";
	}
}