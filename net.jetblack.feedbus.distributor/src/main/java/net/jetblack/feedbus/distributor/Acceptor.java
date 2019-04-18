package net.jetblack.feedbus.distributor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jetblack.feedbus.distributor.interactors.Interactor;
import net.jetblack.feedbus.distributor.interactors.InteractorConnectedEventArgs;
import net.jetblack.feedbus.distributor.interactors.InteractorEventArgs;
import net.jetblack.feedbus.distributor.interactors.InteractorListener;
import net.jetblack.util.concurrent.EventQueue;

public class Acceptor implements Runnable {

	private static final Logger logger = Logger.getLogger(Acceptor.class.getName());

	private final EventQueue<InteractorEventArgs> _eventQueue;
	private final int _writeQueueCapacity;
	private final InetAddress _address;
	private final int _port;

	public Acceptor(InetAddress address, int port, EventQueue<InteractorEventArgs> eventQueue, int writeQueueCapacity) {
		_eventQueue = eventQueue;
		_address = address;
		_port = port;
		_writeQueueCapacity = writeQueueCapacity;
	}

	public Thread start() {
		Thread thread = new Thread(this);
		thread.start();
		return thread;
	}

	@Override
	public void run() {
		InteractorListener listener;
		try {
			listener = new InteractorListener(_address, _port, _eventQueue, _writeQueueCapacity);
		} catch (IOException error) {
			logger.log(Level.SEVERE, "Failed to create server socket", error);
			return;
		}

		while (!Thread.currentThread().isInterrupted()) {
			try {
				Interactor interactor = listener.accept();
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
}
