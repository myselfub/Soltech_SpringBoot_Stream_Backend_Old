package kr.co.soltech.stream.authrt.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.coyote.BadRequestException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import jakarta.transaction.Transactional;
import kr.co.soltech.stream.authrt.model.AuthrtModel;
import kr.co.soltech.stream.authrt.model.AuthrtModelId;
import kr.co.soltech.stream.authrt.model.AuthrtParamDTO;
import kr.co.soltech.stream.authrt.repository.AuthrtRepository;
import kr.co.soltech.stream.commons.model.CustomExceptionDTO;
import lombok.RequiredArgsConstructor;

/***
 * 권한 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class AuthrtServiceImpl implements AuthrtService {
	/***
	 * 권한 JPA 레파지토리 인터페이스
	 */
	private final AuthrtRepository authrtRepository;

	/***
	 * 권한 목록 조회
	 */
	@Override
	public Page<AuthrtModel> inqAuthrt(AuthrtParamDTO authrtParamDTO, Pageable pageable) throws Exception {
		return authrtRepository.inqAuthrt(authrtParamDTO, pageable);
	}

	/***
	 * 권한 상세 조회
	 */
	@Override
	public AuthrtModel getAuthrt(AuthrtModelId authrtModelId) throws Exception {
		return authrtRepository.getAuthrt(authrtModelId);
	}

	/***
	 * 권한 등록/수정
	 */
	@CacheEvict(value = { "authrtCnt", "userAuth" }, allEntries = true)
	@Transactional
	@Override
	public int upsertAuthrt(AuthrtModel authrtModel) throws Exception {
		return authrtRepository.upsertAuthrt(authrtModel);
	}

	/***
	 * 권한 삭제
	 */
	@CacheEvict(value = { "authrtCnt", "userAuth" }, allEntries = true)
	@Transactional
	@Override
	public int deleteAuthrt(AuthrtModel authrtModel) throws Exception {
		return authrtRepository.deleteAuthrt(authrtModel);
	}

	/***
	 * 권한별 메뉴 조회
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, List<String>> inqUrlAuth() throws Exception {
		List<Map> urlAuthList = authrtRepository.inqUrlAuth();
		Map<String, List<String>> resultMap = new HashMap<String, List<String>>();
		for (Map urlAuth : urlAuthList) {
			String authrtSeCd = (String) urlAuth.get("authrt_se_cd");
			if (ObjectUtils.isEmpty(resultMap.get(authrtSeCd))) {
				resultMap.put(authrtSeCd, new ArrayList<String>());
			}
			resultMap.get(authrtSeCd).add((String) urlAuth.get("menu_url"));
		}

		return resultMap;
	}

	/***
	 * 사용자 권한 조회
	 */
	@Cacheable(value = "userAuth", keyGenerator = "urlCacheKeyGenerator")
	@Override
	public Map<String, String> getUserAuthrt(String userId, String menuUrl, String menuMethod) throws Exception {
		List<Map<String, String>> errorList = new ArrayList<Map<String, String>>();
		if (ObjectUtils.isEmpty(userId)) {
			errorList.add(Map.of("message", "VALID_0001", "{field}", "userId"));
		}
		if (ObjectUtils.isEmpty(menuUrl)) {
			errorList.add(Map.of("message", "VALID_0001", "{field}", "menuUrl"));
		}
		if (ObjectUtils.isEmpty(menuUrl)) {
			errorList.add(Map.of("message", "VALID_0001", "{field}", "menuMethod"));
		}
		if (!ObjectUtils.isEmpty(errorList)) {
			throw new CustomExceptionDTO(errorList, HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}

		return authrtRepository.getUserAuthrt(userId, menuUrl, menuMethod);
	}
}