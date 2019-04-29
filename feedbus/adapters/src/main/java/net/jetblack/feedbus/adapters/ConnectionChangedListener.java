package net.jetblack.feedbus.adapters;

/**
 * A listener to connection changed events.
 */
public interface ConnectionChangedListener {

	/**
	 * Called when the connection changes.
	 * @param event The content of the event.
	 */
	void onConnectionChanged(ConnectionChangedEvent event);
	
}
