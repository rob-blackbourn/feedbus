package net.jetblack.feedbus.distributor.monitor;

/**
 * The monitor interface for the acceptor.
 */
public interface AcceptorMonitorMXBean {

	/**
	 * Gets the number of connections that have been accepted. 
	 * @return The number of connections that have been accepted.
	 */
	long getAcceptedConnectionCount();
	
}
