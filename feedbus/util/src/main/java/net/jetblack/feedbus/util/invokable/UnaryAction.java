package net.jetblack.feedbus.util.invokable;

/**
 * An action taking one argument.
 * @param <T1> The type of the first argument.
 */
public interface UnaryAction<T> {

	/**
	 * Invoke the action.
	 * @param arg1 The first argument.
	 */
	public void invoke(T arg);
	
}