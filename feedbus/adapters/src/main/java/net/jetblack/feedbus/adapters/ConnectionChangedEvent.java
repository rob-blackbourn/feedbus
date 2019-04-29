package net.jetblack.feedbus.adapters;

/**
 * Represents the connection changed event.
 */
public class ConnectionChangedEvent {

    private final ConnectionState _state;
    private final Exception _error;

    /**
     * Construct the event.
     * @param state The state.
     * @param error The error if any.
     */
    public ConnectionChangedEvent(ConnectionState state, Exception error) {
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
