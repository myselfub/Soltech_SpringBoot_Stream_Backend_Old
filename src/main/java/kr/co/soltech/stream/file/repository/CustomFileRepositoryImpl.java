package kr.co.soltech.stream.file.repository;

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
import kr.co.soltech.stream.file.model.FileModel;
import kr.co.soltech.stream.file.model.FileParamDTO;
import lombok.RequiredArgsConstructor;

/***
 * 파일 커스텀 레파지토리 클래스
 */
@RequiredArgsConstructor
public class CustomFileRepositoryImpl implements CustomFileRepository {
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
	 * 파일 목록 조회
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Page<FileModel> inqFile(FileParamDTO fileParamDTO, Pageable pageable) throws Exception {
		StringBuilder whereQuery = new StringBuilder("");
		whereQuery.append(
				!ObjectUtils.isEmpty(fileParamDTO.getDmnClsfId()) ? "AND dmnClsfId LIKE '%' || :dmnClsfId || '%' \r\n"
						: "");
		whereQuery.append(
				!ObjectUtils.isEmpty(fileParamDTO.getFileNm()) ? "AND fileNm LIKE '%' || :fileNm || '%' \r\n" : "");
		whereQuery.append(!ObjectUtils.isEmpty(fileParamDTO.getFileExtnNm())
				? "AND fileExtnNm LIKE '%' || :fileExtnNm || '%' \r\n"
				: "");
		whereQuery.append(!ObjectUtils.isEmpty(fileParamDTO.getFileSz()) ? "AND fileSz >= :fileSz \r\n" : "");
		whereQuery.append(!ObjectUtils.isEmpty(fileParamDTO.getRegYmd())
				? "AND TO_CHAR(regDt, 'YYYYMMDD') LIKE '%' || :regYmd || '%' \r\n"
				: "");

		String queryStr = """
				SELECT
					fileId AS file_id,
					dmnClsfId AS dmn_clsf_id,
					fileNm AS file_nm,
					filePath AS file_path,
					orgnflNm AS orgnfl_nm,
					fileExtnNm AS file_extn_nm,
					fileSz AS file_sz,
					(
						SELECT
							userNm
						FROM
							user_table
						WHERE
							useYn = 'Y'
							AND userId = file_tbl.rgtrId
					) AS rgtr_nm,
					useYn AS use_yn,
					regDt AS reg_dt,
					rgtrId AS rgtr_id,
					mdfcnDt AS mdfcn_dt,
					mdfrId AS mdfr_id
				FROM
					file_table file_tbl
				WHERE
					useYn = 'Y'
					""" + whereQuery.toString() + """
				ORDER BY
					fileId DESC
				""";

		TypedQuery<Map> typedQuery = entityManager
				.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "), Map.class);

		String countQueryStr = """
				SELECT
					COUNT(1)
				FROM
					file_table file_tbl
				WHERE
					useYn = 'Y'
				""" + whereQuery.toString();
		TypedQuery<Long> countTypedQuery = entityManager.createQuery(countQueryStr, Long.class);

		if (!ObjectUtils.isEmpty(fileParamDTO.getDmnClsfId())) {
			typedQuery.setParameter("dmnClsfId", fileParamDTO.getDmnClsfId());
			countTypedQuery.setParameter("dmnClsfId", fileParamDTO.getDmnClsfId());
		}
		if (!ObjectUtils.isEmpty(fileParamDTO.getFileNm())) {
			typedQuery.setParameter("fileNm", fileParamDTO.getFileNm());
			countTypedQuery.setParameter("fileNm", fileParamDTO.getFileNm());
		}
		if (!ObjectUtils.isEmpty(fileParamDTO.getFileExtnNm())) {
			typedQuery.setParameter("fileExtnNm", fileParamDTO.getFileExtnNm());
			countTypedQuery.setParameter("fileExtnNm", fileParamDTO.getFileExtnNm());
		}
		if (!ObjectUtils.isEmpty(fileParamDTO.getFileSz())) {
			typedQuery.setParameter("fileSz", fileParamDTO.getFileSz());
			countTypedQuery.setParameter("fileSz", fileParamDTO.getFileSz());
		}
		if (!ObjectUtils.isEmpty(fileParamDTO.getRegYmd())) {
			typedQuery.setParameter("regYmd", fileParamDTO.getRegYmd());
			countTypedQuery.setParameter("regYmd", fileParamDTO.getRegYmd());
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
			countQueryResult = customCntRepository.getFileCnt(countTypedQuery, fileParamDTO);
		} else {
			countQueryResult = queryResult.size();
			pageable = PageRequest.of(0, queryResult.size() == 0 ? 1 : queryResult.size());
		}

		return new PageImpl<FileModel>(objectMapper.convertValue(queryResult, new TypeReference<List<FileModel>>() {
		}), pageable, countQueryResult);
	}

