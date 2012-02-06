package ovation.odata.util;

import junit.framework.TestCase;

public class StringUtilsTest extends TestCase {
	public static void testToString() throws Exception {
		assertEquals("null", StringUtils.toString(null));
		assertEquals("1", StringUtils.toString(Integer.valueOf(1)));
		assertEquals("[1,2,3]", StringUtils.toString(new int[]{1, 2, 3}));
		System.out.println(StringUtils.toString(new byte[]{0x12, 0x23, 0x34, 0x7f, (byte)0xb4}));
	}
}
