package kr.co.soltech.stream.msg.model;

import org.hibernate.annotations.Comment;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.validation.constraints.Size;
import kr.co.soltech.stream.commons.model.BaseModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/***
 * 메세지 모델 클래스
 */
@Comment("메세지 테이블")
@Entity(name = "msg_table")
@IdClass(MsgModelId.class)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class MsgModel extends BaseModel {
	/***
	 * 직렬화 UID
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 메세지 언어
	 */
	@JsonProperty("msg_lang")
	@Size(min = 1, max = 5, message = "VALID_0002")
	@Id
	@Column(length = 5, nullable = false)
	@Comment("메세지 언어")
	private String msgLang;

	/***
	 * 메세지 분류
	 */
	@JsonProperty("msg_clsf")
	@Size(min = 1, max = 20, message = "VALID_0002")
	@Id
	@Column(length = 20, nullable = false)
	@Comment("메세지 분류")
	private String msgClsf;

	/***
	 * 메세지 코드
	 */
	@JsonProperty("msg_cd")
	@Size(min = 1, max = 20, message = "VALID_0002")
	@Id
	@Column(length = 20, nullable = false)
	@Comment("메세지 코드")
	private String msgCd;

	/***
	 * 메세지 명
	 */
	@JsonProperty("msg_nm")
	@Size(min = 1, max = 50, message = "VALID_0002")
	@Column(length = 50, nullable = false)
	@Comment("메세지 명")
	private String msgNm;

	/***
	 * 메세지 내용
	 */
	@JsonProperty("msg_cn")
	@Size(max = 400, message = "VALID_0002")
	@Column(length = 400)
	@Comment("메세지 내용")
	private String msgCn;

	/***
	 * 메세지 사용자정의 값1
	 */
	@JsonProperty("msg_user_dfn_vl1")
	@Size(max = 100, message = "VALID_0002")
	@Column(length = 100)
	@Comment("메세지 사용자정의 값1")
	private String msgUserDfnVl1;

	/***
	 * 메세지 사용자정의 값2
	 */
	@JsonProperty("msg_user_dfn_vl2")
	@Size(max = 100, message = "VALID_0002")
	@Column(length = 100)
	@Comment("메세지 사용자정의 값2")
	private String msgUserDfnVl2;

	/***
	 * 메세지 사용자정의 값3
	 */
	@JsonProperty("msg_user_dfn_vl3")
	@Size(max = 100, message = "VALID_0002")
	@Column(length = 100)
	@Comment("메세지 사용자정의 값3")
	private String msgUserDfnVl3;

	/***
	 * 메세지 사용자정의 값4
	 */
	@JsonProperty("msg_user_dfn_vl4")
	@Size(max = 100, message = "VALID_0002")
	@Column(length = 100)
	@Comment("메세지 사용자정의 값4")
	private String msgUserDfnVl4;
}