package kr.co.soltech.stream.atdc.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Size;
import kr.co.soltech.stream.commons.model.BaseSerializer;
import kr.co.soltech.stream.commons.utils.SoltechStreamUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/***
 * 근태 모델 ID 클래스
 */
@SuperBuilder
@NoArgsConstructor
@Getter
@ToString
public class AtdcModelId implements BaseSerializer, Serializable {
	/**
	 * 직렬화 UID
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 근태 사용자 ID
	 */
	@JsonProperty("atdc_user_id")
	@Size(min = 1, max = 20, message = "VALID_0002")
	private String atdcUserId;

	/***
	 * 근태 일자
	 */
	@JsonProperty("atdc_ymd")
	@Size(min = 8, max = 10, message = "VALID_0002")
	private String atdcYmd;

	/***
	 * 근태 시각
	 */
	@JsonProperty("atdc_tm")
	@Size(min = 6, max = 8, message = "VALID_0002")
	private String atdcTm;

	/***
	 * 근태 구분 코드
	 */
	@JsonProperty("atdc_se_cd")
	@Size(min = 1, max = 20, message = "VALID_0002")
	private String atdcSeCd;

	/***
	 * atdcUserId 스네이크케이스 Setter
	 * 
	 * @param atdc_user_id : 근태 사용자 ID
	 */
	public void setAtdc_user_id(String atdc_user_id) {
		this.atdcUserId = atdc_user_id;
	}

	/***
	 * atdcYmd 스네이크케이스 Setter
	 * 
	 * @param atdc_ymd : 근태 일자
	 */
	public void setAtdc_ymd(String atdc_ymd) {
		this.atdcYmd = atdc_ymd;
	}

	/***
	 * atdcTm 스네이크케이스 Setter
	 * 
	 * @param atdc_tm : 근태 시각
	 */
	public void setAtdc_tm(String atdc_tm) {
		this.atdcTm = atdc_tm;
	}

	/***
	 * atdcSeCd 스네이크케이스 Setter
	 * 
	 * @param atdc_se_cd : 근태 구분 코드
	 */
	public void setAtdc_se_cd(String atdc_se_cd) {
		this.atdcSeCd = atdc_se_cd;
	}

	/***
	 * 데이터를 클라이언트로 보내기 전 포맷 변경
	 */
	@Override
	public void serialize() {
		this.atdcYmd = atdcYmd == null ? "" : SoltechStreamUtils.parseDateToString(atdcYmd, "yyyy-MM-dd");
		this.atdcTm = atdcTm == null ? "" : SoltechStreamUtils.parseDateToString(atdcTm, "HH:mm:ss");
	}

	/***
	 * 데이터를 클라이언트에서 가져오고 포맷 변경
	 */
	@Override
	public void deserialize() {
		this.atdcYmd = atdcYmd == null ? "" : SoltechStreamUtils.parseDateToString(atdcYmd, "yyyyMMdd");
		this.atdcTm = atdcTm == null ? "" : SoltechStreamUtils.parseDateToString(atdcTm, "HHmmss");
	}
}