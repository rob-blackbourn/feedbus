package net.jetblack.feedbus.distributor.monitor;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

public class ServerMonitor implements ServerMonitorMXBean {

	private static final String NAME = "net.jetblack:type=ServerMonitor";
	
	private MBeanServer _mbeanServer;
	private ObjectName _objectName;

	private long _messageCount = 0;

	@Override
	synchronized public long getMessageCount() {
		return _messageCount;
	}
	
	synchronized public void setMessageCount(long value) {
		_messageCount = value;
	}
	
	synchronized public void incrMessageCount() {
		++_messageCount;
	}
	
	public void register() throws MalformedObjectNameException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
		_objectName = new ObjectName(NAME);
		_mbeanServer.registerMBean(this, _objectName);
	}
	
	public void unregister() {
		if (_mbeanServer != null) {
			try {
				_mbeanServer.unregisterMBean(_objectName);
			} catch (MBeanRegistrationException | InstanceNotFoundException e) {
				// Nothing to do
			}
		}
	}
	
}
