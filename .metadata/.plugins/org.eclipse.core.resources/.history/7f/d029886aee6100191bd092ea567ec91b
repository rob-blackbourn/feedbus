package net.jetblack.feedbus.example.notifier;

import java.util.Scanner;

import net.jetblack.feedbus.adapters.Client;
import net.jetblack.feedbus.adapters.ForwardedSubscriptionEventArgs;
import net.jetblack.util.EventListener;

public class Program {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		try {
			Client client = Client.createFromProperties();
			
			client.ForwardedSubscription.add(new EventListener<ForwardedSubscriptionEventArgs>() {

				@Override
				public void onEvent(ForwardedSubscriptionEventArgs event) {
					System.out.println("Subscription received on feed \"" + event.Feed+ "\" for topic \"" + event.Topic + "\" add "+ event.IsAdd);
				}
				
			});

			System.out.println("Press n to add the notification");
			scanner.next();
			
			client.addNotification("FOO");
			
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
