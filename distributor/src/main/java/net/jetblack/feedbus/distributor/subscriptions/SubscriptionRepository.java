package net.jetblack.feedbus.distributor.subscriptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.jetblack.feedbus.util.Enumerable;
import net.jetblack.feedbus.util.KeyValuePair;
import net.jetblack.feedbus.util.invokable.UnaryFunction;
import net.jetblack.feedbus.distributor.interactors.Interactor;
import net.jetblack.feedbus.messages.FeedTopic;

import java.util.Map.Entry;

/**
 * The subscription repository.
 */
public class SubscriptionRepository {

    // Feed->Topic->Interactor->SubscriptionCount.
    private final Map<String, Map<String, Map<Interactor, SubscriptionState>>> _subscriptions = new HashMap<String, Map<String, Map<Interactor, SubscriptionState>>>();
    // Feed->Topic->Interactor->SubscriptionCount.
    private final Map<String, Map<Interactor, SubscriptionState>> _monitors = new HashMap<String, Map<Interactor, SubscriptionState>>();

    /**
     * Add a subscription.
     * 
     * @param subscriber The subscriber.
     * @param feed The feed name.
     * @param topic The topic name.
     */
    public void addSubscription(Interactor subscriber, String feed, String topic) {
        // Find topic subscriptions for this feed.
        Map<String, Map<Interactor, SubscriptionState>> topicSubscriptions = _subscriptions.get(feed);
        if (topicSubscriptions == null) {
            _subscriptions.put(feed, topicSubscriptions = new HashMap<String, Map<Interactor, SubscriptionState>>());
        }

        // Find the list of interactors that have subscribed to this topic.
        Map<Interactor, SubscriptionState> subscribersForTopic = topicSubscriptions.get(topic);
        if (subscribersForTopic == null) {
            topicSubscriptions.put(topic, subscribersForTopic = new HashMap<Interactor, SubscriptionState>());
        }

        // Find this interactor.
        SubscriptionState subscriptionState = subscribersForTopic.get(subscriber);
        if (subscriptionState == null) {
            subscribersForTopic.put(subscriber, subscriptionState = new SubscriptionState());
        }

        // Increment the subscription count.
        subscriptionState.Count = subscriptionState.Count + 1;
    }

    /**
     * Remove a subscription.
     * @param subscriber The subscriber.
     * @param feed The feed name.
     * @param topic The topic name.
     * @param removeAll If true remove all subscriptions.
     */
    public void removeSubscription(Interactor subscriber, String feed, String topic, boolean removeAll) {
        // Can we find topic subscriptions this feed?
        Map<String, Map<Interactor, SubscriptionState>> topicSubscriptions = _subscriptions.get(feed);
        if (topicSubscriptions == null) {
            return;
        }

        // Can we find subscribers for this topic?
        Map<Interactor, SubscriptionState> subscribersForTopic = topicSubscriptions.get(topic); 
        if (subscribersForTopic == null) {
            return;
        }

        // Has this subscriber registered an interest in the topic?
        SubscriptionState subscriptionState = subscribersForTopic.get(subscriber);
        if (subscriptionState == null)
            return;

        if (removeAll || --subscriptionState.Count == 0)
            subscribersForTopic.remove(subscriber);

        // If there are no subscribers left on this topic, remove it from the feed.
        if (subscribersForTopic.isEmpty())
            topicSubscriptions.remove(topic);

        // If there are no topics left in the feed, remove it from the cache.
        if (topicSubscriptions.isEmpty())
            _subscriptions.remove(feed);
    }

    /**
     * Add a monitor to the feed.
     * @param monitor The interactor.
     * @param feed The feed name.
     */
    public void addMonitor(Interactor monitor, String feed) {
        // Find monitors to the feed.
        Map<Interactor, SubscriptionState> feedMonitors = _monitors.get(feed); 
        if (feedMonitors == null) {
            _monitors.put(feed, feedMonitors = new HashMap<Interactor, SubscriptionState>());
        }

        // Find the subscription state of this monitor.
        SubscriptionState subscriptionState = feedMonitors.get(monitor);
        if (subscriptionState == null) {
            feedMonitors.put(monitor, subscriptionState = new SubscriptionState());
        }

        // Increment the subscription count.
        subscriptionState.Count = subscriptionState.Count + 1;
    }

