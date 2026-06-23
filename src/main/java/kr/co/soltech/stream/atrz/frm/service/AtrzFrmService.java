package kr.co.soltech.stream.atrz.frm.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import kr.co.soltech.stream.atrz.frm.model.AtrzFrmModel;
import kr.co.soltech.stream.atrz.frm.model.AtrzFrmParamDTO;

/***
 * 결재 양식 서비스 인터페이스
 */
public interface AtrzFrmService {
	/***
	 * 결재 양식 목록 조회
	 * 
	 * @param atrzFrmParamDTO : 결재 양식 조회 파라메터 DTO 클래스
	 * @param pageable        : 페이징 정보
	 * @return 결재 양식 목록
	 * @throws Exception
	 */
	public Slice<AtrzFrmModel> inqAtrzFrm(AtrzFrmParamDTO atrzFrmParamDTO, Pageable pageable) throws Exception;

	/***
	 * 결재 양식 상세 조회
	 * 
	 * @param atrzFrmParamDTO : 결재 양식 조회 파라메터 DTO 클래스
	 * @return 결재 양식 상세
	 * @throws Exception
	 */
	public AtrzFrmModel getAtrzFrm(AtrzFrmParamDTO atrzFrmParamDTO) throws Exception;

	/***
	 * 결재 양식 등록/수정
	 * 
	 * @param atrzFrmModel : 결재 양식 모델 클래스
	 * @return 업데이트 로우 수
	 * @throws Exception
	 */
	public int upsertAtrzFrm(AtrzFrmModel atrzFrmModel) throws Exception;

	/***
	 * 결재 양식 삭제
	 * 
	 * @param atrzFrmModel : 결재 양식 모델 클래스
	 * @return 업데이트 로우 수
	 * @throws Exception
	 */
	public int deleteAtrzFrm(AtrzFrmModel atrzFrmModel) throws Exception;
}