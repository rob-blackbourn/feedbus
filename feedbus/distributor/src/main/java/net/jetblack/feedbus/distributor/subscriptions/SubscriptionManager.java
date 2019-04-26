package net.jetblack.feedbus.distributor.subscriptions;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import net.jetblack.feedbus.distributor.interactors.Interactor;
import net.jetblack.feedbus.distributor.interactors.InteractorClosedEventArgs;
import net.jetblack.feedbus.distributor.interactors.InteractorFaultedEventArgs;
import net.jetblack.feedbus.distributor.interactors.InteractorManager;
import net.jetblack.feedbus.distributor.notifiers.NotificationEventArgs;
import net.jetblack.feedbus.distributor.notifiers.NotificationManager;
import net.jetblack.feedbus.distributor.publishers.PublisherManager;
import net.jetblack.feedbus.distributor.publishers.StalePublisherEventArgs;
import net.jetblack.feedbus.messages.FeedTopic;
import net.jetblack.feedbus.messages.ForwardedSubscriptionRequest;
import net.jetblack.feedbus.messages.MonitorRequest;
import net.jetblack.feedbus.messages.MulticastData;
import net.jetblack.feedbus.messages.SubscriptionRequest;
import net.jetblack.feedbus.messages.UnicastData;
import net.jetblack.feedbus.util.Enumerable;
import net.jetblack.feedbus.util.EventListener;
import net.jetblack.feedbus.util.KeyValuePair;
import net.jetblack.feedbus.util.StringComparator;
import net.jetblack.feedbus.util.invokable.UnaryFunction;

/**
 * The subscription manager.
 */
public class SubscriptionManager {

	private static final Logger logger = Logger.getLogger(SubscriptionManager.class.getName());

    private final SubscriptionRepository _repository;
    private final NotificationManager _notificationManager;
    private final PublisherManager _publisherManager;

