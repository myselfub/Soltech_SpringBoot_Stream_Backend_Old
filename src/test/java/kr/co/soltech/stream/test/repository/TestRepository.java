package kr.co.soltech.stream.test.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.co.soltech.stream.msg.model.MsgModel;
import kr.co.soltech.stream.test.model.TestModel;
import kr.co.soltech.stream.test.model.TestModelId;

public interface TestRepository {// extends JpaRepository<TestModel, TestModelId>, CustomTestRepository {

	/***
	 * 메세지 내용 포함으로 메세지 목록 조회
	 * 
	 * @param msgCn : 메세지 내용
	 * @return 메세지 목록
	 * @throws Exception
	 */
	public List<MsgModel> findByMsgCnContaining(String msgCn) throws Exception;

	/***
	 * 메세지 언어로 메세지 목록 조회(JPQL 방식)
	 * 
	 * @param msgLang : 메세지 언어
	 * @return 메세지 목록
	 * @throws Exception
	 */
	@Query(value = "SELECT msg FROM msg_table msg WHERE msg.useYn = 'Y' AND msg.msgLang = :msgLang")
	public List<MsgModel> inqMsgByMsgLangJPQL(@Param("msgLang") String msgLang) throws Exception;
}