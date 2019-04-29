package net.jetblack.feedbus.adapters;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import net.jetblack.feedbus.adapters.config.ConnectionConfig;
import net.jetblack.feedbus.messages.ForwardedSubscriptionRequest;
import net.jetblack.feedbus.messages.Message;
import net.jetblack.feedbus.messages.MonitorRequest;
import net.jetblack.feedbus.messages.MulticastData;
import net.jetblack.feedbus.messages.NotificationRequest;
import net.jetblack.feedbus.messages.SubscriptionRequest;
import net.jetblack.feedbus.messages.UnicastData;
import net.jetblack.feedbus.util.io.ByteSerializable;

/**
 * A client for the feed bus.
 */
public class Client implements Closeable {
	
	private static final Logger logger = Logger.getLogger(Client.class.getName());

	private final Socket _socket;
    private final DataInputStream _inputStream;
    private final DataOutputStream _outputStream;
    private final ByteSerializable _byteEncoder;
    private final BlockingQueue<Message> _writeQueue;
    
    
    private final List<DataErrorListener> _dataErrorListeners = new ArrayList<DataErrorListener>();
    private final List<DataReceivedListener> _dataReceivedListeners = new ArrayList<DataReceivedListener>();
    private final List<ForwardedSubscriptionListener> _forwardedSubscriptionListeners = new ArrayList<ForwardedSubscriptionListener>();
    private final List<ConnectionChangedListener> _connectionChangedListener = new ArrayList<ConnectionChangedListener>();
    private final List<HeartbeatListener> _heartbeatListeners = new ArrayList<HeartbeatListener>();

    /**
     * A convenience method to create and start a client from system properties.
     * @return A feed bus client.
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IOException
     * @throws InterruptedException
     * @throws ClassNotFoundException
     */
    public static Client createFromProperties() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, InterruptedException, ClassNotFoundException
    {
    	ConnectionConfig config = ConnectionConfig.createFromProperties();
    	Class<? extends ByteSerializable> klass = config.getByteSerializerType();
    	ByteSerializable byteSerializable = klass.getDeclaredConstructor().newInstance();
        return create(config.getAddress(), config.getPort(), byteSerializable, config.getWriteQueueCapacity());
    }

    /**
     * A convenience method to create and start a client from a configuration.
     * @param config The configuration
     * @return A feed bus client.
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IOException
     * @throws InterruptedException
     * @throws ClassNotFoundException
     */
    public static Client create(ConnectionConfig config) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, InterruptedException, ClassNotFoundException
    {
    	Class<? extends ByteSerializable> klass = config.getByteSerializerType();
    	ByteSerializable byteSerializable = klass.getDeclaredConstructor().newInstance();
        return create(config.getAddress(), config.getPort(), byteSerializable, config.getWriteQueueCapacity());
    }

    /**
     * Create and start a client from the basic parameters.
     * 
     * @param address The address of the message broker.
     * @param port The port of the message broker.
     * @param byteSerializer The instance of a class used to serialize the data.
     * @param writeQueueCapacity The capacity of the write queue.
     * @return A feed bus client.
     * @throws IOException
     * @throws InterruptedException
     */
    public static Client create(InetAddress address, int port, ByteSerializable byteSerializer, int writeQueueCapacity) throws IOException, InterruptedException {
        Socket socket = new Socket(address, port);

        Client client = new Client(socket, byteSerializer, writeQueueCapacity);
        
        client.start();

        client.addSubscription("__admin__", "heartbeat");

        return client;
    }

    /**
     * Adds a listener to data received events.
     * @param listener The listener to add.
     */
    public void addDataReceivedListener(DataReceivedListener listener) {
    	synchronized (_dataReceivedListeners) {
			_dataReceivedListeners.add(listener);
		}
    }

    /**
     * Removes a listener to data received events.
     * @param listener The listener to remove.
     */
    public void removeDataReceivedListener(DataReceivedListener listener) {
    	synchronized (_dataReceivedListeners) {
			_dataReceivedListeners.remove(listener);
		}
    }
    

    /**
     * Adds a listener to data error events.
     * @param listener The listener to add.
     */
    public void addDataErrorListener(DataErrorListener listener) {
    	synchronized (_dataErrorListeners) {
			_dataErrorListeners.add(listener);
		}
    }
    
