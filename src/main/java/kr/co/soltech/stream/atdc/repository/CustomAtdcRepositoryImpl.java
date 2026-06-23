package kr.co.soltech.stream.atdc.repository;

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
import kr.co.soltech.stream.atdc.model.AtdcModel;
import kr.co.soltech.stream.atdc.model.AtdcModelId;
import kr.co.soltech.stream.atdc.model.AtdcParamDTO;
import kr.co.soltech.stream.commons.repository.CustomCntRepository;
import lombok.RequiredArgsConstructor;

/***
 * 근태 커스텀 레파지토리 클래스
 */
@RequiredArgsConstructor
class CustomAtdcRepositoryImpl implements CustomAtdcRepository {
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
	 * 근태 목록 조회
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Page<AtdcModel> inqAtdc(AtdcParamDTO atdcParamDTO, Pageable pageable) throws Exception {
		StringBuilder whereQuery = new StringBuilder("");
		whereQuery
				.append(!ObjectUtils.isEmpty(atdcParamDTO.getAtdcUserId()) ? "AND atdcUserId = :atdcUserId \r\n" : "");
		whereQuery.append(
				!ObjectUtils.isEmpty(atdcParamDTO.getAtdcYmd()) ? "AND atdcYmd LIKE '%' || :atdcYmd || '%' \r\n" : "");
		whereQuery.append(
				!ObjectUtils.isEmpty(atdcParamDTO.getAtdcTm()) ? "AND atdcTm LIKE '%' || :atdcTm || '%' \r\n" : "");
		whereQuery.append(!ObjectUtils.isEmpty(atdcParamDTO.getAtdcSeCd()) ? "AND atdcSeCd = :atdcSeCd \r\n" : "");

		String queryStr = """
				SELECT
					atdcUserId AS atdc_user_id,
					(
						SELECT
							userNm
						FROM
							user_table
						WHERE
							useYn = 'Y'
							AND userId = atdc_tbl.atdcUserId
					) AS atdc_user_nm,
					atdcYmd AS atdc_ymd,
					atdcTm AS atdc_tm,
					atdcSeCd AS atdc_se_cd,
					(
						SELECT
							cmmnCdNm
						FROM
							cmmn_cd_table
						WHERE
							useYn = 'Y'
							AND cmmnCdClsfId = 'SOLTECH_1010'
							AND cmmnCdId = atdc_tbl.atdcSeCd
					) AS atdc_se_nm,
					useYn AS use_yn,
					regDt AS reg_dt,
					rgtrId AS rgtr_id,
					mdfcnDt AS mdfcn_dt,
					mdfrId AS mdfr_id
				FROM
					atdc_table atdc_tbl
				WHERE
					useYn = 'Y'
				""" + whereQuery.toString() + """
				ORDER BY
					atdcYmd DESC,
					atdcTm,
					atdcUserId
				""";

		TypedQuery<Map> typedQuery = entityManager
				.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "), Map.class);

		String countQueryStr = """
				SELECT
					COUNT(1)
				FROM
					atdc_table atdc_tbl
				WHERE
					useYn = 'Y'
				""" + whereQuery.toString();
		TypedQuery<Long> countTypedQuery = entityManager.createQuery(countQueryStr, Long.class);

		if (!ObjectUtils.isEmpty(atdcParamDTO.getAtdcUserId())) {
			typedQuery.setParameter("atdcUserId", atdcParamDTO.getAtdcUserId());
			countTypedQuery.setParameter("atdcUserId", atdcParamDTO.getAtdcUserId());
		}
		if (!ObjectUtils.isEmpty(atdcParamDTO.getAtdcYmd())) {
			typedQuery.setParameter("atdcYmd", atdcParamDTO.getAtdcYmd());
			countTypedQuery.setParameter("atdcYmd", atdcParamDTO.getAtdcYmd());
		}
		if (!ObjectUtils.isEmpty(atdcParamDTO.getAtdcTm())) {
			typedQuery.setParameter("atdcTm", atdcParamDTO.getAtdcTm());
			countTypedQuery.setParameter("atdcTm", atdcParamDTO.getAtdcTm());
		}
		if (!ObjectUtils.isEmpty(atdcParamDTO.getAtdcSeCd())) {
			typedQuery.setParameter("atdcSeCd", atdcParamDTO.getAtdcSeCd());
			countTypedQuery.setParameter("atdcSeCd", atdcParamDTO.getAtdcSeCd());
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
			countQueryResult = customCntRepository.getAtdcCnt(countTypedQuery, atdcParamDTO);
		} else {
			countQueryResult = queryResult.size();
			pageable = PageRequest.of(0, queryResult.size() == 0 ? 1 : queryResult.size() == 0 ? 1 : queryResult.size());
		}

		return new PageImpl<AtdcModel>(objectMapper.convertValue(queryResult, new TypeReference<List<AtdcModel>>() {
		}), pageable, countQueryResult);
	}

