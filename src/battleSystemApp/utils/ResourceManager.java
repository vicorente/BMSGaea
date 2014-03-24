package battleSystemApp.utils;

import java.io.File;

public class ResourceManager {
	private final String FSP = System.getProperty("file.separator");

	/**
	 * * Obtiene el Path al directorio donde se encuentran los mapas, imágenes,
	 * * etc.. * * @return
	 */
	public String getResourcesPath() {
		String dataPath = null;
		String javaPath = System.getProperty("user.dir");
		if (javaPath != null) {
			if (!(javaPath.endsWith("/") || javaPath.endsWith("\\"))) {
				javaPath += FSP;
			}
			dataPath = javaPath + "resources" + FSP;
		}
		File dataFile = new File(dataPath);
		if (!dataFile.exists()) {
			dataPath = ".." + FSP + "resources" + FSP;
		}
		return dataPath;
	}

	public String getMapsPath() {
		return getDataPath() + "maps" + FSP;
	}

	public String getIconsPath() {
		return getResourcesPath() + "icons" + FSP;
	}

	public String getConfigurationPath() {
		String dataPath = null;
		String javaPath = System.getProperty("user.dir");
		if (javaPath != null) {
			if (!(javaPath.endsWith("/") || javaPath.endsWith("\\"))) {
				javaPath += FSP;
			}
			dataPath = javaPath + "configuration" + FSP;
		}
		File dataFile = new File(dataPath);
		if (!dataFile.exists()) {
			dataPath = ".." + FSP + "configuration" + FSP;
		}
		return dataPath;
	}

	public String getGPSPath() {
		return getDataPath() + "gps" + FSP;
	}

	public String getFileSeparator() {
		return FSP;
	}

	public String getDataPath() {
		String dataPath = null;
		String javaPath = System.getProperty("user.dir");
		if (javaPath != null) {
			if (!(javaPath.endsWith("/") || javaPath.endsWith("\\"))) {
				javaPath += FSP;
			}
			dataPath = javaPath + "data" + FSP;
		}
		File dataFile = new File(dataPath);
		if (!dataFile.exists()) {
			dataPath = ".." + FSP + "data" + FSP;
		}
		return dataPath;
	}
}
