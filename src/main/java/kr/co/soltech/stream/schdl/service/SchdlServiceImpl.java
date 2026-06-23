package kr.co.soltech.stream.schdl.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import kr.co.soltech.stream.schdl.model.SchdlModel;
import kr.co.soltech.stream.schdl.model.SchdlParamDTO;
import kr.co.soltech.stream.schdl.repository.SchdlRepository;
import lombok.RequiredArgsConstructor;

/***
 * 일정 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class SchdlServiceImpl implements SchdlService {
	/***
	 * 일정 JPA 레파지토리 인터페이스
	 */
	private final SchdlRepository schdlRepository;

	/***
	 * 일정 목록 조회
	 */
	@Override
	public Page<SchdlModel> inqSchdl(SchdlParamDTO schdlParamDTO, Pageable pageable) throws Exception {
		return schdlRepository.inqSchdl(schdlParamDTO, pageable);
	}

	/***
	 * 일정 상세 조회
	 */
	@Override
	public SchdlModel getSchdl(SchdlParamDTO schdlParamDTO) throws Exception {
		return schdlRepository.getSchdl(schdlParamDTO);
	}

	/***
	 * 일정 등록/수정
	 */
	@CacheEvict(value = "schdlCnt", allEntries = true)
	@Transactional
	@Override
	public int upsertSchdl(SchdlModel schdlModel) throws Exception {
		return schdlRepository.upsertSchdl(schdlModel);
	}

	/***
	 * 다중 일정 등록/수정
	 */
	@CacheEvict(value = "schdlCnt", allEntries = true)
	@Transactional
	@Override
	public int upsertAllSchdl(List<SchdlModel> schdlModelList) throws Exception {
		return schdlRepository.upsertAllSchdl(schdlModelList);
	}

	/***
	 * 일정 삭제
	 */
	@CacheEvict(value = "schdlCnt", allEntries = true)
	@Transactional
	@Override
	public int deleteSchdl(SchdlModel schdlModel) throws Exception {
		return schdlRepository.deleteSchdl(schdlModel);
	}
}