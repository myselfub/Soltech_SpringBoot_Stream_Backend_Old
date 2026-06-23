package kr.co.soltech.stream.commons.configs;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.extern.slf4j.Slf4j;

/***
 * Stream 설정 클래스
 */
@Slf4j
@Configuration
public class StreamConfig implements WebMvcConfigurer {
	/***
	 * 파일 절대 경로
	 */
	private final String FILE_ABSOLUTE_PATH;

	/***
	 * 페이지당 크기
	 */
	private final int PAGE_PER_SIZE = 30;

	/***
	 * 생성자
	 * 
	 * @param FILE_ABSOLUTE_PATH : 파일 절대 경로
	 */
	public StreamConfig(@Value("${soltech.stream.file.absolute.path}") String FILE_ABSOLUTE_PATH) {
		this.FILE_ABSOLUTE_PATH = FILE_ABSOLUTE_PATH;
	}

	/***
	 * 파일 업로드/다운로드 기본 경로
	 * 
	 * @return File
	 */
	@Bean
	@Primary
	File streamFileDirectory() {
		File fileDirectory = new File(FILE_ABSOLUTE_PATH);

		if (!fileDirectory.exists()) {
			fileDirectory.mkdirs();
		}

		return fileDirectory;
	}

	/***
	 * Pageable 파라메터 자동 설정
	 */
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		PageableHandlerMethodArgumentResolver pageableResolver = new PageableHandlerMethodArgumentResolver();
		pageableResolver.setFallbackPageable(PageRequest.of(0, PAGE_PER_SIZE));
		resolvers.add(pageableResolver);
	}
}