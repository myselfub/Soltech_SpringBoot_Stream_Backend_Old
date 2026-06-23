package kr.co.soltech.stream.menu.controller;

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

import kr.co.soltech.stream.commons.model.CustomExceptionDTO;
import kr.co.soltech.stream.commons.service.CustomResponseAPI;
import kr.co.soltech.stream.menu.model.MenuModel;
import kr.co.soltech.stream.menu.model.MenuParamDTO;
import kr.co.soltech.stream.menu.service.MenuService;
import lombok.RequiredArgsConstructor;

/***
 * 메뉴 컨트롤러 클래스
 */
@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {
	/***
	 * 메뉴 서비스 인터페이스
	 */
	private final MenuService menuService;

	/***
	 * 메뉴 목록 조회
	 * 
	 * @param menuParamDTO  : 메뉴 조회 파라메터 DTO 클래스
	 * @param pageable      : 페이징 정보
	 * @param bindingResult : 검증 결과
	 * @return 메뉴 목록
	 * @throws Exception
	 */
	@GetMapping
	@CustomResponseAPI
	public Page<MenuModel> inqMenu(@ModelAttribute @Validated MenuParamDTO menuParamDTO, Pageable pageable,
			BindingResult bindingResult) throws Exception {
		return menuService.inqMenu(menuParamDTO, pageable);
	}

	/***
	 * 메뉴 상세 조회
	 * 
	 * @param menuParamDTO  : 메뉴 조회 파라메터 DTO 클래스
	 * @param bindingResult : 검증 결과
	 * @return 메뉴 상세
	 * @throws Exception
	 */
	@GetMapping("/dtl")
	@CustomResponseAPI
	public MenuModel getMenu(@ModelAttribute @Validated MenuParamDTO menuParamDTO, BindingResult bindingResult)
			throws Exception {
		if (bindingResult.hasErrors()) {
			throw CustomExceptionDTO.of(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}
		if (ObjectUtils.isEmpty(menuParamDTO.getMenuId())) {
			throw CustomExceptionDTO.of("VALID_0001", "menuId", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}

		return menuService.getMenu(menuParamDTO);
	}

	/***
	 * 메뉴 등록/수정
	 * 
	 * @param menuModel     : 메뉴 모델 클래스
	 * @param bindingResult : 검증 결과
	 * @return 성공/에러
	 * @throws Exception
	 */
	@PostMapping
	@PutMapping
	@PatchMapping
	@CustomResponseAPI(status = HttpStatus.CREATED)
	public String upsertMenu(@RequestBody @Validated MenuModel menuModel, BindingResult bindingResult)
			throws Exception {
		if (bindingResult.hasErrors()) {
			throw CustomExceptionDTO.of(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}
		menuModel.deserialize();
		menuModel.preModel();
		menuModel.prePersist();
		int updatedRows = menuService.upsertMenu(menuModel);
		if (updatedRows == 0) {
			throw CustomExceptionDTO.of("ERROR_0000", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("No matching results."));
		}

		return "SUCCESS_0000";
	}

	/***
	 * 메뉴 삭제
	 * 
	 * @param menuParamDTO  : 메뉴 조회 파라메터 DTO 클래스
	 * @param bindingResult : 검증 결과
	 * @return 성공/에러
	 * @throws Exception
	 */
	@DeleteMapping
	@CustomResponseAPI(status = HttpStatus.NO_CONTENT)
	public String deleteMenu(@ModelAttribute @Validated MenuParamDTO menuParamDTO, BindingResult bindingResult)
			throws Exception {
		if (ObjectUtils.isEmpty(menuParamDTO.getMenuId())) {
			throw CustomExceptionDTO.of("VALID_0001", "menuId", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}

		MenuModel menuModel = MenuModel.builder().menuId(menuParamDTO.getMenuId()).build();
		menuModel.deserialize();
		menuModel.preModel();
		menuModel.preUpdate();
		int updatedRows = menuService.deleteMenu(menuModel);
		if (updatedRows == 0) {
			throw CustomExceptionDTO.of("ERROR_0000", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("No matching results."));
		}

		return "SUCCESS_0000";
	}
}