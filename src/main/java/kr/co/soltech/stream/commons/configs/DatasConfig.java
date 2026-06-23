package kr.co.soltech.stream.commons.configs;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.lang.NonNull;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import kr.co.soltech.stream.commons.utils.SoltechStreamUtils;
import lombok.extern.slf4j.Slf4j;

/***
 * 데이터 관련 설정 클래스
 */
@Slf4j
@EnableCaching
@EnableJpaAuditing
@EntityScan("kr.co.soltech.stream")
@Configuration
public class DatasConfig {
	/***
	 * 데이터베이스 드라이버
	 */
	private final String datasourceDriver;
	/***
	 * 데이터베이스 URL
	 */
	private final String datasourceUrl;
	/***
	 * 데이터베이스 ID
	 */
	private final String datasourceUsername;
	/***
	 * 데이터베이스 패스워드
	 */
	private final String datasourcePassword;
	/***
	 * 데이터베이스 커넥션풀 최대 사이즈
	 */
	private final int maximumPoolSize;

	/***
	 * 생성자
	 * 
	 * @param secretKey          : 데이터베이스 패스워드 암호화를 풀기 위한 KEY
	 * @param algorithm          : 데이터베이스 암호화하는데 선택된 알고리즘
	 * @param datasourceDriver   : 데이터베이스 드라이버
	 * @param datasourceUrl      : 데이터베이스 URL
	 * @param datasourceUsername : 데이터베이스 ID
	 * @param datasourcePassword : 데이터베이스 패스워드
	 * @param maximumPoolSize    : 데이터베이스 커넥션풀 최대 사이즈
	 */
	public DatasConfig(@Value("${soltech.stream.secret.key:[SECRET_KEY]}") String secretKey,
			@Value("${soltech.stream.secret.algorithm:AES}") String algorithm,
			@Value("${spring.datasource.driver-class-name}") String datasourceDriver,
			@Value("${spring.datasource.url}") String datasourceUrl,
			@Value("${spring.datasource.username}") String datasourceUsername,
			@Value("${spring.datasource.password}") String datasourcePassword,
			@Value("${spring.datasource.hikari.maximum-pool-size:10}") int maximumPoolSize) {
		this.datasourceDriver = datasourceDriver;
		this.datasourceUrl = datasourceUrl;
		this.datasourceUsername = datasourceUsername;
		try {
			if (!ObjectUtils.isEmpty(algorithm)) {
				datasourcePassword = SoltechStreamUtils.decode(algorithm, secretKey, datasourcePassword);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			this.datasourcePassword = datasourcePassword;
		}
		this.maximumPoolSize = maximumPoolSize;
	}

	/***
	 * 데이터베이스 연결
	 * 
	 * @return 데이터베이스에 연결된 데이터소스
	 * @throws Exception
	 */
	@Bean
	DataSource decodeDatasource() throws Exception {
		HikariConfig hikariConfig = new HikariConfig(); // DriverManagerDataSource();
		hikariConfig.setDriverClassName(datasourceDriver);
		hikariConfig.setJdbcUrl(datasourceUrl);
		hikariConfig.setUsername(datasourceUsername);
		hikariConfig.setPassword(datasourcePassword);
		hikariConfig.setMaximumPoolSize(maximumPoolSize);

		return new HikariDataSource(hikariConfig);
	}

	/***
	 * Query시 ehCache를 생성할 때 캐시의 key 생성(url관련)
	 * 
	 * @return 캐싱 key
	 */
	@Bean("urlCacheKeyGenerator")
	KeyGenerator urlCacheKeyGenerator() {
		return new KeyGenerator() {
			@Override
			public @NonNull String generate(@NonNull Object target, @NonNull Method method, @NonNull Object... params) {
				List<String> paramList = new ArrayList<String>();
				for (Object obj : params) {
					paramList.add((String) obj);
				}
				StringBuilder stringBuilder = new StringBuilder();
				for (String key : paramList) {
					stringBuilder.append(key.replace("/", "-")).append("-");
				}

				return stringBuilder.toString();
			}
		};
	}

	@Bean
	ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

		return objectMapper;
	}
}
