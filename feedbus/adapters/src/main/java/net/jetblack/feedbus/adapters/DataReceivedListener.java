package net.jetblack.feedbus.adapters;

/**
 * A listener to data received events.
 */
public interface DataReceivedListener {

	/**
	 * Called when data is received.
	 * @param event The content of the event.
	 */
	void onDataReceived(DataReceivedEvent event);
	
}
