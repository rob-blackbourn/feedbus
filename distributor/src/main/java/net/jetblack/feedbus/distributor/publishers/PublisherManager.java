package net.jetblack.feedbus.distributor.publishers;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jetblack.feedbus.distributor.interactors.Interactor;
import net.jetblack.feedbus.distributor.interactors.InteractorClosedEventArgs;
import net.jetblack.feedbus.distributor.interactors.InteractorFaultedEventArgs;
import net.jetblack.feedbus.distributor.interactors.InteractorManager;
import net.jetblack.feedbus.messages.FeedTopic;
import net.jetblack.feedbus.messages.MulticastData;
import net.jetblack.feedbus.messages.UnicastData;
import net.jetblack.feedbus.util.EventHandler;
import net.jetblack.feedbus.util.EventListener;
import net.jetblack.feedbus.util.EventRegister;
import net.jetblack.feedbus.util.concurrent.ConcurrentEventHandler;

/**
 * The puublisher manager.
 */
public class PublisherManager {


	private static final Logger logger = Logger.getLogger(PublisherManager.class.getName());

    private final PublisherRepository _repository;
    private final EventHandler<StalePublisherEventArgs> _stalePublisher = new ConcurrentEventHandler<StalePublisherEventArgs>();
    public final EventRegister<StalePublisherEventArgs> StalePublisher = _stalePublisher;

    /**
     * Construct the manager.
     * @param interactorManager The interactor manager.
     */
    public PublisherManager(InteractorManager interactorManager) {
        _repository = new PublisherRepository();
        
        interactorManager.InteractorClosed.add(new EventListener<InteractorClosedEventArgs>() {
			@Override
			public void onEvent(InteractorClosedEventArgs event) {
				closeInteractor(event.getInteractor());
			}
		});
        
        interactorManager.InteractorFaulted.add(new EventListener<InteractorFaultedEventArgs>() {
			
			@Override
			public void onEvent(InteractorFaultedEventArgs event) {
		        logger.fine("Interactor faulted: " + event.getInteractor() + " - " + event.getError());
		        closeInteractor(event.getInteractor());
			}
		});
    }

    // TODO: Change the order of the arguments
    /**
     * Send data to a single interactor.
     * @param publisher The publisher.
     * @param unicastData The data to send.
     * @param subscriber The recipient.
     */
    public void sendUnicastData(Interactor publisher, UnicastData unicastData, Interactor subscriber) {
        _repository.addPublisher(publisher, unicastData.getFeed(), unicastData.getTopic());
        try {
			subscriber.sendMessage(unicastData);
		} catch (InterruptedException error) {
			logger.log(Level.WARNING, "Failed to send unicast data from " + publisher + " to " + subscriber, error);
		}
    }

    /**
     * Send data to all subscribers.
     * @param publisher The publisher.
     * @param subscribers The subscribers to receive the data.
     * @param multicastData The data.
     */
    public void sendMulticastData(Interactor publisher, List<Interactor> subscribers, MulticastData multicastData) {
        for (Interactor subscriber : subscribers) {
			sendMulticastData(publisher, subscriber, multicastData);
        }
    }

    private void sendMulticastData(Interactor publisher, Interactor subscriber, MulticastData multicastData) {
        if (publisher != null)
            _repository.addPublisher(publisher, multicastData.getFeed(), multicastData.getTopic());
        try {
			subscriber.sendMessage(multicastData);
		} catch (InterruptedException error) {
			logger.log(Level.WARNING, "Failed to send multicast data from " + publisher + " to " + subscriber, error);
		}
    }

    private void closeInteractor(Interactor interactor) {
        Set<FeedTopic> topicsWithoutPublishers = _repository.removePublisher(interactor);
        if (topicsWithoutPublishers.size() > 0)
            _stalePublisher.notify(new StalePublisherEventArgs(interactor, topicsWithoutPublishers));
    }

}