	/***
	 * 근태 상세 조회
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public AtdcModel getAtdc(AtdcModelId atdcModelId) throws Exception {
		String queryStr = """
				SELECT
					atdcUserId AS atdc_user_id,
					(
						SELECT
							userNm
						FROM
							user_table
						WHERE
							useYn = 'Y'
							AND userId = atdc_tbl.atdcUserId
					) AS atdc_user_nm,
					atdcYmd AS atdc_ymd,
					atdcTm AS atdc_tm,
					atdcSeCd AS atdc_se_cd,
					(
						SELECT
							cmmnCdNm
						FROM
							cmmn_cd_table
						WHERE
							useYn = 'Y'
							AND cmmnCdClsfId = 'SOLTECH_1010'
							AND cmmnCdId = atdc_tbl.atdcSeCd
					) AS atdc_se_nm,
					useYn AS use_yn,
					regDt AS reg_dt,
					rgtrId AS rgtr_id,
					mdfcnDt AS mdfcn_dt,
					mdfrId AS mdfr_id
				FROM
					atdc_table atdc_tbl
				WHERE
					useYn = 'Y'
					AND atdcUserId = :atdcUserId
					AND atdcYmd = :atdcYmd
					AND atdcTm = :atdcTm
					AND atdcSeCd = :atdcSeCd
				""";

		TypedQuery<Map> typedQuery = entityManager
				.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "), Map.class)
				.setParameter("atdcUserId", atdcModelId.getAtdcUserId())
				.setParameter("atdcYmd", atdcModelId.getAtdcYmd()).setParameter("atdcTm", atdcModelId.getAtdcTm())
				.setParameter("atdcSeCd", atdcModelId.getAtdcSeCd());

		Map queryResult = typedQuery.getResultList().stream().findFirst().orElse(null);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		return objectMapper.convertValue(queryResult, new TypeReference<AtdcModel>() {
		});
	}

	/***
	 * 근태 등록/수정
	 */
	@Override
	public int upsertAtdc(AtdcModel atdcModel) throws Exception {
		String queryStr = """
				MERGE INTO
					atdc_table target_tbl
				USING (
					SELECT
						:atdcUserId AS atdc_user_id,
						:atdcYmd AS atdc_ymd,
						:atdcTm AS atdc_tm,
						:atdcSeCd AS atdc_se_cd,
						:useYn AS use_yn,
						(:regDt)::timestamp AS reg_dt,
						:rgtrId AS rgtr_id,
						(:mdfcnDt)::timestamp AS mdfcn_dt,
						:mdfrId AS mdfr_id
				) source_tbl
				ON (
					target_tbl.atdc_user_id = source_tbl.atdc_user_id
					AND target_tbl.atdc_ymd = source_tbl.atdc_ymd
					AND target_tbl.atdc_tm = source_tbl.atdc_tm
					AND target_tbl.atdc_se_cd = source_tbl.atdc_se_cd
				)
				WHEN MATCHED THEN
				UPDATE SET
					atdc_se_cd = source_tbl.atdc_se_cd,
					mdfcn_dt = source_tbl.mdfcn_dt,
					mdfr_id = source_tbl.mdfr_id
				WHEN NOT MATCHED THEN
				INSERT (
					atdc_user_id,
					atdc_ymd,
					atdc_tm,
					atdc_se_cd,
					use_yn,
					reg_dt,
					rgtr_id,
					mdfcn_dt,
					mdfr_id
				) VALUES (
					source_tbl.atdc_user_id,
					source_tbl.atdc_ymd,
					source_tbl.atdc_tm,
					source_tbl.atdc_se_cd,
					source_tbl.use_yn,
					source_tbl.reg_dt,
					source_tbl.rgtr_id,
					source_tbl.mdfcn_dt,
					source_tbl.mdfr_id
				)
				""";

		Query query = entityManager.createNativeQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "))
				.setParameter("atdcUserId", atdcModel.getAtdcUserId()).setParameter("atdcYmd", atdcModel.getAtdcYmd())
				.setParameter("atdcTm", atdcModel.getAtdcTm()).setParameter("atdcSeCd", atdcModel.getAtdcSeCd())
				.setParameter("useYn", atdcModel.getUseYn()).setParameter("regDt", atdcModel.getRegDt())
				.setParameter("rgtrId", atdcModel.getRgtrId()).setParameter("mdfcnDt", atdcModel.getMdfcnDt())
				.setParameter("mdfrId", atdcModel.getMdfrId());

		return query.executeUpdate();
	}

