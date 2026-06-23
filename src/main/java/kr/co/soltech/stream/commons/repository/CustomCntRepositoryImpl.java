package kr.co.soltech.stream.commons.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import kr.co.soltech.stream.atdc.model.AtdcParamDTO;
import kr.co.soltech.stream.atrz.frm.model.AtrzFrmParamDTO;
import kr.co.soltech.stream.atrz.model.AtrzParamDTO;
import kr.co.soltech.stream.atrz.opnn.model.AtrzOpnnParamDTO;
import kr.co.soltech.stream.authrt.model.AuthrtParamDTO;
import kr.co.soltech.stream.cmmncd.model.CmmnCdParamDTO;
import kr.co.soltech.stream.file.model.FileParamDTO;
import kr.co.soltech.stream.hldy.model.HldyParamDTO;
import kr.co.soltech.stream.menu.model.MenuParamDTO;
import kr.co.soltech.stream.msg.model.MsgParamDTO;
import kr.co.soltech.stream.schdl.model.SchdlParamDTO;
import kr.co.soltech.stream.user.model.UserParamDTO;
import kr.co.soltech.stream.vctn.model.VctnParamDTO;

/***
 * 페이징 개수 커스텀 레파지토리 클래스
 */
@Repository
public class CustomCntRepositoryImpl implements CustomCntRepository {
	/***
	 * 근태 목록 페이징 처리 전체 로우수 조회
	 */
	@Cacheable(value = "atdcCnt", key = "(#p1.atdcUserId == null ? '' : #p1.atdcUserId) +"
			+ "'-' + (#p1.atdcYmd == null ? '' : #p1.atdcYmd) +" + "'-' + (#p1.atdcTm == null ? '' : #p1.atdcTm) +"
			+ "'-' + (#p1.atdcSeCd == null ? '' : #p1.atdcSeCd)")
	@Override
	public long getAtdcCnt(TypedQuery<Long> countTypedQuery, AtdcParamDTO atdcParamDTO) throws Exception {
		return countTypedQuery.getSingleResult().longValue();
	}

	/***
	 * 근태 정보 목록 페이징 처리 전체 로우수 조회
	 */
	@Cacheable(value = "atdcInfoCnt", key = "(#p1.atdcUserId == null ? '' : #p1.atdcUserId) +"
			+ "'-' + (#p1.atdcYmd == null ? '' : #p1.atdcYmd) +" + "'-' + (#p1.atdcTm == null ? '' : #p1.atdcTm) +"
			+ "'-' + (#p1.atdcSeCd == null ? '' : #p1.atdcSeCd)")
	@Override
	public long getAtdcInfoCnt(TypedQuery<Long> countTypedQuery, AtdcParamDTO atdcParamDTO) throws Exception {
		return countTypedQuery.getSingleResult().longValue();
	}

	/***
	 * 결재 목록 페이징 처리 전체 로우수 조회
	 */
	@Cacheable(value = "atrzCnt", key = "(#p1.drftrId == null ? '' : #p1.drftrId) +"
			+ "'-' + (#p1.docSeCd == null ? '' : #p1.docSeCd) +"
			+ "'-' + (#p1.atrzSttsSeCd == null ? '' : #p1.atrzSttsSeCd) + "
			+ "'-' + (#p1.drftBgngYmd == null ? '' : #p1.drftBgngYmd) + "
			+ "'-' + (#p1.drftEndYmd == null ? '' : #p1.drftEndYmd) + "
			+ "'-' + (#p1.docTtl == null ? '' : #p1.docTtl)")
	@Override
	public long getAtrzCnt(Query countQuery, AtrzParamDTO atrzParamDTO) throws Exception {
		return ((Long) countQuery.getSingleResult()).longValue();
	}

	/***
	 * 결재 완료 목록 페이징 처리 전체 로우수 조회
	 */
	@Cacheable(value = "atrzCmptnCnt", key = "(#p1.aprvrId == null ? '' : #p1.aprvrId)")
	@Override
	public long getAtrzCmptnCnt(Query countQuery, AtrzParamDTO atrzParamDTO) throws Exception {
		return ((Long) countQuery.getSingleResult()).longValue();
	}

	/***
	 * 결재 미완료 목록 페이징 처리 전체 로우수 조회
	 */
	@Cacheable(value = "atrzUnCmptnCnt", key = "(#p1.aprvrId == null ? '' : #p1.aprvrId)")
	@Override
	public long getAtrzUnCmptnCnt(Query countQuery, AtrzParamDTO atrzParamDTO) throws Exception {
		return ((Long) countQuery.getSingleResult()).longValue();
	}

