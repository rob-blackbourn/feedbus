package net.jetblack.feedbus.adapters;

import net.jetblack.util.EventArgs;

public class DataReceivedEventArgs extends EventArgs {

    private final String _feed;
    private final String _topic;
    private final boolean _isImage;
    private final Object _data;

    public DataReceivedEventArgs(String feed, String topic, Object data, boolean isImage) {
        _feed = feed;
        _topic = topic;
        _isImage = isImage;
        _data = data;
    }

    public String getFeed() {
    	return _feed;
    }
    
    public String getTopic() {
    	return _topic;
    }
    
    public boolean getIsImage() {
    	return _isImage;
    }
    
    public Object getData() {
    	return _data;
    }
}
