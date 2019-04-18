package net.jetblack.feedbus.messages;

import static org.junit.Assert.*;

import org.junit.Test;

public class FeedTopicTest {

	@Test
	public void testEquals() {
		FeedTopic a = new FeedTopic("a", "b");
		FeedTopic b = new FeedTopic("a", "b");
		assertEquals(a, b);
	}

	@Test
	public void testNotEquals() {
		FeedTopic a = new FeedTopic("a", "b");
		FeedTopic b = new FeedTopic("A", "b");
		assertNotEquals(a, b);
	}

}
