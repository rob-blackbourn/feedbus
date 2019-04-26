package net.jetblack.feedbus.example.publisher;

import net.jetblack.feedbus.adapters.Client;

public class Publisher {
	
	public static void main(String[] args) {
		
		try {
			ProgramArgs programArgs = ProgramArgs.parse(args);
			String[] remainingArgs = programArgs.getRemaining();
			if (remainingArgs.length != 3) {
				System.out.println("Usage: publisher [options] feed topic data");
			}
			
			Client client = Client.create(programArgs.getConfig());
			
			String feed = remainingArgs[0];
			String topic = remainingArgs[1];
			String data = remainingArgs[2];

			System.out.println("Publishing: Feed=\"" + feed + "\", Topic=\"" + topic + "\", Data=\"" + data + "\"");
			for (int i = 0; i < 10000; ++i) {
				Thread.sleep(1);
				client.publish(feed, topic, true, data);
			}

			System.out.println("Press ^C to quit");
			Thread.currentThread().join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
