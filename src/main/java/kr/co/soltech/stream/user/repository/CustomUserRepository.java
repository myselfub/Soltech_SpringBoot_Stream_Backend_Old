package kr.co.soltech.stream.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.soltech.stream.user.model.UserModel;
import kr.co.soltech.stream.user.model.UserParamDTO;

/***
 * 사용자 커스텀 레파지토리 인터페이스
 */
interface CustomUserRepository {
	/***
	 * 사용자 목록 조회
	 * 
	 * @param userParamDTO : 사용자 조회 파라메터 DTO 클래스
	 * @param pageable     : 페이징 정보
	 * @return 사용자 목록
	 * @throws Exception
	 */
	public Page<UserModel> inqUser(UserParamDTO userParamDTO, Pageable pageable) throws Exception;

	/***
	 * 사용자 상세 조회
	 * 
	 * @param userParamDTO : 사용자 조회 파라메터 DTO 클래스
	 * @return 사용자 상세
	 * @throws Exception
	 */
	public UserModel getUser(UserParamDTO userParamDTO) throws Exception;

	/***
	 * 사용자 등록/수정
	 * 
	 * @param userModel : 사용자 모델 클래스
	 * @return 업데이트 로우 수
	 * @throws Exception
	 */
	public int upsertUser(UserModel userModel) throws Exception;

	/***
	 * 사용자 삭제
	 * 
	 * @param userModel : 사용자 모델 클래스
	 * @return 업데이트 로우 수
	 * @throws Exception
	 */
	public int deleteUser(UserModel userModel) throws Exception;
}