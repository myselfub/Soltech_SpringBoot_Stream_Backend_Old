package kr.co.soltech.stream.menu.model;

import org.hibernate.annotations.Comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import kr.co.soltech.stream.commons.model.BaseModel;
import kr.co.soltech.stream.commons.model.BaseSerializer;
import kr.co.soltech.stream.commons.model.CustomDateSerializer;
import kr.co.soltech.stream.commons.utils.SoltechStreamUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/***
 * 메뉴 모델 클래스
 */
@Comment("메뉴 테이블")
@Entity(name = "menu_table")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class MenuModel extends BaseModel implements BaseSerializer {
	/***
	 * 직렬화 UID
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 메뉴 ID
	 */
	@JsonProperty("menu_id")
	@Size(min = 1, max = 20, message = "VALID_0002")
	@Id
	@Column(length = 20, nullable = false)
	@Comment("메뉴 ID")
	private String menuId;

	/***
	 * 메뉴 명
	 */
	@JsonProperty("menu_nm")
	@Size(min = 1, max = 40, message = "VALID_0002")
	@Column(length = 40, nullable = false)
	@Comment("메뉴 명")
	private String menuNm;

	/***
	 * 메뉴 설명
	 */
	@JsonProperty("menu_expln")
	@Size(max = 200, message = "VALID_0002")
	@Column(length = 200)
	@Comment("메뉴 설명")
	private String menuExpln;

	/***
	 * 메뉴 URL
	 */
	@JsonProperty("menu_url")
	@Size(max = 100, message = "VALID_0002")
	@Column(length = 100)
	@Comment("메뉴 URL")
	private String menuUrl;

	/***
	 * 상위 메뉴 ID
	 */
	@JsonProperty("up_menu_id")
	@Size(max = 10, message = "VALID_0002")
	@Column(length = 10)
	@Comment("상위 메뉴 ID")
	private String upMenuId;

	/***
	 * 메뉴 깊이
	 */
	@JsonProperty("menu_dpth")
	@Digits(integer = 4, fraction = 0, message = "VALID_0002")
	@Column(length = 4)
	@Comment("메뉴 깊이")
	private int menuDpth;

	/***
	 * 메뉴 API 여부
	 */
	@JsonProperty("menu_api_yn")
	@Builder.Default
	@Column(columnDefinition = "CHAR(1) DEFAULT 'Y'", nullable = false)
	@Comment("메뉴 API 여부")
	private Character menuApiYn = 'Y';

	/***
	 * 메뉴 팝업 여부
	 */
	@JsonProperty("menu_popup_yn")
	@Builder.Default
	@Column(columnDefinition = "CHAR(1) DEFAULT 'N'", nullable = false)
	@Comment("메뉴 팝업 여부")
	private Character menuPopupYn = 'N';

	/***
	 * 메뉴 사용자정의 값
	 */
	@JsonProperty("menu_user_dfn_vl")
	@Size(max = 200, message = "VALID_0002")
	@Column(length = 200)
	@Comment("메뉴 사용자정의 값")
	private String menuUserDfnVl;

	/***
	 * 메뉴 시작 일자
	 */
	@JsonProperty("menu_bgng_ymd")
	@JsonSerialize(using = CustomDateSerializer.class)
	@Size(min = 8, max = 8, message = "VALID_0002")
	@Digits(integer = 8, fraction = 0, message = "VALID_0002")
	@Column(length = 8, nullable = false)
	@Comment("메뉴 시작 일자")
	private String menuBgngYmd;

	/***
	 * 메뉴 종료 일자
	 */
	@JsonProperty("menu_end_ymd")
	@JsonSerialize(using = CustomDateSerializer.class)
	@Size(min = 8, max = 8, message = "VALID_0002")
	@Digits(integer = 8, fraction = 0, message = "VALID_0002")
	@Column(length = 8, nullable = false)
	@Comment("메뉴 종료 일자")
	private String menuEndYmd;

	/***
	 * 메뉴 순번
	 */
	@JsonProperty("menu_sn")
	@Size(min = 1, max = 8, message = "VALID_0002")
	@Digits(integer = 8, fraction = 0, message = "VALID_0002")
	@Column(length = 8, nullable = false)
	@Comment("메뉴 순번")
	private String menuSn;

	/***
	 * 데이터베이스 작업전 날짜 포맷 변환
	 */
	@PrePersist
	@PreUpdate
	public void preModel() {
		this.menuBgngYmd = menuBgngYmd == null ? "" : SoltechStreamUtils.parseDateToString(menuBgngYmd, "yyyyMMdd");
		this.menuEndYmd = menuEndYmd == null ? "" : SoltechStreamUtils.parseDateToString(menuEndYmd, "yyyyMMdd");
	}

	/***
	 * 데이터를 클라이언트로 보내기 전 포맷 변경
	 */
	@Override
	public void serialize() {
		this.menuBgngYmd = menuBgngYmd == null ? "" : SoltechStreamUtils.parseDateToString(menuBgngYmd, "yyyy-MM-dd");
		this.menuEndYmd = menuEndYmd == null ? "" : SoltechStreamUtils.parseDateToString(menuEndYmd, "yyyy-MM-dd");
	}

	/***
	 * 데이터를 클라이언트에서 가져오고 포맷 변경
	 */
	@Override
	public void deserialize() {
		this.menuBgngYmd = menuBgngYmd == null ? "" : SoltechStreamUtils.parseDateToString(menuBgngYmd, "yyyyMMdd");
		this.menuEndYmd = menuEndYmd == null ? "" : SoltechStreamUtils.parseDateToString(menuEndYmd, "yyyyMMdd");
	}
}