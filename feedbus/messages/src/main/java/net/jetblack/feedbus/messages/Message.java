package net.jetblack.feedbus.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * The base class for all messages.
 */
public abstract class Message {

	private final MessageType _type;

	/**
	 * Constructs the base class.
	 * 
	 * @param type The type of the message.
	 */
	protected Message(MessageType type) {
		_type = type;
	}
	
	/**
	 * Reads the message from a stream.
	 * 
	 * @param stream The stream from which to read.
	 * @return The message that was read.
	 * @throws IOException Thrown if the message could not be read.
	 */
	public static Message read(DataInputStream stream) throws IOException {
		MessageType type = readHeader(stream);
		
		switch (type) {
		case MulticastData:
			return MulticastData.readBody(stream);
        case UnicastData:
            return UnicastData.readBody(stream);
        case ForwardedSubscriptionRequest:
            return ForwardedSubscriptionRequest.readBody(stream);
        case NotificationRequest:
            return NotificationRequest.readBody(stream);
        case SubscriptionRequest:
            return SubscriptionRequest.readBody(stream);
        case MonitorRequest:
            return MonitorRequest.readBody(stream);
        default:
            throw new IOException("unknown message type");			
		}
	}

	private static MessageType readHeader(DataInputStream stream) throws IOException {
		Byte b = stream.readByte();
		return MessageType.values()[b];
	}
	
	/**
	 * Write the message to a stream.
	 * 
	 * @param stream The stream to which the message should be written.
	 * @throws IOException Thrown if the message could not be written.
	 */
    public void write(DataOutputStream stream) throws IOException {
        stream.write((byte)_type.ordinal());
    }
    
    /**
     * The type of the message.
     * 
     * @return The message type.
     */
    public MessageType getType() {
    	return _type;
    }
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_type == null) ? 0 : _type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message other = (Message) obj;
		if (_type != other._type)
			return false;
		return true;
	}

    @Override
    public String toString() {
        return "MessageType=" + _type;
    }
}
