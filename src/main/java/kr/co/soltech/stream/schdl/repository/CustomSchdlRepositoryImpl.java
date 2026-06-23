package kr.co.soltech.stream.schdl.repository;

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
import kr.co.soltech.stream.schdl.model.SchdlModel;
import kr.co.soltech.stream.schdl.model.SchdlParamDTO;
import lombok.RequiredArgsConstructor;

/***
 * 일정 커스텀 레파지토리 클래스
 */
@RequiredArgsConstructor
class CustomSchdlRepositoryImpl implements CustomSchdlRepository {
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
	 * 일정 목록 조회
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Page<SchdlModel> inqSchdl(SchdlParamDTO schdlParamDTO, Pageable pageable) throws Exception {
		StringBuilder whereQuery = new StringBuilder("");
		whereQuery.append(
				!ObjectUtils.isEmpty(schdlParamDTO.getSchdlUserId()) ? "AND schdlUserId = :schdlUserId \r\n" : "");
		whereQuery.append(!ObjectUtils.isEmpty(schdlParamDTO.getSchdlSeCd()) ? "AND schdlSeCd = :schdlSeCd \r\n" : "");
		whereQuery.append(
				!ObjectUtils.isEmpty(schdlParamDTO.getSchdlTtl()) ? "AND schdlTtl LIKE '%' || :schdlTtl || '%' \r\n"
						: "");
		whereQuery.append(
				!ObjectUtils.isEmpty(schdlParamDTO.getSchdlCn()) ? "AND schdlCn LIKE '%' || :schdlCn || '%' \r\n" : "");
		whereQuery.append(!ObjectUtils.isEmpty(schdlParamDTO.getSchdlBgngDt())
				? "AND TO_CHAR(schdlBgngDt, 'YYYYMMDD HH24MISS') LIKE '%' || :schdlBgngDt || '%' \r\n"
				: "");
		whereQuery
				.append(!ObjectUtils.isEmpty(schdlParamDTO.getSchdlRlsYn()) ? "AND schdlRlsYn = :schdlRlsYn \r\n" : "");

		String queryStr = """
				SELECT
					schdlId AS schdl_id,
					schdlUserId AS schdl_user_id,
					(
						SELECT
							userNm
						FROM
							user_table
						WHERE
							useYn = 'Y'
							AND userId = schdl_tbl.schdlUserId
					) AS schdl_user_nm,
					schdlSeCd AS schdl_se_cd,
					(
						SELECT
							cmmnCdNm
						FROM
							cmmn_cd_table
						WHERE
							useYn = 'Y'
							AND cmmnCdClsfId = 'SOLTECH_1030'
							AND cmmnCdId = schdl_tbl.schdlSeCd
					) AS schdl_se_nm,
					schdlTtl AS schdl_ttl,
					schdlCn AS schdl_cn,
					TO_CHAR(schdlBgngDt, 'YYYY-MM-DD HH24:MI:SS') AS schdl_bgng_dt,
					TO_CHAR(schdlEndDt, 'YYYY-MM-DD HH24:MI:SS') AS schdl_end_dt,
					schdlRlsYn AS schdl_rls_yn,
					useYn AS use_yn,
					TO_CHAR(regDt, 'YYYY-MM-DD"T"HH24:MI:SS') AS reg_dt,
					rgtrId AS rgtr_id,
					TO_CHAR(mdfcnDt, 'YYYY-MM-DD"T"HH24:MI:SS') AS mdfcn_dt,
					mdfrId AS mdfr_id
				FROM
					schdl_table schdl_tbl
				WHERE
					useYn = 'Y'
					""" + whereQuery.toString() + """
				ORDER BY
					schdl_user_nm,
					schdl_se_cd,
					schdl_bgng_dt DESC
				""";

		TypedQuery<Map> typedQuery = entityManager
				.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "), Map.class);

		String countQueryStr = """
				SELECT
					COUNT(1)
				FROM
					schdl_table schdl_tbl
				WHERE
					useYn = 'Y'
				""" + whereQuery.toString();
		TypedQuery<Long> countTypedQuery = entityManager.createQuery(countQueryStr, Long.class);

		if (!ObjectUtils.isEmpty(schdlParamDTO.getSchdlUserId())) {
			typedQuery.setParameter("schdlUserId", schdlParamDTO.getSchdlUserId());
			countTypedQuery.setParameter("schdlUserId", schdlParamDTO.getSchdlUserId());
		}
		if (!ObjectUtils.isEmpty(schdlParamDTO.getSchdlSeCd())) {
			typedQuery.setParameter("schdlSeCd", schdlParamDTO.getSchdlSeCd());
			countTypedQuery.setParameter("schdlSeCd", schdlParamDTO.getSchdlSeCd());
		}
		if (!ObjectUtils.isEmpty(schdlParamDTO.getSchdlTtl())) {
			typedQuery.setParameter("schdlTtl", schdlParamDTO.getSchdlTtl());
			countTypedQuery.setParameter("schdlTtl", schdlParamDTO.getSchdlTtl());
		}
		if (!ObjectUtils.isEmpty(schdlParamDTO.getSchdlCn())) {
			typedQuery.setParameter("schdlCn", schdlParamDTO.getSchdlCn());
			countTypedQuery.setParameter("schdlCn", schdlParamDTO.getSchdlCn());
		}
		if (!ObjectUtils.isEmpty(schdlParamDTO.getSchdlBgngDt())) {
			typedQuery.setParameter("schdlBgngDt", schdlParamDTO.getSchdlBgngDt());
			countTypedQuery.setParameter("schdlBgngDt", schdlParamDTO.getSchdlBgngDt());
		}
		if (!ObjectUtils.isEmpty(schdlParamDTO.getSchdlRlsYn())) {
			typedQuery.setParameter("schdlRlsYn", schdlParamDTO.getSchdlRlsYn());
			countTypedQuery.setParameter("schdlRlsYn", schdlParamDTO.getSchdlRlsYn());
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
			countQueryResult = customCntRepository.getSchdlCnt(countTypedQuery, schdlParamDTO);
		} else {
			countQueryResult = queryResult.size();
			pageable = PageRequest.of(0, queryResult.size() == 0 ? 1 : queryResult.size());
		}

		return new PageImpl<SchdlModel>(objectMapper.convertValue(queryResult, new TypeReference<List<SchdlModel>>() {
		}), pageable, countQueryResult);
	}

