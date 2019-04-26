package net.jetblack.feedbus.util.invokable;

/**
 * An action taking two arguments.
 * @param <T1> The type of the first argument.
 * @param <T2> The type of the second argument.
 */
public interface BinaryAction<T1, T2> {

	/**
	 * Invoke the action.
	 * @param arg1 The first argument.
	 * @param arg2 The second argument.
	 */
	public void invoke(T1 arg1, T2 arg2);
	
}