package net.jetblack.feedbus.util.invokable;

public interface BinaryAction<T1, T2> {

	public void invoke(T1 arg1, T2 arg2);
	
}