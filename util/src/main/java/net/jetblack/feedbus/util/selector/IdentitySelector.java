package net.jetblack.feedbus.util.selector;

import net.jetblack.feedbus.util.invokable.UnaryFunction;

public class IdentitySelector<T> implements UnaryFunction<T,T> {

	@Override
	public T invoke(T arg) {
		return arg;
	}

}