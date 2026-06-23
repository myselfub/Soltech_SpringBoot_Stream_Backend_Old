package kr.co.soltech.stream.file.model;

import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import kr.co.soltech.stream.commons.model.BaseModel;
import kr.co.soltech.stream.commons.model.BaseSerializer;
import kr.co.soltech.stream.commons.utils.SoltechStreamUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/***
 * 파일 조회 파라메터 DTO 클래스
 */
@NoArgsConstructor
@Getter
@ToString
public class FileParamDTO extends BaseModel implements BaseSerializer {
	/***
	 * 직렬화 UID
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 파일 ID
	 */
	@JsonProperty("file_id")
	@Size(max = 20, message = "VALID_0002")
	private String fileId;

	/***
	 * 도메인 분류 ID
	 */
	@JsonProperty("dmn_clsf_id")
	@Size(max = 20, message = "VALID_0002")
	private String dmnClsfId;

	/***
	 * 파일 명
	 */
	@JsonProperty("file_nm")
	@Size(max = 100, message = "VALID_0002")
	private String fileNm;

	/***
	 * 파일 확장자 명
	 */
	@JsonProperty("file_extn_nm")
	@Size(max = 20, message = "VALID_0002")
	private String fileExtnNm;

	/***
	 * 파일 크기
	 */
	@JsonProperty("file_sz")
	@Digits(integer = 13, fraction = 0, message = "VALID_0002")
	private BigInteger fileSz;

	/***
	 * 등록 일자
	 */
	@JsonProperty("reg_ymd")
	@Size(max = 10, message = "VALID_0002")
	private String regYmd;

	/***
	 * fileId 스네이크케이스 Setter
	 * 
	 * @param file_id : 파일 ID
	 */
	public void setFile_id(String file_id) {
		this.fileId = file_id;
	}

	/***
	 * dmnClsfId 스네이크케이스 Setter
	 * 
	 * @param dmn_clsf_id : 메뉴 ID
	 */
	public void setDmn_clsf_id(String dmn_clsf_id) {
		this.dmnClsfId = dmn_clsf_id;
	}

	/***
	 * fileNm 스네이크케이스 Setter
	 * 
	 * @param file_nm : 파일 명
	 */
	public void setFile_nm(String file_nm) {
		this.fileNm = file_nm;
	}

	/***
	 * fileExtnNm 스네이크케이스 Setter
	 * 
	 * @param file_extn_nm : 파일 확장자 명
	 */
	public void setFile_extn_nm(String file_extn_nm) {
		this.fileExtnNm = file_extn_nm;
	}

	/***
	 * fileSz 스네이크케이스 Setter
	 * 
	 * @param file_sz : 파일 크기
	 */
	public void setFile_sz(BigInteger file_sz) {
		this.fileSz = file_sz;
	}

	/***
	 * regYmd 스네이크케이스 Setter
	 * 
	 * @param reg_ymd : 등록 일자
	 */
	public void setReg_ymd(String reg_ymd) {
		this.regYmd = reg_ymd;
	}

	/***
	 * 직렬화(데이터를 클라이언트로 보내기 전 포맷 변경)
	 */
	@Override
	public void serialize() {
		this.regYmd = regYmd == null ? "" : SoltechStreamUtils.parseDateToString(regYmd, "yyyy-MM-dd");
	}

	/***
	 * 역직렬화(데이터를 클라이언트에서 가져오고 포맷 변경)
	 */
	@Override
	public void deserialize() {
		this.regYmd = regYmd == null ? "" : SoltechStreamUtils.parseDateToString(regYmd, "yyyyMMdd");
	}
}