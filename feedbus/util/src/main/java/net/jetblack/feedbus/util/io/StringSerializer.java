package net.jetblack.feedbus.util.io;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Serialize a string to bytes.
 */
public class StringSerializer implements ByteSerializable {

	private final Charset _charset;

	/**
	 * Construct the serializer with the UTF-8 encoding.
	 */
	public StringSerializer() {
		_charset = StandardCharsets.UTF_8;
	}

	/**
	 * Construct the serializer with a given encoding.
	 * @param charset The encoding.
	 */
	public StringSerializer(Charset charset) {
		_charset = charset;
	}

	@Override
	public Object deserialize(byte[] bytes) throws Exception {
		return new String(bytes, _charset);
	}

	@Override
	public byte[] serialize(Object data) throws Exception {
		// TODO Auto-generated method stub
		return ((String)data).getBytes(_charset);
	}

}
