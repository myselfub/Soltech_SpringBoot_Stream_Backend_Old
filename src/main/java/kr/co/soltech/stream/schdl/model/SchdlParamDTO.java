package kr.co.soltech.stream.schdl.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import kr.co.soltech.stream.commons.model.BaseSerializer;
import kr.co.soltech.stream.commons.utils.SoltechStreamUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/***
 * 일정 조회 파라메터 DTO 클래스
 */
@NoArgsConstructor
@Getter
@ToString
public class SchdlParamDTO implements BaseSerializer, Serializable {
	/**
	 * 직렬화 UID
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 일정 ID
	 */
	@JsonProperty("schdl_id")
	private String schdlId;

	/***
	 * 일정 사용자 ID
	 */
	@JsonProperty("schdl_user_id")
	private String schdlUserId;

	/***
	 * 일정 구분 코드
	 */
	@JsonProperty("schdl_se_cd")
	private String schdlSeCd;

	/***
	 * 일정 제목
	 */
	@JsonProperty("schdl_ttl")
	private String schdlTtl;

	/***
	 * 일정 내용
	 */
	@JsonProperty("schdl_cn")
	private String schdlCn;

	/***
	 * 일정 시작 일시
	 */
	@JsonProperty("schdl_bgng_dt")
	private String schdlBgngDt;

	/***
	 * 일정 공개 여부
	 */
	@JsonProperty("schdl_rls_yn")
	private Character schdlRlsYn;

	public void setSchdl_id(String schdl_id) {
		this.schdlId = schdl_id;
	}

	public void setSchdl_user_id(String schdl_user_id) {
		this.schdlUserId = schdl_user_id;
	}

	public void setSchdl_se_cd(String schdl_se_cd) {
		this.schdlSeCd = schdl_se_cd;
	}

	public void setSchdl_ttl(String schdl_ttl) {
		this.schdlTtl = schdl_ttl;
	}

	public void setSchdl_cn(String schdl_cn) {
		this.schdlCn = schdl_cn;
	}

	public void setSchdl_bgng_dt(String schdl_bgng_dt) {
		this.schdlBgngDt = schdl_bgng_dt;
	}

	public void setSchdl_rls_yn(Character schdl_rls_yn) {
		this.schdlRlsYn = schdl_rls_yn;
	}

	@Override
	public void serialize() {
		this.schdlBgngDt = schdlBgngDt == null ? ""
				: SoltechStreamUtils.parseDateToString(schdlBgngDt, "yyyy-MM-dd HH:mm:ss");
	}

	@Override
	public void deserialize() {
		this.schdlBgngDt = schdlBgngDt == null ? ""
				: SoltechStreamUtils.parseDateToString(schdlBgngDt, "yyyyMMdd HHmmss");
	}
}