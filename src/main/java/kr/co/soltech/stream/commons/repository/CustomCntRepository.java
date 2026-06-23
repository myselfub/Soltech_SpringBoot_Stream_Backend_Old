package kr.co.soltech.stream.commons.repository;

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
 * 페이징 개수 커스텀 레파지토리 인터페이스 (self-invocation 문제로 새로운 인터페이스 정의)
 */
public interface CustomCntRepository {
	/***
	 * 근태 목록 페이징 처리 전체 로우수 조회
	 * 
	 * @param countTypedQuery : 작성된 쿼리
	 * @param atdcParamDTO    : 캐싱 키로 쓸 DTO
	 * @return 로우수
	 * @throws Exception
	 */
	public long getAtdcCnt(TypedQuery<Long> countTypedQuery, AtdcParamDTO atdcParamDTO) throws Exception;

	/***
	 * 근태 정보 목록 페이징 처리 전체 로우수 조회
	 * 
	 * @param countTypedQuery : 작성된 쿼리
	 * @param atdcParamDTO    : 캐싱 키로 쓸 DTO
	 * @return 로우수
	 * @throws Exception
	 */
	public long getAtdcInfoCnt(TypedQuery<Long> countTypedQuery, AtdcParamDTO atdcParamDTO) throws Exception;

	/***
	 * 결재 목록 페이징 처리 전체 로우수 조회
	 * 
	 * @param countQuery   : 작성된 쿼리
	 * @param atrzParamDTO : 캐싱 키로 쓸 DTO
	 * @return 로우수
	 * @throws Exception
	 */
	public long getAtrzCnt(Query countQuery, AtrzParamDTO atrzParamDTO) throws Exception;

	/***
	 * 결재 완료 목록 페이징 처리 전체 로우수 조회
	 * 
	 * @param countQuery   : 작성된 쿼리
	 * @param atrzParamDTO : 캐싱 키로 쓸 DTO
	 * @return 로우수
	 * @throws Exception
	 */
	public long getAtrzCmptnCnt(Query countQuery, AtrzParamDTO atrzParamDTO) throws Exception;

	/***
	 * 결재 미완료 목록 페이징 처리 전체 로우수 조회
	 * 
	 * @param countQuery   : 작성된 쿼리
	 * @param atrzParamDTO : 캐싱 키로 쓸 DTO
	 * @return 로우수
	 * @throws Exception
	 */
	public long getAtrzUnCmptnCnt(Query countQuery, AtrzParamDTO atrzParamDTO) throws Exception;

	/***
	 * 결재 양식 목록 페이징 처리 전체 로우수 조회
	 * 
	 * @param countTypedQuery : 작성된 쿼리
	 * @param atrzFrmParamDTO : 캐싱 키로 쓸 DTO
	 * @return 로우수
	 * @throws Exception
	 */
	public long getAtrzFrmCnt(TypedQuery<Long> countTypedQuery, AtrzFrmParamDTO atrzFrmParamDTO) throws Exception;

	/***
	 * 결재 의견 목록 페이징 처리 전체 로우수 조회
	 * 
	 * @param countTypedQuery  : 작성된 쿼리
	 * @param atrzOpnnParamDTO : 캐싱 키로 쓸 DTO
	 * @return 로우수
	 * @throws Exception
	 */
	public long getAtrzOpnnCnt(TypedQuery<Long> countTypedQuery, AtrzOpnnParamDTO atrzOpnnParamDTO) throws Exception;

	/***
	 * 권한 목록 페이징 처리 전체 로우수 조회
	 * 
	 * @param countTypedQuery : 작성된 쿼리
	 * @param authrtParamDTO  : 캐싱 키로 쓸 DTO
	 * @return 로우수
	 * @throws Exception
	 */
	public long getAuthrtCnt(TypedQuery<Long> countTypedQuery, AuthrtParamDTO authrtParamDTO) throws Exception;

