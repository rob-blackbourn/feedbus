package net.jetblack.feedbus.distributor.interactors;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import net.jetblack.feedbus.messages.Message;
import net.jetblack.feedbus.util.concurrent.EventQueue;

/**
 * An interactor represents a connection between a client and the server.
 */
public class Interactor implements Comparable<Interactor>, Closeable {

	private static final Logger logger = Logger.getLogger(Interactor.class.getName());

	private final BlockingQueue<Message> _writeQueue;
	
	private final DataInputStream _inputStream;
	private final DataOutputStream _outputStream;
	private final EventQueue<InteractorEventArgs> _eventQueue;
	
	private Thread _readThread, _writeThread;
	
	private final String _id;
	private final InetAddress _address;

	/**
	 * Construct the interactor.
	 * 
	 * @param socket The socket for cummincation.
	 * @param eventQueue The event queue used to communicate with the server.
	 * @param writeQueueCapacity The capacity of the interactor write queue.
	 * @return A new interactor.
	 * @throws IOException
	 */
	public static Interactor create(Socket socket, EventQueue<InteractorEventArgs> eventQueue, int writeQueueCapacity) throws IOException {
		return new Interactor(
				new DataInputStream(socket.getInputStream()),
				new DataOutputStream(socket.getOutputStream()),
				socket.getInetAddress(),
				eventQueue, 
				writeQueueCapacity);
	}
	
	private Interactor(DataInputStream inputStream, DataOutputStream outputStream, InetAddress address, EventQueue<InteractorEventArgs> eventQueue, int writeQueueCapacity) {
		_inputStream = inputStream;
		_outputStream = outputStream;
		_address = address;
		_eventQueue = eventQueue;
		_id = UUID.randomUUID().toString();
		_writeQueue = new ArrayBlockingQueue<Message>(writeQueueCapacity);
	}

	/**
	 * Gets the unique client identifier.
	 * 
	 * @return A client identifier.
	 */
	public String getId() {
		return _id;
	}
	
	/**
	 * Gets the remote address of the client.
	 * 
	 * @return The clients address.
	 */
	public InetAddress getAddress() {
		return _address;
	}
    
	/**
	 * Start sending and receiving data.
	 */
	public void start() {
		_readThread = new Thread(new Runnable() {
			@Override
			public void run() {
				queueReceivedMessages();
			}
		}, "Interactor-" + _id + "-read");

		_writeThread = new Thread(new Runnable() {
			@Override
			public void run() {
				writeQueuedMessages();
			}
		}, "Interactor-" + _id + "-write");
		
		_readThread.start();
		_writeThread.start();
	}
	
	private void queueReceivedMessages() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                _eventQueue.enqueue(new InteractorMessageEventArgs(this, receiveMessage()));
            }
            catch (InterruptedException error) {
				_writeThread.interrupt();
                break;
            }
            catch (Exception error) {
                try {
					_eventQueue.enqueue(new InteractorErrorEventArgs(this, error));
					_writeThread.interrupt();
				} catch (InterruptedException e) {
					// Nothing to do
				}
                break;
            }
        }

        logger.fine("Exited read loop for " + this);
	}
	
	private void writeQueuedMessages() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Message message = _writeQueue.take();
                message.write(_outputStream);
            }
            catch (InterruptedException error) {
                break;
            }
            catch (Exception error) {
                try {
					_eventQueue.enqueue(new InteractorErrorEventArgs(this, error));
				} catch (InterruptedException e) {
					// Nothing to do
				}
                break;
            }
        }

        logger.fine("Exited read loop for " + this);
	}

	/**
	 * Send a message to the client.
	 * 
	 * @param message The message to send.
	 * @throws InterruptedException
	 */
    public void sendMessage(Message message) throws InterruptedException {
        _writeQueue.put(message);
    }

    /**
     * Receive a message from a client.
     * 
     * @return The message sent by the client.
     * @throws IOException
     */
    public Message receiveMessage() throws IOException {
        return Message.read(_inputStream);
    }
    
    @Override
    public boolean equals(Object obj) {
        return equals((Interactor)obj);
    }

    // TODO: Use a more standard implementation.
    public boolean equals(Interactor other) {
        return other != null && other._id == _id;
    }

    @Override
    public int hashCode() {
        return _id.hashCode();
    }

    @Override
    public String toString() {
        return _id + ":" + _address;
    }

	@Override
	public int compareTo(Interactor other) {
		return other == null ? 1 : _id.compareTo(other._id);
	}

	@Override
	public void close() {
		try {
			_writeThread.interrupt();
			_writeThread.join();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        try {
			_inputStream.close();
			_readThread.join();
		} catch (IOException e) {
			// Nothing to do
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        try {
			_outputStream.close();
		} catch (IOException e) {
			// Nothing to do
			e.printStackTrace();
		}
	}
}
