package kr.co.soltech.stream.msg.service;

import java.util.List;
import java.util.Set;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import kr.co.soltech.stream.commons.service.CustomMsgProvider;
import kr.co.soltech.stream.msg.model.MsgModel;
import kr.co.soltech.stream.msg.model.MsgModelId;
import kr.co.soltech.stream.msg.model.MsgParamDTO;
import kr.co.soltech.stream.msg.repository.MsgRepository;

/***
 * 메세지 서비스 클래스
 */
@Service
public class MsgServiceImpl implements MsgService {
	/***
	 * 메세지 JPA 레파지토리 인터페이스
	 */

	private final MsgRepository msgRepository;
	/***
	 * 커스텀 메세지 프로바이더
	 */
	private final CustomMsgProvider customMsgProvider;

	/***
	 * 생성자
	 * 
	 * @param msgRepository     : 메세지 JPA 레파지토리 인터페이스
	 * @param customMsgProvider : 커스텀 메세지 프로바이더(순환 의존성 문제로 @Lazy 로 지연)
	 */
	public MsgServiceImpl(MsgRepository msgRepository, @Lazy CustomMsgProvider customMsgProvider) {
		this.msgRepository = msgRepository;
		this.customMsgProvider = customMsgProvider;
	}

	/***
	 * 메세지 목록 조회
	 */
	@Override
	public Page<MsgModel> inqMsg(MsgParamDTO msgParamDTO, Pageable pageable) throws Exception {
		return msgRepository.inqMsg(msgParamDTO, pageable);
	}

	/***
	 * 메세지 상세 조회
	 */
	@Override
	public MsgModel getMsg(MsgModelId msgModelId) throws Exception {
		return msgRepository.getMsg(msgModelId);
	}

	/***
	 * 메세지 등록/수정
	 */
	@CacheEvict(value = "msgCnt", allEntries = true)
	@Transactional
	@Override
	public int upsertMsg(MsgModel msgModel) throws Exception {
		int updatedRows = msgRepository.upsertMsg(msgModel);
		customMsgProvider.setIsUpdate(true);

		return updatedRows;
	}

	/***
	 * 메세지 삭제
	 */
	@CacheEvict(value = "msgCnt", allEntries = true)
	@Transactional
	@Override
	public int deleteMsg(MsgModel msgModel) throws Exception {
		int updatedRows = msgRepository.deleteMsg(msgModel);
		customMsgProvider.setIsUpdate(true);

		return updatedRows;
	}

	/***
	 * 메세지 언어와 메세지 분류로 메세지 목록 조회
	 */
	@Override
	public List<MsgModel> findByMsgLangAndMsgClsfIn(String msgLang, List<String> msgClsfList) throws Exception {
		return msgRepository.findByMsgLangAndMsgClsfInAndUseYn(msgLang, msgClsfList, 'Y');
	}

	/***
	 * 그룹화된 메세지 언어 목록 조회
	 */
	@Override
	public Set<String> inqDistinctMsgLang() throws Exception {
		return msgRepository.inqDistinctMsgLang();
	}
}