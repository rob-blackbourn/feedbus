package net.jetblack.feedbus.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * A message which sends data to all subscribers of the feed and topic.
 */
public class MulticastData extends Message {

	private final String _feed;
	private final String _topic;
	private final boolean _isImage;
	private final byte[] _data;

	/**
	 * Construct a multicast message
	 * 
	 * @param feed The name of the feed.
	 * @param topic The name of the topic.
	 * @param isImage If true the data represents the full image, otherwise it is a delta.
	 * @param data The data transmitted.
	 */
    public MulticastData(String feed, String topic, boolean isImage, byte[] data) {
    	super(MessageType.MulticastData);
	    _feed = feed;
	    _topic = topic;
	    _isImage = isImage;
	    _data = data;
	}

    /**
     * Read the body of a multicast message.
     * 
     * @param stream The stream from which to read.
     * @return The multicast message read from the stream.
     * @throws IOException Thrown if the message could not be read.
     */
	public static MulticastData readBody(DataInputStream stream) throws IOException {
	    String feed = stream.readUTF();
	    String topic = stream.readUTF();
	    boolean isImage = stream.readBoolean();
	    int len = stream.readInt();
	    byte[] data = len == 0 ? null : new byte[len];
	    if (len > 0) {
	    	stream.readFully(data);
	    }
	    
	    return new MulticastData(feed, topic, isImage, data);
	}
	
	@Override
	public void write(DataOutputStream stream) throws IOException {
	    super.write(stream);
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
	
	public String getFeed() {
		return _feed;
	}
	
	public String getTopic() {
		return _topic;
	}
	
	public boolean isImage() {
		return _isImage;
	}
	
	public byte[] getData() {
		return _data;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
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
		MulticastData other = (MulticastData) obj;
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
	    return super.toString() + ", Feed=\"" + _feed + "\", Topic=\"" + _topic + "\", IsImage=" + _isImage + ", Data=" + _data;
	}
	
}
