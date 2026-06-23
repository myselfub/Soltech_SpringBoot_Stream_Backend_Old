package kr.co.soltech.stream.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.soltech.stream.menu.model.MenuModel;

/***
 * 메뉴 JPA 레파지토리 인터페이스
 */
@Repository
public interface MenuRepository extends JpaRepository<MenuModel, String>, CustomMenuRepository {
}