	/***
	 * 일정 상세 조회
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public SchdlModel getSchdl(SchdlParamDTO schdlParamDTO) throws Exception {
		StringBuilder whereQuery = new StringBuilder("");
		whereQuery.append(!ObjectUtils.isEmpty(schdlParamDTO.getSchdlUserId())
				? "AND (schdlUserId = :schdlUserId OR schdlRlsYn = 'Y') \r\n"
				: "AND schdlRlsYn = 'Y'");

		String queryStr = """
				SELECT
					schdlId AS schdl_id,
					schdlUserId AS schdl_user_id,
					(
						SELECT
							userNm
						FROM
							user_table
						WHERE
							useYn = 'Y'
							AND userId = schdl_tbl.schdlUserId
					) AS schdl_user_nm,
					schdlSeCd AS schdl_se_cd,
					(
						SELECT
							cmmnCdNm
						FROM
							cmmn_cd_table
						WHERE
							useYn = 'Y'
							AND cmmnCdClsfId = 'SOLTECH_1030'
							AND cmmnCdId = schdl_tbl.schdlSeCd
					) AS schdl_se_nm,
					schdlTtl AS schdl_ttl,
					schdlCn AS schdl_cn,
					TO_CHAR(schdlBgngDt, 'YYYY-MM-DD HH24:MI:SS') AS schdl_bgng_dt,
					TO_CHAR(schdlEndDt, 'YYYY-MM-DD HH24:MI:SS') AS schdl_end_dt,
					schdlRlsYn AS schdl_rls_yn,
					useYn AS use_yn,
					TO_CHAR(regDt, 'YYYY-MM-DD"T"HH24:MI:SS') AS reg_dt,
					rgtrId AS rgtr_id,
					TO_CHAR(mdfcnDt, 'YYYY-MM-DD"T"HH24:MI:SS') AS mdfcn_dt,
					mdfrId AS mdfr_id
				FROM
					schdl_table schdl_tbl
				WHERE
					useYn = 'Y'
					AND schdlId = :schdlId
					""" + whereQuery.toString() + """
				ORDER BY
					schdl_user_nm,
					schdl_se_cd,
					schdl_bgng_dt DESC
				""";

		TypedQuery<Map> typedQuery = entityManager
				.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "), Map.class)
				.setParameter("schdlId", schdlParamDTO.getSchdlId());

		if (!ObjectUtils.isEmpty(schdlParamDTO.getSchdlUserId())) {
			typedQuery.setParameter("schdlUserId", schdlParamDTO.getSchdlUserId());
		}

		Map queryResult = typedQuery.getResultList().stream().findFirst().orElse(null);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		return objectMapper.convertValue(queryResult, new TypeReference<SchdlModel>() {
		});
	}

	/***
	 * 일정 등록/수정
	 */
	@Override
	public int upsertSchdl(SchdlModel schdlModel) throws Exception {
		String queryStr = """
				MERGE INTO
					schdl_table target_tbl
				USING (
					SELECT
						:schdlId AS schdl_id,
						:schdlUserId AS schdl_user_id,
						:schdlSeCd AS schdl_se_cd,
						:schdlTtl AS schdl_ttl,
						:schdlCn AS schdl_cn,
						TO_TIMESTAMP(:schdlBgngDt, 'YYYY-MM-DD"T"HH24:MI:SS') AS schdl_bgng_dt,
						TO_TIMESTAMP(:schdlEndDt, 'YYYY-MM-DD"T"HH24:MI:SS') AS schdl_end_dt,
						:schdlRlsYn AS schdl_rls_yn,
						:useYn AS use_yn,
						(:regDt)::timestamp AS reg_dt,
						:rgtrId AS rgtr_id,
						(:mdfcnDt)::timestamp AS mdfcn_dt,
						:mdfrId AS mdfr_id
				) source_tbl
				ON (
					target_tbl.schdl_id = source_tbl.schdl_id
				)
				WHEN MATCHED AND target_tbl.schdl_user_id = source_tbl.schdl_user_id THEN
				UPDATE SET
					schdl_user_id = source_tbl.schdl_user_id,
					schdl_se_cd = source_tbl.schdl_se_cd,
					schdl_bgng_dt = source_tbl.schdl_bgng_dt,
					schdl_end_dt = source_tbl.schdl_end_dt,
					schdl_ttl = source_tbl.schdl_ttl,
					schdl_cn = source_tbl.schdl_cn,
					schdl_rls_yn = source_tbl.schdl_rls_yn,
					mdfcn_dt = source_tbl.mdfcn_dt,
					mdfr_id = source_tbl.mdfr_id
				WHEN NOT MATCHED AND schdl_user_id IS NOT NULL THEN
				INSERT (
					schdl_id,
					schdl_user_id,
					schdl_se_cd,
					schdl_bgng_dt,
					schdl_end_dt,
					schdl_ttl,
					schdl_cn,
					schdl_rls_yn,
					use_yn,
					reg_dt,
					rgtr_id,
					mdfcn_dt,
					mdfr_id
				) VALUES (
					source_tbl.schdl_id,
					source_tbl.schdl_user_id,
					source_tbl.schdl_se_cd,
					source_tbl.schdl_bgng_dt,
					source_tbl.schdl_end_dt,
					source_tbl.schdl_ttl,
					source_tbl.schdl_cn,
					source_tbl.schdl_rls_yn,
					source_tbl.use_yn,
					source_tbl.reg_dt,
					source_tbl.rgtr_id,
					source_tbl.mdfcn_dt,
					source_tbl.mdfr_id
				)
				""";

		Query query = entityManager.createNativeQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "))
				.setParameter("schdlUserId", schdlModel.getSchdlUserId())
				.setParameter("schdlId", schdlModel.getSchdlId()).setParameter("schdlSeCd", schdlModel.getSchdlSeCd())
				.setParameter("schdlTtl", schdlModel.getSchdlTtl()).setParameter("schdlCn", schdlModel.getSchdlCn())
				.setParameter("schdlBgngDt", schdlModel.getSchdlBgngDt())
				.setParameter("schdlEndDt", schdlModel.getSchdlEndDt())
				.setParameter("schdlRlsYn", schdlModel.getSchdlRlsYn()).setParameter("useYn", schdlModel.getUseYn())
				.setParameter("regDt", schdlModel.getRegDt()).setParameter("rgtrId", schdlModel.getRgtrId())
				.setParameter("mdfcnDt", schdlModel.getMdfcnDt()).setParameter("mdfrId", schdlModel.getMdfrId());

		return query.executeUpdate();
	}

