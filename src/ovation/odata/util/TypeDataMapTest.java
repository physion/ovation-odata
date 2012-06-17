package ovation.odata.util;

import org.junit.Assert;

import org.junit.Test;

import ovation.odata.util.TypeDataMap.TypeData;

public class TypeDataMapTest {

	@Test
	public void testUTILookup() {
		Assert.assertNull(TypeDataMap.getUTIData(null));
		Assert.assertNull(TypeDataMap.getUTIData(""));
		Assert.assertNull(TypeDataMap.getUTIData("something funny"));
		
		TypeData data = TypeDataMap.getUTIData("public.tiff");
		Assert.assertNotNull(data);
		Assert.assertEquals("public.tiff", data.getUTI());
		Assert.assertEquals("image/tiff", data.getMimeType());
		Assert.assertTrue(data.getExtensions().contains("tif"));
		Assert.assertTrue(data.getExtensions().contains("tiff"));
	}
	
	@Test
	public void testExtLookup() {
		Assert.assertNull(TypeDataMap.getExtData(null));
		Assert.assertNull(TypeDataMap.getExtData(""));
		Assert.assertNull(TypeDataMap.getExtData("ron"));
		
		TypeData data = TypeDataMap.getExtData("tiff");
		Assert.assertNotNull(data);
		Assert.assertEquals("public.tiff", data.getUTI());
		Assert.assertEquals("image/tiff", data.getMimeType());
		Assert.assertTrue(data.getExtensions().contains("tif"));
		Assert.assertTrue(data.getExtensions().contains("tiff"));
		
		data = TypeDataMap.getExtData("mp4");
		Assert.assertNotNull(data);
		Assert.assertEquals("public.mpeg-4", data.getUTI());
		Assert.assertEquals("audio/mp4", data.getMimeType());
		Assert.assertTrue(data.getExtensions().contains("mp4"));
		
	}
	
	@Test
	public void testMimeLookup() {
		Assert.assertNull(TypeDataMap.getMimeData(null));
		Assert.assertNull(TypeDataMap.getMimeData(""));
		Assert.assertNull(TypeDataMap.getMimeData("ron"));
		
		TypeData data = TypeDataMap.getMimeData("image/tiff");
		Assert.assertNotNull(data);
		Assert.assertEquals("public.tiff", data.getUTI());
		Assert.assertEquals("image/tiff", data.getMimeType());
		Assert.assertTrue(data.getExtensions().contains("tif"));
		Assert.assertTrue(data.getExtensions().contains("tiff"));
		
		data = TypeDataMap.getMimeData("video/avi");
		Assert.assertNotNull(data);
		Assert.assertEquals("public.avi", data.getUTI());
		Assert.assertEquals("video/avi", data.getMimeType());
		Assert.assertTrue(data.getExtensions().contains("avi"));
	}

}
