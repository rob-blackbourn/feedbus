package net.jetblack.feedbus.util;

/**
 * An interface for registering a generic event listener.
 * @param <T> The type of the event.
 */
public interface EventRegister<T> {
    void add(EventListener<T> listener);
    void remove(EventListener<T> listener);
}
