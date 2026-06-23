package kr.co.soltech.stream.commons.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.persistence.NoResultException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.soltech.stream.commons.model.BaseSerializer;
import kr.co.soltech.stream.commons.model.CustomExceptionDTO;
import kr.co.soltech.stream.commons.model.CustomResponseDTO;
import kr.co.soltech.stream.commons.service.CustomMsgProvider;
import kr.co.soltech.stream.commons.service.CustomResponseAPI;
import kr.co.soltech.stream.commons.utils.SoltechStreamUtils;
import kr.co.soltech.stream.msg.model.MsgModel;
import lombok.extern.slf4j.Slf4j;

/***
 * 공통 응답 처리 핸들러 클래스
 */
@Slf4j
@RestControllerAdvice
public class CustomResponseHandler<T> implements ResponseBodyAdvice<T> {
	/***
	 * 커스텀 메세지 프로바이더
	 */
	private final CustomMsgProvider customMsgProvider;

	/***
	 * 개발모드 여부
	 */
	private final String isDevMode;

	/***
	 * 생성자
	 * 
	 * @param customMsgProvider : 커스텀 메세지 프로바이더
	 */
	public CustomResponseHandler(CustomMsgProvider customMsgProvider,
			@Value("${soltech.stream.is-dev-mode:false}") String isDevMode) {
		this.customMsgProvider = customMsgProvider;
		this.isDevMode = isDevMode;
	}

	/***
	 * 공통 에러 처리 DTO와 예상치 못한 에러에 대한 공통 에러응답 처리 메소드
	 * 
	 * @param e : 에러
	 * @return 공통 에러응답 처리 결과
	 */
	@ExceptionHandler({ Exception.class, CustomExceptionDTO.class })
	public ResponseEntity<CustomResponseDTO<Object, Object>> exceptionHandlerCustom(Exception e,
			HttpServletRequest request) {
		AtomicReference<String> lang = new AtomicReference<String>(null);
		if (request != null) {
			if (!ObjectUtils.isEmpty(request.getHeader("Accept-Language"))) {
				lang.set(request.getHeader("Accept-Language"));
			}
		}

		Object msg = null;
		int statusCode = HttpStatus.BAD_REQUEST.value();

		if (e instanceof CustomExceptionDTO) {
			CustomExceptionDTO customExceptionDTO = (CustomExceptionDTO) e;
			msg = customExceptionDTO.getMessages().stream().map(msgMap -> {
				AtomicReference<String> msgCn = new AtomicReference<String>(
						customMsgProvider.getMsgCn(lang.get(), msgMap.get("message")));

				Pattern pattern = Pattern.compile("\\{(\\d+)\\}");
				Matcher matcher = pattern.matcher(msgCn.get());
				int lastNum = -1;
				while (matcher.find()) {
					lastNum = Integer.parseInt(matcher.group(1));
				}
				int keySize = msgMap.keySet().size();
				keySize = msgCn.get().contains("{message}") ? keySize - 1 : keySize;
				keySize = msgCn.get().contains("{field}") ? keySize - 1 : keySize;

				if (keySize > lastNum) {
					msgMap.keySet().forEach(key -> {
						if (!"message".equals(key)) {
							String filedNm = msgMap.get(key);
							MsgModel msgModelField = customMsgProvider.getMsgModelField(lang.get(), filedNm);
							msgCn.set(msgCn.get().replace(key, msgModelField.getMsgCn()));
						}
					});
				} else {
					String filedNm = msgMap.get("{field}");
					MsgModel msgModelField = customMsgProvider.getMsgModelField(lang.get(), filedNm);
					Map<String, String> msgUserDfnVl = Map.of("{0}",
							Objects.requireNonNullElse(msgModelField.getMsgUserDfnVl1(), ""), "{1}",
							Objects.requireNonNullElse(msgModelField.getMsgUserDfnVl2(), ""), "{2}",
							Objects.requireNonNullElse(msgModelField.getMsgUserDfnVl3(), ""));
					msgCn.set(msgCn.get().replace("{field}", msgModelField.getMsgCn()));
					for (int idx = 0; idx < lastNum + 1; idx++) {
						String oldStr = "{" + idx + "}";
						String newStr = msgUserDfnVl.get(oldStr);
						msgCn.set(msgCn.get().replace(oldStr, ObjectUtils.isEmpty(newStr) ? "" : newStr));
					}
				}

				return msgCn.get();
			}).toList();
			statusCode = customExceptionDTO.getStatusCode();
		} else {
			if (ObjectUtils.isEmpty(isDevMode) || !"true".equals(isDevMode.toLowerCase())) {
				msg = customMsgProvider.getMsgCn(lang.get(), "ERROR_0001");
				log.error(e.getMessage());
			} else {
				msg = ObjectUtils.isEmpty(e.getMessage()) ? customMsgProvider.getMsgCn(lang.get(), "ERROR_0001")
						: e.getMessage();
				e.printStackTrace();
			}
		}

		return ResponseEntity.status(statusCode)
				.body(CustomResponseDTO.<Object, Object>of(null, null, msg, statusCode));
	}

