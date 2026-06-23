package kr.co.soltech.stream.cmmncd.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/***
 * 공통코드 모델 ID 클래스
 * ----------------------------------------------------------------------
 * 
 * VALID 어노테이션(@NotBlank 등)의 동작 1. msg_table의 msg_clsf 중에서 분류코드(VALID, FIELD)를
 * 가져옴. 2. VALID 어노테이션의 message속성과 분류코드(VAILD)의 msg_cd가 같은 것을 찾음. 3. 클래스의 필드명과
 * 동일한 분류코드(FIELD)의 msg_cd가 같은 것을 찾음. 4. 분류코드(VAILD)의 msg_cn에 {field}이 있으면,
 * 분류코드(FIELD)의 msg_cn으로 변환. 5. {0}, {1}, {2}도 존재하면, msg_user_dfn_vl1,
 * msg_user_dfn_vl2, msg_user_dfn_vl3로 각각 변환 6. 동일한 값이 없으면, 메세지 그대로 표출
 * 
 * ----------------------------------------------------------------------
 * msg_lang msg_clsf msg_cd msg_cn
 * ---------------------------------------------------------------------- KR
 * VALID VALID_0001 {field}은(는) 공백일 수 없습니다. KR FIELD cmmnCdClsfId 공통코드분류ID
 * ----------------------------------------------------------------------
 * 
 * => 공통코드분류ID은(는) 공백일 수 없습니다.
 */
@SuperBuilder
@NoArgsConstructor
@Getter
@ToString
public class CmmnCdModelId implements Serializable {
	/***
	 * 직렬화 UID
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 공통코드 분류 ID
	 */
	@JsonProperty("cmmn_cd_clsf_id")
	@Size(min = 1, max = 20, message = "VALID_0002")
	private String cmmnCdClsfId;

	/***
	 * 공통코드 ID
	 */
	@JsonProperty("cmmn_cd_id")
	@NotBlank(message = "VALID_0001")
	private String cmmnCdId;

	/***
	 * cmmnCdClsfId 스네이크케이스 Setter
	 * 
	 * @param cmmn_cd_clsf_id : 공통코드 분류 ID
	 */
	public void setCmmn_cd_clsf_id(String cmmn_cd_clsf_id) {
		this.cmmnCdClsfId = cmmn_cd_clsf_id;
	}

	/***
	 * cmmnCdId 스네이크케이스 Setter
	 * 
	 * @param cmmn_cd_id : 공통코드 ID
	 */
	public void setCmmn_cd_id(String cmmn_cd_id) {
		this.cmmnCdId = cmmn_cd_id;
	}
}