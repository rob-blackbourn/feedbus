package net.jetblack.feedbus.example.selectfeed;

import java.util.HashMap;
import java.util.Map;

public class CacheItem {
	
	private final Map<String, Boolean> _clientStates = new HashMap<String, Boolean>();
	
	private Map<String, Object> _data;
	
	public Map<String, Boolean> getClientStates() {
		return _clientStates;
	}
	
	public Map<String, Object> getData() {
		return _data;
	}
	
	public void setData(Map<String, Object> value) {
		_data = value;
	}
			
}
