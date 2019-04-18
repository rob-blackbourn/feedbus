package net.jetblack.feedbus.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class MulticastData extends Message {

	public final String Feed;
	public final String Topic;
	public final boolean IsImage;
	public final byte[] Data;

    public MulticastData(String feed, String topic, boolean isImage, byte[] data) {
    	super(MessageType.MulticastData);
	    Feed = feed;
	    Topic = topic;
	    IsImage = isImage;
	    Data = data;
	}

	public static MulticastData readBody(DataInputStream stream) throws IOException {
	    String feed = stream.readUTF();
	    String topic = stream.readUTF();
	    boolean isImage = stream.readBoolean();
	    int len = stream.readInt();
	    byte[] data = new byte[len];
	    stream.readFully(data);
	    
	    return new MulticastData(feed, topic, isImage, data);
	}
	
	@Override
	public DataOutputStream write(DataOutputStream stream) throws IOException {
	    super.write(stream);
	    stream.writeUTF(Feed);
	    stream.writeUTF(Topic);
	    stream.writeBoolean(IsImage);
	    stream.writeInt(Data.length);
	    stream.write(Data);
	    return stream;
	}
	
	@Override
	public String toString() {
	    return super.toString() + ", Feed=" + Feed + ", Topic=" + Topic + ", IsImage=" + IsImage + ", Data=" + Data;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Arrays.hashCode(Data);
		result = prime * result + ((Feed == null) ? 0 : Feed.hashCode());
		result = prime * result + (IsImage ? 1231 : 1237);
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
		MulticastData other = (MulticastData) obj;
		if (!Arrays.equals(Data, other.Data))
			return false;
		if (Feed == null) {
			if (other.Feed != null)
				return false;
		} else if (!Feed.equals(other.Feed))
			return false;
		if (IsImage != other.IsImage)
			return false;
		if (Topic == null) {
			if (other.Topic != null)
				return false;
		} else if (!Topic.equals(other.Topic))
			return false;
		return true;
	}
}
