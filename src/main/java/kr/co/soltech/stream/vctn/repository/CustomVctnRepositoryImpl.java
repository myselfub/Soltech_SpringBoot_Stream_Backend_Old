package kr.co.soltech.stream.vctn.repository;

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
import kr.co.soltech.stream.commons.repository.CustomCntRepository;
import kr.co.soltech.stream.vctn.model.VctnModel;
import kr.co.soltech.stream.vctn.model.VctnModelId;
import kr.co.soltech.stream.vctn.model.VctnParamDTO;
import lombok.RequiredArgsConstructor;

/***
 * 휴가 커스텀 레파지토리 클래스
 */
@RequiredArgsConstructor
class CustomVctnRepositoryImpl implements CustomVctnRepository {
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
	 * 휴가 목록 조회
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Page<VctnModel> inqVctn(VctnParamDTO vctnParamDTO, Pageable pageable) throws Exception {
		StringBuilder whereQuery = new StringBuilder("");
		whereQuery.append(
				!ObjectUtils.isEmpty(vctnParamDTO.getVctnUserId()) ? "AND vctn_user_id = :vctnUserId \r\n" : "");
		whereQuery.append(!ObjectUtils.isEmpty(vctnParamDTO.getVctnSeCd()) ? "AND vctn_se_cd = :vctnSeCd \r\n" : "");
		whereQuery.append(!ObjectUtils.isEmpty(vctnParamDTO.getVctnBgngYmd())
				? "AND vctn_bgng_ymd LIKE '%' || :vctnBgngYmd || '%' \r\n"
				: "");

		String queryStr = """
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
						""" + (!ObjectUtils.isEmpty(vctnParamDTO.getVctnUserId()) ? """
				 \r\n AND cmmn_cd_id = (
					SELECT
						user_dept_se_cd
					FROM
						user_table
					WHERE
						use_yn = 'Y'
						AND user_id = :vctnUserId
				) \r\n
				""" : "") + """
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
					vctn_user_id AS vctn_user_id,
					user_tbl.user_nm AS vctn_user_nm,
					(
						SELECT
							cmmn_cd_nm
						FROM
							cmmn_cd_table
						WHERE
							use_yn = 'Y'
							AND cmmn_cd_clsf_id = 'SOLTECH_0000'
							AND cmmn_cd_id = user_tbl.user_dept_se_cd
					) AS user_dept_se_nm,
					(
						SELECT
							user_whol_dept_nm
						FROM
							dept_hierarchy
						WHERE
							cmmn_cd_id = user_tbl.user_dept_se_cd
						ORDER BY
							LENGTH(user_whol_dept_nm) DESC
						LIMIT 1
					) AS user_whol_dept_nm,
					(
						SELECT
							cmmn_cd_nm
						FROM
							cmmn_cd_table
						WHERE
							use_yn = 'Y'
							AND cmmn_cd_clsf_id = 'SOLTECH_0001'
							AND cmmn_cd_id = user_tbl.user_jbgd_se_cd
					) AS user_jbgd_se_nm,
					vctn_se_cd AS vctn_se_cd,
					(
						SELECT
							cmmn_cd_nm
						FROM
							cmmn_cd_table
						WHERE
							use_yn = 'Y'
							AND cmmn_cd_clsf_id = 'SOLTECH_1020'
							AND cmmn_cd_id = vctn_tbl.vctn_se_cd
					) AS vctn_se_nm,
					vctn_bgng_ymd AS vctn_bgng_ymd,
					vctn_end_ymd AS vctn_end_ymd,
					vctn_use_cnt AS vctn_use_cnt,
					vctn_tbl.atrz_doc_no AS atrz_doc_no,
					atrz_tbl.drft_ymd AS drft_ymd,
					(
						SELECT
							cmmn_cd_nm
						FROM
							cmmn_cd_table
						WHERE
							use_yn = 'Y'
							AND cmmn_cd_clsf_id = 'SOLTECH_1001'
							AND cmmn_cd_id = atrz_tbl.atrz_stts_se_cd
					) AS atrz_stts_se_nm,
					vctn_tbl.use_yn AS use_yn,
					TO_CHAR(vctn_tbl.reg_dt, 'YYYY-MM-DD"T"HH24:MI:SS') AS reg_dt,
					vctn_tbl.rgtr_id AS rgtr_id,
					TO_CHAR(vctn_tbl.mdfcn_dt, 'YYYY-MM-DD"T"HH24:MI:SS') AS mdfcn_dt,
					vctn_tbl.mdfr_id AS mdfr_id
				FROM
					vctn_table vctn_tbl,
					user_table user_tbl,
					atrz_table atrz_tbl
				WHERE
					vctn_tbl.use_yn = 'Y'
					AND user_tbl.use_yn = 'Y'
					AND atrz_tbl.use_yn = 'Y'
					""" + whereQuery.toString() + """
					AND vctn_tbl.vctn_user_id = user_tbl.user_id
					AND vctn_tbl.atrz_doc_no = atrz_tbl.doc_no
				ORDER BY
					vctn_user_nm,
					vctn_se_cd,
					vctn_bgng_ymd DESC
				""";

		Query query = entityManager.createNativeQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "),
				Map.class);

