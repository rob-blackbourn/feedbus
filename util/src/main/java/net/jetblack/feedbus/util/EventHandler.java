package net.jetblack.feedbus.util;

public interface EventHandler<T> extends EventRegister<T> {
    void notify(T event);
}
