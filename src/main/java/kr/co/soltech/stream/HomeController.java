package kr.co.soltech.stream;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.SliceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;

import kr.co.soltech.stream.atdc.model.AtdcModel;
import kr.co.soltech.stream.cmmncd.model.CmmnCdParamDTO;
import kr.co.soltech.stream.cmmncd.service.CmmnCdService;
import kr.co.soltech.stream.commons.utils.ExcelToHtmlUtil;
import kr.co.soltech.stream.file.service.FileService;

@RestController
public class HomeController {
	@Autowired
	private ExcelToHtmlUtil excelToHtmlUtil;
	@Autowired
	private ResourceLoader resourceLoader;
	@Autowired
	private FileService fileService;
	@Autowired
	private CmmnCdService cmmnCdService;

	@GetMapping("/hello")
	public String hello() throws Exception {
		Resource resource = resourceLoader.getResource("classpath:static/uploads/atrz/template/결재1.xlsx");
		String css = """
				<style type="text/css">
					table { border: 0; }
					table tr { border: 0; }
					table td { border: 0; }
				</style>
				""";
		return css + excelToHtmlUtil.convertExcelToHtml(resource.getFile().getPath()).get(0);
	}
}
/*
 * TODO:
 * 
 * 메세지, 공통코드 데이터 저장 / 메뉴, 권한 데이터 저장(1차 완료) / Valid 적용 및 확인
 * 
 * 부서이력 테이블(HSTRY)
 * 
 * 쿼리 일부는 JPA -> Mybatis? 검토
 * 
 * typedQuery.setParameter("LIMIT", pageable.getPageSize());
 * typedQuery.setParameter("OFFSET", pageable.getPageNumber() *
 * pageable.getPageSize());
 * 
 * long countQueryResult = countTypedQuery.getSingleResult().longValue();
 * boolean hasNext = pageable.getPageNumber() * pageable.getPageSize() +
 * pageable.getPageSize() < countQueryResult;
 * 
 * return new SliceImpl<AtdcModel>(objectMapper.convertValue(queryResult, new
 * TypeReference<List<AtdcModel>>() { }), pageable, hasNext);
 */