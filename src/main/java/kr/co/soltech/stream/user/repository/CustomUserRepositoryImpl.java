package kr.co.soltech.stream.user.repository;

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
import kr.co.soltech.stream.user.model.UserModel;
import kr.co.soltech.stream.user.model.UserParamDTO;
import lombok.RequiredArgsConstructor;

/***
 * 사용자 커스텀 레파지토리 클래스
 */
@RequiredArgsConstructor
class CustomUserRepositoryImpl implements CustomUserRepository {
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
	 * 사용자 목록 조회
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Page<UserModel> inqUser(UserParamDTO userParamDTO, Pageable pageable) throws Exception {
		StringBuilder whereQuery = new StringBuilder("");
		whereQuery.append(
				!ObjectUtils.isEmpty(userParamDTO.getUserId()) ? "AND userId LIKE '%' || :userId || '%' \r\n" : "");
		whereQuery.append(
				!ObjectUtils.isEmpty(userParamDTO.getUserEmpNo()) ? "AND userEmpNo LIKE '%' || :userEmpNo || '%' \r\n"
						: "");
		whereQuery.append(
				!ObjectUtils.isEmpty(userParamDTO.getUserNm()) ? "AND userNm LIKE '%' || :userNm || '%' \r\n" : "");
		whereQuery.append(!ObjectUtils.isEmpty(userParamDTO.getUserBrthYmd())
				? "AND userBrthYmd LIKE '%' || :userBrthYmd || '%' \r\n"
				: "");
		whereQuery.append(!ObjectUtils.isEmpty(userParamDTO.getUserJncmpYmd())
				? "AND userJncmpYmd LIKE '%' || :userJncmpYmd || '%' \r\n"
				: "");
		whereQuery.append(
				!ObjectUtils.isEmpty(userParamDTO.getUserDeptSeCd()) ? "AND userDeptSeCd = :userDeptSeCd \r\n" : "");
		whereQuery.append(
				!ObjectUtils.isEmpty(userParamDTO.getUserJbgdSeCd()) ? "AND userJbgdSeCd = :userJbgdSeCd \r\n" : "");
		whereQuery.append(
				!ObjectUtils.isEmpty(userParamDTO.getUserAuthrtSeCd()) ? "AND userAuthrtSeCd = :userAuthrtSeCd \r\n"
						: "");

		String queryStr = """
				SELECT
					userId AS user_id,
					userEmpNo AS user_emp_no,
					userNm AS user_nm,
					userHpNo AS user_hp_no,
					userEml AS user_eml,
					userAddr AS user_addr,
					userBrthYmd AS user_brth_ymd,
					userJncmpYmd AS user_jncmp_ymd,
					userAtdcId AS user_atdc_id,
					userDeptSeCd AS user_dept_se_cd,
					(
						SELECT
							cmmnCdNm
						FROM
							cmmn_cd_table
						WHERE
							useYn = 'Y'
							AND cmmnCdClsfId = 'SOLTECH_0000'
							AND cmmnCdId = user_tbl.userDeptSeCd
					) AS user_dept_se_nm,
					userJbgdSeCd AS user_jbgd_se_cd,
					(
						SELECT
							cmmnCdNm
						FROM
							cmmn_cd_table
						WHERE
							useYn = 'Y'
							AND cmmnCdClsfId = 'SOLTECH_0001'
							AND cmmnCdId = user_tbl.userJbgdSeCd
					) AS user_jbgd_se_nm,
					userAuthrtSeCd AS user_authrt_se_cd,
					(
						SELECT
							cmmnCdNm
						FROM
							cmmn_cd_table
						WHERE
							useYn = 'Y'
							AND cmmnCdClsfId = 'SOLTECH_0002'
							AND cmmnCdId = user_tbl.userAuthrtSeCd
					) AS user_authrt_se_nm,
					useYn AS use_yn,
					regDt AS reg_dt,
					rgtrId AS rgtr_id,
					mdfcnDt AS mdfcn_dt,
					mdfrId AS mdfr_id
				FROM
					user_table user_tbl
				WHERE
					useYn = 'Y'""" + whereQuery.toString() + """
				ORDER BY
					userEmpNo
				""";

		TypedQuery<Map> typedQuery = entityManager
				.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "), Map.class);

		String countQueryStr = """
				SELECT
					COUNT(1)
				FROM
					user_table user_tbl
				WHERE
					useYn = 'Y'
				""" + whereQuery.toString();
		TypedQuery<Long> countTypedQuery = entityManager.createQuery(countQueryStr, Long.class);

		if (!ObjectUtils.isEmpty(userParamDTO.getUserId())) {
			typedQuery.setParameter("userId", userParamDTO.getUserId());
			countTypedQuery.setParameter("userId", userParamDTO.getUserId());
		}
		if (!ObjectUtils.isEmpty(userParamDTO.getUserEmpNo())) {
			typedQuery.setParameter("userEmpNo", userParamDTO.getUserEmpNo());
			countTypedQuery.setParameter("userEmpNo", userParamDTO.getUserEmpNo());
		}
		if (!ObjectUtils.isEmpty(userParamDTO.getUserNm())) {
			typedQuery.setParameter("userNm", userParamDTO.getUserNm());
			countTypedQuery.setParameter("userNm", userParamDTO.getUserNm());
		}
		if (!ObjectUtils.isEmpty(userParamDTO.getUserBrthYmd())) {
			typedQuery.setParameter("userBrthYmd", userParamDTO.getUserBrthYmd());
			countTypedQuery.setParameter("userBrthYmd", userParamDTO.getUserBrthYmd());
		}
		if (!ObjectUtils.isEmpty(userParamDTO.getUserJncmpYmd())) {
			typedQuery.setParameter("userJncmpYmd", userParamDTO.getUserJncmpYmd());
			countTypedQuery.setParameter("userJncmpYmd", userParamDTO.getUserJncmpYmd());
		}
		if (!ObjectUtils.isEmpty(userParamDTO.getUserDeptSeCd())) {
			typedQuery.setParameter("userDeptSeCd", userParamDTO.getUserDeptSeCd());
			countTypedQuery.setParameter("userDeptSeCd", userParamDTO.getUserDeptSeCd());
		}
		if (!ObjectUtils.isEmpty(userParamDTO.getUserJbgdSeCd())) {
			typedQuery.setParameter("userJbgdSeCd", userParamDTO.getUserJbgdSeCd());
			countTypedQuery.setParameter("userJbgdSeCd", userParamDTO.getUserJbgdSeCd());
		}
		if (!ObjectUtils.isEmpty(userParamDTO.getUserAuthrtSeCd())) {
			typedQuery.setParameter("userAuthrtSeCd", userParamDTO.getUserAuthrtSeCd());
			countTypedQuery.setParameter("userAuthrtSeCd", userParamDTO.getUserAuthrtSeCd());
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
			countQueryResult = customCntRepository.getUserCnt(countTypedQuery, userParamDTO);
		} else {
			countQueryResult = queryResult.size();
			pageable = PageRequest.of(0, queryResult.size() == 0 ? 1 : queryResult.size());
		}

		return new PageImpl<UserModel>(objectMapper.convertValue(queryResult, new TypeReference<List<UserModel>>() {
		}), pageable, countQueryResult);
	}