	/***
	 * 결재 양식 목록 페이징 처리 전체 로우수 조회
	 */
	@Cacheable(value = "atrzFrmCnt", key = "(#p1.docSeCd == null ? '' : #p1.docSeCd)")
	@Override
	public long getAtrzFrmCnt(TypedQuery<Long> countTypedQuery, AtrzFrmParamDTO atrzFrmParamDTO) throws Exception {
		return countTypedQuery.getSingleResult().longValue();
	}

	/***
	 * 결재 의견 목록 페이징 처리 전체 로우수 조회
	 */
	@Cacheable(value = "atrzOpnnCnt", key = "(#p1.docNo == null ? '' : #p1.docNo) +"
			+ "'-' + (#p1.rgtrId == null ? '' : #p1.rgtrId) +" + "'-' + (#p1.regYmd == null ? '' : #p1.regYmd)")
	@Override
	public long getAtrzOpnnCnt(TypedQuery<Long> countTypedQuery, AtrzOpnnParamDTO atrzOpnnParamDTO) throws Exception {
		return countTypedQuery.getSingleResult().longValue();
	}

	/***
	 * 권한 목록 페이징 처리 전체 로우수 조회
	 */
	@Cacheable(value = "authrtCnt", key = "(#p1.authrtSeCd == null ? '' : #p1.authrtSeCd) +"
			+ "'-' + (#p1.menuId == null ? '' : #p1.menuId) +"
			+ "'-' + (#p1.authrtBgngYmd == null ? '' : #p1.authrtBgngYmd) +"
			+ "'-' + (#p1.authrtEndYmd == null ? '' : #p1.authrtEndYmd) +"
			+ "'-' + (#p1.mthdList == null ? '' : #p1.mthdList)")
	@Override
	public long getAuthrtCnt(TypedQuery<Long> countTypedQuery, AuthrtParamDTO authrtParamDTO) throws Exception {
		return countTypedQuery.getSingleResult().longValue();
	}

	/***
	 * 공통코드 목록 페이징 처리 전체 로우수 조회
	 */
	@Cacheable(value = "cmmnCdCnt", key = "(#p1.cmmnCdClsfId == null ? '' : #p1.cmmnCdClsfId) +"
			+ "'-' + (#p1.cmmnCdId == null ? '' : #p1.cmmnCdId) +" + "'-' + (#p1.cmmnCdNm == null ? '' : #p1.cmmnCdNm)")
	@Override
	public long getCmmnCdCnt(TypedQuery<Long> countTypedQuery, CmmnCdParamDTO cmmnCdParamDTO) throws Exception {
		return countTypedQuery.getSingleResult().longValue();
	}

	/***
	 * 공통코드 분류 목록 페이징 처리 전체 로우수 조회
	 */
	@Cacheable(value = "cmmnCdCnt", key = "(#p1.cmmnCdClsfId == null ? '' : #p1.cmmnCdClsfId) +"
			+ "'-' + (#p1.cmmnCdClsfNm == null ? '' : #p1.cmmnCdClsfNm)")
	@Override
	public long getCmmnCdClsfCnt(TypedQuery<Long> countTypedQuery, CmmnCdParamDTO cmmnCdParamDTO) throws Exception {
		return countTypedQuery.getSingleResult().longValue();
	}

	/***
	 * 파일 목록 페이징 처리 전체 로우수 조회
	 */
	@Cacheable(value = "fileCnt", key = "(#p1.dmnClsfId == null ? '' : #p1.dmnClsfId) +"
			+ "'-' + (#p1.fileNm == null ? '' : #p1.fileNm) +"
			+ "'-' + (#p1.fileExtnNm == null ? '' : #p1.fileExtnNm) +"
			+ "'-' + (#p1.fileSz == null ? '' : #p1.fileSz) +" + "'-' + (#p1.regYmd == null ? '' : #p1.regYmd)")
	@Override
	public long getFileCnt(TypedQuery<Long> countTypedQuery, FileParamDTO fileParamDTO) throws Exception {
		return countTypedQuery.getSingleResult().longValue();
	}

	/***
	 * 휴일 목록 페이징 처리 전체 로우수 조회
	 */
	@Cacheable(value = "hldyCnt", key = "(#p1.hldyYmd == null ? '' : #p1.hldyYmd) +"
			+ "'-' + (#p1.hldyYmdSn == null ? '' : #p1.hldyYmdSn) +" + "'-' + (#p1.hldyNm == null ? '' : #p1.hldyNm)")
	@Override
	public long getHldyCnt(TypedQuery<Long> countTypedQuery, HldyParamDTO hldyParamDTO) throws Exception {
		return countTypedQuery.getSingleResult().longValue();
	}

