package battleSystemApp.components;

/** The ContextMenuInfo class specifies the contents of the context menu. */
public class ContextMenuInfo {
	private String menuTitle;
	private ContextMenuItemInfo[] menuItems;

	public ContextMenuInfo(String title, ContextMenuItemInfo[] menuItems) {
		this.setMenuTitle(title);
		this.setMenuItems(menuItems);
	}

	public String getMenuTitle() {
		return menuTitle;
	}

	public void setMenuTitle(String menuTitle) {
		this.menuTitle = menuTitle;
	}

	public ContextMenuItemInfo[] getMenuItems() {
		return menuItems;
	}

	public void setMenuItems(ContextMenuItemInfo[] menuItems) {
		this.menuItems = menuItems;
	}
}