package net.jetblack.feedbus.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * A helper class for String values.
 */
public class Strings {

	/**
	 * Joins strings from an Iterator<?>.
	 * @param separator The separator to use when joining the strings.
	 * @param iterator The source iterator.
	 * @return A String of the items separated by the given separator.
	 */
	public static String join(String separator, Iterator<?> iterator) {
		
		StringBuilder s = new StringBuilder();
		
		while (iterator.hasNext()) {
			if (s.length() > 0)
				s.append(separator);
			s.append(iterator.next());
		}
		
		return s.toString();
	}

	/**
	 * Joins strings from an Iterator<?>.
	 * @param separator The separator to use when joining the strings.
	 * @param collection The source collection.
	 * @return A String of the items separated by the given separator.
	 */
	public static String join(String separator, Collection<?> collection) {
		return join(separator, collection.iterator());
	}

	/**
	 * Joins strings from an Iterator<?>.
	 * @param separator The separator to use when joining the strings.
	 * @param array The source array.
	 * @return A String of the items separated by the given separator.
	 */
	public static String join(String separator, String[] array)
	{
		return join(separator, Enumerable.create(array));
	}
}