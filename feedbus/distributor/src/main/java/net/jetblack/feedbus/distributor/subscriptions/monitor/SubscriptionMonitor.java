package net.jetblack.feedbus.distributor.subscriptions.monitor;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

/**
 * Implements the MXBean for monitoring subscriptions.
 */
public class SubscriptionMonitor implements SubscriptionMonitorMXBean {

	private static final String NAME = "net.jetblack:type=SubscriptionMonitor";
	
	private MBeanServer _mbeanServer;
	private ObjectName _objectName;

	private int _subscriptionCount = 0;
	private int _monitorCount = 0;
	
	@Override
	synchronized public int getSubscriptionCount() {
		return _subscriptionCount;
	}

	/**
	 * Sets the subscription count.
	 * 
	 * @param value The subscription count.
	 */
	synchronized public void setSubscriptionCount(int value) {
		_subscriptionCount = value; 
	}
	
	/**
	 * Increments the subscription count.
	 */
	synchronized public void incrSubscriptionCount() {
		++_subscriptionCount;
	}
	
	/**
	 * Decrements the subscription count.
	 */
	synchronized public void decrSubscriptionCount() {
		--_subscriptionCount;
	}

	
	@Override
	synchronized public int getMonitorCount() {
		return _monitorCount;
	}
	
	/**
	 * Sets the current monitor count.
	 * 
	 * @param value The current monitor count.
	 */
	synchronized public void setMonitorCount(int value) {
		_monitorCount = value; 
	}
	
	/**
	 * Increments the current monitor count.
	 */
	synchronized public void incrMonitorCount() {
		++_monitorCount;
	}
	
	/**
	 * Decrements the current monitor count.
	 */
	synchronized public void decrMonitorCount() {
		--_monitorCount;
	}

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
	
}
