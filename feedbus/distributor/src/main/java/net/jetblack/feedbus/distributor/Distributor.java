package net.jetblack.feedbus.distributor;

import java.io.IOException;
import java.util.logging.LogManager;

/**
 * The entry point for starting a distributor.
 */
public class Distributor {

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
			ProgramArgs programArgs = ProgramArgs.parse(args);

			Server server = new Server(
					programArgs.getConfig().getAddress(), 
					programArgs.getConfig().getPort(), 
					programArgs.getConfig().getEventQueueCapacity(), 
					programArgs.getConfig().getWriteQueueCapacity());
	        server.start(programArgs.getConfig().getHeartbeatInterval());

	        
	        Runtime.getRuntime().addShutdownHook(new Thread() {
	        	public void run() {
	        		try {
						server.close();
				        LogManager.getLogManager().reset();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        	}
	        });
	        
	        Thread.currentThread().join();
	        System.out.println("Done");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
