package kr.co.soltech.stream.msg.repository;

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
import kr.co.soltech.stream.msg.model.MsgModel;
import kr.co.soltech.stream.msg.model.MsgModelId;
import kr.co.soltech.stream.msg.model.MsgParamDTO;
import lombok.RequiredArgsConstructor;

/***
 * 메세지 커스텀 레파지토리 클래스
 */
@RequiredArgsConstructor
class CustomMsgRepositoryImpl implements CustomMsgRepository {
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
	 * 메세지 목록 조회
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Page<MsgModel> inqMsg(MsgParamDTO msgParamDTO, Pageable pageable) throws Exception {
		StringBuilder whereQuery = new StringBuilder("");
		whereQuery.append(
				!ObjectUtils.isEmpty(msgParamDTO.getMsgLang()) ? "AND msgLang LIKE '%' || :msgLang || '%' \r\n" : "");
		whereQuery.append(
				!ObjectUtils.isEmpty(msgParamDTO.getMsgClsf()) ? "AND msgClsf LIKE '%' || :msgClsf || '%' \r\n" : "");
		whereQuery
				.append(!ObjectUtils.isEmpty(msgParamDTO.getMsgCd()) ? "AND msgCd LIKE '%' || :msgCd || '%' \r\n" : "");
		whereQuery
				.append(!ObjectUtils.isEmpty(msgParamDTO.getMsgNm()) ? "AND msgNm LIKE '%' || :msgNm || '%' \r\n" : "");

		String queryStr = """
				SELECT
					msgLang AS msg_lang,
					msgClsf AS msg_clsf,
					msgCd AS msg_cd,
					msgNm AS msg_nm,
					msgCn AS msg_cn,
					msgUserDfnVl1 AS msg_user_dfn_vl1,
					msgUserDfnVl2 AS msg_user_dfn_vl2,
					msgUserDfnVl3 AS msg_user_dfn_vl3,
					msgUserDfnVl4 AS msg_user_dfn_vl4,
					useYn AS use_yn,
					regDt AS reg_dt,
					rgtrId AS rgtr_id,
					mdfcnDt AS mdfcn_dt,
					mdfrId AS mdfr_id
				FROM
					msg_table msg_tbl
				WHERE
					useYn = 'Y'
					""" + whereQuery.toString() + """
				ORDER BY
					msgLang,
					msgClsf,
					msgCd
				""";

		TypedQuery<Map> typedQuery = entityManager
				.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "), Map.class);

		String countQueryStr = """
				SELECT
					COUNT(1)
				FROM
					msg_table msg_tbl
				WHERE
					useYn = 'Y'
				""" + whereQuery.toString();
		TypedQuery<Long> countTypedQuery = entityManager.createQuery(countQueryStr, Long.class);

		if (!ObjectUtils.isEmpty(msgParamDTO.getMsgLang())) {
			typedQuery.setParameter("msgLang", msgParamDTO.getMsgLang());
			countTypedQuery.setParameter("msgLang", msgParamDTO.getMsgLang());
		}
		if (!ObjectUtils.isEmpty(msgParamDTO.getMsgClsf())) {
			typedQuery.setParameter("msgClsf", msgParamDTO.getMsgClsf());
			countTypedQuery.setParameter("msgClsf", msgParamDTO.getMsgClsf());
		}
		if (!ObjectUtils.isEmpty(msgParamDTO.getMsgCd())) {
			typedQuery.setParameter("msgCd", msgParamDTO.getMsgCd());
			countTypedQuery.setParameter("msgCd", msgParamDTO.getMsgCd());
		}
		if (!ObjectUtils.isEmpty(msgParamDTO.getMsgNm())) {
			typedQuery.setParameter("msgNm", msgParamDTO.getMsgNm());
			countTypedQuery.setParameter("msgNm", msgParamDTO.getMsgNm());
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
			countQueryResult = customCntRepository.getMsgCnt(countTypedQuery, msgParamDTO);
		} else {
			countQueryResult = queryResult.size();
			pageable = PageRequest.of(0, queryResult.size() == 0 ? 1 : queryResult.size());
		}

		return new PageImpl<MsgModel>(objectMapper.convertValue(queryResult, new TypeReference<List<MsgModel>>() {
		}), pageable, countQueryResult);
	}

