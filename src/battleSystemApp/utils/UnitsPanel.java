package battleSystemApp.utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;

import gov.nasa.worldwind.WWObjectImpl;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Offset;
import gov.nasa.worldwind.render.PreRenderable;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.util.tree.BasicFrameAttributes;
import gov.nasa.worldwind.util.tree.ScrollFrame;
import gov.nasa.worldwind.util.tree.Scrollable;
import gov.nasa.worldwind.util.tree.TreeLayout;

/**
 * Ventana scrollable con las operaciones para informar sobre unidades
 * 
 * @author vgonllo
 * 
 */
public class UnitsPanel extends WWObjectImpl implements Scrollable,
		PreRenderable, Renderable {

	/** Attributes to use when the frame is highlighted. */
	protected BasicScrollFrameAttributes highlightAttributes;
	/** Attributes to use when is not highlighted. */
	protected BasicScrollFrameAttributes activeAttributes;

	protected PanelLayout layout;
	protected static final Offset DEFAULT_OFFSET = new Offset(100.0, 100.0,
			AVKey.PIXELS, AVKey.PIXELS);
	protected static final String DEFAULT_FRAME_IMAGE = "resources/images/info-20x20.png";
	protected static final String DEFAULT_FRAME_TITLE = "Mensajes";
	protected ScrollFrame frame;

	public UnitsPanel() {
		this.initialize(null);

	}

	public UnitsPanel(Offset screenLocation) {
		this.initialize(screenLocation);

	}

	protected void initialize(Offset offset) {
		this.setLayout(this.createPanelLayout(offset));
	}

	/**
	 * Get the frame that surrounds the panel.
	 * 
	 * @return The frame that the panel is drawn on.
	 */
	public ScrollFrame getFrame() {
		return this.frame;
	}

	protected PanelLayout createPanelLayout(Offset offset) {
		if (offset == null)
			offset = DEFAULT_OFFSET;

		layout = new BasicPanelLayout(offset);

		layout.getFrame().setFrameTitle(DEFAULT_FRAME_TITLE);
		layout.getFrame().setIconImageSource(DEFAULT_FRAME_IMAGE);

		BasicScrollFrameAttributes attributes = new BasicScrollFrameAttributes();
		attributes.setRootVisible(false);
		attributes.setColor(new Color(0x00FF00));
		attributes.setFont(Font.decode("Verdana-12"));
		attributes.setRowSpacing(0);
		layout.setAttributes(attributes);

		BasicFrameAttributes frameAttributes = new BasicFrameAttributes();
		frameAttributes.setBackgroundOpacity(0.7);
		layout.getFrame().setAttributes(frameAttributes);

		BasicScrollFrameAttributes highlightAttributes = new BasicScrollFrameAttributes(
				attributes);
		layout.setHighlightAttributes(highlightAttributes);

		BasicFrameAttributes highlightFrameAttributes = new BasicFrameAttributes(
				frameAttributes);
		highlightFrameAttributes.setForegroundOpacity(1.0);
		highlightFrameAttributes.setBackgroundOpacity(1.0);
		layout.getFrame().setHighlightAttributes(highlightFrameAttributes);

		return layout;
	}

	// @Override
	// public void preRender(DrawContext dc) {
	// // TODO Auto-generated method stub
	// PanelLayout layout = this.getLayout();
	// if (layout instanceof PreRenderable) {
	// ((PreRenderable) layout).preRender(dc);
	// }
	// }

	/** {@inheritDoc} */
	public PanelLayout getLayout() {
		return layout;
	}

	/** {@inheritDoc} */
	private void setLayout(PanelLayout layout) {
		if (this.layout != null)
			this.layout.removePropertyChangeListener(this);

		this.layout = layout;

		if (this.layout != null)
			this.layout.addPropertyChangeListener(this);
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

	@Override
	public void preRender(DrawContext dc) {
		PanelLayout layout = this.getLayout();
		if (layout instanceof PreRenderable) {
			((PreRenderable) layout).preRender(dc);
		}

	}
}
