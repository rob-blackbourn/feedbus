package net.jetblack.feedbus.util.invokable;

/**
 * A function taking three arguments.
 * @param <T1> The type of the first argument.
 * @param <T2> The type of the second argument.
 * @param <T3> The type of the third argument.
 * @param <R> The return type of the function.
 */
public interface TernaryFunction<T1, T2, T3, R> {

	/**
	 * Invoke the function.
	 * @param arg1 The first argument.
	 * @param arg2 The second argument.
	 * @param arg3 The third argument.
	 * @return The result of the function.
	 */
	public R invoke(T1 arg1, T2 arg2, T3 arg3);
	
}