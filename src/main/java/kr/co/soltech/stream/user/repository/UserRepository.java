package kr.co.soltech.stream.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.soltech.stream.user.model.UserModel;

/***
 * 사용자 JPA 레파지토리 인터페이스
 */
@Repository
public interface UserRepository extends JpaRepository<UserModel, String>, CustomUserRepository {
}