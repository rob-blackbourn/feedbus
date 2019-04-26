package net.jetblack.feedbus.distributor.interactors;

/**
 * Represents an interactor closed event.
 */
public class InteractorClosedEventArgs extends InteractorEventArgs {

	/**
	 * Constructs the event.
	 * @param interactor The interactor.
	 */
    public InteractorClosedEventArgs(Interactor interactor) {
    	super(interactor);
    }
}