	/***
	 * 다중 일정 등록/수정
	 */
	@Override
	public int upsertAllSchdl(List<SchdlModel> schdlModelList) throws Exception {
		int updatedRows = 0;
		int batchSize = 50;

		for (int idx = 0; idx < schdlModelList.size(); idx += batchSize) {
			int end = Math.min(idx + batchSize, schdlModelList.size());
			List<SchdlModel> schdlModelSubList = schdlModelList.subList(idx, end);

			StringBuilder queryStr = new StringBuilder("""
					MERGE INTO
						schdl_table target_tbl
					USING (
						""");

			for (int batchIdx = 0; batchIdx < schdlModelSubList.size(); batchIdx++) {
				queryStr.append("SELECT \r\n");
				queryStr.append(":schdlId").append(batchIdx).append(" AS schdl_id, \r\n");
				queryStr.append(":schdlUserId").append(batchIdx).append(" AS schdl_user_id, \r\n");
				queryStr.append(":schdlSeCd").append(batchIdx).append(" AS schdl_se_cd, \r\n");
				queryStr.append(":schdlTtl").append(batchIdx).append(" AS schdl_ttl, \r\n");
				queryStr.append(":schdlCn").append(batchIdx).append(" AS schdl_cn, \r\n");
				queryStr.append("TO_TIMESTAMP(:schdlBgngDt").append(batchIdx).append(", 'YYYY-MM-DD\"T\"HH24:MI:SS')")
						.append(" AS schdl_bgng_dt, \r\n");
				queryStr.append("TO_TIMESTAMP(:schdlEndDt").append(batchIdx).append(", 'YYYY-MM-DD\"T\"HH24:MI:SS')")
						.append(" AS schdl_end_dt, \r\n");
				queryStr.append(":schdlRlsYn").append(batchIdx).append(" AS schdl_rls_yn, \r\n");
				queryStr.append(":useYn").append(batchIdx).append(" AS use_yn, \r\n");
				queryStr.append("(:regDt").append(batchIdx).append(")::timestamp").append(" AS reg_dt, \r\n");
				queryStr.append(":rgtrId").append(batchIdx).append(" AS rgtr_id, \r\n");
				queryStr.append("(:mdfcnDt").append(batchIdx).append(")::timestamp").append(" AS mdfcn_dt, \r\n");
				queryStr.append(":mdfrId").append(batchIdx).append(" AS mdfr_id \r\n");
				if (batchIdx < schdlModelSubList.size() - 1) {
					queryStr.append("UNION ALL \r\n");
				}
			}
			queryStr.append("""
					) source_tbl
					ON (
						target_tbl.schdl_id = source_tbl.schdl_id
					)
					WHEN MATCHED AND target_tbl.schdl_user_id = source_tbl.schdl_user_id THEN
					UPDATE SET
						schdl_user_id = source_tbl.schdl_user_id,
						schdl_se_cd = source_tbl.schdl_se_cd,
						schdl_bgng_dt = source_tbl.schdl_bgng_dt,
						schdl_end_dt = source_tbl.schdl_end_dt,
						schdl_ttl = source_tbl.schdl_ttl,
						schdl_cn = source_tbl.schdl_cn,
						schdl_rls_yn = source_tbl.schdl_rls_yn,
						mdfcn_dt = source_tbl.mdfcn_dt,
						mdfr_id = source_tbl.mdfr_id
					WHEN NOT MATCHED AND schdl_user_id IS NOT NULL THEN
					INSERT (
						schdl_id,
						schdl_user_id,
						schdl_se_cd,
						schdl_bgng_dt,
						schdl_end_dt,
						schdl_ttl,
						schdl_cn,
						schdl_rls_yn,
						use_yn,
						reg_dt,
						rgtr_id,
						mdfcn_dt,
						mdfr_id
					) VALUES (
						source_tbl.schdl_id,
						source_tbl.schdl_user_id,
						source_tbl.schdl_se_cd,
						source_tbl.schdl_bgng_dt,
						source_tbl.schdl_end_dt,
						source_tbl.schdl_ttl,
						source_tbl.schdl_cn,
						source_tbl.schdl_rls_yn,
						source_tbl.use_yn,
						source_tbl.reg_dt,
						source_tbl.rgtr_id,
						source_tbl.mdfcn_dt,
						source_tbl.mdfr_id
					)
					""");

			Query query = entityManager
					.createNativeQuery(queryStr.toString().stripIndent().replace("\t", " ").replace("\n", " "));

			for (int batchIdx = 0; batchIdx < schdlModelSubList.size(); batchIdx++) {
				SchdlModel schdlModel = schdlModelSubList.get(batchIdx);
				query.setParameter("schdlUserId" + batchIdx, schdlModel.getSchdlUserId())
						.setParameter("schdlId" + batchIdx, schdlModel.getSchdlId())
						.setParameter("schdlSeCd" + batchIdx, schdlModel.getSchdlSeCd())
						.setParameter("schdlTtl" + batchIdx, schdlModel.getSchdlTtl())
						.setParameter("schdlCn" + batchIdx, schdlModel.getSchdlCn())
						.setParameter("schdlBgngDt" + batchIdx, schdlModel.getSchdlBgngDt())
						.setParameter("schdlEndDt" + batchIdx, schdlModel.getSchdlEndDt())
						.setParameter("schdlRlsYn" + batchIdx, schdlModel.getSchdlRlsYn())
						.setParameter("useYn" + batchIdx, schdlModel.getUseYn())
						.setParameter("regDt" + batchIdx, schdlModel.getRegDt())
						.setParameter("rgtrId" + batchIdx, schdlModel.getRgtrId())
						.setParameter("mdfcnDt" + batchIdx, schdlModel.getMdfcnDt())
						.setParameter("mdfrId" + batchIdx, schdlModel.getMdfrId());
			}
			updatedRows += query.executeUpdate();

			entityManager.flush();
			entityManager.clear();
		}

		return updatedRows;
	}

	/***
	 * 일정 삭제
	 */
	@Override
	public int deleteSchdl(SchdlModel schdlModel) throws Exception {
		StringBuilder whereQuery = new StringBuilder("");
		whereQuery.append(!ObjectUtils.isEmpty(schdlModel.getSchdlUserId()) ? " AND schdlUserId = :schdlUserId"
				: " AND schdlRlsYn = 'Y'");

		String queryStr = """
				UPDATE
					schdl_table
				SET
					useYn = 'N',
					mdfcnDt = :mdfcnDt,
					mdfrId = :mdfrId
				WHERE
					useYn = 'Y'
					AND schdlId = :schdlId
				""" + whereQuery;
		Query query = entityManager.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "))
				.setParameter("schdlId", schdlModel.getSchdlId()).setParameter("mdfcnDt", schdlModel.getMdfcnDt())
				.setParameter("mdfrId", schdlModel.getMdfrId());
		if (!ObjectUtils.isEmpty(schdlModel.getSchdlUserId())) {
			query.setParameter("schdlUserId", schdlModel.getSchdlUserId());
		}

		return query.executeUpdate();
	}
}