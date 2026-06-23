package kr.co.soltech.stream.menu.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.soltech.stream.menu.model.MenuModel;
import kr.co.soltech.stream.menu.model.MenuParamDTO;

/***
 * 메뉴 커스텀 레파지토리 인터페이스
 */
interface CustomMenuRepository {
	/***
	 * 메뉴 목록 조회
	 * 
	 * @param menuParamDTO : 메뉴 조회 파라메터 DTO 클래스
	 * @param pageable     : 페이징 정보
	 * @return 메뉴 목록
	 * @throws Exception
	 */
	public Page<MenuModel> inqMenu(MenuParamDTO menuParamDTO, Pageable pageable) throws Exception;

	/***
	 * 메뉴 상세 조회
	 * 
	 * @param menuParamDTO : 메뉴 조회 파라메터 DTO 클래스
	 * @return 메뉴 상세
	 * @throws Exception
	 */
	public MenuModel getMenu(MenuParamDTO menuParamDTO) throws Exception;

	/***
	 * 메뉴 등록/수정
	 * 
	 * @param menuModel : 메뉴 모델 클래스
	 * @return 업데이트 로우 수
	 * @throws Exception
	 */
	public int upsertMenu(MenuModel menuModel) throws Exception;

	/***
	 * 메뉴 삭제
	 * 
	 * @param menuModel : 메뉴 모델 클래스
	 * @return 업데이트 로우 수
	 * @throws Exception
	 */
	public int deleteMenu(MenuModel menuModel) throws Exception;
}