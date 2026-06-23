package kr.co.soltech.stream.atrz.opnn.repository;

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
import kr.co.soltech.stream.atrz.opnn.model.AtrzOpnnModel;
import kr.co.soltech.stream.atrz.opnn.model.AtrzOpnnParamDTO;
import kr.co.soltech.stream.commons.repository.CustomCntRepository;
import lombok.RequiredArgsConstructor;

/***
 * 결재 의견 커스텀 레파지토리 클래스
 */
@RequiredArgsConstructor
class CustomAtrzOpnnRepositoryImpl implements CustomAtrzOpnnRepository {
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
	 * 결재 의견 목록 조회
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Page<AtrzOpnnModel> inqAtrzOpnn(AtrzOpnnParamDTO atrzOpnnParamDTO, Pageable pageable) throws Exception {
		StringBuilder whereQuery = new StringBuilder("");
		whereQuery.append(!ObjectUtils.isEmpty(atrzOpnnParamDTO.getDocNo()) ? "AND docNo = :docNo \r\n" : "");
		whereQuery.append(!ObjectUtils.isEmpty(atrzOpnnParamDTO.getRgtrId()) ? "AND rgtrId = :rgtrId \r\n" : "");
		whereQuery.append(!ObjectUtils.isEmpty(atrzOpnnParamDTO.getRegYmd())
				? "AND TO_CHAR(regDt, 'YYYYMMDD') LIKE '%' || :regYmd || '%' \r\n"
				: "");

		String queryStr = """
				SELECT
					opnnId AS opnn_id,
					opnnUpId AS opnn_up_id,
					docNo AS doc_no,
					opnnCn AS opnn_cn,
					(
						SELECT
							userNm
						FROM
							user_table
						WHERE
							useYn = 'Y'
							AND userId = atrz_opnn_tbl.rgtrId
					) AS rgtr_nm,
					useYn AS use_yn,
					regDt AS reg_dt,
					rgtrId AS rgtr_id,
					mdfcnDt AS mdfcn_dt,
					mdfrId AS mdfr_id
				FROM
					atrz_opnn_table atrz_opnn_tbl
				WHERE
					useYn = 'Y'
					""" + whereQuery.toString() + """
				ORDER BY
					docNo DESC,
					opnnId DESC
				""";
		TypedQuery<Map> typedQuery = entityManager
				.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "), Map.class);

		String countQueryStr = """
				SELECT
					COUNT(1)
				FROM
					atrz_opnn_table atrz_opnn_tbl
				WHERE
					useYn = 'Y'
				""" + whereQuery.toString();
		TypedQuery<Long> countTypedQuery = entityManager.createQuery(countQueryStr, Long.class);

		if (!ObjectUtils.isEmpty(atrzOpnnParamDTO.getDocNo())) {
			typedQuery.setParameter("docNo", atrzOpnnParamDTO.getDocNo());
			countTypedQuery.setParameter("docNo", atrzOpnnParamDTO.getDocNo());
		}
		if (!ObjectUtils.isEmpty(atrzOpnnParamDTO.getRgtrId())) {
			typedQuery.setParameter("rgtrId", atrzOpnnParamDTO.getRgtrId());
			countTypedQuery.setParameter("rgtrId", atrzOpnnParamDTO.getRgtrId());
		}
		if (!ObjectUtils.isEmpty(atrzOpnnParamDTO.getRegYmd())) {
			typedQuery.setParameter("regYmd", atrzOpnnParamDTO.getRegYmd());
			countTypedQuery.setParameter("regYmd", atrzOpnnParamDTO.getRegYmd());
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
			countQueryResult = customCntRepository.getAtrzOpnnCnt(countTypedQuery, atrzOpnnParamDTO);
		} else {
			countQueryResult = queryResult.size();
			pageable = PageRequest.of(0, queryResult.size() == 0 ? 1 : queryResult.size() == 0 ? 1 : queryResult.size());
		}

		return new PageImpl<AtrzOpnnModel>(
				objectMapper.convertValue(queryResult, new TypeReference<List<AtrzOpnnModel>>() {
				}), pageable, countQueryResult);
	}

