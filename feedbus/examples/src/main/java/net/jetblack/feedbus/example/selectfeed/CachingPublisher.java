package net.jetblack.feedbus.example.selectfeed;

import java.util.Map;

import net.jetblack.feedbus.adapters.Client;
import net.jetblack.feedbus.adapters.ForwardedSubscriptionEvent;
import net.jetblack.feedbus.adapters.ForwardedSubscriptionListener;

public class CachingPublisher {

	private final Client _client;
	private final Cache _cache;
	private final Object _gate = new Object();

	public CachingPublisher(Client client) {
		_client = client;
		_cache = new Cache(client);

		_client.addForwardedSubscriptionListener(new ForwardedSubscriptionListener() {
			
			@Override
			public void onForwardedSubscription(ForwardedSubscriptionEvent event) {
				synchronized (_gate) {
					if (event.isAdd()) {
						_cache.addSubscription(event.getClientId(), event.getFeed(), event.getTopic());
					}
					else {
						_cache.removeSubscription(event.getClientId(), event.getFeed(), event.getTopic());
					}
				}
			}
		});
	}

	public void publish(String feed, String topic, Map<String, Object> data) {
		synchronized (_gate) {
			_cache.publish(feed, topic, data);
		}
	}

	public void addNotification(String feed) throws InterruptedException {
		_client.addNotification(feed);
	}

	public void removeNotification(String feed) throws InterruptedException {
		_client.removeNotification(feed);
	}
	
	
}
