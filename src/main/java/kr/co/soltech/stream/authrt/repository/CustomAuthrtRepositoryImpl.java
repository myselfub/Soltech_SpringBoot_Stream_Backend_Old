package kr.co.soltech.stream.authrt.repository;

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
import kr.co.soltech.stream.authrt.model.AuthrtModel;
import kr.co.soltech.stream.authrt.model.AuthrtModelId;
import kr.co.soltech.stream.authrt.model.AuthrtParamDTO;
import kr.co.soltech.stream.commons.repository.CustomCntRepository;
import lombok.RequiredArgsConstructor;

/***
 * 권한 커스텀 레파지토리 클래스
 */
@RequiredArgsConstructor
class CustomAuthrtRepositoryImpl implements CustomAuthrtRepository {
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
	 * 권한 목록 조회
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Page<AuthrtModel> inqAuthrt(AuthrtParamDTO authrtParamDTO, Pageable pageable) throws Exception {
		StringBuilder whereQuery = new StringBuilder("");
		whereQuery.append(
				!ObjectUtils.isEmpty(authrtParamDTO.getAuthrtSeCd()) ? "AND authrtSeCd = :authrtSeCd \r\n" : "");
		whereQuery.append(
				!ObjectUtils.isEmpty(authrtParamDTO.getMenuId()) ? "AND menuId LIKE '%' || :menuId || '%' \r\n" : "");
		whereQuery.append(!ObjectUtils.isEmpty(authrtParamDTO.getAuthrtBgngYmd())
				? "AND authrtBgngYmd LIKE '%' || :authrtBgngYmd || '%' \r\n"
				: "");
		whereQuery.append(!ObjectUtils.isEmpty(authrtParamDTO.getAuthrtEndYmd())
				? "AND authrtEndYmd LIKE '%' || :authrtEndYmd || '%' \r\n"
				: "");
		whereQuery.append(
				!ObjectUtils.isEmpty(authrtParamDTO.getMthdList()) ? "AND mthdList LIKE '%' || :mthdList || '%' \r\n"
						: "");

		String queryStr = """
				SELECT
					authrtSeCd AS authrt_se_cd,
					(
						SELECT
							cmmnCdNm
						FROM
							cmmn_cd_table
						WHERE
							useYn = 'Y'
							AND cmmnCdClsfId = 'SOLTECH_0002'
							AND cmmnCdId = authrt_tbl.authrtSeCd
					) AS authrt_se_nm,
					menuId AS menu_id,
					authrtBgngYmd AS authrt_bgng_ymd,
					authrtEndYmd AS authrt_end_ymd,
					mthdList AS mthd_list,
					useYn AS use_yn,
					regDt AS reg_dt,
					rgtrId AS rgtr_id,
					mdfcnDt AS mdfcn_dt,
					mdfrId AS mdfr_id
				FROM
					authrt_table authrt_tbl
				WHERE
					useYn = 'Y'""" + whereQuery.toString() + """
				ORDER BY
					authrtSeCd,
					menuId
				""";

		TypedQuery<Map> typedQuery = entityManager
				.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "), Map.class);

		String countQueryStr = """
				SELECT
					COUNT(1)
				FROM
					authrt_table authrt_tbl
				WHERE
					useYn = 'Y'
				""" + whereQuery.toString();
		TypedQuery<Long> countTypedQuery = entityManager.createQuery(countQueryStr, Long.class);

		if (!ObjectUtils.isEmpty(authrtParamDTO.getAuthrtSeCd())) {
			typedQuery.setParameter("authrtSeCd", authrtParamDTO.getAuthrtSeCd());
			countTypedQuery.setParameter("authrtSeCd", authrtParamDTO.getAuthrtSeCd());
		}
		if (!ObjectUtils.isEmpty(authrtParamDTO.getMenuId())) {
			typedQuery.setParameter("menuId", authrtParamDTO.getMenuId());
			countTypedQuery.setParameter("menuId", authrtParamDTO.getMenuId());
		}
		if (!ObjectUtils.isEmpty(authrtParamDTO.getAuthrtBgngYmd())) {
			typedQuery.setParameter("authrtBgngYmd", authrtParamDTO.getAuthrtBgngYmd());
			countTypedQuery.setParameter("authrtBgngYmd", authrtParamDTO.getAuthrtBgngYmd());
		}
		if (!ObjectUtils.isEmpty(authrtParamDTO.getAuthrtEndYmd())) {
			typedQuery.setParameter("authrtEndYmd", authrtParamDTO.getAuthrtEndYmd());
			countTypedQuery.setParameter("authrtEndYmd", authrtParamDTO.getAuthrtEndYmd());
		}
		if (!ObjectUtils.isEmpty(authrtParamDTO.getMthdList())) {
			typedQuery.setParameter("mthdList", authrtParamDTO.getMthdList());
			countTypedQuery.setParameter("mthdList", authrtParamDTO.getMthdList());
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
			countQueryResult = customCntRepository.getAuthrtCnt(countTypedQuery, authrtParamDTO);
		} else {
			countQueryResult = queryResult.size();
			pageable = PageRequest.of(0, queryResult.size() == 0 ? 1 : queryResult.size());
		}

		return new PageImpl<AuthrtModel>(objectMapper.convertValue(queryResult, new TypeReference<List<AuthrtModel>>() {
		}), pageable, countQueryResult);
	}

