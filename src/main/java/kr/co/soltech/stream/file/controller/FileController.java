package kr.co.soltech.stream.file.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.soltech.stream.commons.model.CustomExceptionDTO;
import kr.co.soltech.stream.commons.service.CustomResponseAPI;
import kr.co.soltech.stream.commons.utils.SoltechStreamUtils;
import kr.co.soltech.stream.file.model.FileModel;
import kr.co.soltech.stream.file.model.FileParamDTO;
import kr.co.soltech.stream.file.service.FileService;
import lombok.RequiredArgsConstructor;

/***
 * 파일 컨트롤러 클래스
 */
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {
	/***
	 * 파일 서비스 인터페이스
	 */
	private final FileService fileService;

	/***
	 * 파일 목록 조회
	 * 
	 * @param fileParamDTO  : 파일 조회 파라메터 DTO 클래스
	 * @param pageable      : 페이징 정보
	 * @param bindingResult : 검증 결과
	 * @return 파일 목록
	 * @throws Exception
	 */
	@GetMapping
	@CustomResponseAPI
	public Page<FileModel> inqFile(@ModelAttribute @Validated FileParamDTO fileParamDTO, Pageable pageable,
			BindingResult bindingResult) throws Exception {
		fileParamDTO.deserialize();

		return fileService.inqFile(fileParamDTO, pageable);
	}

	/***
	 * 파일 상세 조회
	 * 
	 * @param fileParamDTO  : 파일 조회 파라메터 DTO 클래스
	 * @param bindingResult : 검증 결과
	 * @return 파일 상세
	 * @throws Exception
	 */
	@GetMapping("/dtl")
	@CustomResponseAPI
	public FileModel getFile(@ModelAttribute @Validated FileParamDTO fileParamDTO, BindingResult bindingResult)
			throws Exception {
		if (bindingResult.hasErrors()) {
			throw CustomExceptionDTO.of(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}
		if (ObjectUtils.isEmpty(fileParamDTO.getFileId())) {
			throw CustomExceptionDTO.of("ERROR_0001", "fileId", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}

		fileParamDTO.deserialize();

		return fileService.getFile(fileParamDTO);
	}

	/***
	 * 파일 삭제
	 * 
	 * @param fileParamDTO  : 파일 조회 파라메터 DTO 클래스
	 * @param bindingResult : 검증 결과
	 * @return 성공/에러
	 * @throws Exception
	 */
	@DeleteMapping
	@CustomResponseAPI(status = HttpStatus.NO_CONTENT)
	public String deleteFile(@ModelAttribute @Validated FileParamDTO fileParamDTO, BindingResult bindingResult)
			throws Exception {
		if (bindingResult.hasErrors()) {
			throw CustomExceptionDTO.of(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}

		fileParamDTO.deserialize();

		FileModel fileModel = FileModel.builder().fileId(fileParamDTO.getFileId()).build();
		fileModel.preUpdate();

		int updatedRows = fileService.deleteFile(fileModel);
		if (updatedRows == 0) {
			throw CustomExceptionDTO.of("ERROR_0000", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("No matching results."));
		}

		return "SUCCESS_0000";
	}

	/***
	 * 파일 다운로드
	 * 
	 * @param fileParamDTO  : 파일 조회 파라메터 DTO 클래스
	 * @param bindingResult : 검증 결과
	 * @return 파일 목록
	 * @throws Exception
	 */
	@GetMapping("/dwnld")
	@CustomResponseAPI
	public ResponseEntity<StreamingResponseBody> dwnldFile(@ModelAttribute @Validated FileParamDTO fileParamDTO,
			BindingResult bindingResult, HttpServletRequest request) throws Exception {
		if (bindingResult.hasErrors()) {
			throw CustomExceptionDTO.of(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}
		if (ObjectUtils.isEmpty(fileParamDTO.getFileId())) {
			throw CustomExceptionDTO.of("ERROR_0001", "fileId", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}

		FileModel fileModel = fileService.getFile(fileParamDTO);
		if (ObjectUtils.isEmpty(fileModel) || ObjectUtils.isEmpty(fileModel.getFileId())
				|| ObjectUtils.isEmpty(fileModel.getFilePath())) {
			throw CustomExceptionDTO.of("ERROR_0001", "fileId", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}

		String contentDisposition = SoltechStreamUtils.getContentDisposition(request.getHeader("User-Agent"),
				fileModel.getOrgnflNm());

		Path filePath = Paths.get(fileModel.getFilePath()).normalize();
		File file = filePath.toFile();

		if (!file.exists() || !file.canRead()) {
			throw CustomExceptionDTO.of("ERROR_0000", HttpStatus.INTERNAL_SERVER_ERROR.value(),
					new FileNotFoundException("File not found Error"));
		}

		StreamingResponseBody streamingResponseBody = outputStream -> {
			try (InputStream inputStream = new FileInputStream(file)) {
				byte[] buffer = new byte[10240];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
			}
		};

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition).body(streamingResponseBody);
	}
}