package net.jetblack.feedbus.example.selectfeed;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.jetblack.feedbus.adapters.Client;

public class Cache {
	
	private static final Logger logger = Logger.getLogger(Cache.class.getName());

	private final Client _client;
	private final Map<String, Map<String, CacheItem>> _cacheItems = new HashMap<String, Map<String, CacheItem>>(); // Feed -> Topic -> CacheItem

	public Cache(Client client) {
		_client = client;
	}

    public void addSubscription(String clientId, String feed, String topic) {
        logger.fine("AddSubscription: clientId=" + clientId + ", topic=\"" + topic + "\"");

        // Have we received a subscription or published data on this feed yet?
        Map<String, CacheItem> topicCache = _cacheItems.get(feed);
        if (topicCache == null) {
        	topicCache = new HashMap<String, CacheItem>();
        	_cacheItems.put(feed, topicCache);
        }

        // Have we received a subscription or published data on this topic yet?
        CacheItem cacheItem = topicCache.get(topic);
        if (cacheItem == null) {
        	cacheItem = new CacheItem();
            topicCache.put(topic, cacheItem);
        }

        // Has this client already subscribed to this topic?
        if (!cacheItem.getClientStates().containsKey(clientId))
        {
            // Add the client to the cache item, and indicate that we have not yet sent an image.
            cacheItem.getClientStates().put(clientId, false);
        }

        if (!(cacheItem.getClientStates().get(clientId) || cacheItem.getData() == null)) {
            // Send the image and mark this client appropriately.
            cacheItem.getClientStates().put(clientId, true);

            logger.info("Sending image on feed \"" + feed + "\" with topic \"" + topic + " to client " + clientId);
            _client.send(clientId, feed, topic, true, cacheItem.getData());
        }
    }
    
    public void removeSubscription(String clientId, String feed, String topic)
    {
        logger.fine("RemoveSubscription: clientId=" + clientId + ", topic=\"" + topic + "\"");

        // Have we received a subscription or published data on this feed yet?
        Map<String, CacheItem> topicCache = _cacheItems.get(feed);
        if (topicCache == null) {
        	return;
        }

        // Have we received a subscription or published data on this topic yet?
        CacheItem cacheItem = topicCache.get(topic);
        if (cacheItem == null) {
            return;
        }

        // Does this topic have this client?
        if (!cacheItem.getClientStates().containsKey(clientId)) {
            return;
        }

        cacheItem.getClientStates().remove(clientId);

        // If there are no clients and no data remove the item.
        if (cacheItem.getClientStates().size() == 0 || cacheItem.getData() == null) {
        	logger.info("Stop publishing feed \"" + feed + "\" topic \"" +topic + "\"");
            _cacheItems.remove(topic);
        }
    }
    
    public void publish(String feed, String topic, Map<String, Object> data) {

    	// If the feed is not in the cache add it.
        Map<String, CacheItem> topicCache = _cacheItems.get(feed);
        if (topicCache == null) {
        	topicCache = new HashMap<String, CacheItem>();
        	_cacheItems.put(feed, topicCache);
        }

    	// If the topic is not in the cache add it.
        CacheItem cacheItem = topicCache.get(topic);
        if (cacheItem == null) {
        	cacheItem = new CacheItem();
        	cacheItem.setData(data);
            topicCache.put(topic, cacheItem);
        }

        // Bring the cache data up to date.
        for (Map.Entry<String, Object> item : data.entrySet()) {
            cacheItem.getData().put(item.getKey(), item.getValue());
        }

        // If there are any clients listening publish the data.
        if (cacheItem.getClientStates().size() > 0) {
            logger.info("Publishing update on feed \"" + feed + "\" with topic \"" + topic);
            _client.publish(feed, topic, false, (Object)data);
        }
    }
    
}
