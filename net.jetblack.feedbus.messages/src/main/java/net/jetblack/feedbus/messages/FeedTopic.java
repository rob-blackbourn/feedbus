package net.jetblack.feedbus.messages;

/**
 * A class which holds the name of the feed and the topic.
 */
public class FeedTopic {

	private final String _feed;
	private final String _topic;

	/**
	 * Construct the feed and topic.
	 * 
	 * @param feed The name of the feed.
	 * @param topic The name of the topic.
	 */
	public FeedTopic(String feed, String topic) {
		if (feed == null) {
			throw new IllegalArgumentException("feed");
		}
		if (topic == null) {
			throw new IllegalArgumentException("topic");
		}

		_feed = feed;
		_topic = topic;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_feed == null) ? 0 : _feed.hashCode());
		result = prime * result + ((_topic == null) ? 0 : _topic.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FeedTopic other = (FeedTopic) obj;
		if (_feed == null) {
			if (other._feed != null)
				return false;
		} else if (!_feed.equals(other._feed))
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
		return "Feed=\"" + _feed + "\", Topic=\"" + _topic + "\"";
	}
}
