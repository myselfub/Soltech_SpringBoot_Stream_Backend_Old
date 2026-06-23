package kr.co.soltech.stream.msg.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.soltech.stream.msg.model.MsgModel;
import kr.co.soltech.stream.msg.model.MsgModelId;
import kr.co.soltech.stream.msg.model.MsgParamDTO;

/***
 * 메세지 커스텀 레파지토리 인터페이스
 */
interface CustomMsgRepository {
	/***
	 * 메세지 목록 조회
	 * 
	 * @param msgParamDTO : 메세지 조회 파라메터 DTO 클래스
	 * @param pageable    : 페이징 정보
	 * @return 메세지 목록
	 * @throws Exception
	 */
	public Page<MsgModel> inqMsg(MsgParamDTO msgParamDTO, Pageable pageable) throws Exception;

	/***
	 * 메세지 상세 조회
	 * 
	 * @param msgModelId : 메세지 모델 ID 클래스
	 * @return 메세지 상세
	 * @throws Exception
	 */
	public MsgModel getMsg(MsgModelId msgModelId) throws Exception;

	/***
	 * 메세지 등록/수정
	 * 
	 * @param msgModel : 메세지 모델 클래스
	 * @return : 수정된 메세지 상세
	 * @throws Exception
	 */
	public int upsertMsg(MsgModel msgModel) throws Exception;

	/***
	 * 메세지 삭제
	 * 
	 * @param msgModel : 메세지 모델 클래스
	 * @return 업데이트 로우 수
	 * @throws Exception
	 */
	public int deleteMsg(MsgModel msgModel) throws Exception;
}