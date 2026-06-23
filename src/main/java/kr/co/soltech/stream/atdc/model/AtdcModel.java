package kr.co.soltech.stream.atdc.model;

import org.hibernate.annotations.Comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Size;
import kr.co.soltech.stream.commons.model.BaseModel;
import kr.co.soltech.stream.commons.model.BaseSerializer;
import kr.co.soltech.stream.commons.model.CustomDateSerializer;
import kr.co.soltech.stream.commons.utils.SoltechStreamUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/***
 * 근태 모델 클래스
 */
@Comment("근태 테이블")
@Entity(name = "atdc_table")
@IdClass(AtdcModelId.class)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class AtdcModel extends BaseModel implements BaseSerializer {
	/**
	 * 직렬화 UID
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 근태 사용자 ID
	 */
	@JsonProperty("atdc_user_id")
	@Size(min = 1, max = 20, message = "VALID_0002")
	@Id
	@Column(length = 20, nullable = false)
	@Comment("근태 사용자 ID")
	private String atdcUserId;

	/***
	 * 근태 사용자 명
	 */
	@JsonProperty("atdc_user_nm")
	@Transient
	private String atdcUserNm;

	/***
	 * 근태 일자
	 */
	@JsonProperty("atdc_ymd")
	@JsonSerialize(using = CustomDateSerializer.class)
	@Size(min = 8, max = 8, message = "VALID_0002")
	@Id
	@Column(length = 8, nullable = false)
	@Comment("근태 일자")
	private String atdcYmd;

	/***
	 * 근태 시각
	 */
	@JsonProperty("atdc_tm")
	@Size(min = 6, max = 6, message = "VALID_0002")
	@Id
	@Column(length = 6, nullable = false)
	@Comment("근태 시각")
	private String atdcTm;

	/***
	 * 근태 구분 코드
	 */
	@JsonProperty("atdc_se_cd")
	@Size(min = 1, max = 20, message = "VALID_0002")
	@Id
	@Column(length = 20, nullable = false)
	@Comment("근태 구분 코드")
	private String atdcSeCd;

	/***
	 * 근태 명
	 */
	@JsonProperty("atdc_se_nm")
	@Transient
	private String atdcSeNm;

	/***
	 * 데이터베이스 작업전 날짜 포맷 변환
	 */
	@PrePersist
	@PreUpdate
	@PostConstruct
	public void preModel() {
		this.atdcYmd = atdcYmd == null ? "" : SoltechStreamUtils.parseDateToString(atdcYmd, "yyyyMMdd");
		this.atdcTm = atdcTm == null ? "" : SoltechStreamUtils.parseDateToString(atdcTm, "HHmmss");
	}

	/***
	 * 직렬화(데이터를 클라이언트로 보내기 전 포맷 변경)
	 */
	@Override
	public void serialize() {
		this.atdcYmd = atdcYmd == null ? "" : SoltechStreamUtils.parseDateToString(atdcYmd, "yyyy-MM-dd");
		this.atdcTm = atdcTm == null ? "" : SoltechStreamUtils.parseDateToString(atdcTm, "HH:mm:ss");
	}

	/***
	 * 역직렬화(데이터를 클라이언트에서 가져오고 포맷 변경)
	 */
	@Override
	public void deserialize() {
		this.atdcYmd = atdcYmd == null ? "" : SoltechStreamUtils.parseDateToString(atdcYmd, "yyyyMMdd");
		this.atdcTm = atdcTm == null ? "" : SoltechStreamUtils.parseDateToString(atdcTm, "HHmmss");
	}
}