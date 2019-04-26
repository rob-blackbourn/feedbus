package net.jetblack.feedbus.util;

/**
 * A generic immutable key value pair.
 * @param <K> The type of the key.
 * @param <V> The type of the value.
 */
public class KeyValuePair<K, V> {

	/**
	 * Constructs a key value pair.
	 * @param key The key.
	 * @param value The value.
	 */
	public KeyValuePair(K key, V value) {
		Key = key;
		Value = value;
	}
	
	/**
	 * The key.
	 */
	public final K Key;
	
	/**
	 * The value.
	 */
	public final V Value;

	/**
	 * A convenience function for create KeyValuePair<K, V>.
	 * @param key The key.
	 * @param value The value.
	 * @return A KeyValuePair<K, V>.
	 */
	public static <K,V> KeyValuePair<K,V> create(K key, V value) {
		return new KeyValuePair<K,V>(key, value);
	}
	
}
