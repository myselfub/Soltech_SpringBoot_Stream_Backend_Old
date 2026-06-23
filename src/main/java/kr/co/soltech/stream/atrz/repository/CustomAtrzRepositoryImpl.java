package kr.co.soltech.stream.atrz.repository;

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
import kr.co.soltech.stream.atrz.model.AtrzModel;
import kr.co.soltech.stream.atrz.model.AtrzParamDTO;
import kr.co.soltech.stream.commons.repository.CustomCntRepository;
import lombok.RequiredArgsConstructor;

/***
 * 결재 커스텀 레파지토리 클래스
 */
@RequiredArgsConstructor
class CustomAtrzRepositoryImpl implements CustomAtrzRepository {
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
	 * 결재 목록 조회
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Page<AtrzModel> inqAtrz(AtrzParamDTO atrzParamDTO, Pageable pageable) throws Exception {
		StringBuilder whereQuery = new StringBuilder("");
		whereQuery.append(!ObjectUtils.isEmpty(atrzParamDTO.getDrftrId()) ? " AND drftr_id = :drftrId \r\n" : "");
		whereQuery.append(!ObjectUtils.isEmpty(atrzParamDTO.getDocSeCd()) ? " AND doc_se_cd = :docSeCd \r\n" : "");
		whereQuery.append(
				!ObjectUtils.isEmpty(atrzParamDTO.getAtrzSttsSeCd()) ? " AND atrz_stts_se_cd = :atrzSttsSeCd \r\n"
						: "");
		whereQuery.append(!ObjectUtils.isEmpty(atrzParamDTO.getDrftBgngYmd())
				&& !ObjectUtils.isEmpty(atrzParamDTO.getDrftEndYmd())
						? " AND drft_ymd BETWEEN :drftBgngYmd AND :drftEndYmd \r\n"
						: "");
		whereQuery.append(
				!ObjectUtils.isEmpty(atrzParamDTO.getDocTtl()) ? " AND doc_ttl LIKE '%' || :docTtl || '%' \r\n" : "");

		String queryStr = queryAtrzData("", "", "", "") + """
				SELECT
					doc_no AS doc_no,
					doc_se_cd AS doc_se_cd,
					(
						SELECT
							cmmn_cd_nm
						FROM
							cmmn_cd_table cmmn_tbl
						WHERE
							cmmn_tbl.use_yn = 'Y'
							AND cmmn_cd_clsf_id = 'SOLTECH_1000'
							AND cmmn_cd_id = atrz_tbl.doc_se_cd
					) AS doc_se_nm,
					drft_ymd AS drft_ymd,
					drftr_id AS drftr_id,
					(
						SELECT
							user_nm
						FROM
							user_table user_tbl
						WHERE
							user_tbl.use_yn = 'Y'
							AND user_id = atrz_tbl.drftr_id
					) AS drftr_nm,
					doc_ttl AS doc_ttl,
					atrz_stts_se_cd AS atrz_stts_se_cd,
					(
						SELECT
							cmmn_cd_nm
						FROM
							cmmn_cd_table cmmn_tbl
						WHERE
							cmmn_tbl.use_yn = 'Y'
							AND cmmn_cd_clsf_id = 'SOLTECH_1001'
							AND cmmn_cd_id = atrz_tbl.atrz_stts_se_cd
					) AS atrz_stts_se_nm,
					atrz_data AS atrz_data,
					(
						SELECT
							count(opnn_id)
						FROM
							atrz_opnn_table
						WHERE
							use_yn = 'Y'
							AND doc_no = atrz_tbl.doc_no
					) AS atrz_opnn_cnt,
					(
						CASE
							WHEN EXISTS(
								SELECT
									doc_no
								FROM
									atrz_init_list
								WHERE
									doc_no = atrz_tbl.doc_no
									AND infos_key = 'file_id'
							)
							THEN 'Y'
							ELSE 'N'
						END
					) AS file_yn,
					use_yn AS use_yn,
					TO_CHAR(reg_dt, 'YYYY-MM-DD"T"HH24:MI:SS') AS reg_dt,
					rgtr_id AS rgtr_id,
					TO_CHAR(mdfcn_dt, 'YYYY-MM-DD"T"HH24:MI:SS') AS mdfcn_dt,
					mdfr_id AS mdfr_id
				FROM
					atrz_table atrz_tbl
				WHERE
					use_yn = 'Y'
					""" + whereQuery.toString() + """
				ORDER BY
					doc_no DESC
				""";
		Query query = entityManager.createNativeQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "),
				Map.class);

		String countQueryStr = """
				SELECT
					COUNT(1)
				FROM
					atrz_table atrz_tbl
				WHERE
					use_yn = 'Y'
				""" + whereQuery.toString();
		Query countQuery = entityManager
				.createNativeQuery(countQueryStr.stripIndent().replace("\t", " ").replace("\n", " "), Long.class);