    /**
     * Remove the monitor to a feed.
     * @param monitor The interactor.
     * @param feed The feed name.
     * @param removeAll If true remove all monitors.
     */
    public void removeMonitor(Interactor monitor, String feed, boolean removeAll) {
        // Can we find monitors for this feed in the cache?
        Map<Interactor, SubscriptionState> feedMonitors = _monitors.get(feed);
        if (feedMonitors == null) {
            return;
        }

        // Does this monitor have a subscription state?
        SubscriptionState subscriptionState = feedMonitors.get(monitor);
        if (subscriptionState == null) {
            return;
        }

        if (removeAll || --subscriptionState.Count == 0) {
            feedMonitors.remove(monitor);
        }

        // If there are no topics left in the feed, remove it from the cache.
        if (feedMonitors.isEmpty()) {
            _monitors.remove(feed);
        }
    }

    /**
     * Find the feeds and topics for a subscriber.
     * @param subscriber The subscriber.
     * @return The subscribed feeds and topics.
     */
    public List<FeedTopic> findFeedTopicsBySubscriber(Interactor subscriber) {
    	
    	return Enumerable.create(_subscriptions)
    		.select(new UnaryFunction<Entry<String, Map<String, Map<Interactor,SubscriptionState>>>, Enumerable<FeedTopic>>() {

				@Override
				public Enumerable<FeedTopic> invoke(
						Entry<String, Map<String, Map<Interactor, SubscriptionState>>> topicCache) {
					return Enumerable.create(topicCache.getValue())
							.where(new UnaryFunction<Map.Entry<String,Map<Interactor,SubscriptionState>>, Boolean>() {
								@Override
								public Boolean invoke(Entry<String, Map<Interactor, SubscriptionState>> arg) {
									return arg.getValue().containsKey(subscriber);
								}
							})
							.select(new UnaryFunction<Map.Entry<String,Map<Interactor,SubscriptionState>>, FeedTopic>() {
								@Override
								public FeedTopic invoke(Entry<String, Map<Interactor, SubscriptionState>> arg) {
									return new FeedTopic(topicCache.getKey(), arg.getKey());
								}
							});
				}
    		})
    		.selectMany(new UnaryFunction<Enumerable<FeedTopic>, Enumerable<FeedTopic>>() {
				@Override
				public Enumerable<FeedTopic> invoke(Enumerable<FeedTopic> arg) {
					return arg;
				}
			}).toList(new ArrayList<FeedTopic>());
    }

    /**
     * Get all subscribers to a given feed and topic.
     * @param feed The feed name.
     * @param topic The topic name.
     * @return The subscribers.
     */
    public List<Interactor> GetSubscribersToFeedAndTopic(String feed, String topic) {
    	List<Interactor> subscribers = new ArrayList<Interactor>();

        // Look for subscriptions to this feed.
        Map<String, Map<Interactor, SubscriptionState>> topicSubscriptions =_subscriptions.get(feed); 
        if (topicSubscriptions != null) {
            // Are there subscribers for this topic?
            Map<Interactor, SubscriptionState> subscribersForTopic = topicSubscriptions.get(topic);
            if (subscribersForTopic != null) {
            	for (Interactor subscriber : subscribersForTopic.keySet()) {
                    subscribers.add(subscriber);
            	}
            }
        }

        // Look for monitors to this feed.
        Map<Interactor, SubscriptionState> feedMonitors =_monitors.get(feed); 
        if (feedMonitors != null) {
        	for (Interactor subscriber : feedMonitors.keySet()) {
                subscribers.add(subscriber);
        	}
        }

        return subscribers;
    }

    /**
     * Get the subscribers to a feed.
     * @param feed The feed name.
     * @return Subscribers to the feed and the topics subscriber to.
     */
    public List<KeyValuePair<String, Set<Interactor>>> getSubscribersToFeed(String feed) {
    	List<KeyValuePair<String, Set<Interactor>>> subscribersToFeed = new ArrayList<KeyValuePair<String, Set<Interactor>>>(); 
    	
        // Can we find this feed in the cache?
        Map<String, Map<Interactor, SubscriptionState>> topicCache =_subscriptions.get(feed); 
        if (topicCache != null) {
        	for (Entry<String, Map<Interactor, SubscriptionState>> x : topicCache.entrySet()) {
        		subscribersToFeed.add(new KeyValuePair<String, Set<Interactor>>(x.getKey(), x.getValue().keySet()));
        	}
        }

        return subscribersToFeed;
    }

    private class SubscriptionState {
        public int Count;
    }

}
