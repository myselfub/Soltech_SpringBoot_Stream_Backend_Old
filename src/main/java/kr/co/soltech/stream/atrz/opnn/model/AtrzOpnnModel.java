package kr.co.soltech.stream.atrz.opnn.model;

import org.hibernate.annotations.Comment;

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
 * 결재 의견 모델 클래스
 */
@Comment("결재 의견 테이블")
@Entity(name = "atrz_opnn_table")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class AtrzOpnnModel extends BaseModel {
	/***
	 * 직렬화 UID
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 의견 ID
	 */
	@JsonProperty("opnn_id")
	@Size(max = 20, message = "VALID_0002")
	@Id
	@Column(length = 20, nullable = false)
	@Comment("의견 ID")
	private String opnnId;

	/***
	 * 의견 상위 ID
	 */
	@JsonProperty("opnn_up_id")
	@Size(max = 20, message = "VALID_0002")
	@Column(length = 20)
	@Comment("의견 상위 ID")
	private String opnnUpId;

	/***
	 * 문서 번호
	 */
	@JsonProperty("doc_no")
	@Size(max = 10, message = "VALID_0002")
	@Column(length = 10, nullable = false)
	@Comment("문서 번호")
	private String docNo;

	/***
	 * 의견 내용
	 */
	@JsonProperty("opnn_cn")
	@Size(max = 400, message = "VALID_0002")
	@Column(length = 400, nullable = false)
	@Comment("의견 내용")
	private String opnnCn;

	/***
	 * 등록자 명
	 */
	@JsonProperty("rgtr_nm")
	@Transient
	private String rgtrNm;
}