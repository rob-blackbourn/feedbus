package net.jetblack.feedbus.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import net.jetblack.feedbus.util.comparers.EqualityComparer;
import net.jetblack.feedbus.util.invokable.BinaryFunction;
import net.jetblack.feedbus.util.invokable.UnaryAction;
import net.jetblack.feedbus.util.invokable.UnaryFunction;
import net.jetblack.feedbus.util.selector.IdentitySelector;
import net.jetblack.feedbus.util.type.TypeLiteral;

/**
 * A class to provide transformations on enumerables. 
 * @param <T> The type of the enumerable.
 */
public abstract class Enumerable<T> implements Iterator<T>, Iterable<T> {

	/**
	 * remove throws UnsupportedOperationException.
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the enumerator as an iterator.
	 */
	@Override
	public Iterator<T> iterator() {
		return this;
	}

	/**
	 * Create an Enumerable from an array.
	 * @param array The array source for the enumerable.
	 * @return an object to enumerate over the array.
	 */
	public static <T> Enumerable<T> create(final T[] array) {
		return new Enumerable<T>() {

			int i = 0;

			@Override
			public boolean hasNext() {
				return i < array.length;
			}

			@Override
			public T next() {
				return array[i++];
			}

		};
	}

	/**
	 * Create a reverse enumerable for an array.
	 * @param array The array source for the enumerable.
	 * @return an object to enumerate over the array.
	 */
	public static <T> Enumerable<T> createReverse(final T[] array) {
		return new Enumerable<T>() {

			int i = array.length;

			@Override
			public boolean hasNext() {
				return i > 0;
			}

			@Override
			public T next() {
				return array[--i];
			}

		};
	}

	/**
	 * Create  an enumerable from an iterator.
	 * @param iterator The iterator from which the enumerable is made.
	 * @return an object to enumerate over the iterable.
	 */
	public static <T> Enumerable<T> create(final Iterator<T> iterator) {
		return new Enumerable<T>() {

			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public T next() {
				return iterator.next();
			}

		};
	}

	/**
	 * Creates a reverse enumerable from a list iterator.
	 * @param iterator The list iterator from which the enumerator is made.
	 * @return an object to iterate over the list iterator.
	 */
	public static <T> Enumerable<T> createReverse(final ListIterator<T> iterator) {
		return new Enumerable<T>() {

			{
				while (iterator.hasNext()) {
					iterator.next();
				}
			}

			@Override
			public boolean hasNext() {
				return iterator.hasPrevious();
			}

			@Override
			public T next() {
				return iterator.previous();
			}

		};
	}

	/**
	 * Create an enumerable from an iterable.
	 * @param iterable The iterable from which the enumerable is made.
	 * @return an object to iterator over the iterable.
	 */
	public static <T> Enumerable<T> create(final Iterable<T> iterable) {
		return create(iterable.iterator());
	}

	/**
	 * Creates a depth first iterator.
	 * @param root The root of the tree.
	 * @param childSelector A function to get the children of the current node.
	 * @param leftToRight If true iterate left to right, otherwise right to left.
	 * @return an object which iterates over the tree.
	 */
	public static <T> Enumerable<T> createDepthFirst(final T root, final UnaryFunction<T, Enumerable<T>> childSelector, final boolean leftToRight) {
		return new Enumerable<T>() {

			final Set<T> visited = new HashSet<T>();
			final Stack<T> stack = new Stack<T>();
			
			{
				stack.push(root);
			}
			
			@Override
			public boolean hasNext() {
				return !stack.isEmpty();
			}

			@Override
			public T next() {
				T current = stack.pop();
				visited.add(current);
				
				Enumerable<T> children = childSelector.invoke(current);
				if (children != null) {
					children = children.where(new UnaryFunction<T, Boolean>(){
						@Override
						public Boolean invoke(T arg) {
							return !visited.contains(arg);
						}
					});
					
					if (leftToRight) {
						children = Enumerable.createReverse(children.toList().listIterator());
					}

					for (T child : children) {
						stack.push(child);
					}
				}
				
				return current;
			}
			
		};
	}
	
