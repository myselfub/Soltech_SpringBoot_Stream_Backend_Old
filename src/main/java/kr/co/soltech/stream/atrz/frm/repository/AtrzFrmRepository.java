package kr.co.soltech.stream.atrz.frm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.soltech.stream.atrz.frm.model.AtrzFrmModel;

/***
 * 결재 양식 JPA 레파지토리 인터페이스
 */
@Repository
public interface AtrzFrmRepository extends JpaRepository<AtrzFrmModel, String>, CustomAtrzFrmRepository {
}