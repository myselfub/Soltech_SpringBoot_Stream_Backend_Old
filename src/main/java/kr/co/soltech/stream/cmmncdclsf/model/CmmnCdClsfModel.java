package kr.co.soltech.stream.cmmncdclsf.model;

import org.hibernate.annotations.Comment;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Size;
import kr.co.soltech.stream.commons.model.BaseModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/***
 * 공통코드 분류 모델 클래스
 */
@Comment("공통코드 분류 테이블")
@Entity(name = "cmmn_cd_clsf_table")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class CmmnCdClsfModel extends BaseModel {
	/***
	 * 직렬화 UID
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 공통코드 분류 ID
	 */
	@JsonProperty("cmmn_cd_clsf_id")
	@Size(min = 1, max = 20, message = "VALID_0002")
	@Id
	@Column(name = "cmmnCdClsfId", length = 20, nullable = false)
	@Comment("공통코드 분류 ID")
	private String cmmnCdClsfId;

	/***
	 * 공통코드 분류 명
	 */
	@JsonProperty("cmmn_cd_clsf_nm")
	@Size(min = 1, max = 40, message = "VALID_0002")
	@Column(length = 40, nullable = false)
	@Comment("공통코드 분류 명")
	private String cmmnCdClsfNm;

	/***
	 * 공통코드 분류 설명
	 */
	@JsonProperty("cmmn_cd_clsf_expln")
	@Size(max = 500, message = "VALID_0002")
	@Column(length = 500)
	@Comment("공통코드 분류 설명")
	private String cmmnCdClsfExpln;

	/***
	 * 공통코드 분류 사용자정의 값1
	 */
	@JsonProperty("cmmn_cd_clsf_user_dfn_vl1")
	@Size(max = 400, message = "VALID_0002")
	@Column(length = 400)
	@Comment("공통코드 분류 사용자정의 값1")
	private String cmmnCdClsfUserDfnVl1;
}