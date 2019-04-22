package net.jetblack.feedbus.distributor;

import net.jetblack.feedbus.distributor.config.DistributorConfig;

/**
 * The entry point for starting a distributor.
 */
public class Program {

	/**
	 * Starts a distributor.
	 * 
	 * <ul>
	 *   <li>--host 0.0.0.0</li>
	 *   <li>--port 30011</li>
	 *   <li>--event-queue-capacity 8096</li>
	 *   <li>--write-queue-capacity 0896</li>
	 *   <li>--heartbeat-interval 1000</li>
	 * </ul>
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		try {
			DistributorConfig config = DistributorConfig.createFromArgs(args);

			Server server = new Server(
					config.getAddress(), 
					config.getPort(), 
					config.getEventQueueCapacity(), 
					config.getWriteQueueCapacity());
	        server.start(config.getHeartbeatInterval());
	        
	        System.out.println("Press <ENTER> to quit");
	        System.in.read();
	        
	        server.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
