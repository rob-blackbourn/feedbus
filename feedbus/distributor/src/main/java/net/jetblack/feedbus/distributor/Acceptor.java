package net.jetblack.feedbus.distributor;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;

import net.jetblack.feedbus.distributor.interactors.Interactor;
import net.jetblack.feedbus.distributor.interactors.InteractorConnectedEventArgs;
import net.jetblack.feedbus.distributor.interactors.InteractorEventArgs;
import net.jetblack.feedbus.distributor.interactors.InteractorListener;
import net.jetblack.feedbus.util.concurrent.EventQueue;

/**
 * Accept incoming connections.
 */
public class Acceptor implements Runnable, Closeable {

	private static final Logger logger = Logger.getLogger(Acceptor.class.getName());

	private final EventQueue<InteractorEventArgs> _eventQueue;
	private final int _writeQueueCapacity;
	private final InetAddress _address;
	private final int _port;

	private InteractorListener _listener;

	/**
	 * Constructs the acceptor.
	 * 
	 * @param address The address to bind to.
	 * @param port The port to bind to.
	 * @param eventQueue A queue with which the service will communicate with the client.
	 * @param writeQueueCapacity The capacity of the interactor write queue.
	 */
	public Acceptor(
			InetAddress address, 
			int port, 
			EventQueue<InteractorEventArgs> eventQueue, 
			int writeQueueCapacity) {
		_eventQueue = eventQueue;
		_address = address;
		_port = port;
		_writeQueueCapacity = writeQueueCapacity;
	}

	/**
	 * Start accepting connections.
	 * 
	 * @return The listener thread.
	 * @throws NotCompliantMBeanException 
	 * @throws MBeanRegistrationException 
	 * @throws InstanceAlreadyExistsException 
	 * @throws MalformedObjectNameException 
	 */
	public Thread start() throws MalformedObjectNameException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
		Thread thread = new Thread(this);
		thread.start();
		return thread;
	}

	@Override
	public void run() {
		try {
			logger.log(Level.INFO, "Listening on " + _address + ":" + _port + ".");
			_listener = new InteractorListener(_address, _port, _eventQueue, _writeQueueCapacity);
		} catch (IOException error) {
			logger.log(Level.SEVERE, "Failed to create server socket", error);
			return;
		}

		while (!Thread.currentThread().isInterrupted()) {
			try {
				Interactor interactor = _listener.accept();
				_eventQueue.enqueue(new InteractorConnectedEventArgs(interactor));
			} catch (InterruptedException error) {
				logger.info("Thread interrupted - exiting");
				break;
			} catch (SocketException error) {
				logger.info("Socket error - exiting");
				break;
			} catch (IOException error) {
				logger.log(Level.SEVERE, "Failed to accept interactor", error);
				break;
			}
		}
	}
	
	
	@Override
	public String toString() {
		return String.format("Address=%1$s, Port=%2$d, WriteQueueCapacity=%3$d", _address, _port, _writeQueueCapacity);
	}

	@Override
	public void close() throws IOException {
		if (_listener != null) {
			_listener.close();
		}
	}
}
