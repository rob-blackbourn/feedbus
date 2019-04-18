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
					System.out.println("Data received: " + event.Data);
					@SuppressWarnings("unchecked")
					Map<String,Object> data = (Map<String, Object>) event.Data;
					for (Map.Entry<String, Object> item : data.entrySet()) {
						System.out.println(item.getKey() + ": " + item.getValue());
					}
				}
			});

			
			System.out.println("Type the name of the ticker (e.g. SBRY)");
			String ticker = scanner.next();

			client.addSubscription("LSE", ticker);

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
