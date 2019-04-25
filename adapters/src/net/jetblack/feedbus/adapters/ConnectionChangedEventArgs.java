package net.jetblack.feedbus.adapters;

import net.jetblack.util.EventArgs;

public class ConnectionChangedEventArgs extends EventArgs {

    private final ConnectionState _state;
    private final Exception _error;

    public ConnectionChangedEventArgs(ConnectionState state, Exception error) {
        _state = state;
        _error = error;
    }

    public ConnectionState getState() {
    	return _state;
    }
    
    public Exception getError() {
    	return _error;
    }
    
    @Override
    public String toString() {
    	return "State=" + _state + ", Error=" + _error;
    }
}
