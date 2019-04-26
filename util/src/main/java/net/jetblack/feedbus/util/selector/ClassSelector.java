package net.jetblack.feedbus.util.selector;

import net.jetblack.feedbus.util.invokable.UnaryFunction;

/**
 * Extracts the class of a value.
 * @param <T> The type of the value.
 */
public class ClassSelector<T extends Object> implements UnaryFunction<T, Class<?>> {

	@Override
	public Class<?> invoke(T arg) {
		return arg.getClass();
	}

}