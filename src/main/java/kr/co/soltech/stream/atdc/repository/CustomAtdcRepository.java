package kr.co.soltech.stream.atdc.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.soltech.stream.atdc.model.AtdcModel;
import kr.co.soltech.stream.atdc.model.AtdcModelId;
import kr.co.soltech.stream.atdc.model.AtdcParamDTO;

/***
 * 근태 커스텀 레파지토리 인터페이스
 */
interface CustomAtdcRepository {
	/***
	 * 근태 목록 조회
	 * 
	 * @param atdcParamDTO : 근태 조회 파라메터 DTO 클래스
	 * @param pageable     : 페이징 정보
	 * @return 근태 목록
	 * @throws Exception
	 */
	public Page<AtdcModel> inqAtdc(AtdcParamDTO atdcParamDTO, Pageable pageable) throws Exception;

	/***
	 * 근태 상세 조회
	 * 
	 * @param atdcModelId : 근태 모델 ID 클래스
	 * @return 근태 상세
	 * @throws Exception
	 */
	public AtdcModel getAtdc(AtdcModelId atdcModelId) throws Exception;

	/***
	 * 근태 등록/수정
	 * 
	 * @param atdcModel : 근태 모델 클래스
	 * @return 업데이트 로우 수
	 * @throws Exception
	 */
	public int upsertAtdc(AtdcModel atdcModel) throws Exception;

	/***
	 * 다중 근태 등록/수정
	 * 
	 * @param atdcModelList : 근태 모델 클래스 목록
	 * @return 업데이트 로우 수
	 * @throws Exception
	 */
	public int upsertAllAtdc(List<AtdcModel> atdcModelList) throws Exception;

	/***
	 * 근태 삭제
	 * 
	 * @param atdcModel : 근태 모델 클래스
	 * @return 업데이트 로우 수
	 * @throws Exception
	 */
	public int deleteAtdc(AtdcModel atdcModel) throws Exception;

	/***
	 * 근태 정보 목록 조회
	 * 
	 * @param atdcParamDTO : 근태 조회 파라메터 DTO 클래스
	 * @param pageable     : 페이징 정보
	 * @return 근태 정보 목록
	 * @throws Exception
	 */
	public Page<AtdcModel> inqAtdcInfo(AtdcParamDTO atdcParamDTO, Pageable pageable) throws Exception;
}