package net.jetblack.feedbus.distributor.publishers;

import java.util.Set;

import net.jetblack.feedbus.distributor.interactors.Interactor;
import net.jetblack.feedbus.distributor.interactors.InteractorEventArgs;
import net.jetblack.feedbus.messages.FeedTopic;

/**
 * The stale publisher event.
 */
public class StalePublisherEventArgs extends InteractorEventArgs {

    private final Set<FeedTopic> _feedsAndTopics;

    /**
     * Construct the event.
     * @param interactor The interactor that is stale.
     * @param feedsAndTopics The feeds and topics published by the interactor.
     */
    public StalePublisherEventArgs(Interactor interactor, Set<FeedTopic> feedsAndTopics) {
        super(interactor);
        _feedsAndTopics = feedsAndTopics;
    }

    /**
     * Gets the feeds and topics.
     * @return The feeds and topics.
     */
    public Set<FeedTopic> getFeedsAndTopics() {
    	return _feedsAndTopics;
    }

	@Override
	public String toString() {
	    return super.toString() + ", FeedsAndTopics=[" + _feedsAndTopics + "]";
	}
}
