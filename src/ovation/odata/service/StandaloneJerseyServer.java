package ovation.odata.service;

import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.core4j.CoreUtils;
import org.odata4j.jersey.producer.resources.ODataApplication;
import org.odata4j.jersey.producer.resources.ODataProducerProvider;
import org.odata4j.jersey.producer.server.JerseyServer;
import org.odata4j.producer.ODataProducer;
import org.odata4j.producer.resources.RootApplication;
import org.odata4j.producer.server.ODataServer;

import ovation.odata.util.PropertyManager;
import ovation.odata.util.Props;

import com.sun.jersey.api.container.filter.LoggingFilter;
import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

public class StandaloneJerseyServer {
	static {
		// in case -Djava.util.logging.manager=ovation.odata.util.JULLog4JBridge isn't on cmd-line
		ovation.odata.util.JULLog4JBridge.register();	// send all java.util.logging to Log4J
	}
	
	public static final Logger _log = Logger.getLogger(StandaloneJerseyServer.class);
	
	
	/**
	 * fires up this server on it standard URL
	 * @param args
	 */
	public static void main(String[] args) {
		Properties props = PropertyManager.getProperties(StandaloneJerseyServer.class);
		
		String  serviceUrl 	= props.getProperty(Props.SERVER_URL, "http://localhost:8887/ovodata/" + OvationOData4JProducer.getServiceName() + ".svc/"); 
		boolean logRequest 	= Props.getProp(props, Props.LOG_REQUEST, Props.LOG_REQUEST_DEFAULT);
		boolean logResponse	= Props.getProp(props, Props.LOG_RESPONSE, Props.LOG_RESPONSE_DEFAULT);
		
		ODataServer server = null;
		ODataProducer producer = null;
		try {
	        // register the producer as the static instance, then launch the http server
			producer = new OvationOData4JProducer();
	        ODataProducerProvider.setInstance(producer);
			
	        // instead of altering odata4j we kinda extend it to get access to the inner private server object to set the authenticator
	        // (not the prettiest of code...)
			server = new JerseyServer(serviceUrl) {
				@SuppressWarnings("unchecked")
				List<HttpContext> getAllContexts(HttpServer server) {
					// copied from JerseyServer.start
					Object tmp = CoreUtils.getFieldValue(server, "server", Object.class);
					tmp = CoreUtils.getFieldValue(tmp, "contexts", Object.class);
					tmp = CoreUtils.getFieldValue(tmp, "list", Object.class);
					return (List<HttpContext>) tmp;
				}

				public ODataServer start() {
				    ODataServer odServer = super.start();
					HttpServer server = CoreUtils.getFieldValue(this, "server", HttpServer.class);
					Authenticator authenticator = new JerseyAuthenticator();
					for (HttpContext ctx : getAllContexts(server)) {
						ctx.setAuthenticator(authenticator);
					}
					return odServer;
				}				
			};
			
			server.setODataApplication(ODataApplication.class);
			server.setRootApplication(RootApplication.class);
	        
	        if (logRequest) {
	        	((JerseyServer)server).addJerseyRequestFilter(LoggingFilter.class); // log all requests
	        }
	        if (logResponse) {
	            ((JerseyServer)server).addJerseyResponseFilter(LoggingFilter.class);
	        }
	        
	        server.start();
    		_log.info("server started");
	        
	        System.out.println("Press CR to exit");
	        System.in.read();
		} catch (Exception ex) {
			_log.fatal(ex, ex);
        } finally {
        	if (server != null) {
        		server.stop();
        		_log.info("server stopped");
        	}
        	if (producer != null) {
        		producer.close();	// this isn't invoked by the current OData4J framework (by design)
        	}
        }
	}
}