	/***
	 * 메세지 상세 조회
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public MsgModel getMsg(MsgModelId msgModelId) throws Exception {
		String queryStr = """
				SELECT
					msgLang AS msg_lang,
					msgClsf AS msg_clsf,
					msgCd AS msg_cd,
					msgNm AS msg_nm,
					msgCn AS msg_cn,
					msgUserDfnVl1 AS msg_user_dfn_vl1,
					msgUserDfnVl2 AS msg_user_dfn_vl2,
					msgUserDfnVl3 AS msg_user_dfn_vl3,
					msgUserDfnVl4 AS msg_user_dfn_vl4,
					useYn AS use_yn,
					regDt AS reg_dt,
					rgtrId AS rgtr_id,
					mdfcnDt AS mdfcn_dt,
					mdfrId AS mdfr_id
				FROM
					msg_table msg_tbl
				WHERE
					useYn = 'Y'
					AND msgLang = :msgLang
					AND msgClsf = :msgClsf
					AND msgCd = :msgCd
				""";

		TypedQuery<Map> typedQuery = entityManager
				.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "), Map.class)
				.setParameter("msgLang", msgModelId.getMsgLang()).setParameter("msgClsf", msgModelId.getMsgClsf())
				.setParameter("msgCd", msgModelId.getMsgCd());

		Map queryResult = typedQuery.getResultList().stream().findFirst().orElse(null);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		return objectMapper.convertValue(queryResult, new TypeReference<MsgModel>() {
		});
	}

	/***
	 * 메세지 등록/수정
	 */
	@Override
	public int upsertMsg(MsgModel msgModel) throws Exception {
		String queryStr = """
				MERGE INTO
					msg_table target_tbl
				USING (
					SELECT
						:msgLang AS msg_lang,
						:msgClsf AS msg_clsf,
						:msgCd AS msg_cd,
						:msgNm AS msg_nm,
						:msgCn AS msg_cn,
						:msgUserDfnVl1 AS msg_user_dfn_vl1,
						:msgUserDfnVl2 AS msg_user_dfn_vl2,
						:msgUserDfnVl3 AS msg_user_dfn_vl3,
						:msgUserDfnVl4 AS msg_user_dfn_vl4,
						:useYn AS use_yn,
						(:regDt)::timestamp AS reg_dt,
						:rgtrId AS rgtr_id,
						(:mdfcnDt)::timestamp AS mdfcn_dt,
						:mdfrId AS mdfr_id
				) source_tbl
				ON (
					target_tbl.msg_lang = source_tbl.msg_lang
					AND target_tbl.msg_clsf = source_tbl.msg_clsf
					AND target_tbl.msg_cd = source_tbl.msg_cd
				)
				WHEN MATCHED THEN
				UPDATE SET
					msg_nm = source_tbl.msg_nm,
					msg_cn = source_tbl.msg_cn,
					msg_user_dfn_vl1 = source_tbl.msg_user_dfn_vl1,
					msg_user_dfn_vl2 = source_tbl.msg_user_dfn_vl2,
					msg_user_dfn_vl3 = source_tbl.msg_user_dfn_vl3,
					msg_user_dfn_vl4 = source_tbl.msg_user_dfn_vl4,
					mdfcn_dt = source_tbl.mdfcn_dt,
					mdfr_id = source_tbl.mdfr_id
				WHEN NOT MATCHED THEN
				INSERT (
					msg_lang,
					msg_clsf,
					msg_cd,
					msg_nm,
					msg_cn,
					msg_user_dfn_vl1,
					msg_user_dfn_vl2,
					msg_user_dfn_vl3,
					msg_user_dfn_vl4,
					use_yn,
					reg_dt,
					rgtr_id,
					mdfcn_dt,
					mdfr_id
				) VALUES (
					source_tbl.msg_lang,
					source_tbl.msg_clsf,
					source_tbl.msg_cd,
					source_tbl.msg_nm,
					source_tbl.msg_cn,
					source_tbl.msg_user_dfn_vl1,
					source_tbl.msg_user_dfn_vl2,
					source_tbl.msg_user_dfn_vl3,
					source_tbl.msg_user_dfn_vl4,
					source_tbl.use_yn,
					source_tbl.reg_dt,
					source_tbl.rgtr_id,
					source_tbl.mdfcn_dt,
					source_tbl.mdfr_id
				)
				""";

		Query query = entityManager.createNativeQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "))
				.setParameter("msgLang", msgModel.getMsgLang()).setParameter("msgClsf", msgModel.getMsgClsf())
				.setParameter("msgCd", msgModel.getMsgCd()).setParameter("msgNm", msgModel.getMsgNm())
				.setParameter("msgCn", msgModel.getMsgCn()).setParameter("msgUserDfnVl1", msgModel.getMsgUserDfnVl1())
				.setParameter("msgUserDfnVl2", msgModel.getMsgUserDfnVl2())
				.setParameter("msgUserDfnVl3", msgModel.getMsgUserDfnVl3())
				.setParameter("msgUserDfnVl4", msgModel.getMsgUserDfnVl4()).setParameter("useYn", msgModel.getUseYn())
				.setParameter("regDt", msgModel.getRegDt()).setParameter("rgtrId", msgModel.getRgtrId())
				.setParameter("mdfcnDt", msgModel.getMdfcnDt()).setParameter("mdfrId", msgModel.getMdfrId());

		return query.executeUpdate();
	}

	/***
	 * 메세지 삭제
	 */
	@Override
	public int deleteMsg(MsgModel msgModel) throws Exception {
		String queryStr = """
				UPDATE
					msg_table
				SET
					useYn = 'N',
					mdfcnDt = :mdfcnDt,
					mdfrId = :mdfrId
				WHERE
					useYn = 'Y'
					AND msgLang = :msgLang
					AND msgClsf = :msgClsf
					AND msgCd = :msgCd
				""";
		Query query = entityManager.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "))
				.setParameter("msgLang", msgModel.getMsgLang()).setParameter("msgClsf", msgModel.getMsgClsf())
				.setParameter("msgCd", msgModel.getMsgCd()).setParameter("mdfcnDt", msgModel.getMdfcnDt())
				.setParameter("mdfrId", msgModel.getMdfrId());

		return query.executeUpdate();
	}

}