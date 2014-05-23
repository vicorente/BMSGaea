package battleSystemApp.core;

/**
 * Interfaz que se deberá implementar cuando se desee recibir actualizaciones de un símbolo AbstracTacticalSymbol
 * @author vgonllo
 *
 */
public interface SymbolListener {

	public void moved();
}
