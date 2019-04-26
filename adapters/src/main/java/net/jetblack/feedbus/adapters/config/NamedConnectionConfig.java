package net.jetblack.feedbus.adapters.config;

import java.net.InetAddress;

import net.jetblack.feedbus.util.io.ByteSerializable;

public class NamedConnectionConfig extends ConnectionConfig {

    public NamedConnectionConfig() {
		super();
	}

	public NamedConnectionConfig(String name, InetAddress address, int port, Class<? extends ByteSerializable> byteSerializerType,
			int writeQueueLength, long heartbeatInterval) {
		super(address, port, byteSerializerType, writeQueueLength, heartbeatInterval);
		_name = name;
	}

	private String _name;
    
    public String getName() {
    	return _name;
    }
    
    public void setName(String value) {
    	_name = value;
    }

    @Override
    public String toString() {
        return String.format("Name=\"%1$\", %2$", _name,super.toString());
    }
}
