package kr.co.soltech.stream.menu.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import kr.co.soltech.stream.menu.model.MenuModel;
import kr.co.soltech.stream.menu.model.MenuParamDTO;
import kr.co.soltech.stream.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;

/***
 * 메뉴 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {
	/***
	 * 메뉴 JPA 레파지토리 인터페이스
	 */
	private final MenuRepository menuRepository;

	/***
	 * 메뉴 목록 조회
	 */
	@Override
	public Page<MenuModel> inqMenu(MenuParamDTO menuParamDTO, Pageable pageable) throws Exception {
		return menuRepository.inqMenu(menuParamDTO, pageable);
	}

	/***
	 * 메뉴 상세 조회
	 */
	@Override
	public MenuModel getMenu(MenuParamDTO menuParamDTO) throws Exception {
		return menuRepository.getMenu(menuParamDTO);
	}

	/***
	 * 메뉴 등록/수정
	 */
	@CacheEvict(value = "menuCnt", allEntries = true)
	@Transactional
	@Override
	public int upsertMenu(MenuModel menuModel) throws Exception {
		return menuRepository.upsertMenu(menuModel);
	}

	/***
	 * 메뉴 삭제
	 */
	@CacheEvict(value = "menuCnt", allEntries = true)
	@Transactional
	@Override
	public int deleteMenu(MenuModel menuModel) throws Exception {
		return menuRepository.deleteMenu(menuModel);
	}
}