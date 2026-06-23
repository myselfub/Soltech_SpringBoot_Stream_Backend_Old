package kr.co.soltech.stream.atrz.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.soltech.stream.atrz.model.AtrzModel;
import kr.co.soltech.stream.atrz.model.AtrzParamDTO;

/***
 * 결재 서비스 인터페이스
 */
public interface AtrzService {
	/***
	 * 결재 목록 조회
	 * 
	 * @param atrzParamDTO : 결재 조회 파라메터 DTO 클래스
	 * @param pageable     : 페이징 정보
	 * @return 결재 목록
	 * @throws Exception
	 */
	public Page<AtrzModel> inqAtrz(AtrzParamDTO atrzParamDTO, Pageable pageable) throws Exception;

	/***
	 * 결재 상세 조회
	 * 
	 * @param atrzParamDTO : 결재 조회 파라메터 DTO 클래스
	 * @return 결재 상세
	 * @throws Exception
	 */
	public AtrzModel getAtrz(AtrzParamDTO atrzParamDTO) throws Exception;

	/***
	 * 결재 등록/수정
	 * 
	 * @param atrzModel : 결재 모델 클래스
	 * @return 업데이트 로우 수
	 * @throws Exception
	 */
	public int upsertAtrz(AtrzModel atrzModel) throws Exception;

	/***
	 * 결재 삭제
	 * 
	 * @param atrzModel : 결재 모델 클래스
	 * @return 업데이트 로우 수
	 * @throws Exception
	 */
	public int deleteAtrz(AtrzModel atrzModel) throws Exception;

	/***
	 * 결재 완료 목록 조회
	 * 
	 * @param atrzParamDTO : 결재 조회 파라메터 DTO 클래스
	 * @param pageable     : 페이징 정보
	 * @return 결재 완료 목록
	 * @throws Exception
	 */
	public Page<AtrzModel> inqAtrzCmptn(AtrzParamDTO atrzParamDTO, Pageable pageable) throws Exception;

	/***
	 * 결재 미완료 목록 조회
	 * 
	 * @param atrzParamDTO : 결재 조회 파라메터 DTO 클래스
	 * @param pageable     : 페이징 정보
	 * @return 결재 미완료 목록
	 * @throws Exception
	 */
	public Page<AtrzModel> inqAtrzUnCmptn(AtrzParamDTO atrzParamDTO, Pageable pageable) throws Exception;

	/***
	 * 결재 상태 수정
	 * 
	 * @param atrzModel : 결재 모델 클래스
	 * @return 업데이트 로우 수
	 * @throws Exception
	 */
	public int updateAtrzStts(AtrzModel atrzModel) throws Exception;
}