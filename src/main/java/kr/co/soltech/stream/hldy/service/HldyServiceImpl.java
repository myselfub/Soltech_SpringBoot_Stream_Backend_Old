package kr.co.soltech.stream.hldy.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import kr.co.soltech.stream.hldy.model.HldyModel;
import kr.co.soltech.stream.hldy.model.HldyModelId;
import kr.co.soltech.stream.hldy.model.HldyParamDTO;
import kr.co.soltech.stream.hldy.repository.HldyRepository;
import lombok.RequiredArgsConstructor;

/***
 * 휴일 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class HldyServiceImpl implements HldyService {
	/***
	 * 휴일 JPA 레파지토리 인터페이스
	 */
	private final HldyRepository hldyRepository;

	/***
	 * 휴일 목록 조회
	 */
	@Cacheable(value = "inqHldy", key = "(#p0.hldyYmd == null ? '' : #p0.hldyYmd) + '-' + (#p1 == null ? '' : #p1.pageSize + '-' + #p1.pageNumber)")
	@Override
	public Page<HldyModel> inqHldy(HldyParamDTO hldyParamDTO, Pageable pageable) throws Exception {
		return hldyRepository.inqHldy(hldyParamDTO, pageable);
	}

	/***
	 * 휴일 상세 조회
	 */
	@Override
	public HldyModel getHldy(HldyModelId hldyModelId) throws Exception {
		return hldyRepository.getHldy(hldyModelId);
	}

	/***
	 * 휴일 등록/수정
	 */
	@CacheEvict(value = { "inqHldy", "hldyCnt" }, allEntries = true)
	@Transactional
	@Override
	public int upsertHldy(HldyModel hldyModel) throws Exception {
		return hldyRepository.upsertHldy(hldyModel);
	}

	/***
	 * 다중 휴일 등록/수정
	 */
	@CacheEvict(value = { "inqHldy", "hldyCnt" }, allEntries = true)
	@Transactional
	@Override
	public int upsertAllHldy(List<HldyModel> hldyModelList) throws Exception {
		return hldyRepository.upsertAllHldy(hldyModelList);
	}

	/***
	 * 휴일 삭제
	 */
	@CacheEvict(value = { "inqHldy", "hldyCnt" }, allEntries = true)
	@Transactional
	@Override
	public int deleteHldy(HldyModel hldyModel) throws Exception {
		return hldyRepository.deleteHldy(hldyModel);
	}
}