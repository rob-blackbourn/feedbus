package net.jetblack.feedbus.distributor.interactors;

import net.jetblack.feedbus.util.EventArgs;

public class InteractorEventArgs extends EventArgs {

    private final Interactor _interactor;

    public InteractorEventArgs(Interactor interactor) {
        _interactor = interactor;
    }

    public Interactor getInteractor() {
    	return _interactor;
    }
    
    @Override
    public String toString() {
    	return "Interactor=" + _interactor;
    }
}
