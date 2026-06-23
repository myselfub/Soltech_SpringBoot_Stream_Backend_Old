package kr.co.soltech.stream.authrt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.soltech.stream.authrt.model.AuthrtModel;
import kr.co.soltech.stream.authrt.model.AuthrtModelId;

/***
 * 권한 JPA 레파지토리 인터페이스
 */
@Repository
public interface AuthrtRepository extends JpaRepository<AuthrtModel, AuthrtModelId>, CustomAuthrtRepository {
}