    /**
     * Constructs the manager.
     * @param interactorManager The interactor manager.
     * @param notificationManager The notification manager.
     */
    public SubscriptionManager(InteractorManager interactorManager, NotificationManager notificationManager) {
        _notificationManager = notificationManager;

        _repository = new SubscriptionRepository();
        _publisherManager = new PublisherManager(interactorManager);

        interactorManager.InteractorClosed.add(new EventListener<InteractorClosedEventArgs>() {
			@Override
			public void onEvent(InteractorClosedEventArgs event) {
		        closeInteractor(event.getInteractor());
			}
		});
        
        interactorManager.InteractorFaulted.add(new EventListener<InteractorFaultedEventArgs>() {
			@Override
			public void onEvent(InteractorFaultedEventArgs event) {
		        logger.fine("Interactor faulted: " + event.getInteractor() + " - " + event.getError().getMessage());
		        closeInteractor(event.getInteractor());
			}
		});

        notificationManager.NewNotificationRequest.add(new EventListener<NotificationEventArgs>() {
			@Override
			public void onEvent(NotificationEventArgs event) {
		        // Find the subscribers whoes subscriptions match the pattern.
		        for (KeyValuePair<String, Set<Interactor>> matchingSubscriptions : _repository.getSubscribersToFeed(event.getFeed())) {
		            // Tell the requestor about subscribers that are interested in this topic.
		            for (Interactor subscriber : matchingSubscriptions.Value) {
		                try {
							event.getInteractor().sendMessage(new ForwardedSubscriptionRequest(subscriber.getId(), event.getFeed(), matchingSubscriptions.Key, true));
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		            }
		        }
			}
		});

        _publisherManager.StalePublisher.add(new EventListener<StalePublisherEventArgs>() {

			@Override
			public void onEvent(StalePublisherEventArgs event) {
		        for (FeedTopic staleFeedTopic : event.getFeedsAndTopics()) {
		        	MulticastData staleMessage = new MulticastData(staleFeedTopic.getFeed(), staleFeedTopic.getTopic(), true, null);

		            for (Interactor subscriber : _repository.GetSubscribersToFeedAndTopic(staleFeedTopic.getFeed(), staleFeedTopic.getTopic())) {
		                try {
							subscriber.sendMessage(staleMessage);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		            }
		        }
			}
        	
        });
        
    }

    /**
     * Request a subscription.
     * @param subscriber The subscriber.
     * @param subscriptionRequest The request.
     */
    public void requestSubscription(Interactor subscriber, SubscriptionRequest subscriptionRequest) {
        logger.info("Received subscription from " + subscriber + " on \"" + subscriptionRequest + "\"");

        if (subscriptionRequest.isAdd())
            _repository.addSubscription(subscriber, subscriptionRequest.getFeed(), subscriptionRequest.getTopic());
        else
            _repository.removeSubscription(subscriber, subscriptionRequest.getFeed(), subscriptionRequest.getTopic(), false);

        _notificationManager.forwardSubscription(new ForwardedSubscriptionRequest(subscriber.getId(), subscriptionRequest.getFeed(), subscriptionRequest.getTopic(), subscriptionRequest.isAdd()));
    }

    /**
     * Request monitoring a feed.
     * @param monitor The requester.
     * @param monitorRequest The request.
     */
    public void requestMonitor(Interactor monitor, MonitorRequest monitorRequest) {
        logger.info("Received monitor from " + monitor + " on \"" + monitorRequest + "\"");

        if (monitorRequest.isAdd())
            _repository.addMonitor(monitor, monitorRequest.getFeed());
        else
            _repository.removeMonitor(monitor, monitorRequest.getFeed(), false);
    }

    private void closeInteractor(Interactor interactor) {
        logger.fine("Removing subscriptions for " + interactor);

        // Remove the subscriptions
        List<FeedTopic> feedTopics = _repository.findFeedTopicsBySubscriber(interactor);
        for (FeedTopic feedTopic : feedTopics) {
            _repository.removeSubscription(interactor, feedTopic.getFeed(), feedTopic.getTopic(), true);
        }

        Enumerable<String> monitorEnumerator = Enumerable.create(feedTopics)
        		.select(new UnaryFunction<FeedTopic, String>() {
					@Override
					public String invoke(FeedTopic arg) {
						return arg.getFeed();
					}
				})
        		.distinct(StringComparator.Default);
        
        for (String feed : monitorEnumerator) {
            _repository.removeMonitor(interactor, feed, true);
        }

        Enumerable<ForwardedSubscriptionRequest> subscriptionEnumerator = Enumerable.create(feedTopics)
        		.select(new UnaryFunction<FeedTopic, ForwardedSubscriptionRequest>() {
					@Override
					public ForwardedSubscriptionRequest invoke(FeedTopic feedTopic) {
						return new ForwardedSubscriptionRequest(interactor.getId(), feedTopic.getFeed(), feedTopic.getTopic(), false);
					}
				});
        // Inform those interested that this interactor is no longer subscribed to these topics.
        for (ForwardedSubscriptionRequest subscriptionRequest : subscriptionEnumerator) {
            _notificationManager.forwardSubscription(subscriptionRequest);
        }
    }

    /**
     * Send data to a single interactor.
     * @param publisher The publisher
     * @param unicastData The data.
     */
    public void sendUnicastData(Interactor publisher, UnicastData unicastData) {
        // Can we find this client in the subscribers to this topic?
        Interactor subscriber = Enumerable.create(_repository.GetSubscribersToFeedAndTopic(unicastData.getFeed(), unicastData.getTopic()))
                .firstOrDefault(new UnaryFunction<Interactor, Boolean>() {
					@Override
					public Boolean invoke(Interactor arg) {
						return arg.getId().equals(unicastData.getClientId());
					}
				});

        if (subscriber == null)
            return;

        _publisherManager.sendUnicastData(publisher, unicastData, subscriber);
    }

    /**
     * Send data to subscribers.
     * @param publisher The publisher.
     * @param multicastData The data.
     */
    public void sendMulticastData(Interactor publisher, MulticastData multicastData) {
    	List<Interactor> subscribers = _repository.GetSubscribersToFeedAndTopic(multicastData.getFeed(), multicastData.getTopic());
        _publisherManager.sendMulticastData(publisher, subscribers, multicastData);
    }
}
