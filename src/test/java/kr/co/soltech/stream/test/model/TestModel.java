package kr.co.soltech.stream.test.model;

import org.hibernate.annotations.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;

/*
@Entity
@IdClass(TestModelId.class)
@Table(name = "test", indexes = { @Index(name = "idx_test", columnList = "dc_no") })
 */
@Data
public class TestModel {
	@Id
	@Column(name = "dcNo", length = 9)
	@Comment("문서번호")
	private String dcNo;

	@Id
	@Column(length = 10)
	@Comment("사원번호")
	private String empNo;

	@Comment("결재여부")
	@Column(columnDefinition = "CHAR(1) DEFAULT 'Y'")
	private char appYn;

	@Comment("순서")
	private int appOrder;
}