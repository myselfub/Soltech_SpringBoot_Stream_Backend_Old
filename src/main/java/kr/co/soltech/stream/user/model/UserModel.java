package kr.co.soltech.stream.user.model;

import org.hibernate.annotations.Comment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import kr.co.soltech.stream.commons.model.BaseModel;
import kr.co.soltech.stream.commons.model.BaseSerializer;
import kr.co.soltech.stream.commons.utils.SoltechStreamUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/***
 * 사용자 모델 클래스
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Comment("사용자 테이블")
@Entity(name = "user_table")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class UserModel extends BaseModel implements BaseSerializer {
	/***
	 * 직렬화 UID
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 사용자 ID
	 */
	@JsonProperty("user_id")
	@Size(min = 1, max = 20, message = "VALID_0002")
	@Id
	@Column(length = 20, nullable = false)
	@Comment("사용자 ID")
	private String userId;

	/***
	 * 사용자 비밀번호
	 */
	@JsonProperty("user_pw")
	@Size(min = 1, max = 100, message = "VALID_0002")
	@Column(length = 100, nullable = false)
	@Comment("사용자 비밀번호")
	private String userPw;

	/***
	 * 사용자 사원 번호
	 */
	@JsonProperty("user_emp_no")
	@Size(min = 1, max = 10, message = "VALID_0002")
	@Digits(integer = 10, fraction = 0, message = "VALID_0002")
	@Column(length = 10, nullable = false)
	@Comment("사용자 사원 번호")
	private String userEmpNo;

	/***
	 * 사용자 명
	 */
	@JsonProperty("user_nm")
	@Size(min = 1, max = 40, message = "VALID_0002")
	@Column(length = 40, nullable = false)
	@Comment("사용자 명")
	private String userNm;

	/***
	 * 사용자 핸드폰 번호
	 */
	@JsonProperty("user_hp_no")
	@Size(max = 20, message = "VALID_0002")
	@Column(length = 20)
	@Comment("사용자 핸드폰 번호")
	private String userHpNo;

	/***
	 * 사용자 이메일
	 */
	@JsonProperty("user_eml")
	@Email
	@Size(max = 50, message = "VALID_0004")
	@Column(length = 50)
	@Comment("사용자 이메일")
	private String userEml;

	/***
	 * 사용자 주소
	 */
	@JsonProperty("user_addr")
	@Size(max = 200, message = "VALID_0002")
	@Column(length = 200)
	@Comment("사용자 주소")
	private String userAddr;

	/***
	 * 사용자 출생 일자
	 */
	@JsonProperty("user_brth_ymd")
	@Size(max = 8, message = "VALID_0002")
	@Digits(integer = 8, fraction = 0, message = "VALID_0002")
	@Column(length = 8)
	@Comment("사용자 출생 일자")
	private String userBrthYmd;

	/***
	 * 사용자 사진 파일 경로
	 */
	@JsonProperty("user_photo_file_path")
	@Column(length = 255)
	@Comment("사용자 사진 파일 경로")
	private String userPhotoFilePath;

	/***
	 * 사용자 입사 일자
	 */
	@JsonProperty("user_jncmp_ymd")
	@Size(max = 8, message = "VALID_0002")
	@Digits(integer = 8, fraction = 0, message = "VALID_0002")
	@Column(length = 8)
	@Comment("사용자 입사 일자")
	private String userJncmpYmd;

	/***
	 * 사용자 사무실 전화번호
	 */
	@JsonProperty("user_ofc_telno")
	@Size(max = 13, message = "VALID_0002")
	@Digits(integer = 13, fraction = 0, message = "VALID_0002")
	@Column(length = 13)
	@Comment("사용자 사무실 전화번호")
	private String userOfcTelno;

	/***
	 * 사용자 근태 ID
	 */
	@JsonProperty("user_atdc_id")
	@Size(max = 4, message = "VALID_0002")
	@Digits(integer = 4, fraction = 0, message = "VALID_0002")
	@Column(length = 4)
	@Comment("사용자 근태 ID(CAPS ID)")
	private int userAtdcId;

	/***
	 * 사용자 부서 구분 코드
	 */
	@JsonProperty("user_dept_se_cd")
	@Size(max = 20, message = "VALID_0002")
	@Builder.Default
	@Column(length = 20)
	@Comment("사용자 부서 구분 코드")
	private String userDeptSeCd = "000000"; // 솔텍시스템

	/***
	 * 사용자 부서 구분 명
	 */
	@JsonProperty("user_dept_se_nm")
	@Transient
	private String userDeptSeNm;

	/***
	 * 사용자 전체 부서 명
	 */
	@JsonProperty("user_whol_dept_nm")
	@Transient
	private String userWholDeptNm;

	/***
	 * 사용자 직급 구분 코드
	 */
	@JsonProperty("user_jbgd_se_cd")
	@Size(max = 20, message = "VALID_0002")
	@Column(length = 20)
	@Comment("사용자 직급 구분 코드")
	private String userJbgdSeCd;

	/***
	 * 사용자 직급 구분 명
	 */
	@JsonProperty("user_jbgd_se_nm")
	@Transient
	private String userJbgdSeNm;

	/***
	 * 사용자 권한 구분 코드
	 */
	@JsonProperty("user_authrt_se_cd")
	@Size(max = 20, message = "VALID_0002")
	@Builder.Default
	@Column(length = 20)
	@Comment("사용자 권한 구분 코드")
	private String userAuthrtSeCd = "00030"; // 사용자

	/***
	 * 사용자 권한 구분 명
	 */
	@JsonProperty("user_authrt_se_nm")
	@Transient
	private String userAuthrtSeNm;

	/***
	 * IP 정보
	 */
	@JsonProperty("user_ip")
	@Transient
	private String userIp;

	/***
	 * 사용자 IP 변경
	 * 
	 * @param userIp : 사용자 IP
	 */
	public void updateUserIp(String userIp) {
		this.userIp = userIp;
	}

	/***
	 * 데이터베이스 작업전 날짜 포맷 변환
	 */
	@PrePersist
	@PreUpdate
	public void preModel() {
		this.userBrthYmd = userBrthYmd == null ? "" : SoltechStreamUtils.parseDateToString(userBrthYmd, "yyyyMMdd");
		this.userJncmpYmd = userJncmpYmd == null ? "" : SoltechStreamUtils.parseDateToString(userJncmpYmd, "yyyyMMdd");
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