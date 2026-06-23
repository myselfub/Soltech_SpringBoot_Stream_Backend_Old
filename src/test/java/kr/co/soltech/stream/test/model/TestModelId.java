package kr.co.soltech.stream.test.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class TestModelId implements Serializable {
	/***
	 * 직렬화 UID
	 */
	private static final long serialVersionUID = 1L;

	private String dcNo;
	private String empNo;
}