	/***
	 * 공통코드 목록 페이징 처리 전체 로우수 조회
	 * 
	 * @param countTypedQuery : 작성된 쿼리
	 * @param cmmnCdParamDTO  : 캐싱 키로 쓸 DTO
	 * @return 로우수
	 * @throws Exception
	 */
	public long getCmmnCdCnt(TypedQuery<Long> countTypedQuery, CmmnCdParamDTO cmmnCdParamDTO) throws Exception;

	/***
	 * 공통코드 분류 목록 페이징 처리 전체 로우수 조회
	 * 
	 * @param countTypedQuery : 작성된 쿼리
	 * @param cmmnCdParamDTO  : 캐싱 키로 쓸 DTO
	 * @return 로우수
	 * @throws Exception
	 */
	public long getCmmnCdClsfCnt(TypedQuery<Long> countTypedQuery, CmmnCdParamDTO cmmnCdParamDTO) throws Exception;

	/***
	 * 파일 목록 페이징 처리 전체 로우수 조회
	 * 
	 * @param countTypedQuery : 작성된 쿼리
	 * @param fileParamDTO    : 캐싱 키로 쓸 DTO
	 * @return 로우수
	 * @throws Exception
	 */
	public long getFileCnt(TypedQuery<Long> countTypedQuery, FileParamDTO fileParamDTO) throws Exception;

	/***
	 * 휴일 목록 페이징 처리 전체 로우수 조회
	 * 
	 * @param countTypedQuery : 작성된 쿼리
	 * @param hldyParamDTO    : 캐싱 키로 쓸 DTO
	 * @return 로우수
	 * @throws Exception
	 */
	public long getHldyCnt(TypedQuery<Long> countTypedQuery, HldyParamDTO hldyParamDTO) throws Exception;

	/***
	 * 메뉴 목록 페이징 처리 전체 로우수 조회
	 * 
	 * @param countTypedQuery : 작성된 쿼리
	 * @param menuParamDTO    : 캐싱 키로 쓸 DTO
	 * @return 로우수
	 * @throws Exception
	 */
	public long getMenuCnt(TypedQuery<Long> countTypedQuery, MenuParamDTO menuParamDTO) throws Exception;

	/***
	 * 메세지 목록 페이징 처리 전체 로우수 조회
	 * 
	 * @param countTypedQuery : 작성된 쿼리
	 * @param msgParamDTO     : 캐싱 키로 쓸 DTO
	 * @return 로우수
	 * @throws Exception
	 */
	public long getMsgCnt(TypedQuery<Long> countTypedQuery, MsgParamDTO msgParamDTO) throws Exception;

	/***
	 * 일정 목록 페이징 처리 전체 로우수 조회
	 * 
	 * @param countTypedQuery : 작성된 쿼리
	 * @param schdlParamDTO   : 캐싱 키로 쓸 DTO
	 * @return 로우수
	 * @throws Exception
	 */
	public long getSchdlCnt(TypedQuery<Long> countTypedQuery, SchdlParamDTO schdlParamDTO) throws Exception;

	/***
	 * 사용자 목록 페이징 처리 전체 로우수 조회
	 * 
	 * @param countTypedQuery : 작성된 쿼리
	 * @param userParamDTO    : 캐싱 키로 쓸 DTO
	 * @return 로우수
	 * @throws Exception
	 */
	public long getUserCnt(TypedQuery<Long> countTypedQuery, UserParamDTO userParamDTO) throws Exception;

	/***
	 * 휴가 목록 페이징 처리 전체 로우수 조회
	 * 
	 * @param countQuery   : 작성된 쿼리
	 * @param vctnParamDTO : 캐싱 키로 쓸 DTO
	 * @return 로우수
	 * @throws Exception
	 */
	public long getVctnCnt(Query countQuery, VctnParamDTO vctnParamDTO) throws Exception;

	/***
	 * 휴가 정보 목록 페이징 처리 전체 로우수 조회
	 * 
	 * @param countQuery   : 작성된 쿼리
	 * @param vctnParamDTO : 캐싱 키로 쓸 DTO
	 * @return 로우수
	 * @throws Exception
	 */
	public long getVctnInfoCnt(Query countQuery, VctnParamDTO vctnParamDTO) throws Exception;
}