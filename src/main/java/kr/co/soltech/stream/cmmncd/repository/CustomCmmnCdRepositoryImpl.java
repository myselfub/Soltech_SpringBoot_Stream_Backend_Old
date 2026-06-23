package kr.co.soltech.stream.cmmncd.repository;

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
import kr.co.soltech.stream.cmmncd.model.CmmnCdModel;
import kr.co.soltech.stream.cmmncd.model.CmmnCdModelId;
import kr.co.soltech.stream.cmmncd.model.CmmnCdParamDTO;
import kr.co.soltech.stream.commons.repository.CustomCntRepository;
import lombok.RequiredArgsConstructor;

/***
 * 공통코드 커스텀 레파지토리 클래스
 */
@RequiredArgsConstructor
class CustomCmmnCdRepositoryImpl implements CustomCmmnCdRepository {
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
	 * 공통코드 목록 조회
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Page<CmmnCdModel> inqCmmnCd(CmmnCdParamDTO cmmnCdParamDTO, Pageable pageable) throws Exception {
		StringBuilder whereQuery = new StringBuilder("");
		whereQuery.append(
				!ObjectUtils.isEmpty(cmmnCdParamDTO.getCmmnCdClsfId()) ? " AND cmmnCdClsfId = :cmmnCdClsfId \r\n" : "");
		whereQuery.append(!ObjectUtils.isEmpty(cmmnCdParamDTO.getCmmnCdId()) ? " AND cmmnCdId = :cmmnCdId \r\n" : "");
		whereQuery.append(
				!ObjectUtils.isEmpty(cmmnCdParamDTO.getCmmnCdNm()) ? " AND cmmnCdNm LIKE '%' || :cmmnCdNm || '%' \r\n"
						: "");

		String queryStr = """
				SELECT
					cmmnCdClsfId AS cmmn_cd_clsf_id,
					(
						SELECT
							cmmnCdClsfNm
						FROM
							cmmn_cd_clsf_table
						WHERE
							useYn = 'Y'
							AND cmmnCdClsfId = cmmn_cd_tbl.cmmnCdClsfId
					) AS cmmn_cd_clsf_nm,
					cmmnCdId AS cmmn_cd_id,
					cmmnCdNm AS cmmn_cd_nm,
					cmmnCdExpln AS cmmn_cd_expln,
					cmmnCdUserDfnVl1 AS cmmn_cd_user_dfn_vl1,
					cmmnCdUserDfnVl2 AS cmmn_cd_user_dfn_vl2,
					cmmnCdUserDfnVl3 AS cmmn_cd_user_dfn_vl3,
					cmmnCdSortNo AS cmmn_cd_sort_no,
					useYn AS use_yn,
					regDt AS reg_dt,
					rgtrId AS rgtr_id,
					mdfcnDt AS mdfcn_dt,
					mdfrId AS mdfr_id
				FROM
					cmmn_cd_table cmmn_cd_tbl
				WHERE
					useYn = 'Y'""" + whereQuery.toString() + """
				ORDER BY
					cmmnCdClsfId,
					cmmnCdSortNo
				""";

		TypedQuery<Map> typedQuery = entityManager
				.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "), Map.class);

		String countQueryStr = """
				SELECT
					COUNT(1)
				FROM
					cmmn_cd_table cmmn_cd_tbl
				WHERE
					useYn = 'Y'
				""" + whereQuery.toString();
		TypedQuery<Long> countTypedQuery = entityManager.createQuery(countQueryStr, Long.class);

		if (!ObjectUtils.isEmpty(cmmnCdParamDTO.getCmmnCdClsfId())) {
			typedQuery.setParameter("cmmnCdClsfId", cmmnCdParamDTO.getCmmnCdClsfId());
			countTypedQuery.setParameter("cmmnCdClsfId", cmmnCdParamDTO.getCmmnCdClsfId());
		}
		if (!ObjectUtils.isEmpty(cmmnCdParamDTO.getCmmnCdId())) {
			typedQuery.setParameter("cmmnCdId", cmmnCdParamDTO.getCmmnCdId());
			countTypedQuery.setParameter("cmmnCdId", cmmnCdParamDTO.getCmmnCdId());
		}
		if (!ObjectUtils.isEmpty(cmmnCdParamDTO.getCmmnCdNm())) {
			typedQuery.setParameter("cmmnCdNm", cmmnCdParamDTO.getCmmnCdNm());
			countTypedQuery.setParameter("cmmnCdNm", cmmnCdParamDTO.getCmmnCdNm());
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
			countQueryResult = customCntRepository.getCmmnCdCnt(countTypedQuery, cmmnCdParamDTO);
		} else {
			countQueryResult = queryResult.size();
			pageable = PageRequest.of(0, queryResult.size() == 0 ? 1 : queryResult.size());
		}

		return new PageImpl<CmmnCdModel>(objectMapper.convertValue(queryResult, new TypeReference<List<CmmnCdModel>>() {
		}), pageable, countQueryResult);
	}

