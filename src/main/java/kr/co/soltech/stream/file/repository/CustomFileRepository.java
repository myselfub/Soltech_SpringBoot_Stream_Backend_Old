package kr.co.soltech.stream.file.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.soltech.stream.file.model.FileModel;
import kr.co.soltech.stream.file.model.FileParamDTO;

/***
 * 파일 커스텀 레파지토리 인터페이스
 */
interface CustomFileRepository {
	/***
	 * 파일 목록 조회
	 * 
	 * @param fileParamDTO : 파일 조회 파라메터 DTO 클래스
	 * @param pageable     : 페이징 정보
	 * @return 파일 목록
	 * @throws Exception
	 */
	public Page<FileModel> inqFile(FileParamDTO fileParamDTO, Pageable pageable) throws Exception;

	/***
	 * 파일 상세 조회
	 * 
	 * @param fileParamDTO : 파일 조회 파라메터 DTO 클래스
	 * @return 파일 상세
	 * @throws Exception
	 */
	public FileModel getFile(FileParamDTO fileParamDTO) throws Exception;

	/***
	 * 파일 등록/수정
	 * 
	 * @param fileModel : 파일 모델 클래스
	 * @return 업데이트 로우 수
	 * @throws Exception
	 */
	public int upsertFile(FileModel fileModel) throws Exception;

	/***
	 * 다중 파일 등록/수정
	 * 
	 * @param fileModelList : 파일 모델 클래스 목록
	 * @return 업데이트 로우 수
	 * @throws Exception
	 */
	public int upsertAllFile(List<FileModel> fileModelList) throws Exception;

	/***
	 * 파일 삭제
	 * 
	 * @param fileModel : 파일 모델 클래스
	 * @return 업데이트 로우 수
	 * @throws Exception
	 */
	public int deleteFile(FileModel fileModel) throws Exception;
}