		if (!ObjectUtils.isEmpty(atrzParamDTO.getDrftrId())) {
			query.setParameter("drftrId", atrzParamDTO.getDrftrId());
			countQuery.setParameter("drftrId", atrzParamDTO.getDrftrId());
		}
		if (!ObjectUtils.isEmpty(atrzParamDTO.getDocSeCd())) {
			query.setParameter("docSeCd", atrzParamDTO.getDocSeCd());
			countQuery.setParameter("docSeCd", atrzParamDTO.getDocSeCd());
		}
		if (!ObjectUtils.isEmpty(atrzParamDTO.getAtrzSttsSeCd())) {
			query.setParameter("atrzSttsSeCd", atrzParamDTO.getAtrzSttsSeCd());
			countQuery.setParameter("atrzSttsSeCd", atrzParamDTO.getAtrzSttsSeCd());
		}
		if (!ObjectUtils.isEmpty(atrzParamDTO.getDrftBgngYmd()) && !ObjectUtils.isEmpty(atrzParamDTO.getDrftEndYmd())) {
			query.setParameter("drftBgngYmd", atrzParamDTO.getDrftBgngYmd());
			query.setParameter("drftEndYmd", atrzParamDTO.getDrftEndYmd());
			countQuery.setParameter("drftBgngYmd", atrzParamDTO.getDrftBgngYmd());
			countQuery.setParameter("drftEndYmd", atrzParamDTO.getDrftEndYmd());
		}
		if (!ObjectUtils.isEmpty(atrzParamDTO.getDocTtl())) {
			query.setParameter("docTtl", atrzParamDTO.getDocTtl());
			countQuery.setParameter("docTtl", atrzParamDTO.getDocTtl());
		}
		if (!ObjectUtils.isEmpty(pageable)) {
			query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
			query.setMaxResults(pageable.getPageSize());
		}

		List<Map> queryResult = query.getResultList();
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		Map<String, Object> atrzData = null;
		for (Map map : queryResult) {
			if (map.get("atrz_data") instanceof String) {
				atrzData = objectMapper.readValue((String) map.get("atrz_data"), Map.class);
			} else {
				atrzData = (Map<String, Object>) map.get("atrz_data");
			}
			map.put("atrz_data", atrzData);
		}

		long countQueryResult = 0;
		if (!ObjectUtils.isEmpty(pageable)) {
			countQueryResult = customCntRepository.getAtrzCnt(countQuery, atrzParamDTO);
		} else {
			countQueryResult = queryResult.size();
			pageable = PageRequest.of(0, queryResult.size() == 0 ? 1 : queryResult.size());
		}

