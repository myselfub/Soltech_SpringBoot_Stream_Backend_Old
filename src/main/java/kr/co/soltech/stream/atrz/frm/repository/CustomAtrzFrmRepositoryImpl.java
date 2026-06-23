package kr.co.soltech.stream.atrz.frm.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import kr.co.soltech.stream.atrz.frm.model.AtrzFrmModel;
import kr.co.soltech.stream.atrz.frm.model.AtrzFrmParamDTO;
import kr.co.soltech.stream.commons.repository.CustomCntRepository;
import lombok.RequiredArgsConstructor;

/***
 * 결재 양식 커스텀 레파지토리 클래스
 */
@RequiredArgsConstructor
class CustomAtrzFrmRepositoryImpl implements CustomAtrzFrmRepository {
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
	 * 결재 양식 목록 조회
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Slice<AtrzFrmModel> inqAtrzFrm(AtrzFrmParamDTO atrzFrmParamDTO, Pageable pageable) throws Exception {
		StringBuilder whereQuery = new StringBuilder("");
		whereQuery.append(
				!ObjectUtils.isEmpty(atrzFrmParamDTO.getDocSeCd()) ? "AND docSeCd LIKE '%' || :docSeCd || '%' \r\n"
						: "");

		String queryStr = """
				SELECT
					docSeCd AS doc_se_cd,
					(
						SELECT
							cmmnCdNm
						FROM
							cmmn_cd_table
						WHERE
							useYn = 'Y'
							AND cmmnCdClsfId = 'SOLTECH_1000'
							AND cmmnCdId = atrz_frm_tbl.docSeCd
					) AS doc_se_nm,
					atrzFrmData AS atrz_frm_data,
					useYn AS use_yn,
					regDt AS reg_dt,
					rgtrId AS rgtr_id,
					mdfcnDt AS mdfcn_dt,
					mdfrId AS mdfr_id
				FROM
					atrz_frm_table atrz_frm_tbl
				WHERE
					useYn = 'Y'
					""" + whereQuery.toString() + """
				ORDER BY
					docSeCd
				""" + (!ObjectUtils.isEmpty(pageable) ? "LIMIT :LIMIT OFFSET :OFFSET" : "");
		TypedQuery<Map> typedQuery = entityManager
				.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "), Map.class);

		String countQueryStr = """
				SELECT
					COUNT(1)
				FROM
					atrz_frm_table atrz_frm_tbl
				WHERE
					useYn = 'Y'
				""" + whereQuery.toString();
		TypedQuery<Long> countTypedQuery = entityManager.createQuery(countQueryStr, Long.class);

		if (!ObjectUtils.isEmpty(atrzFrmParamDTO.getDocSeCd())) {
			typedQuery.setParameter("docSeCd", atrzFrmParamDTO.getDocSeCd());
			countTypedQuery.setParameter("docSeCd", atrzFrmParamDTO.getDocSeCd());
		}
		if (!ObjectUtils.isEmpty(pageable)) {
			typedQuery.setParameter("LIMIT", pageable.getPageSize());
			typedQuery.setParameter("OFFSET", pageable.getPageNumber() * pageable.getPageSize());
		}

		List<Map> queryResult = typedQuery.getResultList();
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		boolean hasNext = false;
		if (!ObjectUtils.isEmpty(pageable)) {
			long countQueryResult = customCntRepository.getAtrzFrmCnt(countTypedQuery, atrzFrmParamDTO);
			hasNext = pageable.getPageNumber() * pageable.getPageSize() + pageable.getPageSize() < countQueryResult;
		} else {
			pageable = PageRequest.of(0, queryResult.size() == 0 ? 1 : queryResult.size());
		}

		return new SliceImpl<AtrzFrmModel>(
				objectMapper.convertValue(queryResult, new TypeReference<List<AtrzFrmModel>>() {
				}), pageable, hasNext);
	}

