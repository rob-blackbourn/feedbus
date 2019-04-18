package net.jetblack.feedbus.distributor;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import net.jetblack.feedbus.distributor.interactors.InteractorConnectedEventArgs;
import net.jetblack.feedbus.distributor.interactors.InteractorErrorEventArgs;
import net.jetblack.feedbus.distributor.interactors.InteractorEventArgs;
import net.jetblack.feedbus.distributor.interactors.InteractorManager;
import net.jetblack.feedbus.distributor.interactors.InteractorMessageEventArgs;
import net.jetblack.feedbus.distributor.notifiers.NotificationManager;
import net.jetblack.feedbus.distributor.subscriptions.SubscriptionManager;
import net.jetblack.feedbus.messages.MonitorRequest;
import net.jetblack.feedbus.messages.MulticastData;
import net.jetblack.feedbus.messages.NotificationRequest;
import net.jetblack.feedbus.messages.SubscriptionRequest;
import net.jetblack.feedbus.messages.UnicastData;
import net.jetblack.util.EventListener;
import net.jetblack.util.concurrent.EventQueue;

public class Server implements Closeable {

	private static final Logger logger = Logger.getLogger(Server.class.getName());

    private final EventQueue<InteractorEventArgs> _eventQueue;
    private final Acceptor _acceptor;

    private final InteractorManager _interactorManager;
    private final SubscriptionManager _subscriptionManager;
    private final NotificationManager _notificationManager;
    
    private Timer _heartbeatTimer;
    private Thread _eventQueueThread;
    private Thread _acceptThread;

    public Server(InetAddress address, int port, int eventQueueCapacity, int writeQueueCapacity) {
        _eventQueue = new EventQueue<InteractorEventArgs>(eventQueueCapacity);
        
        _eventQueue.Listener.add(new EventListener<InteractorEventArgs>() {
			@Override
			public void onEvent(InteractorEventArgs event) {
				onInteractorEvent(event);
			}
		});


        _acceptor = new Acceptor(address, port, _eventQueue, writeQueueCapacity);

        _interactorManager = new InteractorManager();

        _notificationManager = new NotificationManager(_interactorManager);

        _subscriptionManager = new SubscriptionManager(_interactorManager, _notificationManager);
    }

    public void start(long heartbeatInterval) {
        logger.info("Starting server");

        _eventQueueThread = _eventQueue.start();
        _acceptThread = _acceptor.start();

        if (heartbeatInterval == 0) {
        	logger.info("Heartbeat is disabled");
        } else  {
        	logger.info("Heartbeat every " + heartbeatInterval + "ms");
            _heartbeatTimer = new Timer("Heartbeat", true);
            _heartbeatTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					sendHeartbeat();
				}
			}, heartbeatInterval, heartbeatInterval);
        }

        logger.info("Server started");
    }

    private void onInteractorEvent(InteractorEventArgs args) {
        if (args instanceof InteractorConnectedEventArgs)
            onInteractorConnected((InteractorConnectedEventArgs)args);
        else if (args instanceof InteractorMessageEventArgs)
            onMessage((InteractorMessageEventArgs)args);
        else if (args instanceof InteractorErrorEventArgs)
            onInteractorError((InteractorErrorEventArgs)args);
    }

    private void onInteractorConnected(InteractorConnectedEventArgs event) {
        _interactorManager.addInteractor(event.Interactor);
        event.Interactor.start();
    }

    private void onInteractorError(InteractorErrorEventArgs event) {
        if (event.Error instanceof EOFException)
            _interactorManager.closeInteractor(event.Interactor);
        else
            _interactorManager.faultInteractor(event.Interactor, event.Error);
    }

    private void onMessage(InteractorMessageEventArgs event) {
        logger.fine(String.format("OnMessage(sender=%s, message=%s", event.Interactor, event.Message));

        switch (event.Message.getType()) {
            case MonitorRequest:
                _subscriptionManager.requestMonitor(event.Interactor, (MonitorRequest)event.Message);
                break;

            case SubscriptionRequest:
                _subscriptionManager.requestSubscription(event.Interactor, (SubscriptionRequest)event.Message);
                break;

            case MulticastData:
                _subscriptionManager.sendMulticastData(event.Interactor, (MulticastData)event.Message);
                break;

            case UnicastData:
                _subscriptionManager.sendUnicastData(event.Interactor, (UnicastData)event.Message);
                break;

            case NotificationRequest:
                _notificationManager.requestNotification(event.Interactor, (NotificationRequest)event.Message);
                break;

            default:
                logger.warning("Received unknown message type " + event.Message.getType() + " from interactor " + event.Interactor + ".");
                break;
        }
    }

    private void sendHeartbeat() {
        logger.fine("Sending heartbeat");
        try {
    		_eventQueue.enqueue(new InteractorMessageEventArgs(null, new MulticastData("__admin__", "heartbeat", true, null)));
		} catch (InterruptedException e) {
			// TODO: Should this be caught?
		}
    }

	@Override
	public void close() throws IOException {
        logger.info("Stopping server");

        if (_heartbeatTimer != null) {
        	_heartbeatTimer.cancel();
        }

        try {
			_interactorManager.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        try {
        	_acceptThread.interrupt();
			_acceptThread.join();
		} catch (InterruptedException e) {
			// Nothing to do.
		}
        
        try {
        	_eventQueueThread.interrupt();
			_eventQueueThread.join();
		} catch (InterruptedException e) {
			// Nothing to do.
		}

        logger.info("Server stopped");
	}
}
