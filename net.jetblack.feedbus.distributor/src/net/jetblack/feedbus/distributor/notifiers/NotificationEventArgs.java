package net.jetblack.feedbus.distributor.notifiers;

import net.jetblack.feedbus.distributor.interactors.Interactor;
import net.jetblack.feedbus.distributor.interactors.InteractorEventArgs;

public class NotificationEventArgs extends InteractorEventArgs {

    private final String _feed;

    public NotificationEventArgs(Interactor interactor, String feed) {
        super(interactor);
        _feed = feed;
    }
    public String getFeed() {
    	return _feed;
    }

	@Override
	public String toString() {
	    return super.toString() + ", Feed=" + _feed;
	}
}
