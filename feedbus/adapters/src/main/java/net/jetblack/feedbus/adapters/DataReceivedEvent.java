package net.jetblack.feedbus.adapters;

/**
 * Represents data received.
 */
public class DataReceivedEvent {

    private final String _feed;
    private final String _topic;
    private final boolean _isImage;
    private final Object _data;

    /**
     * Constructs the event.
     * @param feed The feed name.
     * @param topic The topic name.
     * @param data The data.
     * @param isImage If tru the data represents an image, otherwise false.
     */
    public DataReceivedEvent(String feed, String topic, Object data, boolean isImage) {
        _feed = feed;
        _topic = topic;
        _isImage = isImage;
        _data = data;
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
     * Gets whether the data is an image or a delta.
     * @return true if the data represents an image; otherwise false.
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
}
