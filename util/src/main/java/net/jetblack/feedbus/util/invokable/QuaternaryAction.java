package net.jetblack.feedbus.util.invokable;

/**
 * An action taking four arguments.
 * @param <T1> The type of the first argument.
 * @param <T2> The type of the second argument.
 * @param <T3> The type of the third argument.
 * @param <T4> The type of the fourth argument.
 */
public interface QuaternaryAction<T1, T2, T3, T4> {

	/**
	 * Invoke the action.
	 * @param arg1 The first argument.
	 * @param arg2 The second argument.
	 * @param arg3 The third argument.
	 * @param arg4 The fourth argument.
	 */
	public void invoke(T1 arg1, T2 arg2, T3 arg3, T4 arg4);
	
}