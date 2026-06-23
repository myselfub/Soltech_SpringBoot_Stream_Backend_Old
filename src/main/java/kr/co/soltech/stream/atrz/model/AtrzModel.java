package kr.co.soltech.stream.atrz.model;

import java.util.List;
import java.util.Map;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import kr.co.soltech.stream.atrz.opnn.model.AtrzOpnnModel;
import kr.co.soltech.stream.commons.model.BaseModel;
import kr.co.soltech.stream.commons.model.BaseSerializer;
import kr.co.soltech.stream.commons.model.CustomDateSerializer;
import kr.co.soltech.stream.commons.utils.SoltechStreamUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/***
 * 결재 모델 클래스
 */
//@JsonInclude(JsonInclude.Include.NON_NULL)
@Comment("결재 테이블")
@Entity(name = "atrz_table")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class AtrzModel extends BaseModel implements BaseSerializer {
	/***
	 * 직렬화 UID
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 문서 번호
	 */
	@JsonProperty("doc_no")
	@Size(max = 10, message = "VALID_0002")
	@Id
	@Column(length = 10, nullable = false)
	@Comment("문서 번호")
	private String docNo;

	/***
	 * 문서 구분 코드
	 */
	@JsonProperty("doc_se_cd")
	@Size(min = 1, max = 20, message = "VALID_0002")
	@Column(length = 20, nullable = false)
	@Comment("문서 구분 코드")
	private String docSeCd;

	/***
	 * 문서 구분 명
	 */
	@JsonProperty("doc_se_nm")
	@Transient
	private String docSeNm;

	/***
	 * 기안 일자
	 */
	@JsonProperty("drft_ymd")
	@JsonSerialize(using = CustomDateSerializer.class)
	@Size(min = 8, max = 8, message = "VALID_0002")
	@Digits(integer = 8, fraction = 0, message = "VALID_0002")
	@Column(length = 8, nullable = false)
	@Comment("기안 일자")
	private String drftYmd;

	/***
	 * 기안자 ID
	 */
	@JsonProperty("drftr_id")
	@Size(min = 1, max = 20, message = "VALID_0002")
	@Column(length = 20, nullable = false)
	@Comment("기안자 아이디")
	private String drftrId;

	/***
	 * 기안자 명
	 */
	@JsonProperty("drftr_nm")
	@Transient
	private String drftrNm;

	/***
	 * 기안자 전체 부서 명
	 */
	@JsonProperty("drftr_whol_dept_nm")
	@Transient
	private String drftrWholDeptNm;

	/***
	 * 기안자 직급 구분 명
	 */
	@JsonProperty("drftr_jbgd_se_nm")
	@Transient
	private String drftrJbgdSeNm;

	/***
	 * 문서 제목
	 */
	@JsonProperty("doc_ttl")
	@Size(min = 1, max = 100, message = "VALID_0002")
	@Column(length = 100, nullable = false)
	@Comment("문서 제목")
	private String docTtl;

	/***
	 * 결재 데이터
	 * 
	 * { atrz_user1(결재자1): { date: "", emp_no: empNo }, atrz_user2(결재자2): { date:
	 * "", emp_no: empNo }, ... rfrnc_user1(참조자1): { date: "", emp_no: empNo }, ...
	 * cprt_user1(협조자1): { date: "", emp_no: empNo }, ... content1(내용): content }
	 */
	@JsonProperty("atrz_data")
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(columnDefinition = "JSONB")
	@Comment("결재 데이터")
	private Map<String, Object> atrzData;

	/***
	 * 결재 상태 구분 코드
	 */
	@JsonProperty("atrz_stts_se_cd")
	@Size(max = 20, message = "VALID_0002")
	@Column(length = 20, nullable = false)
	@Comment("결재 상태 구분 코드")
	private String atrzSttsSeCd;

	/***
	 * 결재 상태 구분 명
	 */
	@JsonProperty("atrz_stts_se_nm")
	@Transient
	private String atrzSttsSeNm;

	/***
	 * 결재자 ID
	 */
	@JsonProperty("aprvr_id")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Transient
	private String aprvrId;

	/***
	 * 결재자 명
	 */
	@JsonProperty("aprvr_nm")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Transient
	private String aprvrNm;

	/***
	 * 결재 일시
	 */
	@JsonProperty("atrz_dt")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Transient
	private String atrzDt;

	/***
	 * 결재 데이터 KEY
	 */
	@JsonProperty("atrz_data_key")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Transient
	private String atrzDataKey;

	/***
	 * 결재 의견 수
	 */
	@JsonProperty("atrz_opnn_cnt")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Transient
	private String atrzOpnnCnt;

	/***
	 * 결재 의견 수
	 */
	@JsonProperty("atrz_opnn_list")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Transient
	private List<AtrzOpnnModel> atrzOpnnList;

	/***
	 * 기안자 전체 부서 명
	 */
	@JsonProperty("file_yn")
	@Transient
	private String fileYn;

	/***
	 * 결재 첨부파일 목록
	 */
	@JsonIgnore
	@Transient
	private List<MultipartFile> multipartFileList;

	/***
	 * 데이터베이스 작업전 날짜 포맷 변환
	 */
	@PrePersist
	@PreUpdate
	public void preModel() {
		this.drftYmd = drftYmd == null ? "" : SoltechStreamUtils.parseDateToString(drftYmd, "yyyyMMdd");
		this.atrzData = SoltechStreamUtils.convertResultMap(atrzData, "yyyyMMdd", "HHmmss");
	}

	/***
	 * 데이터를 클라이언트로 보내기 전 포맷 변경
	 */
	@Override
	public void serialize() {
		this.drftYmd = drftYmd == null ? "" : SoltechStreamUtils.parseDateToString(drftYmd, "yyyy-MM-dd");
	}

	/***
	 * 데이터를 클라이언트에서 가져오고 포맷 변경
	 */
	@Override
	public void deserialize() {
		this.drftYmd = drftYmd == null ? "" : SoltechStreamUtils.parseDateToString(drftYmd, "yyyyMMdd");
	}
}