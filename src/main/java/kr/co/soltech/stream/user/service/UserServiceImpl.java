package kr.co.soltech.stream.user.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import kr.co.soltech.stream.commons.model.CustomUserDetails;
import kr.co.soltech.stream.user.model.UserModel;
import kr.co.soltech.stream.user.model.UserParamDTO;
import kr.co.soltech.stream.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

/***
 * 사용자 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;

	/***
	 * 사용자 목록 조회
	 */
	@Override
	public Page<UserModel> inqUser(UserParamDTO userParamDTO, Pageable pageable) throws Exception {
		return userRepository.inqUser(userParamDTO, pageable);
	}

	/***
	 * 사용자 상세 조회
	 */
	@Override
	public UserModel getUser(UserParamDTO userParamDTO) throws Exception {
		return userRepository.getUser(userParamDTO);
	}

	/***
	 * 사용자 등록/삭제
	 */
	@CacheEvict(value = "userCnt", allEntries = true)
	@Transactional
	@Override
	public int upsertUser(UserModel userModel) throws Exception {
		return userRepository.upsertUser(userModel);
	}

	/***
	 * 사용자 삭제
	 */
	@CacheEvict(value = "userCnt", allEntries = true)
	@Transactional
	@Override
	public int deleteUser(UserModel userModel) throws Exception {
		return userRepository.deleteUser(userModel);
	}

	/***
	 * 사용자 존재 유무 확인
	 */
	@Override
	public boolean existsById(String userId) throws Exception {
		return userRepository.existsById(userId);
	}

	/***
	 * 로그인
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserModel userModel = userRepository.findById(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		return new CustomUserDetails(userModel);
	}
}