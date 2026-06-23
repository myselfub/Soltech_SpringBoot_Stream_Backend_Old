package kr.co.soltech.stream.cmmncdclsf.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.soltech.stream.cmmncd.model.CmmnCdParamDTO;
import kr.co.soltech.stream.cmmncdclsf.model.CmmnCdClsfModel;

/***
 * 공통코드 분류 서비스 인터페이스
 */
public interface CmmnCdClsfService {
	/***
	 * 공통코드 분류 목록 조회
	 * 
	 * @param cmmnCdParamDTO : 공통코드 분류 조회 파라메터 DTO 클래스
	 * @param pageable       : 페이징 정보
	 * @return 공통코드 분류 목록
	 * @throws Exception
	 */
	public Page<CmmnCdClsfModel> inqCmmnCdClsf(CmmnCdParamDTO cmmnCdParamDTO, Pageable pageable) throws Exception;

	/***
	 * 공통코드 분류 상세 조회
	 * 
	 * @param cmmnCdParamDTO : 공통코드 분류 조회 파라메터 DTO 클래스
	 * @return 공통코드 분류 상세
	 * @throws Exception
	 */
	public CmmnCdClsfModel getCmmnCdClsf(CmmnCdParamDTO cmmnCdParamDTO) throws Exception;

	/***
	 * 공통코드 분류 등록/수정
	 * 
	 * @param cmmnCdClsfModel : 공통코드 분류 모델 클래스
	 * @return 업데이트 로우 수
	 * @throws Exception
	 */
	public int upsertCmmnCdClsf(CmmnCdClsfModel cmmnCdClsfModel) throws Exception;

	/***
	 * 공통코드 분류 삭제
	 * 
	 * @param cmmnCdClsfModel : 공통코드 분류 모델 클래스
	 * @return 업데이트 로우 수
	 * @throws Exception
	 */
	public int deleteCmmnCdClsf(CmmnCdClsfModel cmmnCdClsfModel) throws Exception;
}