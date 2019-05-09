package net.jetblack.feedbus.distributor.subscriptions.monitor;

/**
 * The interface for the subscription monitor MXBean.
 */
public interface SubscriptionMonitorMXBean {

	/**
	 * Gets the current number of subscriptions.
	 * 
	 * @return The current number of subscriptions.
	 */
	int getSubscriptionCount();
	
	/**
	 * Gets the current number of monitors.
	 * 
	 * @return The current number of monitors.
	 */
	int getMonitorCount();

}