	/***
	 * 결재 양식 상세 조회
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public AtrzFrmModel getAtrzFrm(AtrzFrmParamDTO atrzFrmParamDTO) throws Exception {
		String queryStr = """
				SELECT
					docSeCd AS doc_se_cd,
					(
						SELECT
							cmmnCdNm
						FROM
							cmmn_cd_table
						WHERE
							useYn = 'Y'
							AND cmmnCdClsfId = 'SOLTECH_1000'
							AND cmmnCdId = atrz_frm_tbl.docSeCd
					) AS doc_se_nm,
					atrzFrmData AS atrz_frm_data,
					useYn AS use_yn,
					regDt AS reg_dt,
					rgtrId AS rgtr_id,
					mdfcnDt AS mdfcn_dt,
					mdfrId AS mdfr_id
				FROM
					atrz_frm_table atrz_frm_tbl
				WHERE
					useYn = 'Y'
					AND docSeCd = :docSeCd
				ORDER BY
					docSeCd
				""";
		TypedQuery<Map> typedQuery = entityManager
				.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "), Map.class)
				.setParameter("docSeCd", atrzFrmParamDTO.getDocSeCd());

		Map queryResult = typedQuery.getResultList().stream().findFirst().orElse(null);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		return objectMapper.convertValue(queryResult, new TypeReference<AtrzFrmModel>() {
		});
	}

	/***
	 * 결재 양식 등록/수정
	 */
	@Override
	public int upsertAtrzFrm(AtrzFrmModel atrzFrmModel) throws Exception {
		String queryStr = """
				MERGE INTO
					atrz_frm_table target_tbl
				USING (
					SELECT
						:docSeCd AS doc_se_cd,
						(:atrzFrmData)::jsonb AS atrz_frm_data,
						:useYn AS use_yn,
						(:regDt)::timestamp AS reg_dt,
						:rgtrId AS rgtr_id,
						(:mdfcnDt)::timestamp AS mdfcn_dt,
						:mdfrId AS mdfr_id
				) source_tbl
				ON (
					target_tbl.doc_se_cd = source_tbl.doc_se_cd
				)
				WHEN MATCHED THEN
				UPDATE SET
					atrz_frm_data = source_tbl.atrz_frm_data
				WHEN NOT MATCHED THEN
				INSERT (
					doc_se_cd,
					atrz_frm_data,
					use_yn,
					reg_dt,
					rgtr_id,
					mdfcn_dt,
					mdfr_id
				) VALUES (
					source_tbl.doc_se_cd,
					source_tbl.atrz_frm_data,
					source_tbl.use_yn,
					source_tbl.reg_dt,
					source_tbl.rgtr_id,
					source_tbl.mdfcn_dt,
					source_tbl.mdfr_id
				)
				""";

		ObjectMapper objectMapper = new ObjectMapper();
		String atrzFrmData = objectMapper.writeValueAsString(atrzFrmModel.getAtrzFrmData());
		Query query = entityManager.createNativeQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "))
				.setParameter("docSeCd", atrzFrmModel.getDocSeCd()).setParameter("atrzFrmData", atrzFrmData)
				.setParameter("useYn", atrzFrmModel.getUseYn()).setParameter("regDt", atrzFrmModel.getRegDt())
				.setParameter("rgtrId", atrzFrmModel.getRgtrId()).setParameter("mdfcnDt", atrzFrmModel.getMdfcnDt())
				.setParameter("mdfrId", atrzFrmModel.getMdfrId());

		return query.executeUpdate();
	}

	/***
	 * 결재 양식 삭제
	 */
	@Override
	public int deleteAtrzFrm(AtrzFrmModel atrzFrmModel) throws Exception {
		String queryStr = """
				UPDATE
					atrz_frm_table
				SET
					useYn = 'N',
					mdfcnDt = :mdfcnDt,
					mdfrId = :mdfrId
				WHERE
					useYn = 'Y'
					AND docSeCd = :docSeCd
				""";
		Query query = entityManager.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "))
				.setParameter("docSeCd", atrzFrmModel.getDocSeCd()).setParameter("mdfcnDt", atrzFrmModel.getMdfcnDt())
				.setParameter("mdfrId", atrzFrmModel.getMdfrId());

		return query.executeUpdate();
	}
}