	/***
	 * 파일 상세 조회
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public FileModel getFile(FileParamDTO fileParamDTO) throws Exception {
		String queryStr = """
				SELECT
					fileId AS file_id,
					dmnClsfId AS dmn_clsf_id,
					fileNm AS file_nm,
					filePath AS file_path,
					orgnflNm AS orgnfl_nm,
					fileExtnNm AS file_extn_nm,
					fileSz AS file_sz,
					(
						SELECT
							userNm
						FROM
							user_table
						WHERE
							useYn = 'Y'
							AND userId = file_tbl.rgtrId
					) AS rgtr_nm,
					useYn AS use_yn,
					regDt AS reg_dt,
					rgtrId AS rgtr_id,
					mdfcnDt AS mdfcn_dt,
					mdfrId AS mdfr_id
				FROM
					file_table file_tbl
				WHERE
					useYn = 'Y'
					AND fileId = :fileId
				""";

		TypedQuery<Map> typedQuery = entityManager
				.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "), Map.class)
				.setParameter("fileId", fileParamDTO.getFileId());

		Map queryResult = typedQuery.getResultList().stream().findFirst().orElse(null);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		return objectMapper.convertValue(queryResult, new TypeReference<FileModel>() {
		});
	}

	/***
	 * 파일 등록/수정
	 */
	@Override
	public int upsertFile(FileModel fileModel) throws Exception {
		String queryStr = """
				MERGE INTO
					file_table target_tbl
				USING (
					SELECT
						:fileId AS file_id,
						:dmnClsfId AS dmn_clsf_id,
						:fileNm AS file_nm,
						:filePath AS file_path,
						:orgnflNm AS orgnfl_nm,
						:fileExtnNm AS file_extn_nm,
						:fileSz AS file_sz,
						:fileMimeType AS file_mime_type,
						:useYn AS use_yn,
						(:regDt)::timestamp AS reg_dt,
						:rgtrId AS rgtr_id,
						(:mdfcnDt)::timestamp AS mdfcn_dt,
						:mdfrId AS mdfr_id
				) source_tbl
				ON (
					target_tbl.file_id = source_tbl.file_id
				)
				WHEN MATCHED THEN
				UPDATE SET
					dmn_clsf_id = source_tbl.dmn_clsf_id,
					file_nm = source_tbl.file_nm,
					file_path = source_tbl.file_path,
					orgnfl_nm = source_tbl.orgnfl_nm,
					file_extn_nm = source_tbl.file_extn_nm,
					file_sz = source_tbl.file_sz,
					file_mime_type = source_tbl.file_mime_type,
					mdfcn_dt = source_tbl.mdfcn_dt,
					mdfr_id = source_tbl.mdfr_id
				WHEN NOT MATCHED THEN
				INSERT (
					file_id,
					dmn_clsf_id,
					file_nm,
					file_path,
					orgnfl_nm,
					file_extn_nm,
					file_sz,
					file_mime_type,
					use_yn,
					reg_dt,
					rgtr_id,
					mdfcn_dt,
					mdfr_id
				) VALUES (
					source_tbl.file_id,
					source_tbl.dmn_clsf_id,
					source_tbl.file_nm,
					source_tbl.file_path,
					source_tbl.orgnfl_nm,
					source_tbl.file_extn_nm,
					source_tbl.file_sz,
					source_tbl.file_mime_type,
					source_tbl.use_yn,
					source_tbl.reg_dt,
					source_tbl.rgtr_id,
					source_tbl.mdfcn_dt,
					source_tbl.mdfr_id
				)
				""";

		Query query = entityManager.createNativeQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "))
				.setParameter("fileId", fileModel.getFileId()).setParameter("dmnClsfId", fileModel.getDmnClsfId())
				.setParameter("fileNm", fileModel.getFileNm()).setParameter("filePath", fileModel.getFilePath())
				.setParameter("orgnflNm", fileModel.getOrgnflNm()).setParameter("fileExtnNm", fileModel.getFileExtnNm())
				.setParameter("fileSz", fileModel.getFileSz()).setParameter("fileMimeType", fileModel.getFileMimeType())
				.setParameter("useYn", fileModel.getUseYn()).setParameter("regDt", fileModel.getRegDt())
				.setParameter("rgtrId", fileModel.getRgtrId()).setParameter("mdfcnDt", fileModel.getMdfcnDt())
				.setParameter("mdfrId", fileModel.getMdfrId());

		return query.executeUpdate();
	}

