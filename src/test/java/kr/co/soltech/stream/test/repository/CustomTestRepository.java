package kr.co.soltech.stream.test.repository;

import java.util.List;

import kr.co.soltech.stream.msg.model.MsgModel;

public interface CustomTestRepository {
	/***
	 * 메세지 언어로 메세지 목록 조회(Criteria 방식)
	 * 
	 * @param msgLang : 메세지 언어
	 * @return 메세지 목록
	 * @throws Exception
	 */
	public List<MsgModel> inqMsgByMsgLangManagerCriteria(String msgLang) throws Exception;

	/***
	 * 메세지 언어로 메세지 목록 조회(매니저 JPQL 방식)
	 * 
	 * @param msgLang : 메세지 언어
	 * @return 메세지 목록
	 * @throws Exception
	 */
	public List<MsgModel> inqMsgByMsgLangManagerJPQL(String msgLang) throws Exception;
}