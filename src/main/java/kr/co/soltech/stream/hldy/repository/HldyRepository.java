package kr.co.soltech.stream.hldy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.soltech.stream.hldy.model.HldyModel;
import kr.co.soltech.stream.hldy.model.HldyModelId;

/***
 * 휴일 JPA 레파지토리 인터페이스
 */
@Repository
public interface HldyRepository extends JpaRepository<HldyModel, HldyModelId>, CustomHldyRepository {
}