	/***
	 * 공통코드 상세 조회
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public CmmnCdModel getCmmnCd(CmmnCdModelId cmmnCdModelId) throws Exception {
		String queryStr = """
				SELECT
					cmmnCdClsfId AS cmmn_cd_clsf_id,
					(
						SELECT
							cmmnCdClsfNm
						FROM
							cmmn_cd_clsf_table
						WHERE
							useYn = 'Y'
							AND cmmnCdClsfId = cmmn_cd_tbl.cmmnCdClsfId
					) AS cmmn_cd_clsf_nm,
					cmmnCdId AS cmmn_cd_id,
					cmmnCdNm AS cmmn_cd_nm,
					cmmnCdExpln AS cmmn_cd_expln,
					cmmnCdUserDfnVl1 AS cmmn_cd_user_dfn_vl1,
					cmmnCdUserDfnVl2 AS cmmn_cd_user_dfn_vl2,
					cmmnCdUserDfnVl3 AS cmmn_cd_user_dfn_vl3,
					cmmnCdSortNo AS cmmn_cd_sort_no,
					useYn AS use_yn,
					regDt AS reg_dt,
					rgtrId AS rgtr_id,
					mdfcnDt AS mdfcn_dt,
					mdfrId AS mdfr_id
				FROM
					cmmn_cd_table cmmn_cd_tbl
				WHERE
					useYn = 'Y'
					AND cmmnCdClsfId = :cmmnCdClsfId
					AND cmmnCdId = :cmmnCdId
				""";
		TypedQuery<Map> typedQuery = entityManager
				.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "), Map.class)
				.setParameter("cmmnCdClsfId", cmmnCdModelId.getCmmnCdClsfId())
				.setParameter("cmmnCdId", cmmnCdModelId.getCmmnCdId());

		Map queryResult = typedQuery.getResultList().stream().findFirst().orElse(null);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		return objectMapper.convertValue(queryResult, new TypeReference<CmmnCdModel>() {
		});
	}

	/***
	 * 공통코드 등록/수정
	 */
	@Override
	public int upsertCmmnCd(CmmnCdModel cmmnCdModel) throws Exception {
		String queryStr = """
				MERGE INTO
					cmmn_cd_table target_tbl
				USING (
					SELECT
						:cmmnCdClsfId AS cmmn_cd_clsf_id,
						:cmmnCdId AS cmmn_cd_id,
						:cmmnCdNm AS cmmn_cd_nm,
						:cmmnCdExpln AS cmmn_cd_expln,
						:cmmnCdUpClsfId AS cmmn_cd_up_clsf_id,
						:cmmnCdUpId AS cmmn_cd_up_id,
						:cmmnCdClsfUserDfnVl1 AS cmmn_cd_clsf_user_dfn_vl1,
						:cmmnCdClsfUserDfnVl2 AS cmmn_cd_clsf_user_dfn_vl2,
						:cmmnCdClsfUserDfnVl3 AS cmmn_cd_clsf_user_dfn_vl3,
						:cmmnCdSortNo AS cmmn_cd_sort_no,
						:useYn AS use_yn,
						(:regDt)::timestamp AS reg_dt,
						:rgtrId AS rgtr_id,
						(:mdfcnDt)::timestamp AS mdfcn_dt,
						:mdfrId AS mdfr_id
				) source_tbl
				ON (
					target_tbl.cmmn_cd_clsf_id = source_tbl.cmmn_cd_clsf_id
					AND target_tbl.cmmn_cd_id = source_tbl.cmmn_cd_id
				)
				WHEN MATCHED THEN
				UPDATE SET
					cmmn_cd_nm = source_tbl.cmmn_cd_nm,
					cmmn_cd_expln = source_tbl.cmmn_cd_expln,
					cmmn_cd_up_clsf_id = source_tbl.cmmn_cd_up_clsf_id,
					cmmn_cd_up_id = source_tbl.cmmn_cd_up_id,
					cmmn_cd_clsf_user_dfn_vl1 = source_tbl.cmmn_cd_clsf_user_dfn_vl1,
					cmmn_cd_clsf_user_dfn_vl2 = source_tbl.cmmn_cd_clsf_user_dfn_vl2,
					cmmn_cd_clsf_user_dfn_vl3 = source_tbl.cmmn_cd_clsf_user_dfn_vl3,
					cmmn_cd_sort_no = source_tbl.cmmn_cd_sort_no,
					mdfcn_dt = source_tbl.mdfcn_dt,
					mdfr_id = source_tbl.mdfr_id
				WHEN NOT MATCHED THEN
				INSERT (
					cmmn_cd_clsf_id,
					cmmn_cd_id,
					cmmn_cd_nm,
					cmmn_cd_expln,
					cmmn_cd_up_clsf_id,
					cmmn_cd_up_id,
					cmmn_cd_clsf_user_dfn_vl1,
					cmmn_cd_clsf_user_dfn_vl2,
					cmmn_cd_clsf_user_dfn_vl3,
					cmmn_cd_sort_no,
					use_yn,
					reg_dt,
					rgtr_id,
					mdfcn_dt,
					mdfr_id
				) VALUES (
					source_tbl.cmmn_cd_clsf_id,
					source_tbl.cmmn_cd_id,
					source_tbl.cmmn_cd_nm,
					source_tbl.cmmn_cd_expln,
					source_tbl.cmmn_cd_up_clsf_id,
					source_tbl.cmmn_cd_up_id,
					source_tbl.cmmn_cd_clsf_user_dfn_vl1,
					source_tbl.cmmn_cd_clsf_user_dfn_vl2,
					source_tbl.cmmn_cd_clsf_user_dfn_vl3,
					source_tbl.cmmn_cd_sort_no,
					source_tbl.use_yn,
					source_tbl.reg_dt,
					source_tbl.rgtr_id,
					source_tbl.mdfcn_dt,
					source_tbl.mdfr_id
				)
				""";

		Query query = entityManager.createNativeQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "))
				.setParameter("cmmnCdClsfId", cmmnCdModel.getCmmnCdClsfId())
				.setParameter("cmmnCdId", cmmnCdModel.getCmmnCdId()).setParameter("cmmnCdNm", cmmnCdModel.getCmmnCdNm())
				.setParameter("cmmnCdExpln", cmmnCdModel.getCmmnCdExpln())
				.setParameter("cmmnCdUpClsfId", cmmnCdModel.getCmmnCdUpClsfId())
				.setParameter("cmmnCdUpId", cmmnCdModel.getCmmnCdUpId())
				.setParameter("cmmnCdUserDfnVl1", cmmnCdModel.getCmmnCdUserDfnVl1())
				.setParameter("cmmnCdUserDfnVl2", cmmnCdModel.getCmmnCdUserDfnVl2())
				.setParameter("cmmnCdUserDfnVl3", cmmnCdModel.getCmmnCdUserDfnVl3())
				.setParameter("cmmnCdSortNo", cmmnCdModel.getCmmnCdSortNo())
				.setParameter("useYn", cmmnCdModel.getUseYn()).setParameter("regDt", cmmnCdModel.getRegDt())
				.setParameter("rgtrId", cmmnCdModel.getRgtrId()).setParameter("mdfcnDt", cmmnCdModel.getMdfcnDt())
				.setParameter("mdfrId", cmmnCdModel.getMdfrId());

		return query.executeUpdate();
	}

