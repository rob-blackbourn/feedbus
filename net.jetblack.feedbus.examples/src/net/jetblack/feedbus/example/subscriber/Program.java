package net.jetblack.feedbus.example.subscriber;

import java.util.Map;

import net.jetblack.feedbus.adapters.Client;
import net.jetblack.feedbus.adapters.ConnectionChangedEventArgs;
import net.jetblack.feedbus.adapters.DataReceivedEventArgs;
import net.jetblack.util.EventListener;

public class Program {

	// --byte-serializer-type net.jetblack.feedbus.json.JsonSerializer 
	public static void main(String[] args) {
		
		try {
			ProgramArgs programArgs = ProgramArgs.parse(args);
			
			String[] remainingArgs = programArgs.getRemaining();
			if (remainingArgs.length != 2) {
				System.out.println("Usage: subscriber [options] feed topic");
				System.exit(1);
			}

			Client client = Client.create(programArgs.getConfig());
			
			
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
			
			client.ConnectionChanged.add(new EventListener<ConnectionChangedEventArgs>() {
				
				@Override
				public void onEvent(ConnectionChangedEventArgs event) {
					System.out.println("Connection changed: " + event);
				}
			});


			String feed = remainingArgs[0];
			String topic = remainingArgs[1];
			
			System.out.println("Subscribing to feed \"" + feed + "\" topic \"" + topic + "\"");
			client.addSubscription(feed, topic);

			System.out.println("Press ^C to quit");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
