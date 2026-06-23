package kr.co.soltech.stream.commons.model;

import java.io.IOException;

import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import kr.co.soltech.stream.commons.utils.SoltechStreamUtils;

public class CustomDateSerializer extends JsonSerializer<String> {
	@Override
	public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		if (!ObjectUtils.isEmpty(value)) {
			String result = SoltechStreamUtils.parseDateToString(value, "yyyy-MM-dd");
			result = ObjectUtils.isEmpty(result) ? "" : result;
			gen.writeString(result);
		} else {
			gen.writeString(value);
		}
	}
}