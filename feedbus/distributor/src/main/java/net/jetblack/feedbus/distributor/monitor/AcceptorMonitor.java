package net.jetblack.feedbus.distributor.monitor;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

/**
 * Implements the acceptor monitor MXBean
 */
public class AcceptorMonitor implements AcceptorMonitorMXBean {

	private static final String NAME = "net.jetblack:type=AcceptorMonitor";
	
	private MBeanServer _mbeanServer;
	private ObjectName _objectName;
	
	private long _acceptedConnectionCount = 0;

	/**
	 * Register the MXBean.
	 * 
	 * @throws MalformedObjectNameException
	 * @throws InstanceAlreadyExistsException
	 * @throws MBeanRegistrationException
	 * @throws NotCompliantMBeanException
	 */
	public void register() throws MalformedObjectNameException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
		_objectName = new ObjectName(NAME);
		_mbeanServer.registerMBean(this, _objectName);
	}
	
	/**
	 * Unregister the MXBean.
	 */
	public void unregister() {
		if (_mbeanServer != null) {
			try {
				_mbeanServer.unregisterMBean(_objectName);
			} catch (MBeanRegistrationException | InstanceNotFoundException e) {
				// Nothing to do
			}
		}
	}

	@Override
	public long getAcceptedConnectionCount() {
		return _acceptedConnectionCount;
	}
	
	/**
	 * Sets the accepted connection count.
	 * 
	 * @param value The accepted connection count.
	 */
	public void setAcceptedConnectionCount(long value) {
		_acceptedConnectionCount = value;
	}
	
	/**
	 * Increment the accepted connection count.
	 */
	public void incrAcceptedConnectionCount() {
		++_acceptedConnectionCount;
	}
	
}
