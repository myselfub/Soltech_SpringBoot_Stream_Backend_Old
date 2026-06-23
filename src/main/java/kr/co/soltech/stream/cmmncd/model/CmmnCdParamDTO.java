package kr.co.soltech.stream.cmmncd.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/***
 * 공통코드 조회 파라메터 DTO 클래스
 */
@NoArgsConstructor
@Getter
@ToString
public class CmmnCdParamDTO implements Serializable {
	/**
	 * 직렬화 UID
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 공통코드 분류 ID
	 */
	@JsonProperty("cmmn_cd_clsf_id")
	private String cmmnCdClsfId;

	/***
	 * 공통코드 분류 명
	 */
	@JsonProperty("cmmn_cd_clsf_nm")
	private String cmmnCdClsfNm;

	/***
	 * 공통코드 ID
	 */
	@JsonProperty("cmmn_cd_id")
	private String cmmnCdId;

	/***
	 * 공통코드 명
	 */
	@JsonProperty("cmmn_cd_nm")
	private String cmmnCdNm;

	/***
	 * 공통코드 사용자정의 값1
	 */
	@JsonProperty("cmmn_cd_user_dfn_vl1")
	private String cmmnCdUserDfnVl1;

	/***
	 * 공통코드 사용자정의 값2
	 */
	@JsonProperty("cmmn_cd_user_dfn_vl2")
	private String cmmnCdUserDfnVl2;

	/***
	 * cmmnCdClsfId 스네이크케이스 Setter
	 * 
	 * @param cmmn_cd_clsf_id : 공통코드 분류 ID
	 */
	public void setCmmn_cd_clsf_id(String cmmn_cd_clsf_id) {
		this.cmmnCdClsfId = cmmn_cd_clsf_id;
	}

	/***
	 * cmmnCdClsfNm 스네이크케이스 Setter
	 * 
	 * @param cmmn_cd_clsf_nm : 공통코드 분류 명
	 */
	public void setCmmn_cd_clsf_nm(String cmmn_cd_clsf_nm) {
		this.cmmnCdClsfNm = cmmn_cd_clsf_nm;
	}

	/***
	 * cmmnCdId 스네이크케이스 Setter
	 * 
	 * @param cmmn_cd_id : 공통코드 ID
	 */
	public void setCmmn_cd_id(String cmmn_cd_id) {
		this.cmmnCdId = cmmn_cd_id;
	}

	/***
	 * cmmnCdNm 스네이크케이스 Setter
	 * 
	 * @param cmmn_cd_nm : 공통코드 명
	 */
	public void setCmmn_cd_nm(String cmmn_cd_nm) {
		this.cmmnCdNm = cmmn_cd_nm;
	}

	/***
	 * cmmnCdUserDfnVl1 스네이크케이스 Setter
	 * 
	 * @param cmmn_cd_user_dfn_vl1 : 공통코드 사용자정의 값1
	 */
	public void setCmmn_cd_user_dfn_vl1(String cmmn_cd_user_dfn_vl1) {
		this.cmmnCdUserDfnVl1 = cmmn_cd_user_dfn_vl1;
	}

	/***
	 * cmmnCdUserDfnVl1 스네이크케이스 Setter
	 * 
	 * @param cmmn_cd_user_dfn_vl2 : 공통코드 사용자정의 값2
	 */
	public void setCmmn_cd_user_dfn_vl2(String cmmn_cd_user_dfn_vl2) {
		this.cmmnCdUserDfnVl1 = cmmn_cd_user_dfn_vl2;
	}
}