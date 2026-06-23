package kr.co.soltech.stream.authrt.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.soltech.stream.authrt.model.AuthrtModel;
import kr.co.soltech.stream.authrt.model.AuthrtModelId;
import kr.co.soltech.stream.authrt.model.AuthrtParamDTO;

/***
 * 권한 커스텀 레파지토리 인터페이스
 */
interface CustomAuthrtRepository {
	/***
	 * 권한 목록 조회
	 * 
	 * @param authrtParamDTO : 권한 조회 파라메터 DTO 클래스
	 * @param pageable       : 페이징 정보
	 * @return 권한 목록
	 * @throws Exception
	 */
	public Page<AuthrtModel> inqAuthrt(AuthrtParamDTO authrtParamDTO, Pageable pageable) throws Exception;

	/***
	 * 권한 상세 조회
	 * 
	 * @param authrtModelId : 권한 모델 ID 클래스
	 * @return 권한 상세
	 * @throws Exception
	 */
	public AuthrtModel getAuthrt(AuthrtModelId authrtModelId) throws Exception;

	/***
	 * 권한 등록/수정
	 * 
	 * @param authrtModel : 권한 모델 클래스
	 * @return 업데이트 로우 수
	 * @throws Exception
	 */
	public int upsertAuthrt(AuthrtModel authrtModel) throws Exception;

	/***
	 * 권한 삭제
	 * 
	 * @param authrtModel : 권한 모델 클래스
	 * @return 업데이트 로우 수
	 * @throws Exception
	 */
	public int deleteAuthrt(AuthrtModel authrtModel) throws Exception;

	/***
	 * 권한별 메뉴 목록 조회
	 * 
	 * @return 권한별 메뉴 목록
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public List<Map> inqUrlAuth() throws Exception;

	/***
	 * 사용자 권한 조회
	 * 
	 * @param userId     : 사용자 ID
	 * @param menuUrl    : 메뉴 URL
	 * @param menuMethod : 메뉴 메소드
	 * @return 사용자 권한 상세
	 * @throws Exception
	 */
	public Map<String, String> getUserAuthrt(String userId, String menuUrl, String menuMethod) throws Exception;
}