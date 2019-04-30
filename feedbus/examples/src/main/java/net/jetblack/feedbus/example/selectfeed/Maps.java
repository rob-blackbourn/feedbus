package net.jetblack.feedbus.example.selectfeed;

import java.util.HashMap;
import java.util.Map;

/**
 * A convenience class for Map<K, V>.
 */
public class Maps {

	/**
	 * Creates a Map<String, Object> from the given arguments.
	 * @param args A sequence of String and Object values.
	 * @return A map of the supplied arguments.
	 * @throws IllegalArgumentException If the supplied key is not a String.
	 */
	public static Map<String, Object> mapOf(Object...args) {
		
		if (args.length % 2 != 0) {
			throw new IllegalArgumentException("There must be an even number of arguments");
		}
		
		Map<String, Object> map = new HashMap<String,Object>();
		for (int i = 0; i < args.length; i += 2) {
			if (!(args[i] instanceof String)) {
				throw new IllegalArgumentException("Key must be a String");
			}
			String key = (String)args[i];
			Object value = args[i+1];
			map.put(key, value);
		}
		
		return map;
	}
	
}
