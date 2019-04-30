package net.jetblack.feedbus.json;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.jetblack.feedbus.adapters.ByteSerializable;

public class JsonSerializer implements ByteSerializable {

	private final Gson _gson;

	public JsonSerializer() {
		_gson = new GsonBuilder()
				.disableHtmlEscaping() // This will return '=' instead of '\u003d'.
				.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
				.create();
	}
	
	@Override
	public Object deserialize(byte[] bytes) throws Exception {
		String json = new String(bytes, StandardCharsets.UTF_8);
		return _gson.fromJson(json, Map.class);
	}

	@Override
	public byte[] serialize(Object data) throws Exception {
		String json = _gson.toJson(data);
		return json.getBytes(StandardCharsets.UTF_8);
	}

}
