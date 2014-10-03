/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package battleSystemApp.utils;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.event.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.globes.EllipsoidalGlobe;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.pick.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.SymbologyConstants;
import gov.nasa.worldwind.symbology.TacticalSymbol;
import gov.nasa.worldwind.symbology.milstd2525.MilStd2525TacticalSymbol;
import gov.nasa.worldwind.util.Logging;

import java.awt.*;
import java.awt.event.*;

import battleSystemApp.components.ContextMenuInfo;
import battleSystemApp.core.Constants;
import battleSystemApp.core.Controller;

/**
 * Provides an interactive region selector. To use, construct and call
 * enable/disable. Register a property listener to receive changes to the sector
 * as they occur, or just wait until the user is done and then query the result
 * via {@link #getSector()}.
 *
 * @author tag
 * @version $Id: SectorSelector.java 1171 2013-02-11 21:45:02Z dcollins $
 */
public class UnitCreator extends WWObjectImpl implements SelectListener,
		MouseListener {
	public final static String SECTOR_PROPERTY = "gov.nasa.worldwind.SectorSelector";

	protected static final int NONE = 0;

	protected static final int MOVING = 1;
	protected static final int SIZING = 2;

	protected static final int NORTH = 1;
	protected static final int SOUTH = 2;
	protected static final int EAST = 4;
	protected static final int WEST = 8;
	protected static final int NORTHWEST = NORTH + WEST;
	protected static final int NORTHEAST = NORTH + EAST;
	protected static final int SOUTHWEST = SOUTH + WEST;
	protected static final int SOUTHEAST = SOUTH + EAST;

	private final WorldWindow wwd;
	private double edgeFactor = 0.10;

	// state tracking fields
	private boolean armed = false;
	private int operation = NONE;
	private int side = NONE;
	private Position previousPosition = null;
	private Sector previousSector = null;
	private Controller controller = null;

	public UnitCreator(Controller controller) {
		if (controller == null || controller.getWWd() == null) {
			String msg = Logging.getMessage("nullValue.WorldWindow");
			Logging.logger().log(java.util.logging.Level.SEVERE, msg);
			throw new IllegalArgumentException(msg);
		}

		this.controller = controller;
		this.wwd = controller.getWWd();

		// Update the layer panel to display the symbol layer.
	}

	public WorldWindow getWwd() {
		return wwd;
	}

	public void enable() {

		if (!controller.getMilSymbolFeatureLayer().getLayer().isEnabled())
			controller.getMilSymbolFeatureLayer().getLayer().setEnabled(true);

		this.setArmed(true);

		this.getWwd().addSelectListener(this);
		this.getWwd().getInputHandler().addMouseListener(this);

		this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
	}

	public void disable() {
		this.getWwd().removeSelectListener(this);
		this.getWwd().getInputHandler().removeMouseListener(this);
		this.setArmed(false);
	}

	public boolean isArmed() {
		return armed;
	}

	protected void setArmed(boolean armed) {
		this.armed = armed;
	}

	protected int getOperation() {
		return operation;
	}

	protected void setOperation(int operation) {
		this.operation = operation;
	}

	protected int getSide() {
		return side;
	}

	protected void setSide(int side) {
		this.side = side;
	}

	protected Position getPreviousPosition() {
		return previousPosition;
	}

	protected void setPreviousPosition(Position previousPosition) {
		this.previousPosition = previousPosition;
	}

	protected double getEdgeFactor() {
		return edgeFactor;
	}

	protected void setEdgeFactor(double edgeFactor) {
		this.edgeFactor = edgeFactor;
	}

	public void mousePressed(MouseEvent mouseEvent) {
		if (MouseEvent.BUTTON1_DOWN_MASK != mouseEvent.getModifiersEx())
			return;

		if (!this.isArmed())
			return;
		
		Position posicion = this.getWwd().getCurrentPosition();
		if (posicion != null) {
			this.setPreviousPosition(this.getWwd().getCurrentPosition());
			// Add symbol in the specified position
			TacticalSymbol groundSymbol = new MilStd2525TacticalSymbol(
					"SHGXUCFRMS----G", posicion);
			groundSymbol.setValue(AVKey.DISPLAY_NAME,
					"MIL-STD-2525 Hostile Self-Propelled Rocket Launchers");
			groundSymbol.setAttributes(controller.getMilSymbolFeatureLayer()
					.getGroundAttrs());
			groundSymbol.setHighlightAttributes(controller
					.getMilSymbolFeatureLayer().getSharedHighlightAttrs());
			groundSymbol.setModifier(SymbologyConstants.DIRECTION_OF_MOVEMENT,
					Angle.fromDegrees(90));
			groundSymbol
					.setModifier(SymbologyConstants.SPEED_LEADER_SCALE, 0.5);
			groundSymbol.setShowLocation(false);
			groundSymbol.setValue(Constants.CONTEXT_MENU_INFO,
					new ContextMenuInfo("Acciones", controller.getMilSymbolFeatureLayer().getItemActions()));
			controller.getMilSymbolFeatureLayer().getLayer()
					.addRenderable(groundSymbol);

		}

		mouseEvent.consume();
	
	}

	public void mouseReleased(MouseEvent mouseEvent) {

	}

	public void mouseDragged(MouseEvent mouseEvent) {
		if (MouseEvent.BUTTON1_DOWN_MASK != mouseEvent.getModifiersEx())
			return;

		mouseEvent.consume(); // prevent view operations
	}

	public void mouseClicked(MouseEvent mouseEvent) {
		
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	private static double abs(double a) {
		return a >= 0 ? a : -a;
	}

	protected void setCursor(int sideName) {
		Cursor cursor = null;

		switch (sideName) {
		case NONE:
			cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
			break;
		case NORTH:
			cursor = Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
			break;
		case SOUTH:
			cursor = Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
			break;
		case EAST:
			cursor = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
			break;
		case WEST:
			cursor = Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
			break;
		case NORTHWEST:
			cursor = Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
			break;
		case NORTHEAST:
			cursor = Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
			break;
		case SOUTHWEST:
			cursor = Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
			break;
		case SOUTHEAST:
			cursor = Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
			break;
		}

		this.setCursor(cursor);
	}

	public void setCursor(Cursor cursor) {
		((Component) this.getWwd()).setCursor(cursor != null ? cursor : Cursor
				.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
	}

	public Cursor getCursor() {
		return ((Component) this.getWwd()).getCursor();
	}
	
	protected static class RegionShape extends SurfaceSector {
		private boolean resizeable = false;
		private Position startPosition;
		private Position endPosition;
		private SurfaceSector borderShape;

		protected RegionShape(Sector sector) {
			super(sector);

			// Create the default border shape.
			this.setBorder(new SurfaceSector(sector));

			// The edges of the region shape should be constant lines of
			// latitude and longitude.
			this.setPathType(AVKey.LINEAR);
			this.getBorder().setPathType(AVKey.LINEAR);

			// Setup default interior rendering attributes. Note that the
			// interior rendering attributes are
			// configured so only the SurfaceSector's interior is rendered.
			ShapeAttributes interiorAttrs = new BasicShapeAttributes();
			interiorAttrs.setDrawOutline(false);
			interiorAttrs.setInteriorMaterial(new Material(Color.WHITE));
			interiorAttrs.setInteriorOpacity(0.1);
			this.setAttributes(interiorAttrs);
			this.setHighlightAttributes(interiorAttrs);

			// Setup default border rendering attributes. Note that the border
			// rendering attributes are configured
			// so that only the SurfaceSector's outline is rendered.
			ShapeAttributes borderAttrs = new BasicShapeAttributes();
			borderAttrs.setDrawInterior(false);
			borderAttrs.setOutlineMaterial(new Material(Color.RED));
			borderAttrs.setOutlineOpacity(0.7);
			borderAttrs.setOutlineWidth(3);
			this.getBorder().setAttributes(borderAttrs);
			this.getBorder().setHighlightAttributes(borderAttrs);
		}

		public Color getInteriorColor() {
			return this.getAttributes().getInteriorMaterial().getDiffuse();
		}

		public void setInteriorColor(Color color) {
			ShapeAttributes attr = this.getAttributes();
			attr.setInteriorMaterial(new Material(color));
			this.setAttributes(attr);
		}

		public Color getBorderColor() {
			return this.getBorder().getAttributes().getOutlineMaterial()
					.getDiffuse();
		}

		public void setBorderColor(Color color) {
			ShapeAttributes attr = this.getBorder().getAttributes();
			attr.setOutlineMaterial(new Material(color));
			this.getBorder().setAttributes(attr);
		}

		public double getInteriorOpacity() {
			return this.getAttributes().getInteriorOpacity();
		}

		public void setInteriorOpacity(double opacity) {
			ShapeAttributes attr = this.getAttributes();
			attr.setInteriorOpacity(opacity);
			this.setAttributes(attr);
		}

		public double getBorderOpacity() {
			return this.getBorder().getAttributes().getOutlineOpacity();
		}

		public void setBorderOpacity(double opacity) {
			ShapeAttributes attr = this.getBorder().getAttributes();
			attr.setOutlineOpacity(opacity);
			this.getBorder().setAttributes(attr);
		}

		public double getBorderWidth() {
			return this.getBorder().getAttributes().getOutlineWidth();
		}

		public void setBorderWidth(double width) {
			ShapeAttributes attr = this.getBorder().getAttributes();
			attr.setOutlineWidth(width);
			this.getBorder().setAttributes(attr);
		}

		public void setSector(Sector sector) {
			super.setSector(sector);
			this.getBorder().setSector(sector);
		}

		protected boolean isResizeable() {
			return resizeable;
		}

		protected void setResizeable(boolean resizeable) {
			this.resizeable = resizeable;
		}

		protected Position getStartPosition() {
			return startPosition;
		}

		protected void setStartPosition(Position startPosition) {
			this.startPosition = startPosition;
		}

		protected Position getEndPosition() {
			return endPosition;
		}

		protected void setEndPosition(Position endPosition) {
			this.endPosition = endPosition;
		}

		protected SurfaceSector getBorder() {
			return borderShape;
		}

		protected void setBorder(SurfaceSector shape) {
			if (shape == null) {
				String message = Logging.getMessage("nullValue.Shape");
				Logging.logger().severe(message);
				throw new IllegalArgumentException(message);
			}

			this.borderShape = shape;
		}

		protected boolean hasSelection() {
			return getStartPosition() != null && getEndPosition() != null;
		}

		protected void clear() {
			this.setStartPosition(null);
			this.setEndPosition(null);
			this.setSector(Sector.EMPTY_SECTOR);
		}

		public void preRender(DrawContext dc) {
			// This is called twice: once during normal rendering, then again
			// during ordered surface rendering. During
			// normal renering we pre-render both the interior and border
			// shapes. During ordered surface rendering, both
			// shapes are already added to the DrawContext and both will be
			// individually processed. Therefore we just
			// call our superclass behavior
			if (dc.isOrderedRenderingMode()) {
				super.preRender(dc);
				return;
			}

			this.doPreRender(dc);
		}

		@Override
		public void render(DrawContext dc) {
			if (dc.isPickingMode() && this.isResizeable())
				return;

			// This is called twice: once during normal rendering, then again
			// during ordered surface rendering. During
			// normal renering we render both the interior and border shapes.
			// During ordered surface rendering, both
			// shapes are already added to the DrawContext and both will be
			// individually processed. Therefore we just
			// call our superclass behavior
			if (dc.isOrderedRenderingMode()) {
				super.render(dc);
				return;
			}

			if (!this.isResizeable()) {
				if (this.hasSelection()) {
					this.doRender(dc);
				}
				return;
			}

			PickedObjectList pos = dc.getPickedObjects();
			PickedObject terrainObject = pos != null ? pos.getTerrainObject()
					: null;

			if (terrainObject == null)
				return;

			if (this.getStartPosition() != null) {
				Position end = terrainObject.getPosition();
				if (!this.getStartPosition().equals(end)) {
					this.setEndPosition(end);
					this.setSector(Sector.boundingSector(
							this.getStartPosition(), this.getEndPosition()));
					this.doRender(dc);
				}
			} else {
				this.setStartPosition(pos.getTerrainObject().getPosition());
			}
		}

		protected void doPreRender(DrawContext dc) {
			this.doPreRenderInterior(dc);
			this.doPreRenderBorder(dc);
		}

		protected void doPreRenderInterior(DrawContext dc) {
			super.preRender(dc);
		}

		protected void doPreRenderBorder(DrawContext dc) {
			this.getBorder().preRender(dc);
		}

		protected void doRender(DrawContext dc) {
			this.doRenderInterior(dc);
			this.doRenderBorder(dc);
		}

		protected void doRenderInterior(DrawContext dc) {
			super.render(dc);
		}

		protected void doRenderBorder(DrawContext dc) {
			this.getBorder().render(dc);
		}
	}

	@Override
	public void selected(SelectEvent event) {
		// TODO Auto-generated method stub

	}
}