		return new PageImpl<AtrzModel>(objectMapper.convertValue(queryResult, new TypeReference<List<AtrzModel>>() {
		}), pageable, countQueryResult);
	}

	/***
	 * 결재 상세 조회
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public AtrzModel getAtrz(AtrzParamDTO atrzParamDTO) throws Exception {
		StringBuilder whereQuery = new StringBuilder("");
		whereQuery.append(!ObjectUtils.isEmpty(atrzParamDTO.getAprvrId())
				? " AND (drftr_id = :aprvrId OR (infos_key = 'aprvr_id' AND infos_value = :aprvrId)) \r\n"
				: "");

		String queryStr = queryAtrzData("", "", "", "")
				+ """
						SELECT
							doc_no AS doc_no,
							doc_se_cd AS doc_se_cd,
							(
								SELECT
									cmmn_cd_nm
								FROM
									cmmn_cd_table
								WHERE
									cmmn_cd_table.use_yn = 'Y'
									AND cmmn_cd_clsf_id = 'SOLTECH_1000'
									AND cmmn_cd_id = atrz_tbl.doc_se_cd
							) AS doc_se_nm,
							drft_ymd AS drft_ymd,
							drftr_id AS drftr_id,
							(
								SELECT
									user_nm
								FROM
									user_table
								WHERE
									user_table.use_yn = 'Y'
									AND user_id = atrz_tbl.drftr_id
							) AS drftr_nm,
							(
								WITH RECURSIVE dept_hierarchy AS (
									SELECT
										cmmn_cd_id AS cmmn_cd_id,
										cmmn_cd_nm AS cmmn_cd_nm,
										cmmn_cd_up_id AS cmmn_cd_up_id,
										cmmn_cd_nm::varchar(1000) AS user_whol_dept_nm
									FROM
										cmmn_cd_table
									WHERE
										use_yn = 'Y'
										AND cmmn_cd_clsf_id = 'SOLTECH_0000'
										AND cmmn_cd_id != '000000' /*** (CD) 솔텍시스템 */
									UNION ALL
									SELECT
										dh.cmmn_cd_id AS cmmn_cd_id,
										dh.cmmn_cd_nm AS cmmn_cd_nm,
										dept_tbl.cmmn_cd_up_id AS cmmn_cd_up_id,
										(dept_tbl.cmmn_cd_nm || ' > ' || dh.user_whol_dept_nm)::varchar(1000) AS user_whol_dept_nm
									FROM (
										SELECT
											cmmn_cd_id,
											cmmn_cd_nm,
											cmmn_cd_up_id
										FROM
											cmmn_cd_table
										WHERE
											use_yn = 'Y'
											AND cmmn_cd_clsf_id = 'SOLTECH_0000'
											AND cmmn_cd_id != '000000' /*** (CD) 솔텍시스템 */
									) dept_tbl
									JOIN
										dept_hierarchy dh
									ON
										dept_tbl.cmmn_cd_id = dh.cmmn_cd_up_id
								)
								SELECT
									user_whol_dept_nm
								FROM
									dept_hierarchy
								WHERE
									cmmn_cd_id = (
										SELECT
											user_dept_se_cd
										FROM
											user_table
										WHERE
											user_table.use_yn = 'Y'
											AND user_id = atrz_tbl.drftr_id
									)
								ORDER BY
									LENGTH(user_whol_dept_nm) DESC
								LIMIT 1
							) AS drftr_whol_dept_nm,
							(
								SELECT
									cmmn_cd_nm
								FROM
									cmmn_cd_table
								WHERE
									use_yn = 'Y'
									AND cmmn_cd_clsf_id = 'SOLTECH_0001'
									AND cmmn_cd_id = (
										SELECT
											user_jbgd_se_cd
										FROM
											user_table
										WHERE
											user_table.use_yn = 'Y'
											AND user_id = atrz_tbl.drftr_id
									)
							) AS drftr_jbgd_se_nm,
							doc_ttl AS doc_ttl,
							atrz_stts_se_cd AS atrz_stts_se_cd,
							(
								SELECT
									cmmn_cd_nm
								FROM
									cmmn_cd_table
								WHERE
									cmmn_cd_table.use_yn = 'Y'
									AND cmmn_cd_clsf_id = 'SOLTECH_1001'
									AND cmmn_cd_id = atrz_tbl.atrz_stts_se_cd
							) AS atrz_stts_se_nm,
							(
								SELECT
									JSONB_OBJECT_AGG(fields_key,
										CASE
											WHEN fields_key LIKE '%_info%' AND user_tbl.user_id IS NOT NULL
											THEN fields_value ||
												JSONB_BUILD_OBJECT(
													'aprvr_nm', COALESCE(user_tbl.user_nm, ''),
													'aprvr_dept_se_cd', COALESCE(user_tbl.user_dept_se_cd, ''),
													'aprvr_dept_se_nm', COALESCE(user_tbl.user_dept_se_nm, ''),
													'aprvr_jbgd_se_cd', COALESCE(user_tbl.user_jbgd_se_cd, ''),
													'aprvr_jbgd_se_nm', COALESCE(user_tbl.user_jbgd_se_nm, '')
												)
											ELSE fields_value
										END
									)
								FROM
									JSONB_EACH(atrz_tbl.atrz_data) AS FIELDS_KV(fields_key, fields_value)
								LEFT JOIN (
									SELECT
										user_id,
										user_nm,
										user_dept_se_cd,
										(
											SELECT
												cmmn_cd_nm
											FROM
												cmmn_cd_table
											WHERE
												use_yn = 'Y'
												AND cmmn_cd_clsf_id = 'SOLTECH_0000'
												AND cmmn_cd_id = user_dept_se_cd
										) AS user_dept_se_nm,
										user_jbgd_se_cd,
										(
											SELECT
												cmmn_cd_nm
											FROM
												cmmn_cd_table
											WHERE
												use_yn = 'Y'
												AND cmmn_cd_clsf_id = 'SOLTECH_0001'
												AND cmmn_cd_id = user_jbgd_se_cd
										) AS user_jbgd_se_nm
									FROM
										user_table
								) user_tbl
								ON
									(fields_value ->> 'aprvr_id') = user_tbl.user_id
							)::jsonb AS atrz_data,
							use_yn AS use_yn,
							TO_CHAR(reg_dt, 'YYYY-MM-DD"T"HH24:MI:SS') AS reg_dt,
							rgtr_id AS rgtr_id,
							TO_CHAR(mdfcn_dt, 'YYYY-MM-DD"T"HH24:MI:SS') AS mdfcn_dt,
							mdfr_id AS mdfr_id
						FROM
							atrz_table atrz_tbl
						WHERE
							doc_no = :docNo
							AND EXISTS (
								SELECT
									'Y'
								FROM
									atrz_init_list
								WHERE
									doc_no = :docNo
									"""
				+ whereQuery.toString() + """
							)
						""";

		Query query = entityManager
				.createNativeQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "), Map.class)
				.setParameter("docNo", atrzParamDTO.getDocNo());
		if (!ObjectUtils.isEmpty(atrzParamDTO.getAprvrId())) {
			query.setParameter("aprvrId", atrzParamDTO.getAprvrId());
		}

		List<Map> queryResultList = query.getResultList();
		Map queryResult = queryResultList.stream().findFirst().orElse(null);
		if (ObjectUtils.isEmpty(queryResult)) {
			return null;
		}

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		Map<String, Object> atrzData = null;
		if (queryResult.get("atrz_data") instanceof String) {
			atrzData = objectMapper.readValue((String) queryResult.get("atrz_data"), Map.class);
		} else {
			atrzData = (Map<String, Object>) queryResult.get("atrz_data");
		}
		queryResult.put("atrz_data", atrzData);

		return objectMapper.convertValue(queryResult, new TypeReference<AtrzModel>() {
		});
	}

	/***
	 * 결재 등록/수정
	 */
	@Override
	public int upsertAtrz(AtrzModel atrzModel) throws Exception {
		String queryStr = """
				MERGE INTO
					atrz_table target_tbl
				USING (
					SELECT
						:docNo AS doc_no,
						:docSeCd AS doc_se_cd,
						:drftYmd AS drft_ymd,
						:drftrId AS drftr_id,
						:docTtl AS doc_ttl,
						(:atrzData)::jsonb AS atrz_data,
						:atrzSttsSeCd AS atrz_stts_se_cd,
						:useYn AS use_yn,
						(:regDt)::timestamp AS reg_dt,
						:rgtrId AS rgtr_id,
						(:mdfcnDt)::timestamp AS mdfcn_dt,
						:mdfrId AS mdfr_id
				) source_tbl
				ON (
					target_tbl.doc_no = source_tbl.doc_no
				)
				WHEN MATCHED AND target_tbl.drftr_id = source_tbl.drftr_id THEN
				UPDATE SET
					doc_se_cd = source_tbl.doc_se_cd,
					drft_ymd = source_tbl.drft_ymd,
					drftr_id = source_tbl.drftr_id,
					doc_ttl = source_tbl.doc_ttl,
					atrz_data = source_tbl.atrz_data,
					atrz_stts_se_cd = source_tbl.atrz_stts_se_cd,
					mdfcn_dt = source_tbl.mdfcn_dt,
					mdfr_id = source_tbl.mdfr_id
				WHEN NOT MATCHED AND drftr_id IS NOT NULL THEN
				INSERT (
					doc_no,
					doc_se_cd,
					drft_ymd,
					drftr_id,
					doc_ttl,
					atrz_data,
					atrz_stts_se_cd,
					use_yn,
					reg_dt,
					rgtr_id,
					mdfcn_dt,
					mdfr_id
				) VALUES (
					source_tbl.doc_no,
					source_tbl.doc_se_cd,
					source_tbl.drft_ymd,
					source_tbl.drftr_id,
					source_tbl.doc_ttl,
					source_tbl.atrz_data,
					source_tbl.atrz_stts_se_cd,
					source_tbl.use_yn,
					source_tbl.reg_dt,
					source_tbl.rgtr_id,
					source_tbl.mdfcn_dt,
					source_tbl.mdfr_id
				)
				""";

		ObjectMapper objectMapper = new ObjectMapper();
		String atrzData = objectMapper.writeValueAsString(atrzModel.getAtrzData());
		Query query = entityManager.createNativeQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "))
				.setParameter("docNo", atrzModel.getDocNo()).setParameter("docSeCd", atrzModel.getDocSeCd())
				.setParameter("drftYmd", atrzModel.getDrftYmd()).setParameter("drftrId", atrzModel.getDrftrId())
				.setParameter("docTtl", atrzModel.getDocTtl()).setParameter("atrzData", atrzData)
				.setParameter("atrzSttsSeCd", atrzModel.getAtrzSttsSeCd()).setParameter("useYn", atrzModel.getUseYn())
				.setParameter("regDt", atrzModel.getRegDt()).setParameter("rgtrId", atrzModel.getRgtrId())
				.setParameter("mdfcnDt", atrzModel.getMdfcnDt()).setParameter("mdfrId", atrzModel.getMdfrId());

		return query.executeUpdate();
	}

	/***
	 * 결재 삭제
	 */
	@Override
	public int deleteAtrz(AtrzModel atrzModel) throws Exception {
		String queryStr = """
				UPDATE
					atrz_table
				SET
					useYn = 'N',
					mdfcnDt = :mdfcnDt,
					mdfrId = :mdfrId
				WHERE
					useYn = 'Y'
					AND docNo = :docNo
					AND drftrId = :drftrId
				""";

		Query query = entityManager.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "))
				.setParameter("docNo", atrzModel.getDocNo()).setParameter("drftrId", atrzModel.getDrftrId())
				.setParameter("mdfcnDt", atrzModel.getMdfcnDt()).setParameter("mdfrId", atrzModel.getMdfrId());

		return query.executeUpdate();
	}

	private String queryAtrzData(String fieldsKey, String fieldsValue, String infosKey, String infosValue)
			throws Exception {
		StringBuilder whereQuery = new StringBuilder("");
		whereQuery.append(!ObjectUtils.isEmpty(fieldsKey) ? " AND fields_key LIKE :fieldsKey\r\n" : "");
		whereQuery.append(!ObjectUtils.isEmpty(fieldsValue) ? " AND fields_value LIKE :fieldsValue \r\n" : "");
		whereQuery.append(!ObjectUtils.isEmpty(infosKey) ? " AND infos_key LIKE :infosKey \r\n" : "");
		whereQuery.append(!ObjectUtils.isEmpty(infosValue) ? " AND infos_value LIKE :infosValue \r\n" : "");

		String queryStr = """
				WITH atrz_init_list AS (
					/*** 에러 방지를 포함한 JSONB를 필드화 */
					SELECT
						doc_no AS doc_no,
						doc_se_cd AS doc_se_cd,
						(
							SELECT
								cmmn_cd_nm
							FROM
								cmmn_cd_table
							WHERE
								cmmn_cd_table.use_yn = 'Y'
								AND cmmn_cd_clsf_id = 'SOLTECH_1000'
								AND cmmn_cd_id = atrz_tbl.doc_se_cd
						) AS doc_se_nm,
						drft_ymd AS drft_ymd,
						drftr_id AS drftr_id,
						(
							SELECT
								user_nm
							FROM
								user_table
							WHERE
								user_table.use_yn = 'Y'
								AND user_id = atrz_tbl.drftr_id
						) AS drftr_nm,
						doc_ttl AS doc_ttl,
						atrz_stts_se_cd AS atrz_stts_se_cd,
						(
							SELECT
								cmmn_cd_nm
							FROM
								cmmn_cd_table
							WHERE
								cmmn_cd_table.use_yn = 'Y'
								AND cmmn_cd_clsf_id = 'SOLTECH_1001'
								AND cmmn_cd_id = atrz_tbl.atrz_stts_se_cd
						) AS atrz_stts_se_nm,
						atrz_data AS atrz_data,
						fields_key AS fields_key,
						fields_value AS fields_value,
						infos_key AS infos_key,
						infos_value AS infos_value,
						use_yn AS use_yn,
						reg_dt AS reg_dt,
						rgtr_id AS rgtr_id,
						mdfcn_dt AS mdfcn_dt,
						mdfr_id AS mdfr_id
					FROM (
						SELECT
							doc_no AS doc_no,
							doc_se_cd AS doc_se_cd,
							drft_ymd AS drft_ymd,
							drftr_id AS drftr_id,
							doc_ttl AS doc_ttl,
							atrz_stts_se_cd AS atrz_stts_se_cd,
							atrz_data AS atrz_data,
							use_yn AS use_yn,
							TO_CHAR(reg_dt, 'YYYY-MM-DD"T"HH24:MI:SS') AS reg_dt,
							rgtr_id AS rgtr_id,
							TO_CHAR(mdfcn_dt, 'YYYY-MM-DD"T"HH24:MI:SS') AS mdfcn_dt,
							mdfr_id AS mdfr_id
						FROM
							atrz_table
						WHERE
							use_yn = 'Y'
							AND EXISTS (
								SELECT
									'Y'
								FROM
									JSONB_EACH_TEXT(atrz_data) AS FIELDS_KV(fields_key, fields_value)
								WHERE
									1 = 1
					""" + (!ObjectUtils.isEmpty(fieldsKey) ? "AND fields_key LIKE :fieldsKey \r\n" : "\r\n") + """
					)
				) atrz_tbl,
				LATERAL JSONB_EACH_TEXT(atrz_data) AS FIELDS_KV(fields_key, fields_value),
				LATERAL (
					SELECT
						infos_key,
						infos_value
					FROM
						JSONB_EACH_TEXT(
							CASE
								WHEN fields_value ~ '^\\{.*\\}$' THEN fields_value::jsonb
								ELSE '{}'::jsonb
							END
						) AS INFOS_KV(infos_key, infos_value)
					WHERE
						fields_value ~ '^\\{.*\\}$'
					UNION ALL
					SELECT
						NULL AS info_key,
						NULL AS info_value
					WHERE
						fields_value !~ '^\\{.*\\}$'
				) info_tbl
				WHERE
					1 = 1
				""" + whereQuery.toString() + """
				ORDER BY
					doc_no,
					fields_key,
					infos_key
					) \r\n
				""";

		return queryStr;
	}

	/***
	 * 신규 등록시 다음으로 등록 될 문서 번호 조회
	 */
	@Override
	public String getMaxDocNo(String year) throws Exception {
		String queryStr = """
					WITH max_doc_no_list AS (
						SELECT
							COALESCE(MAX(doc_no), :year || '-0000') AS max_doc_no
						FROM
							atrz_table
						WHERE
							doc_no LIKE :year || '\\-%'
					)
					SELECT
						CONCAT(SUBSTRING(max_doc_no FROM 1 FOR 4), '-', LPAD(CAST(CAST(SUBSTRING(max_doc_no FROM 6) AS INTEGER) + 1 AS TEXT), 4, '0'))
					FROM
						max_doc_no_list
				""";

		return (String) entityManager
				.createNativeQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "), String.class)
				.setParameter("year", year).getSingleResult();
	}

	/***
	 * 결재 완료 목록 조회
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Page<AtrzModel> inqAtrzCmptn(AtrzParamDTO atrzParamDTO, Pageable pageable) throws Exception {
		StringBuilder whereQuery = new StringBuilder("");
		whereQuery.append(!ObjectUtils.isEmpty(atrzParamDTO.getAprvrId()) ? " AND aprvr_id = :aprvrId \r\n" : "");

		String fieldsKey = "atrz_info%";
		String infosKey = "atrz_dt";
		String queryStr = queryAtrzData(fieldsKey, "", infosKey, "") + """
				SELECT
					doc_no AS doc_no,
					doc_se_cd AS doc_se_cd,
					doc_se_nm AS doc_se_nm,
					drft_ymd AS drft_ymd,
					drftr_id AS drftr_id,
					drftr_nm AS drftr_nm,
					doc_ttl AS doc_ttl,
					atrz_stts_se_cd AS atrz_stts_se_cd,
					atrz_stts_se_nm AS atrz_stts_se_nm,
					atrz_data_key AS atrz_data_key,
					aprvr_id AS aprvr_id,
					(
						SELECT
							user_nm
						FROM
							user_table
						WHERE
							user_table.use_yn = 'Y'
							AND user_id = atrz_tbl.aprvr_id
					) AS aprvr_nm,
					atrz_dt AS atrz_dt,
					(
						SELECT
							count(opnn_id)
						FROM
							atrz_opnn_table
						WHERE
							use_yn = 'Y'
							AND doc_no = atrz_tbl.doc_no
					) AS atrz_opnn_cnt,
					(
						CASE
							WHEN EXISTS(
								SELECT
									doc_no
								FROM
									atrz_init_list
								WHERE
									doc_no = atrz_tbl.doc_no
									AND infos_key = 'file_id'
							)
							THEN 'Y'
							ELSE 'N'
						END
					) AS file_yn,
					use_yn AS use_yn,
					reg_dt AS reg_dt,
					rgtr_id AS rgtr_id,
					mdfcn_dt AS mdfcn_dt,
					mdfr_id AS mdfr_id
				FROM (
					SELECT
						doc_no AS doc_no,
						MAX(doc_se_cd) AS doc_se_cd,
						MAX(doc_se_nm) AS doc_se_nm,
						MAX(drft_ymd) AS drft_ymd,
						MAX(drftr_id) AS drftr_id,
						MAX(drftr_nm) AS drftr_nm,
						MAX(doc_ttl) AS doc_ttl,
						MAX(atrz_stts_se_cd) AS atrz_stts_se_cd,
						MAX(atrz_stts_se_nm) AS atrz_stts_se_nm,
						fields_key AS atrz_data_key,
						MAX((fields_value::jsonb ->> 'aprvr_id')) AS aprvr_id,
						MAX((fields_value::jsonb ->> 'atrz_dt')) AS atrz_dt,
						MAX(use_yn) AS use_yn,
						MAX(reg_dt) AS reg_dt,
						MAX(rgtr_id) AS rgtr_id,
						MAX(mdfcn_dt) AS mdfcn_dt,
						MAX(mdfr_id) AS mdfr_id
					FROM
						atrz_init_list
					WHERE
						infos_value IS NOT NULL
						AND TRIM(infos_value) != ''
					GROUP BY
						doc_no,
						fields_key
				) atrz_tbl
				WHERE
					1 = 1
					AND atrz_data_key != 'atrz_info1'
					""" + whereQuery.toString() + """
				ORDER BY
					doc_no DESC
				""";

		Query query = entityManager.createNativeQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "),
				Map.class);

		String countQueryStr = queryAtrzData(fieldsKey, "", infosKey, "") + """
				SELECT
					COUNT(1)
				FROM (
					SELECT
						doc_no AS doc_no,
						fields_key AS atrz_data_key,
						MAX((fields_value::jsonb ->> 'aprvr_id')) AS aprvr_id
					FROM
						atrz_init_list
					WHERE
						infos_value IS NOT NULL
						AND TRIM(infos_value) != ''
					GROUP BY
						doc_no,
						fields_key
				) atrz_tbl
				WHERE
					1 = 1
					AND atrz_data_key != 'atrz_info1'
				""" + whereQuery.toString();
		Query countQuery = entityManager
				.createNativeQuery(countQueryStr.stripIndent().replace("\t", " ").replace("\n", " "), Long.class);

		query.setParameter("fieldsKey", fieldsKey);
		countQuery.setParameter("fieldsKey", fieldsKey);
		query.setParameter("infosKey", infosKey);
		countQuery.setParameter("infosKey", infosKey);
		if (!ObjectUtils.isEmpty(atrzParamDTO.getAprvrId())) {
			query.setParameter("aprvrId", atrzParamDTO.getAprvrId());
			countQuery.setParameter("aprvrId", atrzParamDTO.getAprvrId());
		}
		if (!ObjectUtils.isEmpty(pageable)) {
			query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
			query.setMaxResults(pageable.getPageSize());
		}

		List<Map> queryResult = query.getResultList();
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		long countQueryResult = 0;
		if (!ObjectUtils.isEmpty(pageable)) {
			countQueryResult = customCntRepository.getAtrzCmptnCnt(countQuery, atrzParamDTO);
		} else {
			countQueryResult = queryResult.size();
			pageable = PageRequest.of(0, queryResult.size() == 0 ? 1 : queryResult.size());
		}

		return new PageImpl<AtrzModel>(objectMapper.convertValue(queryResult, new TypeReference<List<AtrzModel>>() {
		}), pageable, countQueryResult);
	}

	/***
	 * 결재 미완료 목록 조회
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Page<AtrzModel> inqAtrzUnCmptn(AtrzParamDTO atrzParamDTO, Pageable pageable) throws Exception {
		StringBuilder whereQuery = new StringBuilder("");
		whereQuery.append(!ObjectUtils.isEmpty(atrzParamDTO.getAprvrId()) ? " AND aprvr_id = :aprvrId \r\n" : "");

		String fieldsKey = "atrz_info%";
		String infosKey = "atrz_dt";
		String queryStr = queryAtrzData(fieldsKey, "", infosKey, "") + """
				SELECT
					DISTINCT ON (doc_no) doc_no AS doc_no,
					doc_se_cd AS doc_se_cd,
					doc_se_nm AS doc_se_nm,
					drft_ymd AS drft_ymd,
					drftr_id AS drftr_id,
					drftr_nm AS drftr_nm,
					doc_ttl AS doc_ttl,
					atrz_stts_se_cd AS atrz_stts_se_cd,
					atrz_stts_se_nm AS atrz_stts_se_nm,
					atrz_data_key AS atrz_data_key,
					aprvr_id AS aprvr_id,
					(
						SELECT
							user_nm
						FROM
							user_table
						WHERE
							user_table.use_yn = 'Y'
							AND user_id = atrz_tbl.aprvr_id
					) AS aprvr_nm,
					(
						SELECT
							count(opnn_id)
						FROM
							atrz_opnn_table
						WHERE
							use_yn = 'Y'
							AND doc_no = atrz_tbl.doc_no
					) AS atrz_opnn_cnt,
					(
						CASE
							WHEN EXISTS(
								SELECT
									doc_no
								FROM
									atrz_init_list
								WHERE
									doc_no = atrz_tbl.doc_no
									AND infos_key = 'file_id'
							)
							THEN 'Y'
							ELSE 'N'
						END
					) AS file_yn,
					use_yn AS use_yn,
					reg_dt AS reg_dt,
					rgtr_id AS rgtr_id,
					mdfcn_dt AS mdfcn_dt,
					mdfr_id AS mdfr_id
				FROM (
					SELECT
						doc_no AS doc_no,
						MAX(doc_se_cd) AS doc_se_cd,
						MAX(doc_se_nm) AS doc_se_nm,
						MAX(drft_ymd) AS drft_ymd,
						MAX(drftr_id) AS drftr_id,
						MAX(drftr_nm) AS drftr_nm,
						MAX(doc_ttl) AS doc_ttl,
						MAX(atrz_stts_se_cd) AS atrz_stts_se_cd,
						MAX(atrz_stts_se_nm) AS atrz_stts_se_nm,
						fields_key AS atrz_data_key,
						MAX((fields_value::jsonb ->> 'aprvr_id')) AS aprvr_id,
						MAX(use_yn) AS use_yn,
						MAX(reg_dt) AS reg_dt,
						MAX(rgtr_id) AS rgtr_id,
						MAX(mdfcn_dt) AS mdfcn_dt,
						MAX(mdfr_id) AS mdfr_id
					FROM
						atrz_init_list
					WHERE
						atrz_stts_se_cd = '00000' /*** (CD) 진행중 */
						AND (
							infos_value IS NULL
							OR TRIM(infos_value) = ''
						)
					GROUP BY
						doc_no,
						fields_key
				) atrz_tbl
				WHERE
					1 = 1
					AND atrz_data_key != 'atrz_info1'
					AND atrz_data_key = (
						SELECT
							MIN(fields_key) AS fields_key
						FROM
							atrz_init_list
						WHERE
							atrz_stts_se_cd = '00000' /*** (CD) 진행중 */
							AND (
								infos_value IS NULL
								OR TRIM(infos_value) = ''
							)
							AND doc_no = atrz_tbl.doc_no
						GROUP BY
							doc_no
					)
				""" + whereQuery.toString() + """
				ORDER BY
						doc_no DESC
					""";

		Query query = entityManager.createNativeQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "),
				Map.class);

		String countQueryStr = queryAtrzData(fieldsKey, "", infosKey, "") + """
				SELECT
					COUNT(1)
				FROM (
					SELECT
						doc_no AS doc_no,
						fields_key AS atrz_data_key,
						MAX((fields_value::jsonb ->> 'aprvr_id')) AS aprvr_id
					FROM
						atrz_init_list
					WHERE
						atrz_stts_se_cd = '00000' /*** (CD) 진행중 */
						AND (
							infos_value IS NULL
							OR TRIM(infos_value) = ''
						)
					GROUP BY
						doc_no,
						fields_key
				) atrz_tbl
				WHERE
					1 = 1
					AND atrz_data_key != 'atrz_info1'
					AND atrz_data_key = (
						SELECT
							MIN(fields_key) AS fields_key
						FROM
							atrz_init_list
						WHERE
							atrz_stts_se_cd = '00000' /*** (CD) 진행중 */
							AND (
								infos_value IS NULL
								OR TRIM(infos_value) = ''
							)
							AND doc_no = atrz_tbl.doc_no
						GROUP BY
							doc_no
					)
				""" + whereQuery.toString();
		Query countQuery = entityManager
				.createNativeQuery(countQueryStr.stripIndent().replace("\t", " ").replace("\n", " "), Long.class);

		query.setParameter("fieldsKey", fieldsKey);
		countQuery.setParameter("fieldsKey", fieldsKey);
		query.setParameter("infosKey", infosKey);
		countQuery.setParameter("infosKey", infosKey);
		if (!ObjectUtils.isEmpty(atrzParamDTO.getAprvrId())) {
			query.setParameter("aprvrId", atrzParamDTO.getAprvrId());
			countQuery.setParameter("aprvrId", atrzParamDTO.getAprvrId());
		}
		if (!ObjectUtils.isEmpty(pageable)) {
			query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
			query.setMaxResults(pageable.getPageSize());
		}

		List<Map> queryResult = query.getResultList();
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		long countQueryResult = 0;
		if (!ObjectUtils.isEmpty(pageable)) {
			countQueryResult = customCntRepository.getAtrzUnCmptnCnt(countQuery, atrzParamDTO);
		} else {
			countQueryResult = queryResult.size();
			pageable = PageRequest.of(0, queryResult.size() == 0 ? 1 : queryResult.size());
		}

		return new PageImpl<AtrzModel>(objectMapper.convertValue(queryResult, new TypeReference<List<AtrzModel>>() {
		}), pageable, countQueryResult);
	}

	/***
	 * 결재 데이터의 JSON을 Key값으로 조회하여 Map으로 파싱
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> inqAtrzData(String fieldsKey, String fieldsValue, String infosKey,
			String infosValue) throws Exception {
		String queryStr = queryAtrzData(fieldsKey, fieldsValue, infosKey, infosValue) + """
				SELECT
					doc_no AS doc_no,
					fields_key AS fields_key,
					fields_value AS fields_value,
					infos_key AS infos_key,
					infos_value AS infos_value
				FROM
					atrz_init_list
				ORDER BY
					doc_no DESC
				""";

		Query query = entityManager.createNativeQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "),
				Map.class);
		if (!ObjectUtils.isEmpty(fieldsKey)) {
			query.setParameter("fieldsKey", fieldsKey);
		}
		if (!ObjectUtils.isEmpty(fieldsValue)) {
			query.setParameter("fieldsValue", fieldsValue);
		}
		if (!ObjectUtils.isEmpty(infosKey)) {
			query.setParameter("infosKey", infosKey);
		}
		if (!ObjectUtils.isEmpty(infosValue)) {
			query.setParameter("infosValue", infosValue);
		}

		return query.getResultList();
	}

	/***
	 * 결재 상태 수정
	 */
	@Override
	public int updateAtrzStts(AtrzModel atrzModel, String fieldsKey) throws Exception {
		String infosKey = "atrz_dt";

		String queryStr = queryAtrzData(fieldsKey, "", "", "")
				+ """
						, atrz_list AS (
							SELECT
								doc_no AS doc_no,
								doc_se_cd AS doc_se_cd,
								doc_se_nm AS doc_se_nm,
								drft_ymd AS drft_ymd,
								drftr_id AS drftr_id,
								drftr_nm AS drftr_nm,
								doc_ttl AS doc_ttl,
								atrz_stts_se_cd AS atrz_stts_se_cd,
								atrz_stts_se_nm AS atrz_stts_se_nm,
								atrz_data AS atrz_data,
								fields_key AS fields_key,
								fields_value AS fields_value,
								infos_key AS infos_key,
								infos_value AS infos_value,
								use_yn AS use_yn,
								reg_dt AS reg_dt,
								rgtr_id AS rgtr_id,
								mdfcn_dt AS mdfcn_dt,
								mdfr_id AS mdfr_id
							FROM
								atrz_init_list
							WHERE
								infos_key LIKE :infosKey
							ORDER BY
								doc_no,
								fields_key,
								infos_key
						), last_atrz_data_key_list AS (
							/*** 마지막 결재자 찾기 */
							SELECT
								MAX(fields_key) AS last_atrz_data_key
							FROM
								atrz_init_list
							WHERE
								doc_no = :docNo
							GROUP BY
								doc_no
							ORDER BY
								last_atrz_data_key DESC
						), exists_list AS (
							/*** 미결재 목록 */
							SELECT
								DISTINCT ON (doc_no) doc_no AS doc_no,
								doc_se_cd AS doc_se_cd,
								doc_se_nm AS doc_se_nm,
								drft_ymd AS drft_ymd,
								drftr_id AS drftr_id,
								drftr_nm AS drftr_nm,
								doc_ttl AS doc_ttl,
								atrz_stts_se_cd AS atrz_stts_se_cd,
								atrz_stts_se_nm AS atrz_stts_se_nm,
								atrz_data_key AS atrz_data_key,
								aprvr_id AS aprvr_id,
								(
									SELECT
										user_nm
									FROM
										user_table
									WHERE
										user_table.use_yn = 'Y'
										AND user_id = aprvr_id
								) AS aprvr_nm,
								use_yn AS use_yn,
								reg_dt AS reg_dt,
								rgtr_id AS rgtr_id,
								mdfcn_dt AS mdfcn_dt,
								mdfr_id AS mdfr_id
							FROM (
								SELECT
									doc_no AS doc_no,
									MAX(doc_se_cd) AS doc_se_cd,
									MAX(doc_se_nm) AS doc_se_nm,
									MAX(drft_ymd) AS drft_ymd,
									MAX(drftr_id) AS drftr_id,
									MAX(drftr_nm) AS drftr_nm,
									MAX(doc_ttl) AS doc_ttl,
									MAX(atrz_stts_se_cd) AS atrz_stts_se_cd,
									MAX(atrz_stts_se_nm) AS atrz_stts_se_nm,
									fields_key AS atrz_data_key,
									MAX((fields_value::jsonb ->> 'aprvr_id')) AS aprvr_id,
									MAX(use_yn) AS use_yn,
									MAX(reg_dt) AS reg_dt,
									MAX(rgtr_id) AS rgtr_id,
									MAX(mdfcn_dt) AS mdfcn_dt,
									MAX(mdfr_id) AS mdfr_id
								FROM
									atrz_list
								WHERE
									atrz_stts_se_cd = '00000' /*** (CD) 진행중 */
									AND (
										infos_value IS NULL
										OR TRIM(infos_value) = ''
									)
								GROUP BY
									doc_no,
									fields_key
							)
							WHERE
								1 = 1
								AND aprvr_id = :aprvrId
								AND doc_no = :docNo
							ORDER BY
								doc_no DESC,
								atrz_data_key DESC
								)
								UPDATE
									atrz_table
								SET
									atrz_data = jsonb_set(
										jsonb_set(
											atrz_data,
											ARRAY[(SELECT atrz_data_key FROM exists_list LIMIT 1), :infosKey],
											to_jsonb(:atrzDt), true
										),
										ARRAY[(SELECT atrz_data_key FROM exists_list LIMIT 1),
										'atrz_stts_se_cd'],
										to_jsonb(:atrzSttsSeCd), true
									),
									atrz_stts_se_cd = (
										CASE
											WHEN :atrzSttsSeCd = '00020' /*** (CD) 결재 */
												AND (SELECT atrz_data_key FROM exists_list LIMIT 1)
												= (SELECT last_atrz_data_key FROM last_atrz_data_key_list LIMIT 1)
											THEN :atrzSttsSeCd
											WHEN :atrzSttsSeCd = '00020' /*** (CD) 결재 */
												AND (SELECT atrz_data_key FROM exists_list LIMIT 1)
												!= (SELECT last_atrz_data_key FROM last_atrz_data_key_list LIMIT 1)
											THEN '00000' /*** (CD) 진행중 */
											WHEN :atrzSttsSeCd IN (SELECT cmmn_cd_id FROM cmmn_cd_table WHERE cmmn_cd_clsf_id = 'SOLTECH_1001')
											THEN :atrzSttsSeCd
											ELSE atrz_stts_se_cd
										END
									),
									mdfcn_dt = :mdfcnDt,
									mdfr_id = :mdfrId
								WHERE
									doc_no = :docNo
									AND EXISTS (
										SELECT
											'Y'
										FROM
											exists_list
									)
									""";

		Query query = entityManager.createNativeQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "))
				.setParameter("fieldsKey", fieldsKey).setParameter("infosKey", infosKey)
				.setParameter("docNo", atrzModel.getDocNo()).setParameter("aprvrId", atrzModel.getAprvrId())
				.setParameter("atrzDt", atrzModel.getAtrzDt()).setParameter("atrzSttsSeCd", atrzModel.getAtrzSttsSeCd())
				.setParameter("mdfcnDt", atrzModel.getMdfcnDt()).setParameter("mdfrId", atrzModel.getMdfrId());

		return query.executeUpdate();
	}
}