	/***
	 * 메뉴 목록 페이징 처리 전체 로우수 조회
	 */
	@Cacheable(value = "menuCnt", key = "(#p1.menuId == null ? '' : #p1.menuId) +"
			+ "'-' + (#p1.menuNm == null ? '' : #p1.menuNm) +" + "'-' + (#p1.menuUrl == null ? '' : #p1.menuUrl) +"
			+ "'-' + (#p1.menuApiYn == null ? '' : #p1.menuApiYn) +"
			+ "'-' + (#p1.menuPopupYn == null ? '' : #p1.menuPopupYn)")
	@Override
	public long getMenuCnt(TypedQuery<Long> countTypedQuery, MenuParamDTO menuParamDTO) throws Exception {
		return countTypedQuery.getSingleResult().longValue();
	}

	/***
	 * 메세지 목록 페이징 처리 전체 로우수 조회
	 */
	@Cacheable(value = "msgCnt", key = "(#p1.msgLang == null ? '' : #p1.msgLang) +"
			+ "'-' + (#p1.msgClsf == null ? '' : #p1.msgClsf) +" + "'-' + (#p1.msgCd == null ? '' : #p1.msgCd) +"
			+ "'-' + (#p1.msgNm == null ? '' : #p1.msgNm) +" + "'-' + (#p1.menuPopupYn == null ? '' : #p1.menuPopupYn)")
	@Override
	public long getMsgCnt(TypedQuery<Long> countTypedQuery, MsgParamDTO msgParamDTO) throws Exception {
		return countTypedQuery.getSingleResult().longValue();
	}

	/***
	 * 일정 목록 페이징 처리 전체 로우수 조회
	 */
	@Cacheable(value = "schdlCnt", key = "(#p1.schdlUserId == null ? '' : #p1.schdlUserId) +"
			+ "'-' + (#p1.schdlSeCd == null ? '' : #p1.schdlSeCd) +"
			+ "'-' + (#p1.schdlTtl == null ? '' : #p1.schdlTtl) +" + "'-' + (#p1.schdlCn == null ? '' : #p1.schdlCn) +"
			+ "'-' + (#p1.schdlBgngDt == null ? '' : #p1.schdlBgngDt) +"
			+ "'-' + (#p1.schdlRlsYn == null ? '' : #p1.schdlRlsYn)")
	@Override
	public long getSchdlCnt(TypedQuery<Long> countTypedQuery, SchdlParamDTO schdlParamDTO) throws Exception {
		return countTypedQuery.getSingleResult().longValue();
	}

	/***
	 * 사용자 목록 페이징 처리 전체 로우수 조회
	 */
	@Cacheable(value = "userCnt", key = "(#p1.userId == null ? '' : #p1.userId) +"
			+ "'-' + (#p1.userEmpNo == null ? '' : #p1.userEmpNo) +" + "'-' + (#p1.userNm == null ? '' : #p1.userNm) +"
			+ "'-' + (#p1.userBrthYmd == null ? '' : #p1.userBrthYmd) +"
			+ "'-' + (#p1.userJncmpYmd == null ? '' : #p1.userJncmpYmd) +"
			+ "'-' + (#p1.userDeptSeCd == null ? '' : #p1.userDeptSeCd) +"
			+ "'-' + (#p1.userJbgdSeCd == null ? '' : #p1.userJbgdSeCd) +"
			+ "'-' + (#p1.userAuthrtSeCd == null ? '' : #p1.userAuthrtSeCd)")
	@Override
	public long getUserCnt(TypedQuery<Long> countTypedQuery, UserParamDTO userParamDTO) throws Exception {
		return countTypedQuery.getSingleResult().longValue();
	}

	/***
	 * 휴가 목록 페이징 처리 전체 로우수 조회
	 */
	@Cacheable(value = "vctnCnt", key = "(#p1.vctnUserId == null ? '' : #p1.vctnUserId) +"
			+ "'-' + (#p1.vctnSeCd == null ? '' : #p1.vctnSeCd) +"
			+ "'-' + (#p1.vctnBgngYmd == null ? '' : #p1.vctnBgngYmd)")
	@Override
	public long getVctnCnt(Query countQuery, VctnParamDTO vctnParamDTO) throws Exception {
		return ((Long) countQuery.getSingleResult()).longValue();
	}

	/***
	 * 휴가 정보 목록 페이징 처리 전체 로우수 조회
	 */
	@Cacheable(value = "vctnInfoCnt", key = "(#p1.vctnUserId == null ? '' : #p1.vctnUserId) +"
			+ "'-' + (#p1.vctnBgngYmd == null ? '' : #p1.vctnBgngYmd)")
	@Override
	public long getVctnInfoCnt(Query countQuery, VctnParamDTO vctnParamDTO) throws Exception {
		return ((Long) countQuery.getSingleResult()).longValue();
	}
}