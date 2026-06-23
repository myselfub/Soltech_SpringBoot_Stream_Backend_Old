package kr.co.soltech.stream.hldy.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.soltech.stream.hldy.model.HldyModel;
import kr.co.soltech.stream.hldy.model.HldyModelId;
import kr.co.soltech.stream.hldy.model.HldyParamDTO;

/***
 * 휴일 서비스 인터페이스
 */
public interface HldyService {
	/***
	 * 휴일 목록 조회
	 * 
	 * @param hldyParamDTO : 휴일 조회 파라메터 DTO 클래스
	 * @param pageable     : 페이징 정보
	 * @return 휴일 목록
	 * @throws Exception
	 */
	public Page<HldyModel> inqHldy(HldyParamDTO hldyParamDTO, Pageable pageable) throws Exception;

	/***
	 * 휴일 상세 조회
	 * 
	 * @param hldyModelId : 휴일 모델 ID 클래스
	 * @return 휴일 상세
	 * @throws Exception
	 */
	public HldyModel getHldy(HldyModelId hldyModelId) throws Exception;

	/***
	 * 휴일 등록/수정
	 * 
	 * @param hldyModel : 휴일 모델 클래스
	 * @return 업데이트 로우 수
	 * @throws Exception
	 */
	public int upsertHldy(HldyModel hldyModel) throws Exception;

	/***
	 * 다중 휴일 등록/수정
	 * 
	 * @param hldyModelList : 휴일 모델 클래스 목록
	 * @return 업데이트 로우 수
	 * @throws Exception
	 */
	public int upsertAllHldy(List<HldyModel> hldyModelList) throws Exception;

	/***
	 * 휴일 삭제
	 * 
	 * @param hldyModel : 휴일 모델 클래스
	 * @return 업데이트 로우 수
	 * @throws Exception
	 */
	public int deleteHldy(HldyModel hldyModel) throws Exception;
}