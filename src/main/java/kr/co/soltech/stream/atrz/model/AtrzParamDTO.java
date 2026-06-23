package kr.co.soltech.stream.atrz.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Pattern;
import kr.co.soltech.stream.commons.model.BaseSerializer;
import kr.co.soltech.stream.commons.utils.SoltechStreamUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/***
 * 결재 조회 파라메터 DTO 클래스
 */
@NoArgsConstructor
@Getter
@ToString
public class AtrzParamDTO implements BaseSerializer, Serializable {
	/**
	 * 직렬화 UID
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 문서 번호
	 */
	@JsonProperty("doc_no")
	private String docNo;

	/***
	 * 문서 구분 코드
	 */
	@JsonProperty("doc_se_cd")
	private String docSeCd;

	/***
	 * 기안 시작 일자
	 */
	@JsonProperty("drft_bgng_ymd")
	@Pattern(regexp = "^$|^[0-9]{8}$", message = "VALID_0003")
	private String drftBgngYmd;

	/***
	 * 기안 종료 일자
	 */
	@JsonProperty("drft_end_ymd")
	@Pattern(regexp = "^$|^[0-9]{8}$", message = "VALID_0003")
	private String drftEndYmd;

	/***
	 * 기안자 아이디
	 */
	@JsonProperty("drftr_id")
	private String drftrId;

	/***
	 * 문서 제목
	 */
	@JsonProperty("doc_ttl")
	private String docTtl;

	/***
	 * 결재 상태 구분 코드
	 */
	@JsonProperty("atrz_stts_se_cd")
	private String atrzSttsSeCd;

	/***
	 * 결재자 ID
	 */
	@JsonProperty("aprvr_id")
	private String aprvrId;

	/***
	 * docNo 스네이크케이스 Setter
	 * 
	 * @param doc_no : 문서 번호
	 */
	public void setDoc_no(String doc_no) {
		this.docNo = doc_no;
	}

	/***
	 * docSeCd 스네이크케이스 Setter
	 * 
	 * @param doc_se_cd : 문서 구분 코드
	 */
	public void setDoc_se_cd(String doc_se_cd) {
		this.docSeCd = doc_se_cd;
	}

	/***
	 * drftBgngYmd 스네이크케이스 Setter
	 * 
	 * @param drft_bgng_ymd : 기안 시작 일자
	 */
	public void setDrft_bgng_ymd(String drft_bgng_ymd) {
		this.drftBgngYmd = drft_bgng_ymd;
	}

	/***
	 * drftEndYmd 스네이크케이스 Setter
	 * 
	 * @param drft_end_ymd : 기안 종료 일자
	 */
	public void setDrft_end_ymd(String drft_end_ymd) {
		this.drftEndYmd = drft_end_ymd;
	}

	/***
	 * drftrId 스네이크케이스 Setter
	 * 
	 * @param drftr_id : 기안자 아이디
	 */
	public void setDrftr_id(String drftr_id) {
		this.drftrId = drftr_id;
	}

	/***
	 * docTtl 스네이크케이스 Setter
	 * 
	 * @param doc_ttl : 문서 제목
	 */
	public void setDoc_ttl(String doc_ttl) {
		this.docTtl = doc_ttl;
	}

	/***
	 * atrzSttsSeCd 스네이크케이스 Setter
	 * 
	 * @param atrz_stts_se_cd : 결재 상태 구분 코드
	 */
	public void setAtrz_stts_se_cd(String atrz_stts_se_cd) {
		this.atrzSttsSeCd = atrz_stts_se_cd;
	}

	/***
	 * aprvrId 스네이크케이스 Setter
	 * 
	 * @param aprvr_id : 결재자 ID
	 */
	public void setAprvr_id(String aprvr_id) {
		this.aprvrId = aprvr_id;
	}

	/***
	 * 데이터를 클라이언트로 보내기 전 포맷 변경
	 */
	@Override
	public void serialize() {
		this.drftBgngYmd = drftBgngYmd == null ? "" : SoltechStreamUtils.parseDateToString(drftBgngYmd, "yyyy-MM-dd");
		this.drftEndYmd = drftEndYmd == null ? "" : SoltechStreamUtils.parseDateToString(drftEndYmd, "yyyy-MM-dd");
	}

	/***
	 * 데이터를 클라이언트에서 가져오고 포맷 변경
	 */
	@Override
	public void deserialize() {
		this.drftBgngYmd = drftBgngYmd == null ? "" : SoltechStreamUtils.parseDateToString(drftBgngYmd, "yyyyMMdd");
		this.drftEndYmd = drftEndYmd == null ? "" : SoltechStreamUtils.parseDateToString(drftEndYmd, "yyyyMMdd");
	}
}