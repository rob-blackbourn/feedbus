package net.jetblack.feedbus.util;

public interface EventListener<T> {
	void onEvent(T event);
}
