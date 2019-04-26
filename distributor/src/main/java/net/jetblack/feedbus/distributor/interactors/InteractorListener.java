package net.jetblack.feedbus.distributor.interactors;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import net.jetblack.feedbus.util.concurrent.EventQueue;

public class InteractorListener implements Closeable {

    private final EventQueue<InteractorEventArgs> _eventQueue;
    private final ServerSocket _listener;
    private final int _writeQueueCapacity;

    public InteractorListener(InetAddress address, int port, EventQueue<InteractorEventArgs> eventQueue, int writeQueueCapacity) throws IOException {
        _eventQueue = eventQueue;
    	_listener = new ServerSocket(port, -1, address);
    	_writeQueueCapacity = writeQueueCapacity;
    }
    
    public Interactor accept() throws IOException {
        Socket socket = _listener.accept();
        return Interactor.create(socket, _eventQueue, _writeQueueCapacity);
    }

	@Override
	public void close() throws IOException {
		_listener.close();
	}
}
