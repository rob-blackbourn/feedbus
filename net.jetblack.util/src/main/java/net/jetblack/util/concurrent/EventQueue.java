package net.jetblack.util.concurrent;

import java.util.logging.Logger;

import net.jetblack.util.EventHandler;
import net.jetblack.util.EventRegister;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;

public class EventQueue<T> implements Runnable {
	
	private static final Logger logger = Logger.getLogger(EventQueue.class.getName());

    private final BlockingQueue<T> _interactorEventQueue;
    private final EventHandler<T> _listener = new ConcurrentEventHandler<T>(); 
    public final EventRegister<T> Listener = _listener; 

    public EventQueue(int queueCapacity) {
        _interactorEventQueue = new ArrayBlockingQueue<T>(queueCapacity);
    }

    public void enqueue(T item) throws InterruptedException {
        _interactorEventQueue.put(item);
    }

    public Thread start() {
    	Thread thread = new Thread(this);
    	thread.start();
    	return thread;
    }
    
	@Override
	public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                T item = _interactorEventQueue.take();
                _listener.notify(item);
            }
            catch (InterruptedException error) {
            	logger.info("The event queue has been interrupted");
            	break;
            }
            catch (Exception error) {
                logger.severe("The event queue has faulted");
                break;
            }
        }

        logger.info("Exited the event loop");
	}
}
