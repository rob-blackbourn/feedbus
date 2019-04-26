package net.jetblack.feedbus.util.invokable;

public interface UnaryFunction<T,R> {

	public R invoke(T arg);
	
}