		String countQueryStr = """
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
						""" + (!ObjectUtils.isEmpty(vctnParamDTO.getVctnUserId()) ? """
				 \r\n AND cmmn_cd_id = (
					SELECT
						user_dept_se_cd
					FROM
						user_table
					WHERE
						use_yn = 'Y'
						AND user_id = :vctnUserId
				) \r\n
				""" : "") + """
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
					COUNT(1)
				FROM
					vctn_table vctn_tbl,
					user_table user_tbl,
					atrz_table atrz_tbl
				WHERE
					vctn_tbl.use_yn = 'Y'
					AND user_tbl.use_yn = 'Y'
					AND atrz_tbl.use_yn = 'Y'
					""" + whereQuery.toString() + """
					AND vctn_tbl.vctn_user_id = user_tbl.user_id
					AND vctn_tbl.atrz_doc_no = atrz_tbl.doc_no
				""";
		Query countQuery = entityManager
				.createNativeQuery(countQueryStr.stripIndent().replace("\t", " ").replace("\n", " "), Long.class);

		if (!ObjectUtils.isEmpty(vctnParamDTO.getVctnUserId())) {
			query.setParameter("vctnUserId", vctnParamDTO.getVctnUserId());
			countQuery.setParameter("vctnUserId", vctnParamDTO.getVctnUserId());
		}
		if (!ObjectUtils.isEmpty(vctnParamDTO.getVctnSeCd())) {
			query.setParameter("vctnSeCd", vctnParamDTO.getVctnSeCd());
			countQuery.setParameter("vctnSeCd", vctnParamDTO.getVctnSeCd());
		}
		if (!ObjectUtils.isEmpty(vctnParamDTO.getVctnBgngYmd())) {
			query.setParameter("vctnBgngYmd", vctnParamDTO.getVctnBgngYmd());
			countQuery.setParameter("vctnBgngYmd", vctnParamDTO.getVctnBgngYmd());
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
			countQueryResult = customCntRepository.getVctnCnt(countQuery, vctnParamDTO);
		} else {
			countQueryResult = queryResult.size();
			pageable = PageRequest.of(0, queryResult.size() == 0 ? 1 : queryResult.size());
		}

