package ovation.odata.util;

import java.util.Properties;

public abstract class Props {
	public static final String DC_MAX_CACHE_SIZE	= "ovodata.DataContext.cacheMaxSize";
	public static final String DC_FILE_BASE 		= "ovodata.DataContext.file.";
	public static final String DC_FILE_DEFAULT 		= "ovodata.DataContext.file";
	public static final String DC_EXPIRY_MS			= "ovodata.DataContext.cacheExpiryMs";
	public static final long   DC_EXPIRY_MS_DEFAULT = 0;	// disable caching by default
	public static final String SERVER_NAME			= "ovodata.OData4JServer.name";
	public static final String SERVER_NAME_DEFAULT  = "Ovodata"; 
	public static final String SERVER_URL           = "ovodata.OData4JServer.url";
	public static final String SERVER_MAX_RESULTS	= "ovodata.OData4JServer.maxResults";
	public static final int		SERVER_MAX_RESULTS_DEFAULT = 100;
	public static final String LOG_REQUEST			= "ovodata.OData4JServer.logRequest";
	public static final boolean LOG_REQUEST_DEFAULT = false;
	public static final String LOG_RESPONSE 		= "ovodata.OData4JServer.logResponse";
	public static final boolean LOG_RESPONSE_DEFAULT= false;

	public static final int getProp(Properties props, String prop, int def) throws NumberFormatException {
		String val = props.getProperty(prop);
		return val != null ? Integer.parseInt(val) : def;
	}
	public static final long getProp(Properties props, String prop, long def) throws NumberFormatException {
		String val = props.getProperty(prop);
		return val != null ? Long.parseLong(val) : def;
	}
	public static final boolean getProp(Properties props, String prop, boolean def) {
		String val = props.getProperty(prop);
		return val != null ? Boolean.parseBoolean(val) : def;
	}
	
}
