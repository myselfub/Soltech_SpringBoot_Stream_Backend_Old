package kr.co.soltech.stream.atdc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.soltech.stream.atdc.model.AtdcModel;
import kr.co.soltech.stream.atdc.model.AtdcModelId;

/***
 * 근태 JPA 레파지토리 인터페이스
 */
@Repository
public interface AtdcRepository extends JpaRepository<AtdcModel, AtdcModelId>, CustomAtdcRepository {
}