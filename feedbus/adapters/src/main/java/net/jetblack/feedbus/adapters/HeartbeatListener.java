package net.jetblack.feedbus.adapters;

/**
 * A listener to heart beat events.
 */
public interface HeartbeatListener {

	/**
	 * Called when a heartbeat is received.
	 */
	void onHeartbeat();
}