	/***
	 * 다중 근태 등록/수정
	 */
	@Override
	public int upsertAllAtdc(List<AtdcModel> atdcModelList) throws Exception {
		int updatedRows = 0;
		int batchSize = 50;

		for (int idx = 0; idx < atdcModelList.size(); idx += batchSize) {
			int end = Math.min(idx + batchSize, atdcModelList.size());
			List<AtdcModel> atdcModelSubList = atdcModelList.subList(idx, end);

			StringBuilder queryStr = new StringBuilder("""
					MERGE INTO
						atdc_table target_tbl
					USING (
						""");

			for (int batchIdx = 0; batchIdx < atdcModelSubList.size(); batchIdx++) {
				queryStr.append("SELECT \r\n");
				queryStr.append(":atdcUserId").append(batchIdx).append(" AS atdc_user_id, \r\n");
				queryStr.append(":atdcYmd").append(batchIdx).append(" AS atdc_ymd, \r\n");
				queryStr.append(":atdcTm").append(batchIdx).append(" AS atdc_tm, \r\n");
				queryStr.append(":atdcSeCd").append(batchIdx).append(" AS atdc_se_cd, \r\n");
				queryStr.append(":useYn").append(batchIdx).append(" AS use_yn, \r\n");
				queryStr.append("(:regDt").append(batchIdx).append(")::timestamp").append(" AS reg_dt, \r\n");
				queryStr.append(":rgtrId").append(batchIdx).append(" AS rgtr_id, \r\n");
				queryStr.append("(:mdfcnDt").append(batchIdx).append(")::timestamp").append(" AS mdfcn_dt, \r\n");
				queryStr.append(":mdfrId").append(batchIdx).append(" AS mdfr_id \r\n");
				if (batchIdx < atdcModelSubList.size() - 1) {
					queryStr.append("UNION ALL \r\n");
				}
			}
			queryStr.append("""
					) source_tbl
					ON (
						target_tbl.atdc_user_id = source_tbl.atdc_user_id
						AND target_tbl.atdc_ymd = source_tbl.atdc_ymd
						AND target_tbl.atdc_tm = source_tbl.atdc_tm
						AND target_tbl.atdc_se_cd = source_tbl.atdc_se_cd
					)
					WHEN MATCHED THEN
					UPDATE SET
						atdc_se_cd = source_tbl.atdc_se_cd,
						mdfcn_dt = source_tbl.mdfcn_dt,
						mdfr_id = source_tbl.mdfr_id
					WHEN NOT MATCHED THEN
					INSERT (
						atdc_user_id,
						atdc_ymd,
						atdc_tm,
						atdc_se_cd,
						use_yn,
						reg_dt,
						rgtr_id,
						mdfcn_dt,
						mdfr_id
					) VALUES (
						source_tbl.atdc_user_id,
						source_tbl.atdc_ymd,
						source_tbl.atdc_tm,
						source_tbl.atdc_se_cd,
						source_tbl.use_yn,
						source_tbl.reg_dt,
						source_tbl.rgtr_id,
						source_tbl.mdfcn_dt,
						source_tbl.mdfr_id
					)
					""");

			Query query = entityManager
					.createNativeQuery(queryStr.toString().stripIndent().replace("\t", " ").replace("\n", " "));

			for (int batchIdx = 0; batchIdx < atdcModelSubList.size(); batchIdx++) {
				AtdcModel atdcModel = atdcModelSubList.get(batchIdx);
				query.setParameter("atdcUserId" + batchIdx, atdcModel.getAtdcUserId())
						.setParameter("atdcYmd" + batchIdx, atdcModel.getAtdcYmd())
						.setParameter("atdcTm" + batchIdx, atdcModel.getAtdcTm())
						.setParameter("atdcSeCd" + batchIdx, atdcModel.getAtdcSeCd())
						.setParameter("useYn" + batchIdx, atdcModel.getUseYn())
						.setParameter("regDt" + batchIdx, atdcModel.getRegDt())
						.setParameter("rgtrId" + batchIdx, atdcModel.getRgtrId())
						.setParameter("mdfcnDt" + batchIdx, atdcModel.getMdfcnDt())
						.setParameter("mdfrId" + batchIdx, atdcModel.getMdfrId());
			}
			updatedRows += query.executeUpdate();

			entityManager.flush();
			entityManager.clear();
		}

		return updatedRows;
	}

