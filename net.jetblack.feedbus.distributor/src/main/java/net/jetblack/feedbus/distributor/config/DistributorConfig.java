package net.jetblack.feedbus.distributor.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

import net.jetblack.feedbus.distributor.Program;

public class DistributorConfig {
	
	public static final String DEFAULT_HOST = "0.0.0.0";
	public static final int DEFAULT_PORT = 30011;
	public static final int DEFAULT_EVENT_QUEUE_CAPACITY = 8096;
	public static final int DEFAULT_WRITE_QUEUE_CAPACITY = 8096;
	public static final long DEFAULT_HEARTBEAT_INTERVAL = 1000;

    private InetAddress _address;
    private int _port;
    private int _eventQueueCapacity;
    private int _writeQueueCapacity;
    private long _heartbeatInterval;
    
    public DistributorConfig() {
    }
    
    public DistributorConfig(InetAddress address, int port, int eventQueueLength, int writeQueueLength, long heartbeatInterval) {
    	_address = address;
    	_port = port;
    	_eventQueueCapacity = eventQueueLength;
    	_writeQueueCapacity = writeQueueLength;
    	_heartbeatInterval = heartbeatInterval;
    }
    
    public InetAddress getAddress() {
    	return _address;
    }
    
    public void setAddress(InetAddress value) {
    	_address = value;
    }
    
    public int getPort() {
    	return _port;
    }
    
    public void setPort(int value) {
    	_port = value;
    }
    
    public int getEventQueueCapacity() {
    	return _eventQueueCapacity;
    }
    
    public void setEventQueueCapacity(int value) {
    	_eventQueueCapacity = value;
    }
    
    public int getWriteQueueCapacity() {
    	return _writeQueueCapacity;
    }
    
    public void setWriteQueueCapacity(int value) {
    	_writeQueueCapacity = value;
    }
    
    public long getHeartbeatInterval() {
    	return _heartbeatInterval;
    }
    
    public void setHeartbeatInterval(long value) {
    	_heartbeatInterval = value;
    }

    public static DistributorConfig createFromProperties() throws UnknownHostException {
		String packageName = Program.class.getPackage().getName();
		
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
