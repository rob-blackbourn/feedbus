package net.jetblack.feedbus.adapters;

/**
 * A class representing an error in the serialization of data.
 */
public class DataErrorEventArgs {

    private final boolean _isSending;
    private final String _feed;
    private final String _topic;
    private final boolean _isImage;
    private final Object _data;
    private final Exception _error;

    /**
     * Construct the class.
     * 
     * @param isSending If true the error was in sending, otherwise in receiving.
     * @param feed The name of the feed.
     * @param topic The name of the topic.
     * @param isImage If true the data was an image, otherwise a delta.
     * @param data The data being sent or received.
     * @param error The exception that was thrown.
     */
    public DataErrorEventArgs(boolean isSending, String feed, String topic, boolean isImage, Object data, Exception error) {
        _isSending = isSending;
        _feed = feed;
        _topic = topic;
        _isImage = isImage;
        _data = data;
        _error = error;
    }

    public boolean getIsSending() {
    	return _isSending;
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
    
    public Exception getError() {
    	return _error;
    }
}
