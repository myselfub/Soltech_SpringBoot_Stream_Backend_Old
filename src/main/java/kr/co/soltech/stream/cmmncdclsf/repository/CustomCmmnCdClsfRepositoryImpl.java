package kr.co.soltech.stream.cmmncdclsf.repository;

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
import kr.co.soltech.stream.cmmncd.model.CmmnCdParamDTO;
import kr.co.soltech.stream.cmmncdclsf.model.CmmnCdClsfModel;
import kr.co.soltech.stream.commons.repository.CustomCntRepository;
import lombok.RequiredArgsConstructor;

/***
 * 공통코드 커스텀 레파지토리 클래스
 */
@RequiredArgsConstructor
class CustomCmmnCdClsfRepositoryImpl implements CustomCmmnCdClsfRepository {
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
	 * 공통코드 분류 목록 조회
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Page<CmmnCdClsfModel> inqCmmnCdClsf(CmmnCdParamDTO cmmnCdParamDTO, Pageable pageable) throws Exception {
		StringBuilder whereQuery = new StringBuilder("");
		whereQuery.append(!ObjectUtils.isEmpty(cmmnCdParamDTO.getCmmnCdClsfId())
				? "AND cmmnCdClsfId LIKE '%' || :cmmnCdClsfId || '%' \r\n"
				: "");
		whereQuery.append(!ObjectUtils.isEmpty(cmmnCdParamDTO.getCmmnCdClsfNm())
				? "AND cmmnCdClsfNm LIKE '%' || :cmmnCdClsfNm || '%' \r\n"
				: "");

		String queryStr = """
				SELECT
					cmmnCdClsfId AS cmmn_cd_clsf_id,
					cmmnCdClsfNm AS cmmn_cd_clsf_nm,
					cmmnCdClsfExpln AS cmmn_cd_clsf_expln,
					useYn AS use_yn,
					regDt AS reg_dt,
					rgtrId AS rgtr_id,
					mdfcnDt AS mdfcn_dt,
					mdfrId AS mdfr_id
				FROM
					cmmn_cd_clsf_table cmmn_cd_clsf_tbl
				WHERE
					useYn = 'Y'""" + whereQuery.toString() + """
				ORDER BY
					cmmnCdClsfId
				""";

		TypedQuery<Map> typedQuery = entityManager
				.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "), Map.class);

		String countQueryStr = """
				SELECT
					COUNT(1)
				FROM
					cmmn_cd_clsf_table cmmn_cd_clsf_tbl
				WHERE
					useYn = 'Y'
				""" + whereQuery.toString();
		TypedQuery<Long> countTypedQuery = entityManager.createQuery(countQueryStr, Long.class);

		if (!ObjectUtils.isEmpty(cmmnCdParamDTO.getCmmnCdClsfId())) {
			typedQuery.setParameter("cmmnCdClsfId", cmmnCdParamDTO.getCmmnCdClsfId());
			countTypedQuery.setParameter("cmmnCdClsfId", cmmnCdParamDTO.getCmmnCdClsfId());
		}
		if (!ObjectUtils.isEmpty(cmmnCdParamDTO.getCmmnCdClsfNm())) {
			typedQuery.setParameter("cmmnCdClsfNm", cmmnCdParamDTO.getCmmnCdClsfNm());
			countTypedQuery.setParameter("cmmnCdClsfNm", cmmnCdParamDTO.getCmmnCdClsfNm());
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
			countQueryResult = customCntRepository.getCmmnCdClsfCnt(countTypedQuery, cmmnCdParamDTO);
		} else {
			countQueryResult = queryResult.size();
			pageable = PageRequest.of(0, queryResult.size() == 0 ? 1 : queryResult.size());
		}

		return new PageImpl<CmmnCdClsfModel>(
				objectMapper.convertValue(queryResult, new TypeReference<List<CmmnCdClsfModel>>() {
				}), pageable, countQueryResult);
	}

