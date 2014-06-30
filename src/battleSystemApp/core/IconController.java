/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package battleSystemApp.core;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import battleSystemApp.components.ContextMenuInfo;
import battleSystemApp.components.ContextMenuItemInfo;
import battleSystemApp.dds.idl.Msg;
import battleSystemApp.features.AbstractFeature;
import battleSystemApp.utils.Util;
import gov.nasa.worldwind.Disposable;
import gov.nasa.worldwind.Movable;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.event.*;
import gov.nasa.worldwind.geom.Intersection;
import gov.nasa.worldwind.geom.Line;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.Highlightable;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.PatternFactory;
import gov.nasa.worldwind.render.UserFacingIcon;
import gov.nasa.worldwind.symbology.milstd2525.MilStd2525TacticalSymbol;

/**
 * @author vgonllo
 * @version $Id: IconController.java 1171 2013-02-11 21:45:02Z dcollins $
 * Controlador de los iconos de pantalla
 */
@SuppressWarnings("serial")
public class IconController extends AbstractFeature implements SelectListener,
		Disposable {
	protected Highlightable lastPickedIcon = null;
	protected boolean dragging = false;
	private Vec4 dragRefObjectPoint;
	private Point dragRefCursorPoint;
	private double dragRefAltitude;

	public boolean isDragging() {
		return this.dragging;
	}

	public IconController(Registry registry) {
		super("Icon Controller", Constants.FEATURE_ICON_CONTROLLER, registry);
	}

	public void initialize(Controller controller) {
		super.initialize(controller);

		this.controller.getWWd().addSelectListener(this);
	}

	public void dispose() {
		this.controller.getWWd().removeSelectListener(this);
	}

	public void selected(SelectEvent event) {
		try {

			if (event == null) {
				Util.getLogger().severe("null event");
				throw new IllegalArgumentException("null event");
			} else if (event.getEventAction().equals(SelectEvent.ROLLOVER))
				highlight(event, event.getTopObject());

			else if (event.getEventAction().equals(SelectEvent.DRAG_END)) {
				DragSelectEvent dragEvent = (DragSelectEvent) event;
				Object topObject = dragEvent.getTopObject();
				if (topObject instanceof MilStd2525TacticalSymbol) {
					MilStd2525TacticalSymbol dragObject = (MilStd2525TacticalSymbol) topObject;

					// Compute dragged object ref-point in model coordinates.
					// Use the Icon and Annotation logic of elevation as offset
					// above
					// ground when below max elevation.
					Position refPos = dragObject.getReferencePosition();

					View view = controller.getWWd().getView();
					Globe globe = controller.getWWd().getModel().getGlobe();

					// Compute dragged object ref-point in model coordinates.
					// Use the Icon and Annotation logic of elevation as offset
					// above
					// ground when below max elevation.

					Vec4 refPoint = globe.computePointFromPosition(refPos);

					// viewController.sceneChanged();

					// Prepare DDS message to publish
					Msg message = new Msg(
							dragObject.getIdentifier(),
							dragObject.getPosition().getLatitude().getDegrees(),
							dragObject.getPosition().getLongitude()
									.getDegrees(), dragObject.getPosition()
									.getAltitude());

					// this.dds.publish(message);

					this.dragging = false;
					event.consume();
				}

			} else if (event.getEventAction().equals(SelectEvent.DRAG)) {
				DragSelectEvent dragEvent = (DragSelectEvent) event;
				Object topObject = dragEvent.getTopObject();
				if (topObject == null)
					return;

				if (!(topObject instanceof Movable))
					return;

				Movable dragObject = (Movable) topObject;
				View view = controller.getWWd().getView();
				Globe globe = controller.getWWd().getModel().getGlobe();

				// Compute dragged object ref-point in model coordinates.
				// Use the Icon and Annotation logic of elevation as offset
				// above
				// ground when below max elevation.
				Position refPos = dragObject.getReferencePosition();
				if (refPos == null)
					return;

				Vec4 refPoint = globe.computePointFromPosition(refPos);

				if (!this.isDragging()) // Dragging started
				{
					// Save initial reference points for object and cursor in
					// screen
					// coordinates
					// Note: y is inverted for the object point.
					this.dragRefObjectPoint = view.project(refPoint);
					// Save cursor position
					this.dragRefCursorPoint = dragEvent.getPreviousPickPoint();
					// Save start altitude
					this.dragRefAltitude = globe.computePositionFromPoint(
							refPoint).getElevation();
				}

				// Compute screen-coord delta since drag started.
				int dx = dragEvent.getPickPoint().x - this.dragRefCursorPoint.x;
				int dy = dragEvent.getPickPoint().y - this.dragRefCursorPoint.y;

				// Find intersection of screen coord (refObjectPoint + delta)
				// with
				// globe.
				double x = this.dragRefObjectPoint.x + dx;
				double y = event.getMouseEvent().getComponent().getSize().height
						- this.dragRefObjectPoint.y + dy - 1;
				Line ray = view.computeRayFromScreenPoint(x, y);
				Position pickPos = null;
				// Use intersection with sphere at reference altitude.
				Intersection inters[] = globe.intersect(ray,
						this.dragRefAltitude);
				if (inters != null)
					pickPos = globe.computePositionFromPoint(inters[0]
							.getIntersectionPoint());

				if (pickPos != null) {
					// Intersection with globe. Move reference point to the
					// intersection point,
					// but maintain current altitude.
					Position p = new Position(pickPos, dragObject
							.getReferencePosition().getElevation());
					dragObject.moveTo(p);
				}
				this.dragging = true;
				event.consume();
				// movemos la vista de la cámara si está activado
				controller.getTrackingView().sceneChanged();
			} else if (event.getEventAction().equals(SelectEvent.RIGHT_PRESS)) {
				showContextMenu(event);
				event.consume();
			} else if (event.getEventAction().equals(SelectEvent.LEFT_PRESS)) {
				controller.setStatusMessage("Seleccionado");
				event.consume();
			}
		} catch (Exception e) {
			// Wrap the handler in a try/catch to keep exceptions from bubbling
			// up
			Util.getLogger().warning(
					e.getMessage() != null ? e.getMessage() : e.toString());
		}
	}

	protected void highlight(SelectEvent event, Object o) {
		// Manage highlighting of icons.

		if (this.lastPickedIcon == o)
			return; // same thing selected

		// Turn off highlight if on.
		if (this.lastPickedIcon != null) {
			this.lastPickedIcon.setHighlighted(false);
			this.lastPickedIcon = null;
		}

		// Turn on highlight if object selected.
		if (o != null && o instanceof Highlightable) {
			this.lastPickedIcon = (Highlightable) o;
			this.lastPickedIcon.setHighlighted(true);
		}

	}

	/**
	 * Muestra el menu contextual al hacer click con el botón derecho en un
	 * objeto
	 * 
	 * @param event
	 */
	protected void showContextMenu(SelectEvent event) {
		if (!(event.getTopObject() instanceof MilStd2525TacticalSymbol))
			return;

		// See if the top picked object has context-menu info defined. Show the
		// menu if it does.

		Object o = event.getTopObject();
		if (o instanceof AVList) // Uses an AVList in order to be applicable to
									// all shapes.
		{

			if (!(event.getSource() instanceof Component))
				return;

			TacticalSymbolContextMenu menu = new TacticalSymbolContextMenu(
					(AVList) o, (Component) event.getSource());
			menu.show(event.getMouseEvent());
		}
	}

	 // Create a blurred pattern bitmap
    private BufferedImage createBitmap(String pattern, Color color)
    {
        // Create bitmap with pattern
        BufferedImage image = PatternFactory.createPattern(pattern, new Dimension(128, 128), 0.7f,
            color, new Color(color.getRed(), color.getGreen(), color.getBlue(), 0));
        // Blur a lot to get a fuzzy edge
        image = PatternFactory.blur(image, 13);
        image = PatternFactory.blur(image, 13);
        image = PatternFactory.blur(image, 13);
        image = PatternFactory.blur(image, 13);
        return image;
    }
    
    
    
	private class TacticalSymbolContextMenu {
		protected Component sourceComponent;
		protected JMenuItem menuTitleItem;
		protected ArrayList<JMenuItem> menuItems = new ArrayList<JMenuItem>();
		protected AVList topObject;
		protected ContextMenuInfo menuInfo;

		public TacticalSymbolContextMenu(AVList topObject,
				Component sourceComponent) {

			this.menuInfo = (ContextMenuInfo) topObject
					.getValue(Constants.CONTEXT_MENU_INFO);

			this.sourceComponent = sourceComponent;
			this.topObject = topObject;

			if (this.menuInfo != null) {
				this.makeMenuTitle();
				this.makeMenuItems();
			}
		}

		protected void makeMenuTitle() {
			this.menuTitleItem = new JMenuItem(menuInfo.getMenuTitle());
		}

		protected void makeMenuItems() {
			for (ContextMenuItemInfo itemInfo : menuInfo.getMenuItems()) {
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
		 * The ContextMenuItemAction responds to user selection of a context
		 * menu item.
		 */
		private class ContextMenuItemAction extends AbstractAction {
			/**
		 * 
		 */
			private static final long serialVersionUID = -1585214756119587446L;
			protected ContextMenuItemInfo itemInfo;
			private UserFacingIcon icon;
			
			public ContextMenuItemAction(ContextMenuItemInfo itemInfo) {
				super(itemInfo.displayString());

				this.itemInfo = itemInfo;
			}

			public void actionPerformed(ActionEvent event) {

				if (itemInfo.displayString().equals(
						Constants.CONTEXT_MENU_ACTION_FOLLOW)) {
					if (controller.getTrackingView().isTracked(
							(Movable) topObject)) {
						controller.getTrackingView().removeMovableFromTrack(
								(Movable) topObject);						
						((MilStd2525TacticalSymbol) topObject).getAttributes().setInteriorMaterial(null);
						controller.getTrackingView().sceneChanged();
					} else if (!controller.getTrackingView().isTracked(
							(Movable) topObject)) {
						controller.getTrackingView().addMovableToTrack(
								(Movable) topObject);		
												
						Color diffuse = new Color(20, 100, 3);
						Color specular = new Color(255, 255, 255, diffuse.getAlpha());
						Color ambient = new Color(100, 2, 100);
						Color emission = new Color(0, 0, 0, diffuse.getAlpha());												
						//Material material = ((MilStd2525TacticalSymbol) topObject).getAttributes().getInteriorMaterial();
						Material mat = new Material(specular, diffuse, ambient, emission, 80.0f);
						((MilStd2525TacticalSymbol) topObject).getAttributes().setInteriorMaterial(mat);						
						controller.getTrackingView().sceneChanged();
					}
				}
			}
			
			

		}
	}
}
