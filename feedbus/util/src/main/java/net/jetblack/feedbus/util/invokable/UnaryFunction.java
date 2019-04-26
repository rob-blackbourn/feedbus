package net.jetblack.feedbus.util.invokable;

/**
 * A function taking one argument.
 * @param <T1> The type of the first argument.
 * @param <R> The return type of the function.
 */
public interface UnaryFunction<T,R> {

	/**
	 * Invoke the function.
	 * @param arg1 The first argument.
	 * @return The result of the function.
	 */
	public R invoke(T arg);
	
}