package net.jetblack.feedbus.util.selector;

import net.jetblack.feedbus.util.invokable.UnaryFunction;

/**
 * Returns the supplied value.
 * @param <T> The type of the value.
 */
public class IdentitySelector<T> implements UnaryFunction<T,T> {

	@Override
	public T invoke(T arg) {
		return arg;
	}

}