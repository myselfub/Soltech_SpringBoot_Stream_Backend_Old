package kr.co.soltech.stream.cmmncdclsf.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import kr.co.soltech.stream.cmmncd.model.CmmnCdParamDTO;
import kr.co.soltech.stream.cmmncdclsf.model.CmmnCdClsfModel;
import kr.co.soltech.stream.cmmncdclsf.repository.CmmnCdClsfRepository;
import lombok.RequiredArgsConstructor;

/***
 * 공통코드 분류 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class CmmnCdClsfServiceImpl implements CmmnCdClsfService {
	/***
	 * 공통코드 분류 JPA 레파지토리 인터페이스
	 */
	private final CmmnCdClsfRepository cmmnCdClsfRepository;

	/***
	 * 공통코드 분류 목록 조회
	 */
	@Override
	public Page<CmmnCdClsfModel> inqCmmnCdClsf(CmmnCdParamDTO cmmnCdParamDTO, Pageable pageable) throws Exception {
		return cmmnCdClsfRepository.inqCmmnCdClsf(cmmnCdParamDTO, pageable);
	}

	/***
	 * 공통코드 분류 상세 조회
	 */
	@Override
	public CmmnCdClsfModel getCmmnCdClsf(CmmnCdParamDTO cmmnCdParamDTO) throws Exception {
		return cmmnCdClsfRepository.getCmmnCdClsf(cmmnCdParamDTO);
	}

	/***
	 * 공통코드 분류 등록/수정
	 */
	@CacheEvict(value = "cmmnCdClsfCnt", allEntries = true)
	@Transactional
	@Override
	public int upsertCmmnCdClsf(CmmnCdClsfModel cmmnCdClsfModel) throws Exception {
		return cmmnCdClsfRepository.upsertCmmnCdClsf(cmmnCdClsfModel);
	}

	/***
	 * 공통코드 분류 삭제
	 */
	@CacheEvict(value = "cmmnCdClsfCnt", allEntries = true)
	@Transactional
	@Override
	public int deleteCmmnCdClsf(CmmnCdClsfModel cmmnCdClsfModel) throws Exception {
		return cmmnCdClsfRepository.deleteCmmnCdClsf(cmmnCdClsfModel);
	}
}