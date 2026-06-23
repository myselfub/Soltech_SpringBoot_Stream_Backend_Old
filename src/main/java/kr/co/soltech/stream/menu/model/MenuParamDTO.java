package kr.co.soltech.stream.menu.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/***
 * 메뉴 조회 파라메터 DTO 클래스
 */
@NoArgsConstructor
@Getter
@ToString
public class MenuParamDTO implements Serializable {
	/**
	 * 직렬화 UID
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 메뉴 ID
	 */
	@JsonProperty("menu_id")
	private String menuId;

	/***
	 * 메뉴 명
	 */
	@JsonProperty("menu_nm")
	private String menuNm;

	/***
	 * 메뉴 URL
	 */
	@JsonProperty("menu_url")
	private String menuUrl;

	/***
	 * 메뉴 API 여부
	 */
	@JsonProperty("menu_api_yn")
	private Character menuApiYn;

	/***
	 * 메뉴 팝업 여부
	 */
	@JsonProperty("menu_popup_yn")
	private Character menuPopupYn = 'N';

	/***
	 * menuId 스네이크케이스 Setter
	 * 
	 * @param menu_id : 메뉴 ID
	 */
	public void setMenu_id(String menu_id) {
		this.menuId = menu_id;
	}

	/***
	 * menuNm 스네이크케이스 Setter
	 * 
	 * @param menu_nm : 메뉴 명
	 */
	public void setMenu_nm(String menu_nm) {
		this.menuNm = menu_nm;
	}

	/***
	 * menuUrl 스네이크케이스 Setter
	 * 
	 * @param menu_url : 메뉴 URL
	 */
	public void setMenu_url(String menu_url) {
		this.menuUrl = menu_url;
	}

	/***
	 * menuApiYn 스네이크케이스 Setter
	 * 
	 * @param menu_api_yn : 메뉴 API 여부
	 */
	public void setMenu_api_yn(Character menu_api_yn) {
		this.menuApiYn = menu_api_yn;
	}

	/***
	 * menuPopupYn 스네이크케이스 Setter
	 * 
	 * @param menu_popup_yn : 메뉴 팝업 여부
	 */
	public void setMenu_popup_yn(Character menu_popup_yn) {
		this.menuPopupYn = menu_popup_yn;
	}
}