package ovation.odata.service.tomcat;

import java.security.Principal;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.RealmBase;
import org.apache.log4j.Logger;

import ovation.odata.service.JerseyAuthenticator;

/**
 * NOTE - this class is here for safe-keeping - it is not used in the current code-base, but was 
 * considered as a possibility for more robust auth - the problem is it directly depends on the 
 * Tomcat-specific class RealmBase in order to plug in JAAS which makes it very not portable.
 * 
 * based on org.apache.catalina.realm.UserDatabaseRealm 
 * to use:
 * <Realm className="ovation.odata.service.tomcat.OvationDBRealm"/>
 * @author Ron
 */
public class OvationDBRealm extends RealmBase {
	public static final Logger _log = Logger.getLogger(OvationDBRealm.class);

    /** Descriptive information about this Realm implementation. */
    protected static final String info = "ovation.odata.service.tomcat.OvationDBRealm/1.0";
    /** Descriptive information about this Realm implementation. */
    protected static final String name = "OvationDBRealm";

    public 		String getInfo() { return info; }
    protected 	String getName() { return name; }
	protected 	String getPassword(String user) { 
		throw new UnsupportedOperationException("We can't get a user's password"); 
	}

	public Principal authenticate(String username, String credentials) {
		try {
			if (JerseyAuthenticator.setThreadContext(username, credentials)) {
				_log.trace("authenticated '" + username + "'");
		        return new GenericPrincipal(username, credentials);
		    }
		} catch (Exception ex) {
			_log.warn(ex, ex);
		}
		_log.info("failed to authenticate '" + username + "'");
        return null;
	}
	    
	public boolean hasRole(Wrapper wrapper, Principal principal, String role) {
		// OvationDB doesn't have roles so whatever role you're looking for they have it.
		return true;
	}
	
	protected Principal getPrincipal(String username) {
        return new GenericPrincipal(username, "no password");
	}
	
	protected void startInternal() throws LifecycleException {
		_log.info("webapp starting up");
		super.startInternal();
	}
	protected void stopInternal() throws LifecycleException {
		_log.info("webapp shutting down");
		super.stopInternal();
	}
}