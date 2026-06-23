package kr.co.soltech.stream.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.soltech.stream.file.model.FileModel;

/***
 * 파일 JPA 레파지토리 인터페이스
 */
@Repository
public interface FileRepository extends JpaRepository<FileModel, String>, CustomFileRepository {
}