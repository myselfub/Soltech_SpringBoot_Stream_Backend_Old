package kr.co.soltech.stream.utils;

import org.junit.jupiter.api.Test;

import kr.co.soltech.stream.commons.utils.SoltechStreamUtils;

public class CreateBase62 {
	@Test
	public void createBase62() {
		long longData = System.currentTimeMillis();
		String base62 = createBase62Logic(longData);
		System.out.println(base62);
		System.out.println(createBase62Logic(longData + 1));
	}

	private String createBase62Logic(long longData) {
		String hexData = Long.toHexString(longData);
		String base62 = null;
		try {
			base62 = SoltechStreamUtils.base62Encode(hexData);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return base62;
	}
}