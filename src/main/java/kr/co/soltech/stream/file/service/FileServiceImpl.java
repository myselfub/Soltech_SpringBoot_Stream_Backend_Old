package kr.co.soltech.stream.file.service;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import kr.co.soltech.stream.file.model.FileModel;
import kr.co.soltech.stream.file.model.FileParamDTO;
import kr.co.soltech.stream.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;

/***
 * 파일 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
	/***
	 * 파일 JPA 레파지토리 인터페이스
	 */
	private final FileRepository fileRepository;

	/***
	 * 파일 업로드/다운로드 기본 경로
	 */
	@Qualifier("streamFileDirectory")
	private final File streamFileDirectory;

	/***
	 * 파일 목록 조회
	 */
	@Override
	public Page<FileModel> inqFile(FileParamDTO fileParamDTO, Pageable pageable) throws Exception {
		return fileRepository.inqFile(fileParamDTO, pageable);
	}

	/***
	 * 파일 상세 조회
	 */
	@Override
	public FileModel getFile(FileParamDTO fileParamDTO) throws Exception {
		return fileRepository.getFile(fileParamDTO);
	}

	/***
	 * 파일 등록/수정
	 */
	@CacheEvict(value = "fileCnt", allEntries = true)
	@Transactional
	@Override
	public int upsertFile(FileModel fileModel) throws Exception {
		return fileRepository.upsertFile(fileModel);
	}

	/***
	 * 다중 파일 등록/수정
	 */
	@CacheEvict(value = "fileCnt", allEntries = true)
	@Transactional
	@Override
	public int upsertAllFile(List<FileModel> fileModelList) throws Exception {
		return fileRepository.upsertAllFile(fileModelList);
	}

	/***
	 * 파일 삭제
	 */
	@CacheEvict(value = "fileCnt", allEntries = true)
	@Transactional
	@Override
	public int deleteFile(FileModel fileModel) throws Exception {
		return fileRepository.deleteFile(fileModel);
	}

	/***
	 * 파일 관련 기본 경로 가져오기
	 */
	@Override
	public String getDefaultFilePath() throws Exception {
		return streamFileDirectory.getAbsolutePath();
	}
}