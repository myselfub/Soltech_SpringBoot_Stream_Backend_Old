package kr.co.soltech.stream.file.model;

import java.math.BigInteger;

import org.hibernate.annotations.Comment;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import kr.co.soltech.stream.commons.model.BaseModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/***
 * 파일 모델 클래스
 */
@Comment("파일 테이블")
@Entity(name = "file_table")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class FileModel extends BaseModel {
	/***
	 * 직렬화 UID
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 파일 ID
	 */
	@JsonProperty("file_id")
	@Size(max = 20, message = "VALID_0002")
	@Id
	@Column(length = 20, nullable = false)
	@Comment("파일 ID")
	private String fileId;

	/***
	 * 도메인 분류 ID
	 */
	@JsonProperty("dmn_clsf_id")
	@Size(max = 20, message = "VALID_0002")
	@Column(length = 20, nullable = false)
	@Comment("도메인 분류 ID")
	private String dmnClsfId;

	/***
	 * 파일 명
	 */
	@JsonProperty("file_nm")
	@Size(min = 1, max = 100, message = "VALID_0002")
	@Column(length = 100, nullable = false)
	@Comment("파일 명")
	private String fileNm;

	/***
	 * 파일 경로
	 */
	@JsonProperty("file_path")
	@Size(min = 1, max = 512, message = "VALID_0002")
	@Column(length = 512, nullable = false)
	@Comment("파일 경로")
	private String filePath;

	/***
	 * 원파일 명
	 */
	@JsonProperty("orgnfl_nm")
	@Size(min = 1, max = 100, message = "VALID_0002")
	@Column(length = 100, nullable = false)
	@Comment("원파일 명")
	private String orgnflNm;

	/***
	 * 파일 확장자 명
	 */
	@JsonProperty("file_extn_nm")
	@Size(max = 20, message = "VALID_0002")
	@Column(length = 20)
	@Comment("파일 확장자 명")
	private String fileExtnNm;

	/***
	 * 파일 크기
	 */
	@JsonProperty("file_sz")
	@Digits(integer = 13, fraction = 0, message = "VALID_0002")
	@Column(precision = 13, scale = 0)
	@Comment("파일 크기")
	private BigInteger fileSz;

	/***
	 * 파일 MIME 유형
	 */
	@JsonProperty("file_mime_type")
	@Size(max = 100, message = "VALID_0002")
	@Column(length = 100)
	@Comment("파일 MIME 유형")
	private String fileMimeType;

	/***
	 * 등록자 명
	 */
	@JsonProperty("rgtr_nm")
	@Transient
	private String rgtrNm;
}