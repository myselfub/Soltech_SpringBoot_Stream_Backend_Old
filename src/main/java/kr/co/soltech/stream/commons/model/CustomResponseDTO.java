package kr.co.soltech.stream.commons.model;

import java.io.Serializable;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/***
 * 공통 응답 포맷 DTO
 */
@SuperBuilder
@NoArgsConstructor
@Getter
@ToString
public class CustomResponseDTO<D, M> implements Serializable {
	/**
	 * 직렬화 UID
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 정보
	 */
	private Map<String, Object> infos;

	/***
	 * 데이터
	 */
	private D datas;

	/***
	 * 메세지
	 */
	private M message;

	/***
	 * HTTP 상태 코드
	 */
	private int statusCode;

	/***
	 * 팩토리
	 * 
	 * @param <D>        : 데이터 타입
	 * @param <M>        : 메세지 타입
	 * @param infos      : 정보
	 * @param datas      : 데이터
	 * @param message    : 메세지
	 * @param statusCode : HTTP 상태 코드
	 * @return CustomResponseDTO
	 */
	public static <D, M> CustomResponseDTO<D, M> of(Map<String, Object> infos, D datas, M message, int statusCode) {
		return CustomResponseDTO.<D, M>builder().infos(infos).datas(datas).message(message).statusCode(statusCode)
				.build();
	}
}