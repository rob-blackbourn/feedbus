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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import org.apache.commons.math3.exception.NullArgumentException;

import net.jetblack.feedbus.adapters.config.NamedConnectionConfig;
import net.jetblack.feedbus.adapters.config.ConnectionConfig;
import net.jetblack.feedbus.adapters.config.FeedBusConfig;
import net.jetblack.feedbus.messages.ForwardedSubscriptionRequest;
import net.jetblack.feedbus.messages.Message;
import net.jetblack.feedbus.messages.MonitorRequest;
import net.jetblack.feedbus.messages.MulticastData;
import net.jetblack.feedbus.messages.NotificationRequest;
import net.jetblack.feedbus.messages.SubscriptionRequest;
import net.jetblack.feedbus.messages.UnicastData;
import net.jetblack.util.EventArgs;
import net.jetblack.util.EventHandler;
import net.jetblack.util.EventRegister;
import net.jetblack.util.concurrent.ConcurrentEventHandler;
import net.jetblack.util.io.ByteSerializable;

public class Client implements Closeable {
	
	private static final Logger logger = Logger.getLogger(Client.class.getName());

	private final Socket _socket;
    private final DataInputStream _inputStream;
    private final DataOutputStream _outputStream;
    private final ByteSerializable _byteEncoder;
    private final BlockingQueue<Message> _writeQueue;
    private final EventHandler<DataReceivedEventArgs> _dataReceived = new ConcurrentEventHandler<DataReceivedEventArgs>();
    private final EventHandler<DataErrorEventArgs> _dataError = new ConcurrentEventHandler<DataErrorEventArgs>();
    private final EventHandler<ForwardedSubscriptionEventArgs> _forwardedSubscription = new ConcurrentEventHandler<ForwardedSubscriptionEventArgs>();
    private final EventHandler<ConnectionChangedEventArgs> _connectionChanged = new ConcurrentEventHandler<ConnectionChangedEventArgs>();
    private final EventHandler<EventArgs> _heartbeat = new ConcurrentEventHandler<EventArgs>();


    public static Client createFromProperties() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, InterruptedException, ClassNotFoundException
    {
    	ConnectionConfig config = ConnectionConfig.createFromProperties();
    	Class<? extends ByteSerializable> klass = config.getByteSerializerType();
    	ByteSerializable byteSerializable = klass.getDeclaredConstructor().newInstance();
        return create(config.getAddress(), config.getPort(), byteSerializable, config.getWriteQueueCapacity());
    }

    public static Client create(String name, FeedBusConfig config, int writeQueueCapacity) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, InterruptedException
    {
        NamedConnectionConfig connectionConfig = config.Connections.get(name);
        ByteSerializable  byteSerializer = (ByteSerializable) connectionConfig.getByteSerializerType().getConstructor().newInstance();
        return create(connectionConfig.getAddress(), connectionConfig.getPort(), byteSerializer, writeQueueCapacity);
    }

    public static Client create(InetAddress address, int port, ByteSerializable byteSerializer, int writeQueueCapacity) throws IOException, InterruptedException {
        Socket socket = new Socket(address, port);

        Client client = new Client(socket, byteSerializer, writeQueueCapacity);
        
        client.start();

        client.addSubscription("__admin__", "heartbeat");

        return client;
    }

    public final EventRegister<DataReceivedEventArgs> DataReceived = _dataReceived;
    public final EventRegister<DataErrorEventArgs> DataError = _dataError;
    public final EventRegister<ForwardedSubscriptionEventArgs> ForwardedSubscription = _forwardedSubscription;
    public final EventRegister<ConnectionChangedEventArgs> ConnectionChanged = _connectionChanged;
    public final EventRegister<EventArgs> Heartbeat = _heartbeat;

    public Client(Socket socket, ByteSerializable byteSerializer, int writeQueueCapacity) throws IOException {
    	_socket = socket;
    	_inputStream = new DataInputStream(socket.getInputStream()); 
    	_outputStream = new DataOutputStream(socket.getOutputStream()); 
        _byteEncoder = byteSerializer;
        _writeQueue = new ArrayBlockingQueue<Message>(writeQueueCapacity);
    }

