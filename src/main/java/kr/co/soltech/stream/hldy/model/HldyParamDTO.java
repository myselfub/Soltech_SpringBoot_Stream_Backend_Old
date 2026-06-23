package kr.co.soltech.stream.hldy.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import kr.co.soltech.stream.commons.model.BaseSerializer;
import kr.co.soltech.stream.commons.utils.SoltechStreamUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/***
 * 휴일 조회 파라메터 DTO 클래스
 */
@NoArgsConstructor
@Getter
@ToString
public class HldyParamDTO implements BaseSerializer, Serializable {
	/**
	 * 직렬화 UID
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 휴일 일자
	 */
	@JsonProperty("hldy_ymd")
	private String hldyYmd;

	/***
	 * 휴일 일자 순번
	 */
	@JsonProperty("hldy_ymd_sn")
	private int hldyYmdSn;

	/***
	 * 휴일 명
	 */
	@JsonProperty("hldy_nm")
	private String hldyNm;

	/***
	 * hldyYmd 스네이크케이스 Setter
	 * 
	 * @param hldy_ymd : 휴일 일자
	 */
	public void setHldy_ymd(String hldy_ymd) {
		this.hldyYmd = hldy_ymd;
	}

	/***
	 * hldyYmdSn 스네이크케이스 Setter
	 * 
	 * @param hldy_ymd_sn : 휴일 일자 순번
	 */
	public void setHldy_ymd_sn(int hldy_ymd_sn) {
		this.hldyYmdSn = hldy_ymd_sn;
	}

	/***
	 * hldyNm 스네이크케이스 Setter
	 * 
	 * @param hldy_nm : 휴일 명
	 */
	public void setHldy_nm(String hldy_nm) {
		this.hldyNm = hldy_nm;
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