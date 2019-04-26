package net.jetblack.feedbus.util;

import java.util.HashMap;
import java.util.Map;

public class Maps {
	
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
