package net.jetblack.feedbus.util.selector;

import net.jetblack.feedbus.util.invokable.UnaryFunction;

/**
 * Converts the argument to a string.
 * @param <T> The type of the value.
 */
public class ToStringSelector<T> implements UnaryFunction<T, String> {

	@Override
	public String invoke(T arg) {
		return arg.toString();
	}

}