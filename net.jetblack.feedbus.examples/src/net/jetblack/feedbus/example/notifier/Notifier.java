package net.jetblack.feedbus.example.notifier;

import net.jetblack.feedbus.adapters.Client;
import net.jetblack.feedbus.adapters.ForwardedSubscriptionEventArgs;
import net.jetblack.util.EventListener;

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
			
			client.ForwardedSubscription.add(new EventListener<ForwardedSubscriptionEventArgs>() {

				@Override
				public void onEvent(ForwardedSubscriptionEventArgs event) {
					System.out.println("Subscription received on feed \"" + event.getFeed() + "\" for topic \"" + event.getTopic() + "\" add "+ event.getIsAdd());
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
