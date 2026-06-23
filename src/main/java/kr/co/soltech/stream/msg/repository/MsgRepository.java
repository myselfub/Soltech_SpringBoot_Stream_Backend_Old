package kr.co.soltech.stream.msg.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import kr.co.soltech.stream.msg.model.MsgModel;
import kr.co.soltech.stream.msg.model.MsgModelId;

/***
 * 메세지 JPA 레파지토리 인터페이스
 */
@Repository
public interface MsgRepository extends JpaRepository<MsgModel, MsgModelId>, CustomMsgRepository {
	/***
	 * 메세지 언어와 메세지 분류로 메세지 목록 조회
	 * 
	 * @param msgLang     : 메세지 언어
	 * @param msgClsfList : 메세지 분류 목록
	 * @param useYn       : 사용여부
	 * @return 메세지 목록
	 * @throws Exception
	 */
	public List<MsgModel> findByMsgLangAndMsgClsfInAndUseYn(String msgLang, List<String> msgClsfList, char useYn)
			throws Exception;

	/***
	 * 그룹화된 메세지 언어 목록 조회
	 * 
	 * @return 메세지 언어 목록
	 * @throws Exception
	 */
	@Query(value = "SELECT msg.msgLang FROM msg_table msg WHERE msg.useYn = 'Y' GROUP BY msg.msgLang")
	public Set<String> inqDistinctMsgLang() throws Exception;
}