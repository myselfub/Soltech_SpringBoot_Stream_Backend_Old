package kr.co.soltech.stream.vctn.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import kr.co.soltech.stream.commons.model.BaseSerializer;
import kr.co.soltech.stream.commons.utils.SoltechStreamUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/***
 * 휴가 모델 ID 클래스
 */
@SuperBuilder
@NoArgsConstructor
@Getter
@ToString
public class VctnModelId implements BaseSerializer, Serializable {
	/**
	 * 직렬화 UID
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 휴가 사용자 ID
	 */
	@JsonProperty("vctn_user_id")
	@Size(min = 1, max = 20, message = "VALID_0002")
	private String vctnUserId;

	/***
	 * 휴가 구분 코드
	 */
	@JsonProperty("vctn_se_cd")
	@Size(min = 1, max = 20, message = "VALID_0002")
	private String vctnSeCd;

	/***
	 * 휴가 시작 일자
	 */
	@JsonProperty("vctn_bgng_ymd")
	@Pattern(regexp = "^$|^[0-9]{8}$", message = "VALID_0003")
	private String vctnBgngYmd;

	/***
	 * vctnUserId 스네이크케이스 Setter
	 * 
	 * @param vctn_user_id : 휴가 사용자 ID
	 */
	public void setVctn_user_id(String vctn_user_id) {
		this.vctnUserId = vctn_user_id;
	}

	/***
	 * vctnSeCd 스네이크케이스 Setter
	 * 
	 * @param vctn_se_cd : 휴가 구분 코드
	 */
	public void setVctn_se_cd(String vctn_se_cd) {
		this.vctnSeCd = vctn_se_cd;
	}

	/***
	 * vctnBgngYmd 스네이크케이스 Setter
	 * 
	 * @param vctn_bgng_ymd : 휴가 시작 일자
	 */
	public void setVctn_bgng_ymd(String vctn_bgng_ymd) {
		this.vctnBgngYmd = vctn_bgng_ymd;
	}

	/***
	 * 데이터를 클라이언트로 보내기 전 포맷 변경
	 */
	@Override
	public void serialize() {
		this.vctnBgngYmd = vctnBgngYmd == null ? "" : SoltechStreamUtils.parseDateToString(vctnBgngYmd, "yyyy-MM-dd");
	}

	/***
	 * 데이터를 클라이언트에서 가져오고 포맷 변경
	 */
	@Override
	public void deserialize() {
		this.vctnBgngYmd = vctnBgngYmd == null ? "" : SoltechStreamUtils.parseDateToString(vctnBgngYmd, "yyyyMMdd");
	}
}