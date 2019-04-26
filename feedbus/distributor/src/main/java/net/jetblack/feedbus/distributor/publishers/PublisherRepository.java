package net.jetblack.feedbus.distributor.publishers;

import java.util.HashSet;
import java.util.Set;

import net.jetblack.feedbus.distributor.interactors.Interactor;
import net.jetblack.feedbus.messages.FeedTopic;
import net.jetblack.feedbus.util.TwoWaySet;

/**
 * The publisher repository.
 */
public class PublisherRepository {

    private final TwoWaySet<FeedTopic, Interactor> _topicsAndPublishers = new TwoWaySet<FeedTopic, Interactor>();

    /**
     * Add a publisher.
     * @param publisher The publisher.
     * @param feed The feed on which it has published.
     * @param topic The topic on which it has published.
     */
    public void addPublisher(Interactor publisher, String feed, String topic) {
        _topicsAndPublishers.addSecondAndFirst(publisher, new FeedTopic(feed, topic));
    }

    /**
     * Remove a publisher.
     * @param publisher The publisher to remove.
     * @return true if topics were removed.
     */
    public Set<FeedTopic> removePublisher(Interactor publisher) {
    	Set<FeedTopic> removedTopics = _topicsAndPublishers.removeSecond(publisher);
        return removedTopics != null ? removedTopics : new HashSet<FeedTopic>();
    }

}
