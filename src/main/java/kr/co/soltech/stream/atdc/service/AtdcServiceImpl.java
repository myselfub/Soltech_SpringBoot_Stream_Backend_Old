package kr.co.soltech.stream.atdc.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import kr.co.soltech.stream.atdc.model.AtdcModel;
import kr.co.soltech.stream.atdc.model.AtdcModelId;
import kr.co.soltech.stream.atdc.model.AtdcParamDTO;
import kr.co.soltech.stream.atdc.repository.AtdcRepository;
import lombok.RequiredArgsConstructor;

/***
 * 근태 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class AtdcServiceImpl implements AtdcService {
	/***
	 * 근태 JPA 레파지토리 인터페이스
	 */
	private final AtdcRepository atdcRepository;

	/***
	 * 근태 목록 조회
	 */
	@Override
	public Page<AtdcModel> inqAtdc(AtdcParamDTO atdcParamDTO, Pageable pageable) throws Exception {
		return atdcRepository.inqAtdc(atdcParamDTO, pageable);
	}

	/***
	 * 근태 상세 조회
	 */
	@Override
	public AtdcModel getAtdc(AtdcModelId atdcModelId) throws Exception {
		return atdcRepository.getAtdc(atdcModelId);
	}

	/***
	 * 근태 등록/수정
	 */
	@CacheEvict(value = { "atdcCnt", "atdcInfoCnt" }, allEntries = true)
	@Transactional
	@Override
	public int upsertAtdc(AtdcModel atdcModel) throws Exception {
		return atdcRepository.upsertAtdc(atdcModel);
	}

	/***
	 * 다중 근태 등록/수정
	 */
	@CacheEvict(value = { "atdcCnt", "atdcInfoCnt" }, allEntries = true)
	@Transactional
	@Override
	public int upsertAllAtdc(List<AtdcModel> atdcModelList) throws Exception {
		return atdcRepository.upsertAllAtdc(atdcModelList);
	}

	/***
	 * 근태 삭제
	 */
	@CacheEvict(value = { "atdcCnt", "atdcInfoCnt" }, allEntries = true)
	@Transactional
	@Override
	public int deleteAtdc(AtdcModel atdcModel) throws Exception {
		return atdcRepository.deleteAtdc(atdcModel);
	}

	/***
	 * 근태 정보 목록 조회
	 */
	@Override
	public Page<AtdcModel> inqAtdcInfo(AtdcParamDTO atdcParamDTO, Pageable pageable) throws Exception {
		return atdcRepository.inqAtdcInfo(atdcParamDTO, pageable);
	}
}