package net.jetblack.feedbus.example.selectfeed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import net.jetblack.feedbus.adapters.Client;

public class Program {

	private final static int PUBLISH_MS = 100;

	private static final Random RND = new Random();

	public static void main(String[] args) {

		System.setProperty("net.jetblack.feedbus.adapters.SERIALIZER", "net.jetblack.feedbus.json.JsonSerializer");

		try {
			Client client = Client.createFromProperties();

			CachingPublisher cachingPublisher = new CachingPublisher(client);

			StartPublishing(cachingPublisher);

			System.out.println("Press ^C to quit");
			Thread.currentThread().join();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static List<Timer> StartPublishing(CachingPublisher cachingPublisher) throws InterruptedException {
		// Prepare some data.
		Map<String, Map<String, Object>> lseData = new HashMap<String, Map<String, Object>>();
		lseData.put("VOD", Maps.mapOf("NAME", "Vodafone Group PLC", "BID", 140.60, "ASK", 140.65));
		lseData.put("TSCO", Maps.mapOf("NAME", "Tesco PLC", "BID", 423.15, "ASK", 423.25));
		lseData.put("SBRY", Maps.mapOf("NAME", "J Sainsbury PLC", "BID", 325.30, "ASK", 325.35));

		Map<String, Map<String, Map<String, Object>>> marketData = new HashMap<String, Map<String, Map<String, Object>>>();
		marketData.put("LSE", lseData);
		

		// Publish the data.
		for (Map.Entry<String, Map<String, Map<String, Object>>> feedItem : marketData.entrySet()) {
			String feed = feedItem.getKey();
			Map<String, Map<String, Object>> topicData = feedItem.getValue();

			cachingPublisher.addNotification(feed);

			for (Map.Entry<String, Map<String, Object>> topicItem : topicData.entrySet()) {
				String topic = topicItem.getKey();
				Map<String,Object> data = topicItem.getValue();
				
				cachingPublisher.publish(feed, topic, data);
			}
		}

		List<Timer> timers = new ArrayList<Timer>();
		for (Map.Entry<String, Map<String, Map<String, Object>>> feedItem : marketData.entrySet()) {
			String feed = feedItem.getKey();
			Map<String, Map<String, Object>> topicData = feedItem.getValue();

			for (Map.Entry<String, Map<String, Object>> topicItem : topicData.entrySet()) {
				String topic = topicItem.getKey();
				Map<String,Object> data = topicItem.getValue();

				Timer timer = new Timer();
				timers.add(timer);
				
				ScheduleUpdate(cachingPublisher, feed, topic, data, timer);
			}
		}

		return timers;
	}

	private static void ScheduleUpdate(CachingPublisher cachingPublisher, String feed, String topic, Map<String, Object> data,
			Timer timer) {
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				PublishUpdate(cachingPublisher, feed, topic, data);
				ScheduleUpdate(cachingPublisher, feed, topic, data, timer);
			}
		}, (long) (PUBLISH_MS * RND.nextDouble() * 100 + 5));

	}

	private static void PublishUpdate(CachingPublisher cachingPublisher, String feed, String topic, Map<String, Object> data) {
		// Perturb the data a little.
		double bid = (double) data.get("BID");
		double ask = (double) data.get("ASK");
		double spread = ask - bid;
		double nextBid = Math.round((bid + bid * RND.nextDouble() * 5.0 / 100.0) * 100.0) / 100.0;
		double nextAsk = Math.round((nextBid + spread) * 100.0) / 100.0;
		Map<String, Object> newData = Maps.mapOf("BID", nextBid, "ASK", nextAsk);
		cachingPublisher.publish(feed, topic, newData);
	}
}
