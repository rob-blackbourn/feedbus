package net.jetblack.feedbus.example.notifier;

import net.jetblack.feedbus.adapters.Client;
import net.jetblack.feedbus.adapters.ForwardedSubscriptionEvent;
import net.jetblack.feedbus.adapters.ForwardedSubscriptionListener;

public class Notifier {

	public static void main(String[] args) {

		try {
			ProgramArgs programArgs = ProgramArgs.parse(args);
			String[] remainingArgs = programArgs.getRemaining();
			if (remainingArgs.length != 1) {
				System.out.println("Usage: notifier [options] feed");
				System.exit(1);
			}
			
			Client client = Client.create(programArgs.getConfig());
			client.addForwardedSubscriptionListener(new ForwardedSubscriptionListener() {
				
				@Override
				public void onForwardedSubscription(ForwardedSubscriptionEvent event) {
					System.out.println("Subscription received on feed \"" + event.getFeed() + "\" for topic \"" + event.getTopic() + "\" add "+ event.isAdd());
				}
			});

			String feed = remainingArgs[0];
			System.out.println("Requesting notifications on Feed=\"" + feed + "\"");
			client.addNotification(feed);
			
			System.out.println("Press ^C to quit");
			Thread.currentThread().join();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
