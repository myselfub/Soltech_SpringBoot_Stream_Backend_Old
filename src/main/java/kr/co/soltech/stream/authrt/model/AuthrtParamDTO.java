package kr.co.soltech.stream.authrt.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import kr.co.soltech.stream.commons.model.BaseSerializer;
import kr.co.soltech.stream.commons.utils.SoltechStreamUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/***
 * 권한 조회 파라메터 DTO 클래스
 */
@NoArgsConstructor
@Getter
@ToString
public class AuthrtParamDTO implements BaseSerializer, Serializable {
	/***
	 * 직렬화 UID
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 권한 구분 코드
	 */
	@JsonProperty("authrt_se_cd")
	private String authrtSeCd;

	/***
	 * 메뉴 ID
	 */
	@JsonProperty("menu_id")
	private String menuId;

	/***
	 * 권한 시작 일자
	 */
	@JsonProperty("authrt_bgng_ymd")
	private String authrtBgngYmd;

	/***
	 * 권한 종료 일자
	 */
	@JsonProperty("authrt_end_ymd")
	private String authrtEndYmd;

	/***
	 * 메소드 리스트
	 */
	@JsonProperty("mthd_list")
	private String mthdList;

	/***
	 * authrtSeCd 스네이크케이스 Setter
	 * 
	 * @param authrt_se_cd : 권한 구분 코드
	 */
	public void setAuthrt_se_cd(String authrt_se_cd) {
		this.authrtSeCd = authrt_se_cd;
	}

	/***
	 * menuId 스네이크케이스 Setter
	 * 
	 * @param menu_id : 메뉴 ID
	 */
	public void setMenu_id(String menu_id) {
		this.menuId = menu_id;
	}

	/***
	 * authrtBgngYmd 스네이크케이스 Setter
	 * 
	 * @param authrt_bgng_ymd : 권한 시작 일자
	 */
	public void setAuthrt_bgng_ymd(String authrt_bgng_ymd) {
		this.authrtBgngYmd = authrt_bgng_ymd;
	}

	/***
	 * authrtEndYmd 스네이크케이스 Setter
	 * 
	 * @param authrt_end_ymd : 권한 종료 일자
	 */
	public void setAuthrt_end_ymd(String authrt_end_ymd) {
		this.authrtEndYmd = authrt_end_ymd;
	}

	/***
	 * mthdList 스네이크케이스 Setter
	 * 
	 * @param mthd_list : 메소드 리스트
	 */
	public void setMthd_list(String mthd_list) {
		this.mthdList = mthd_list;
	}

	/***
	 * 데이터를 클라이언트로 보내기 전 포맷 변경
	 */
	@Override
	public void serialize() {
		this.authrtBgngYmd = authrtBgngYmd == null ? ""
				: SoltechStreamUtils.parseDateToString(authrtBgngYmd, "yyyyMMdd");
		this.authrtEndYmd = authrtEndYmd == null ? "" : SoltechStreamUtils.parseDateToString(authrtEndYmd, "yyyyMMdd");
	}

	/***
	 * 데이터를 클라이언트에서 가져오고 포맷 변경
	 */
	@Override
	public void deserialize() {
		this.authrtBgngYmd = authrtBgngYmd == null ? ""
				: SoltechStreamUtils.parseDateToString(authrtBgngYmd, "yyyy-MM-dd");
		this.authrtEndYmd = authrtEndYmd == null ? ""
				: SoltechStreamUtils.parseDateToString(authrtEndYmd, "yyyy-MM-dd");
	}
}