	/***
	 * 근태 삭제
	 */
	@Override
	public int deleteAtdc(AtdcModel atdcModel) throws Exception {
		String queryStr = """
				UPDATE
					atdc_table
				SET
					useYn = 'N',
					mdfcnDt = :mdfcnDt,
					mdfrId = :mdfrId
				WHERE
					useYn = 'Y'
					AND atdcUserId = :atdcUserId
					AND atdcYmd = :atdcYmd
					AND atdcTm = :atdcTm
					AND atdcSeCd = :atdcSeCd
				""";
		Query query = entityManager.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "))
				.setParameter("atdcUserId", atdcModel.getAtdcUserId()).setParameter("atdcYmd", atdcModel.getAtdcYmd())
				.setParameter("atdcTm", atdcModel.getAtdcTm()).setParameter("atdcSeCd", atdcModel.getAtdcSeCd())
				.setParameter("mdfcnDt", atdcModel.getMdfcnDt()).setParameter("mdfrId", atdcModel.getMdfrId());

		return query.executeUpdate();
	}

	/***
	 * 근태 정보 목록 조회
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Page<AtdcModel> inqAtdcInfo(AtdcParamDTO atdcParamDTO, Pageable pageable) throws Exception {
		StringBuilder whereQuery = new StringBuilder("");
		whereQuery
				.append(!ObjectUtils.isEmpty(atdcParamDTO.getAtdcUserId()) ? "AND atdcUserId = :atdcUserId \r\n" : "");
		whereQuery.append(
				!ObjectUtils.isEmpty(atdcParamDTO.getAtdcYmd()) ? "AND atdcYmd LIKE '%' || :atdcYmd || '%' \r\n" : "");
		whereQuery.append(
				!ObjectUtils.isEmpty(atdcParamDTO.getAtdcTm()) ? "AND atdcTm LIKE '%' || :atdcTm || '%' \r\n" : "");
		whereQuery.append(!ObjectUtils.isEmpty(atdcParamDTO.getAtdcSeCd()) ? "AND atdcSeCd = :atdcSeCd \r\n" : "");

		String queryStr = """
				SELECT
					atdcUserId AS atdc_user_id,
					(
						SELECT
							userNm
						FROM
							user_table
						WHERE
							useYn = 'Y'
							AND userId = atdc_tbl.atdcUserId
					) AS atdc_user_nm,
					atdcYmd AS atdc_ymd,
					(
						CASE
							WHEN atdcSeCd = '00000' /*** 출근 */
							THEN MIN(atdcTm)
							ELSE MAX(atdcTm)
						END
					) AS atdc_tm,
					atdcSeCd AS atdc_se_cd,
					(
						SELECT
							cmmnCdNm
						FROM
							cmmn_cd_table
						WHERE
							useYn = 'Y'
							AND cmmnCdClsfId = 'SOLTECH_1010'
							AND cmmnCdId = atdc_tbl.atdcSeCd
					) AS atdc_se_nm,
					MAX(useYn) AS use_yn,
					MIN(regDt) AS reg_dt,
					MIN(rgtrId) AS rgtr_id,
					MAX(mdfcnDt) AS mdfcn_dt,
					MAX(mdfrId) AS mdfr_id
				FROM
					atdc_table atdc_tbl
				WHERE
					useYn = 'Y'""" + whereQuery.toString() + """
				GROUP BY
					atdcUserId,
					atdcYmd,
					atdcSeCd
				ORDER BY
					atdcYmd DESC,
					atdc_tm,
					atdcUserId
				""";

		TypedQuery<Map> typedQuery = entityManager
				.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "), Map.class);

		String countQueryStr = """
				SELECT
					COUNT(1)
				FROM (
					SELECT
						atdcUserId AS atdc_user_id
					FROM
						atdc_table atdc_tbl
					WHERE
						useYn = 'Y'
					""" + whereQuery.toString() + """
					GROUP BY
						atdcUserId,
						atdcYmd,
						atdcSeCd
				)
				""";
		TypedQuery<Long> countTypedQuery = entityManager.createQuery(countQueryStr, Long.class);

		if (!ObjectUtils.isEmpty(atdcParamDTO.getAtdcUserId())) {
			typedQuery.setParameter("atdcUserId", atdcParamDTO.getAtdcUserId());
			countTypedQuery.setParameter("atdcUserId", atdcParamDTO.getAtdcUserId());
		}
		if (!ObjectUtils.isEmpty(atdcParamDTO.getAtdcYmd())) {
			typedQuery.setParameter("atdcYmd", atdcParamDTO.getAtdcYmd());
			countTypedQuery.setParameter("atdcYmd", atdcParamDTO.getAtdcYmd());
		}
		if (!ObjectUtils.isEmpty(atdcParamDTO.getAtdcTm())) {
			typedQuery.setParameter("atdcTm", atdcParamDTO.getAtdcTm());
			countTypedQuery.setParameter("atdcTm", atdcParamDTO.getAtdcTm());
		}
		if (!ObjectUtils.isEmpty(atdcParamDTO.getAtdcSeCd())) {
			typedQuery.setParameter("atdcSeCd", atdcParamDTO.getAtdcSeCd());
			countTypedQuery.setParameter("atdcSeCd", atdcParamDTO.getAtdcSeCd());
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
			countQueryResult = customCntRepository.getAtdcInfoCnt(countTypedQuery, atdcParamDTO);
		} else {
			countQueryResult = queryResult.size();
			pageable = PageRequest.of(0, queryResult.size() == 0 ? 1 : queryResult.size() == 0 ? 1 : queryResult.size());
		}

		return new PageImpl<AtdcModel>(objectMapper.convertValue(queryResult, new TypeReference<List<AtdcModel>>() {
		}), pageable, countQueryResult);
	}
}