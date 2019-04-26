package net.jetblack.feedbus.distributor.notifiers;

import net.jetblack.feedbus.distributor.interactors.Interactor;
import net.jetblack.feedbus.distributor.interactors.InteractorEventArgs;

/**
 * A notification event.
 */
public class NotificationEventArgs extends InteractorEventArgs {

    private final String _feed;

    /**
     * Constructs the event.
     * @param interactor The interactor.
     * @param feed The feed name.
     */
    public NotificationEventArgs(Interactor interactor, String feed) {
        super(interactor);
        _feed = feed;
    }
    
    /**
     * Gets the feed name.
     * @return The feed name.
     */
    public String getFeed() {
    	return _feed;
    }

	@Override
	public String toString() {
	    return super.toString() + ", Feed=" + _feed;
	}
}
