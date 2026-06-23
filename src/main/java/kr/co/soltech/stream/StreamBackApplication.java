package kr.co.soltech.stream;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import kr.co.soltech.stream.commons.configs.ServletInitializer;

@SpringBootApplication
public class StreamBackApplication extends ServletInitializer {
	public static void main(String[] args) {
		// 기본 타임존 설정
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
		SpringApplication.run(StreamBackApplication.class, args);
	}
}