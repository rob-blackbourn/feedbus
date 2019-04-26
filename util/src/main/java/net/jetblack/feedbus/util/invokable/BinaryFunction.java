package net.jetblack.feedbus.util.invokable;

public interface BinaryFunction<T1, T2, R> {

	public R invoke(T1 arg1, T2 arg2);
	
}