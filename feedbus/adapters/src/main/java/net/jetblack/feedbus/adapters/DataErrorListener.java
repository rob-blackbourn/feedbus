package net.jetblack.feedbus.adapters;

/**
 * A listener to data error events.
 */
public interface DataErrorListener {

	/**
	 * Called when a data error event is raised.
	 * @param event The content of the event.
	 */
	void onDataErrorEvent(DataErrorEvent event);
	
}
