package kr.co.soltech.stream.authrt.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/***
 * 권한 모델 ID 클래스
 */
@SuperBuilder
@NoArgsConstructor
@Getter
@ToString
public class AuthrtModelId implements Serializable {
	/**
	 * 직렬화 UID
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 권한 구분 코드
	 */
	@JsonProperty("authrt_se_cd")
	@Size(min = 1, max = 20, message = "VALID_0002")
	private String authrtSeCd;

	/***
	 * 메뉴 ID
	 */
	@JsonProperty("menu_id")
	@Size(min = 1, max = 20, message = "VALID_0002")
	private String menuId;

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
}