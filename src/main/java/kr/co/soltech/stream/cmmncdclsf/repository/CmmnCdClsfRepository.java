package kr.co.soltech.stream.cmmncdclsf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.soltech.stream.cmmncdclsf.model.CmmnCdClsfModel;

/***
 * 공통코드 분류 JPA 레파지토리 인터페이스
 */
@Repository
public interface CmmnCdClsfRepository extends JpaRepository<CmmnCdClsfModel, String>, CustomCmmnCdClsfRepository {
}