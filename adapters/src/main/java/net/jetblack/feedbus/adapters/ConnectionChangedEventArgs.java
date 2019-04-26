package net.jetblack.feedbus.adapters;

import net.jetblack.feedbus.util.EventArgs;

/**
 * Represents the connection changed event.
 */
public class ConnectionChangedEventArgs extends EventArgs {

    private final ConnectionState _state;
    private final Exception _error;

    /**
     * Construct the event.
     * @param state The state.
     * @param error The error if any.
     */
    public ConnectionChangedEventArgs(ConnectionState state, Exception error) {
        _state = state;
        _error = error;
    }

    /**
     * Gets the state.
     * @return The state.
     */
    public ConnectionState getState() {
    	return _state;
    }
    
    /**
     * Gets the error, if any.
     * @return The error if any.
     */
    public Exception getError() {
    	return _error;
    }
    
    @Override
    public String toString() {
    	return "State=" + _state + ", Error=" + _error;
    }
}
