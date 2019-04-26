package net.jetblack.feedbus.util.invokable;

/**
 * A function taking non arguments
 * @param <R> The return type of the function.
 */
public interface NullaryFunction<R> {

	/**
	 * Invoke the function.
	 * @return The result of the function.
	 */
	public R invoke();
	
}