	/**
	 * Creates a breadth first iterator.
	 * @param root The root of the tree.
	 * @param childSelector a function to select the children of the current node.
	 * @param topToBottom If true traverse from the top to the bottom, otherwise from the bottom to the top.
	 * @return An object to iterate over the tree.
	 */
	public static <T> Enumerable<T> createBreadthFirst(final T root, final UnaryFunction<T, Enumerable<T>> childSelector, final boolean topToBottom) {
		return new Enumerable<T>() {

			Queue<T> queue = new LinkedList<T>();
			
			{
				queue.add(root);
			}
			
			@Override
			public boolean hasNext() {
				return !queue.isEmpty();
			}

			@Override
			public T next() {
				T current = queue.remove();
				
				Enumerable<T> children = childSelector.invoke(current);
				if (children != null) {
					
					if (!topToBottom) {
						children = Enumerable.createReverse(children.toList().listIterator());
					}
					
					for (T child : children) {
						queue.add(child);
					}
				}
				
				return current;
			}
			
		};
	}

	/**
	 * Creates an enumerable which iterates over the entries of a map.
	 * @param map The map on which the iterator is made.
	 * @return An object to iterate over the entries in a map.
	 */
	public static <K, V> Enumerable<Map.Entry<K, V>> create(final Map<K, V> map) {
		return create(map.entrySet().iterator());
	}

	/**
	 * Projects each element of a sequence into a new form.
	 * @param projector A transform function to apply to each element.
	 * @return An Enumerable<T> whose elements are the result of invoking the transform function on each element of source.
	 */
	public <U> Enumerable<U> select(final UnaryFunction<T, U> projector) {
		return new Enumerable<U>() {

			@Override
			public boolean hasNext() {
				return Enumerable.this.hasNext();
			}

			@Override
			public U next() {
				return projector.invoke(Enumerable.this.next());
			}

		};
	}

	/**
	 * Projects each element of a sequence into a new form by incorporating the element's index.
	 * @param projector A transform function to apply to each source element; the second parameter of the function represents the index of the source element.
	 * @return An Enumerable<T> whose elements are the result of invoking the transform function on each element of source.
	 */
	public <U> Enumerable<U> select(final BinaryFunction<T, Integer, U> projector) {
		return new Enumerable<U>() {

			private int i = 0;
			
			@Override
			public boolean hasNext() {
				return Enumerable.this.hasNext();
			}

			@Override
			public U next() {
				return projector.invoke(Enumerable.this.next(), i++);
			}

		};
	}

	/**
	 * Filters a sequence of values based on a predicate.
	 * @param predicate A function to test each element for a condition.
	 * @return An Enumerable that contains elements from the input sequence that satisfy the condition.
	 */
	public Enumerable<T> where(final UnaryFunction<T, Boolean> predicate) {

		return new Enumerable<T>() {

			private T nextItem = null;

			{
				nextItem = findNext();
			}

			private T findNext() {
				while (Enumerable.this.hasNext()) {
					T i = Enumerable.this.next();
					if (predicate.invoke(i)) {
						return i;
					}
				}
				return null;
			}

			@Override
			public boolean hasNext() {
				return nextItem != null;
			}

			@Override
			public T next() {
				if (nextItem == null) {
					throw new NoSuchElementException();
				}

				T value = nextItem;
				nextItem = findNext();

				return value;
			}

			@Override
			public void remove() {
				Enumerable.this.remove();
			}
		};
	}