	/***
	 * 다중 공통코드 등록/수정
	 */
	@Override
	public int upsertAllCmmnCd(List<CmmnCdModel> cmmnCdModelList) throws Exception {
		int updatedRows = 0;
		int batchSize = 50;

		for (int idx = 0; idx < cmmnCdModelList.size(); idx += batchSize) {
			int end = Math.min(idx + batchSize, cmmnCdModelList.size());
			List<CmmnCdModel> cmmnCdModelSubList = cmmnCdModelList.subList(idx, end);

			StringBuilder queryStr = new StringBuilder("""
					MERGE INTO
						cmmn_cd_table target_tbl
					USING (
						""");

			for (int batchIdx = 0; batchIdx < cmmnCdModelSubList.size(); batchIdx++) {
				queryStr.append("SELECT \r\n");
				queryStr.append(":cmmnCdClsfId").append(batchIdx).append(" AS cmmn_cd_clsf_id, \r\n");
				queryStr.append(":cmmnCdId").append(batchIdx).append(" AS cmmn_cd_id, \r\n");
				queryStr.append(":cmmnCdNm").append(batchIdx).append(" AS cmmn_cd_nm, \r\n");
				queryStr.append(":cmmnCdExpln").append(batchIdx).append(" AS cmmn_cd_expln, \r\n");
				queryStr.append(":cmmnCdUpClsfId").append(batchIdx).append(" AS cmmn_cd_up_clsf_id, \r\n");
				queryStr.append(":cmmnCdUpId").append(batchIdx).append(" AS cmmn_cd_up_id, \r\n");
				queryStr.append("cmmnCdClsfUserDfnVl1").append(batchIdx).append(" AS cmmn_cd_clsf_user_dfn_vl1, \r\n");
				queryStr.append("cmmnCdClsfUserDfnVl2").append(batchIdx).append(" AS cmmn_cd_clsf_user_dfn_vl2, \r\n");
				queryStr.append("cmmnCdClsfUserDfnVl3").append(batchIdx).append(" AS cmmn_cd_clsf_user_dfn_vl3, \r\n");
				queryStr.append("cmmnCdSortNo").append(batchIdx).append(" AS cmmn_cd_sort_no, \r\n");
				queryStr.append(":useYn").append(batchIdx).append(" AS use_yn, \r\n");
				queryStr.append("(:regDt").append(batchIdx).append(")::timestamp").append(" AS reg_dt, \r\n");
				queryStr.append(":rgtrId").append(batchIdx).append(" AS rgtr_id, \r\n");
				queryStr.append("(:mdfcnDt").append(batchIdx).append(")::timestamp").append(" AS mdfcn_dt, \r\n");
				queryStr.append(":mdfrId").append(batchIdx).append(" AS mdfr_id \r\n");
				if (batchIdx < cmmnCdModelSubList.size() - 1) {
					queryStr.append("UNION ALL \r\n");
				}
			}
			queryStr.append("""
					ON (
						target_tbl.cmmn_cd_clsf_id = source_tbl.cmmn_cd_clsf_id
						AND target_tbl.cmmn_cd_id = source_tbl.cmmn_cd_id
					)
					WHEN MATCHED THEN
					UPDATE SET
						cmmn_cd_nm = source_tbl.cmmn_cd_nm,
						cmmn_cd_expln = source_tbl.cmmn_cd_expln,
						cmmn_cd_up_clsf_id = source_tbl.cmmn_cd_up_clsf_id,
						cmmn_cd_up_id = source_tbl.cmmn_cd_up_id,
						cmmn_cd_clsf_user_dfn_vl1 = source_tbl.cmmn_cd_clsf_user_dfn_vl1,
						cmmn_cd_clsf_user_dfn_vl2 = source_tbl.cmmn_cd_clsf_user_dfn_vl2,
						cmmn_cd_clsf_user_dfn_vl3 = source_tbl.cmmn_cd_clsf_user_dfn_vl3,
						cmmn_cd_sort_no = source_tbl.cmmn_cd_sort_no,
						mdfcn_dt = source_tbl.mdfcn_dt,
						mdfr_id = source_tbl.mdfr_id
					WHEN NOT MATCHED THEN
					INSERT (
						cmmn_cd_clsf_id,
						cmmn_cd_id,
						cmmn_cd_nm,
						cmmn_cd_expln,
						cmmn_cd_up_clsf_id,
						cmmn_cd_up_id,
						cmmn_cd_clsf_user_dfn_vl1,
						cmmn_cd_clsf_user_dfn_vl2,
						cmmn_cd_clsf_user_dfn_vl3,
						cmmn_cd_sort_no,
						use_yn,
						reg_dt,
						rgtr_id,
						mdfcn_dt,
						mdfr_id
					) VALUES (
						source_tbl.cmmn_cd_clsf_id,
						source_tbl.cmmn_cd_id,
						source_tbl.cmmn_cd_nm,
						source_tbl.cmmn_cd_expln,
						source_tbl.cmmn_cd_up_clsf_id,
						source_tbl.cmmn_cd_up_id,
						source_tbl.cmmn_cd_clsf_user_dfn_vl1,
						source_tbl.cmmn_cd_clsf_user_dfn_vl2,
						source_tbl.cmmn_cd_clsf_user_dfn_vl3,
						source_tbl.cmmn_cd_sort_no,
						source_tbl.use_yn,
						source_tbl.reg_dt,
						source_tbl.rgtr_id,
						source_tbl.mdfcn_dt,
						source_tbl.mdfr_id
						""");

			Query query = entityManager
					.createNativeQuery(queryStr.toString().stripIndent().replace("\t", " ").replace("\n", " "));

			for (int batchIdx = 0; batchIdx < cmmnCdModelSubList.size(); batchIdx++) {
				CmmnCdModel cmmnCdModel = cmmnCdModelSubList.get(batchIdx);
				query.setParameter("cmmnCdClsfId" + batchIdx, cmmnCdModel.getCmmnCdClsfId())
						.setParameter("cmmnCdId" + batchIdx, cmmnCdModel.getCmmnCdId())
						.setParameter("cmmnCdNm" + batchIdx, cmmnCdModel.getCmmnCdNm())
						.setParameter("cmmnCdExpln" + batchIdx, cmmnCdModel.getCmmnCdExpln())
						.setParameter("cmmnCdUpClsfId" + batchIdx, cmmnCdModel.getCmmnCdUpClsfId())
						.setParameter("cmmnCdUpId" + batchIdx, cmmnCdModel.getCmmnCdUpId())
						.setParameter("cmmnCdUserDfnVl1" + batchIdx, cmmnCdModel.getCmmnCdUserDfnVl1())
						.setParameter("cmmnCdUserDfnVl2" + batchIdx, cmmnCdModel.getCmmnCdUserDfnVl2())
						.setParameter("cmmnCdUserDfnVl3" + batchIdx, cmmnCdModel.getCmmnCdUserDfnVl3())
						.setParameter("cmmnCdSortNo" + batchIdx, cmmnCdModel.getCmmnCdSortNo())
						.setParameter("useYn" + batchIdx, cmmnCdModel.getUseYn())
						.setParameter("regDt" + batchIdx, cmmnCdModel.getRegDt())
						.setParameter("rgtrId" + batchIdx, cmmnCdModel.getRgtrId())
						.setParameter("mdfcnDt" + batchIdx, cmmnCdModel.getMdfcnDt())
						.setParameter("mdfrId" + batchIdx, cmmnCdModel.getMdfrId());
			}
			updatedRows += query.executeUpdate();

			entityManager.flush();
			entityManager.clear();
		}

		return updatedRows;
	}

