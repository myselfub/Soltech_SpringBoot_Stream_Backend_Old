package kr.co.soltech.stream.cmmncd.model;

import org.hibernate.annotations.Comment;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import kr.co.soltech.stream.commons.model.BaseModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/***
 * 공통코드 모델 클래스
 */
@Comment("공통코드 테이블")
@Entity(name = "cmmn_cd_table")
@IdClass(CmmnCdModelId.class)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@Getter
@ToString(callSuper = true)
/*
 * @Data
 * 
 * @EqualsAndHashCode(callSuper = false)
 * 
 * @Table(name = "cmmn_cd_table", uniqueConstraints = @UniqueConstraint(name =
 * "uk_cmmn_cd_table", columnNames = { "cmmn_cd_clsf", "cmmn_cd" }))
 */
public class CmmnCdModel extends BaseModel {
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
	@Column(name = "cmmnCdClsfId", columnDefinition = "", length = 20, nullable = false)
	@Comment("공통코드 분류 ID")
	private String cmmnCdClsfId;

	/***
	 * 공통코드 분류 명
	 */
	@JsonProperty("cmmn_cd_clsf_nm")
	@Transient
	private String cmmnCdClsfNm;

	/***
	 * 공통코드 ID
	 */
	@JsonProperty("cmmn_cd_id")
	@Size(min = 1, max = 20, message = "VALID_0002")
	@Id
	@Column(length = 20, nullable = false)
	@Comment("공통코드 ID")
	private String cmmnCdId;

	/***
	 * 공통코드 명
	 */
	@JsonProperty("cmmn_cd_nm")
	@Size(min = 1, max = 20, message = "VALID_0002")
	@Column(length = 40, nullable = false)
	@Comment("공통코드 명")
	private String cmmnCdNm;

	/***
	 * 공통코드 설명
	 */
	@JsonProperty("cmmn_cd_expln")
	@Size(max = 200, message = "VALID_0002")
	@Column(length = 200)
	@Comment("공통코드 설명")
	private String cmmnCdExpln;

	/***
	 * 공통코드 상위 분류 ID
	 */
	@JsonProperty("cmmn_cd_up_clsf_id")
	@Size(max = 20, message = "VALID_0002")
	@Column(length = 20)
	@Comment("공통코드 상위 분류 ID")
	private String cmmnCdUpClsfId;

	/***
	 * 공통코드 상위 ID
	 */
	@JsonProperty("cmmn_cd_up_id")
	@Size(max = 20, message = "VALID_0002")
	@Column(length = 20)
	@Comment("공통코드 상위 ID")
	private String cmmnCdUpId;

	/***
	 * 공통코드 사용자정의 값1
	 */
	@JsonProperty("cmmn_cd_user_dfn_vl1")
	@Size(max = 400, message = "VALID_0002")
	@Column(length = 400)
	@Comment("공통코드 사용자정의 값1")
	private String cmmnCdUserDfnVl1;

	/***
	 * 공통코드 사용자정의 값2
	 */
	@JsonProperty("cmmn_cd_user_dfn_vl2")
	@Size(max = 400, message = "VALID_0002")
	@Column(length = 400)
	@Comment("공통코드 사용자정의 값2")
	private String cmmnCdUserDfnVl2;

	/***
	 * 공통코드 사용자정의 값3
	 */
	@JsonProperty("cmmn_cd_user_dfn_vl3")
	@Size(max = 400, message = "VALID_0002")
	@Column(length = 400)
	@Comment("공통코드 사용자정의 값3")
	private String cmmnCdUserDfnVl3;

	/***
	 * 공통코드 정렬 번호
	 */
	@JsonProperty("cmmn_cd_sort_no")
	@Size(min = 1, max = 4, message = "VALID_0002")
	@Digits(integer = 4, fraction = 0, message = "VALID_0002")
	@Column(length = 4, nullable = false)
	@Comment("공통코드 정렬 번호")
	private int cmmnCdSortNo;
}