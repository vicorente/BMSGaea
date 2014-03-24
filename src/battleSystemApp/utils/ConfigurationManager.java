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
public class ConfigurationManager {
	Properties properties = null;
	
	public final String CONFIG_FILE_NAME = "configuration.properties";
	public final String USERNAME = "username";
	public final String PASSWORD = "password";
	
	private ResourceManager rm;

	public ConfigurationManager() {
		this.properties = new Properties();
		this.rm = new ResourceManager();
		try {
			Logger.getLogger(ConfigurationManager.class.getName()).log(
					Level.INFO,
					"Fichero de configuraci�n: " + rm.getConfigurationPath()
							+ CONFIG_FILE_NAME);
			FileInputStream in = new FileInputStream(rm.getConfigurationPath()
					+ CONFIG_FILE_NAME);
			properties.load(in);
			in.close();
			Logger.getLogger(ConfigurationManager.class.getName()).log(
					Level.INFO,
					"Leyendo valores del fichero de configuraci�n...");
			for (String key : properties.stringPropertyNames()) {
				String value = properties.getProperty(key);
				Logger.getLogger(ConfigurationManager.class.getName()).log(
						Level.INFO, "Key:- " + key + " Value:- " + value);
			}
		} catch (IOException ex) {
			Logger.getLogger(ConfigurationManager.class.getName()).log(
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