package net.jetblack.feedbus.messages;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.junit.Test;

public class MonitorRequestTest {

	@Test
	public void testEqualsObject() {
		MonitorRequest source = new MonitorRequest("FEED", true);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			source.write(new DataOutputStream(outputStream));
			Message message = Message.read(new DataInputStream(new ByteArrayInputStream(outputStream.toByteArray())));
			assertEquals(source, message);
		} catch (IOException e) {
			fail("Threw exception");
		}	
	}

}
