package kr.co.soltech.stream.menu.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import kr.co.soltech.stream.commons.repository.CustomCntRepository;
import kr.co.soltech.stream.menu.model.MenuModel;
import kr.co.soltech.stream.menu.model.MenuParamDTO;
import lombok.RequiredArgsConstructor;

/***
 * 메뉴 커스텀 레파지토리 클래스
 */
@RequiredArgsConstructor
class CustomMenuRepositoryImpl implements CustomMenuRepository {
	/***
	 * JPA 엔티티 매니저
	 */
	@PersistenceContext
	private EntityManager entityManager;

	/***
	 * 페이징 개수 커스텀 레파지토리 인터페이스
	 */
	private final CustomCntRepository customCntRepository;

	/***
	 * 메뉴 목록 조회
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Page<MenuModel> inqMenu(MenuParamDTO menuParamDTO, Pageable pageable) throws Exception {
		StringBuilder whereQuery = new StringBuilder("");
		whereQuery.append(
				!ObjectUtils.isEmpty(menuParamDTO.getMenuId()) ? "AND menuId LIKE '%' || :menuId || '%' \r\n" : "");
		whereQuery.append(
				!ObjectUtils.isEmpty(menuParamDTO.getMenuNm()) ? "AND menuNm LIKE '%' || :menuNm || '%' \r\n" : "");
		whereQuery.append(
				!ObjectUtils.isEmpty(menuParamDTO.getMenuUrl()) ? "AND menuUrl LIKE '%' || :menuUrl || '%' \r\n" : "");
		whereQuery.append(!ObjectUtils.isEmpty(menuParamDTO.getMenuApiYn()) ? "AND menuApiYn = :menuApiYn \r\n" : "");
		whereQuery.append(
				!ObjectUtils.isEmpty(menuParamDTO.getMenuPopupYn()) ? "AND menuPopupYn = :menuPopupYn \r\n" : "");

		String queryStr = """
				SELECT
					menuId AS menu_id,
					menuNm AS menu_nm,
					menuExpln AS menu_expln,
					menuUrl AS menu_url,
					upMenuId AS up_menu_id,
					menuDpth AS menu_dpth,
					menuApiYn AS menu_api_yn,
					menuPopupYn AS menu_popup_yn,
					menuUserDfnVl AS menu_user_dfn_vl,
					menuBgngYmd AS menu_bgng_ymd,
					menuEndYmd AS menu_end_ymd,
					menuSn AS menu_sn,
					useYn AS use_yn,
					regDt AS reg_dt,
					rgtrId AS rgtr_id,
					mdfcnDt AS mdfcn_dt,
					mdfrId AS mdfr_id
				FROM
					menu_table menu_tbl
				WHERE
					useYn = 'Y'
					""" + whereQuery.toString() + """
				ORDER BY
					menuSn,
					menuId
				""";

		TypedQuery<Map> typedQuery = entityManager
				.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "), Map.class);

		String countQueryStr = """
				SELECT
					COUNT(1)
				FROM
					menu_table menu_tbl
				WHERE
					useYn = 'Y'
				""" + whereQuery.toString();
		TypedQuery<Long> countTypedQuery = entityManager.createQuery(countQueryStr, Long.class);

		if (!ObjectUtils.isEmpty(menuParamDTO.getMenuId())) {
			typedQuery.setParameter("menuId", menuParamDTO.getMenuId());
			countTypedQuery.setParameter("menuId", menuParamDTO.getMenuId());
		}
		if (!ObjectUtils.isEmpty(menuParamDTO.getMenuNm())) {
			typedQuery.setParameter("menuNm", menuParamDTO.getMenuNm());
			countTypedQuery.setParameter("menuNm", menuParamDTO.getMenuNm());
		}
		if (!ObjectUtils.isEmpty(menuParamDTO.getMenuUrl())) {
			typedQuery.setParameter("menuUrl", menuParamDTO.getMenuUrl());
			countTypedQuery.setParameter("menuUrl", menuParamDTO.getMenuUrl());
		}
		if (!ObjectUtils.isEmpty(menuParamDTO.getMenuApiYn())) {
			typedQuery.setParameter("menuApiYn", menuParamDTO.getMenuApiYn());
			countTypedQuery.setParameter("menuApiYn", menuParamDTO.getMenuApiYn());
		}
		if (!ObjectUtils.isEmpty(menuParamDTO.getMenuPopupYn())) {
			typedQuery.setParameter("menuPopupYn", menuParamDTO.getMenuPopupYn());
			countTypedQuery.setParameter("menuPopupYn", menuParamDTO.getMenuPopupYn());
		}
		if (!ObjectUtils.isEmpty(pageable)) {
			typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
			typedQuery.setMaxResults(pageable.getPageSize());
		}

		List<Map> queryResult = typedQuery.getResultList();
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		long countQueryResult = 0;
		if (!ObjectUtils.isEmpty(pageable)) {
			countQueryResult = customCntRepository.getMenuCnt(countTypedQuery, menuParamDTO);
		} else {
			countQueryResult = queryResult.size();
			pageable = PageRequest.of(0, queryResult.size() == 0 ? 1 : queryResult.size());
		}

		return new PageImpl<MenuModel>(objectMapper.convertValue(queryResult, new TypeReference<List<MenuModel>>() {
		}), pageable, countQueryResult);
	}

	/***
	 * 메뉴 상세 조회
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public MenuModel getMenu(MenuParamDTO menuParamDTO) throws Exception {
		String queryStr = """
				SELECT
					menuId AS menu_id,
					menuNm AS menu_nm,
					menuExpln AS menu_expln,
					menuUrl AS menu_url,
					upMenuId AS up_menu_id,
					menuDpth AS menu_dpth,
					menuApiYn AS menu_api_yn,
					menuPopupYn AS menu_popup_yn,
					menuUserDfnVl AS menu_user_dfn_vl,
					menuBgngYmd AS menu_bgng_ymd,
					menuEndYmd AS menu_end_ymd,
					menuSn AS menu_sn,
					useYn AS use_yn,
					regDt AS reg_dt,
					rgtrId AS rgtr_id,
					mdfcnDt AS mdfcn_dt,
					mdfrId AS mdfr_id
				FROM
					menu_table menu_tbl
				WHERE
					useYn = 'Y'
					AND menuId = :menuId
				""";

		TypedQuery<Map> typedQuery = entityManager
				.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "), Map.class)
				.setParameter("menuId", menuParamDTO.getMenuId());

		Map queryResult = typedQuery.getResultList().stream().findFirst().orElse(null);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		return objectMapper.convertValue(queryResult, new TypeReference<MenuModel>() {
		});
	}

	/***
	 * 메뉴 등록/수정
	 */
	@Override
	public int upsertMenu(MenuModel menuModel) throws Exception {
		String queryStr = """
				MERGE INTO
					menu_table target_tbl
				USING (
					SELECT
						:menuId AS menu_id,
						:menuNm AS menu_nm,
						:menuExpln AS menu_expln,
						:menuUrl AS menu_url,
						:upMenuId AS up_menu_id,
						:menuDpth AS menu_dpth,
						:menuApiYn AS menu_api_yn,
						:menuPopupYn AS menu_popup_yn,
						:menuUserDfnVl AS menu_user_dfn_vl,
						:menuBgngYmd AS menu_bgng_ymd,
						:menuEndYmd AS menu_end_ymd,
						:menuSn AS menu_sn,
						:useYn AS use_yn,
						(:regDt)::timestamp AS reg_dt,
						:rgtrId AS rgtr_id,
						(:mdfcnDt)::timestamp AS mdfcn_dt,
						:mdfrId AS mdfr_id
				) source_tbl
				ON (
					target_tbl.menu_id = source_tbl.menu_id
				)
				WHEN MATCHED THEN
				UPDATE SET
					menu_nm = source_tbl.menu_nm,
					menu_expln = source_tbl.menu_expln,
					menu_url = source_tbl.menu_url,
					up_menu_id = source_tbl.up_menu_id,
					menu_dpth = source_tbl.menu_dpth,
					menu_api_yn = source_tbl.menu_api_yn,
					menu_popup_yn = source_tbl.menu_popup_yn,
					menu_user_dfn_vl = source_tbl.menu_user_dfn_vl,
					menu_bgng_ymd = source_tbl.menu_bgng_ymd,
					menu_end_ymd = source_tbl.menu_end_ymd,
					menu_sn = source_tbl.menu_sn,
					mdfcn_dt = source_tbl.mdfcn_dt,
					mdfr_id = source_tbl.mdfr_id
				WHEN NOT MATCHED THEN
				INSERT (
					menu_id,
					menu_nm,
					menu_expln,
					menu_url,
					up_menu_id,
					menu_dpth,
					menu_api_yn,
					menu_popup_yn,
					menu_user_dfn_vl,
					menu_bgng_ymd,
					menu_end_ymd,
					menu_sn,
					use_yn,
					reg_dt,
					rgtr_id,
					mdfcn_dt,
					mdfr_id
				) VALUES (
					source_tbl.menu_id,
					source_tbl.menu_nm,
					source_tbl.menu_expln,
					source_tbl.menu_url,
					source_tbl.up_menu_id,
					source_tbl.menu_dpth,
					source_tbl.menu_api_yn,
					source_tbl.menu_popup_yn,
					source_tbl.menu_user_dfn_vl,
					source_tbl.menu_bgng_ymd,
					source_tbl.menu_end_ymd,
					source_tbl.menu_sn,
					source_tbl.use_yn,
					source_tbl.reg_dt,
					source_tbl.rgtr_id,
					source_tbl.mdfcn_dt,
					source_tbl.mdfr_id
				)
				""";

		Query query = entityManager.createNativeQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "))
				.setParameter("menuId", menuModel.getMenuId()).setParameter("menuNm", menuModel.getMenuNm())
				.setParameter("menuExpln", menuModel.getMenuExpln()).setParameter("menuUrl", menuModel.getMenuUrl())
				.setParameter("upMenuId", menuModel.getUpMenuId()).setParameter("menuDpth", menuModel.getMenuDpth())
				.setParameter("menuApiYn", menuModel.getMenuApiYn())
				.setParameter("menuPopupYn", menuModel.getMenuPopupYn())
				.setParameter("menuUserDfnVl", menuModel.getMenuUserDfnVl())
				.setParameter("menuBgngYmd", menuModel.getMenuBgngYmd())
				.setParameter("menuEndYmd", menuModel.getMenuEndYmd()).setParameter("menuSn", menuModel.getMenuSn())
				.setParameter("useYn", menuModel.getUseYn()).setParameter("regDt", menuModel.getRegDt())
				.setParameter("rgtrId", menuModel.getRgtrId()).setParameter("mdfcnDt", menuModel.getMdfcnDt())
				.setParameter("mdfrId", menuModel.getMdfrId());

		return query.executeUpdate();
	}

	/***
	 * 메뉴 삭제
	 */
	@Override
	public int deleteMenu(MenuModel menuModel) throws Exception {
		String queryStr = """
				UPDATE
					menu_table
				SET
					useYn = 'N',
					mdfcnDt = :mdfcnDt,
					mdfrId = :mdfrId
				WHERE
					useYn = 'Y'
					AND menuId = :menuId
				""";
		Query query = entityManager.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "))
				.setParameter("menuId", menuModel.getMenuId()).setParameter("mdfcnDt", menuModel.getMdfcnDt())
				.setParameter("mdfrId", menuModel.getMdfrId());

		return query.executeUpdate();
	}
}