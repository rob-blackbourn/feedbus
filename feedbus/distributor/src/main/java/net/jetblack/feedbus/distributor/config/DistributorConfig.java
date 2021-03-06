package net.jetblack.feedbus.distributor.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

import net.jetblack.feedbus.distributor.Distributor;

/**
 * Configuration for the distributor.
 */
public class DistributorConfig {
	
	public static final String DEFAULT_HOST = "0.0.0.0";
	public static final int DEFAULT_PORT = 9775;
	public static final int DEFAULT_EVENT_QUEUE_CAPACITY = 8096;
	public static final int DEFAULT_WRITE_QUEUE_CAPACITY = 8096;
	public static final long DEFAULT_HEARTBEAT_INTERVAL = 1000;

    private InetAddress _address;
    private int _port;
    private int _eventQueueCapacity;
    private int _writeQueueCapacity;
    private long _heartbeatInterval;

    /**
     * Construct a distributor with default values.
     * 
     * @throws UnknownHostException 
     */
    public DistributorConfig() throws UnknownHostException {
    	_address = InetAddress.getByName(DEFAULT_HOST);
    	_port = DEFAULT_PORT;
    	_eventQueueCapacity = DEFAULT_EVENT_QUEUE_CAPACITY;
    	_writeQueueCapacity = DEFAULT_WRITE_QUEUE_CAPACITY;
    	_heartbeatInterval = DEFAULT_HEARTBEAT_INTERVAL;
    }
    
    /**
     * Construct explicitly.
     * 
     * @param address The address to bind to.
     * @param port The port to bind to.
     * @param eventQueueCapacity The capacity of the event queue.
     * @param writeQueueCapacity The capacity of the interactor write queues.
     * @param heartbeatInterval The heart beat interval in milliseconds or 0 for no heart beat.
     */
    public DistributorConfig(InetAddress address, int port, int eventQueueCapacity, int writeQueueCapacity, long heartbeatInterval) {
    	_address = address;
    	_port = port;
    	_eventQueueCapacity = eventQueueCapacity;
    	_writeQueueCapacity = writeQueueCapacity;
    	_heartbeatInterval = heartbeatInterval;
    }
    
    /**
     * Get the address to bind to.
     * 
     * @return The bind address.
     */
    public InetAddress getAddress() {
    	return _address;
    }
    
    /**
     * Set the address to bind to.
     * 
     * @param value The address to bind to.
     */
    public void setAddress(InetAddress value) {
    	_address = value;
    }
    
    /**
     * Get the port to bind to.
     * 
     * @return The port to bind to.
     */
    public int getPort() {
    	return _port;
    }
    
    /**
     * Set the port to bind to.
     * 
     * @param value The port to bind to.
     */
    public void setPort(int value) {
    	_port = value;
    }
    
    /**
     * Get the capacity of the event queue.
     * 
     * @return The capacity of the event queue.
     */
    public int getEventQueueCapacity() {
    	return _eventQueueCapacity;
    }
    
    /**
     * Set the capacity of the event queue.
     * 
     * @param value The capacity of the event queue.
     */
    public void setEventQueueCapacity(int value) {
    	_eventQueueCapacity = value;
    }
    
    /**
     * Get the capacity of the interactor write queue.
     * 
     * @return The capacity of the interactor write queue.
     */
    public int getWriteQueueCapacity() {
    	return _writeQueueCapacity;
    }
    
    /**
     * Set the capacity of the interactor write queues.
     * 
     * @param value The capacity of the interactor write queues.
     */
    public void setWriteQueueCapacity(int value) {
    	_writeQueueCapacity = value;
    }
    
    /**
     * Gets the heart beat interval.
     * 
     * @return The heart beat interval.
     */
    public long getHeartbeatInterval() {
    	return _heartbeatInterval;
    }
    
    /**
     * Sets the heart beat interval in milliseconds or 0 for no heart beat.
     * 
     * @param value The heart beat interval in milliseconds.
     */
    public void setHeartbeatInterval(long value) {
    	_heartbeatInterval = value;
    }

    /**
     * Create configuration from properties.
     * 
     * The following properties are available:
     * 
     * net.jetblack.feedbus.distributor.HOST=0.0.0.0
	 * net.jetblack.feedbus.distributor.PORT=30011
	 * net.jetblack.feedbus.distributor.EVENT_QUEUE_CAPACITY=8096
	 * net.jetblack.feedbus.distributor.WRITE_QUEUE_CAPACITY=8096
	 * net.jetblack.feedbus.distributor.HEARTBEAT_INTERVAL=1000
	 * 
     * @return The distributor configuration.
     * @throws UnknownHostException
     */
    public static DistributorConfig createFromProperties() throws UnknownHostException {
		String packageName = Distributor.class.getPackage().getName();
		
		InetAddress address = InetAddress.getByName(System.getProperty(packageName + ".HOST", DEFAULT_HOST));
		
		String portText = System.getProperty(packageName + ".PORT");
		int port = portText== null ? DEFAULT_PORT : Integer.parseInt(portText);

		String eventQueueLengthText = System.getProperty(packageName + ".EVENT_QUEUE_CAPACITY");
		int eventQueueLength = eventQueueLengthText== null ? DEFAULT_EVENT_QUEUE_CAPACITY : Integer.parseInt(eventQueueLengthText);

		String writeQueueLengthText = System.getProperty(packageName + ".WRITE_QUEUE_CAPACITY");
		int writeQueueLength = writeQueueLengthText== null ? DEFAULT_WRITE_QUEUE_CAPACITY : Integer.parseInt(writeQueueLengthText);

		String heartbeatIntervalText = System.getProperty(packageName + ".HEARTBEAT_INTERVAL");
		long heartbeatInterval = heartbeatIntervalText== null ? DEFAULT_HEARTBEAT_INTERVAL : Long.parseLong(heartbeatIntervalText);
		
		return new DistributorConfig(address, port, eventQueueLength, writeQueueLength, heartbeatInterval);
    }
}
