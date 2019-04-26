package net.jetblack.feedbus.distributor.interactors;

/**
 * An interactor error event.
 */
public class InteractorErrorEventArgs extends InteractorEventArgs {
	
	private final Exception _error;

	/**
	 * Constructs the event.
	 * @param interactor The interactor.
	 * @param error The error.
	 */
	public InteractorErrorEventArgs(Interactor interactor, Exception error) {
		super(interactor);
		_error = error;
	}
	
	/**
	 * Gets the error
	 * @return The error.
	 */
	public Exception getError() {
		return _error;
	}

	@Override
	public String toString() {
		return super.toString() + ", Error=" + _error.getMessage();
	}
}
