package net.jetblack.feedbus.adapters;

/**
 * A listener to forwarded subscription events.
 */
public interface ForwardedSubscriptionListener {

	/**
	 * Called when a forwarded subscription event is raised.
	 * @param event The content of the event.
	 */
	void onForwardedSubscription(ForwardedSubscriptionEvent event);
	
}
