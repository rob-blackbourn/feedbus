package net.jetblack.feedbus.distributor.interactors;

import net.jetblack.feedbus.messages.Message;

public class InteractorMessageEventArgs extends InteractorEventArgs {
	
	private final Message _message;

	public InteractorMessageEventArgs(Interactor interactor, Message message) {
		super(interactor);
		_message = message;
	}

	public Message getMessage() {
		return _message;
	}
}
