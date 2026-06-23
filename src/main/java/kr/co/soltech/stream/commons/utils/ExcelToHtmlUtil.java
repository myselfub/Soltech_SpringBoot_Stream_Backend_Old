package kr.co.soltech.stream.commons.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class ExcelToHtmlUtil {
	public List<String> convertExcelToHtml(String filePath) throws IOException {
		File excelFile = new File(filePath);

		if (!excelFile.exists()) {
			throw new IOException("No Files.");
		}

		ArrayList<String> resultList = new ArrayList<String>();
		try (FileInputStream fileInputStream = new FileInputStream(excelFile)) {
			Workbook workbook = new XSSFWorkbook(fileInputStream);

			for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
				// 첫번째 시트만 가져오게.(추후 유지보수를 위해 여러개 가져오는 로직만 추가해둠)
				if (sheetNum > 0) {
					break;
				}

				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append("<table border=\"1\" style=\"border-collapse: collapse;\">");
				Sheet sheet = workbook.getSheetAt(sheetNum);
				List<CellRangeAddress> mergedList = sheet.getMergedRegions();

				for (int rowIdx = 0; rowIdx <= sheet.getLastRowNum(); rowIdx++) {
					Row row = sheet.getRow(rowIdx);
					if (row == null) {
						continue;
					}
					// 셀 높이
					// int rowHeight = (int) (row.getHeightInPoints() * 1.33);
					int rowHeight = (int) (row.getHeight() / 22);
					stringBuilder.append("<tr style=\"height: ").append(rowHeight).append("px;\">");

					for (int colIdx = 0; colIdx < row.getLastCellNum(); colIdx++) {
						Cell cell = row.getCell(colIdx);

						if (cell == null) {
							// int columnWidth = (int) (sheet.getColumnWidthInPixels(colIdx) * 1.33);
							int columnWidth = (sheet.getColumnWidth(colIdx) / 256 * 4) + 5;
							stringBuilder.append("<td style=\"width: ").append(columnWidth).append("px;\"></tb>");
							continue;
						}

						// 셀 병합 체크
						String rowSpan = null;
						String colSpan = null;
						boolean isInRange = false;
						for (CellRangeAddress mergedRangeAddress : mergedList) {
							if (mergedRangeAddress.isInRange(rowIdx, colIdx)) {
								if (mergedRangeAddress.getFirstRow() == rowIdx
										&& mergedRangeAddress.getFirstColumn() == colIdx) {
									rowSpan = String.valueOf(
											mergedRangeAddress.getLastRow() - mergedRangeAddress.getFirstRow() + 1);
									colSpan = String.valueOf(mergedRangeAddress.getLastColumn()
											- mergedRangeAddress.getFirstColumn() + 1);
									break;
								}
								isInRange = true;
							}
						}
						if (isInRange) {
							continue;
						}

						stringBuilder.append("<td");
						// 셀 병합
						if (rowSpan != null) {
							stringBuilder.append(" rowspan=\"").append(rowSpan).append("\"");
						}
						if (colSpan != null) {
							stringBuilder.append(" colspan=\"").append(colSpan).append("\"");
						}
						// 셀 네임속성/값
						String cellStyle = parseCellStyle(sheet, cell);
						String cellValue = getCellValue(cell);
						stringBuilder.append(cellStyle).append(cellValue);

						stringBuilder.append("</td>");
					}
					stringBuilder.append("</tr>");
				}
				stringBuilder.append("</table>");
				resultList.add(stringBuilder.toString());
			}
			workbook.close();
		}
		return resultList;
	}

	public String getCellValue(Cell cell) {
		if (cell == null) {
			return parseCellValue("");
		}
		switch (cell.getCellType()) {
		case STRING: {
			return parseCellValue(cell.getStringCellValue());
		}
		case NUMERIC: {
			return String.valueOf(cell.getNumericCellValue());
		}
		case BOOLEAN: {
			return String.valueOf(cell.getBooleanCellValue());
		}
		case FORMULA: {
			return cell.getCellFormula();
		}
		default:
			return parseCellValue("");
		}
	}

	public String parseCellStyle(Sheet sheet, Cell cell) {
		if (cell == null) {
			return "";
		}
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(" style=\"");

		XSSFCellStyle cellStyle = (XSSFCellStyle) cell.getCellStyle();
		XSSFFont font = cellStyle.getFont();
		// 셀 크기
		// int columnWidth = (int) (sheet.getColumnWidth(cell.getColumnIndex())/23.27);
		// int columnWidth = (int) (sheet.getColumnWidthInPixels(cell.getColumnIndex())
		// * 1.33); // 1.42
		int columnWidth = (sheet.getColumnWidth(cell.getColumnIndex()) / 256 * 4) + 5;
		stringBuilder.append("width: ").append(columnWidth).append("px; ");

		// 셀 배경색
		XSSFColor bgColor = (XSSFColor) cellStyle.getFillForegroundColorColor();
		if (bgColor == null) {
			bgColor = (XSSFColor) cellStyle.getFillBackgroundColorColor();
		}
		if (bgColor != null) {
			byte[] bgRgb = bgColor.getRGB();
			if (bgRgb != null && bgRgb.length == 3) {
				int bgRgbRed = bgRgb[0] & 0xFF;
				int bgRgbGreen = bgRgb[1] & 0xFF;
				int bgRgbBlue = bgRgb[2] & 0xFF;
				String bgColorHex = String.format("#%02X%02X%02X", bgRgbRed, bgRgbGreen, bgRgbBlue);
				stringBuilder.append("background: ").append(bgColorHex).append("; ");
			}
		}

		// 테두리
		stringBuilder.append("border-top: ")
				.append(parseBorderStyle(cellStyle.getBorderTop(), cellStyle.getTopBorderXSSFColor()));
		stringBuilder.append("border-right: ")
				.append(parseBorderStyle(cellStyle.getBorderRight(), cellStyle.getRightBorderXSSFColor()));
		stringBuilder.append("border-bottom: ")
				.append(parseBorderStyle(cellStyle.getBorderBottom(), cellStyle.getBottomBorderXSSFColor()));
		stringBuilder.append("border-left: ")
				.append(parseBorderStyle(cellStyle.getBorderLeft(), cellStyle.getLeftBorderXSSFColor()));

		// 셀 정렬
		HorizontalAlignment horizontalAlignment = cellStyle.getAlignment();
		VerticalAlignment verticalAlignment = cellStyle.getVerticalAlignment();
		switch (horizontalAlignment) {
		case HorizontalAlignment.LEFT:
		case HorizontalAlignment.CENTER:
		case HorizontalAlignment.RIGHT:
		case HorizontalAlignment.JUSTIFY: {
			stringBuilder.append("text-align: ").append(horizontalAlignment.toString().toLowerCase()).append("; ");
			break;
		}
		case HorizontalAlignment.GENERAL:
		default: {
			break;
		}
		}
		switch (verticalAlignment) {
		case VerticalAlignment.TOP:
		case VerticalAlignment.CENTER:
		case VerticalAlignment.BOTTOM: {
			stringBuilder.append("vertical-align: ").append(verticalAlignment.toString().toLowerCase()).append("; ");
			break;
		}
		case VerticalAlignment.JUSTIFY:
		default: {
			break;
		}
		}

		// 폰트 색상
		short fontColor = font.getColor();
		if (fontColor != -1) {
			String fontColorHex = String.format("#%02X%02X%02X", (fontColor >> 16) & 0xFF, (fontColor >> 8) & 0xFF,
					fontColor & 0xFF);
			stringBuilder.append("color: ").append(fontColorHex).append("; ");
		}

		// 폰트 크기
		// int fontSize = (int) (font.getFontHeightInPoints() * 1.33);
		int fontSize = (font.getFontHeight() / 256 * 8) + 5;
		stringBuilder.append("font-size: ").append(fontSize).append("px; ");

		// 폰트 굵기
		if (font.getBold()) {
			stringBuilder.append("font-weight: bold; ");
		}
		stringBuilder.append("\"");

		return stringBuilder.toString();
	}

	public String parseCellValue(String str) {
		str = str != null ? str : "";
		Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}");
		Matcher matcher = pattern.matcher(str);
		if (matcher.find()) {
			StringBuilder stringBuilder = new StringBuilder();
			String nameAttr = null;
			int lastEnd = 0;
			boolean isFirst = true;
			do {
				stringBuilder.append(str, lastEnd, matcher.start());
				if (isFirst) {
					String value = matcher.group(1);
					nameAttr = " name=\"" + (value != null ? value : "") + "\">";
					isFirst = false;
				} else {
					stringBuilder.append(matcher.group(0));
				}
				lastEnd = matcher.end();
			} while (matcher.find());
			stringBuilder.append(str.substring(lastEnd));
			return (nameAttr != null ? nameAttr : ">") + stringBuilder.toString();
		}
		return ">" + str;
	}

	public String parseBorderStyle(BorderStyle borderLocation, short borderColor) {
		String border = null;
		switch (borderLocation) {
		case THIN:
			border = "1px solid ";
			break;
		case MEDIUM:
			border = "2px solid ";
			break;
		case DASHED:
			border = "1px dashed ";
			break;
		case DOTTED:
			border = "1px dotted ";
			break;
		case DOUBLE:
			border = "1px double ";
			break;
		default:
			border = "none";
		}
		if (borderColor != -1) {
			String fontColorHex = String.format("#%02X%02X%02X", (borderColor >> 16) & 0xFF, (borderColor >> 8) & 0xFF,
					borderColor & 0xFF);
			if (!"none".equals(border)) {
				border = border + fontColorHex + "; ";
			}
		}

		return border;
	}

	public String parseBorderStyle(BorderStyle borderLocation, XSSFColor borderColor) {
		String border = null;
		switch (borderLocation) {
		case THIN:
		case THICK:
			border = "1px solid ";
			break;
		case MEDIUM:
		case HAIR:
			border = "2px solid ";
			break;
		case DASHED:
		case DASH_DOT:
			border = "1px dashed ";
			break;
		case MEDIUM_DASHED:
		case MEDIUM_DASH_DOT:
			border = "2px dashed ";
			break;
		case DOTTED:
		case DASH_DOT_DOT:
			border = "1px dotted ";
			break;
		case MEDIUM_DASH_DOT_DOT:
		case SLANTED_DASH_DOT:
			border = "2px dotted ";
			break;
		case DOUBLE:
			border = "1px double ";
			break;
		default:
			border = "none;";
		}
		if (borderColor != null) {
			byte[] borderRgb = borderColor.getRGB();
			if (borderRgb != null && borderRgb.length == 3) {
				int borderRgbRed = borderRgb[0] & 0xFF;
				int borderRgbGreen = borderRgb[1] & 0xFF;
				int borderRgbBlue = borderRgb[2] & 0xFF;
				String borderColorHex = String.format("#%02X%02X%02X", borderRgbRed, borderRgbGreen, borderRgbBlue);
				if (!"none".equals(border)) {
					border = border + borderColorHex + "; ";
				}
			}
		}

		return border;
	}
}