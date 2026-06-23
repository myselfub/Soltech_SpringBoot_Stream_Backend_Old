package kr.co.soltech.stream.vctn.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.soltech.stream.vctn.model.VctnModel;
import kr.co.soltech.stream.vctn.model.VctnModelId;
import kr.co.soltech.stream.vctn.model.VctnParamDTO;

/***
 * 휴가 서비스 인터페이스
 */
public interface VctnService {
	/***
	 * 휴가 목록 조회
	 * 
	 * @param vctnParamDTO : 휴가 조회 파라메터 DTO 클래스
	 * @param pageable     : 페이징 정보
	 * @return 휴가 목록
	 * @throws Exception
	 */
	public Page<VctnModel> inqVctn(VctnParamDTO vctnParamDTO, Pageable pageable) throws Exception;

	/***
	 * 휴가 상세 조회
	 * 
	 * @param vctnModelId : 휴가 모델 ID 클래스
	 * @return 휴가 상세
	 * @throws Exception
	 */
	public VctnModel getVctn(VctnModelId vctnModelId) throws Exception;

	/***
	 * 휴가 등록/수정
	 * 
	 * @param vctnModel : 휴가 모델 클래스
	 * @return 업데이트 로우 수
	 * @throws Exception
	 */
	public int upsertVctn(VctnModel vctnModel) throws Exception;

	/***
	 * 다중 휴가 등록/수정
	 * 
	 * @param vctnModelList : 휴가 모델 클래스 목록
	 * @return 업데이트 로우 수
	 * @throws Exception
	 */
	public int upsertAllVctn(List<VctnModel> vctnModelList) throws Exception;

	/***
	 * 휴가 삭제
	 * 
	 * @param vctnModel : 휴가 모델 클래스
	 * @return 업데이트 로우 수
	 * @throws Exception
	 */
	public int deleteVctn(VctnModel vctnModel) throws Exception;

	/***
	 * 휴가 정보 목록 조회
	 * 
	 * @param vctnParamDTO : 휴가 조회 파라메터 DTO 클래스
	 * @param pageable     : 페이징 정보
	 * @return 휴가 목록
	 * @throws Exception
	 */
	public Page<VctnModel> inqVctnInfo(VctnParamDTO vctnParamDTO, Pageable pageable) throws Exception;
}