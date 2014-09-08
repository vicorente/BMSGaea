package battleSystemApp.utils;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import gov.nasa.worldwind.WWObjectImpl;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Offset;
import gov.nasa.worldwind.render.PreRenderable;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.util.tree.BasicTreeLayout;
import gov.nasa.worldwind.util.tree.Scrollable;

/**
 * Ventana scrollable con las operaciones para informar sobre unidades
 * 
 * @author vgonllo
 * 
 */
public class UnitsPanel extends WWObjectImpl implements Scrollable,
		PreRenderable, Renderable {

	/** Attributes to use when is not highlighted. */
	protected BasicScrollFrameAttributes normalAttributes = new BasicScrollFrameAttributes();
	/** Attributes to use when the frame is highlighted. */
	protected BasicScrollFrameAttributes highlightAttributes = new BasicScrollFrameAttributes();

	protected PanelLayout layout;

	/** Active attributes, either normal or highlight. */
	protected BasicScrollFrameAttributes activeAttributes = new BasicScrollFrameAttributes();
	/** Frame that contains the window. */
	protected ScrollFrame frame;

	public UnitsPanel(Offset screenLocation) {
		this.frame = this.createFrame();
		layout = new PanelLayout(screenLocation);
		BasicScrollFrameAttributes attributes = new BasicScrollFrameAttributes();
		
	}

	@Override
	public void preRender(DrawContext dc) {
		// TODO Auto-generated method stub
		PanelLayout layout = this.getLayout();
		if (layout instanceof PreRenderable) {
			((PreRenderable) layout).preRender(dc);
		}
	}

	/** {@inheritDoc} */
	public PanelLayout getLayout() {
		return layout;
	}

	@Override
	public void renderScrollable(DrawContext dc, Point location,
			Dimension frameSize, Rectangle clipBounds) {
		// TODO Auto-generated method stub

	}

	@Override
	public Dimension getSize(DrawContext dc, Dimension frameSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setHighlighted(boolean highlighted) {
		// TODO Auto-generated method stub

	}

	@Override
	public long getUpdateTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	protected BasicScrollFrameAttributes getActiveAttributes() {
		return this.activeAttributes;
	}

	@Override
	public void render(DrawContext dc) {
		PanelLayout layout = this.getLayout();
		if (layout != null) {
			layout.render(dc);
		}
	}
}
