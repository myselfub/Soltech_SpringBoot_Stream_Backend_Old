package kr.co.soltech.stream.hldy.repository;

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
import kr.co.soltech.stream.hldy.model.HldyModel;
import kr.co.soltech.stream.hldy.model.HldyModelId;
import kr.co.soltech.stream.hldy.model.HldyParamDTO;
import lombok.RequiredArgsConstructor;

/***
 * 휴일 커스텀 레파지토리 클래스
 */
@RequiredArgsConstructor
public class CustomHldyRepositoryImpl implements CustomHldyRepository {
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
	 * 휴일 목록 조회
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Page<HldyModel> inqHldy(HldyParamDTO hldyParamDTO, Pageable pageable) throws Exception {
		StringBuilder whereQuery = new StringBuilder("");
		whereQuery.append(
				!ObjectUtils.isEmpty(hldyParamDTO.getHldyYmd()) ? "AND hldyYmd LIKE '%' || :hldyYmd || '%' \r\n" : "");
		whereQuery.append(!ObjectUtils.isEmpty(hldyParamDTO.getHldyYmdSn()) && hldyParamDTO.getHldyYmdSn() > 0
				? "AND hldyYmdSn = :hldyYmdSn \r\n"
				: "");
		whereQuery.append(
				!ObjectUtils.isEmpty(hldyParamDTO.getHldyNm()) ? "AND hldyNm LIKE '%' || :hldyNm || '%' \r\n" : "");

		String queryStr = """
				SELECT
					hldyYmd AS hldy_ymd,
					hldyYmdSn AS hldy_ymd_sn,
					hldyNm AS hldy_nm,
					hldyRmrk AS hldy_rmrk,
					useYn AS use_yn,
					regDt AS reg_dt,
					rgtrId AS rgtr_id,
					mdfcnDt AS mdfcn_dt,
					mdfrId AS mdfr_id
				FROM
					hldy_table hldy_tbl
				WHERE
					useYn = 'Y'
					""" + whereQuery.toString() + """
				ORDER BY
					hldyYmd DESC,
					hldyYmdSn
				""";

		TypedQuery<Map> typedQuery = entityManager
				.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "), Map.class);

		String countQueryStr = """
				SELECT
					COUNT(1)
				FROM
					hldy_table hldy_tbl
				WHERE
					useYn = 'Y'
				""" + whereQuery.toString();
		TypedQuery<Long> countTypedQuery = entityManager.createQuery(countQueryStr, Long.class);

		if (!ObjectUtils.isEmpty(hldyParamDTO.getHldyYmd())) {
			typedQuery.setParameter("hldyYmd", hldyParamDTO.getHldyYmd());
			countTypedQuery.setParameter("hldyYmd", hldyParamDTO.getHldyYmd());
		}
		if (!ObjectUtils.isEmpty(hldyParamDTO.getHldyYmdSn()) && hldyParamDTO.getHldyYmdSn() > 0) {
			typedQuery.setParameter("hldyYmdSn", hldyParamDTO.getHldyYmdSn());
			countTypedQuery.setParameter("hldyYmdSn", hldyParamDTO.getHldyYmdSn());
		}
		if (!ObjectUtils.isEmpty(hldyParamDTO.getHldyNm())) {
			typedQuery.setParameter("hldyNm", hldyParamDTO.getHldyNm());
			countTypedQuery.setParameter("hldyNm", hldyParamDTO.getHldyNm());
		}

		List<Map> queryResult = typedQuery.getResultList();
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		long countQueryResult = 0;
		if (!ObjectUtils.isEmpty(pageable)) {
			countQueryResult = customCntRepository.getHldyCnt(countTypedQuery, hldyParamDTO);
		} else {
			countQueryResult = queryResult.size();
			pageable = PageRequest.of(0, queryResult.size() == 0 ? 1 : queryResult.size());
		}

		return new PageImpl<HldyModel>(objectMapper.convertValue(queryResult, new TypeReference<List<HldyModel>>() {
		}), pageable, countQueryResult);
	}

