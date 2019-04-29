package net.jetblack.feedbus.example.subscriber;

import java.util.Map;

import net.jetblack.feedbus.adapters.Client;
import net.jetblack.feedbus.adapters.ConnectionChangedEvent;
import net.jetblack.feedbus.adapters.ConnectionChangedListener;
import net.jetblack.feedbus.adapters.DataErrorEvent;
import net.jetblack.feedbus.adapters.DataErrorListener;
import net.jetblack.feedbus.adapters.DataReceivedEvent;
import net.jetblack.feedbus.adapters.DataReceivedListener;

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
			
			client.addDataReceivedListener(new DataReceivedListener() {
				
				@Override
				public void onDataReceived(DataReceivedEvent event) {
					System.out.println("Data received: " + event.getData());
//					@SuppressWarnings("unchecked")
//					Map<String,Object> data = (Map<String, Object>) event.getData();
//					for (Map.Entry<String, Object> item : data.entrySet()) {
//						System.out.println(item.getKey() + ": " + item.getValue());
//					}
				}
			});
			
			client.addDataErrorListener(new DataErrorListener() {
				
				@Override
				public void onDataErrorEvent(DataErrorEvent event) {
					System.out.println("onDataError: " + event);
					
				}
			});

			client.addConnectionChangedListener(new ConnectionChangedListener() {
				
				@Override
				public void onConnectionChanged(ConnectionChangedEvent event) {
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
