package net.jetblack.feedbus.util;

public interface EventRegister<T> {
    void add(EventListener<T> listener);
    void remove(EventListener<T> listener);
}
