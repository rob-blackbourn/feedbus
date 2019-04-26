package net.jetblack.feedbus.util.invokable;

/**
 * A function taking nine arguments.
 * @param <T1> The type of the first argument.
 * @param <T2> The type of the second argument.
 * @param <T3> The type of the third argument.
 * @param <T4> The type of the fourth argument.
 * @param <T5> The type of the fifth argument.
 * @param <T6> The type of the sixth argument.
 * @param <T7> The type of the seventh argument.
 * @param <T8> The type of the eighth argument.
 * @param <T9> The type of the ninth argument.
 * @param <R> The return type of the function.
 */
public interface NonaryFunction<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> {

	/**
	 * Invoke the function.
	 * @param arg1 The first argument.
	 * @param arg2 The second argument.
	 * @param arg3 The third argument.
	 * @param arg4 The fourth argument.
	 * @param arg5 The fifth argument.
	 * @param arg6 The sixth argument.
	 * @param arg7 The seventh argument.
	 * @param arg8 The eighth argument.
	 * @param arg9 The ninth argument.
	 * @return The result of the function.
	 */
	public R invoke(T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5, T6 arg6, T7 arg7, T8 arg8, T9 arg9);
	
}