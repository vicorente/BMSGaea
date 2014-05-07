package battleSystemApp.dds;

import battleSystemApp.dds.idl.Msg;

public interface DDSListener {
	/**
	 * Llamado por DDSCommLayer cuando llega un mensaje
	 * @param message
	 */
	public void receivedMessage(Msg message);
}