    /**
     * Removes a listener to data error events.
     * @param listener The listener to remove.
     */
    public void removeDataErrorListener(DataErrorListener listener) {
    	synchronized (_dataErrorListeners) {
			_dataErrorListeners.remove(listener);
		}
    }

    /**
     * Adds a listener to forwarded subscription events.
     * @param listener The listener to add
     */
    public void addForwardedSubscriptionListener(ForwardedSubscriptionListener listener) {
    	synchronized (_forwardedSubscriptionListeners) {
			_forwardedSubscriptionListeners.add(listener);
		}
    }
    
    /**
     * Removes a listener to forwarded subscription events.
     * @param listener The listener to remove
     */
    public void removeForwardedSubscriptionListener(ForwardedSubscriptionListener listener) {
    	synchronized (_forwardedSubscriptionListeners) {
			_forwardedSubscriptionListeners.remove(listener);
		}
    }
    

    /**
     * Adds a listener to connection changed events.
     * @param listener The listener to add.
     */
    public void addConnectionChangedListener(ConnectionChangedListener listener) {
    	synchronized (_connectionChangedListener) {
			_connectionChangedListener.add(listener);
		}
    }
    
    /**
     * Removes a listener to connection changed events.
     * @param listener The listener to remove.
     */
    public void removeConnectionChangedListener(ConnectionChangedListener listener) {
    	synchronized (_connectionChangedListener) {
			_connectionChangedListener.remove(listener);
		}
    }

    /**
     * Adds a listener to heartbeat events.
     * @param listener The listener to add.
     */
    public void addHeartbeatListener(HeartbeatListener listener) {
    	synchronized (_heartbeatListeners) {
			_heartbeatListeners.add(listener);
		}
    }

    /**
     * Removes a listener to heartbeat events.
     * @param listener The listener to remove.
     */
    public void removeHeartbeatListener(HeartbeatListener listener) {
    	synchronized (_heartbeatListeners) {
			_heartbeatListeners.remove(listener);
		}
    }

    private Client(Socket socket, ByteSerializable byteSerializer, int writeQueueCapacity) throws IOException {
    	_socket = socket;
    	_inputStream = new DataInputStream(socket.getInputStream()); 
    	_outputStream = new DataOutputStream(socket.getOutputStream()); 
        _byteEncoder = byteSerializer;
        _writeQueue = new ArrayBlockingQueue<Message>(writeQueueCapacity);
    }

    private Thread _readThread, _writeThread;
    
    private void start()
    {
    	_readThread = new Thread(new Runnable() {
			@Override
			public void run() {
				read();
			}
		}, "read");
    	
    	_writeThread = new Thread(new Runnable() {
			@Override
			public void run() {
				write();
			}
		}, "write");

    	_readThread.start();
    	_writeThread.start();
    }

    private void read() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Message message = Message.read(_inputStream);

