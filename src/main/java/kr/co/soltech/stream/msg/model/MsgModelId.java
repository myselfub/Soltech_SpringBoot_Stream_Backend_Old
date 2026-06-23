package kr.co.soltech.stream.msg.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/***
 * 메세지 모델 ID 클래스
 */
@SuperBuilder
@NoArgsConstructor
@Getter
@ToString
public class MsgModelId implements Serializable {
	/***
	 * 직렬화 UID
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 메세지 언어
	 */
	@JsonProperty("msg_lang")
	@Size(min = 1, max = 5, message = "VALID_0002")
	private String msgLang;

	/***
	 * 메세지 분류
	 */
	@JsonProperty("msg_clsf")
	@Size(min = 1, max = 20, message = "VALID_0002")
	private String msgClsf;

	/***
	 * 메세지 코드
	 */
	@JsonProperty("msg_cd")
	@Size(min = 1, max = 20, message = "VALID_0002")
	private String msgCd;

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
}