package net.jetblack.feedbus.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A subscription request that has been forwarded due to a notification request.
 */
public class ForwardedSubscriptionRequest extends Message {

	private final String _clientId;
    private final String _feed;
    private final String _topic;
    private final boolean _isAdd;

    /**
     * Construct the message.
     * 
     * @param clientId The identity of the client.
     * @param feed The name of the feed.
     * @param topic The name of the topic.
     * @param isAdd If true the subscription was added, otherwise it was removed.
     */
	public ForwardedSubscriptionRequest(String clientId, String feed, String topic, boolean isAdd) {
		super(MessageType.ForwardedSubscriptionRequest);
        _clientId = clientId;
        _feed = feed;
        _topic = topic;
        _isAdd = isAdd;
    }

	/**
	 * Read the body of the message from a stream.
	 * 
	 * @param stream The stream from which to read.
	 * @return The message read.
	 * @throws IOException Thrown if the message could not be read.
	 */
    public static ForwardedSubscriptionRequest readBody(DataInputStream stream) throws IOException {
        String clientId = stream.readUTF();
        String feed = stream.readUTF();
        String topic = stream.readUTF();
        boolean isAdd = stream.readBoolean();
        return new ForwardedSubscriptionRequest(clientId, feed, topic, isAdd);
    }

    @Override
    public void write(DataOutputStream stream) throws IOException {
    	super.write(stream);
        stream.writeUTF(_clientId);
        stream.writeUTF(_feed);
        stream.writeUTF(_topic);
        stream.writeBoolean(_isAdd);
    }
    
    /**
     * The identity of the client that made the subscription.
     * 
     * @return The client identity.
     */
    public String getClientId() {
    	return _clientId;
    }
    
    /**
     * The name of the feed.
     * 
     * @return The feed name
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
     * If true the subscription request was added, otherwise it was removed.
     * 
     * @return The state of the original request.
     */
    public boolean isAdd() {
    	return _isAdd;
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((_clientId == null) ? 0 : _clientId.hashCode());
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
		ForwardedSubscriptionRequest other = (ForwardedSubscriptionRequest) obj;
		if (_clientId == null) {
			if (other._clientId != null)
				return false;
		} else if (!_clientId.equals(other._clientId))
			return false;
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
        return super.toString() + ", ClientId=" + _clientId + ", Feed=\"" + _feed + "\", Topic=\"" + _topic + ", IsAdd=" + _isAdd;
    }
}
