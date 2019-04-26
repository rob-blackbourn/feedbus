package net.jetblack.feedbus.util;

/**
 * A generic event handler. 
 * @param <T> The type of the event.
 */
public interface EventHandler<T> extends EventRegister<T> {
	
	/**
	 * Called when the event occurs.
	 * @param event The event
	 */
    void notify(T event);
    
}
