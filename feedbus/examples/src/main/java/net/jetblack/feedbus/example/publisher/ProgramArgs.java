package net.jetblack.feedbus.example.publisher;

import java.net.InetAddress;
import java.net.UnknownHostException;

import net.jetblack.feedbus.adapters.config.ConnectionConfig;
import net.jetblack.feedbus.adapters.ByteSerializable;

public class ProgramArgs {

	private final ConnectionConfig _config;
	private final String[] _remaining;
	
	private ProgramArgs(ConnectionConfig config, String[] remaining) {
		_config = config;
		_remaining = remaining;
	}
	
	public ConnectionConfig getConfig() {
		return _config;
	}
	
	public String[] getRemaining() {
		return _remaining;
	}
	
	public static ProgramArgs parse(String[] args) throws UnknownHostException, ClassNotFoundException {
		
	    String host = ConnectionConfig.DEFAULT_HOST;
	    int port = ConnectionConfig.DEFAULT_PORT;
	    int writeQueueCapacity = ConnectionConfig.DEFAULT_WRITE_QUEUE_CAPACITY;
	    long heartbeatInterval = ConnectionConfig.DEFAULT_HEARTBEAT_INTERVAL;
	    Class<?> byteSerializerType = ConnectionConfig.DEFAULT_SERIALIZER;
	    
		int argc = 0;
		while (argc < args.length) {
			
			if (!args[argc].startsWith("-")) {
				break;
			}
			
			if ("--host".equals(args[argc])) {
				if (++argc >= args.length) {
					Usage("--host");
				}
				host = args[argc];
			} else if ("--port".equals(args[argc])) {
				if (++argc >= args.length) {
					Usage("--port");
				}
				port = Integer.parseInt(args[argc]);
			} else if ("--write-queue-capacity".equals(args[argc])) {
				if (++argc >= args.length) {
					Usage("--write-queue-capacity");
				}
				writeQueueCapacity = Integer.parseInt(args[argc]);
			} else if ("--heartbeat-interval".equals(args[argc])) {
				if (++argc >= args.length) {
					Usage("--heartbeat-interval");
				}
				heartbeatInterval = Long.parseLong(args[argc]);
			} else if ("--byte-serializer-type".equals(args[argc])) {
				if (++argc >= args.length) {
					Usage("--byte-serializer-type");
				}
				byteSerializerType = Class.forName(args[argc]);
				if (!ByteSerializable.class.isAssignableFrom(byteSerializerType)) {
					Usage("--byte-serializer-type");
				}
			}
			
			++argc;
		}
		
		@SuppressWarnings("unchecked")
		ConnectionConfig config = new ConnectionConfig(
				InetAddress.getByName(host), 
				port, 
				(Class<? extends ByteSerializable>)byteSerializerType, 
				writeQueueCapacity, 
				heartbeatInterval);
		
		String[] remaining = new String[args.length - argc];
		for (int i = 0; i < remaining.length; ++i) {
			remaining[i] = args[argc + i];
		}
		
		return new ProgramArgs(config, remaining);
	}
	
	private static void Usage(String parameter) {
		System.out.println("Invalid argument: " + parameter);
		System.exit(1);
	}
}