	/***
	 * 권한 상세 조회
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public AuthrtModel getAuthrt(AuthrtModelId authrtModelId) throws Exception {
		String queryStr = """
				SELECT
					authrtSeCd AS authrt_se_cd,
					(
						SELECT
							cmmnCdNm
						FROM
							cmmn_cd_table
						WHERE
							useYn = 'Y'
							AND cmmnCdClsfId = 'SOLTECH_0002'
							AND cmmnCdId = authrt_tbl.authrtSeCd
					) AS authrt_se_nm,
					menuId AS menu_id,
					authrtBgngYmd AS authrt_bgng_ymd,
					authrtEndYmd AS authrt_end_ymd,
					mthdList AS mthd_list,
					useYn AS use_yn,
					regDt AS reg_dt,
					rgtrId AS rgtr_id,
					mdfcnDt AS mdfcn_dt,
					mdfrId AS mdfr_id
				FROM
					authrt_table authrt_tbl
				WHERE
					useYn = 'Y'
					AND authrtSeCd = :authrtSeCd
					AND menuId = :menuId
				""";

		TypedQuery<Map> typedQuery = entityManager
				.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "), Map.class)
				.setParameter("authrtSeCd", authrtModelId.getAuthrtSeCd())
				.setParameter("menuId", authrtModelId.getMenuId());

		Map queryResult = typedQuery.getResultList().stream().findFirst().orElse(null);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		return objectMapper.convertValue(queryResult, new TypeReference<AuthrtModel>() {
		});
	}

	/***
	 * 권한 등록/수정
	 */
	@Override
	public int upsertAuthrt(AuthrtModel authrtModel) throws Exception {
		String queryStr = """
				MERGE INTO
					authrt_table target_tbl
				USING (
					SELECT
						:authrtSeCd AS authrt_se_cd,
						:menuId AS menu_id,
						:authrtBgngYmd AS authrt_bgng_ymd,
						:authrtEndYmd AS authrt_end_ymd,
						:mthdList AS mthd_list,
						:useYn AS use_yn,
						(:regDt)::timestamp AS reg_dt,
						:rgtrId AS rgtr_id,
						(:mdfcnDt)::timestamp AS mdfcn_dt,
						:mdfrId AS mdfr_id
				) source_tbl
				ON (
					target_tbl.authrt_se_cd = source_tbl.authrt_se_cd
					AND target_tbl.menu_id = source_tbl.menu_id
				)
				WHEN MATCHED THEN
				UPDATE SET
					authrt_bgng_ymd = source_tbl.authrt_bgng_ymd,
					authrt_end_ymd = source_tbl.authrt_end_ymd,
					mthd_list = source_tbl.mthd_list,
					mdfcn_dt = source_tbl.mdfcn_dt,
					mdfr_id = source_tbl.mdfr_id
				WHEN NOT MATCHED THEN
				INSERT (
					authrt_se_cd,
					menu_id,
					authrt_bgng_ymd,
					authrt_end_ymd,
					mthd_list,
					use_yn,
					reg_dt,
					rgtr_id,
					mdfcn_dt,
					mdfr_id
				) VALUES (
					source_tbl.authrt_se_cd,
					source_tbl.menu_id,
					source_tbl.authrt_bgng_ymd,
					source_tbl.authrt_end_ymd,
					source_tbl.mthd_list,
					source_tbl.use_yn,
					source_tbl.reg_dt,
					source_tbl.rgtr_id,
					source_tbl.mdfcn_dt,
					source_tbl.mdfr_id
				)
				""";

		Query query = entityManager.createNativeQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "))
				.setParameter("authrtSeCd", authrtModel.getAuthrtSeCd()).setParameter("menuId", authrtModel.getMenuId())
				.setParameter("authrtBgngYmd", authrtModel.getAuthrtBgngYmd())
				.setParameter("authrtEndYmd", authrtModel.getAuthrtEndYmd())
				.setParameter("mthdList", authrtModel.getMthdList()).setParameter("useYn", authrtModel.getUseYn())
				.setParameter("regDt", authrtModel.getRegDt()).setParameter("rgtrId", authrtModel.getRgtrId())
				.setParameter("mdfcnDt", authrtModel.getMdfcnDt()).setParameter("mdfrId", authrtModel.getMdfrId());

		return query.executeUpdate();
	}

