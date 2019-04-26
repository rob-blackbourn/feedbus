package net.jetblack.feedbus.util.predicate;

import net.jetblack.feedbus.util.invokable.UnaryFunction;

/**
 * A predicate indicating the value is not null.
 * @param <T> The type of the value.
 */
public class NotNullPredicate<T> implements UnaryFunction<T,Boolean> {

	@Override
	public Boolean invoke(T arg) {
		return arg != null;
	}

}