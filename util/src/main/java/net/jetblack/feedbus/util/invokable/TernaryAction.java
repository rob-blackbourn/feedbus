package net.jetblack.feedbus.util.invokable;

public interface TernaryAction<T1, T2, T3> {

	public void invoke(T1 arg1, T2 arg2, T3 arg3);
	
}