	/***
	 * 예상 가능한 에러에 대한 공통 에러응답 처리 메소드
	 * 
	 * @param e : 에러
	 * @return 공통 에러응답 처리 결과
	 */
	@ExceptionHandler({ BadRequestException.class, InvalidDataAccessApiUsageException.class,
			MethodArgumentNotValidException.class, HandlerMethodValidationException.class, NoResultException.class,
			AuthenticationException.class, BadCredentialsException.class, UsernameNotFoundException.class,
			AccessDeniedException.class, MaxUploadSizeExceededException.class })
	public ResponseEntity<CustomResponseDTO<Object, Object>> exceptionHandler(Exception e, HttpServletRequest request) {
		final Object[] BAD_REQUEST = { BadRequestException.class, InvalidDataAccessApiUsageException.class,
				MethodArgumentNotValidException.class, HandlerMethodValidationException.class, NoResultException.class,
				MaxUploadSizeExceededException.class };
		final Object[] UNAUTHORIZED = { AuthenticationException.class, BadCredentialsException.class,
				UsernameNotFoundException.class };
		final Object[] FORBIDDEN = { AccessDeniedException.class };
		final Object[] NOT_FOUND = { NoResourceFoundException.class };

		String lang = null;
		if (request != null) {
			if (!ObjectUtils.isEmpty(request.getHeader("Accept-Language"))) {
				lang = request.getHeader("Accept-Language");
			}
		}

		Object msg = null;
		int statusCode = 0;

		statusCode = instanceofClass(BAD_REQUEST, e, HttpStatus.BAD_REQUEST.value(), statusCode);
		statusCode = instanceofClass(UNAUTHORIZED, e, HttpStatus.UNAUTHORIZED.value(), statusCode);
		statusCode = instanceofClass(FORBIDDEN, e, HttpStatus.FORBIDDEN.value(), statusCode);
		statusCode = instanceofClass(NOT_FOUND, e, HttpStatus.NOT_FOUND.value(), statusCode);
		statusCode = statusCode == 0 ? HttpStatus.BAD_REQUEST.value() : statusCode;

		if (ObjectUtils.isEmpty(isDevMode) || !"true".equals(isDevMode.toLowerCase())) {
			msg = customMsgProvider.getMsgCn(lang, "ERROR_0001");
			log.error(e.getMessage());
		} else {
			msg = ObjectUtils.isEmpty(e.getMessage()) ? customMsgProvider.getMsgCn(lang, "ERROR_0001") : e.getMessage();
			e.printStackTrace();
		}

		return ResponseEntity.status(statusCode).body(CustomResponseDTO.of(null, null, msg, statusCode));
	}

	/***
	 * 파라메터로 들어온 에러가 배열로 들어온 에러 목록에 포함 돼있는지 확인하고 포함 되있으면 HTTP 상태 코드를 변경하여 리턴
	 * 
	 * @param exceptionClasses  : 에러 배열
	 * @param targetException   : 포함 돼있는지 확인할 에러
	 * @param changeStatusCode  : 변경할 HTTP 상태 코드 값
	 * @param currentStatusCode : 현재 HTTP 상태 코드 값
	 * @return HTTP 상태 코드 값
	 */
	private int instanceofClass(Object[] exceptionClasses, Exception targetException, int changeStatusCode,
			int currentStatusCode) {
		if (currentStatusCode != 0) {
			for (Object clazz : exceptionClasses) {
				if (((Class<?>) clazz).isAssignableFrom(targetException.getClass())) {
					return changeStatusCode;
				}
			}
		}

		return currentStatusCode;
	}

	/***
	 * 공통 응답 메소드(beforeBodyWrite 메소드) 적용 여부에 대한 조건
	 */
	@Override
	public boolean supports(@NonNull MethodParameter returnType,
			@NonNull Class<? extends HttpMessageConverter<?>> converterType) {
		return returnType.hasMethodAnnotation(CustomResponseAPI.class);
	}

