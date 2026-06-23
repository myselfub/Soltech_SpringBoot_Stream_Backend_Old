package kr.co.soltech.stream.atrz.opnn.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.soltech.stream.atrz.opnn.model.AtrzOpnnModel;
import kr.co.soltech.stream.atrz.opnn.model.AtrzOpnnParamDTO;

/***
 * 결재 의견 커스텀 레파지토리 인터페이스
 */
interface CustomAtrzOpnnRepository {
	/***
	 * 결재 의견 목록 조회
	 * 
	 * @param atrzOpnnParamDTO : 결재 의견 조회 파라메터 DTO 클래스
	 * @param pageable         : 페이징 정보
	 * @return 결재 의견 목록
	 * @throws Exception
	 */
	public Page<AtrzOpnnModel> inqAtrzOpnn(AtrzOpnnParamDTO atrzOpnnParamDTO, Pageable pageable) throws Exception;

	/***
	 * 결재 의견 상세 조회
	 * 
	 * @param atrzOpnnParamDTO : 결재 의견 조회 파라메터 DTO 클래스
	 * @return 결재 의견 상세
	 * @throws Exception
	 */
	public AtrzOpnnModel getAtrzOpnn(AtrzOpnnParamDTO atrzOpnnParamDTO) throws Exception;

	/***
	 * 결재 의견 등록/수정
	 * 
	 * @param atrzOpnnModel : 결재 의견 모델 클래스
	 * @return 업데이트 로우 수
	 * @throws Exception
	 */
	public int upsertAtrzOpnn(AtrzOpnnModel atrzOpnnModel) throws Exception;

	/***
	 * 결재 의견 삭제
	 * 
	 * @param atrzOpnnModel : 결재 의견 모델 클래스
	 * @return 업데이트 로우 수
	 * @throws Exception
	 */
	public int deleteAtrzOpnn(AtrzOpnnModel atrzOpnnModel) throws Exception;
}