	/***
	 * 휴일 상세 조회
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public HldyModel getHldy(HldyModelId hldyModelId) throws Exception {
		String queryStr = """
				SELECT
					hldyYmd AS hldy_ymd,
					hldyYmdSn AS hldy_ymd_sn,
					hldyNm AS hldy_nm,
					hldyRmrk AS hldy_rmrk,
					useYn AS use_yn,
					regDt AS reg_dt,
					rgtrId AS rgtr_id,
					mdfcnDt AS mdfcn_dt,
					mdfrId AS mdfr_id
				FROM
					hldy_table hldy_tbl
				WHERE
					useYn = 'Y'
					AND hldyYmd = :hldyYmd
					AND hldyYmdSn = :hldyYmdSn
				""";

		TypedQuery<Map> typedQuery = entityManager
				.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "), Map.class)
				.setParameter("hldyYmd", hldyModelId.getHldyYmd())
				.setParameter("hldyYmdSn", hldyModelId.getHldyYmdSn());

		Map queryResult = typedQuery.getResultList().stream().findFirst().orElse(null);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		return objectMapper.convertValue(queryResult, new TypeReference<HldyModel>() {
		});
	}

	/***
	 * 휴일 등록/수정
	 */
	@Override
	public int upsertHldy(HldyModel hldyModel) throws Exception {
		String queryStr = """
				MERGE INTO
					hldy_table target_tbl
				USING (
					SELECT
						:hldyYmd AS hldy_ymd,
						:hldyYmdSn AS hldy_ymd_sn,
						:hldyNm AS hldy_nm,
						:hldyRmrk AS hldy_rmrk,
						:useYn AS use_yn,
						(:regDt)::timestamp AS reg_dt,
						:rgtrId AS rgtr_id,
						(:mdfcnDt)::timestamp AS mdfcn_dt,
						:mdfrId AS mdfr_id
				) source_tbl
				ON (
					target_tbl.hldy_ymd = source_tbl.hldy_ymd
					AND target_tbl.hldy_ymd_sn = source_tbl.hldy_ymd_sn
				)
				WHEN MATCHED THEN
				UPDATE SET
					hldy_nm = source_tbl.hldy_nm,
					hldy_rmrk = source_tbl.hldy_rmrk,
					mdfcn_dt = source_tbl.mdfcn_dt,
					mdfr_id = source_tbl.mdfr_id
				WHEN NOT MATCHED THEN
				INSERT (
					hldy_ymd,
					hldy_ymd_sn,
					hldy_nm,
					hldy_rmrk,
					use_yn,
					reg_dt,
					rgtr_id,
					mdfcn_dt,
					mdfr_id
				) VALUES (
					source_tbl.hldy_ymd,
					source_tbl.hldy_ymd_sn,
					source_tbl.hldy_nm,
					source_tbl.hldy_rmrk,
					source_tbl.use_yn,
					source_tbl.reg_dt,
					source_tbl.rgtr_id,
					source_tbl.mdfcn_dt,
					source_tbl.mdfr_id
				)
				""";
		Query query = entityManager.createNativeQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "))
				.setParameter("hldyYmd", hldyModel.getHldyYmd()).setParameter("hldyYmdSn", hldyModel.getHldyYmdSn())
				.setParameter("hldyNm", hldyModel.getHldyNm()).setParameter("hldyRmrk", hldyModel.getHldyRmrk())
				.setParameter("useYn", hldyModel.getUseYn()).setParameter("regDt", hldyModel.getRegDt())
				.setParameter("rgtrId", hldyModel.getRgtrId()).setParameter("mdfcnDt", hldyModel.getMdfcnDt())
				.setParameter("mdfrId", hldyModel.getMdfrId());

		return query.executeUpdate();
	}

