package kr.co.soltech.stream.msg.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/***
 * 메세지 조회 파라메터 DTO 클래스
 */
@NoArgsConstructor
@Getter
@ToString
public class MsgParamDTO implements Serializable {
	/***
	 * 직렬화 UID
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 메세지 언어
	 */
	@JsonProperty("msg_lang")
	private String msgLang;

	/***
	 * 메세지 분류
	 */
	@JsonProperty("msg_clsf")
	private String msgClsf;

	/***
	 * 메세지 코드
	 */
	@JsonProperty("msg_cd")
	private String msgCd;

	/***
	 * 메세지 명
	 */
	@JsonProperty("msg_nm")
	private String msgNm;

	/***
	 * msgLang 스네이크케이스 Setter
	 * 
	 * @param msg_lang : 메세지 언어
	 */
	public void setMsg_lang(String msg_lang) {
		this.msgLang = msg_lang;
	}

	/***
	 * msgClsf 스네이크케이스 Setter
	 * 
	 * @param msg_clsf : 메세지 분류
	 */
	public void setMsg_clsf(String msg_clsf) {
		this.msgClsf = msg_clsf;
	}

	/***
	 * msgCd 스네이크케이스 Setter
	 * 
	 * @param msg_cd : 메세지 코드
	 */
	public void setMsg_cd(String msg_cd) {
		this.msgCd = msg_cd;
	}

	/***
	 * msgNm 스네이크케이스 Setter
	 * 
	 * @param msg_nm : 메세지 명
	 */
	public void setMsg_nm(String msg_nm) {
		this.msgNm = msg_nm;
	}
}