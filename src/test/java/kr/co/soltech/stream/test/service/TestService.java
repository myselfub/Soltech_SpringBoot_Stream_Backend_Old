package kr.co.soltech.stream.test.service;

import java.util.List;

import kr.co.soltech.stream.test.model.TestModel;

public interface TestService {
	public List<TestModel> getAllTest() throws Exception;

	public TestModel upsertTest(TestModel testModel) throws Exception;
}
