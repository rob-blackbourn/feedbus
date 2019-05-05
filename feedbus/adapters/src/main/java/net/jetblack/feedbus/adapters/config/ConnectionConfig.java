package net.jetblack.feedbus.adapters.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

import net.jetblack.feedbus.adapters.ByteSerializable;
import net.jetblack.feedbus.adapters.StringSerializer;
import net.jetblack.feedbus.adapters.Client;

/**
 * Configuration for the client.
 */
public class ConnectionConfig {

	/**
	 * The default server host name.
	 */
	public static final String DEFAULT_HOST = "localhost";
	/**
	 * The default server port.
	 */
	public static final int DEFAULT_PORT = 9775;
	/**
	 * The default serializer.
	 */
	public static final Class<? extends ByteSerializable> DEFAULT_SERIALIZER = StringSerializer.class;
	/**
	 * The default write queue capacity for the client.
	 */
	public static final int DEFAULT_WRITE_QUEUE_CAPACITY = 8096;
	/**
	 * The default server heartbeat interval.
	 */
	public static final long DEFAULT_HEARTBEAT_INTERVAL = 1000;

    private InetAddress _address;
    private int _port;
    private Class<? extends ByteSerializable> _byteSerializerType;
    private int _writeQueueCapacity;
    private long _heartbeatInterval;

    /**
     * Create the configuration from properties.
     * @return The configuration.
     * @throws UnknownHostException
     * @throws ClassNotFoundException
     */
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
	
	/**
	 * Construct with defaults.
	 */
	public ConnectionConfig() {
	}
	
	/**
	 * Construct the configuration.
	 * @param address The server address.
	 * @param port The server port.
	 * @param byteSerializerType The byte serializer type.
	 * @param writeQueueCapacity The write queue capacity.
	 * @param heartbeatInterval The server heart beat interval.
	 */
	public ConnectionConfig(InetAddress address, int port, Class<? extends ByteSerializable> byteSerializerType, int writeQueueCapacity, long heartbeatInterval) {
		_address = address;
		_port = port;
		_byteSerializerType = byteSerializerType;
		_writeQueueCapacity = writeQueueCapacity;
		_heartbeatInterval = heartbeatInterval;
	}

	/**
	 * Gets the server address.
	 * @return The server address.
	 */
    public InetAddress getAddress() {
    	return _address;
    }
    
    /**
     * Sets the server address.
     * @param value The server address.
     */
    public void setAddress(InetAddress  value)  {
    	_address = value;
    }
    
    /**
     * Gets the server port.
     * @return The server port.
     */
    public int getPort() {
    	return _port;
    }
    
    /**
     * Sets the server port.
     * @param value The server port.
     */
    public void setPort(int value) {
    	_port = value;
    }

    /**
     * Gets the byte serializer type.
     * @return The byte serializer type.
     */
    public Class<? extends ByteSerializable> getByteSerializerType() {
    	return _byteSerializerType;
    }
    
    /**
     * Sets the byte serializer type.
     * @param value The byte serializer type.
     */
    public void setByteSerializerType(Class<? extends ByteSerializable> value) {
    	_byteSerializerType = value;
    }
    
    /**
     * Gets the write queue capacity.
     * @return The write queue capacity.
     */
    public int getWriteQueueCapacity() {
    	return _writeQueueCapacity;
    }
    
    /**
     * Sets the write queue capacity.
     * @param value The write queue capacity.
     */
    public void setWriteQueueCapacity(int value) {
    	_writeQueueCapacity = value;
    }

    /**
     * Gets the heart beat interval.
     * @return The heart beat interval.
     */
    public long getHeartbeatInterval() {
    	return _heartbeatInterval;
    }
    
    /**
     * Sets the heart beat interval.
     * @param value
     */
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
