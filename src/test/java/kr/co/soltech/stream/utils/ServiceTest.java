package kr.co.soltech.stream.utils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.co.soltech.stream.cmmncd.model.CmmnCdParamDTO;
import kr.co.soltech.stream.cmmncd.service.CmmnCdService;

@SpringBootTest
class ServiceTest {
	@Autowired
	private CmmnCdService cmmnCdService;

	@Test
	void test1() throws Exception {
		CmmnCdParamDTO cmmnCdParamDTO = new CmmnCdParamDTO();
		cmmnCdParamDTO.setCmmn_cd_id("00040");
		System.out.println(cmmnCdService.getAtrzMapping(cmmnCdParamDTO));
	}
}