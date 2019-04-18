package net.jetblack.feedbus.distributor.interactors;

public class InteractorErrorEventArgs extends InteractorEventArgs {
	
	private final Exception _error;

	public InteractorErrorEventArgs(Interactor interactor, Exception error) {
		super(interactor);
		_error = error;
	}
	
	public Exception getError() {
		return _error;
	}

}
