package net.jetblack.feedbus.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * A message that sends data to a single client.
 */
public class UnicastData extends Message {

	private final String _clientId;
	private final String _feed;
	private final String _topic;
	private final boolean _isImage;
	private final byte[] _data;

	/**
	 * Constructs a unicast message.
	 * 
	 * @param clientId The id of the client, usually provided by a notification request.
	 * @param feed The name of the feed.
	 * @param topic The name of the topic.
	 * @param isImage If true the data represents a complete image, otherwise it is a delta.
	 * @param data The message data.
	 */
    public UnicastData(String clientId, String feed, String topic, boolean isImage, byte[] data) {
        super(MessageType.UnicastData);
	    _clientId = clientId;
	    _feed = feed;
	    _topic = topic;
	    _isImage = isImage;
	    _data = data;
	}
	
    /**
     * Read the body of a unicast message.
     * 
     * @param stream The stream to read from.
     * @return A unicast message.
     * @throws IOException Thrown when the message cannot be read.
     */
	public static UnicastData readBody(DataInputStream stream) throws IOException {
	    String clientId = stream.readUTF();
	    String feed = stream.readUTF();
	    String topic = stream.readUTF();
	    boolean isImage = stream.readBoolean();
	    int len = stream.readInt();
	    byte[] data = len == 0 ? null : new byte[len];
	    if (len > 0) {
	    	stream.readFully(data);
	    }
	    return new UnicastData(clientId, feed, topic, isImage, data);
	}

	@Override
	public void write(DataOutputStream stream) throws IOException {
	    super.write(stream);
	    stream.writeUTF(_clientId);
	    stream.writeUTF(_feed);
	    stream.writeUTF(_topic);
	    stream.writeBoolean(_isImage);
	    if (_data == null) {
		    stream.writeInt(0);
	    } else {
		    stream.writeInt(_data.length);
		    stream.write(_data);
	    }
	}

	/**
	 * The client identifier.
	 * 
	 * @return The client identifier.
	 */
	public String getClientId() {
		return _clientId;
	}
	
	/**
	 * The feed name.
	 * 
	 * @return The feed name.
	 */
	public String getFeed() {
		return _feed;
	}
	
	/**
	 * The topic name.
	 * 
	 * @return The topic name.
	 */
	public String getTopic() {
		return _topic;
	}
	
	/**
	 * If the data represents the complete image true, otherwise false.
	 * 
	 * @return The state of the data.
	 */
	public boolean isImage() {
		return _isImage;
	}
	
	/**
	 * The actual data.
	 * 
	 * @return The transmitted data.
	 */
	public byte[] getData() {
		return _data;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((_clientId == null) ? 0 : _clientId.hashCode());
		result = prime * result + Arrays.hashCode(_data);
		result = prime * result + ((_feed == null) ? 0 : _feed.hashCode());
		result = prime * result + (_isImage ? 1231 : 1237);
		result = prime * result + ((_topic == null) ? 0 : _topic.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		UnicastData other = (UnicastData) obj;
		if (_clientId == null) {
			if (other._clientId != null)
				return false;
		} else if (!_clientId.equals(other._clientId))
			return false;
		if (!Arrays.equals(_data, other._data))
			return false;
		if (_feed == null) {
			if (other._feed != null)
				return false;
		} else if (!_feed.equals(other._feed))
			return false;
		if (_isImage != other._isImage)
			return false;
		if (_topic == null) {
			if (other._topic != null)
				return false;
		} else if (!_topic.equals(other._topic))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
	    return super.toString() + ", ClientId=" + _clientId + ", Feed=" + _feed + ", Topic=" + _topic + ", IsImage=" + _isImage + ", Data=" + _data;
	}
}