	/***
	 * 다중 휴일 등록/수정
	 */
	@Override
	public int upsertAllHldy(List<HldyModel> hldyModelList) throws Exception {
		int updatedRows = 0;
		int batchSize = 50;

		for (int idx = 0; idx < hldyModelList.size(); idx += batchSize) {
			int end = Math.min(idx + batchSize, hldyModelList.size());
			List<HldyModel> hldyModelSubList = hldyModelList.subList(idx, end);

			StringBuilder queryStr = new StringBuilder("""
					MERGE INTO
						hldy_table target_tbl
					USING (
						""");

			for (int batchIdx = 0; batchIdx < hldyModelSubList.size(); batchIdx++) {
				queryStr.append("SELECT \r\n");
				queryStr.append(":hldyYmd").append(batchIdx).append(" AS hldy_ymd, \r\n");
				queryStr.append(":hldyYmdSn").append(batchIdx).append(" AS hldy_ymd_sn, \r\n");
				queryStr.append(":hldyNm").append(batchIdx).append(" AS hldy_nm, \r\n");
				queryStr.append(":hldyRmrk").append(batchIdx).append(" AS hldy_rmrk, \r\n");
				queryStr.append(":useYn").append(batchIdx).append(" AS use_yn, \r\n");
				queryStr.append("(:regDt").append(batchIdx).append(")::timestamp").append(" AS reg_dt, \r\n");
				queryStr.append(":rgtrId").append(batchIdx).append(" AS rgtr_id, \r\n");
				queryStr.append("(:mdfcnDt").append(batchIdx).append(")::timestamp").append(" AS mdfcn_dt, \r\n");
				queryStr.append(":mdfrId").append(batchIdx).append(" AS mdfr_id \r\n");
				if (batchIdx < hldyModelSubList.size() - 1) {
					queryStr.append("UNION ALL \r\n");
				}
			}
			queryStr.append("""
					) source_tbl
					ON (
						target_tbl.hldy_ymd = source_tbl.hldy_ymd
						AND target_tbl.hldy_ymd_sn = source_tbl.hldy_ymd_sn
					)
					WHEN MATCHED THEN
					UPDATE SET
						hldy_nm = source_tbl.hldy_nm,
						hldy_rmrk = source_tbl.hldy_rmrk,
						mdfcn_dt = source_tbl.mdfcn_dt,
						mdfr_id = source_tbl.mdfr_id
					WHEN NOT MATCHED THEN
					INSERT (
						hldy_ymd,
						hldy_ymd_sn,
						hldy_nm,
						hldy_rmrk,
						use_yn,
						reg_dt,
						rgtr_id,
						mdfcn_dt,
						mdfr_id
					) VALUES (
						source_tbl.hldy_ymd,
						source_tbl.hldy_ymd_sn,
						source_tbl.hldy_nm,
						source_tbl.hldy_rmrk,
						source_tbl.use_yn,
						source_tbl.reg_dt,
						source_tbl.rgtr_id,
						source_tbl.mdfcn_dt,
						source_tbl.mdfr_id
					)
					""");

			Query query = entityManager
					.createNativeQuery(queryStr.toString().stripIndent().replace("\t", " ").replace("\n", " "));

			for (int batchIdx = 0; batchIdx < hldyModelSubList.size(); batchIdx++) {
				HldyModel hldyModel = hldyModelSubList.get(batchIdx);
				query.setParameter("hldyYmd" + batchIdx, hldyModel.getHldyYmd())
						.setParameter("hldyYmdSn" + batchIdx, hldyModel.getHldyYmdSn())
						.setParameter("hldyNm" + batchIdx, hldyModel.getHldyNm())
						.setParameter("hldyRmrk" + batchIdx, hldyModel.getHldyRmrk())
						.setParameter("useYn" + batchIdx, hldyModel.getUseYn())
						.setParameter("regDt" + batchIdx, hldyModel.getRegDt())
						.setParameter("rgtrId" + batchIdx, hldyModel.getRgtrId())
						.setParameter("mdfcnDt" + batchIdx, hldyModel.getMdfcnDt())
						.setParameter("mdfrId" + batchIdx, hldyModel.getMdfrId());
			}
			updatedRows += query.executeUpdate();

			entityManager.flush();
			entityManager.clear();
		}

		return updatedRows;
	}

	/***
	 * 휴일 삭제
	 */
	@Override
	public int deleteHldy(HldyModel hldyModel) throws Exception {
		String queryStr = """
				UPDATE
					hldy_table
				SET
					useYn = 'N',
					mdfcnDt = :mdfcnDt,
					mdfrId = :mdfrId
				WHERE
					useYn = 'Y'
					AND hldyYmd = :hldyYmd
					AND hldyYmdSn = :hldyYmdSn
				""";
		Query query = entityManager.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "))
				.setParameter("hldyYmd", hldyModel.getHldyYmd()).setParameter("hldyYmdSn", hldyModel.getHldyYmdSn())
				.setParameter("mdfcnDt", hldyModel.getMdfcnDt()).setParameter("mdfrId", hldyModel.getMdfrId());

		return query.executeUpdate();
	}
}