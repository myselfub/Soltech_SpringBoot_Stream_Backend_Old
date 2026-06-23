package kr.co.soltech.stream.atrz.opnn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.soltech.stream.atrz.opnn.model.AtrzOpnnModel;

/***
 * 결재 의견 JPA 레파지토리 인터페이스
 */
@Repository
public interface AtrzOpnnRepository extends JpaRepository<AtrzOpnnModel, String>, CustomAtrzOpnnRepository {
}