package net.jetblack.feedbus.distributor.interactors;

/**
 * A faulted interactor event.
 */
public class InteractorFaultedEventArgs extends InteractorErrorEventArgs {

	/**
	 * Constructs the event.
	 * @param interactor The interactor.
	 * @param error The error.
	 */
	public InteractorFaultedEventArgs(Interactor interactor, Exception error) {
		super(interactor, error);
	}
}
