package net.jetblack.util;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class StringsTest {

	@Test
	public void JoinStringArrayTest() {
		String[] strings = new String[] { "one", "two", "three" };
		String joined = Strings.join(",", strings);
		assertEquals("one,two,three", joined);
	}

	@Test
	public void JoinStringCollectionTest() {
		List<String> strings = new ArrayList<String>();
		strings.add("one");
		strings.add("two");
		strings.add("three");
		String joined = Strings.join(",", strings);
		assertEquals("one,two,three", joined);
	}

	@Test
	public void JoinStringEnumerableTest() {
		List<String> strings = new ArrayList<String>();
		strings.add("one");
		strings.add("two");
		strings.add("three");
		String joined = Strings.join(",", Enumerable.create(strings));
		assertEquals("one,two,three", joined);
	}

}
