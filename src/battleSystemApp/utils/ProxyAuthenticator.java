package battleSystemApp.utils;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
/**
 * Permite la utilización de la aplicación en presencia de un Proxy
 * @author vgonllo
 *
 */
public class ProxyAuthenticator extends Authenticator {

	private String username;
	private final char[] password;

	public ProxyAuthenticator(String username, String password) {
		super();
		this.username = new String(username);
		this.password = password.toCharArray();
	}

	protected PasswordAuthentication getPasswordAuthentication() {
		String requestingHost = getRequestingHost();
		System.out
				.println("getPasswordAuthentication() request recieved from->"
						+ requestingHost);
		return new PasswordAuthentication(username, password);

	}
}
