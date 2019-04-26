package net.jetblack.feedbus.util.selector;

import net.jetblack.feedbus.util.invokable.UnaryFunction;

public class ClassSelector<T extends Object> implements UnaryFunction<T, Class<?>> {

	@Override
	public Class<?> invoke(T arg) {
		return arg.getClass();
	}

}