package net.jetblack.feedbus.distributor.notifiers;

import java.util.Set;
import java.util.logging.Logger;

import net.jetblack.feedbus.distributor.interactors.Interactor;
import net.jetblack.feedbus.distributor.interactors.InteractorClosedEventArgs;
import net.jetblack.feedbus.distributor.interactors.InteractorFaultedEventArgs;
import net.jetblack.feedbus.distributor.interactors.InteractorManager;
import net.jetblack.feedbus.messages.ForwardedSubscriptionRequest;
import net.jetblack.feedbus.messages.NotificationRequest;
import net.jetblack.feedbus.util.EventHandler;
import net.jetblack.feedbus.util.EventListener;
import net.jetblack.feedbus.util.EventRegister;
import net.jetblack.feedbus.util.concurrent.ConcurrentEventHandler;

/**
 * The notification manager.
 */
public class NotificationManager {

	private static final Logger logger = Logger.getLogger(NotificationManager.class.getName());

    private final NotificationRepository _repository;
    private final EventHandler<NotificationEventArgs> _newNotificationRequest = new ConcurrentEventHandler<NotificationEventArgs>();
    
    public final EventRegister<NotificationEventArgs> NewNotificationRequest = _newNotificationRequest;

    /**
     * Construct the manager.
     * @param interactorManager The interaction manager.
     */
    public NotificationManager(InteractorManager interactorManager)
    {
        _repository = new NotificationRepository();
        
        interactorManager.InteractorClosed.add(new EventListener<InteractorClosedEventArgs>() {
			@Override
			public void onEvent(InteractorClosedEventArgs event) {
		        logger.fine("Removing notification requests from " + event.getInteractor());
		        removeInteractor(event.getInteractor());
			}
		});
        
        interactorManager.InteractorFaulted.add(new EventListener<InteractorFaultedEventArgs>() {
			
			@Override
			public void onEvent(InteractorFaultedEventArgs event) {
		        logger.fine("Interactor faulted: " + event.getInteractor() + " - " + event.getError());
		        removeInteractor(event.getInteractor());
			}
		});
    }

    private void removeInteractor(Interactor interactor)
    {
        _repository.removeInteractor(interactor);
    }

    /**
     * Request a notification.
     * @param notifiable The interactor to be notified.
     * @param notificationRequest The notification request.
     */
    public void requestNotification(Interactor notifiable, NotificationRequest notificationRequest)
    {
        logger.info("Handling notification request for " + notifiable + " on " + notificationRequest);

        if (notificationRequest.isAdd()) {
            if (_repository.addRequest(notifiable, notificationRequest.getFeed())) {
            	_newNotificationRequest.notify(new NotificationEventArgs(notifiable, notificationRequest.getFeed()));
            }
        }
        else
            _repository.removeRequest(notifiable, notificationRequest.getFeed());
    }

    /**
     * Forward a subscription to interested interactors.
     * @param forwardedSubscriptionRequest The forwarded subscription request.
     */
    public void forwardSubscription(ForwardedSubscriptionRequest forwardedSubscriptionRequest)
    {
        // Find all the interactors that wish to be notified of subscriptions to this topic.
        Set<Interactor> notifiables = _repository.findNotifiables(forwardedSubscriptionRequest.getFeed());
        if (notifiables == null)
            return;

        logger.fine("Notifying interactors[" + notifiables + "] of subscription " + forwardedSubscriptionRequest);

        // Inform each notifiable interactor of the subscription request.
        for (Interactor notifiable : notifiables) {
            try {
				notifiable.sendMessage(forwardedSubscriptionRequest);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

}
