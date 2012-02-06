package ovation.odata.service;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.odata4j.repack.org.apache.commons.codec.binary.Base64;

import ovation.DataContext;
import ovation.OvationException;
import ovation.UserAuthenticationException;
import ovation.odata.util.DataContextCache;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;

/**
 * this class ensures the user is authenticated, authenticates them if not, and 
 * ensures that a DataContext is on the thread for the authenticated users that
 * matches their credentials.
 * @author Ron
 *
 */
public class JerseyAuthenticator extends Authenticator {
	public static final Logger _log = Logger.getLogger(JerseyAuthenticator.class);
	
	public static boolean setThreadContext(String user, String password) throws OvationException, UserAuthenticationException {
		DataContext ctx = null;
		String key = user;	// easiest implementation, tho probably should be bound to session FIXME
		if (password == null) {
			// user should already be authenticated - look up ctx by user name only
			ctx = DataContextCache.getDataContext(key);
		} else {
			ctx = DataContextCache.getDataContext(user, password, key);
		}
		
		_log.info("got context " + ctx + " for '" + user + "'");
		
		if (ctx != null) {
			DataContextCache.setThreadContext(ctx);
		}
		return ctx != null;
	}
	
	public static String[] parseBasicAuthHeader(String authHeader) throws IllegalArgumentException {
		if (authHeader.startsWith("Basic ")) {
			authHeader = authHeader.substring("Basic ".length());
		}
		byte[] decodedBytes = Base64.decodeBase64(authHeader);
		if (decodedBytes != null) {
			String decodedString = new String(decodedBytes);
			_log.info("decoded '" + decodedString + "'");
			int delim = decodedString.indexOf(':');
			if (delim != -1) {
				String userName = decodedString.substring(0, delim);
				String password = decodedString.substring(delim + 1);
				return new String[]{userName, password};
			} else {
				throw new IllegalArgumentException("failed to find delimiter in '" + decodedString + "'");
			}
		} else {
			throw new IllegalArgumentException("failed to decode '" + authHeader + "'");
		}
	}
			
	public Result authenticate(HttpExchange xchng) {
		try {
			HttpPrincipal principal = xchng.getPrincipal();
			Headers requestHeaders = xchng.getRequestHeaders();
			if (principal != null) {
				_log.info("got principal - " + principal);
				// authenticated user
				if (setThreadContext(principal.getName(), null)) {
					return new Authenticator.Success(principal);
				} else {
					_log.info("no context found for principal - auth timeout?");
				}
			} else {
				_log.info("no principal yet");
			}
			
			String authHeader = requestHeaders.getFirst(HttpHeaders.AUTHORIZATION);
			if (authHeader != null) {
				// auth attempt
				_log.debug("attempt to authenticate '" + authHeader + "'");
				try {
					String[] usernamePasswordArray = parseBasicAuthHeader(authHeader);
					String userName = usernamePasswordArray[0];
					String password = usernamePasswordArray[1];
					try {
						if (setThreadContext(userName, password)) {
							return new Authenticator.Success(new HttpPrincipal(userName, "ovodata"));
						}
					} catch (UserAuthenticationException uax) {
					} catch (OvationException ox) {
						// FIXME - special-case for change in auth failure in DataContext.authenticateUser()
						if ("Incorrect password".equals(ox.getMessage()) == false) {
							throw ox;
						}
					}
					_log.info("failed to authenticate user '" + userName + "'");
				} catch (IllegalArgumentException iax) {
					_log.error(iax.toString());
				}
			} else {
				_log.debug("no auth header provided");
			}
			xchng.getResponseHeaders().add(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"ovodata\"");
			return new Authenticator.Failure(Response.Status.UNAUTHORIZED.getStatusCode());
		} catch (Exception ex) {
			_log.error(ex, ex);
			xchng.getResponseHeaders().add("Warning", ex.toString());
			return new Authenticator.Failure(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}
	}
}
