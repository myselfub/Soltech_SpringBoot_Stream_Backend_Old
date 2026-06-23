package kr.co.soltech.stream.vctn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.soltech.stream.vctn.model.VctnModel;
import kr.co.soltech.stream.vctn.model.VctnModelId;

/***
 * 휴가 JPA 레파지토리 인터페이스
 */
@Repository
public interface VctnRepository extends JpaRepository<VctnModel, VctnModelId>, CustomVctnRepository {
}