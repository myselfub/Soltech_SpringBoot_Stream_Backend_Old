package kr.co.soltech.stream.atrz.opnn.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import kr.co.soltech.stream.commons.model.BaseSerializer;
import kr.co.soltech.stream.commons.utils.SoltechStreamUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/***
 * 결재 의견 조회 파라메터 DTO 클래스
 */
@NoArgsConstructor
@Getter
@ToString
public class AtrzOpnnParamDTO implements BaseSerializer, Serializable {
	/**
	 * 직렬화 UID
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 의견 ID
	 */
	@JsonProperty("opnn_id")
	private String opnnId;

	/***
	 * 문서 번호
	 */
	@JsonProperty("doc_no")
	private String docNo;

	/***
	 * 등록 일자
	 */
	@JsonProperty("reg_ymd")
	private String regYmd;

	/***
	 * 등록자 ID
	 */
	@JsonProperty("rgtr_id")
	private String rgtrId;

	/***
	 * opnnId 스네이크케이스 Setter
	 * 
	 * @param opnn_id : 의견 ID
	 */
	public void setOpnn_id(String opnn_id) {
		this.opnnId = opnn_id;
	}

	/***
	 * docNo 스네이크케이스 Setter
	 * 
	 * @param doc_no : 문서 번호
	 */
	public void setDoc_no(String doc_no) {
		this.docNo = doc_no;
	}

	/***
	 * regYmd 스네이크케이스 Setter
	 * 
	 * @param reg_ymd : 등록 일자
	 */
	public void setReg_ymd(String reg_ymd) {
		this.regYmd = reg_ymd;
	}

	/***
	 * rgtrId 스네이크케이스 Setter
	 * 
	 * @param rgtr_id : 등록자 ID
	 */
	public void setRgtr_id(String rgtr_id) {
		this.rgtrId = rgtr_id;
	}

	/***
	 * 데이터를 클라이언트로 보내기 전 포맷 변경
	 */
	@Override
	public void serialize() {
		this.regYmd = regYmd == null ? "" : SoltechStreamUtils.parseDateToString(regYmd, "yyyy-MM-dd");
	}

	/***
	 * 데이터를 클라이언트에서 가져오고 포맷 변경
	 */
	@Override
	public void deserialize() {
		this.regYmd = regYmd == null ? "" : SoltechStreamUtils.parseDateToString(regYmd, "yyyyMMdd");
	}
}