		return new PageImpl<VctnModel>(objectMapper.convertValue(queryResult, new TypeReference<List<VctnModel>>() {
		}), pageable, countQueryResult);
	}

	/***
	 * 휴가 상세 조회
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public VctnModel getVctn(VctnModelId vctnModelId) throws Exception {
		String queryStr = """
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
						AND cmmn_cd_id = (
							SELECT
								user_dept_se_cd
							FROM
								user_table
							WHERE
								use_yn = 'Y'
								AND user_id = :vctnUserId
						)
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
					vctn_user_id AS vctn_user_id,
					user_tbl.user_nm AS vctn_user_nm,
					(
						SELECT
							cmmn_cd_nm
						FROM
							cmmn_cd_table
						WHERE
							use_yn = 'Y'
							AND cmmn_cd_clsf_id = 'SOLTECH_0000'
							AND cmmn_cd_id = user_tbl.user_dept_se_cd
					) AS user_dept_se_nm,
					(
						SELECT
							user_whol_dept_nm
						FROM
							dept_hierarchy
						WHERE
							cmmn_cd_id = user_tbl.user_dept_se_cd
						ORDER BY
							LENGTH(user_whol_dept_nm) DESC
						LIMIT 1
					) AS user_whol_dept_nm,
					(
						SELECT
							cmmn_cd_nm
						FROM
							cmmn_cd_table
						WHERE
							use_yn = 'Y'
							AND cmmn_cd_clsf_id = 'SOLTECH_0001'
							AND cmmn_cd_id = user_tbl.user_jbgd_se_cd
					) AS user_jbgd_se_nm,
					vctn_se_cd AS vctn_se_cd,
					(
						SELECT
							cmmn_cd_nm
						FROM
							cmmn_cd_table
						WHERE
							use_yn = 'Y'
							AND cmmn_cd_clsf_id = 'SOLTECH_1020'
							AND cmmn_cd_id = vctn_tbl.vctn_se_cd
					) AS vctn_se_nm,
					vctn_bgng_ymd AS vctn_bgng_ymd,
					vctn_end_ymd AS vctn_end_ymd,
					vctn_use_cnt AS vctn_use_cnt,
					vctn_tbl.atrz_doc_no AS atrz_doc_no,
					atrz_tbl.drft_ymd AS drft_ymd,
					(
						SELECT
							cmmn_cd_nm
						FROM
							cmmn_cd_table
						WHERE
							use_yn = 'Y'
							AND cmmn_cd_clsf_id = 'SOLTECH_1001'
							AND cmmn_cd_id = atrz_tbl.atrz_stts_se_cd
					) AS atrz_stts_se_nm,
					vctn_tbl.use_yn AS use_yn,
					TO_CHAR(vctn_tbl.reg_dt, 'YYYY-MM-DD"T"HH24:MI:SS') AS reg_dt,
					vctn_tbl.rgtr_id AS rgtr_id,
					TO_CHAR(vctn_tbl.mdfcn_dt, 'YYYY-MM-DD"T"HH24:MI:SS') AS mdfcn_dt,
					vctn_tbl.mdfr_id AS mdfr_id
				FROM
					vctn_table vctn_tbl,
					user_table user_tbl,
					atrz_table atrz_tbl
				WHERE
					vctn_tbl.use_yn = 'Y'
					AND user_tbl.use_yn = 'Y'
					AND atrz_tbl.use_yn = 'Y'
					AND vctn_user_id = :vctnUserId
					AND vctn_se_cd = :vctnSeCd
					AND vctn_bgng_ymd = :vctnBgngYmd
					AND vctn_tbl.vctn_user_id = user_tbl.user_id
					AND vctn_tbl.atrz_doc_no = atrz_tbl.doc_no
				ORDER BY
					vctn_user_nm,
					vctn_se_cd,
					vctn_bgng_ymd DESC
				""";

		Query query = entityManager
				.createNativeQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "), Map.class)
				.setParameter("vctnUserId", vctnModelId.getVctnUserId())
				.setParameter("vctnSeCd", vctnModelId.getVctnSeCd())
				.setParameter("vctnBgngYmd", vctnModelId.getVctnBgngYmd());

		List<Map> queryResultList = query.getResultList();
		Map queryResult = queryResultList.stream().findFirst().orElse(null);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		return objectMapper.convertValue(queryResult, new TypeReference<VctnModel>() {
		});
	}

	/***
	 * 휴가 등록/수정
	 */
	@Override
	public int upsertVctn(VctnModel vctnModel) throws Exception {
		String queryStr = """
				MERGE INTO
					vctn_table target_tbl
				USING (
					SELECT
						:vctnUserId AS vctn_user_id,
						:vctnSeCd AS vctn_se_cd,
						:vctnBgngYmd AS vctn_bgng_ymd,
						:vctnEndYmd AS vctn_end_ymd,
						:vctnUseCnt AS vctn_use_cnt,
						:atrzDocNo AS atrz_doc_no,
						:useYn AS use_yn,
						(:regDt)::timestamp AS reg_dt,
						:rgtrId AS rgtr_id,
						(:mdfcnDt)::timestamp AS mdfcn_dt,
						:mdfrId AS mdfr_id
				) source_tbl
				ON (
					target_tbl.vctn_user_id = source_tbl.vctn_user_id
					AND target_tbl.vctn_se_cd = source_tbl.vctn_se_cd
					AND target_tbl.vctn_bgng_ymd = source_tbl.vctn_bgng_ymd
				)
				WHEN MATCHED THEN
				UPDATE SET
					vctn_end_ymd = source_tbl.vctn_end_ymd,
					vctn_use_cnt = source_tbl.vctn_use_cnt,
					atrz_doc_no = source_tbl.atrz_doc_no,
					mdfcn_dt = source_tbl.mdfcn_dt,
					mdfr_id = source_tbl.mdfr_id
				WHEN NOT MATCHED THEN
				INSERT (
					vctn_user_id,
					vctn_se_cd,
					vctn_bgng_ymd,
					vctn_end_ymd,
					vctn_use_cnt,
					atrz_doc_no,
					use_yn,
					reg_dt,
					rgtr_id,
					mdfcn_dt,
					mdfr_id
				) VALUES (
					source_tbl.vctn_user_id,
					source_tbl.vctn_se_cd,
					source_tbl.vctn_bgng_ymd,
					source_tbl.vctn_end_ymd,
					source_tbl.vctn_use_cnt,
					source_tbl.atrz_doc_no,
					source_tbl.use_yn,
					source_tbl.reg_dt,
					source_tbl.rgtr_id,
					source_tbl.mdfcn_dt,
					source_tbl.mdfr_id
				)
				""";

		Query query = entityManager.createNativeQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "))
				.setParameter("vctnUserId", vctnModel.getVctnUserId()).setParameter("vctnSeCd", vctnModel.getVctnSeCd())
				.setParameter("vctnBgngYmd", vctnModel.getVctnBgngYmd())
				.setParameter("vctnEndYmd", vctnModel.getVctnEndYmd())
				.setParameter("vctnUseCnt", vctnModel.getVctnUseCnt())
				.setParameter("atrzDocNo", vctnModel.getAtrzDocNo()).setParameter("useYn", vctnModel.getUseYn())
				.setParameter("regDt", vctnModel.getRegDt()).setParameter("rgtrId", vctnModel.getRgtrId())
				.setParameter("mdfcnDt", vctnModel.getMdfcnDt()).setParameter("mdfrId", vctnModel.getMdfrId());

		return query.executeUpdate();
	}

	/***
	 * 다중 휴가 등록/수정
	 */
	@Override
	public int upsertAllVctn(List<VctnModel> vctnModelList) throws Exception {
		int updatedRows = 0;
		int batchSize = 50;

		for (int idx = 0; idx < vctnModelList.size(); idx += batchSize) {
			int end = Math.min(idx + batchSize, vctnModelList.size());
			List<VctnModel> vctnModelSubList = vctnModelList.subList(idx, end);

			StringBuilder queryStr = new StringBuilder("""
					MERGE INTO
						vctn_table target_tbl
					USING (
						""");

			for (int batchIdx = 0; batchIdx < vctnModelSubList.size(); batchIdx++) {
				queryStr.append("SELECT \r\n");
				queryStr.append(":vctnUserId").append(batchIdx).append(" AS vctn_user_id, \r\n");
				queryStr.append(":vctnSeCd").append(batchIdx).append(" AS vctn_se_cd, \r\n");
				queryStr.append(":vctnBgngYmd").append(batchIdx).append(" AS vctn_bgng_ymd, \r\n");
				queryStr.append(":vctnEndYmd").append(batchIdx).append(" AS vctn_end_ymd, \r\n");
				queryStr.append(":vctnUseCnt").append(batchIdx).append(" AS vctn_use_cnt, \r\n");
				queryStr.append(":atrzDocNo").append(batchIdx).append(" AS atrz_doc_no, \r\n");
				queryStr.append(":useYn").append(batchIdx).append(" AS use_yn, \r\n");
				queryStr.append("(:regDt").append(batchIdx).append(")::timestamp").append(" AS reg_dt, \r\n");
				queryStr.append(":rgtrId").append(batchIdx).append(" AS rgtr_id, \r\n");
				queryStr.append("(:mdfcnDt").append(batchIdx).append(")::timestamp").append(" AS mdfcn_dt, \r\n");
				queryStr.append(":mdfrId").append(batchIdx).append(" AS mdfr_id \r\n");
				if (batchIdx < vctnModelSubList.size() - 1) {
					queryStr.append("UNION ALL \r\n");
				}
			}
			queryStr.append("""
					) source_tbl
					ON (
						target_tbl.vctn_user_id = source_tbl.vctn_user_id
						AND target_tbl.vctn_se_cd = source_tbl.vctn_se_cd
						AND target_tbl.vctn_bgng_ymd = source_tbl.vctn_bgng_ymd
					)
					WHEN MATCHED THEN
					UPDATE SET
						vctn_end_ymd = source_tbl.vctn_end_ymd,
						vctn_use_cnt = source_tbl.vctn_use_cnt,
						atrz_doc_no = source_tbl.atrz_doc_no,
						mdfcn_dt = source_tbl.mdfcn_dt,
						mdfr_id = source_tbl.mdfr_id
					WHEN NOT MATCHED THEN
					INSERT (
						vctn_user_id,
						vctn_se_cd,
						vctn_bgng_ymd,
						vctn_end_ymd,
						vctn_use_cnt,
						atrz_doc_no,
						use_yn,
						reg_dt,
						rgtr_id,
						mdfcn_dt,
						mdfr_id
					) VALUES (
						source_tbl.vctn_user_id,
						source_tbl.vctn_se_cd,
						source_tbl.vctn_bgng_ymd,
						source_tbl.vctn_end_ymd,
						source_tbl.vctn_use_cnt,
						source_tbl.atrz_doc_no,
						source_tbl.use_yn,
						source_tbl.reg_dt,
						source_tbl.rgtr_id,
						source_tbl.mdfcn_dt,
						source_tbl.mdfr_id
					)
					""");

			Query query = entityManager
					.createNativeQuery(queryStr.toString().stripIndent().replace("\t", " ").replace("\n", " "));

			for (int batchIdx = 0; batchIdx < vctnModelSubList.size(); batchIdx++) {
				VctnModel vctnModel = vctnModelSubList.get(batchIdx);
				query.setParameter("vctnUserId" + batchIdx, vctnModel.getVctnUserId())
						.setParameter("vctnSeCd" + batchIdx, vctnModel.getVctnSeCd())
						.setParameter("vctnBgngYmd" + batchIdx, vctnModel.getVctnBgngYmd())
						.setParameter("vctnEndYmd" + batchIdx, vctnModel.getVctnEndYmd())
						.setParameter("vctnUseCnt" + batchIdx, vctnModel.getVctnUseCnt())
						.setParameter("atrzDocNo" + batchIdx, vctnModel.getAtrzDocNo())
						.setParameter("useYn" + batchIdx, vctnModel.getUseYn())
						.setParameter("regDt" + batchIdx, vctnModel.getRegDt())
						.setParameter("rgtrId" + batchIdx, vctnModel.getRgtrId())
						.setParameter("mdfcnDt" + batchIdx, vctnModel.getMdfcnDt())
						.setParameter("mdfrId" + batchIdx, vctnModel.getMdfrId());
			}
			updatedRows += query.executeUpdate();

			entityManager.flush();
			entityManager.clear();
		}

		return updatedRows;
	}

	/***
	 * 휴가 삭제
	 */
	@Override
	public int deleteVctn(VctnModel vctnModel) throws Exception {
		String queryStr = """
				UPDATE
					vctn_table
				SET
					useYn = 'N',
					mdfcnDt = :mdfcnDt,
					mdfrId = :mdfrId
				WHERE
					useYn = 'Y'
					AND vctnUserId = :vctnUserId
					AND vctnSeCd = :vctnSeCd
					AND vctnBgngYmd = :vctnBgngYmd
				""";
		Query query = entityManager.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "))
				.setParameter("vctnUserId", vctnModel.getVctnUserId()).setParameter("vctnSeCd", vctnModel.getVctnSeCd())
				.setParameter("vctnBgngYmd", vctnModel.getVctnBgngYmd()).setParameter("mdfcnDt", vctnModel.getMdfcnDt())
				.setParameter("mdfrId", vctnModel.getMdfrId());

		return query.executeUpdate();
	}

	/***
	 * 휴가 정보 목록 조회
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Page<VctnModel> inqVctnInfo(VctnParamDTO vctnParamDTO, Pageable pageable) throws Exception {
		StringBuilder whereQuery = new StringBuilder("");
		whereQuery.append(
				!ObjectUtils.isEmpty(vctnParamDTO.getVctnUserId()) ? "AND vctn_user_id = :vctnUserId \r\n" : "");
		whereQuery.append(!ObjectUtils.isEmpty(vctnParamDTO.getVctnBgngYmd())
				? "AND vctn_bgng_ymd LIKE '%' || :vctnBgngYmd || '%' \r\n"
				: "");

		String queryStr = """
				WITH vctn_list AS (
					SELECT
						vctn_user_id,
						vctn_se_cd,
						COALESCE((
							SELECT
								cmmn_cd_id
							FROM
								cmmn_cd_table
							WHERE
								use_yn = 'Y'
								AND (cmmn_cd_clsf_id, cmmn_cd_id) = (
									SELECT
										cmmn_cd_up_clsf_id,
										cmmn_cd_up_id
									FROM
										cmmn_cd_table
									WHERE
										use_yn = 'Y'
										AND cmmn_cd_clsf_id = 'SOLTECH_1020'
										AND cmmn_cd_id = vctn_tbl.vctn_se_cd
									)
						), vctn_tbl.vctn_se_cd) AS vctn_se_up_cd,
						SUM(CASE
								WHEN atrz_doc_no IS NULL OR TRIM(atrz_doc_no) = ''
								THEN vctn_use_cnt
								WHEN atrz_doc_no IS NOT NULL AND TRIM(atrz_doc_no) != ''
									AND (
										SELECT
											atrz_stts_se_cd
										FROM
											atrz_table
										WHERE
											use_yn = 'Y'
											AND doc_no = atrz_doc_no
										) = '00020' /*** (CD) 결재 */
								THEN vctn_use_cnt
								ELSE 0
							END
						) AS vctn_use_cnt,
						(
							SELECT
								cmmn_cd_user_dfn_vl1
							FROM
								cmmn_cd_table
							WHERE
								use_yn = 'Y'
								AND cmmn_cd_clsf_id = 'SOLTECH_1020'
								AND cmmn_cd_id = vctn_tbl.vctn_se_cd
						) AS is_count /*** 잔여 합계 제외 */
					FROM
						vctn_table vctn_tbl
					WHERE
						use_yn = 'Y'
					""" + whereQuery.toString() + """
					GROUP BY
						vctn_user_id,
						vctn_se_cd
				)
				SELECT
					vctn_user_id AS vctn_user_id,
					(
						SELECT
							user_nm
						FROM
							user_table
						WHERE
							user_table.use_yn = 'Y'
							AND user_id = vctn_user_id
					) AS vctn_user_nm,
					cmmn_cd_id AS vctn_se_cd,
					cmmn_cd_nm AS vctn_se_nm,
					COALESCE(vctn_use_cnt, 0) AS vctn_use_cnt
				FROM (
					SELECT
						cmmn_cd_id,
						cmmn_cd_nm
					FROM
						cmmn_cd_table
					WHERE
						use_yn = 'Y'
						AND cmmn_cd_clsf_id = 'SOLTECH_1020'
				) base
				LEFT JOIN (
					SELECT
						vctn_user_id,
						vctn_se_cd,
						vctn_use_cnt
					FROM
						vctn_list
					UNION
					SELECT
						vctn_user_id,
						vctn_se_up_cd,
						SUM(vctn_use_cnt) AS vctn_use_cnt
					FROM
						vctn_list
					WHERE
						vctn_se_up_cd != '00010' /*** 휴가 */
					GROUP BY
						vctn_user_id,
						vctn_se_up_cd
					UNION
					SELECT
						vctn_user_id,
						'00010' AS vctn_se_up_cd, /*** 휴가 */
						SUM(vctn_use_cnt) AS vctn_use_cnt
					FROM
						vctn_list
					WHERE
						is_count = '1'
						AND vctn_se_cd != '00000' /*** 지급 */
					GROUP BY
						vctn_user_id
					UNION
					SELECT
						vctn_user_id,
						'00002', /*** 잔여 */
						SUM(vctn_use_cnt) AS vctn_use_cnt
					FROM
						vctn_list
					WHERE
						is_count = '1'
					GROUP BY
						vctn_user_id
				) vctn_tbl
				ON
					base.cmmn_cd_id = vctn_tbl.vctn_se_cd
				ORDER BY
					vctn_user_id,
					cmmn_cd_id
				""";

		Query query = entityManager.createNativeQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "),
				Map.class);

		String countQueryStr = """
				WITH vctn_list AS (
				SELECT
					vctn_user_id,
					vctn_se_cd,
					COALESCE((
						SELECT
							cmmn_cd_id
						FROM
							cmmn_cd_table
						WHERE
							use_yn = 'Y'
							AND (cmmn_cd_clsf_id, cmmn_cd_id) = (
								SELECT
									cmmn_cd_up_clsf_id,
									cmmn_cd_up_id
								FROM
									cmmn_cd_table
								WHERE
									use_yn = 'Y'
									AND cmmn_cd_clsf_id = 'SOLTECH_1020'
									AND cmmn_cd_id = vctn_tbl.vctn_se_cd
								)
					), vctn_tbl.vctn_se_cd) AS vctn_se_up_cd,
					SUM(CASE
							WHEN atrz_doc_no IS NULL OR TRIM(atrz_doc_no) = ''
							THEN vctn_use_cnt
							WHEN atrz_doc_no IS NOT NULL AND TRIM(atrz_doc_no) != ''
								AND (
									SELECT
										atrz_stts_se_cd
									FROM
										atrz_table
									WHERE
										use_yn = 'Y'
										AND doc_no = atrz_doc_no
									) = '00020' /*** (CD) 결재 */
							THEN vctn_use_cnt
							ELSE 0
						END
					) AS vctn_use_cnt,
					(
						SELECT
							cmmn_cd_user_dfn_vl1
						FROM
							cmmn_cd_table
						WHERE
							use_yn = 'Y'
							AND cmmn_cd_clsf_id = 'SOLTECH_1020'
							AND cmmn_cd_id = vctn_tbl.vctn_se_cd
					) AS is_count /*** 잔여 합계 제외 */
				FROM
					vctn_table vctn_tbl
				WHERE
					use_yn = 'Y'
				""" + whereQuery.toString() + """
					GROUP BY
						vctn_user_id,
						vctn_se_cd
				)
				SELECT
					COUNT(1)
				FROM (
					SELECT
						vctn_user_id AS vctn_user_id,
						cmmn_cd_id AS vctn_se_cd
					FROM (
						SELECT
							cmmn_cd_id,
							cmmn_cd_nm
						FROM
							cmmn_cd_table
						WHERE
							use_yn = 'Y'
							AND cmmn_cd_clsf_id = 'SOLTECH_1020'
					) base
					LEFT JOIN (
						SELECT
							vctn_user_id,
							vctn_se_cd,
							vctn_use_cnt
						FROM
							vctn_list
						UNION
						SELECT
							vctn_user_id,
							vctn_se_up_cd,
							SUM(vctn_use_cnt) AS vctn_use_cnt
						FROM
							vctn_list
						WHERE
							vctn_se_up_cd != '00010' /*** 휴가 */
						GROUP BY
							vctn_user_id,
							vctn_se_up_cd
						UNION
						SELECT
							vctn_user_id,
							'00010' AS vctn_se_up_cd, /*** 휴가 */
							SUM(vctn_use_cnt) AS vctn_use_cnt
						FROM
							vctn_list
						WHERE
							is_count = '1'
							AND vctn_se_cd != '00000' /*** 지급 */
						GROUP BY
							vctn_user_id
						UNION
						SELECT
							vctn_user_id,
							'00002', /*** 잔여 */
							SUM(vctn_use_cnt) AS vctn_use_cnt
						FROM
							vctn_list
						WHERE
							is_count = '1'
						GROUP BY
							vctn_user_id
					) vctn_tbl
					ON
						base.cmmn_cd_id = vctn_tbl.vctn_se_cd
				)
				""";
		Query countQuery = entityManager
				.createNativeQuery(countQueryStr.stripIndent().replace("\t", " ").replace("\n", " "), Long.class);

		if (!ObjectUtils.isEmpty(vctnParamDTO.getVctnUserId())) {
			query.setParameter("vctnUserId", vctnParamDTO.getVctnUserId());
			countQuery.setParameter("vctnUserId", vctnParamDTO.getVctnUserId());
		}
		if (!ObjectUtils.isEmpty(vctnParamDTO.getVctnBgngYmd())) {
			query.setParameter("vctnBgngYmd", vctnParamDTO.getVctnBgngYmd());
			countQuery.setParameter("vctnBgngYmd", vctnParamDTO.getVctnBgngYmd());
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
			countQueryResult = customCntRepository.getVctnInfoCnt(countQuery, vctnParamDTO);
		} else {
			countQueryResult = queryResult.size();
			pageable = PageRequest.of(0, queryResult.size() == 0 ? 1 : queryResult.size());
		}

		return new PageImpl<VctnModel>(objectMapper.convertValue(queryResult, new TypeReference<List<VctnModel>>() {
		}), pageable, countQueryResult);
	}
}