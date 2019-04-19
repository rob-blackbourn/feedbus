package net.jetblack.feedbus.distributor;

import net.jetblack.feedbus.distributor.config.DistributorConfig;

public class Program {

	public static void main(String[] args) {
		try {
			DistributorConfig config = DistributorConfig.createFromProperties();

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