	/***
	 * 결재 의견 상세 조회
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public AtrzOpnnModel getAtrzOpnn(AtrzOpnnParamDTO atrzOpnnParamDTO) throws Exception {
		String queryStr = """
				SELECT
					opnnId AS opnn_id,
					opnnUpId AS opnn_up_id,
					docNo AS doc_no,
					opnnCn AS opnn_cn,
					(
						SELECT
							userNm
						FROM
							user_table
						WHERE
							useYn = 'Y'
							AND userId = atrz_opnn_tbl.rgtrId
					) AS rgtr_nm,
					useYn AS use_yn,
					regDt AS reg_dt,
					rgtrId AS rgtr_id,
					mdfcnDt AS mdfcn_dt,
					mdfrId AS mdfr_id
				FROM
					atrz_opnn_table atrz_opnn_tbl
				WHERE
					useYn = 'Y'
					AND opnnId = :opnnId
					AND rgtrId = :rgtrId
				ORDER BY
					docNo DESC,
					opnnId DESC
				""";
		TypedQuery<Map> typedQuery = entityManager
				.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "), Map.class)
				.setParameter("opnnId", atrzOpnnParamDTO.getOpnnId())
				.setParameter("rgtrId", atrzOpnnParamDTO.getRgtrId());

		Map queryResult = typedQuery.getResultList().stream().findFirst().orElse(null);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		return objectMapper.convertValue(queryResult, new TypeReference<AtrzOpnnModel>() {
		});
	}

	/***
	 * 결재 의견 등록/수정
	 */
	@Override
	public int upsertAtrzOpnn(AtrzOpnnModel atrzOpnnModel) throws Exception {
		String queryStr = """
				MERGE INTO
					atrz_opnn_table target_tbl
				USING (
					SELECT
						:opnnId AS opnn_id,
						:opnnUpId AS opnn_up_id,
						:docNo AS doc_no,
						:opnnCn AS opnn_cn,
						:useYn AS use_yn,
						(:regDt)::timestamp AS reg_dt,
						:rgtrId AS rgtr_id,
						(:mdfcnDt)::timestamp AS mdfcn_dt,
						:mdfrId AS mdfr_id
				) source_tbl
				ON (
					target_tbl.opnn_id = source_tbl.opnn_id
				)
				WHEN MATCHED AND target_tbl.rgtr_id = source_tbl.rgtr_id THEN
				UPDATE SET
					doc_no = source_tbl.doc_no,
					opnn_cn = source_tbl.opnn_cn,
					mdfcn_dt = source_tbl.mdfcn_dt,
					mdfr_id = source_tbl.mdfr_id
				WHEN NOT MATCHED AND rgtr_id IS NOT NULL THEN
				INSERT (
					opnn_id,
					opnn_up_id,
					doc_no,
					opnn_cn,
					use_yn,
					reg_dt,
					rgtr_id,
					mdfcn_dt,
					mdfr_id
				) VALUES (
					source_tbl.opnn_id,
					source_tbl.opnn_up_id,
					source_tbl.doc_no,
					source_tbl.opnn_cn,
					source_tbl.use_yn,
					source_tbl.reg_dt,
					source_tbl.rgtr_id,
					source_tbl.mdfcn_dt,
					source_tbl.mdfr_id
				)
				""";

		Query query = entityManager.createNativeQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "))
				.setParameter("opnnId", atrzOpnnModel.getOpnnId()).setParameter("opnnUpId", atrzOpnnModel.getOpnnUpId())
				.setParameter("docNo", atrzOpnnModel.getDocNo()).setParameter("opnnCn", atrzOpnnModel.getOpnnCn())
				.setParameter("useYn", atrzOpnnModel.getUseYn()).setParameter("regDt", atrzOpnnModel.getRegDt())
				.setParameter("rgtrId", atrzOpnnModel.getRgtrId()).setParameter("mdfcnDt", atrzOpnnModel.getMdfcnDt())
				.setParameter("mdfrId", atrzOpnnModel.getMdfrId());

		return query.executeUpdate();
	}

	/***
	 * 결재 의견 삭제
	 */
	@Override
	public int deleteAtrzOpnn(AtrzOpnnModel atrzOpnnModel) throws Exception {
		String queryStr = """
				UPDATE
					atrz_opnn_table
				SET
					useYn = 'N',
					mdfcnDt = :mdfcnDt,
					mdfrId = :mdfrId
				WHERE
					useYn = 'Y'
					AND opnnId = :opnnId
					AND rgtrId = :rgtrId
				""";
		Query query = entityManager.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "))
				.setParameter("opnnId", atrzOpnnModel.getOpnnId()).setParameter("rgtrId", atrzOpnnModel.getRgtrId())
				.setParameter("mdfcnDt", atrzOpnnModel.getMdfcnDt()).setParameter("mdfrId", atrzOpnnModel.getMdfrId());

		return query.executeUpdate();
	}
}