	/***
	 * 다중 파일 등록/수정
	 */
	@Override
	public int upsertAllFile(List<FileModel> fileModelList) throws Exception {
		int updatedRows = 0;
		int batchSize = 50;

		for (int idx = 0; idx < fileModelList.size(); idx += batchSize) {
			int end = Math.min(idx + batchSize, fileModelList.size());
			List<FileModel> fileModelSubList = fileModelList.subList(idx, end);

			StringBuilder queryStr = new StringBuilder("""
					MERGE INTO
						file_table target_tbl
					USING (
						""");

			for (int batchIdx = 0; batchIdx < fileModelSubList.size(); batchIdx++) {
				queryStr.append("SELECT \r\n");
				queryStr.append(":fileId").append(batchIdx).append(" AS file_id, \r\n");
				queryStr.append(":dmnClsfId").append(batchIdx).append(" AS dmn_clsf_id, \r\n");
				queryStr.append(":fileNm").append(batchIdx).append(" AS file_nm, \r\n");
				queryStr.append(":filePath").append(batchIdx).append(" AS file_path, \r\n");
				queryStr.append(":orgnflNm").append(batchIdx).append(" AS orgnfl_nm, \r\n");
				queryStr.append(":fileExtnNm").append(batchIdx).append(" AS file_extn_nm, \r\n");
				queryStr.append(":fileSz").append(batchIdx).append(" AS file_sz, \r\n");
				queryStr.append(":fileMimeType").append(batchIdx).append(" AS file_mime_type, \r\n");
				queryStr.append(":useYn").append(batchIdx).append(" AS use_yn, \r\n");
				queryStr.append("(:regDt").append(batchIdx).append(")::timestamp").append(" AS reg_dt, \r\n");
				queryStr.append(":rgtrId").append(batchIdx).append(" AS rgtr_id, \r\n");
				queryStr.append("(:mdfcnDt").append(batchIdx).append(")::timestamp").append(" AS mdfcn_dt, \r\n");
				queryStr.append(":mdfrId").append(batchIdx).append(" AS mdfr_id \r\n");
				if (batchIdx < fileModelSubList.size() - 1) {
					queryStr.append("UNION ALL \r\n");
				}
			}
			queryStr.append("""
					) source_tbl
					ON (
						target_tbl.file_id = source_tbl.file_id
					)
					WHEN MATCHED THEN
					UPDATE SET
						dmn_clsf_id = source_tbl.dmn_clsf_id,
						file_nm = source_tbl.file_nm,
						file_path = source_tbl.file_path,
						orgnfl_nm = source_tbl.orgnfl_nm,
						file_extn_nm = source_tbl.file_extn_nm,
						file_sz = source_tbl.file_sz,
						file_mime_type = source_tbl.file_mime_type,
						mdfcn_dt = source_tbl.mdfcn_dt,
						mdfr_id = source_tbl.mdfr_id
					WHEN NOT MATCHED THEN
					INSERT (
						file_id,
						dmn_clsf_id,
						file_nm,
						file_path,
						orgnfl_nm,
						file_extn_nm,
						file_sz,
						file_mime_type,
						use_yn,
						reg_dt,
						rgtr_id,
						mdfcn_dt,
						mdfr_id
					) VALUES (
						source_tbl.file_id,
						source_tbl.dmn_clsf_id,
						source_tbl.file_nm,
						source_tbl.file_path,
						source_tbl.orgnfl_nm,
						source_tbl.file_extn_nm,
						source_tbl.file_sz,
						source_tbl.file_mime_type,
						source_tbl.use_yn,
						source_tbl.reg_dt,
						source_tbl.rgtr_id,
						source_tbl.mdfcn_dt,
						source_tbl.mdfr_id
					)
					""");

			Query query = entityManager
					.createNativeQuery(queryStr.toString().stripIndent().replace("\t", " ").replace("\n", " "));

			for (int batchIdx = 0; batchIdx < fileModelSubList.size(); batchIdx++) {
				FileModel fileModel = fileModelSubList.get(batchIdx);
				query.setParameter("fileId" + batchIdx, fileModel.getFileId())
						.setParameter("dmnClsfId" + batchIdx, fileModel.getDmnClsfId())
						.setParameter("fileNm" + batchIdx, fileModel.getFileNm())
						.setParameter("filePath" + batchIdx, fileModel.getFilePath())
						.setParameter("orgnflNm" + batchIdx, fileModel.getOrgnflNm())
						.setParameter("fileExtnNm" + batchIdx, fileModel.getFileExtnNm())
						.setParameter("fileSz" + batchIdx, fileModel.getFileSz())
						.setParameter("fileMimeType" + batchIdx, fileModel.getFileMimeType())
						.setParameter("useYn" + batchIdx, fileModel.getUseYn())
						.setParameter("regDt" + batchIdx, fileModel.getRegDt())
						.setParameter("rgtrId" + batchIdx, fileModel.getRgtrId())
						.setParameter("mdfcnDt" + batchIdx, fileModel.getMdfcnDt())
						.setParameter("mdfrId" + batchIdx, fileModel.getMdfrId());
			}
			updatedRows += query.executeUpdate();

			entityManager.flush();
			entityManager.clear();
		}

		return updatedRows;
	}

	/***
	 * 파일 삭제
	 */
	@Override
	public int deleteFile(FileModel fileModel) throws Exception {
		String queryStr = """
				UPDATE
					file_table
				SET
					useYn = 'N',
					mdfcnDt = :mdfcnDt,
					mdfrId = :mdfrId
				WHERE
					useYn = 'Y'
					AND fileId = :fileId
				""";
		Query query = entityManager.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "))
				.setParameter("fileId", fileModel.getFileId()).setParameter("mdfcnDt", fileModel.getMdfcnDt())
				.setParameter("mdfrId", fileModel.getMdfrId());

		return query.executeUpdate();
	}
}