package net.jetblack.feedbus.distributor.monitor;

/**
 * The interface for the server monitor MXBean.
 */
public interface ServerMonitorMXBean {

	/**
	 * Gets the total number of messages that have been received.
	 * 
	 * @return The total number of messages that have been received.
	 */
	long getMessageCount();
	
}
