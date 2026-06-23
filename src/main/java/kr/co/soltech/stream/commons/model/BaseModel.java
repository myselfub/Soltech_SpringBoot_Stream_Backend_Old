package kr.co.soltech.stream.commons.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/***
 * 기본 테이블 컬럼
 */
/*
 * @Table(name = "base_model", indexes = { @Index(name = "idx_reg_dt",
 * columnList = "reg_dt") })
 * 
 * @Setter
 * 
 * @Data
 */
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@Getter
@ToString
// TODO: ID 기본값 변경
public class BaseModel implements Serializable {
	/***
	 * 직렬화 UID
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 사용 여부
	 */
	@JsonProperty("use_yn")
	@Builder.Default
	@Column(columnDefinition = "CHAR(1) DEFAULT 'Y'", nullable = false)
	@Comment("사용 여부")
	private Character useYn = 'Y';

	/***
	 * 등록 일시
	 */
	// @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonProperty("reg_dt")
	@Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP::TIMESTAMP(0)")
	@Comment("등록 일시")
	@CreatedDate
	private LocalDateTime regDt;

	/***
	 * 등록자 ID
	 */
	@JsonProperty("rgtr_id")
	@Size(min = 1, max = 20, message = "VALID_0002")
	@Column(length = 20, nullable = false)
	@Comment("등록자 ID")
	@Builder.Default
	private String rgtrId = "TEST";

	/***
	 * 수정 일시
	 */
	@JsonProperty("mdfcn_dt")
	@Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP::TIMESTAMP(0)")
	@Comment("수정 일시")
	@LastModifiedDate
	private LocalDateTime mdfcnDt;

	/***
	 * 수정자 ID
	 */
	@JsonProperty("mdfr_id")
	@Size(min = 1, max = 20, message = "VALID_0002")
	@Column(length = 20, nullable = false)
	@Comment("수정자 ID")
	@Builder.Default
	private String mdfrId = "TEST";

	/***
	 * 데이터베이스 등록전 날짜 포맷 변환(등록일/수정일 자동 부여)
	 */
	@PrePersist
	public void prePersist() {
		if (!ObjectUtils.isEmpty(SecurityContextHolder.getContext())
				&& !ObjectUtils.isEmpty(SecurityContextHolder.getContext().getAuthentication())) {
			if (!ObjectUtils.isEmpty(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
					&& SecurityContextHolder.getContext().getAuthentication()
							.getPrincipal() instanceof CustomUserDetails) {
				CustomUserDetails customUserDetails = ((CustomUserDetails) SecurityContextHolder.getContext()
						.getAuthentication().getPrincipal());
				if (!ObjectUtils.isEmpty(customUserDetails) && !ObjectUtils.isEmpty(customUserDetails.getUserModel())) {
					this.rgtrId = customUserDetails.getUserModel().getUserId();
					this.mdfrId = customUserDetails.getUserModel().getUserId();
				}
			}
		}
		this.regDt = regDt != null ? regDt.truncatedTo(ChronoUnit.SECONDS)
				: LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
		this.mdfcnDt = mdfcnDt != null ? mdfcnDt.truncatedTo(ChronoUnit.SECONDS)
				: LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
	}

	/***
	 * 데이터베이스 업데이트전 날짜 포맷 변환(수정일 자동 부여)
	 */
	@PreUpdate
	public void preUpdate() {
		if (!ObjectUtils.isEmpty(SecurityContextHolder.getContext())
				&& !ObjectUtils.isEmpty(SecurityContextHolder.getContext().getAuthentication())) {
			if (!ObjectUtils.isEmpty(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
					&& SecurityContextHolder.getContext().getAuthentication()
							.getPrincipal() instanceof CustomUserDetails) {
				CustomUserDetails customUserDetails = ((CustomUserDetails) SecurityContextHolder.getContext()
						.getAuthentication().getPrincipal());
				if (!ObjectUtils.isEmpty(customUserDetails) && !ObjectUtils.isEmpty(customUserDetails.getUserModel())) {
					this.mdfrId = customUserDetails.getUserModel().getUserId();
				}
			}
		}
		this.mdfcnDt = mdfcnDt != null ? mdfcnDt.truncatedTo(ChronoUnit.SECONDS)
				: LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
	}
}