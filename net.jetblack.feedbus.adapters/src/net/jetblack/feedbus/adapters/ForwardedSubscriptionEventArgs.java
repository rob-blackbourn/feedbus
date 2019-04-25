package net.jetblack.feedbus.adapters;

public class ForwardedSubscriptionEventArgs {

    private final String _clientId;
    private final String _feed;
    private final String _topic;
    private final boolean _isAdd;

    public ForwardedSubscriptionEventArgs(String clientId, String feed, String topic, boolean isAdd) {
        _clientId = clientId;
        _feed = feed;
        _topic = topic;
        _isAdd = isAdd;
    }

    public String getClientId() {
    	return _clientId;
    }
    
    public String getFeed() {
    	return _feed;
    }
    
    public String getTopic() {
    	return _topic;
    }
    
    public boolean getIsAdd() {
    	return _isAdd;
    }
}
