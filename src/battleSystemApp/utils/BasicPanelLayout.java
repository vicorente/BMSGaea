package battleSystemApp.utils;

import gov.nasa.worldwind.WWObjectImpl;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.event.Message;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Offset;
import gov.nasa.worldwind.render.PreRenderable;
import gov.nasa.worldwind.util.BoundedHashMap;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.OGLTextRenderer;
import gov.nasa.worldwind.util.tree.ScrollFrame;
import gov.nasa.worldwind.util.tree.Scrollable;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

import com.jogamp.opengl.util.awt.TextRenderer;

public class BasicPanelLayout extends WWObjectImpl implements PanelLayout,
		Scrollable, PreRenderable {

	/** Frame that contains the tree. */
	protected ScrollFrame frame;
	protected BasicScrollFrameAttributes highlightAttributes;
	protected ScrollFrameAttributes attributes;
	protected long attributesFrameNumber = -1L;
	/** The attributes used if attributes are not specified. */
	protected static final ScrollFrameAttributes defaultAttributes;
	/** Indicates that the tree size needs to be computed. */
	protected boolean mustRecomputeSize = true;
	/** Indicates that the tree layout needs to be computed. */
	protected boolean mustRecomputeLayout = true;
	protected BoundedHashMap<TextCacheKey, Rectangle2D> textCache = new BoundedHashMap<TextCacheKey, Rectangle2D>();
	/**
	 * Time at which the rendered tree last changed. Used to indicate when the
	 * ScrollFrame needs to refresh the rendered representation
	 */
	protected long updateTime;
	protected Point screenLocation;
	protected ScrollFrameAttributes activeAttributes = new BasicScrollFrameAttributes();
	protected int indent;
	protected int lineHeight;
	protected boolean highlighted;
	protected long frameNumber = -1L;
	protected Dimension previousFrameSize;
	/**
	 * Point at which the next component should be drawn. Nodes are drawn left
	 * to right, and the draw point is updated as parts of the node are
	 * rendered. For example, the toggle triangle is drawn first at the draw
	 * point, and then the draw point is moved to the right by the width of the
	 * triangle, so the next component will draw at the correct point. The draw
	 * point is reset to the lower left corner of the node bounds before each
	 * render cycle.
	 */
	protected Point drawPoint;
	static {
		defaultAttributes = new BasicScrollFrameAttributes();
	}

	public BasicPanelLayout(Offset screenLocation) {
		this.frame = this.createFrame();
		this.frame.setContents(this);

		// Listen for property changes in the frame. These will be forwarded to
		// the layout listeners. This is necessary
		// to pass AVKey.REPAINT events up the layer.
		this.frame.addPropertyChangeListener(this);

		this.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
				// Ignore events originated by this Layout, and repaint
				// events. There is no need to recompute the
				// tree layout just because a repaint was triggered.
				if (propertyChangeEvent.getSource() != BasicPanelLayout.this
						&& !AVKey.REPAINT.equals(propertyChangeEvent
								.getPropertyName())) {
					BasicPanelLayout.this.invalidate();
				}
			}
		});

		if (screenLocation != null)
			this.setScreenLocation(screenLocation);
	}

	private ScrollFrame createFrame() {
		return new ScrollFrame();
	}

	/**
	 * Set the location of the upper left corner of the tree, measured in screen
	 * coordinates with the origin at the upper left corner of the screen.
	 * 
	 * @param screenLocation
	 *            New screen location.
	 */
	public void setScreenLocation(Offset screenLocation) {
		frame.setScreenLocation(screenLocation);
	}

	/**
	 * Get the location of the upper left corner of the tree, measured in screen
	 * coordinates with the origin at the upper left corner of the screen.
	 * 
	 * @return Screen location, measured in pixels from the upper left corner of
	 *         the screen.
	 */
	public Offset getScreenLocation() {
		return this.frame.getScreenLocation();
	}

	@Override
	public Object setValue(String key, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AVList setValues(AVList avList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getValue(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Object> getValues() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getStringValue(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Entry<String, Object>> getEntries() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasKey(String key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object removeKey(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public void firePropertyChange(PropertyChangeEvent propertyChangeEvent) {
		// TODO Auto-generated method stub

	}

	@Override
	public AVList copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AVList clearList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
		// Ignore events originated by this TreeLayout, and repaint events.
		// There is no need to recompute the
		// tree layout just because a repaint was triggered.
		if (propertyChangeEvent.getSource() != BasicPanelLayout.this
				&& !AVKey.REPAINT.equals(propertyChangeEvent.getPropertyName())) {
			BasicPanelLayout.this.invalidate();
		}

	}

	/** Force the layout to recompute. */
	public void invalidate() {
		this.markUpdated();
		this.mustRecomputeSize = true;
		this.mustRecomputeLayout = true;
	}

	/**
	 * Set the {@link #updateTime} to the current system time, marking the
	 * Scrollable contents as updated.
	 */
	protected void markUpdated() {
		this.updateTime = System.currentTimeMillis();
	}

	@Override
	public void onMessage(Message msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(DrawContext dc) {
		this.frame.render(dc);

	}

	@Override
	public void setAttributes(ScrollFrameAttributes attributes) {
		if (attributes == null) {
			String msg = Logging.getMessage("nullValue.AttributesIsNull");
			Logging.logger().severe(msg);
			throw new IllegalArgumentException(msg);
		}

		this.attributes = attributes;
	}

	@Override
	public ScrollFrameAttributes getAttributes() {
		return this.attributes;
	}

	@Override
	public ScrollFrame getFrame() {
		return this.frame;
	}

	@Override
	public void setHighlightAttributes(
			BasicScrollFrameAttributes highlightAttributes) {
		if (highlightAttributes == null) {
			String msg = Logging.getMessage("nullValue.AttributesIsNull");
			Logging.logger().severe(msg);
			throw new IllegalArgumentException(msg);
		}

		this.highlightAttributes = highlightAttributes;
	}

	@Override
	public void preRender(DrawContext dc) {
		this.frame.preRender(dc);
	}

	/**
	 * Update the active attributes for the current frame, and compute other
	 * properties that are based on the active attributes. This method only
	 * computes attributes once for each frame. Subsequent calls in the same
	 * frame will not recompute the attributes.
	 * 
	 * @param dc
	 *            Current draw context.
	 */
	protected void updateAttributes(DrawContext dc) {
		if (dc.getFrameTimeStamp() != this.attributesFrameNumber) {
			this.determineActiveAttributes();
			this.indent = this.computeIndentation();
			this.lineHeight = this.computeMaxTextHeight(dc);

			this.attributesFrameNumber = dc.getFrameTimeStamp();

		}
	}

	/**
	 * Determine the maximum height of a line of text using the active font.
	 * 
	 * @param dc
	 *            Current draw context.
	 * 
	 * @return The maximum height of a line of text.
	 */
	protected int computeMaxTextHeight(DrawContext dc) {
		ScrollFrameAttributes attributes = this.getActiveAttributes();

		// Use underscore + capital E with acute accent as max height
		Rectangle2D bounds = this.getTextBounds(dc, "_\u00c9",
				attributes.getFont());

		double lineHeight = Math.abs(bounds.getY());
		return (int) Math.max(lineHeight, attributes.getIconSize().height);
	}

	/**
	 * Get the bounds of a text string. This method consults the text bound
	 * cache. If the bounds of the input string are not already cached, they
	 * will be computed and added to the cache.
	 * 
	 * @param dc
	 *            Draw context.
	 * @param text
	 *            Text to get bounds of.
	 * @param font
	 *            Font applied to the text.
	 * 
	 * @return A rectangle that describes the node bounds. See
	 *         com.jogamp.opengl.util.awt.TextRenderer.getBounds for information
	 *         on how this rectangle should be interpreted.
	 */
	protected Rectangle2D getTextBounds(DrawContext dc, String text, Font font) {
		TextCacheKey cacheKey = new TextCacheKey(text, font);
		Rectangle2D bounds = this.textCache.get(cacheKey);

		if (bounds == null) {
			TextRenderer textRenderer = OGLTextRenderer
					.getOrCreateTextRenderer(dc.getTextRendererCache(), font);
			bounds = textRenderer.getBounds(text);

			this.textCache.put(cacheKey, bounds);
		}

		return bounds;
	}

	/**
	 * Get the size of the symbol that indicates that a node is selected or not
	 * selected.
	 * 
	 * @return The size of the node selection symbol.
	 */
	protected Dimension getSelectedSymbolSize() {
		return new Dimension(12, 12);
	}

	/**
	 * Compute the indentation, in pixels, applied to each new level of the
	 * tree.
	 * 
	 * @return indention (in pixels) to apply to each new level in the tree.
	 */
	protected int computeIndentation() {
		int iconWidth = this.getActiveAttributes().getIconSize().width;
		int iconSpacing = this.getActiveAttributes().getIconSpace();
		int checkboxWidth = this.getSelectedSymbolSize().width;

		// Compute the indentation to make the checkbox of the child level line
		// up the icon of the parent level
		return checkboxWidth + iconSpacing + ((iconWidth - checkboxWidth) / 2);
	}

	protected ScrollFrameAttributes getActiveAttributes() {
		return this.activeAttributes;
	}

	/**
	 * Determines which attributes -- normal, highlight or default -- to use
	 * each frame.
	 */
	protected void determineActiveAttributes() {
		ScrollFrameAttributes newAttributes = defaultAttributes;

		if (this.isHighlighted()) {
			if (this.getHighlightAttributes() != null)
				newAttributes = this.getHighlightAttributes();
			else {
				// If no highlight attributes have been specified we will use
				// the normal attributes.
				if (this.getAttributes() != null)
					newAttributes = this.getAttributes();
				else
					newAttributes = defaultAttributes;
			}
		} else if (this.getAttributes() != null) {
			newAttributes = this.getAttributes();
		}

		// If the attributes have changed since the last frame, change the
		// update time since the tree needs to repaint
		if (!newAttributes.equals(this.activeAttributes)) {
			this.markUpdated();
		}

		this.activeAttributes.copy(newAttributes);
	}

	/**
	 * Is the tree highlighted? The tree is highlighted when the mouse is within
	 * the bounds of the containing frame.
	 * 
	 * @return True if the tree is highlighted.
	 */
	public boolean isHighlighted() {
		return this.highlighted;
	}

	/**
	 * Get the attributes to apply when the tree is highlighted.
	 * 
	 * @return Attributes to use when tree is highlighted.
	 */
	public ScrollFrameAttributes getHighlightAttributes() {
		return this.highlightAttributes;
	}

	/**
	 * Indicates whether or not the tree layout needs to be recomputed.
	 * 
	 * @param frameSize
	 *            Size of the frame that holds the tree.
	 * 
	 * @return {@code true} if the layout needs to be recomputed, otherwise
	 *         {@code false}.
	 */
	protected boolean mustRecomputeLayout(Dimension frameSize) {
		return this.mustRecomputeLayout || this.previousFrameSize == null
				|| this.previousFrameSize.width != frameSize.width;
	}

	@Override
	public void renderScrollable(DrawContext dc, Point location,
			Dimension frameSize, Rectangle clipBounds) {

		this.screenLocation = location;
		this.updateAttributes(dc);

		if (this.frameNumber != dc.getFrameTimeStamp()) {
			if (this.mustRecomputeLayout(frameSize)) {
				// this.treeNodes.clear();
				//
				// Point drawPoint = new Point(0, this.size.height);
				// this.computeTreeLayout(root, dc, frameSize, drawPoint, 1,
				// treeNodes);

				this.previousFrameSize = frameSize;
				this.mustRecomputeLayout = false;
			}

			this.frameNumber = dc.getFrameTimeStamp();
		}

	}

	@Override
	public Dimension getSize(DrawContext dc, Dimension frameSize) {
		Dimension size = new Dimension();
		size.height = 100;
		size.width = 100;
		return size;
	}

	@Override
	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
	}

	@Override
	public long getUpdateTime() {
		return this.updateTime;
	}

	/** Cache key for cache text bound cache. */
	protected static class TextCacheKey {
		/** Text string. */
		protected String text;
		/** Font used to compute bounds. */
		protected Font font;
		/** Hash code. */
		protected int hash = 0;

		/**
		 * Create a cache key for a string rendered in a font.
		 * 
		 * @param text
		 *            String for which to cache bounds.
		 * @param font
		 *            Font of the rendered string.
		 */
		public TextCacheKey(String text, Font font) {
			if (text == null) {
				String message = Logging.getMessage("nullValue.StringIsNull");
				Logging.logger().severe(message);
				throw new IllegalArgumentException(message);
			}
			if (font == null) {
				String message = Logging.getMessage("nullValue.FontIsNull");
				Logging.logger().severe(message);
				throw new IllegalArgumentException(message);
			}

			this.text = text;
			this.font = font;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || this.getClass() != o.getClass())
				return false;

			TextCacheKey cacheKey = (TextCacheKey) o;

			return this.text.equals(cacheKey.text)
					&& this.font.equals(cacheKey.font);
		}

		@Override
		public int hashCode() {
			if (this.hash == 0) {
				int result;
				result = this.text.hashCode();
				result = 31 * result + this.font.hashCode();
				this.hash = result;
			}
			return this.hash;
		}
	}
}
