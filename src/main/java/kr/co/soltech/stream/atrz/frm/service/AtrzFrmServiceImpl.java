package kr.co.soltech.stream.atrz.frm.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import kr.co.soltech.stream.atrz.frm.model.AtrzFrmModel;
import kr.co.soltech.stream.atrz.frm.model.AtrzFrmParamDTO;
import kr.co.soltech.stream.atrz.frm.repository.AtrzFrmRepository;
import lombok.RequiredArgsConstructor;

/***
 * 결재 양식 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class AtrzFrmServiceImpl implements AtrzFrmService {
	/***
	 * 결재 JPA 레파지토리 인터페이스
	 */
	private final AtrzFrmRepository atrzFrmRepository;

	/***
	 * 결재 양식 목록 조회
	 */
	@Override
	public Slice<AtrzFrmModel> inqAtrzFrm(AtrzFrmParamDTO atrzFrmParamDTO, Pageable pageable) throws Exception {
		return atrzFrmRepository.inqAtrzFrm(atrzFrmParamDTO, pageable);
	}

	/***
	 * 결재 양식 상세 조회
	 */
	@Override
	public AtrzFrmModel getAtrzFrm(AtrzFrmParamDTO atrzFrmParamDTO) throws Exception {
		return atrzFrmRepository.getAtrzFrm(atrzFrmParamDTO);
	}

	/***
	 * 결재 양식 등록/수정
	 */
	@CacheEvict(value = "atrzFrmCnt", allEntries = true)
	@Transactional
	@Override
	public int upsertAtrzFrm(AtrzFrmModel atrzFrmModel) throws Exception {
		return atrzFrmRepository.upsertAtrzFrm(atrzFrmModel);
	}

	/***
	 * 결재 양식 삭제
	 */
	@CacheEvict(value = "atrzFrmCnt", allEntries = true)
	@Transactional
	@Override
	public int deleteAtrzFrm(AtrzFrmModel atrzFrmModel) throws Exception {
		return atrzFrmRepository.deleteAtrzFrm(atrzFrmModel);
	}
}