package net.jetblack.feedbus.distributor.interactors;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import net.jetblack.feedbus.util.concurrent.EventQueue;

/**
 * An interactor listener.
 */
public class InteractorListener implements Closeable {

    private final EventQueue<InteractorEventArgs> _eventQueue;
    private final ServerSocket _listener;
    private final int _writeQueueCapacity;

    /**
     * Constructs the listener.
     * @param address The address on which to listen.
     * @param port The port on which to listen.
     * @param eventQueue The event queue to pass to the interactor.
     * @param writeQueueCapacity The capacity of the interactor write queue.
     * @throws IOException
     */
    public InteractorListener(InetAddress address, int port, EventQueue<InteractorEventArgs> eventQueue, int writeQueueCapacity) throws IOException {
        _eventQueue = eventQueue;
    	_listener = new ServerSocket(port, -1, address);
    	_writeQueueCapacity = writeQueueCapacity;
    }
    
    /**
     * Accept a new interactor connection.
     * @return The new interactor.
     * @throws IOException
     */
    public Interactor accept() throws IOException {
        Socket socket = _listener.accept();
        return Interactor.create(socket, _eventQueue, _writeQueueCapacity);
    }

	@Override
	public void close() throws IOException {
		_listener.close();
	}
}
