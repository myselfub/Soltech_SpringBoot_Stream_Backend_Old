package kr.co.soltech.stream.atrz.frm.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/***
 * 결재 양식 조회 파라메터 DTO 클래스
 */
@NoArgsConstructor
@Getter
@ToString
public class AtrzFrmParamDTO implements Serializable {
	/**
	 * 직렬화 UID
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 문서 구분 코드
	 */
	@JsonProperty("doc_se_cd")
	private String docSeCd;

	/***
	 * docSeCd 스네이크케이스 Setter
	 * 
	 * @param doc_se_cd : 문서 구분 코드
	 */
	public void setDoc_se_cd(String doc_se_cd) {
		this.docSeCd = doc_se_cd;
	}
}