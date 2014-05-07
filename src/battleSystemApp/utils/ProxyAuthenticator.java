package battleSystemApp.utils;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Permite la utilizaci�n de la aplicaci�n en presencia de un Proxy
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
		Logger.getLogger(ProxyAuthenticator.class.getName()).log(
				Level.INFO,
				"Creando autenticador para el proxy...");
	}

	protected PasswordAuthentication getPasswordAuthentication() {
		String requestingHost = getRequestingHost();
//		Logger.getLogger(ProxyAuthenticator.class.getName()).log(
//				Level.INFO,
//				"petición recibida del proxy -> "+requestingHost);
		return new PasswordAuthentication(username, password);

	}
}
