package kr.co.soltech.stream.commons.configs;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.ObjectUtils;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.soltech.stream.hldy.model.HldyModel;
import kr.co.soltech.stream.hldy.service.HldyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/***
 * 스케쥴러 관련 설정 클래스
 */
@Slf4j
@EnableScheduling
@Configuration
@RequiredArgsConstructor
public class SchedulingConfig {
	/***
	 * 공공데이터포털 서비스키
	 */
	private String serviceKey;

	/***
	 * 휴일 서비스 인터페이스
	 */
	private final HldyService hldyService;

	/***
	 * 휴일 API 마지막 실행 시각
	 */
	private LocalDateTime lastHldyApiDateTime = null;

	/***
	 * 조회 년도(파라메터 전달용)
	 */
	private String solYear = null;

	/***
	 * lastHldyApiDateTime Getter
	 * 
	 * @return lastHldyApiDateTime
	 */
	public LocalDateTime getLastHldyApiDateTime() {
		return lastHldyApiDateTime;
	}

	/***
	 * 매년 1월 1일 00시 00분에 공공데이터 포털-한국천문연구원_특일 정보-공휴일 정보 조회 API를 조회하여 휴일 테이블에 삽입 스케쥴러
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@CacheEvict(value = { "inqHldy", "hldyCnt" }, allEntries = true)
	@Scheduled(cron = "0 0 0 1 1 *")
	public void hldyApiScheduling() throws Exception {
		if (ObjectUtils.isEmpty(solYear)) {
			solYear = String.valueOf(LocalDate.now().getYear());
		}
		String baseUrl = "https://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo";
		// TODO: 외부로 빼기
		serviceKey = "[PUBLIC_DATA_API_KEY]";

		String requestUrl = UriComponentsBuilder.fromHttpUrl(baseUrl)
				.queryParam("serviceKey", URLEncoder.encode(serviceKey, "UTF-8")).queryParam("_type", "json")
				.queryParam("numOfRows", "100").queryParam("solYear", solYear).build(false).toUriString();
		solYear = null;

		URI uri = new URI(requestUrl);
		HttpURLConnection httpUrlConnection = (HttpURLConnection) uri.toURL().openConnection();
		httpUrlConnection.setRequestMethod("GET");
		httpUrlConnection.setRequestProperty("Content-type", "application/json");
		httpUrlConnection.setRequestProperty("Accept-Language", "ko-KR,ko");

		BufferedReader bufferedReader = null;
		if (httpUrlConnection.getResponseCode() >= 200 && httpUrlConnection.getResponseCode() <= 300) {
			bufferedReader = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream()));
		} else {
			bufferedReader = new BufferedReader(new InputStreamReader(httpUrlConnection.getErrorStream()));
		}
		StringBuilder stringBuilder = new StringBuilder();
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			stringBuilder.append(line);
		}
		bufferedReader.close();
		httpUrlConnection.disconnect();

		List<HldyModel> hldyModelList = new ArrayList<HldyModel>();
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> apiResponse = objectMapper.readValue(stringBuilder.toString(), Map.class);
		if (!ObjectUtils.isEmpty(apiResponse)) {
			Object responseObject = apiResponse.get("response");
			if (!ObjectUtils.isEmpty(responseObject) && responseObject instanceof Map) {
				Map<String, Object> responseMap = (Map<String, Object>) responseObject;
				Object bodyObject = responseMap.get("body");
				if (!ObjectUtils.isEmpty(bodyObject) && bodyObject instanceof Map) {
					Map<String, Object> bodyMap = (Map<String, Object>) bodyObject;
					Object itemsObject = bodyMap.get("items");
					if (!ObjectUtils.isEmpty(itemsObject) && itemsObject instanceof Map) {
						Map<String, Object> itemsMap = (Map<String, Object>) itemsObject;
						Object itemObject = itemsMap.get("item");
						if (!ObjectUtils.isEmpty(itemObject) && itemObject instanceof List) {
							List<Object> itemList = (List<Object>) itemObject;
							if (!ObjectUtils.isEmpty(itemList) && itemList.get(0) instanceof Map) {
								List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
								for (Object dataObject : itemList) {
									dataList.add((Map<String, Object>) dataObject);
								}
								for (Map<String, Object> data : dataList) {
									if ("Y".equals(data.getOrDefault("isHoliday", "N"))) {
										String hldyYmd = String.valueOf(data.get("locdate"));
										int hldyYmdSn = Integer.parseInt(String.valueOf(data.getOrDefault("seq", "1")));
										String hldyNm = String.valueOf(data.get("dateName"));
										HldyModel hldyModel = HldyModel.builder().hldyYmd(hldyYmd).hldyYmdSn(hldyYmdSn)
												.hldyNm(hldyNm).build();
										hldyModel.deserialize();
										hldyModel.preModel();
										hldyModel.prePersist();
										hldyModel = hldyModel.toBuilder().rgtrId("System").mdfrId("System").build();
										hldyModelList.add(hldyModel);
									}
								}
							}
						}
					}
				}
			}
		}
		hldyService.upsertAllHldy(hldyModelList);
		lastHldyApiDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
	}

	/***
	 * hldyApiScheduling 스케쥴러를 강제 호출
	 * 
	 * @param userId : 호출한 사용자 ID
	 * @param year   : 조회할 년도
	 * @throws Exception
	 */
	@Async
	public void runHldyApiScheduling(String userId, String year) throws Exception {
		log.info("User '" + userId + "' called HldyApiScheduling(" + year + " year)");
		solYear = year;
		hldyApiScheduling();
	}
}