	/***
	 * 공통코드 삭제
	 */
	@Override
	public int deleteCmmnCd(CmmnCdModel cmmnCdModel) throws Exception {
		String queryStr = """
				UPDATE
					cmmn_cd_table
				SET
					useYn = 'N',
					mdfcnDt = :mdfcnDt,
					mdfrId = :mdfrId
				WHERE
					useYn = 'Y'
					AND cmmnCdClsfId = :cmmnCdClsfId
					AND cmmnCdId = :cmmnCdId
				""";
		Query query = entityManager.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "))
				.setParameter("cmmnCdClsfId", cmmnCdModel.getCmmnCdClsfId())
				.setParameter("cmmnCdId", cmmnCdModel.getCmmnCdId()).setParameter("mdfcnDt", cmmnCdModel.getMdfcnDt())
				.setParameter("mdfrId", cmmnCdModel.getMdfrId());

		return query.executeUpdate();
	}

	/***
	 * 사용자용 공통코드 목록 조회
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public List<CmmnCdModel> inqUserCmmnCd(String cmmnCdClsfUserDfnVl1) throws Exception {
		String queryStr = """
				SELECT
					(
						SELECT
							cmmnCdClsfNm
						FROM
							cmmn_cd_clsf_table
						WHERE
							useYn = 'Y'
							AND cmmnCdClsfId = cmmn_cd_tbl.cmmnCdClsfId
					) AS cmmn_cd_clsf_nm,
					cmmnCdId AS cmmn_cd_id,
					cmmnCdNm AS cmmn_cd_nm,
					cmmnCdExpln AS cmmn_cd_expln,
					cmmnCdUserDfnVl1 AS cmmn_cd_user_dfn_vl1,
					cmmnCdUserDfnVl2 AS cmmn_cd_user_dfn_vl2,
					cmmnCdUserDfnVl3 AS cmmn_cd_user_dfn_vl3,
					cmmnCdSortNo AS cmmn_cd_sort_no
				FROM
					cmmn_cd_table cmmn_cd_tbl
				WHERE
					useYn = 'Y'
					AND cmmnCdClsfId IN (
						SELECT
							cmmnCdClsfId
						FROM
							cmmn_cd_clsf_table
						WHERE
							cmmnCdClsfUserDfnVl1 = :cmmnCdClsfUserDfnVl1
					)
				ORDER BY
					cmmnCdSortNo
				""";
		TypedQuery<Map> typedQuery = entityManager
				.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "), Map.class);

		typedQuery.setParameter("cmmnCdClsfUserDfnVl1", cmmnCdClsfUserDfnVl1);

		List<Map> queryResult = typedQuery.getResultList();
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		return objectMapper.convertValue(queryResult, new TypeReference<List<CmmnCdModel>>() {
		});
	}

	/***
	 * 조직도용 공통코드(부서) 목록 조회
	 */
	@SuppressWarnings({ "unchecked" })
	@Override
	public List<Map<String, Object>> inqOgnzChrtDept(CmmnCdParamDTO cmmnCdParamDTO) throws Exception {
		String queryStr = """
				WITH RECURSIVE dept_list AS (
					SELECT
						cmmn_cd_id,
						cmmn_cd_nm,
						cmmn_cd_up_id,
						cmmn_cd_up_clsf_id,
						1 AS dpth
					FROM
						cmmn_cd_table
					WHERE
						use_yn = 'Y'
						AND cmmn_cd_clsf_id = 'SOLTECH_0000'
						AND cmmn_cd_id = '000000'
					UNION ALL
					SELECT
						cmmn_cd_tbl.cmmn_cd_id,
						cmmn_cd_tbl.cmmn_cd_nm,
						cmmn_cd_tbl.cmmn_cd_up_id,
						cmmn_cd_tbl.cmmn_cd_up_clsf_id,
						depts.dpth + 1 as dpth
					FROM (
						SELECT
							cmmn_cd_id,
							cmmn_cd_nm,
							cmmn_cd_up_id,
							cmmn_cd_up_clsf_id
						FROM
							cmmn_cd_table
						WHERE
							use_yn = 'Y'
							AND cmmn_cd_clsf_id = 'SOLTECH_0000'
					) cmmn_cd_tbl
					INNER JOIN
						dept_list depts
					ON
						cmmn_cd_tbl.cmmn_cd_up_id = depts.cmmn_cd_id
					)
					SELECT
						cmmn_cd_id AS dept_cd,
						cmmn_cd_nm AS dept_nm,
						cmmn_cd_up_id AS dept_up_cd,
						1 AS dpth
					FROM
						dept_list
					ORDER BY
						dpth,
						dept_up_cd
				""";
		Query query = entityManager.createNativeQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "),
				Map.class);

