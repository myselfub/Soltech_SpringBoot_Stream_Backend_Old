package kr.co.soltech.stream.vctn.model;

import java.math.BigDecimal;

import org.hibernate.annotations.Comment;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import kr.co.soltech.stream.commons.model.BaseModel;
import kr.co.soltech.stream.commons.model.BaseSerializer;
import kr.co.soltech.stream.commons.utils.SoltechStreamUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/***
 * 휴가 모델 클래스
 */
@Comment("휴가 테이블")
@Entity(name = "vctn_table")
@IdClass(VctnModelId.class)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class VctnModel extends BaseModel implements BaseSerializer {
	/**
	 * 직렬화 UID
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 휴가 사용자 ID
	 */
	@JsonProperty("vctn_user_id")
	@Size(min = 1, max = 20, message = "VALID_0002")
	@Id
	@Column(length = 20, nullable = false)
	@Comment("휴가 사용자 ID")
	private String vctnUserId;

	/***
	 * 휴가 사용자 명
	 */
	@JsonProperty("vctn_user_nm")
	@Transient
	private String vctnUserNm;

	/***
	 * 사용자 부서 코드 명
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
	 * 사용자 직급 코드 명
	 */
	@JsonProperty("user_jbgd_se_nm")
	@Transient
	private String userJbgdSeNm;

	/***
	 * 휴가 구분 코드
	 */
	@JsonProperty("vctn_se_cd")
	@Size(min = 1, max = 20, message = "VALID_0002")
	@Id
	@Column(length = 20, nullable = false)
	@Comment("휴가 구분 코드")
	private String vctnSeCd;

	/***
	 * 휴가 구분 명
	 */
	@JsonProperty("vctn_se_nm")
	@Transient
	private String vctnSeNm;

	/***
	 * 휴가 시작 일자
	 */
	@JsonProperty("vctn_bgng_ymd")
	@Pattern(regexp = "^$|^[0-9]{8}$", message = "VALID_0003")
	@Id
	@Column(length = 8, nullable = false)
	@Comment("휴가 시작 일자")
	private String vctnBgngYmd;

	/***
	 * 휴가 종료 일자
	 */
	@JsonProperty("vctn_end_ymd")
	@Pattern(regexp = "^$|^[0-9]{8}$", message = "VALID_0003")
	@Column(length = 8, nullable = false)
	@Comment("휴가 종료 일자")
	private String vctnEndYmd;

	/***
	 * 휴가 사용 수
	 */
	@JsonProperty("vctn_use_cnt")
	@Digits(integer = 3, fraction = 3, message = "VALID_0002")
	@Column(precision = 6, scale = 3, nullable = false)
	@Comment("휴가 사용 수")
	private BigDecimal vctnUseCnt;

	/***
	 * 결재 문서 번호
	 */
	@JsonProperty("atrz_doc_no")
	@Size(max = 10, message = "VALID_0002")
	@Column(length = 10)
	@Comment("결재 문서 번호")
	private String atrzDocNo;

	/***
	 * 기안 일자
	 */
	@JsonProperty("drft_ymd")
	@Transient
	private String drftYmd;

	/***
	 * 결재 상태 구분 명
	 */
	@JsonProperty("atrz_stts_se_nm")
	@Transient
	private String atrzSttsSeNm;

	/***
	 * 데이터베이스 작업전 날짜 포맷 변환
	 */
	@PrePersist
	@PreUpdate
	public void preModel() {
		this.vctnBgngYmd = vctnBgngYmd == null ? "" : SoltechStreamUtils.parseDateToString(vctnBgngYmd, "yyyyMMdd");
		this.vctnEndYmd = vctnEndYmd == null ? "" : SoltechStreamUtils.parseDateToString(vctnEndYmd, "yyyyMMdd");
	}

	/***
	 * 데이터를 클라이언트로 보내기 전 포맷 변경
	 */
	@Override
	public void serialize() {
		this.vctnBgngYmd = vctnBgngYmd == null ? "" : SoltechStreamUtils.parseDateToString(vctnBgngYmd, "yyyy-MM-dd");
		this.vctnEndYmd = vctnEndYmd == null ? "" : SoltechStreamUtils.parseDateToString(vctnEndYmd, "yyyy-MM-dd");
	}

	/***
	 * 데이터를 클라이언트에서 가져오고 포맷 변경
	 */
	@Override
	public void deserialize() {
		this.vctnBgngYmd = vctnBgngYmd == null ? "" : SoltechStreamUtils.parseDateToString(vctnBgngYmd, "yyyyMMdd");
		this.vctnEndYmd = vctnEndYmd == null ? "" : SoltechStreamUtils.parseDateToString(vctnEndYmd, "yyyyMMdd");
	}
}