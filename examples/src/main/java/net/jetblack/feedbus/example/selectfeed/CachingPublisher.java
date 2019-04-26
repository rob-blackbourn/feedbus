package net.jetblack.feedbus.example.selectfeed;

import java.util.Map;

import net.jetblack.feedbus.adapters.Client;
import net.jetblack.feedbus.adapters.ForwardedSubscriptionEventArgs;
import net.jetblack.feedbus.util.EventListener;

public class CachingPublisher {

	private final Client _client;
	private final Cache _cache;
	private final Object _gate = new Object();

	public CachingPublisher(Client client) {
		_client = client;
		_cache = new Cache(client);

		_client.ForwardedSubscription.add(new EventListener<ForwardedSubscriptionEventArgs>() {

			@Override
			public void onEvent(ForwardedSubscriptionEventArgs event) {
				synchronized (_gate) {
					if (event.getIsAdd()) {
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
