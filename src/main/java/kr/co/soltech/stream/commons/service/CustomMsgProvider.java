package kr.co.soltech.stream.commons.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import kr.co.soltech.stream.msg.model.MsgModel;
import kr.co.soltech.stream.msg.service.MsgService;
import lombok.extern.slf4j.Slf4j;

/***
 * 커스텀 메세지 프로바이더
 */
@Slf4j
@Component
public class CustomMsgProvider {
	/***
	 * 메세지 서비스 인터페이스
	 */
	private final MsgService msgService;

	/***
	 * 메세지 기본 언어
	 */
	@Value("${soltech.stream.msg.language:ko-KR}")
	private String defaultLang;

	/***
	 * 메세지 업데이트 주기
	 */
	@Value("${soltech.stream.msg.interval:1800}")
	private long updateInterval;

	/***
	 * 메세지 언어별 맵
	 */
	private final Map<String, List<MsgModel>> msgListMap = new HashMap<String, List<MsgModel>>();

	/***
	 * 메세지 언어 목록
	 */
	private Set<String> langSet;

	/***
	 * 마지막 메세지 업데이트 시간
	 */
	private LocalDateTime msgLastUpdateDateTime = LocalDateTime.now();

	/**
	 * 사용자지정 업데이트 유무
	 */
	private boolean isUpdate = false;

	/***
	 * 생성자
	 * 
	 * @param msgService : 메세지 서비스 인터페이스
	 */
	public CustomMsgProvider(MsgService msgService) {
		this.msgService = msgService;
	}

	/***
	 * 메세지 업데이트 주기 변경
	 * 
	 * @param updateInterval : 메세지 업데이트 주기
	 */
	public void setUpdateInterval(long updateInterval) {
		this.updateInterval = updateInterval;
	}

