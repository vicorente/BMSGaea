package battleSystemApp.components;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/** The ContextMenu class implements the context menu. */
public  class TacticalSymbolContextMenu {
	public static final String CONTEXT_MENU_INFO = "ContextMenuInfo";

	protected ContextMenuInfo ctxMenuInfo;
	protected Component sourceComponent;
	protected JMenuItem menuTitleItem;
	protected ArrayList<JMenuItem> menuItems = new ArrayList<JMenuItem>();

	public TacticalSymbolContextMenu(Component sourceComponent,
			ContextMenuInfo contextMenuInfo) {
		this.sourceComponent = sourceComponent;
		this.ctxMenuInfo = contextMenuInfo;

		this.makeMenuTitle();
		this.makeMenuItems();
	}

	protected void makeMenuTitle() {
		this.menuTitleItem = new JMenuItem(this.ctxMenuInfo.getMenuTitle());
	}

	protected void makeMenuItems() {
		for (ContextMenuItemInfo itemInfo : this.ctxMenuInfo.getMenuItems()) {
			this.menuItems.add(new JMenuItem(new ContextMenuItemAction(
					itemInfo)));
		}
	}

	public void show(final MouseEvent event) {
		JPopupMenu popup = new JPopupMenu();

		popup.add(this.menuTitleItem);

		popup.addSeparator();

		for (JMenuItem subMenu : this.menuItems) {
			popup.add(subMenu);
		}

		popup.show(sourceComponent, event.getX(), event.getY());
	}
	
	/**
	 * The ContextMenuItemAction responds to user selection of a context menu
	 * item.
	 */
	public static class ContextMenuItemAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1585214756119587446L;
		protected ContextMenuItemInfo itemInfo;

		public ContextMenuItemAction(ContextMenuItemInfo itemInfo) {
			super(itemInfo.displayString());

			this.itemInfo = itemInfo;
		}

		public void actionPerformed(ActionEvent event) {
			System.out.println(this.itemInfo.displayString()); // Replace with
																// application's
																// menu-item
																// response.
		}
	}
}