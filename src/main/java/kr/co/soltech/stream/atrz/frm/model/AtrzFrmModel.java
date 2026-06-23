package kr.co.soltech.stream.atrz.frm.model;

import java.util.Map;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Size;
import kr.co.soltech.stream.commons.model.BaseModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/***
 * 결재 양식 모델 클래스
 */
@Comment("결재 양식 테이블")
@Entity(name = "atrz_frm_table")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class AtrzFrmModel extends BaseModel {
	/***
	 * 직렬화 UID
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 문서 구분 코드
	 */
	@JsonProperty("doc_se_cd")
	@Size(min = 1, max = 20, message = "VALID_0002")
	@Id
	@Column(length = 20, nullable = false)
	@Comment("문서 구분 코드")
	private String docSeCd;

	@JsonProperty("doc_se_nm")
	@Transient
	private String docSeNm;

	/***
	 * 결재 양식 데이터
	 */
	@JsonProperty("atrz_frm_data")
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(columnDefinition = "JSONB")
	@Comment("결재 양식 데이터")
	private Map<String, Object> atrzFrmData;
}