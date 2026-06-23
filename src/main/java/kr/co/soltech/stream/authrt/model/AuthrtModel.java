package kr.co.soltech.stream.authrt.model;

import org.hibernate.annotations.Comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import kr.co.soltech.stream.commons.model.BaseModel;
import kr.co.soltech.stream.commons.model.BaseSerializer;
import kr.co.soltech.stream.commons.model.CustomDateSerializer;
import kr.co.soltech.stream.commons.utils.SoltechStreamUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/***
 * 권한 모델 클래스
 */
@Comment("권한 테이블")
@Entity(name = "authrt_table")
@IdClass(AuthrtModelId.class)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString(callSuper = true)
public class AuthrtModel extends BaseModel implements BaseSerializer {
	/***
	 * 직렬화 UID
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 권한 구분 코드
	 */
	@JsonProperty("authrt_se_cd")
	@Size(min = 1, max = 20, message = "VALID_0002")
	@Id
	@Column(length = 20, nullable = false)
	@Comment("권한 구분 코드")
	private String authrtSeCd;

	/***
	 * 권한 구분 명
	 */
	@JsonProperty("authrt_se_nm")
	@Transient
	private String authrtSeNm;

	/***
	 * 메뉴 ID
	 */
	@JsonProperty("menu_id")
	@Size(min = 1, max = 20, message = "VALID_0002")
	@Id
	@Column(length = 20, nullable = false)
	@Comment("메뉴 ID")
	private String menuId;

	/***
	 * 권한 시작 일자
	 */
	@JsonProperty("authrt_bgng_ymd")
	@JsonSerialize(using = CustomDateSerializer.class)
	@Size(min = 8, max = 8, message = "VALID_0002")
	@Digits(integer = 8, fraction = 0, message = "VALID_0002")
	@Column(length = 8, nullable = false)
	@Comment("권한 시작 일자")
	private String authrtBgngYmd;

	/***
	 * 권한 종료 일자
	 */
	@JsonProperty("authrt_end_ymd")
	@JsonSerialize(using = CustomDateSerializer.class)
	@Size(min = 8, max = 8, message = "VALID_0002")
	@Digits(integer = 8, fraction = 0, message = "VALID_0002")
	@Column(length = 8, nullable = false)
	@Comment("권한 종료 일자")
	private String authrtEndYmd;

	/***
	 * 메소드 리스트
	 */
	@JsonProperty("mthd_list")
	@Size(max = 100, message = "VALID_0002")
	@Column(length = 100)
	@Comment("메소드 리스트")
	private String mthdList;

	/***
	 * 데이터베이스 작업전 날짜 포맷 변환
	 */
	@PrePersist
	@PreUpdate
	public void preModel() {
		this.authrtBgngYmd = authrtBgngYmd == null ? ""
				: SoltechStreamUtils.parseDateToString(authrtBgngYmd, "yyyyMMdd");
		this.authrtEndYmd = authrtEndYmd == null ? "" : SoltechStreamUtils.parseDateToString(authrtEndYmd, "yyyyMMdd");
	}

	/***
	 * 데이터를 클라이언트로 보내기 전 포맷 변경
	 */
	@Override
	public void serialize() {
		this.authrtBgngYmd = authrtBgngYmd == null ? ""
				: SoltechStreamUtils.parseDateToString(authrtBgngYmd, "yyyyMMdd");
		this.authrtEndYmd = authrtEndYmd == null ? "" : SoltechStreamUtils.parseDateToString(authrtEndYmd, "yyyyMMdd");
	}

	/***
	 * 데이터를 클라이언트에서 가져오고 포맷 변경
	 */
	@Override
	public void deserialize() {
		this.authrtBgngYmd = authrtBgngYmd == null ? ""
				: SoltechStreamUtils.parseDateToString(authrtBgngYmd, "yyyy-MM-dd");
		this.authrtEndYmd = authrtEndYmd == null ? ""
				: SoltechStreamUtils.parseDateToString(authrtEndYmd, "yyyy-MM-dd");
	}
}