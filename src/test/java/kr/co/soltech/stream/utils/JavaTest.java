package kr.co.soltech.stream.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.util.ObjectUtils;

import kr.co.soltech.stream.atrz.model.AtrzModel;
import kr.co.soltech.stream.commons.utils.SoltechStreamUtils;

public class JavaTest {
	void test() {
		AtrzModel a = AtrzModel.builder().docNo("1").atrzSttsSeCd("2").docSeCd("3").build();
		System.out.println(a);
		a = a.toBuilder().docTtl("4").build();
		System.out.println(a);
		a = a.toBuilder().docNo("5").build();
		System.out.println(a);
	}

	@SuppressWarnings("unchecked")
	void test1() throws Exception {
		boolean isCamelCase = false;
		Map<String, Object> atrzData = new HashMap<String, Object>();
		atrzData.put("testCd", "test");
		atrzData.put("testNm", "test1");
		Map<String, Object> atrzData2 = new HashMap<String, Object>();
		atrzData2.put("empNo", "20000");
		atrzData.put("testData", atrzData2);
		for (String key : atrzData.keySet()) {
			if (key.matches(".*[A-Z].*")) {
				isCamelCase = true;
				break;
			}
			if (atrzData.get(key) instanceof Map) {
				for (String innerKey : ((Map<String, ?>) atrzData.get(key)).keySet()) {
					if (innerKey.matches(".*[A-Z].*")) {
						isCamelCase = true;
						break;
					}
				}
			}
		}
		if (isCamelCase) {
			Map<String, Object> newAtrzData = new HashMap<String, Object>();
			for (String key : atrzData.keySet()) {
				// TODO: 데이터 수정
				if (atrzData.get(key) instanceof Map) {
					Map<String, Object> newAtrzDataInner = new HashMap<String, Object>();
					for (String innerKey : ((Map<String, ?>) atrzData.get(key)).keySet()) {
						newAtrzDataInner.put(SoltechStreamUtils.camelCaseToSnakeCase(innerKey),
								((Map<String, ?>) atrzData.get(key)).get(innerKey));
					}
					atrzData.put(key, newAtrzDataInner);
				}
				newAtrzData.put(SoltechStreamUtils.camelCaseToSnakeCase(key), atrzData.get(key));
			}
			System.out.println(newAtrzData);
		}
	}

	public void test2() {
		System.out.println(SoltechStreamUtils.parseDateToString("19010101 " + "130123", "HHmmss"));
		System.out.println(SoltechStreamUtils.parseDateToString("19010101 " + "1301", "HHmmss"));
		System.out.println(SoltechStreamUtils.parseDateToString("19010101 " + "13", "HHmmss"));
	}

	@SuppressWarnings("unchecked")
	public void test3() {
		Map<String, Object> atrzData = new HashMap<>();
		Map<String, Object> atrz_info1 = new HashMap<>();
		atrz_info1.put("atrz_dt", "20250225");
		atrz_info1.put("aprvr_id", "[USER_ID]");
		atrzData.put("atrz_info1", atrz_info1);

		Map<String, Object> atrz_info2 = new HashMap<>();
		atrz_info2.put("atrz_dt", "");
		atrz_info2.put("aprvr_id", "[USER_ID]");
		atrzData.put("atrz_info2", atrz_info2);

		Map<String, Object> atrz_info3 = new HashMap<>();
		atrz_info3.put("aprvr_id", "[USER_ID]");
		atrzData.put("atrz_info3", atrz_info3);
		System.out.println(atrzData);

		String requiredInfo1 = "atrz_dt";
		boolean isCamelCase = false;
		if (!ObjectUtils.isEmpty(atrzData)) {
			for (String key : atrzData.keySet()) {
				if (!isCamelCase && key.matches(".*[A-Z].*")) {
					isCamelCase = true;
				}
				if (atrzData.get(key) instanceof Map) {
					Map<String, Object> atrzDataInner = (Map<String, Object>) atrzData.get(key);
					if (key.startsWith("atrz_info") && !atrzDataInner.keySet().contains(requiredInfo1)) {
						atrzDataInner.put(requiredInfo1, null);
					}
					for (String innerKey : atrzDataInner.keySet()) {
						if (!isCamelCase && innerKey.matches(".*[A-Z].*")) {
							isCamelCase = true;
						}
					}
				}
			}
		}
		System.out.println(atrzData);
	}

	public void test4() {
		String name = "asdf.exe";
		String extension = "";
		int dotIndex = name.lastIndexOf('.');
		if (dotIndex == -1) {
			dotIndex = name.length();
		} else {
			extension = name.substring(dotIndex + 1);
		}
		String fileNameWithoutExtension = name.substring(0, dotIndex);

		System.out.println(dotIndex);
		System.out.println(extension);
		System.out.println(fileNameWithoutExtension);
	}

	public void test5() {
		LocalDateTime start = SoltechStreamUtils.parseDateTime("20250320 0000");
		LocalDateTime end = SoltechStreamUtils.parseDateTime("20250331 0000");
		System.out.println(start);
		System.out.println(end);
		System.out.println(SoltechStreamUtils.calcDateDiff(start, end, false,
				Set.of(SoltechStreamUtils.parseDateTime("20250328").toLocalDate())));
		System.out.println(SoltechStreamUtils.calcDateDiff(start, end, true,
				Set.of(SoltechStreamUtils.parseDateTime("20250328").toLocalDate())));
		System.out.println(SoltechStreamUtils.calcDateBetween(start, end, false,
				Set.of(SoltechStreamUtils.parseDateTime("20250328").toLocalDate())));
	}

	@Test
	public void test6() {
		LocalDateTime start = SoltechStreamUtils.parseDateTime("20250320 0900");
		LocalDateTime end = SoltechStreamUtils.parseDateTime("20250401 2359");
		List<LocalDateTime> localDateTimeList = SoltechStreamUtils.calcDateBetween(start, end, false,
				Set.of(SoltechStreamUtils.parseDateTime("20250328").toLocalDate()));
		System.out.println(localDateTimeList);
		LocalDateTime startLocalDateTime = null;
		LocalDateTime preLocalDateTime = null;
		for (int idx = 0; idx < localDateTimeList.size(); idx++) {
			LocalDateTime localDateTime = localDateTimeList.get(idx);
			if (!ObjectUtils.isEmpty(preLocalDateTime)
					&& Duration.between(preLocalDateTime, localDateTime).toDays() > 1) {
				System.out.println(startLocalDateTime + "~" + preLocalDateTime);
				startLocalDateTime = null;
			}
			if (ObjectUtils.isEmpty(startLocalDateTime)) {
				startLocalDateTime = localDateTime;
			}
			preLocalDateTime = localDateTime.toLocalDate().atStartOfDay();
			if (idx == localDateTimeList.size() - 1) {
				System.out.println(startLocalDateTime + "~" + preLocalDateTime);
			}
		}
	}
}
