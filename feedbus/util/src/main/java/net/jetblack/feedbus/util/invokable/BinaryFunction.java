package net.jetblack.feedbus.util.invokable;

/**
 * A function taking two arguments.
 * @param <T1> The type of the first argument.
 * @param <T2> The type of the second argument.
 * @param <R> The return type of the function.
 */
public interface BinaryFunction<T1, T2, R> {

	/**
	 * Invoke the function.
	 * @param arg1 The first argument.
	 * @param arg2 The second argument.
	 * @return The result of the function.
	 */
	public R invoke(T1 arg1, T2 arg2);
	
}