    private Thread _readThread, _writeThread;
    
    public void start()
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

                switch (message.Type) {
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
    	_connectionChanged.notify(new ConnectionChangedEventArgs(state, error));
    }

    public void addSubscription(String feed, String topic) throws InterruptedException {
        makeSubscriptionRequest(feed, topic, true);
    }

    public void removeSubscription(String feed, String topic) throws InterruptedException {
        makeSubscriptionRequest(feed, topic, false);
    }

    private void makeSubscriptionRequest(String feed, String topic, boolean isAdd) throws InterruptedException {
        if (feed == null)
            throw new NullArgumentException();
        if (topic == null)
            throw new NullArgumentException();

        _writeQueue.put(new SubscriptionRequest(feed, topic, isAdd));
    }

    public void addMonitor(String feed) throws InterruptedException {
        makeMonitorRequest(feed, true);
    }

    public void removeMonitor(String feed) throws InterruptedException {
        makeMonitorRequest(feed, false);
    }

    private void makeMonitorRequest(String feed, boolean isAdd) throws InterruptedException {
        if (feed == null)
            throw new NullArgumentException();

        _writeQueue.put(new MonitorRequest(feed, isAdd));
    }

    public void addNotification(String feed) throws InterruptedException {
        makeNotificationRequest(feed, true);
    }

    public void removeNotification(String feed) throws InterruptedException {
        makeNotificationRequest(feed, false);
    }

    private void makeNotificationRequest(String feed, boolean isAdd) throws InterruptedException {
        if (feed == null)
            throw new NullArgumentException();

        _writeQueue.put(new NotificationRequest(feed, isAdd));
    }

    public void send(String clientId, String feed, String topic, boolean isImage, Object data) {
        if (feed == null)
            throw new NullArgumentException();
        if (topic == null)
            throw new NullArgumentException();

        try {
            _writeQueue.put(new UnicastData(clientId, feed, topic, isImage, serialize(data)));
        }
        catch (Exception error) {
        	_dataError.notify(new DataErrorEventArgs(true, feed, topic, isImage, data, error));
        }
    }

    public void publish(String feed, String topic, boolean isImage, Object data) {
        if (feed == null)
            throw new NullArgumentException();
        if (topic == null)
            throw new NullArgumentException();

        try {
            _writeQueue.put(new MulticastData(feed, topic, isImage, serialize(data)));
        }
        catch (Exception error) {
        	_dataError.notify(new DataErrorEventArgs(true, feed, topic, isImage, data, error));
        }
    }

    private void raiseOnForwardedSubscriptionRequest(ForwardedSubscriptionRequest message) {
    	_forwardedSubscription.notify(new ForwardedSubscriptionEventArgs(message.ClientId, message.Feed, message.Topic, message.IsAdd));
    }

    private void raiseOnDataOrHeartbeat(MulticastData message) {
        if (message.Feed == "__admin__" && message.Topic == "heartbeat")
            raiseOnHeartbeat();
        else
            raiseOnData(message.Feed, message.Topic, message.Data, message.IsImage);
    }

    private void raiseOnHeartbeat() {
    	_heartbeat.notify(EventArgs.Empty);
    }

    private void raiseOnData(UnicastData message) {
        raiseOnData(message.Feed, message.Topic, message.Data, message.IsImage);
    }

    protected byte[] serialize(Object data) throws Exception {
        return data == null ? null : _byteEncoder.serialize(data);
    }

    protected Object deserialize(byte[] data) throws Exception {
        return _byteEncoder.deserialize(data);
    }

    private void raiseOnData(String feed, String topic, byte[] data, boolean isImage) {
        try {
        	_dataReceived.notify(new DataReceivedEventArgs(feed, topic, deserialize(data), isImage));
        }
        catch (Exception error) {
        	_dataError.notify(new DataErrorEventArgs(false, feed, topic, isImage, data, error));
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