	/**
	 * Determines whether all elements of a sequence satisfy a condition.
	 * @param predicate
	 * @return true if every element of the source sequence passes the test in the specified predicate, or if the sequence is empty; otherwise, false.
	 */
	public boolean all(final UnaryFunction<T, Boolean> predicate) {
		while (hasNext()) {
			if (!predicate.invoke(next())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Determines whether any element of a sequence satisfies a condition.
	 * @param predicate A function to test each element for a condition.
	 * @return true if any elements in the source sequence pass the test in the specified predicate; otherwise, false.
	 */
	public boolean any(final UnaryFunction<T, Boolean> predicate) {
		while (hasNext()) {
			if (predicate.invoke(next())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Iterate over the Enumerable<T> applying the given action.
	 * @param action The action to apply to each element.
	 */
	public void forEach(final UnaryAction<T> action) {
		while (hasNext()) {
			action.invoke(next());
		}
	}

	/**
	 * Returns a specified number of contiguous elements from the start of a sequence.
	 * @param size The number of elements to return.
	 * @return An Enumerable that contains the specified number of elements from the start of the input sequence.
	 */
	public Enumerable<T> take(final int size) {
		return new Enumerable<T>() {

			private int i = size;

			@Override
			public boolean hasNext() {
				return Enumerable.this.hasNext() && i > 0;
			}

			@Override
			public T next() {
				if (i-- <= 0)
					throw new NoSuchElementException();
				return Enumerable.this.next();
			}

			@Override
			public void remove() {
				Enumerable.this.remove();
			}

		};
	}

	/**
	 * Bypasses a specified number of elements in a sequence and then returns the remaining elements.
	 * @param size The number of elements to skip before returning elements.
	 * @return An Enumerable that contains the elements that occur after the specified index in the input sequence.
	 */
	public Enumerable<T> skip(final int size) {
		return new Enumerable<T>() {

			private int i = 0;
			
			@Override
			public boolean hasNext() {
				
				while (Enumerable.this.hasNext() && i++ < size) {
					Enumerable.this.next();
				}

				return Enumerable.this.hasNext();
			}

			@Override
			public T next() {

				return Enumerable.this.next();
			}
			
		};
	}
	
	/**
	 * Buffers an enumerable into enumerables of a given size.
	 * @param size The size of enumerables to buffer.
	 * @return An enumerable of enumerables.
	 */
	public Enumerable<Enumerable<T>> buffer(final int size) {

		return new Enumerable<Enumerable<T>>() {

			@Override
			public boolean hasNext() {
				return Enumerable.this.hasNext();
			}

			@Override
			public Enumerable<T> next() {
				return Enumerable.this.take(size);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		};
	}

	/**
	 * Create an enumerable of the given type omitting items not of that type.
	 * @param type The type of the enumerable.
	 * @return An enumerable of the given type with other types omitted.
	 */
	public <U> Enumerable<U> ofType(TypeLiteral<U> type) {
		return ofType(type.asClass());
	}

	public <U> Enumerable<U> ofType(final Class<U> clazz) {
		return where(new UnaryFunction<T, Boolean>() {

			@Override
			public Boolean invoke(T arg) {
				return clazz.isInstance(arg);
			}

		}).select(new UnaryFunction<T, U>() {

			@Override
			public U invoke(T arg) {
				return clazz.cast(arg);
			}

		});
	}

	/**
	 * Casts an enumerable to the given type.
	 * @param clazz The type of the items.
	 * @return An enumerable of the given type.
	 */
	public <U> Enumerable<U> cast(final Class<U> clazz) {
		return select(new UnaryFunction<T, U>() {

			@Override
			public U invoke(T arg) {
				return clazz.cast(arg);
			}

		});
	}

	/**
	 * Cast an enumerable to the given type.
	 * 
	 * @param type The type of the items.
	 * @return An enumerable of the given type.
	 */
	public <U> Enumerable<U> cast(TypeLiteral<U> type) {
		return cast(type.asClass());
	}

	/**
	 * Aggregates the enumerable with a given function and initial value.
	 * @param initialValue The initial value.
	 * @param aggregator A function to apply to the item and the current value.
	 * @return The aggregated value.
	 */
	public <U> U aggregate(final U initialValue, BinaryFunction<T, U, U> aggregator) {
		U value = initialValue;
		while (hasNext()) {
			value = aggregator.invoke(next(), value);
		}
		return value;
	}

	/**
	 * Aggregates the enumeration with a null initial value.
	 * @param aggregator A function to apply to the item and the current value.
	 * @return The aggregated value.
	 */
	public <U> U aggregate(BinaryFunction<T, U, U> aggregator) {
		return aggregate(null, aggregator);
	}
	
	/**
	 * Concatenates two sequences.
	 * @param enumerable The sequence to concatenate.
	 * @return An Enumerable<T> that contains the concatenated elements of the two input sequences.
	 */
	public Enumerable<T> concat(final Enumerable<T> enumerable) {
		return new Enumerable<T>() {

			@Override
			public boolean hasNext() {
				return Enumerable.this.hasNext() || enumerable.hasNext();
			}

			@Override
			public T next() {
				if (Enumerable.this.hasNext()) {
					return Enumerable.this.next();
				} else {
					return enumerable.next();
				}
			}
			
		};
	}
	
	/**
	 * Creates a Map<K, V> from an Enumerable<T> according to specified key selector and element selector functions.
	 * @param keySelector A selector returning the key.
	 * @param valueSelector A selector returning the value.
	 * @param map The map to populate.
	 * @return The populated map.
	 */
	public <K, V> Map<K, V> toMap(UnaryFunction<T, K> keySelector, UnaryFunction<T, V> valueSelector, Map<K, V> map) {

		while (Enumerable.this.hasNext()) {
			T item = Enumerable.this.next();
			map.put(keySelector.invoke(item), valueSelector.invoke(item));
		}

		return map;
	}

	/**
	 * Creates a Map<K, V> as a HashMap<K, V> from an Enumerable<T> according to specified key selector and element selector functions.
	 * @param keySelector A selector returning the key.
	 * @param valueSelector A selector returning the value.
	 * @return The populated HashMap<K, V>
	 */
	public <K, V> Map<K, V> toMap(UnaryFunction<T, K> keySelector, UnaryFunction<T, V> valueSelector) {
		return toMap(keySelector, valueSelector, new HashMap<K, V>());
	}

	/**
	 * Groups an Enumerable<T> according to a given key selector into a Map<K, V> where the value is generated by the value selector.
	 * @param keySelector A selector returning the key.
	 * @param valueSelector A selector returning the value.
	 * @param map The grouped Map<K, V>
	 * @return A Map<K, V> of the grouped Enumerable<T>.
	 */
	public <K, V> Map<K, Collection<V>> groupBy(UnaryFunction<T, K> keySelector, UnaryFunction<T, V> valueSelector, Map<K,Collection<V>> map) {

		while (Enumerable.this.hasNext()) {

			T item = Enumerable.this.next();

			final K key = keySelector.invoke(item);

			if (!map.containsKey(key)) {
				map.put(key, new ArrayList<V>());
			}

			map.get(key).add(valueSelector.invoke(item));
		}

		return map;
	}

	/**
	 * Groups an Enumerable<T> according to a given key selector into a HashMap<K, V> where the value is generated by the value selector.
	 * @param keySelector A selector returning the key.
	 * @param valueSelector A selector returning the value.
	 * @return A Map<K, V> of the grouped Enumerable<T>.
	 */
	public <K, V> Map<K, Collection<V>> groupBy(UnaryFunction<T, K> keySelector, UnaryFunction<T, V> valueSelector) {
		return groupBy(keySelector, valueSelector, new HashMap<K,Collection<V>>());
	}

	/**
	 * Groups an Enumerable<T> according to a given key selector into a HashMap<K, V> where the value that of the Enumerable<T>.
	 * @param keySelector A selector returning the key.
	 * @param valueSelector A selector returning the value.
	 * @return A Map<K, V> of the grouped Enumerable<T>.
	 */
	public <K> Map<K, Collection<T>> groupBy(UnaryFunction<T, K> keySelector) {
		return groupBy(keySelector, new IdentitySelector<T>());
	}

	/**
	 * Return the size of the enumerable.
	 * @return The size of the enumerable.
	 */
	public int size() {
		int i = 0;
		while (hasNext()) {
			++i;
			next();
		}
		return i;
	}

	/**
	 * Return the count of values which match the predicate.
	 * @param predicate The predicate function to apply to each value.
	 * @return A count of the values for which the predicate evaluates to true.
	 */
	public int size(UnaryFunction<T, Boolean> predicate) {

		int i = 0;

		while (hasNext()) {
			if (predicate.invoke(next())) {
				++i;
			}
		}

		return i;
	}

	/**
	 * Fills a Collection<T> of the Enumerable<T>.
	 * @param collection The collection to fill.
	 * @return The filled collection.
	 */
	public <C extends Collection<T>> C toCollection(C collection) {
		for (T item : Enumerable.this) {
			collection.add(item);
		}
		return collection;
	}

	/**
	 * Fills a List<T> from the Enumerable<T>.
	 * @param list The List<T> to fill.
	 * @return The filled list.
	 */
	public List<T> toList(List<T> list) {
		return toCollection(list);
	}

	/**
	 * Creates a List<T> of the Enumerable<T> as an ArrayList<T>.
	 * @return A List<T> of the Enumerable<T>.
	 */
	public List<T> toList() {
		return toList(new ArrayList<T>());
	}

	/**
	 * Fills a Set<T> from the Enumerable<T>.
	 * @param set The Set<T> to fill.
	 * @return The filled Set<T>.
	 */
	public Set<T> toSet(Set<T> set) {
		return toCollection(set);
	}

	/**
	 * Creates a Set<T> of the Enumerable<T> as a HashSet<T>.
	 * @return The Set<T> of the Enumerable<T>.
	 */
	public Set<T> toSet() {
		return toSet(new HashSet<T>());
	}
	
	/**
	 * Returns the smallest item of the Enumerable<T> determined from the given function.
	 * @param comparator The function used to compare values in the Enumerable<T>.
	 * @return The smallest value in the Enumerable<T>.
	 */
	public T min(final Comparator<T> comparator) {
		return aggregate(null, new BinaryFunction<T, T, T>() {

			@Override
			public T invoke(T arg1, T arg2) {
				return arg2 == null ? arg1 : comparator.compare(arg2, arg1) > 0 ? arg1 : arg2;
			}

		});
	}

	/**
	 * Returns the largest item of the Enumerable<T> determined from the given function.
	 * @param comparator The function used to compare values in the Enumerable<T>.
	 * @return The largest value in the Enumerable<T>.
	 */
	public T max(final Comparator<T> comparator) {
		return aggregate(null, new BinaryFunction<T, T, T>() {

			@Override
			public T invoke(T arg1, T arg2) {
				return arg2 == null ? arg1 : comparator.compare(arg2, arg1) <= 0 ? arg1 : arg2;
			}

		});
	}

	/**
	 * Projects each element of a sequence to an Enumerable and flattens the resulting sequences into one sequence.
	 * @param selector A transform function to apply to each element.
	 * @return An Enumerable whose elements are the result of invoking the one-to-many transform function on each element of the input sequence.
	 */
	public <U> Enumerable<U> selectMany(final UnaryFunction<T, Enumerable<U>> selector) {
		return new Enumerable<U>() {

			Enumerable<U> buffer = null;

			@Override
			public boolean hasNext() {
				while ((buffer == null || !buffer.hasNext()) && Enumerable.this.hasNext()) {
					buffer = selector.invoke(Enumerable.this.next());
				}
				return buffer != null && buffer.hasNext();
			}

			@Override
			public U next() {

				if (!hasNext()) {
					throw new NoSuchElementException();
				}

				return buffer.next();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * Determines whether two sequences are equal by comparing their elements by using a specified comparer.
	 * @param iterator The iterator to compare against.
	 * @param comparer The function to use for comparison.
	 * @return true if the sequences are the same.
	 */
	public <U> boolean sequenceEquals(Iterator<U> iterator, BinaryFunction<T, U, Boolean> comparer) {
		while (hasNext() && iterator.hasNext()) {
			if (comparer.invoke(next(), iterator.next()) != true) {
				return false;
			}
		}
		return !(hasNext() || iterator.hasNext());
	}

	/**
	 * Determines whether two sequences are equal by comparing the elements by using the default equality comparer for their type.
	 * @param iterator The iterator to compare against.
	 * @return true if the sequences are the same.
	 */
	public boolean sequenceEquals(Iterator<T> iterator) {
		return sequenceEquals(iterator, new EqualityComparer<T>());
	}

	/**
	 * Determines whether two sequences are equal by comparing the elements by using the given comparer.
	 * @param iterator The iterator to compare against.
	 * @param comparer The function with which the items will be compared.
	 * @return true if the sequences are the same.
	 */
	public <U> boolean sequenceEquals(Iterable<U> iterable, BinaryFunction<T, U, Boolean> comparer) {
		return sequenceEquals(iterable.iterator(), comparer);
	}

	/**
	 * Determines whether two sequences are equal by comparing the elements by using the default equality comparer for their type.
	 * @param iterable The iterable to compare against.
	 * @return true if the sequences are the same.
	 */
	public boolean sequenceEquals(Iterable<T> iterable) {
		return sequenceEquals(iterable.iterator());
	}

	/**
	 * Determines whether two sequences are equal by comparing the elements by using the given comparer.
	 * @param array The array to compare against.
	 * @param comparer The function with which the items will be compared.
	 * @return true if the sequences are the same.
	 */
	public <U> boolean sequenceEquals(U[] array, BinaryFunction<T, U, Boolean> comparer) {
		return sequenceEquals(Enumerable.create(array).iterator(), comparer);
	}

	/**
	 * Determines whether two sequences are equal by comparing the elements by using the default equality comparer for their type.
	 * @param array The array to compare against.
	 * @return true if the sequences are the same.
	 */
	public boolean sequenceEquals(T[] array) {
		return sequenceEquals(Enumerable.create(array).iterator());
	}

	/**
	 * Returns the first value matching a given predicate, or null if no values match.
	 * @param predicate The predicate to  apply to the items.
	 * @return The first value matching a given predicate, or null if no values match.
	 */
	public T firstOrDefault(UnaryFunction<T, Boolean> predicate) {

		while (hasNext()) {
			final T value = next();
			if (predicate.invoke(value)) {
				return value;
			}
		}

		return null;
	}

	/**
	 * Returns the first value in the Enumeration<T> or null if empty.
	 * @return The first value or null if empty.
	 */
	public T firstOrDefault() {

		return hasNext() ? next() : null;
	}

	/**
	 * Returns the last value matching a given predicate, or null if no values match.
	 * @param predicate The predicate to  apply to the items.
	 * @return The last value matching a given predicate, or null if no values match.
	 */
	public T lastOrDefault(UnaryFunction<T, Boolean> predicate) {

		T result = null;

		while (hasNext()) {
			final T value = next();
			if (predicate.invoke(value)) {
				result = value;
			}
		}

		return result;
	}

	/**
	 * Returns the last value in the Enumeration<T> or null if empty.
	 * @return The last value or null if empty.
	 */
	public T lastOrDefault() {

		T result = null;

		while (hasNext()) {
			result = next();
		}

		return result;
	}

	/**
	 * Returns the first value that matches the given predicate.
	 * @param predicate The predicate to apply.
	 * @return The first matching value.
	 * @throws NoSuchElementException If no value matches.
	 */
	public T first(UnaryFunction<T, Boolean> predicate) {

		while (hasNext()) {
			final T value = next();
			if (predicate.invoke(value)) {
				return value;
			}
		}

		throw new NoSuchElementException();
	}

	/**
	 * Returns the first value of the Enumerable<T>.
	 * @return The first matching value.
	 * @throws NoSuchElementException If no value matches.
	 */
	public T first() {

		if (hasNext()) {
			return next();
		}

		throw new NoSuchElementException();
	}

	/**
	 * Returns the last value that matches the given predicate.
	 * @param predicate The predicate to apply.
	 * @return The last matching value.
	 * @throws NoSuchElementException If no value matches.
	 */
	public T last(UnaryFunction<T,Boolean> predicate) {
		
		boolean hasResult = false;
		
		T result = null;
		
		while (hasNext()) {
			final T value = next();
			if (predicate.invoke(value)) {
				hasResult = true;
				result = value;
			}
		}
		
		if (!hasResult) {
			throw new NoSuchElementException();
		}
		
		return result;
	}

	/**
	 * Returns the last value of the Enumerable<T>.
	 * @return The last matching value.
	 * @throws NoSuchElementException If no value matches.
	 */
	public T last() {

		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		T result = null;

		while (hasNext()) {
			result = next();
		}

		return result;
	}

	/**
	 * Returns the distinct items in the Enumerable<T>.
	 * @param comparator The function to compare the items.
	 * @return An Enumerable<T> of the distinctly different items in the source Enumerable<T>.
	 */
	public Enumerable<T> distinct(final Comparator<T> comparator) {
		
		final Set<T> set = new TreeSet<T>(comparator);

		return where(new UnaryFunction<T, Boolean>() {

			@Override
			public Boolean invoke(T arg) {
				return set.add(arg);
			}
			
		});
	}
	
	/**
	 * Sorts the Enumerable<T> with the given comparison function.
	 * @param comparator A function to compare elements.
	 * @return A sorted Enumerable<T>.
	 */
	public Enumerable<T> sort(final Comparator<? super T> comparator) {
		List<T> list = toList();
		Collections.sort(list, comparator);
		return Enumerable.create(list);
	}
}