		return query.getResultList();
	}

	/***
	 * 도메인별 파일 업로드 확장자 허용 상세 조회
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public CmmnCdModel getSupportExtn(CmmnCdParamDTO cmmnCdParamDTO) throws Exception {
		String queryStr = """
				SELECT
					cmmn_cd_id,
					cmmn_cd_nm,
					cmmn_cd_user_dfn_vl1,
					cmmn_cd_user_dfn_vl2,
					cmmn_cd_user_dfn_vl3,
					use_yn AS use_yn,
					TO_CHAR(reg_dt, 'YYYY-MM-DD"T"HH24:MI:SS') AS reg_dt,
					rgtr_id AS rgtr_id,
					TO_CHAR(mdfcn_dt, 'YYYY-MM-DD"T"HH24:MI:SS') AS mdfcn_dt,
					mdfr_id AS mdfr_id
				FROM
					cmmn_cd_table
				WHERE
					use_yn = 'Y'
					AND cmmn_cd_clsf_id = 'SOLTECH_9000'
					AND cmmn_cd_id = :cmmnCdId
					""";
		Query query = entityManager
				.createNativeQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "), Map.class)
				.setParameter("cmmnCdId", cmmnCdParamDTO.getCmmnCdId());

		List<Map> queryResultList = query.getResultList();
		Map queryResult = queryResultList.stream().findFirst().orElse(null);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		return objectMapper.convertValue(queryResult, new TypeReference<CmmnCdModel>() {
		});
	}

	/***
	 * 결재, 근태, 휴가, 일정 구분코드 맵핑 목록 조회
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String, Object> getAtrzMapping(CmmnCdParamDTO cmmnCdParamDTO) throws Exception {
		StringBuilder whereQuery = new StringBuilder("");
		if (!ObjectUtils.isEmpty(cmmnCdParamDTO.getCmmnCdUserDfnVl1())) {
			whereQuery.append(" AND tbl_1010.cmmn_cd_id_1010 = :cmmnCdUserDfnVl1 \r\n");
		} else if (!ObjectUtils.isEmpty(cmmnCdParamDTO.getCmmnCdUserDfnVl2())) {
			whereQuery.append(" AND tbl_1020.cmmn_cd_id_1020 = :cmmnCdUserDfnVl2 \r\n");
		}

		String queryStr = """
				SELECT
					cmmn_cd_id_1000 AS cmmn_cd_id_1000,
					cmmn_cd_nm_1000 AS cmmn_cd_nm_1000,
					tbl_1010.cmmn_cd_id_1010 AS cmmn_cd_id_1010,
					cmmn_cd_nm_1010 AS cmmn_cd_nm_1010,
					tbl_1020.cmmn_cd_id_1020 AS cmmn_cd_id_1020,
					cmmn_cd_nm_1020 AS cmmn_cd_nm_1020,
					tbl_1030.cmmn_cd_id_1030 AS cmmn_cd_id_1030,
					cmmn_cd_nm_1030 AS cmmn_cd_nm_1030
				FROM (
					SELECT
						cmmn_cd_id AS cmmn_cd_id_1000,
						cmmn_cd_nm AS cmmn_cd_nm_1000,
						UNNEST(STRING_TO_ARRAY(cmmn_cd_user_dfn_vl1, ',')) AS cmmn_cd_id_1010
					FROM
						cmmn_cd_table
					WHERE
						cmmn_cd_clsf_id = 'SOLTECH_1000'
				) tbl_1000
				LEFT OUTER JOIN (
					SELECT
						cmmn_cd_id AS cmmn_cd_id_1010,
						cmmn_cd_nm AS cmmn_cd_nm_1010,
						(STRING_TO_ARRAY(cmmn_cd_user_dfn_vl2, ','))[1] AS cmmn_cd_id_1020,
						(STRING_TO_ARRAY(cmmn_cd_user_dfn_vl2, ','))[2] AS cmmn_cd_id_1030
					FROM
						cmmn_cd_table
					WHERE
						cmmn_cd_clsf_id = 'SOLTECH_1010'
				) tbl_1010
				ON
					tbl_1000.cmmn_cd_id_1010 = tbl_1010.cmmn_cd_id_1010
				LEFT OUTER JOIN (
					SELECT
						cmmn_cd_id AS cmmn_cd_id_1020,
						cmmn_cd_nm AS cmmn_cd_nm_1020
					FROM
						cmmn_cd_table
					WHERE
						cmmn_cd_clsf_id = 'SOLTECH_1020'
				) tbl_1020
				ON
					tbl_1010.cmmn_cd_id_1020 = tbl_1020.cmmn_cd_id_1020
				LEFT OUTER JOIN (
					SELECT
						cmmn_cd_id AS cmmn_cd_id_1030,
						cmmn_cd_nm AS cmmn_cd_nm_1030
					FROM
						cmmn_cd_table
					WHERE
						cmmn_cd_clsf_id = 'SOLTECH_1030'
				) tbl_1030
				ON
					tbl_1010.cmmn_cd_id_1030 = tbl_1030.cmmn_cd_id_1030
				WHERE
					cmmn_cd_id_1000 = :cmmnCdId
					""" + whereQuery.toString() + """
				ORDER BY
					cmmn_cd_id_1000,
					tbl_1010.cmmn_cd_id_1010,
					tbl_1020.cmmn_cd_id_1020,
					tbl_1030.cmmn_cd_id_1030
				""";

		Query query = entityManager
				.createNativeQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "), Map.class)
				.setParameter("cmmnCdId", cmmnCdParamDTO.getCmmnCdId());
		if (!ObjectUtils.isEmpty(cmmnCdParamDTO.getCmmnCdUserDfnVl1())) {
			query.setParameter("cmmnCdUserDfnVl1", cmmnCdParamDTO.getCmmnCdUserDfnVl1());
		} else if (!ObjectUtils.isEmpty(cmmnCdParamDTO.getCmmnCdUserDfnVl2())) {
			query.setParameter("cmmnCdUserDfnVl2", cmmnCdParamDTO.getCmmnCdUserDfnVl2());
		}
		List<Map> queryResultList = query.getResultList();

		return queryResultList.stream().findFirst().orElse(null);
	}
}