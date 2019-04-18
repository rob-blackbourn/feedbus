package net.jetblack.feedbus.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A subscription request.
 */
public class SubscriptionRequest extends Message {

	private final String _feed;
	private final String _topic;
	private final boolean _isAdd;

	/**
	 * Construct a subscription request.
	 * 
	 * @param feed The name of the feed.
	 * @param topic The name of the topic.
	 * @param isAdd If true the subscription should be added, otherwise it should be removed.
	 */
    public SubscriptionRequest(String feed, String topic, boolean isAdd) {
        super(MessageType.SubscriptionRequest);
	    _feed = feed;
	    _topic = topic;
	    _isAdd = isAdd;
	}
	
    /**
     * Read the body of the subscription request.
     * 
     * @param stream The stream from which to read.
     * @return The subscription request read from the stream.
     * @throws IOException Thrown if the message could not be read.
     */
	public static SubscriptionRequest readBody(DataInputStream stream) throws IOException {
	    String feed = stream.readUTF();
	    String topic = stream.readUTF();
	    boolean isAdd = stream.readBoolean();
	    return new SubscriptionRequest(feed, topic, isAdd);
	}
	
	@Override
	public void write(DataOutputStream stream) throws IOException {
	    super.write(stream);
	    stream.writeUTF(_feed);
	    stream.writeUTF(_topic);
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
	 * The name of the topic.
	 * 
	 * @return The topic name.
	 */
	public String getTopic() {
		return _topic;
	}
	
	/**
	 * If true add the subscription, otherwise remove it.
	 * 
	 * @return The intention of the subscription.
	 */
	public boolean getIsAdd() {
		return _isAdd;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((_feed == null) ? 0 : _feed.hashCode());
		result = prime * result + (_isAdd ? 1231 : 1237);
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
		SubscriptionRequest other = (SubscriptionRequest) obj;
		if (_feed == null) {
			if (other._feed != null)
				return false;
		} else if (!_feed.equals(other._feed))
			return false;
		if (_isAdd != other._isAdd)
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
	    return super.toString() + ", Feed=\"" + _feed + "\", Topic=\"" + _topic + "\", IsAdd=" + _isAdd;
	}
}
