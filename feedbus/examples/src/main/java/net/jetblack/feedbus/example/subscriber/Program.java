package net.jetblack.feedbus.example.subscriber;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import net.jetblack.feedbus.adapters.Client;
import net.jetblack.feedbus.adapters.ConnectionChangedEvent;
import net.jetblack.feedbus.adapters.ConnectionChangedListener;
import net.jetblack.feedbus.adapters.DataErrorEvent;
import net.jetblack.feedbus.adapters.DataErrorListener;
import net.jetblack.feedbus.adapters.DataReceivedEvent;
import net.jetblack.feedbus.adapters.DataReceivedListener;
import net.jetblack.feedbus.adapters.HeartbeatListener;
import net.jetblack.feedbus.adapters.config.ConnectionConfig;

public class Program implements DataReceivedListener, DataErrorListener, ConnectionChangedListener, HeartbeatListener {

	// --byte-serializer-type net.jetblack.feedbus.json.JsonSerializer 
	public static void main(String[] args) {
		
		try {
			ProgramArgs programArgs = ProgramArgs.parse(args);
			Program program = new Program(programArgs.getConfig(), programArgs.getRemaining());
			System.out.println("Press ^C to quit");
			Thread.currentThread().join();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private final Client _client;
	
	public Program(ConnectionConfig connectionConfig, String[] args) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException, InterruptedException {
		if (args.length != 2) {
			System.out.println("Usage: subscriber [options] feed topic");
			System.exit(1);
		}

		_client = Client.create(connectionConfig);
		_client.addDataReceivedListener(this);
		_client.addConnectionChangedListener(this);
		_client.addDataErrorListener(this);
		_client.addHeartbeatListener(this);
		
		String feed = args[0];
		String topic = args[1];
		
		System.out.println("Subscribing to feed \"" + feed + "\" topic \"" + topic + "\"");
		_client.addSubscription(feed, topic);
	}

	@Override
	public void onConnectionChanged(ConnectionChangedEvent event) {
		System.out.println("Connection changed: " + event);
	}

	@Override
	public void onDataReceived(DataReceivedEvent event) {
		System.out.println("Data received: " + event.getData());
	}

	@Override
	public void onHeartbeat() {
		System.out.println("Heartbeat received");
	}

	@Override
	public void onDataErrorEvent(DataErrorEvent event) {
		System.out.println("Data error: " + event);
	}

}
