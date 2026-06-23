package kr.co.soltech.stream.utils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class CreatePw {
	/***
	 * 패스워드 인코더 인터페이스
	 */
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	public void createPassword() {
		String pw = "[PASSWORD]";
		String encodePw = passwordEncoder.encode(pw);

		System.out.println(encodePw);
	}
}
