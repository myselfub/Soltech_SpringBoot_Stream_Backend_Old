package kr.co.soltech.stream.schdl.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.soltech.stream.schdl.model.SchdlModel;
import kr.co.soltech.stream.schdl.model.SchdlParamDTO;

/***
 * 일정 서비스 인터페이스
 */
public interface SchdlService {
	/***
	 * 일정 목록 조회
	 * 
	 * @param schdlParamDTO : 일정 조회 파라메터 DTO 클래스
	 * @param pageable      : 페이징 정보
	 * @return 일정 목록
	 * @throws Exception
	 */
	public Page<SchdlModel> inqSchdl(SchdlParamDTO schdlParamDTO, Pageable pageable) throws Exception;

	/***
	 * 일정 상세 조회
	 * 
	 * @param schdlParamDTO : 일정 조회 파라메터 DTO 클래스
	 * @return 일정 상세
	 * @throws Exception
	 */
	public SchdlModel getSchdl(SchdlParamDTO schdlParamDTO) throws Exception;

	/***
	 * 일정 등록/수정
	 * 
	 * @param schdlModel : 일정 모델 클래스
	 * @return 업데이트 로우 수
	 * @throws Exception
	 */
	public int upsertSchdl(SchdlModel schdlModel) throws Exception;

	/***
	 * 다중 일정 등록/수정
	 * 
	 * @param schdlModelList : 일정 모델 클래스 목록
	 * @return 업데이트 로우 수
	 * @throws Exception
	 */
	public int upsertAllSchdl(List<SchdlModel> schdlModelList) throws Exception;

	/***
	 * 일정 삭제
	 * 
	 * @param schdlModel : 일정 모델 클래스
	 * @return 업데이트 로우 수
	 * @throws Exception
	 */
	public int deleteSchdl(SchdlModel schdlModel) throws Exception;
}