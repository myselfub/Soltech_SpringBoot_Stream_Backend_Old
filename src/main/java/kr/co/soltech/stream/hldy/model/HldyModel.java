package kr.co.soltech.stream.hldy.model;

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
import jakarta.validation.constraints.Digits;
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
 * 휴일 모델 클래스
 */
@Comment("휴일 테이블")
@Entity(name = "hldy_table")
@IdClass(HldyModelId.class)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class HldyModel extends BaseModel implements BaseSerializer {
	/**
	 * 직렬화 UID
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 휴일 일자
	 */
	@JsonProperty("hldy_ymd")
	@JsonSerialize(using = CustomDateSerializer.class)
	@Size(min = 8, max = 8, message = "VALID_0002")
	@Id
	@Column(length = 8, nullable = false)
	@Comment("휴일 일자")
	private String hldyYmd;

	/***
	 * 휴일 일자 순번
	 */
	@JsonProperty("hldy_ymd_sn")
	@Size(min = 1, max = 4, message = "VALID_0002")
	@Digits(integer = 4, fraction = 0, message = "VALID_0002")
	@Id
	@Column(length = 4, nullable = false)
	@Comment("휴일 일자 순번")
	private int hldyYmdSn;

	/***
	 * 휴일 명
	 */
	@JsonProperty("hldy_nm")
	@Size(max = 50, message = "VALID_0002")
	@Column(length = 50, nullable = false)
	@Comment("휴일 명")
	private String hldyNm;

	/*** 휴일 비고 */
	@JsonProperty("hldy_rmrk")
	@Size(max = 400, message = "VALID_0002")
	@Column(length = 400)
	@Comment("휴일 비고")
	private String hldyRmrk;

	/***
	 * 데이터베이스 작업전 날짜 포맷 변환
	 */
	@PrePersist
	@PreUpdate
	@PostConstruct
	public void preModel() {
		this.hldyYmd = hldyYmd == null ? "" : SoltechStreamUtils.parseDateToString(hldyYmd, "yyyyMMdd");
	}

	/***
	 * 직렬화(데이터를 클라이언트로 보내기 전 포맷 변경)
	 */
	@Override
	public void serialize() {
		this.hldyYmd = hldyYmd == null ? "" : SoltechStreamUtils.parseDateToString(hldyYmd, "yyyy-MM-dd");
	}

	/***
	 * 역직렬화(데이터를 클라이언트에서 가져오고 포맷 변경)
	 */
	@Override
	public void deserialize() {
		this.hldyYmd = hldyYmd == null ? "" : SoltechStreamUtils.parseDateToString(hldyYmd, "yyyyMMdd");
	}
}