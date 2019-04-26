package net.jetblack.feedbus.distributor.interactors;

/**
 * An interactor connected event.
 */
public class InteractorConnectedEventArgs extends InteractorEventArgs {

	/**
	 * Constructs the event.
	 * @param interactor The interactor.
	 */
	public InteractorConnectedEventArgs(Interactor interactor) {
		super(interactor);
	}

}
