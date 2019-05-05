package net.jetblack.feedbus.distributor;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import net.jetblack.feedbus.distributor.config.DistributorConfig;

/**
 * Represents the command line arguments.
 */
public class ProgramArgs {
	
	private final static String HELP_LONG_OPT = "help";
	private final static String HOST_ARG = "h";
	private final static String HOST_LONG_OPT = "host";
	private final static String PORT_ARG = "p";
	private final static String PORT_LONG_OPT = "port";
	private final static String EVENT_QUEUE_CAPACITY_LONG_OPT = "event-queue-capacity";
	private final static String WRITE_QUEUE_CAPACITY_LONG_OPT = "write-queue-capacity";
	private final static String HEARTBEAT_INTERVAL_LONG_OPT = "heartbeat-interval";

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
	public static ProgramArgs parse(String[] args) {
		
		Options options = new Options()
				.addOption(Option.builder()
						.longOpt(HELP_LONG_OPT)
						.desc("Show the usage.")
						.build())
				.addOption(Option.builder(HOST_ARG)
						.longOpt(HOST_LONG_OPT)
						.hasArg()
						.desc("The hostname or address to bind to.")
						.build())
				.addOption(Option.builder(PORT_ARG)
						.longOpt(PORT_LONG_OPT)
						.hasArg()
						.desc("The port to bind to.")
						.build())
				.addOption(Option.builder()
						.longOpt(EVENT_QUEUE_CAPACITY_LONG_OPT)
						.hasArg()
						.desc("The capacity of the event queue.")
						.build())
				.addOption(Option.builder()
						.longOpt(WRITE_QUEUE_CAPACITY_LONG_OPT)
						.hasArg()
						.desc("The capacuty of the interactor write queue.")
						.build())
				.addOption(Option.builder()
						.longOpt(HEARTBEAT_INTERVAL_LONG_OPT)
						.hasArg()
						.desc("The interval in milliseconds of the heartbeat or 0 to disable.")
						.build());
		
		CommandLineParser parser = new DefaultParser();

		try {
			CommandLine commandLine = parser.parse(options, args, false);

			if (commandLine.hasOption(HELP_LONG_OPT)) {
				throw new ParseException("Show help");
			}
			
		    String host = commandLine.getOptionValue(HOST_LONG_OPT, DistributorConfig.DEFAULT_HOST);
		    int port = commandLine.hasOption(PORT_LONG_OPT)
		    		? Integer.parseInt(commandLine.getOptionValue(PORT_LONG_OPT))
		    				: DistributorConfig.DEFAULT_PORT;
		    int eventQueueCapacity = commandLine.hasOption(EVENT_QUEUE_CAPACITY_LONG_OPT)
		    		? Integer.parseInt(commandLine.getOptionValue(EVENT_QUEUE_CAPACITY_LONG_OPT))
		    				: DistributorConfig.DEFAULT_EVENT_QUEUE_CAPACITY;
		    int writeQueueCapacity = commandLine.hasOption(WRITE_QUEUE_CAPACITY_LONG_OPT)
		    		? Integer.parseInt(commandLine.getOptionValue(WRITE_QUEUE_CAPACITY_LONG_OPT))
		    				: DistributorConfig.DEFAULT_WRITE_QUEUE_CAPACITY;
		    long heartbeatInterval = commandLine.hasOption(HEARTBEAT_INTERVAL_LONG_OPT)
		    		? Long.parseLong(commandLine.getOptionValue(HEARTBEAT_INTERVAL_LONG_OPT))
		    				: DistributorConfig.DEFAULT_HEARTBEAT_INTERVAL;

			DistributorConfig config = new DistributorConfig(
					InetAddress.getByName(host),
					port,
					eventQueueCapacity,
					writeQueueCapacity,
					heartbeatInterval);

			return new ProgramArgs(config, commandLine.getArgs());
			
		} catch (ParseException e) {
		   final HelpFormatter formatter = new HelpFormatter();
		   formatter.printHelp("distributor", options, true);
		} catch (UnknownHostException e) {
			System.err.println("Unknown host name");
		}
		
		System.exit(1);
		
		return null;
	}
}
