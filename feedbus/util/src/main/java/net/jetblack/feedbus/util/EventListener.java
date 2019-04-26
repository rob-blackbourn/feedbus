package net.jetblack.feedbus.util;

/**
 * A generic event listener.
 * @param <T>
 */
public interface EventListener<T> {
	void onEvent(T event);
}
