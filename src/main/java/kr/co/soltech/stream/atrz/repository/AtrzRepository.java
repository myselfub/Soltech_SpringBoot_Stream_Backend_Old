package kr.co.soltech.stream.atrz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.soltech.stream.atrz.model.AtrzModel;

/***
 * 결재 JPA 레파지토리 인터페이스
 */
@Repository
public interface AtrzRepository extends JpaRepository<AtrzModel, String>, CustomAtrzRepository {
}