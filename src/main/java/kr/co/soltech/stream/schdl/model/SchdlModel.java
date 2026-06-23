package kr.co.soltech.stream.schdl.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Size;
import kr.co.soltech.stream.commons.model.BaseModel;
import kr.co.soltech.stream.commons.model.BaseSerializer;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/***
 * 일정 모델 클래스
 */
@Comment("일정 테이블")
@Entity(name = "schdl_table")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class SchdlModel extends BaseModel implements BaseSerializer {
	/**
	 * 직렬화 UID
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 일정 ID
	 */
	@JsonProperty("schdl_id")
	@Size(max = 20, message = "VALID_0002")
	@Id
	@Column(length = 20, nullable = false)
	@Comment("일정 ID")
	private String schdlId;

	/***
	 * 일정 사용자 ID
	 */
	@JsonProperty("schdl_user_id")
	@Size(min = 1, max = 20, message = "VALID_0002")
	@Column(length = 20, nullable = false)
	@Comment("일정 사용자 ID")
	private String schdlUserId;

	/***
	 * 일정 사용자 명
	 */
	@JsonProperty("schdl_user_nm")
	@Transient
	private String schdlUserNm;

	/***
	 * 일정 구분 코드
	 */
	@JsonProperty("schdl_se_cd")
	@Size(min = 1, max = 20, message = "VALID_0002")
	@Column(length = 20, nullable = false)
	@Comment("일정 구분 코드")
	private String schdlSeCd;

	/***
	 * 일정 구분 명
	 */
	@JsonProperty("schdl_se_nm")
	@Transient
	private String schdlSeNm;

	/***
	 * 일정 제목
	 */
	@JsonProperty("schdl_ttl")
	@Size(min = 1, max = 100, message = "VALID_0002")
	@Column(length = 100, nullable = false)
	@Comment("일정 제목")
	private String schdlTtl;

	/***
	 * 일정 내용
	 */
	@JsonProperty("schdl_cn")
	@Size(max = 500, message = "VALID_0002")
	@Column(length = 500)
	@Comment("일정 내용")
	private String schdlCn;

	/***
	 * 일정 시작 일시
	 */
	@JsonProperty("schdl_bgng_dt")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	// @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}", message =
	// "VALID_0003")
	@Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE", nullable = false)
	@Comment("일정 시작 일시")
	private LocalDateTime schdlBgngDt;

	/***
	 * 일정 종료 일시
	 */
	@JsonProperty("schdl_end_dt")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	// @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}", message =
	// "VALID_0003")
	@Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE", nullable = false)
	@Comment("일정 종료 일시")
	private LocalDateTime schdlEndDt;

	/***
	 * 일정 공개 여부
	 */
	@JsonProperty("schdl_rls_yn")
	@Builder.Default
	@Column(columnDefinition = "CHAR(1) DEFAULT 'Y'", nullable = false)
	@Comment("일정 공개 여부")
	private Character schdlRlsYn = 'Y';

	@Override
	public void serialize() {
	}

	@Override
	public void deserialize() {
	}
}