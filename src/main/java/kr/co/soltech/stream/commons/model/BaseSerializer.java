package kr.co.soltech.stream.commons.model;

public interface BaseSerializer {
	/***
	 * 데이터를 클라이언트로 보내기 전 포맷 변경
	 */
	public void serialize();

	/***
	 * 데이터를 클라이언트에서 가져오고 포맷 변경
	 */
	public void deserialize();
}