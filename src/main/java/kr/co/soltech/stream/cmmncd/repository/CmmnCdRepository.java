package kr.co.soltech.stream.cmmncd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.soltech.stream.cmmncd.model.CmmnCdModel;
import kr.co.soltech.stream.cmmncd.model.CmmnCdModelId;

/***
 * 공통코드 JPA 레파지토리 인터페이스
 */
@Repository
public interface CmmnCdRepository extends JpaRepository<CmmnCdModel, CmmnCdModelId>, CustomCmmnCdRepository {
}