package net.jetblack.feedbus.adapters;

/**
 * Represents a forwarded subscription event.
 */
public class ForwardedSubscriptionEvent {

    private final String _clientId;
    private final String _feed;
    private final String _topic;
    private final boolean _isAdd;

    /**
     * Constructs the event.
     * @param clientId The client id.
     * @param feed The feed name.
     * @param topic The topic name.
     * @param isAdd If true the subscription is added, otherwise it has been removed.
     */
    public ForwardedSubscriptionEvent(String clientId, String feed, String topic, boolean isAdd) {
        _clientId = clientId;
        _feed = feed;
        _topic = topic;
        _isAdd = isAdd;
    }

    /**
     * Gets the client id.
     * @return The client id.
     */
    public String getClientId() {
    	return _clientId;
    }
    
    /**
     * Gets the feed name.
     * @return The feed name.
     */
    public String getFeed() {
    	return _feed;
    }
    
    /**
     * Gets the topic name.
     * @return The topic name.
     */
    public String getTopic() {
    	return _topic;
    }
    
    /**
     * Gets whether the subscription is being added or removed.
     * @return true if the subscription has been added, otherwise false if it is being removed.
     */
    public boolean isAdd() {
    	return _isAdd;
    }
}
