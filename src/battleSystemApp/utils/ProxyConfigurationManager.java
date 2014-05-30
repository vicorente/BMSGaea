package battleSystemApp.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase que proporciona acceso al fichero de configuraci�n
 * configuration.properties
 * 
 * @author Victor
 * 
 */
public class ProxyConfigurationManager {
	Properties properties = null;
	
	public final String CONFIG_FILE_NAME = "configuration.properties";
	public final String PROXY_USERNAME = "proxy_username";
	public final String PROXY_PASSWORD = "proxy_password";
	public final String PROXY_HOST = "proxy_host";
	public final String PROXY_PORT = "proxy_port";
	private ResourceManager rm;

	public ProxyConfigurationManager() {
		this.properties = new Properties();
		this.rm = new ResourceManager();
		try {
			Logger.getLogger(ProxyConfigurationManager.class.getName()).log(
					Level.INFO,
					"Fichero de configuración: " + rm.getConfigurationPath()
							+ CONFIG_FILE_NAME);
			FileInputStream in = new FileInputStream(rm.getConfigurationPath()
					+ CONFIG_FILE_NAME);
			properties.load(in);
			in.close();
			Logger.getLogger(ProxyConfigurationManager.class.getName()).log(
					Level.INFO,
					"Leyendo valores del fichero de configuración...");
			for (String key : properties.stringPropertyNames()) {
				String value = properties.getProperty(key);
				Logger.getLogger(ProxyConfigurationManager.class.getName()).log(
						Level.INFO, "Key:- " + key + " Value:- " + value);
			}
		} catch (IOException ex) {
			Logger.getLogger(ProxyConfigurationManager.class.getName()).log(
					Level.SEVERE, null, ex);
			ex.printStackTrace();
		}
	}

	/**
	 * Devuelve la propiedad de configuraci�n solicitada
	 * 
	 * @param key 
	 * @return
	 * 
	 */
	public String getProperty(String key) {
		return this.properties.getProperty(key);
	}
}