package kr.co.soltech.stream.schdl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.soltech.stream.schdl.model.SchdlModel;

/***
 * 일정 JPA 레파지토리 인터페이스
 */
@Repository
public interface SchdlRepository extends JpaRepository<SchdlModel, String>, CustomSchdlRepository {
}