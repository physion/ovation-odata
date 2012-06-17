package ovation.odata.util;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * based on Response's UTIMap and extensionMap with MIME types from 
 * 	http://www.webmaster-toolkit.com/mime-types.shtml, 
 * 	http://www.iana.org/assignments/media-types/application/index.html,
 * 	http://en.wikipedia.org/wiki/Internet_media_type
 *  http://filesuffix.com/ for .mat, .fig, and .m types
 * 
 * @author Ron
 */
public class TypeDataMap {
	/**
	 * immutable data about a media type
	 * @author Ron
	 */
	public static class TypeData {
		private final String 		_uti;
		private final Set<String> 	_extensions;
		private final String 		_mimeType;
		private TypeData(String uti, String mimeType, String ... extensions) {
			_uti = uti;
			_mimeType = mimeType;
			_extensions = Collections.unmodifiableSet(Sets.newHashSet(extensions));
		}
		public Set<String> getExtensions() 	{ return _extensions; }
		public String getMimeType() 		{ return _mimeType; }
		public String getUTI() 				{ return _uti; }
	}

	private static final Map<String,TypeData> _utiDataMap  = Maps.newHashMap();
	private static final Map<String,TypeData> _mimeDataMap = Maps.newHashMap();
	private static final Map<String,TypeData> _extDataMap  = Maps.newHashMap();
	
	private static void _addType(TypeData data) {
		_utiDataMap.put(data.getUTI(), data);
		_mimeDataMap.put(data.getMimeType(), data);
		for (String ext : data.getExtensions()) {
			_extDataMap.put(ext, data);
		}
	}
	static {
		_addType(new TypeData("public.data", 				"application/octet-stream",	"bin"));
		_addType(new TypeData("public.tiff", 				"image/tiff", 				"tif", "tiff"));	// AKA image/x-tiff
		_addType(new TypeData("public.avi", 				"video/avi", 				"avi")); 			// AKA application/x-troff-msvideo, video/msvideo, video/x-msvideo
		_addType(new TypeData("public.mpeg-4", 				"audio/mp4", 				"mp4"));
		_addType(new TypeData("com.apple.quicktime-movie", 	"video/quicktime", 			"mov"));	 
		_addType(new TypeData("com.adobe.pdf", 				"application/pdf", 			"pdf"));
		_addType(new TypeData("org.gnu.gnu-tar-archive", 	"application/x-tar", 		"tar"));
		_addType(new TypeData("org.gnu.gnu-zip-archive", 	"application/x-gzip", 		"gz", ".gzip"));	// AKA application/x-compressed
		_addType(new TypeData("org.gnu.gnu-zip-tar-archive","application/gnutar", 		"tgz"));			// AKA application/x-compressed, application/x-gzip
		_addType(new TypeData("com.pkware.zip-archive", 	"application/zip", 			"zip"));			// AKA application/x-compressed, application/x-zip-compressed, multipart/x-zip		
		_addType(new TypeData("com.mathworks.workspace", 	"application/matlab-mat", 	"mat"));	
		_addType(new TypeData("com.mathworks.figure", 		"application/matlab-fig", 	"fig"));	
		_addType(new TypeData("com.mathworks.matlab-source","application/matlab-m", 	"m"));	
    }
	
	public static TypeData getUTIData(String uti) 	{ return _utiDataMap.get(uti); }
	public static TypeData getMimeData(String mime) { return _mimeDataMap.get(mime); }
	public static TypeData getExtData(String ext) 	{ return _extDataMap.get(ext); }
}