	/***
	 * 공통코드 분류 상세 조회
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public CmmnCdClsfModel getCmmnCdClsf(CmmnCdParamDTO cmmnCdParamDTO) throws Exception {
		String queryStr = """
				SELECT
					cmmnCdClsfId AS cmmn_cd_clsf_id,
					cmmnCdClsfNm AS cmmn_cd_clsf_nm,
					cmmnCdClsfExpln AS cmmn_cd_clsf_expln,
					useYn AS use_yn,
					regDt AS reg_dt,
					rgtrId AS rgtr_id,
					mdfcnDt AS mdfcn_dt,
					mdfrId AS mdfr_id
				FROM
					cmmn_cd_clsf_table cmmn_cd_clsf_tbl
				WHERE
					useYn = 'Y'
					AND cmmnCdClsfId = :cmmnCdClsfId
				""";
		TypedQuery<Map> typedQuery = entityManager
				.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "), Map.class)
				.setParameter("cmmnCdClsfId", cmmnCdParamDTO.getCmmnCdClsfId());

		Map queryResult = typedQuery.getResultList().stream().findFirst().orElse(null);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		return objectMapper.convertValue(queryResult, new TypeReference<CmmnCdClsfModel>() {
		});
	}

	/***
	 * 공통코드 분류 등록/수정
	 */
	@Override
	public int upsertCmmnCdClsf(CmmnCdClsfModel cmmnCdClsfModel) throws Exception {
		String queryStr = """
				MERGE INTO
					cmmn_cd_clsf_table target_tbl
				USING (
					SELECT
						:cmmnCdClsfId AS cmmn_cd_clsf_id,
						:cmmnCdClsfNm AS cmmn_cd_clsf_nm,
						:cmmnCdClsfExpln AS cmmn_cd_clsf_expln,
						:cmmnCdClsfUserDfnVl1 AS cmmn_cd_clsf_user_dfn_vl1,
						:useYn AS use_yn,
						(:regDt)::timestamp AS reg_dt,
						:rgtrId AS rgtr_id,
						(:mdfcnDt)::timestamp AS mdfcn_dt,
						:mdfrId AS mdfr_id
				) source_tbl
				ON (
					target_tbl.cmmn_cd_clsf_id = source_tbl.cmmn_cd_clsf_id
				)
				WHEN MATCHED THEN
				UPDATE SET
					cmmn_cd_clsf_nm = source_tbl.cmmn_cd_clsf_nm,
					cmmn_cd_clsf_expln = source_tbl.cmmn_cd_clsf_expln,
					cmmn_cd_clsf_user_dfn_vl1 = source_tbl.cmmn_cd_clsf_user_dfn_vl1,
					mdfcn_dt = source_tbl.mdfcn_dt,
					mdfr_id = source_tbl.mdfr_id
				WHEN NOT MATCHED THEN
				INSERT (
					cmmn_cd_clsf_id,
					cmmn_cd_clsf_nm,
					cmmn_cd_clsf_expln,
					cmmn_cd_clsf_user_dfn_vl1,
					use_yn,
					reg_dt,
					rgtr_id,
					mdfcn_dt,
					mdfr_id
				) VALUES (
					source_tbl.cmmn_cd_clsf_id,
					source_tbl.cmmn_cd_clsf_nm,
					source_tbl.cmmn_cd_clsf_expln,
					source_tbl.cmmn_cd_clsf_user_dfn_vl1,
					source_tbl.use_yn,
					source_tbl.reg_dt,
					source_tbl.rgtr_id,
					source_tbl.mdfcn_dt,
					source_tbl.mdfr_id
				)
				""";

		Query query = entityManager.createNativeQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "))
				.setParameter("cmmnCdClsfId", cmmnCdClsfModel.getCmmnCdClsfId())
				.setParameter("cmmnCdClsfNm", cmmnCdClsfModel.getCmmnCdClsfNm())
				.setParameter("cmmnCdClsfExpln", cmmnCdClsfModel.getCmmnCdClsfExpln())
				.setParameter("cmmnCdClsfUserDfnVl1", cmmnCdClsfModel.getCmmnCdClsfUserDfnVl1())
				.setParameter("useYn", cmmnCdClsfModel.getUseYn()).setParameter("regDt", cmmnCdClsfModel.getRegDt())
				.setParameter("rgtrId", cmmnCdClsfModel.getRgtrId())
				.setParameter("mdfcnDt", cmmnCdClsfModel.getMdfcnDt())
				.setParameter("mdfrId", cmmnCdClsfModel.getMdfrId());

		return query.executeUpdate();
	}

	/***
	 * 공통코드 분류 삭제
	 */
	@Override
	public int deleteCmmnCdClsf(CmmnCdClsfModel cmmnCdClsfModel) throws Exception {
		String queryStr = """
				UPDATE
					cmmn_cd_cslf_table
				SET
					useYn = 'N',
					mdfcnDt = :mdfcnDt,
					mdfrId = :mdfrId
				WHERE
					useYn = 'Y'
					AND cmmnCdClsfId = :cmmnCdClsfId
				""";
		Query query = entityManager.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "))
				.setParameter("cmmnCdClsfId", cmmnCdClsfModel.getCmmnCdClsfId())
				.setParameter("mdfcnDt", cmmnCdClsfModel.getMdfcnDt())
				.setParameter("mdfrId", cmmnCdClsfModel.getMdfrId());

		return query.executeUpdate();
	}
}