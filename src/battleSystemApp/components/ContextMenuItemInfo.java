package battleSystemApp.components;
/**
 * The ContextMenuItemInfo class specifies the contents of one entry in the
 * context menu.
 */
public class ContextMenuItemInfo {
	protected String displayString;

	public ContextMenuItemInfo(String displayString) {
		this.displayString = displayString;
	}
	
	public String displayString(){
		return displayString;
	}
}