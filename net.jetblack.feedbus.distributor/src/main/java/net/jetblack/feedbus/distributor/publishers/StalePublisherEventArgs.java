package net.jetblack.feedbus.distributor.publishers;

import java.util.Set;

import net.jetblack.feedbus.distributor.interactors.Interactor;
import net.jetblack.feedbus.distributor.interactors.InteractorEventArgs;
import net.jetblack.feedbus.messages.FeedTopic;

public class StalePublisherEventArgs extends InteractorEventArgs {

    private final Set<FeedTopic> _feedsAndTopics;

    public StalePublisherEventArgs(Interactor interactor, Set<FeedTopic> feedsAndTopics) {
        super(interactor);
        _feedsAndTopics = feedsAndTopics;
    }

    public Set<FeedTopic> getFeedsAndTopics() {
    	return _feedsAndTopics;
    }

	@Override
	public String toString() {
	    return super.toString() + ", FeedsAndTopics=[" + _feedsAndTopics + "]";
	}
}
