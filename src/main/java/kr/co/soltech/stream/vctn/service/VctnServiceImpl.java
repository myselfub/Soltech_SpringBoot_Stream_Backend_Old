package kr.co.soltech.stream.vctn.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import kr.co.soltech.stream.vctn.model.VctnModel;
import kr.co.soltech.stream.vctn.model.VctnModelId;
import kr.co.soltech.stream.vctn.model.VctnParamDTO;
import kr.co.soltech.stream.vctn.repository.VctnRepository;
import lombok.RequiredArgsConstructor;

/***
 * 휴가 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class VctnServiceImpl implements VctnService {
	/***
	 * 휴가 JPA 레파지토리 인터페이스
	 */
	private final VctnRepository vctnRepository;

	/***
	 * 휴가 목록 조회
	 */
	@Override
	public Page<VctnModel> inqVctn(VctnParamDTO vctnParamDTO, Pageable pageable) throws Exception {
		return vctnRepository.inqVctn(vctnParamDTO, pageable);
	}

	/***
	 * 휴가 상세 조회
	 */
	@Override
	public VctnModel getVctn(VctnModelId vctnModelId) throws Exception {
		return vctnRepository.getVctn(vctnModelId);
	}

	/***
	 * 휴가 등록/수정
	 */
	@CacheEvict(value = { "vctnCnt", "vctnInfoCnt" }, allEntries = true)
	@Transactional
	@Override
	public int upsertVctn(VctnModel vctnModel) throws Exception {
		return vctnRepository.upsertVctn(vctnModel);
	}

	/***
	 * 다중 휴가 등록/수정
	 */
	@CacheEvict(value = { "vctnCnt", "vctnInfoCnt" }, allEntries = true)
	@Transactional
	@Override
	public int upsertAllVctn(List<VctnModel> vctnModelList) throws Exception {
		return vctnRepository.upsertAllVctn(vctnModelList);
	}

	/***
	 * 휴가 삭제
	 */
	@CacheEvict(value = { "vctnCnt", "vctnInfoCnt" }, allEntries = true)
	@Transactional
	@Override
	public int deleteVctn(VctnModel vctnModel) throws Exception {
		return vctnRepository.deleteVctn(vctnModel);
	}

	/***
	 * 휴가 정보 목록 조회
	 */
	@Override
	public Page<VctnModel> inqVctnInfo(VctnParamDTO vctnParamDTO, Pageable pageable) throws Exception {
		return vctnRepository.inqVctnInfo(vctnParamDTO, pageable);
	}
}