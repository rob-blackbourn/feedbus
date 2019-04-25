package net.jetblack.feedbus.adapters.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

import net.jetblack.feedbus.adapters.Client;
import net.jetblack.util.io.ByteSerializable;
import net.jetblack.util.io.StringSerializer;

public class ConnectionConfig {
	
	public static final String DEFAULT_HOST = "localhost";
	public static final int DEFAULT_PORT = 30011;
	public static final Class<? extends ByteSerializable> DEFAULT_SERIALIZER = StringSerializer.class;
	public static final int DEFAULT_WRITE_QUEUE_CAPACITY = 8096;
	public static final long DEFAULT_HEARTBEAT_INTERVAL = 1000;

    private InetAddress _address;
    private int _port;
    private Class<? extends ByteSerializable> _byteSerializerType;
    private int _writeQueueCapacity;
    private long _heartbeatInterval;

	@SuppressWarnings("unchecked")
	public static ConnectionConfig createFromProperties() throws UnknownHostException, ClassNotFoundException {
		
		String packageName = Client.class.getPackage().getName();
		
		InetAddress address = InetAddress.getByName(System.getProperty(packageName + ".HOST", DEFAULT_HOST));
		
		String portText = System.getProperty(packageName + ".PORT");
		int port = portText== null ? DEFAULT_PORT : Integer.parseInt(portText);

		String writeQueueCapacityText = System.getProperty(packageName + ".WRITE_QUEUE_CAPACITY");
		int writeQueueCapacity = writeQueueCapacityText== null ? DEFAULT_WRITE_QUEUE_CAPACITY : Integer.parseInt(writeQueueCapacityText);
		
		String classText = System.getProperty(packageName + ".SERIALIZER");
		Class<?> cls = classText == null ? DEFAULT_SERIALIZER : Class.forName(classText);
		if (!ByteSerializable.class.isAssignableFrom(cls)) {
			throw new ClassCastException();
		}

		String heartbeatIntervalText = System.getProperty(packageName + ".HEARTBEAT_INTERVAL");
		long heartbeatInterval = heartbeatIntervalText== null ? DEFAULT_HEARTBEAT_INTERVAL : Long.parseLong(heartbeatIntervalText);

		return new ConnectionConfig(address, port, (Class<? extends ByteSerializable>)cls, writeQueueCapacity, heartbeatInterval);
	}
	
	public ConnectionConfig() {
	}
	
	public ConnectionConfig(InetAddress address, int port, Class<? extends ByteSerializable> byteSerializerType, int writeQueueCapacity, long heartbeatInterval) {
		_address = address;
		_port = port;
		_byteSerializerType = byteSerializerType;
		_writeQueueCapacity = writeQueueCapacity;
		_heartbeatInterval = heartbeatInterval;
	}

    public InetAddress getAddress() {
    	return _address;
    }
    
    public void setAddress(InetAddress  value)  {
    	_address = value;
    }
    
    public int getPort() {
    	return _port;
    }
    
    public void setPort(int value) {
    	_port = value;
    }

    public Class<? extends ByteSerializable> getByteSerializerType() {
    	return _byteSerializerType;
    }
    
    public void setByteSerializerType(Class<? extends ByteSerializable> value) {
    	_byteSerializerType = value;
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
    
    @Override
    public String toString() {
        return String.format(
        		"Address=%1$s, Port=%2$d, ByteEncoderType=%3$s, WriteQueueCapacity=%4$d, HeartbeatInterval=%5$d", 
        		_address, 
        		_port, 
        		_byteSerializerType.getName(),
        		_writeQueueCapacity,
        		_heartbeatInterval);
    }

}