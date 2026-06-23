package kr.co.soltech.stream.cmmncd.service;

import java.util.ArrayList;
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
import kr.co.soltech.stream.cmmncd.model.CmmnCdModel;
import kr.co.soltech.stream.cmmncd.model.CmmnCdModelId;
import kr.co.soltech.stream.cmmncd.model.CmmnCdParamDTO;
import kr.co.soltech.stream.cmmncd.repository.CmmnCdRepository;
import kr.co.soltech.stream.commons.model.CustomExceptionDTO;
import lombok.RequiredArgsConstructor;

/***
 * 공통코드 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class CmmnCdServiceImpl implements CmmnCdService {
	/***
	 * 공통코드 JPA 레파지토리 인터페이스
	 */
	private final CmmnCdRepository cmmnCdRepository;

	/***
	 * 공통코드 목록 조회
	 */
	@Override
	public Page<CmmnCdModel> inqCmmnCd(CmmnCdParamDTO cmmnCdParamDTO, Pageable pageable) throws Exception {
		return cmmnCdRepository.inqCmmnCd(cmmnCdParamDTO, pageable);
	}

	/***
	 * 공통코드 상세 조회
	 */
	@Override
	public CmmnCdModel getCmmnCd(CmmnCdModelId cmmnCdModelId) throws Exception {
		return cmmnCdRepository.getCmmnCd(cmmnCdModelId);
	}

	/***
	 * 공통코드 등록/수정
	 */
	@CacheEvict(value = "cmmnCdCnt", allEntries = true)
	@Transactional
	@Override
	public int upsertCmmnCd(CmmnCdModel cmmnCdModel) throws Exception {
		return cmmnCdRepository.upsertCmmnCd(cmmnCdModel);
	}

	/***
	 * 다중 공통코드 등록/수정
	 */
	@CacheEvict(value = "cmmnCdCnt", allEntries = true)
	@Transactional
	@Override
	public int upsertAllCmmnCd(List<CmmnCdModel> cmmnCdModelList) throws Exception {
		return cmmnCdRepository.upsertAllCmmnCd(cmmnCdModelList);
	}

	/***
	 * 공통코드 삭제
	 */
	@CacheEvict(value = "cmmnCdCnt", allEntries = true)
	@Transactional
	@Override
	public int deleteCmmnCd(CmmnCdModel cmmnCdModel) throws Exception {
		return cmmnCdRepository.deleteCmmnCd(cmmnCdModel);
	}

	/***
	 * 사용자용 공통코드 목록 조회
	 */
	@Override
	public List<CmmnCdModel> inqUserCmmnCd(String cmmnCdClsfUserDfnVl1) throws Exception {
		return cmmnCdRepository.inqUserCmmnCd(cmmnCdClsfUserDfnVl1);
	}

	/***
	 * 조직도용 공통코드(부서) 목록 조회
	 */
	@Override
	public List<Map<String, Object>> inqOgnzChrtDept(CmmnCdParamDTO cmmnCdParamDTO) throws Exception {
		return cmmnCdRepository.inqOgnzChrtDept(cmmnCdParamDTO);
	}

	/***
	 * 도메인별 파일 업로드 확장자 허용 목록 조회
	 */
	@Cacheable(value = "supportExtension", key = "#p0.cmmnCdId")
	@Override
	public List<String> getSupportExtn(CmmnCdParamDTO cmmnCdParamDTO) throws Exception {
		if (ObjectUtils.isEmpty(cmmnCdParamDTO.getCmmnCdId())) {
			throw CustomExceptionDTO.of("ERROR_0001", "cmmnCdId", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}
		CmmnCdModel cmmnCdModel = cmmnCdRepository.getSupportExtn(cmmnCdParamDTO);
		String cmmnCdUserDfnVl = cmmnCdModel.getCmmnCdUserDfnVl1() + "," + cmmnCdModel.getCmmnCdUserDfnVl2() + ","
				+ cmmnCdModel.getCmmnCdUserDfnVl3();
		List<String> extensionList = new ArrayList<String>();
		for (String extension : cmmnCdUserDfnVl.split(",")) {
			extensionList.add(extension);
		}

		return extensionList;
	}

	/***
	 * 결재, 근태, 휴가, 일정 구분코드 맵핑 목록 조회
	 */
	@Cacheable(value = "atrzMapping", key = "#p0.cmmnCdId + '-' + (#p0.cmmnCdUserDfnVl1 == null ? (#p0.cmmnCdUserDfnVl2 == null ? '' : #p0.cmmnCdUserDfnVl2) : #p0.cmmnCdUserDfnVl1)")
	@Override
	public Map<String, Object> getAtrzMapping(CmmnCdParamDTO cmmnCdParamDTO) throws Exception {
		return cmmnCdRepository.getAtrzMapping(cmmnCdParamDTO);
	}
}