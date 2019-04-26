package net.jetblack.feedbus.util.invokable;

/**
 * An action taking three arguments.
 * @param <T1> The type of the first argument.
 * @param <T2> The type of the second argument.
 * @param <T3> The type of the third argument.
 */
public interface TernaryAction<T1, T2, T3> {

	/**
	 * Invoke the action.
	 * @param arg1 The first argument.
	 * @param arg2 The second argument.
	 * @param arg3 The third argument.
	 */
	public void invoke(T1 arg1, T2 arg2, T3 arg3);
	
}