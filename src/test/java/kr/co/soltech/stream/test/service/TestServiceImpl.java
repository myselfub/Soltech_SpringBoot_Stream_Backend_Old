package kr.co.soltech.stream.test.service;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import kr.co.soltech.stream.commons.utils.SoltechStreamUtils;
import kr.co.soltech.stream.test.model.TestModel;
import kr.co.soltech.stream.test.repository.TestRepository;

public class TestServiceImpl implements TestService {
	@Autowired
	private TestRepository testRepository;

	public List<TestModel> getAllTest() throws Exception {
		//return testRepository.findAll();
		return null;
	}

	public TestModel upsertTest(TestModel testModel) throws Exception {
		//return testRepository.save(testModel);
		return null;
	}

	void test() throws Exception {
		Random random = new Random();
		String dcNo = "";
		for (int i = 15; i < 26; i++) {
			for (int j = 1; j < 13; j++) {
				for (int k = 1; k < 301; k++) {
					dcNo = SoltechStreamUtils.lpad(String.valueOf(i), 2, '0')
							+ SoltechStreamUtils.lpad(String.valueOf(j), 2, '0') + "-"
							+ SoltechStreamUtils.lpad(String.valueOf(k), 3, '0');
					Set<Integer> empNoSet = new HashSet<Integer>();
					for (int z = 0; z < 5; z++) {
						TestModel testModel = new TestModel();
						testModel.setDcNo(dcNo);
						int randNo;
						do {
							randNo = random.nextInt(100);
						} while (empNoSet.contains(randNo));
						empNoSet.add(randNo);
						testModel.setEmpNo(SoltechStreamUtils.lpad(String.valueOf(randNo), 3, '0'));
						testModel.setAppYn(randNo > 20 ? 'Y' : 'N');
						testModel.setAppOrder(z);
						upsertTest(testModel);
					}
				}
			}
		}
		System.out.println(getAllTest());
	}
}