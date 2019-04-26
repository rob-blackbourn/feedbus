package net.jetblack.feedbus.distributor.interactors;

import net.jetblack.feedbus.messages.Message;

/**
 * An interactor message event.
 */
public class InteractorMessageEventArgs extends InteractorEventArgs {
	
	private final Message _message;

	/**
	 * Constructs the event.
	 * @param interactor The interactor.
	 * @param message The message.
	 */
	public InteractorMessageEventArgs(Interactor interactor, Message message) {
		super(interactor);
		_message = message;
	}

	/**
	 * Gets the message.
	 * @return The message.
	 */
	public Message getMessage() {
		return _message;
	}
	
	@Override
	public String toString() {
		return super.toString() + ", Message=" + _message;
	}
}
