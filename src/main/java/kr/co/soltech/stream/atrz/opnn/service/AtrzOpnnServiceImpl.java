package kr.co.soltech.stream.atrz.opnn.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import kr.co.soltech.stream.atrz.opnn.model.AtrzOpnnModel;
import kr.co.soltech.stream.atrz.opnn.model.AtrzOpnnParamDTO;
import kr.co.soltech.stream.atrz.opnn.repository.AtrzOpnnRepository;
import lombok.RequiredArgsConstructor;

/***
 * 결재 의견 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class AtrzOpnnServiceImpl implements AtrzOpnnService {
	/***
	 * 결재 JPA 레파지토리 인터페이스
	 */
	private final AtrzOpnnRepository atrzOpnnRepository;

	/***
	 * 결재 의견 목록 조회
	 */
	@Override
	public Page<AtrzOpnnModel> inqAtrzOpnn(AtrzOpnnParamDTO atrzOpnnParamDTO, Pageable pageable) throws Exception {
		return atrzOpnnRepository.inqAtrzOpnn(atrzOpnnParamDTO, pageable);
	}

	/***
	 * 결재 의견 상세 조회
	 */
	@Override
	public AtrzOpnnModel getAtrzOpnn(AtrzOpnnParamDTO atrzOpnnParamDTO) throws Exception {
		return atrzOpnnRepository.getAtrzOpnn(atrzOpnnParamDTO);
	}

	/***
	 * 결재 의견 등록/수정
	 */
	@CacheEvict(value = "atrzOpnnCnt", allEntries = true)
	@Transactional
	@Override
	public int upsertAtrzOpnn(AtrzOpnnModel atrzOpnnModel) throws Exception {
		return atrzOpnnRepository.upsertAtrzOpnn(atrzOpnnModel);
	}

	/***
	 * 결재 의견 삭제
	 */
	@CacheEvict(value = "atrzOpnnCnt", allEntries = true)
	@Transactional
	@Override
	public int deleteAtrzOpnn(AtrzOpnnModel atrzOpnnModel) throws Exception {
		return atrzOpnnRepository.deleteAtrzOpnn(atrzOpnnModel);
	}
}