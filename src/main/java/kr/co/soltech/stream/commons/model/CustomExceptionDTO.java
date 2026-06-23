package kr.co.soltech.stream.commons.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

import lombok.Data;
import lombok.EqualsAndHashCode;

/***
 * 공통 에러 처리 DTO
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CustomExceptionDTO extends Exception {
	/***
	 * 직렬화 UID
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 메세지 목록
	 */
	private List<Map<String, String>> messages;

	/**
	 * HTTP 상태 코드
	 */
	private int statusCode = HttpStatus.BAD_REQUEST.value();

	/***
	 * 발생한 예외
	 */
	private Exception exception;

	/***
	 * 생성자
	 */
	public CustomExceptionDTO() {
	}

	/***
	 * 생성자(오버로딩)
	 * 
	 * @param messages : 메세지 목록
	 */
	public CustomExceptionDTO(List<Map<String, String>> messages) {
		this.messages = messages;
	}

	/***
	 * 생성자(오버로딩)
	 * 
	 * @param messages  : 메세지 목록
	 * @param exception : 발생한 예외
	 */
	public CustomExceptionDTO(List<Map<String, String>> messages, Exception exception) {
		this.messages = messages;
		this.exception = exception;
	}

	/***
	 * 생성자(오버로딩)
	 * 
	 * @param messages   : 메세지 목록
	 * @param statusCode : HTTP 상태 코드
	 */
	public CustomExceptionDTO(List<Map<String, String>> messages, int statusCode) {
		this.messages = messages;
		this.statusCode = statusCode;
	}

	/***
	 * 생성자(오버로딩)
	 * 
	 * @param messages   : 메세지 목록
	 * @param statusCode : HTTP 상태 코드
	 * @param exception  : 발생한 예외
	 */
	public CustomExceptionDTO(List<Map<String, String>> messages, int statusCode, Exception exception) {
		this.messages = messages;
		this.statusCode = statusCode;
		this.exception = exception;
	}

	/***
	 * 문자로 메세지 목록 자동 생성(오버로딩)
	 * 
	 * @param message : 메세지
	 * @return 생성된 공통 에러 처리 DTO
	 */
	public static CustomExceptionDTO of(String message) {
		return new CustomExceptionDTO(List.of(Map.of("message", message)));
	}

	/***
	 * 문자로 메세지 목록 자동 생성(오버로딩)
	 * 
	 * @param message   : 메세지
	 * @param exception : 발생한 예외
	 * @return 생성된 공통 에러 처리 DTO
	 */
	public static CustomExceptionDTO of(String message, Exception exception) {
		return new CustomExceptionDTO(List.of(Map.of("message", message)), exception);
	}

	/***
	 * 문자로 메세지 목록 자동 생성(오버로딩)
	 * 
	 * @param message    : 메세지
	 * @param statusCode : HTTP 상태 코드
	 * @return 생성된 공통 에러 처리 DTO
	 */
	public static CustomExceptionDTO of(String message, int statusCode) {
		return new CustomExceptionDTO(List.of(Map.of("message", message)), statusCode);
	}

	/***
	 * 문자로 메세지 목록 자동 생성(오버로딩)
	 * 
	 * @param message    : 메세지
	 * @param statusCode : HTTP 상태 코드
	 * @param exception  : 발생한 예외
	 * @return 생성된 공통 에러 처리 DTO
	 */
	public static CustomExceptionDTO of(String message, int statusCode, Exception exception) {
		return new CustomExceptionDTO(List.of(Map.of("message", message)), statusCode, exception);
	}

	/***
	 * 문자로 메세지 목록 자동 생성(오버로딩)
	 * 
	 * @param message : 메세지
	 * @param param   : 메세지안에 들어갈 파라메터
	 * @return 생성된 공통 에러 처리 DTO
	 */
	public static CustomExceptionDTO of(String message, String param) {
		return new CustomExceptionDTO(List.of(Map.of("message", message, "{field}", param)));
	}

	/***
	 * 문자로 메세지 목록 자동 생성(오버로딩)
	 * 
	 * @param message   : 메세지
	 * @param param     : 메세지안에 들어갈 파라메터
	 * @param exception : 발생한 예외
	 * @return 생성된 공통 에러 처리 DTO
	 */
	public static CustomExceptionDTO of(String message, String param, Exception exception) {
		return new CustomExceptionDTO(List.of(Map.of("message", message, "{field}", param)), exception);
	}

	/***
	 * 문자로 메세지 목록 자동 생성(오버로딩)
	 * 
	 * @param message    : 메세지
	 * @param param      : 메세지안에 들어갈 파라메터
	 * @param statusCode : HTTP 상태 코드
	 * @return 생성된 공통 에러 처리 DTO
	 */
	public static CustomExceptionDTO of(String message, String param, int statusCode) {
		return new CustomExceptionDTO(List.of(Map.of("message", message, "{field}", param)), statusCode);
	}

	/***
	 * 문자로 메세지 목록 자동 생성(오버로딩)
	 * 
	 * @param message    : 메세지
	 * @param param      : 메세지안에 들어갈 파라메터
	 * @param statusCode : HTTP 상태 코드
	 * @param exception  : 발생한 예외
	 * @return 생성된 공통 에러 처리 DTO
	 */
	public static CustomExceptionDTO of(String message, String param, int statusCode, Exception exception) {
		return new CustomExceptionDTO(List.of(Map.of("message", message, "{field}", param)), statusCode, exception);
	}

	/***
	 * FieldErrors로 메세지 목록 자동 생성(오버로딩)
	 * 
	 * @param fieldErrors : 필드에러 목록(bindingResult.getFieldErrors())
	 * @param statusCode  : HTTP 상태 코드
	 * @return 생성된 공통 에러 처리 DTO
	 */
	public static CustomExceptionDTO of(List<FieldError> fieldErrors, int statusCode) {
		List<Map<String, String>> errorMsgList = new ArrayList<Map<String, String>>();
		fieldErrors.forEach(err -> {
			Map<String, String> messages = new HashMap<String, String>();
			messages.put("message", err.getDefaultMessage());
			messages.put("{field}", err.getField());
			errorMsgList.add(messages);
		});

		return new CustomExceptionDTO(errorMsgList, statusCode);
	}

	/***
	 * FieldErrors로 메세지 목록 자동 생성(오버로딩)
	 * 
	 * @param fieldErrors : 필드에러 목록(bindingResult.getFieldErrors())
	 * @param statusCode  : HTTP 상태 코드
	 * @param exception   : 발생한 예외
	 * @return 생성된 공통 에러 처리 DTO
	 */
	public static CustomExceptionDTO of(List<FieldError> fieldErrors, int statusCode, Exception exception) {
		List<Map<String, String>> errorMsgList = new ArrayList<Map<String, String>>();
		fieldErrors.forEach(err -> {
			Map<String, String> messages = new HashMap<String, String>();
			messages.put("message", err.getDefaultMessage());
			messages.put("{field}", err.getField());
			errorMsgList.add(messages);
		});

		return new CustomExceptionDTO(errorMsgList, statusCode, exception);
	}

	/***
	 * String으로 메세지 생성
	 * 
	 * @param errorStr : 들어오는 수만큼 메세지에 바인딩할 파라메터로 생성
	 * @return 메세지
	 */
	public static Map<String, String> createMessages(String... errorStr) {
		Map<String, String> messages = new HashMap<String, String>();
		int errLength = errorStr.length;
		for (int idx = 0; idx < errLength; idx++) {
			if (idx == 0) {
				messages.put("message", errorStr[idx]);
			} else if (idx == 1) {
				messages.put("{field}", errorStr[idx]);
			} else {
				messages.put("{" + (idx - 1) + "}", errorStr[idx]);
			}
		}
		return messages;
	}

	/***
	 * FieldErrors로 메세지 목록 생성
	 * 
	 * @param fieldErrors : 필드에러 목록(bindingResult.getFieldErrors())
	 * @return 메세지 목록
	 */
	public static List<Map<String, String>> createMessages(List<FieldError> fieldErrors) {
		List<Map<String, String>> errorMsgList = new ArrayList<Map<String, String>>();
		fieldErrors.forEach(err -> {
			Map<String, String> messages = new HashMap<String, String>();
			messages.put("message", err.getDefaultMessage());
			messages.put("{field}", err.getField());
			errorMsgList.add(messages);
		});
		return errorMsgList;
	}
}