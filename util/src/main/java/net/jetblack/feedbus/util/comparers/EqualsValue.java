package net.jetblack.feedbus.util.comparers;

import net.jetblack.feedbus.util.invokable.UnaryFunction;

/**
 * An equality comparer
 * @param <T> The type of the object to compare.
 */
public class EqualsValue<T> implements UnaryFunction<T,Boolean> {

	private final T value;
	
	/**
	 * Construct the comparer.
	 * @param value The value to compare against.
	 */
	public EqualsValue(T value) {
		this.value = value;
	}
	
	@Override
	public Boolean invoke(T arg) {
		return (value == null && arg == null) || (value != null && value.equals(arg));
	}

}