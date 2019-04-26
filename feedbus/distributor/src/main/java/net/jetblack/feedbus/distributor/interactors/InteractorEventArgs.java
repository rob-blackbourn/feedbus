package net.jetblack.feedbus.distributor.interactors;

import net.jetblack.feedbus.util.EventArgs;

/**
 * The base class for interactor events.
 */
public class InteractorEventArgs extends EventArgs {

    private final Interactor _interactor;

    /**
     * Constructs the event.
     * @param interactor The interactor.
     */
    public InteractorEventArgs(Interactor interactor) {
        _interactor = interactor;
    }

    /**
     * Gets the interactor.
     * @return The interactor.
     */
    public Interactor getInteractor() {
    	return _interactor;
    }
    
    @Override
    public String toString() {
    	return "Interactor=" + _interactor;
    }
}
