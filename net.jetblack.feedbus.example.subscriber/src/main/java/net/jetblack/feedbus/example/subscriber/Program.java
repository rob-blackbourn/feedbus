package net.jetblack.feedbus.example.subscriber;

import java.util.Map;
import java.util.Scanner;

import net.jetblack.feedbus.adapters.Client;
import net.jetblack.feedbus.adapters.DataReceivedEventArgs;
import net.jetblack.util.EventListener;

public class Program {

	public static void main(String[] args) {
		
		System.setProperty("net.jetblack.feedbus.adapters.SERIALIZER", "net.jetblack.feedbus.json.JsonSerializer");
		
		Scanner scanner = new Scanner(System.in);

		try {
			Client client = Client.createFromProperties();
			
			client.DataReceived.add(new EventListener<DataReceivedEventArgs>() {
				
				@Override
				public void onEvent(DataReceivedEventArgs event) {
					System.out.println("Data received: " + event.getData());
					@SuppressWarnings("unchecked")
					Map<String,Object> data = (Map<String, Object>) event.getData();
					for (Map.Entry<String, Object> item : data.entrySet()) {
						System.out.println(item.getKey() + ": " + item.getValue());
					}
				}
			});

			System.out.println("Type the name of the feed (e.g. LSE)");
			String feed = scanner.next();
			
			System.out.println("Type the name of the topic (e.g. SBRY)");
			String topic = scanner.next();

			System.out.println("Subscribing to feed \"" + feed + "\" topic \"" + topic + "\"");
			client.addSubscription(feed, topic);

			System.out.println("Press n to close");
			scanner.next();
			
			client.close();

			System.out.println("Press n to quit");
			scanner.next();
			
			scanner.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
