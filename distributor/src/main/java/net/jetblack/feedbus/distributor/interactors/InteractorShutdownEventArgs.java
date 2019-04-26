package net.jetblack.feedbus.distributor.interactors;

/**
 * A shutdown event for all interactors.
 */
public class InteractorShutdownEventArgs extends InteractorEventArgs {

	/**
	 * Construct the event.
	 */
    public InteractorShutdownEventArgs() {
    	super(null);
    }
}
