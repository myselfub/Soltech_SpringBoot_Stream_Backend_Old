package kr.co.soltech.stream.cmmncd.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

/***
 * 공통코드 Mybatis 매퍼 인터페이스
 */
@Mapper
public interface CmmnCdMapper {
	/***
	 * 공통코드 조회
	 * 
	 * @return
	 */
	public Map<String, String> getCmmnCd() throws Exception;
}