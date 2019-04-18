package net.jetblack.feedbus.distributor.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

import net.jetblack.feedbus.distributor.Program;

public class DistributorConfig {
	
	public static final String DEFAULT_HOST = "0.0.0.0";
	public static final int DEFAULT_PORT = 30011;
	public static final int DEFAULT_EVENT_QUEUE_LENGTH = 8096;
	public static final int DEFAULT_WRITE_QUEUE_LENGTH = 8096;
	public static final long DEFAULT_HEARTBEAT_INTERVAL = 1000;

    private InetAddress _address;
    private int _port;
    private int _eventQueueLength;
    private int _writeQueueLength;
    private long _heartbeatInterval;
    
    public DistributorConfig() {
    }
    
    public DistributorConfig(InetAddress address, int port, int eventQueueLength, int writeQueueLength, long heartbeatInterval) {
    	_address = address;
    	_port = port;
    	_eventQueueLength = eventQueueLength;
    	_writeQueueLength = writeQueueLength;
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
    
    public int getEventQueueLength() {
    	return _eventQueueLength;
    }
    
    public void setEventQueueLength(int value) {
    	_eventQueueLength = value;
    }
    
    public int getWriteQueueLength() {
    	return _writeQueueLength;
    }
    
    public void setWriteQueueLength(int value) {
    	_writeQueueLength = value;
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

		String eventQueueLengthText = System.getProperty(packageName + ".EVENT_QUEUE_LENGTH");
		int eventQueueLength = eventQueueLengthText== null ? DEFAULT_EVENT_QUEUE_LENGTH : Integer.parseInt(eventQueueLengthText);

		String writeQueueLengthText = System.getProperty(packageName + ".WRITE_QUEUE_LENGTH");
		int writeQueueLength = writeQueueLengthText== null ? DEFAULT_WRITE_QUEUE_LENGTH : Integer.parseInt(writeQueueLengthText);

		String heartbeatIntervalText = System.getProperty(packageName + ".HEARTBEAT_INTERVAL");
		long heartbeatInterval = heartbeatIntervalText== null ? DEFAULT_HEARTBEAT_INTERVAL : Long.parseLong(heartbeatIntervalText);
		
		return new DistributorConfig(address, port, eventQueueLength, writeQueueLength, heartbeatInterval);
    }
}