	/***
	 * 사용자 상세 조회
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public UserModel getUser(UserParamDTO userParamDTO) throws Exception {
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
								AND user_id = :userId
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
					user_id AS user_id,
					user_emp_no AS user_emp_no,
					user_nm AS user_nm,
					user_hp_no AS user_hp_no,
					user_eml AS user_eml,
					user_addr AS user_addr,
					user_brth_ymd AS user_brth_ymd,
					user_jncmp_ymd AS user_jncmp_ymd,
					user_atdc_id AS user_atdc_id,
					user_dept_se_cd AS user_dept_se_cd,
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
						ORDER BY
							LENGTH(user_whol_dept_nm) DESC
						LIMIT 1
					) AS user_whol_dept_nm,
					user_jbgd_se_cd AS user_jbgd_se_cd,
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
					user_authrt_se_cd AS user_authrt_se_cd,
					(
						SELECT
							cmmn_cd_nm
						FROM
							cmmn_cd_table
						WHERE
							use_yn = 'Y'
							AND cmmn_cd_clsf_id = 'SOLTECH_0002'
							AND cmmn_cd_id = user_tbl.user_authrt_se_cd
					) AS user_authrt_se_nm,
					use_yn AS use_yn,
					TO_CHAR(reg_dt, 'YYYY-MM-DD"T"HH24:MI:SS') AS reg_dt,
					rgtr_id AS rgtr_id,
					TO_CHAR(mdfcn_dt, 'YYYY-MM-DD"T"HH24:MI:SS') AS mdfcn_dt,
					mdfr_id AS mdfr_id
				FROM
					user_table user_tbl
				WHERE
					use_yn = 'Y'
					AND user_id = :userId
				""";

		Query query = entityManager
				.createNativeQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "), Map.class)
				.setParameter("userId", userParamDTO.getUserId());

		List<Map> queryResultList = query.getResultList();
		Map queryResult = queryResultList.stream().findFirst().orElse(null);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		return objectMapper.convertValue(queryResult, new TypeReference<UserModel>() {
		});
	}

	/***
	 * 사용자 등록/수정
	 */
	@Override
	public int upsertUser(UserModel userModel) throws Exception {
		String queryStr = """
				MERGE INTO
					user_table target_tbl
				USING (
					SELECT
						:userId AS user_id,
						:userPw AS user_pw,
						:userEmpNo AS user_emp_no,
						:userNm AS user_nm,
						:userHpNo AS user_hp_no,
						:userEml AS user_eml,
						:userAddr AS user_addr,
						:userBrthYmd AS user_brth_ymd,
						:userPhotoFilePath AS user_photo_file_path,
						:userJncmpYmd AS user_jncmp_ymd,
						:userOfcTelno AS user_ofc_telno,
						:userAtdcId AS user_atdc_id,
						:userDeptSeCd AS user_dept_se_cd,
						:userJbgdSeCd AS user_jbgd_se_cd,
						:userAuthrtSeCd AS user_authrt_se_cd,
						:useYn AS use_yn,
						(:regDt)::timestamp AS reg_dt,
						:rgtrId AS rgtr_id,
						(:mdfcnDt)::timestamp AS mdfcn_dt,
						:mdfrId AS mdfr_id
				) source_tbl
				ON (
					target_tbl.user_id = source_tbl.user_id
				)
				WHEN MATCHED THEN
				UPDATE SET
					user_pw = source_tbl.user_pw,
					user_emp_no = source_tbl.user_emp_no,
					user_nm = source_tbl.user_nm,
					user_hp_no = source_tbl.user_hp_no,
					user_eml = source_tbl.user_eml,
					user_addr = source_tbl.user_addr,
					user_brth_ymd = source_tbl.user_brth_ymd,
					user_photo_file_path = source_tbl.user_photo_file_path,
					user_jncmp_ymd = source_tbl.user_jncmp_ymd,
					user_ofc_telno = source_tbl.user_ofc_telno,
					user_atdc_id = source_tbl.user_atdc_id,
					user_dept_se_cd = source_tbl.user_dept_se_cd,
					user_jbgd_se_cd = source_tbl.user_jbgd_se_cd,
					user_authrt_se_cd = source_tbl.user_authrt_se_cd,
					mdfcn_dt = source_tbl.mdfcn_dt,
					mdfr_id = source_tbl.mdfr_id
				WHEN NOT MATCHED THEN
				INSERT (
					user_id,
					user_pw,
					user_emp_no,
					user_nm,
					user_hp_no,
					user_eml,
					user_addr,
					user_brth_ymd,
					user_photo_file_path,
					user_jncmp_ymd,
					user_ofc_telno,
					user_atdc_id,
					user_dept_se_cd,
					user_jbgd_se_cd,
					user_authrt_se_cd,
					use_yn,
					reg_dt,
					rgtr_id,
					mdfcn_dt,
					mdfr_id
				) VALUES (
					source_tbl.user_id,
					source_tbl.user_pw,
					source_tbl.user_emp_no,
					source_tbl.user_nm,
					source_tbl.user_hp_no,
					source_tbl.user_eml,
					source_tbl.user_addr,
					source_tbl.user_brth_ymd,
					source_tbl.user_photo_file_path,
					source_tbl.user_jncmp_ymd,
					source_tbl.user_ofc_telno,
					source_tbl.user_atdc_id,
					source_tbl.user_dept_se_cd,
					source_tbl.user_jbgd_se_cd,
					source_tbl.user_authrt_se_cd,
					source_tbl.use_yn,
					source_tbl.reg_dt,
					source_tbl.rgtr_id,
					source_tbl.mdfcn_dt,
					source_tbl.mdfr_id
				)
				""";

		Query query = entityManager.createNativeQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "))
				.setParameter("userId", userModel.getUserId()).setParameter("userPw", userModel.getUserPw())
				.setParameter("userEmpNo", userModel.getUserEmpNo()).setParameter("userNm", userModel.getUserNm())
				.setParameter("userHpNo", userModel.getUserHpNo()).setParameter("userEml", userModel.getUserEml())
				.setParameter("userAddr", userModel.getUserAddr())
				.setParameter("userBrthYmd", userModel.getUserBrthYmd())
				.setParameter("userPhotoFilePath", userModel.getUserPhotoFilePath())
				.setParameter("userJncmpYmd", userModel.getUserJncmpYmd())
				.setParameter("userOfcTelno", userModel.getUserOfcTelno())
				.setParameter("userAtdcId", userModel.getUserAtdcId())
				.setParameter("userDeptSeCd", userModel.getUserDeptSeCd())
				.setParameter("userJbgdSeCd", userModel.getUserJbgdSeCd())
				.setParameter("userAuthrtSeCd", userModel.getUserAuthrtSeCd())
				.setParameter("useYn", userModel.getUseYn()).setParameter("regDt", userModel.getRegDt())
				.setParameter("rgtrId", userModel.getRgtrId()).setParameter("mdfcnDt", userModel.getMdfcnDt())
				.setParameter("mdfrId", userModel.getMdfrId());

		return query.executeUpdate();
	}

	/***
	 * 사용자 삭제
	 */
	@Override
	public int deleteUser(UserModel userModel) throws Exception {
		String queryStr = """
				UPDATE
					user_table
				SET
					useYn = 'N',
					mdfcnDt = :mdfcnDt,
					mdfrId = :mdfrId
				WHERE
					useYn = 'Y'
					AND userId = :userId
				""";
		Query query = entityManager.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "))
				.setParameter("userId", userModel.getUserId()).setParameter("mdfcnDt", userModel.getMdfcnDt())
				.setParameter("mdfrId", userModel.getMdfrId());

		return query.executeUpdate();
	}
}