	/***
	 * 메세지 업데이트
	 * 
	 * @param lang : 메세지 언어
	 * @return 메세지 언어
	 */
	private String updateMsg(String lang) {
		lang = parseLang(lang);
		try {
			Duration duration = Duration.between(msgLastUpdateDateTime, LocalDateTime.now());
			if (duration.getSeconds() >= updateInterval) {
				isUpdate = true;
			}

			if (ObjectUtils.isEmpty(langSet) || isUpdate) {
				langSet = msgService.inqDistinctMsgLang();
			}

			if (Objects.isNull(msgListMap.get(lang + "MsgList")) || isUpdate) {
				String[] msgClsfArr = { "SYSTEM", "SUCCESS", "ERROR", "VALID" };
				List<MsgModel> tempList = msgService.findByMsgLangAndMsgClsfIn(lang, Arrays.asList(msgClsfArr));
				msgListMap.put(lang + "MsgList", tempList);
			}

			if (Objects.isNull(msgListMap.get(lang + "FieldList")) || isUpdate) {
				String[] msgClsfArr = { "FIELD" };
				List<MsgModel> tempList = msgService.findByMsgLangAndMsgClsfIn(lang, Arrays.asList(msgClsfArr));
				msgListMap.put(lang + "FieldList", tempList);
			}

			if (isUpdate) {
				msgLastUpdateDateTime = LocalDateTime.now();
				isUpdate = false;
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return lang;
	}

	/***
	 * 메세지 언어 파싱
	 * 
	 * @param lang : 메세지 언어
	 * @return 메세지 언어
	 */
	public String parseLang(String lang) {
		try {
			if (Objects.isNull(langSet)) {
				langSet = msgService.inqDistinctMsgLang();
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		if (ObjectUtils.isEmpty(lang)) {
			lang = defaultLang;
		}
		if (lang == defaultLang && (defaultLang.length() < 2 || !langSet.contains(defaultLang.substring(0, 2)))) {
			return defaultLang;
		}

		String[] languageTags = lang.split(",");
		List<String[]> preferences = new ArrayList<String[]>();

		for (String tag : languageTags) {
			String[] parts = tag.split(";");
			String languagePart = parts[0].trim();
			double quality = 1.0;

			for (int idx = 1; idx < parts.length; idx++) {
				if (parts[idx].trim().startsWith("q=")) {
					try {
						quality = Double.parseDouble(parts[idx].trim().substring(2));
					} catch (NumberFormatException e) {
					}
					break;
				}
			}

			if (languagePart.length() >= 2) {
				String languageCode = languagePart.substring(0, 2).toLowerCase();
				if (langSet.contains(languageCode)) {
					preferences.add(new String[] { languageCode, String.valueOf(quality) });
				}
			}
		}
		preferences.sort((a, b) -> Double.compare(Double.parseDouble(b[1]), Double.parseDouble(a[1])));

		if (!preferences.isEmpty()) {
			lang = preferences.get(0)[0];
		} else {
			lang = parseLang(defaultLang);
		}

		return lang;
	}

	/***
	 * 사용자지정 업데이트 유무 설정
	 * 
	 * @param isUpdate : 사용자지정 업데이트 유무
	 */
	public void setIsUpdate(boolean isUpdate) {
		this.isUpdate = isUpdate;
	}

	/***
	 * 메세지 목록 가져오기
	 * 
	 * @param lang : 메세지 언어
	 * @return 메세지 목록
	 */
	public List<MsgModel> getMsgModelList(String lang) {
		lang = updateMsg(lang);

		return msgListMap.get(lang + "MsgList");
	}

	/***
	 * 메세지 상세 가져오기
	 * 
	 * @param lang : 메세지 언어
	 * @return 메세지 상세
	 */
	public MsgModel getMsgModel(String lang, String msgCd) {
		lang = updateMsg(lang);

		MsgModel nullModel = MsgModel.builder().msgCn(msgCd).build();
		if (ObjectUtils.isEmpty(msgCd) || ObjectUtils.isEmpty(msgListMap.get(lang + "MsgList"))) {
			return nullModel;
		}

		return msgListMap.get(lang + "MsgList").stream().filter(msgModel -> msgCd.equals(msgModel.getMsgCd()))
				.findFirst().orElse(nullModel);
	}

	/***
	 * 메세지 내용 가져오기
	 * 
	 * @param lang  : 메세지 언어
	 * @param msgCd : 메세지 코드
	 * @return 메세지 내용
	 */
	public String getMsgCn(String lang, String msgCd) {
		return getMsgModel(lang, msgCd).getMsgCn();
	}

	/***
	 * 메세지 필드 목록 가져오기
	 * 
	 * @param lang : 메세지 언어
	 * @return 메세지 필드 목록
	 */
	public List<MsgModel> getMsgModelFieldList(String lang) {
		lang = updateMsg(lang);

		return msgListMap.get(lang + "FieldList");
	}

	/***
	 * 메세지 필드 상세 가져오기
	 * 
	 * @param lang : 메세지 언어
	 * @return 메세지 필드 상세
	 */
	public MsgModel getMsgModelField(String lang, String msgCd) {
		lang = updateMsg(lang);

		MsgModel nullModel = MsgModel.builder().msgCn(msgCd).build();
		if (ObjectUtils.isEmpty(msgCd) || ObjectUtils.isEmpty(msgListMap.get(lang + "MsgList"))) {
			return nullModel;
		}

		return msgListMap.get(lang + "FieldList").stream().filter(msgModel -> msgCd.equals(msgModel.getMsgCd()))
				.findFirst().orElse(nullModel);
	}

	/***
	 * 메세지 필드 내용 가져오기
	 * 
	 * @param lang  : 메세지 언어
	 * @param msgCd : 메세지 코드
	 * @return 메세지 내용
	 */
	public String getMsgCnField(String lang, String msgCd) {
		return getMsgModelField(lang, msgCd).getMsgCn();
	}
}