	/***
	 * 권한 삭제
	 */
	@Override
	public int deleteAuthrt(AuthrtModel authrtModel) throws Exception {
		String queryStr = """
				UPDATE
					authrt_table
				SET
					useYn = 'N',
					mdfcnDt = :mdfcnDt,
					mdfrId = :mdfrId
				WHERE
					useYn = 'Y'
					AND authrtSeCd = :authrtSeCd
					AND menuId = :menuId
				""";
		Query query = entityManager.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "))
				.setParameter("authrtSeCd", authrtModel.getAuthrtSeCd()).setParameter("menuId", authrtModel.getMenuId())
				.setParameter("mdfcnDt", authrtModel.getMdfcnDt()).setParameter("mdfrId", authrtModel.getMdfrId());

		return query.executeUpdate();
	}

	/***
	 * 권한별 메뉴 목록 조회
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public List<Map> inqUrlAuth() throws Exception {
		String queryStr = """
				SELECT
					menu_tbl.menu_id AS menu_id,
					menu_tbl.menu_url AS menu_url,
					menu_tbl.menu_bgng_ymd AS menu_bgng_ymd,
					menu_tbl.menu_end_ymd AS menu_end_ymd,
					authrt_tbl.authrt_se_cd AS authrt_se_cd,
					authrt_tbl.authrt_end_ymd AS authrt_end_ymd,
					authrt_tbl.authrt_bgng_ymd AS authrt_bgng_ymd,
					cmmn_tbl.cmmn_cd_nm AS authrt_se_nm
				FROM (
					SELECT
						menuId AS menu_id,
						menuUrl AS menu_url,
						menuBgngYmd AS menu_bgng_ymd,
						menuEndYmd AS menu_end_ymd
					FROM
						menu_table
					WHERE
						useYn = 'Y'
						AND current_date BETWEEN TO_DATE(menuBgngYmd, 'yyyyMMdd') AND TO_DATE(menuEndYmd, 'yyyyMMdd')
				) menu_tbl, (
					SELECT
						authrtSeCd AS authrt_se_cd,
						authrtBgngYmd AS authrt_bgng_ymd,
						authrtEndYmd AS authrt_end_ymd,
						menuId AS menu_id
					FROM
						authrt_table
					WHERE
						useYn = 'Y'
						AND current_date BETWEEN TO_DATE(authrtBgngYmd, 'yyyyMMdd') AND TO_DATE(authrtEndYmd, 'yyyyMMdd')
				) authrt_tbl, (
					SELECT
						cmmnCdId AS cmmn_cd_id,
						cmmnCdNm AS cmmn_cd_nm,
						cmmnCdExpln AS cmmn_cd_expln
					FROM
						cmmn_cd_table
					WHERE
						useYn = 'Y'
						AND cmmnCdClsfId = 'SOLTECH_0002'
				) cmmn_tbl
				WHERE
					menu_tbl.menu_id = authrt_tbl.menu_id
					AND authrt_tbl.authrt_se_cd = cmmn_tbl.cmmn_cd_id
				ORDER BY
					menu_tbl.menu_id
				""";

		return entityManager.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "), Map.class)
				.getResultList();
	}

	/***
	 * 사용자 권한 조회
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> getUserAuthrt(String userId, String menuUrl, String menuMethod) throws Exception {
		String queryStr = """
				SELECT
					join_tbl.user_id AS user_id,
					join_tbl.authrt_se_cd AS authrt_se_cd,
					join_tbl.authrt_se_nm AS authrt_se_nm,
					join_tbl.authrt_end_ymd AS authrt_end_ymd,
					join_tbl.authrt_bgng_ymd AS authrt_bgng_ymd,
					join_tbl.mthd_list AS mthd_list,
					menu_tbl.menu_id AS menu_id,
					menu_tbl.menu_url AS menu_url,
					menu_tbl.menu_bgng_ymd AS menu_bgng_ymd,
					menu_tbl.menu_end_ymd AS menu_end_ymd
				FROM (
					SELECT
						user_tbl.user_id AS user_id,
						user_tbl.user_authrt_se_cd AS authrt_se_cd,
						cmmn_tbl.cmmn_cd_nm AS authrt_se_nm,
						authrt_tbl.authrt_end_ymd AS authrt_end_ymd,
						authrt_tbl.authrt_bgng_ymd AS authrt_bgng_ymd,
						authrt_tbl.menu_id AS menu_id,
						authrt_tbl.mthd_list AS mthd_list
					FROM (
						SELECT
							userId AS user_id,
							userAuthrtSeCd AS user_authrt_se_cd
						FROM
							user_table
						WHERE
							useYn = 'Y'
							AND userId = :userId
					) user_tbl, (
						SELECT
							cmmnCdId AS cmmn_cd_id,
							cmmnCdNm AS cmmn_cd_nm,
							cmmnCdExpln AS cmmn_cd_expln
						FROM
							cmmn_cd_table
						WHERE
							useYn = 'Y'
							AND cmmnCdClsfId = 'SOLTECH_0002'
					) cmmn_tbl, (
						SELECT
							authrtSeCd AS authrt_se_cd,
							authrtBgngYmd AS authrt_bgng_ymd,
							authrtEndYmd AS authrt_end_ymd,
							mthdList AS mthd_list,
							menuId AS menu_id
						FROM
							authrt_table
						WHERE
							useYn = 'Y'
							AND mthdList LIKE '%' || :menuMethod || '%'
							AND current_date BETWEEN TO_DATE(authrtBgngYmd, 'yyyyMMdd') AND TO_DATE(authrtEndYmd, 'yyyyMMdd')
					) authrt_tbl
					WHERE
						user_tbl.user_authrt_se_cd = cmmn_tbl.cmmn_cd_id
						AND (
							user_tbl.user_authrt_se_cd = authrt_tbl.authrt_se_cd
							OR authrt_tbl.authrt_se_cd = '00000' /*** 누구나 */
						)
				) join_tbl
				LEFT JOIN (
					SELECT
						menuId AS menu_id,
						menuUrl AS menu_url,
						menuBgngYmd AS menu_bgng_ymd,
						menuEndYmd AS menu_end_ymd
					FROM
						menu_table
					WHERE
						useYn = 'Y'
						AND current_date BETWEEN TO_DATE(menuBgngYmd, 'yyyyMMdd') AND TO_DATE(menuEndYmd, 'yyyyMMdd')
						AND :menuUrl LIKE menuUrl
				) menu_tbl
				ON
					join_tbl.menu_id = menu_tbl.menu_id
				WHERE
					menu_tbl.menu_id IS NOT NULL
				""";

		return entityManager.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "), Map.class)
				.setParameter("userId", userId).setParameter("menuUrl", menuUrl).setParameter("menuMethod", menuMethod)
				.getResultList().stream().findFirst().orElse(null);
	}
}