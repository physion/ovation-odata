package ovation.odata.service.servlet;

import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import ovation.odata.util.CollectionUtils;
import ovation.odata.util.PropertyManager;
import ovation.odata.util.Props;

/**
 * an instance of this class ensures proper setup and tear-down of the web-application's 
 * properties and other singletons needed to function.
 * @author Ron
 */
public class WebappListener implements ServletContextListener {
	public static final Logger _log = Logger.getLogger(WebappListener.class);
	
	public void contextInitialized(ServletContextEvent event) {
		_log.info("context started");
		ServletContext ctx = event.getServletContext();

		Properties rootProps = PropertyManager.getProperties(null);
		// add web.xml init-params to the global root properties
		for (String name : CollectionUtils.makeIterable(ctx.getInitParameterNames())) {
			rootProps.setProperty(name, ctx.getInitParameter(name));
		}

		// this environment variable must take precedence over all other settings (including web.xml <init-params>)
		// this code is duplicated in PropertyManager to maintain consistent prop loading with stand-alone servers
		// (which don't have a web.xml collection of over-rides).
		String dbFile = System.getenv(PropertyManager.OVODATA_CON_FILE);
		if (dbFile != null) {
			rootProps.setProperty(Props.DC_FILE_DEFAULT, dbFile);
		}
		if (_log.isDebugEnabled()) {
			_log.debug("root props : " + rootProps);
		}
	}
	public void contextDestroyed(ServletContextEvent event) {
		_log.info("context stopped");
	}
}
