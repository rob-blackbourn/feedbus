package net.jetblack.feedbus.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SubscriptionRequest extends Message {

	public final String Feed;
	public final String Topic;
	public final boolean IsAdd;

    public SubscriptionRequest(String feed, String topic, boolean isAdd) {
        super(MessageType.SubscriptionRequest);
	    Feed = feed;
	    Topic = topic;
	    IsAdd = isAdd;
	}
	
	public static SubscriptionRequest readBody(DataInputStream stream) throws IOException {
	    String feed = stream.readUTF();
	    String topic = stream.readUTF();
	    boolean isAdd = stream.readBoolean();
	    return new SubscriptionRequest(feed, topic, isAdd);
	}
	
	@Override
	public DataOutputStream write(DataOutputStream stream) throws IOException {
	    super.write(stream);
	    stream.writeUTF(Feed);
	    stream.writeUTF(Topic);
	    stream.writeBoolean(IsAdd);
	    return stream;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((Feed == null) ? 0 : Feed.hashCode());
		result = prime * result + (IsAdd ? 1231 : 1237);
		result = prime * result + ((Topic == null) ? 0 : Topic.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SubscriptionRequest other = (SubscriptionRequest) obj;
		if (Feed == null) {
			if (other.Feed != null)
				return false;
		} else if (!Feed.equals(other.Feed))
			return false;
		if (IsAdd != other.IsAdd)
			return false;
		if (Topic == null) {
			if (other.Topic != null)
				return false;
		} else if (!Topic.equals(other.Topic))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
	    return super.toString() + ", Feed=" + Feed + ", Topic=" + Topic + ", IsAdd=" + IsAdd;
	}
}
