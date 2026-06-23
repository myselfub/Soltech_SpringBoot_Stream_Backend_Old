package kr.co.soltech.stream.user.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import kr.co.soltech.stream.commons.model.BaseSerializer;
import kr.co.soltech.stream.commons.utils.SoltechStreamUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/***
 * 사용자 조회 파라메터 DTO 클래스
 */
@NoArgsConstructor
@Getter
@ToString
public class UserParamDTO implements BaseSerializer, Serializable {
	/**
	 * 직렬화 UID
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 사용자 ID
	 */
	@JsonProperty("user_id")
	private String userId;

	/***
	 * 사용자 비밀번호
	 */
	@JsonProperty("user_pw")
	private String userPw;

	/***
	 * 사용자 사원 번호
	 */
	@JsonProperty("user_emp_no")
	private String userEmpNo;

	/***
	 * 사용자 명
	 */
	@JsonProperty("user_nm")
	private String userNm;

	/***
	 * 사용자 출생 일자
	 */
	@JsonProperty("user_brth_ymd")
	private String userBrthYmd;

	/***
	 * 사용자 입사 일자
	 */
	@JsonProperty("user_jncmp_ymd")
	private String userJncmpYmd;

	/***
	 * 사용자 부서 구분 코드
	 */
	@JsonProperty("user_dept_se_cd")
	private String userDeptSeCd;

	/***
	 * 사용자 직급 구분 코드
	 */
	@JsonProperty("user_jbgd_se_cd")
	private String userJbgdSeCd;

	/***
	 * 사용자 권한 구분 코드
	 */
	@JsonProperty("user_authrt_se_cd")
	private String userAuthrtSeCd;

	/***
	 * userId 스네이크케이스 Setter
	 * 
	 * @param user_id : 사용자 ID
	 */
	public void setUser_id(String user_id) {
		this.userId = user_id;
	}

	/***
	 * userId 스네이크케이스 Setter
	 * 
	 * @param user_pw : 사용자 패스워드
	 */
	public void setUser_pw(String user_pw) {
		this.userPw = user_pw;
	}

	/***
	 * userEmpNo 스네이크케이스 Setter
	 * 
	 * @param user_emp_no : 사용자 사원 번호
	 */
	public void setUser_emp_no(String user_emp_no) {
		this.userEmpNo = user_emp_no;
	}

	/***
	 * userNm 스네이크케이스 Setter
	 * 
	 * @param user_nm : 사용자 명
	 */
	public void setUser_nm(String user_nm) {
		this.userNm = user_nm;
	}

	/***
	 * userBrthYmd 스네이크케이스 Setter
	 * 
	 * @param user_brth_ymd : 사용자 출생 일자
	 */
	public void setUser_brth_ymd(String user_brth_ymd) {
		this.userBrthYmd = user_brth_ymd;
	}

	/***
	 * userJncmpYmd 스네이크케이스 Setter
	 * 
	 * @param user_jncmp_ymd : 사용자 입사 일자
	 */
	public void setUser_jncmp_ymd(String user_jncmp_ymd) {
		this.userJncmpYmd = user_jncmp_ymd;
	}

	/***
	 * userDeptSeCd 스네이크케이스 Setter
	 * 
	 * @param user_dept_se_cd : 사용자 부서 구분 코드
	 */
	public void setUser_dept_se_cd(String user_dept_se_cd) {
		this.userDeptSeCd = user_dept_se_cd;
	}

	/***
	 * userJbgdSeCd 스네이크케이스 Setter
	 * 
	 * @param user_jbgd_se_cd : 사용자 직급 구분 코드
	 */
	public void setUser_jbgd_se_cd(String user_jbgd_se_cd) {
		this.userJbgdSeCd = user_jbgd_se_cd;
	}

	/***
	 * userAuthrtSeCd 스네이크케이스 Setter
	 * 
	 * @param user_authrt_se_cd : 사용자 권한 구분 코드
	 */
	public void setUser_authrt_se_cd(String user_authrt_se_cd) {
		this.userAuthrtSeCd = user_authrt_se_cd;
	}

	/***
	 * 데이터를 클라이언트로 보내기 전 포맷 변경
	 */
	@Override
	public void serialize() {
		this.userBrthYmd = userBrthYmd == null ? "" : SoltechStreamUtils.parseDateToString(userBrthYmd, "yyyy-MM-dd");
		this.userJncmpYmd = userJncmpYmd == null ? ""
				: SoltechStreamUtils.parseDateToString(userJncmpYmd, "yyyy-MM-dd");
	}

	/***
	 * 데이터를 클라이언트에서 가져오고 포맷 변경
	 */
	@Override
	public void deserialize() {
		this.userBrthYmd = userBrthYmd == null ? "" : SoltechStreamUtils.parseDateToString(userBrthYmd, "yyyyMMdd");
		this.userJncmpYmd = userJncmpYmd == null ? "" : SoltechStreamUtils.parseDateToString(userJncmpYmd, "yyyyMMdd");
	}
}