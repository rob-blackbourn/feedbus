package net.jetblack.feedbus.util.comparers;

import net.jetblack.feedbus.util.invokable.BinaryFunction;

/**
 * An equality comparer.
 * @param <T> The type of the values to compare.
 */
public class EqualityComparer<T> implements BinaryFunction<T, T, Boolean> {

	@Override
	public Boolean invoke(T arg1, T arg2) {
		return (arg1 == null && arg2 == null) || (arg1 != null && arg1.equals(arg2));
	}

}