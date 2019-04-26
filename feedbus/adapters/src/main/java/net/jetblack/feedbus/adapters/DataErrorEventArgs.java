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

    /**
     * Returns true if sending.
     * @return true if sending.
     */
    public boolean isSending() {
    	return _isSending;
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
     * Gets whether the data represents an image.
     * @return true of the data represents an image.
     */
    public boolean isImage() {
    	return _isImage;
    }
    
    /**
     * Gets the data.
     * @return The data.
     */
    public Object getData() {
    	return _data;
    }
    
    /**
     * Gets the error.
     * @return The error.
     */
    public Exception getError() {
    	return _error;
    }
}
