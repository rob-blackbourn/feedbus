package net.jetblack.feedbus.util.io;

/**
 * Serialize into bytes.
 */
public interface ByteSerializable {
	/**
	 * Deserialize the bytes to an object.
	 * @param bytes The bytes to deserialize.
	 * @return The deserialized object.
	 * @throws Exception
	 */
    Object deserialize(byte[] bytes) throws Exception;
    
    /**
     * Serialize the object to bytes.
     * @param data The object to serialize.
     * @return The seriaized bytes.
     * @throws Exception
     */
    byte[] serialize(Object data) throws Exception;
}
