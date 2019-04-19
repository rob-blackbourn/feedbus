package net.jetblack.feedbus.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MonitorRequest extends Message {

	public MonitorRequest(String feed, boolean isAdd) {
    	super(MessageType.MonitorRequest);
	    Feed = feed;
	    IsAdd = isAdd;
	}
	
	public final String Feed;
	public final boolean IsAdd;
	
	public static MonitorRequest readBody(DataInputStream stream) throws IOException {
	    String feed = stream.readUTF();
	    boolean isAdd = stream.readBoolean();
	    return new MonitorRequest(feed, isAdd);
	}
	
	@Override
	public DataOutputStream write(DataOutputStream stream) throws IOException {
	    super.write(stream);
	    stream.writeUTF(Feed);
	    stream.writeBoolean(IsAdd);
	    return stream;
	}

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((Feed == null) ? 0 : Feed.hashCode());
		result = prime * result + (IsAdd ? 1231 : 1237);
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
		MonitorRequest other = (MonitorRequest) obj;
		if (Feed == null) {
			if (other.Feed != null)
				return false;
		} else if (!Feed.equals(other.Feed))
			return false;
		if (IsAdd != other.IsAdd)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
	    return super.toString() + ", Feed=" + Feed + ", IsAdd=" + IsAdd;
	}
}
