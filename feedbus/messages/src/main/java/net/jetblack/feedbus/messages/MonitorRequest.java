package net.jetblack.feedbus.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Requests all published data on a feed.
 */
public class MonitorRequest extends Message {
	
	private final String _feed;
	private final boolean _isAdd;

	/**
	 * Construct a monitor request.
	 * 
	 * @param feed The name of a feed.
	 * @param isAdd If true the monitor is added, otherwise it is removed.
	 */
	public MonitorRequest(String feed, boolean isAdd) {
    	super(MessageType.MonitorRequest);
	    _feed = feed;
	    _isAdd = isAdd;
	}
	
	/**
	 * Read the body of the message.
	 * 
	 * @param stream The stream from which to read.
	 * @return The message read from the stream.
	 * @throws IOException Thrown if the message could not be read.
	 */
	public static MonitorRequest readBody(DataInputStream stream) throws IOException {
	    String feed = stream.readUTF();
	    boolean isAdd = stream.readBoolean();
	    return new MonitorRequest(feed, isAdd);
	}
	
	@Override
	public void write(DataOutputStream stream) throws IOException {
	    super.write(stream);
	    stream.writeUTF(_feed);
	    stream.writeBoolean(_isAdd);
	}
	
	/**
	 * The name of the feed.
	 * 
	 * @return The feed name.
	 */
	public String getFeed() {
		return _feed;
	}
	
	/**
	 * If true the monitor should be added, otherwise it should be removed.
	 * 
	 * @return The intention of the request.
	 */
	public boolean isAdd() {
		return _isAdd;
	}

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((_feed == null) ? 0 : _feed.hashCode());
		result = prime * result + (_isAdd ? 1231 : 1237);
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
		MonitorRequest other = (MonitorRequest) obj;
		if (_feed == null) {
			if (other._feed != null)
				return false;
		} else if (!_feed.equals(other._feed))
			return false;
		if (_isAdd != other._isAdd)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
	    return super.toString() + ", Feed=\"" + _feed + "\", IsAdd=" + _isAdd;
	}
}
