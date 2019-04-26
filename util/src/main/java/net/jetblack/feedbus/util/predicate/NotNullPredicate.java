package net.jetblack.feedbus.util.predicate;

import net.jetblack.feedbus.util.invokable.UnaryFunction;

public class NotNullPredicate<T> implements UnaryFunction<T,Boolean> {

	@Override
	public Boolean invoke(T arg) {
		return arg != null;
	}

}