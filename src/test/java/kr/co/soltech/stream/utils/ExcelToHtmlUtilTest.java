package kr.co.soltech.stream.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.Test;

import kr.co.soltech.stream.commons.utils.ExcelToHtmlUtil;

public class ExcelToHtmlUtilTest {
	public void convertExcelToHtmlTest() {
		ExcelToHtmlUtil excelToHtmlUtil = new ExcelToHtmlUtil();
		try {
			List<String> resultList = excelToHtmlUtil.convertExcelToHtml(
					"[EXCEL_TEMPLATE_PATH]");
			for (String result : resultList) {
				System.out.println(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testParseValue() {
		ExcelToHtmlUtil excelToHtmlUtil = new ExcelToHtmlUtil();
		String result01 = excelToHtmlUtil.parseCellValue("테${123}스 ${567} 트");
		System.out.println(result01);

		assertEquals(result01, "name=\"123\">테스 ${567} 트");
	}
}
