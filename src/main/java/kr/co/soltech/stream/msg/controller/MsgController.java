package kr.co.soltech.stream.msg.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.soltech.stream.commons.model.CustomExceptionDTO;
import kr.co.soltech.stream.commons.service.CustomResponseAPI;
import kr.co.soltech.stream.msg.model.MsgModel;
import kr.co.soltech.stream.msg.model.MsgModelId;
import kr.co.soltech.stream.msg.model.MsgParamDTO;
import kr.co.soltech.stream.msg.service.MsgService;
import lombok.RequiredArgsConstructor;

/***
 * 메세지 컨트롤러 클래스
 */
@RestController
@RequestMapping("/msg")
@RequiredArgsConstructor
public class MsgController {
	/***
	 * 메세지 서비스 인터페이스
	 */
	private final MsgService msgService;

	/***
	 * 메세지 목록 조회
	 * 
	 * @param msgModelId    : 메세지 조회 파라메터 DTO 클래스
	 * @param pageable      : 페이징 정보
	 * @param bindingResult : 검증 결과
	 * @return 메세지 목록
	 * @throws Exception
	 */
	@GetMapping
	@CustomResponseAPI
	public Page<MsgModel> inqMsg(@ModelAttribute @Validated MsgParamDTO msgParamDTO, Pageable pageable,
			BindingResult bindingResult) throws Exception {
		return msgService.inqMsg(msgParamDTO, pageable);
	}

	/***
	 * 메세지 상세 조회
	 * 
	 * @param msgModelId    : 메세지 모델 ID 클래스
	 * @param bindingResult : 검증 결과
	 * @return 메세지 상세
	 * @throws Exception
	 */
	@GetMapping("/dtl")
	@CustomResponseAPI
	public MsgModel getMsg(@ModelAttribute @Validated MsgModelId msgModelId, BindingResult bindingResult)
			throws Exception {
		if (bindingResult.hasErrors()) {
			throw CustomExceptionDTO.of(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}

		return msgService.getMsg(msgModelId);
	}

	/***
	 * 메세지 등록/수정
	 * 
	 * @param msgModel      : 메세지 모델 클래스
	 * @param bindingResult : 검증 결과
	 * @return 성공/에러
	 * @throws Exception
	 */
	@PostMapping
	@PutMapping
	@PatchMapping
	@CustomResponseAPI(status = HttpStatus.CREATED)
	public String upsertMsg(@RequestBody @Validated MsgModel msgModel, BindingResult bindingResult) throws Exception {
		if (bindingResult.hasErrors()) {
			throw CustomExceptionDTO.of(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}
		msgModel.prePersist();
		int updatedRows = msgService.upsertMsg(msgModel);
		if (updatedRows == 0) {
			throw CustomExceptionDTO.of("ERROR_0000", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("No matching results."));
		}

		return "SUCCESS_0000";
	}

	/***
	 * 메세지 삭제
	 * 
	 * @param msgModelId    : 메세지 모델 ID 클래스
	 * @param bindingResult : 검증 결과
	 * @return 성공/에러
	 * @throws Exception
	 */
	@DeleteMapping
	@CustomResponseAPI(status = HttpStatus.NO_CONTENT)
	public String deleteMsg(@ModelAttribute @Validated MsgModelId msgModelId, BindingResult bindingResult)
			throws Exception {
		if (bindingResult.hasErrors()) {
			List<Map<String, String>> errorMsgList = new ArrayList<Map<String, String>>();
			bindingResult.getFieldErrors().forEach(err -> {
				Map<String, String> messages = new HashMap<String, String>();
				messages.put("message", err.getDefaultMessage());
				messages.put("{field}", err.getField());
				errorMsgList.add(messages);
			});
			throw new CustomExceptionDTO(errorMsgList, HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}

		MsgModel msgModel = MsgModel.builder().msgLang(msgModelId.getMsgLang()).msgClsf(msgModelId.getMsgClsf())
				.msgCd(msgModelId.getMsgCd()).build();
		msgModel.preUpdate();

		int updatedRows = msgService.deleteMsg(msgModel);
		if (updatedRows == 0) {
			throw CustomExceptionDTO.of("ERROR_0000", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("No matching results."));
		}

		return "SUCCESS_0000";
	}
}