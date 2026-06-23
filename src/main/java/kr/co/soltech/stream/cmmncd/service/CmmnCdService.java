package kr.co.soltech.stream.cmmncd.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.soltech.stream.cmmncd.model.CmmnCdModel;
import kr.co.soltech.stream.cmmncd.model.CmmnCdModelId;
import kr.co.soltech.stream.cmmncd.model.CmmnCdParamDTO;

/***
 * 공통코드 서비스 인터페이스
 */
public interface CmmnCdService {
	/***
	 * 공통코드 목록 조회
	 * 
	 * @param cmmnCdParamDTO : 공통코드 조회 파라메터 DTO 클래스
	 * @param pageable       : 페이징 정보
	 * @return 공통코드 목록
	 * @throws Exception
	 */
	public Page<CmmnCdModel> inqCmmnCd(CmmnCdParamDTO cmmnCdParamDTO, Pageable pageable) throws Exception;

	/***
	 * 공통코드 상세 조회
	 * 
	 * @param cmmnCdModelId : 공통코드 모델 ID 클래스
	 * @return 공통코드 상세
	 * @throws Exception
	 */
	public CmmnCdModel getCmmnCd(CmmnCdModelId cmmnCdModelId) throws Exception;

	/***
	 * 공통코드 등록/수정
	 * 
	 * @param cmmnCdModel : 공통코드 모델 클래스
	 * @return 업데이트 로우 수
	 * @throws Exception
	 */
	public int upsertCmmnCd(CmmnCdModel cmmnCdModel) throws Exception;

	/***
	 * 다중 공통코드 등록/수정
	 * 
	 * @param cmmnCdModelList : 공통코드 모델 클래스 목록
	 * @return 업데이트 로우 수
	 * @throws Exception
	 */
	public int upsertAllCmmnCd(List<CmmnCdModel> cmmnCdModelList) throws Exception;

	/***
	 * 공통코드 삭제
	 * 
	 * @param cmmnCdModel : 공통코드 모델 클래스
	 * @return 업데이트 로우 수
	 * @throws Exception
	 */
	public int deleteCmmnCd(CmmnCdModel cmmnCdModel) throws Exception;

	/***
	 * 사용자용 공통코드 목록 조회
	 * 
	 * @param cmmnCdClsfUserDfnVl1 : 공통코드 분류 사용자정의 값1
	 * @return 사용자용 공통코드 목록
	 * @throws Exception
	 */
	public List<CmmnCdModel> inqUserCmmnCd(String cmmnCdClsfUserDfnVl1) throws Exception;

	/***
	 * 조직도용 공통코드(부서) 목록 조회
	 * 
	 * @param cmmnCdParamDTO : 공통코드 조회 파라메터 DTO 클래스
	 * @return 조직도용 부서코드 목록
	 * @throws Exception
	 */
	public List<Map<String, Object>> inqOgnzChrtDept(CmmnCdParamDTO cmmnCdParamDTO) throws Exception;

	/***
	 * 도메인별 파일 업로드 확장자 허용 목록 조회
	 * 
	 * @param cmmnCdParamDTO : 공통코드 조회 파라메터 DTO 클래스
	 * @return 도메인별 파일 업로드 확장자 허용 목록
	 * @throws Exception
	 */
	public List<String> getSupportExtn(CmmnCdParamDTO cmmnCdParamDTO) throws Exception;

	/***
	 * 결재, 근태, 휴가, 일정 구분코드 맵핑 조회
	 * 
	 * @param cmmnCdParamDTO : 공통코드 조회 파라메터 DTO 클래스
	 * @return 결재, 근태, 휴가, 일정 구분코드 맵핑
	 * @throws Exception
	 */
	public Map<String, Object> getAtrzMapping(CmmnCdParamDTO cmmnCdParamDTO) throws Exception;
}