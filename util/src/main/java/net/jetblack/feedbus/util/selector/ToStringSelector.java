package net.jetblack.feedbus.util.selector;

import net.jetblack.feedbus.util.invokable.UnaryFunction;

public class ToStringSelector<T> implements UnaryFunction<T, String> {

	@Override
	public String invoke(T arg) {
		return arg.toString();
	}

}