	/***
	 * 공통 응답 메소드
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object beforeBodyWrite(@Nullable Object body, @NonNull MethodParameter returnType,
			@NonNull MediaType selectedContentType, @NonNull Class selectedConverterType,
			@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {
		String lang = null;
		if (request != null) {
			if (!ObjectUtils.isEmpty(request.getHeaders().getFirst("Accept-Language"))) {
				lang = request.getHeaders().getFirst("Accept-Language");
			}
		}

		if (ObjectUtils.isEmpty(body)) {
			body = null;
		}

		CustomResponseAPI customResponseAPI = returnType.getMethodAnnotation(CustomResponseAPI.class);
		HttpStatus httpStatus = HttpStatus.OK;

		if (customResponseAPI != null) {
			httpStatus = Objects.requireNonNullElse(customResponseAPI.status(), HttpStatus.OK);
		} else if (response != null) {
			HttpServletResponse httpServletResponse = ((ServletServerHttpResponse) response).getServletResponse();
			if (httpServletResponse != null) {
				int statusCode = httpServletResponse.getStatus();
				httpStatus = Objects.requireNonNullElse(HttpStatus.resolve(statusCode), HttpStatus.OK);
			}
		}

		CustomResponseDTO customResponseDTO = null;
		Object result = null;
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		Map<String, Object> infos = new HashMap<String, Object>();

		if (body instanceof CustomResponseDTO) {
			customResponseDTO = CustomResponseDTO.of(((CustomResponseDTO) body).getInfos(),
					((CustomResponseDTO) body).getDatas(), customMsgProvider.getMsgCn(lang, "SUCCESS_0001"),
					httpStatus.value());
		} else {
			if (body instanceof String) {
				body = customMsgProvider.getMsgCn(lang, (String) body);
			} else if (body instanceof List) {
				List list = (List) body;
				infos.put("size", list.size());
				if (!ObjectUtils.isEmpty(list) && list.getFirst() instanceof BaseSerializer) {
					for (int idx = 0; idx < list.size(); idx++) {
						((BaseSerializer) list.get(idx)).serialize();
					}
				} else if (!ObjectUtils.isEmpty(list) && !(list.getFirst() instanceof BaseSerializer)) {
					List<Map<String, Object>> newList = new ArrayList<Map<String, Object>>();
					List<Map<String, Object>> convertList = objectMapper.convertValue(list,
							new TypeReference<List<Map<String, Object>>>() {
							});
					for (Map<String, Object> map : convertList) {
						newList.add(SoltechStreamUtils.convertResultMap(map, "yyyy-MM-dd", "HH:mm:ss"));
					}
					body = newList;
				}
			} else if (body instanceof BaseSerializer) {
				((BaseSerializer) body).serialize();
			} else if (body instanceof Page) {
				Page page = (Page) body;
				int number = page.getNumber();
				int size = page.getSize();
				long totalElements = page.getTotalElements();
				int totalPages = page.getTotalPages();
				infos.put("page", number);
				infos.put("total_page", totalPages);
				infos.put("size", size);
				infos.put("total_size", totalElements);
				infos.put("first", page.isFirst());
				infos.put("last", page.isLast());
				infos.put("empty", page.isEmpty());

				List list = (List) page.getContent();
				if (!ObjectUtils.isEmpty(list) && list.getFirst() instanceof BaseSerializer) {
					for (int idx = 0; idx < list.size(); idx++) {
						((BaseSerializer) list.get(idx)).serialize();
					}
				} else if (!ObjectUtils.isEmpty(list) && !(list.getFirst() instanceof BaseSerializer)) {
					List<Map<String, Object>> newList = new ArrayList<Map<String, Object>>();
					List<Map<String, Object>> convertList = objectMapper.convertValue(list,
							new TypeReference<List<Map<String, Object>>>() {
							});
					for (Map<String, Object> map : convertList) {
						newList.add(SoltechStreamUtils.convertResultMap(map, "yyyy-MM-dd", "HH:mm:ss"));
					}
					body = newList;
				}
				body = list;
			} else if (body instanceof Slice) {
				Slice slice = (Slice) body;
				int number = slice.getNumber();
				int size = slice.getSize();
				infos.put("page", number);
				infos.put("size", size);
				infos.put("first", slice.isFirst());
				infos.put("last", slice.isLast());
				infos.put("empty", slice.isEmpty());

				List list = (List) slice.getContent();
				if (!ObjectUtils.isEmpty(list) && list.getFirst() instanceof BaseSerializer) {
					for (int idx = 0; idx < list.size(); idx++) {
						((BaseSerializer) list.get(idx)).serialize();
					}
				} else if (!ObjectUtils.isEmpty(list) && !(list.getFirst() instanceof BaseSerializer)) {
					List<Map<String, Object>> newList = new ArrayList<Map<String, Object>>();
					List<Map<String, Object>> convertList = objectMapper.convertValue(list,
							new TypeReference<List<Map<String, Object>>>() {
							});
					for (Map<String, Object> map : convertList) {
						newList.add(SoltechStreamUtils.convertResultMap(map, "yyyy-MM-dd", "HH:mm:ss"));
					}
					body = newList;
				}
				body = list;
			} else {
				body = SoltechStreamUtils
						.convertResultMap(objectMapper.convertValue(body, new TypeReference<Map<String, Object>>() {
						}), "yyyy-MM-dd", "HH:mm:ss");
			}

			customResponseDTO = CustomResponseDTO.of(infos, body, customMsgProvider.getMsgCn(lang, "SUCCESS_0001"),
					httpStatus.value());
		}

		if (body == null || body instanceof String) {
			try {
				result = objectMapper.writeValueAsString(customResponseDTO);
			} catch (Exception e) {
				result = "";
				log.error(e.getMessage());
			}
			return result;
		}

		return customResponseDTO;
	}
}