                switch (message.getType()) {
                    case MulticastData:
                        raiseOnDataOrHeartbeat((MulticastData)message);
                        break;
                    case UnicastData:
                        raiseOnData((UnicastData)message);
                        break;
                    case ForwardedSubscriptionRequest:
                        raiseOnForwardedSubscriptionRequest((ForwardedSubscriptionRequest)message);
                        break;
                    default:
                        throw new Exception("invalid message type");
                }
            }
            catch (InterruptedException error) {
            	logger.info("Interrupted read thread");
                return;
            }
            catch (EOFException error) {
            	logger.info("EOF read thread");
                raiseConnectionStateChanged(ConnectionState.Closed, null);
                return;
            }
            catch (SocketException error) {
            	logger.info("Socket exception read thread");
                return;
            }
            catch (Exception error) {
            	logger.info("Error read thread");
                raiseConnectionStateChanged(ConnectionState.Faulted, error);
                return;
            }
        }
    }

    private void write() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Message message = _writeQueue.take();
                message.write(_outputStream);
            }
            catch (InterruptedException error) {
            	logger.info("Interrupted wriite thread");
                return;
            }
            catch (EOFException error) {
            	logger.info("EOF write thread");
                raiseConnectionStateChanged(ConnectionState.Closed, null);
                return;
            }
            catch (Exception error) {
            	logger.info("Error write thread");
                raiseConnectionStateChanged(ConnectionState.Faulted, error);
                return;
            }
        }
    }

    private void raiseConnectionStateChanged(ConnectionState state, Exception error) {
    	raiseConnectionChangedEvent(state, error);
    }
    
    private void raiseConnectionChangedEvent(ConnectionState state, Exception error) {
    	List<ConnectionChangedListener> listeners = new ArrayList<ConnectionChangedListener>();
    	synchronized (_connectionChangedListener) {
			if (_connectionChangedListener.isEmpty()) {
				return;
			}
			for (ConnectionChangedListener listener : _connectionChangedListener) {
				listeners.add(listener);
			}
		}
    	ConnectionChangedEvent event = new ConnectionChangedEvent(state, error);
    	for (ConnectionChangedListener listener : listeners) {
    		listener.onConnectionChanged(event);
    	}
    }

    /**
     * Add a subscription to a feed and topic.
     * 
     * @param feed The name of the feed.
     * @param topic The name of the topic.
     * @throws InterruptedException
     */
    public void addSubscription(String feed, String topic) throws InterruptedException {
        makeSubscriptionRequest(feed, topic, true);
    }

    /**
     * Remove a subscription from a feed and topic.
     * 
     * @param feed The name of the feed.
     * @param topic The name of the topic.
     * @throws InterruptedException
     */
    public void removeSubscription(String feed, String topic) throws InterruptedException {
        makeSubscriptionRequest(feed, topic, false);
    }

    private void makeSubscriptionRequest(String feed, String topic, boolean isAdd) throws InterruptedException {
        if (feed == null)
            throw new IllegalArgumentException("feed");
        if (topic == null)
            throw new IllegalArgumentException("topic");

        _writeQueue.put(new SubscriptionRequest(feed, topic, isAdd));
    }

    /**
     * Start to monitor a feed.
     * 
     * @param feed The name of the feed.
     * @throws InterruptedException
     */
    public void addMonitor(String feed) throws InterruptedException {
        makeMonitorRequest(feed, true);
    }

    /**
     * Stop monitoring a feed.
     * 
     * @param feed The name of the feed.
     * @throws InterruptedException
     */
    public void removeMonitor(String feed) throws InterruptedException {
        makeMonitorRequest(feed, false);
    }

    private void makeMonitorRequest(String feed, boolean isAdd) throws InterruptedException {
        if (feed == null)
            throw new IllegalArgumentException("feed");

        _writeQueue.put(new MonitorRequest(feed, isAdd));
    }

    /**
     * Request notification of subscriptions on a feed.
     * 
     * @param feed The name of the feed.
     * @throws InterruptedException
     */
    public void addNotification(String feed) throws InterruptedException {
        makeNotificationRequest(feed, true);
    }

    /**
     * Stop receiving notifications of subscriptions to a feed.
     * 
     * @param feed The name of the feed.
     * @throws InterruptedException
     */
    public void removeNotification(String feed) throws InterruptedException {
        makeNotificationRequest(feed, false);
    }

    private void makeNotificationRequest(String feed, boolean isAdd) throws InterruptedException {
        if (feed == null)
            throw new IllegalArgumentException("feed");

        _writeQueue.put(new NotificationRequest(feed, isAdd));
    }

    /**
     * Send data to a specific client for a given feed and topic.
     * 
     * @param clientId The identify of the client.
     * @param feed The name of the feed.
     * @param topic The name of the topic.
     * @param isImage If true the data represents an image, otherwise it is a delta.
     * @param data The data transmitted.
     */
    public void send(String clientId, String feed, String topic, boolean isImage, Object data) {
        if (feed == null)
            throw new IllegalArgumentException("feed");
        if (topic == null)
            throw new IllegalArgumentException("topic");

        try {
            _writeQueue.put(new UnicastData(clientId, feed, topic, isImage, serialize(data)));
        }
        catch (Exception error) {
        	raiseDataErrorEvent(true, feed, topic, isImage, data, error);
        }
    }

    /**
     * Publish data to all subscribers of a feed and topic.
     * 
     * @param feed The name of the feed.
     * @param topic The name of the topic.
     * @param isImage If true the data represents an image, otherwise it is a delta.
     * @param data The data transmitted.
     */
    public void publish(String feed, String topic, boolean isImage, Object data) {
        if (feed == null)
            throw new IllegalArgumentException("feed");
        if (topic == null)
            throw new IllegalArgumentException("topic");

        try {
            _writeQueue.put(new MulticastData(feed, topic, isImage, serialize(data)));
        }
        catch (Exception error) {
        	raiseDataErrorEvent(true, feed, topic, isImage, data, error);
        }
    }
    
    private void raiseDataErrorEvent(boolean isSending, String feed, String topic, boolean isImage, Object data, Exception error) {
    	List<DataErrorListener> listeners = new ArrayList<DataErrorListener>();
    	synchronized (_dataErrorListeners) {
        	if (_dataErrorListeners.isEmpty()) {
        		return;
        	}
			for (DataErrorListener listener : _dataErrorListeners) {
				listeners.add(listener);
			}
		}
    	DataErrorEvent event = new DataErrorEvent(true, feed, topic, isImage, data, error);
    	for (DataErrorListener listener : listeners) {
    		listener.onDataErrorEvent(event);
    	}
    }

    private void raiseOnForwardedSubscriptionRequest(ForwardedSubscriptionRequest message) {
    	raiseForwardedSubscriptionEvent(message.getClientId(), message.getFeed(), message.getTopic(), message.isAdd());
    }
    
    private void raiseForwardedSubscriptionEvent(String clientId, String feed, String topic, boolean isAdd) {
    	List<ForwardedSubscriptionListener> listeners = new ArrayList<ForwardedSubscriptionListener>();
    	synchronized (_forwardedSubscriptionListeners) {
			if (_forwardedSubscriptionListeners.isEmpty()) {
				return;
			}
			for (ForwardedSubscriptionListener listener : _forwardedSubscriptionListeners) {
				listeners.add(listener);
			}
		}
    	ForwardedSubscriptionEvent event = new ForwardedSubscriptionEvent(clientId, feed, topic, isAdd);
    	for (ForwardedSubscriptionListener listener : listeners) {
    		listener.onForwardedSubscription(event);
    	}
    }

    private void raiseOnDataOrHeartbeat(MulticastData message) {
        if ("__admin__".equals(message.getFeed())  && "heartbeat".contentEquals(message.getTopic())) {
            raiseHeartbeatEvent();
        } else {
            raiseOnData(message.getFeed(), message.getTopic(), message.getData(), message.isImage());
        }
    }

    private void raiseHeartbeatEvent() {
    	List<HeartbeatListener> listeners = new ArrayList<HeartbeatListener>();
    	synchronized (_heartbeatListeners) {
			if (_heartbeatListeners.isEmpty()) {
				return;
			}
			for (HeartbeatListener listener : _heartbeatListeners) {
				listeners.add(listener);
			}
		}
    	for (HeartbeatListener listener : listeners) {
    		listener.onHeartbeat();
    	}
    }

    private void raiseOnData(UnicastData message) {
        raiseOnData(message.getFeed(), message.getTopic(), message.getData(), message.isImage());
    }

    protected byte[] serialize(Object data) throws Exception {
        return data == null ? null : _byteEncoder.serialize(data);
    }

    protected Object deserialize(byte[] data) throws Exception {
        return _byteEncoder.deserialize(data);
    }

    private void raiseOnData(String feed, String topic, byte[] data, boolean isImage) {
        try {
        	raiseDataReceivedEvent(feed, topic, deserialize(data), isImage);
        }
        catch (Exception error) {
        	raiseDataErrorEvent(false, feed, topic, isImage, data, error);
        }
    }
    
    private void raiseDataReceivedEvent(String feed, String topic, Object data, boolean isImage) {
    	List<DataReceivedListener> listeners = new ArrayList<DataReceivedListener>();
    	synchronized (_dataReceivedListeners) {
    		if (_dataErrorListeners.isEmpty()) {
    			return;
    		}
			for (DataReceivedListener listener : _dataReceivedListeners) {
				listeners.add(listener);
			}
		}
    	DataReceivedEvent event = new DataReceivedEvent(feed, topic, data, isImage);
    	for (DataReceivedListener listener : listeners) {
    		listener.onDataReceived(event);
    	}
    }

	@Override
	public void close() throws IOException {
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

        try {
			_socket.close();
		} catch (IOException e) {
			// Nothing to do
			e.printStackTrace();
		}
	}
}
