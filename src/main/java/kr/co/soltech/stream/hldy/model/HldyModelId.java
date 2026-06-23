package kr.co.soltech.stream.hldy.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import kr.co.soltech.stream.commons.model.BaseSerializer;
import kr.co.soltech.stream.commons.model.CustomDateSerializer;
import kr.co.soltech.stream.commons.utils.SoltechStreamUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/***
 * 휴일 모델 ID 클래스
 */
@SuperBuilder
@NoArgsConstructor
@Getter
@ToString
public class HldyModelId implements BaseSerializer, Serializable {
	/***
	 * 직렬화 UID
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 휴일 일자
	 */
	@JsonProperty("hldy_ymd")
	@JsonSerialize(using = CustomDateSerializer.class)
	@Size(min = 8, max = 8, message = "VALID_0002")
	private String hldyYmd;

	/***
	 * 휴일 일자 순번
	 */
	@JsonProperty("hldy_ymd_sn")
	@Size(min = 1, max = 4, message = "VALID_0002")
	@Digits(integer = 4, fraction = 0, message = "VALID_0002")
	private int hldyYmdSn;

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