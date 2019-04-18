package net.jetblack.feedbus.adapters.config;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

import net.jetblack.util.io.StringSerializer;

public class ConnectionConfigTest {

	@Test
	public void testToString() {
		try {
			ConnectionConfig connectionConfig = new ConnectionConfig(
					InetAddress.getByName("localhost"), 
					1234, StringSerializer.class,
					8096, 0);
			String text = connectionConfig.toString();
			assertEquals("Address=localhost/127.0.0.1, Port=1234, ByteEncoderType=net.jetblack.util.io.StringSerializer, WriteQueueLength=8096, HeartbeatInterval=0", text);
		} catch (Exception e) {
			fail("Threw");
		}
	}

	@Test
	public void testCreateFromProperties() {
		try {
			ConnectionConfig connectionConfig = ConnectionConfig.createFromProperties();
			assertEquals(InetAddress.getByName("localhost"), connectionConfig.getAddress());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
