package net.jetblack.feedbus.distributor;

import java.net.InetAddress;
import java.net.UnknownHostException;

import net.jetblack.feedbus.distributor.config.DistributorConfig;

/**
 * Represents the command line arguments.
 */
public class ProgramArgs {

	private final DistributorConfig _config;
	private final String[] _remaining;
	
	private ProgramArgs(DistributorConfig config, String[] remaining) {
		_config = config;
		_remaining = remaining;
	}

	/**
	 * Get the distributor config.
	 * @return The distributor config.
	 */
	public DistributorConfig getConfig() {
		return _config;
	}
	
	/**
	 * Gets the unparsed command line.
	 * @return The unparsed command line.
	 */
	public String[] getRemaining() {
		return _remaining;
	}
	
	/**
	 * Parse the command line.
	 * @param args The command line arguments.
	 * @return The parsed command line.
	 * @throws UnknownHostException
	 */
	public static ProgramArgs parse(String[] args) throws UnknownHostException {
		
	    String host = DistributorConfig.DEFAULT_HOST;
	    int port = DistributorConfig.DEFAULT_PORT;
	    int eventQueueCapacity = DistributorConfig.DEFAULT_EVENT_QUEUE_CAPACITY;
	    int writeQueueCapacity = DistributorConfig.DEFAULT_WRITE_QUEUE_CAPACITY;
	    long heartbeatInterval = DistributorConfig.DEFAULT_HEARTBEAT_INTERVAL;
	    
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
			} else if ("--event-queue-capacity".equals(args[argc])) {
				if (++argc >= args.length) {
					Usage("--event-queue-capacity");
				}
				eventQueueCapacity = Integer.parseInt(args[argc]);
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
			}
			
			++argc;
		}
		
		DistributorConfig config = new DistributorConfig(
				InetAddress.getByName(host),
				port,
				eventQueueCapacity,
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
