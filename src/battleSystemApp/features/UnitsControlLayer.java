package battleSystemApp.features;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.AnnotationAttributes;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.ScreenAnnotation;
import gov.nasa.worldwind.util.Logging;

public class UnitsControlLayer extends RenderableLayer {

	// The default images
	protected final static String IMAGE_UNIT = "resources/images/add64x64.png";
	protected final static String IMAGE_ALARM = "resources/images/alert64x64.png";
	protected final static String IMAGE_THREAT = "resources/images/target-grey64x64.png";
	protected final static String IMAGE_TAC_LINE = "resources/images/mapa64x64.png";
	protected final static String IMAGE_INSTALLATION = "resources/images/fire-engineering64x64.png";


	// The annotations used to display the controls.
	protected ScreenAnnotation controlUnit;
	protected ScreenAnnotation controlAlarm;
	protected ScreenAnnotation controlThreat;
	protected ScreenAnnotation controlTacLine;
	protected ScreenAnnotation controlInstallation;
	protected ScreenAnnotation currentControl;

	protected String position = AVKey.SOUTHEAST;
	protected String layout = AVKey.HORIZONTAL;
	protected Vec4 locationCenter = null;
	protected Vec4 locationOffset = null;
	protected double scale = 1;
	protected int borderWidth = 20;
	protected int buttonSize = 32;
	protected int alarmSize = 64;
	protected boolean initialized = false;
	protected Rectangle referenceViewport;
	protected int iconSeparation = 5;
	
	protected boolean showUnitControls = true;
	protected boolean showAlarmControls = true;
	protected boolean showThreatControls = true;
	protected boolean showHeadingControls = true;
	protected boolean showTacLineControls = true;
	protected boolean showInstallationControls = true;
	

	public int getBorderWidth() {
		return this.borderWidth;
	}

	/**
	 * Sets the view controls offset from the viewport border.
	 * 
	 * @param borderWidth
	 *            the number of pixels to offset the view controls from the
	 *            borders indicated by {@link #setPosition(String)}.
	 */
	public void setBorderWidth(int borderWidth) {
		this.borderWidth = borderWidth;
		clearControls();
	}

	/**
	 * Get the controls display scale.
	 * 
	 * @return the controls display scale.
	 */
	public double getScale() {
		return this.scale;
	}

	/**
	 * Set the controls display scale.
	 * 
	 * @param scale
	 *            the controls display scale.
	 */
	public void setScale(double scale) {
		this.scale = scale;
		clearControls();
	}

	protected int getButtonSize() {
		return buttonSize;
	}

	protected void setButtonSize(int buttonSize) {
		this.buttonSize = buttonSize;
		clearControls();
	}

	protected int getalarmSize() {
		return alarmSize;
	}

	protected void setalarmSize(int alarmSize) {
		this.alarmSize = alarmSize;
		clearControls();
	}

	/**
	 * Returns the current relative view controls position.
	 * 
	 * @return the current view controls position.
	 */
	public String getPosition() {
		return this.position;
	}

	/**
	 * Sets the relative viewport location to display the view controls. Can be
	 * one of {@link AVKey#NORTHEAST}, {@link AVKey#NORTHWEST},
	 * {@link AVKey#SOUTHEAST}, or {@link AVKey#SOUTHWEST} (the default). These
	 * indicate the corner of the viewport to place view controls.
	 * 
	 * @param position
	 *            the desired view controls position, in screen coordinates.
	 */
	public void setPosition(String position) {
		if (position == null) {
			String message = Logging.getMessage("nullValue.PositionIsNull");
			Logging.logger().severe(message);
			throw new IllegalArgumentException(message);
		}
		this.position = position;
		clearControls();
	}

	/**
	 * Returns the current layout. Can be one of {@link AVKey#HORIZONTAL} or
	 * {@link AVKey#VERTICAL}.
	 * 
	 * @return the current layout.
	 */
	public String getLayout() {
		return this.layout;
	}

	/**
	 * Sets the desired layout. Can be one of {@link AVKey#HORIZONTAL} or
	 * {@link AVKey#VERTICAL}.
	 * 
	 * @param layout
	 *            the desired layout.
	 */
	public void setLayout(String layout) {
		if (layout == null) {
			String message = Logging.getMessage("nullValue.StringIsNull");
			Logging.logger().severe(message);
			throw new IllegalArgumentException(message);
		}
		if (!this.layout.equals(layout)) {
			this.layout = layout;
			clearControls();
		}
	}

	/**
	 * Layer opacity is not applied to layers of this type. Opacity is
	 * controlled by the alpha values of the operation images.
	 * 
	 * @param opacity
	 *            the current opacity value, which is ignored by this layer.
	 */
	@Override
	public void setOpacity(double opacity) {
		super.setOpacity(opacity);
	}

	/**
	 * Returns the layer's opacity value, which is ignored by this layer.
	 * Opacity is controlled by the alpha values of the operation images.
	 * 
	 * @return The layer opacity, a value between 0 and 1.
	 */
	@Override
	public double getOpacity() {
		return super.getOpacity();
	}

	/**
	 * Returns the current layer image location.
	 * 
	 * @return the current location center. May be null.
	 */
	public Vec4 getLocationCenter() {
		return locationCenter;
	}

	/**
	 * Specifies the screen location of the layer, relative to the image's
	 * center. May be null. If this value is non-null, it overrides the position
	 * specified by {@link #setPosition(String)}. The location is specified in
	 * pixels. The origin is the window's lower left corner. Positive X values
	 * are to the right of the origin, positive Y values are upwards from the
	 * origin. The final image location will be affected by the currently
	 * specified location offset if a non-null location offset has been
	 * specified (see {@link #setLocationOffset(gov.nasa.worldwind.geom.Vec4)}
	 * )}.
	 * 
	 * @param locationCenter
	 *            the location center. May be null.
	 * 
	 * @see #setPosition(String)
	 * @see #setLocationOffset(gov.nasa.worldwind.geom.Vec4)
	 */
	public void setLocationCenter(Vec4 locationCenter) {
		this.locationCenter = locationCenter;
		clearControls();
	}

	/**
	 * Returns the current location offset. See #setLocationOffset for a
	 * description of the offset and its values.
	 * 
	 * @return the location offset. Will be null if no offset has been
	 *         specified.
	 */
	public Vec4 getLocationOffset() {
		return locationOffset;
	}

	/**
	 * Specifies a placement offset from the layer position on the screen.
	 * 
	 * @param locationOffset
	 *            the number of pixels to shift the layer image from its
	 *            specified screen position. A positive X value shifts the image
	 *            to the right. A positive Y value shifts the image up. If null,
	 *            no offset is applied. The default offset is null.
	 * 
	 * @see #setLocationCenter(gov.nasa.worldwind.geom.Vec4)
	 * @see #setPosition(String)
	 */
	public void setLocationOffset(Vec4 locationOffset) {
		this.locationOffset = locationOffset;
		clearControls();
	}

	public boolean isShowUnitControls() {
		return this.showUnitControls;
	}

	public void setShowUnitControls(boolean state) {
		if (this.showUnitControls != state) {
			this.showUnitControls = state;
			clearControls();
		}
	}

	public boolean isShowAlarmControls() {
		return this.showAlarmControls;
	}

	public void setShowAlarmControls(boolean state) {
		if (this.showAlarmControls != state) {
			this.showAlarmControls = state;
			clearControls();
		}
	}

	public boolean isShowThreatControls() {
		return this.showThreatControls;
	}

	public void setShowThreatControls(boolean state) {
		if (this.showThreatControls != state) {
			this.showThreatControls = state;
			clearControls();
		}
	}

	public boolean isShowTacLineControls() {
		return this.showTacLineControls;
	}

	public void setShowTacLineControls(boolean state) {
		if (this.showTacLineControls != state) {
			this.showTacLineControls = state;
			clearControls();
		}
	}

	public boolean isShowInstallationControls() {
		return this.showInstallationControls;
	}

	public void setShowInstallationControls(boolean state) {
		if (this.showInstallationControls != state) {
			this.showInstallationControls = state;
			clearControls();
		}
	}

	
	/**
	 * Get the control type associated with the given object or null if unknown.
	 * 
	 * @param control
	 *            the control object
	 * 
	 * @return the control type. Can be one of {@link AVKey#VIEW_PAN},
	 *         {@link AVKey#VIEW_LOOK}, {@link AVKey#VIEW_HEADING_LEFT},
	 *         {@link AVKey#VIEW_HEADING_RIGHT}, {@link AVKey#VIEW_ZOOM_IN},
	 *         {@link AVKey#VIEW_ZOOM_OUT}, {@link AVKey#VIEW_PITCH_UP},
	 *         {@link AVKey#VIEW_PITCH_DOWN}, {@link AVKey#VIEW_FOV_NARROW} or
	 *         {@link AVKey#VIEW_FOV_WIDE}.
	 *         <p>
	 *         Returns null if the object is not a view control associated with
	 *         this layer.
	 *         </p>
	 */
	public String getControlType(Object control) {
		if (control == null || !(control instanceof ScreenAnnotation))
			return null;

		if (showUnitControls && controlUnit.equals(control))
			return AVKey.VIEW_UNIT;
		else if (showAlarmControls && controlAlarm.equals(control))
			return AVKey.VIEW_ALARM;
		else if (showHeadingControls && controlThreat.equals(control))
			return AVKey.VIEW_THREAT;
		else if (showHeadingControls && controlTacLine.equals(control))
			return AVKey.VIEW_TAC_LINE;
		else if (showThreatControls && controlInstallation.equals(control))
			return AVKey.VIEW_INSTALLATION;

		return null;
	}

	/**
	 * Indicates the currently highlighted control, if any.
	 * 
	 * @return the currently highlighted control, or null if no control is
	 *         highlighted.
	 */
	public Object getHighlightedObject() {
		return this.currentControl;
	}

	/**
	 * Specifies the control to highlight. Any currently highlighted control is
	 * un-highlighted.
	 * 
	 * @param control
	 *            the control to highlight.
	 */
	public void highlight(Object control) {
		// Manage highlighting of controls.
		if (this.currentControl == control)
			return; // same thing selected

		// Turn off highlight if on.
		if (this.currentControl != null) {
			this.currentControl.getAttributes().setImageOpacity(-1); // use
																		// default
																		// opacity
			this.currentControl = null;
		}

		// Turn on highlight if object selected.
		if (control != null && control instanceof ScreenAnnotation) {
			this.currentControl = (ScreenAnnotation) control;
			this.currentControl.getAttributes().setImageOpacity(1);
		}
	}

	@Override
	public void doRender(DrawContext dc) {
		if (!this.initialized)
			initialize(dc);

		if (!this.referenceViewport.equals(dc.getView().getViewport()))
			updatePositions(dc);

		super.doRender(dc);
	}

	protected boolean isInitialized() {
		return initialized;
	}

	protected void initialize(DrawContext dc) {
		if (this.initialized)
			return;

		// Setup user interface - common default attributes
		AnnotationAttributes ca = new AnnotationAttributes();
		ca.setAdjustWidthToText(AVKey.SIZE_FIXED);
		ca.setInsets(new Insets(0, 0, 0, 0));
		ca.setBorderWidth(0);
		ca.setCornerRadius(0);
		ca.setSize(new Dimension(buttonSize, buttonSize));
		ca.setBackgroundColor(new Color(0, 0, 0, 0));
		ca.setImageOpacity(.5);
		ca.setScale(scale);

		final String NOTEXT = "";
		final Point ORIGIN = new Point(0, 0);
		if (this.showUnitControls) {
			// Unit
			controlUnit = new ScreenAnnotation(NOTEXT, ORIGIN, ca);
			controlUnit.setValue(AVKey.VIEW_OPERATION, AVKey.VIEW_UNIT);
			controlUnit.getAttributes().setImageSource(
					getImageSource(AVKey.VIEW_UNIT));
			controlUnit.getAttributes().setSize(
					new Dimension(alarmSize, alarmSize));
			this.addRenderable(controlUnit);
		}
		if (this.showAlarmControls) {
			// Alarm
			controlAlarm = new ScreenAnnotation(NOTEXT, ORIGIN, ca);
			controlAlarm.setValue(AVKey.VIEW_OPERATION, AVKey.VIEW_ALARM);
			controlAlarm.getAttributes().setImageSource(
					getImageSource(AVKey.VIEW_ALARM));
			controlAlarm.getAttributes().setSize(
					new Dimension(alarmSize, alarmSize));
			this.addRenderable(controlAlarm);
		}
		if (this.showThreatControls) {
			// Threat
			controlThreat = new ScreenAnnotation(NOTEXT, ORIGIN, ca);
			controlThreat.setValue(AVKey.VIEW_OPERATION, AVKey.VIEW_THREAT);
			controlThreat.getAttributes().setImageSource(
					getImageSource(AVKey.VIEW_THREAT));
			controlThreat.getAttributes().setSize(
					new Dimension(alarmSize, alarmSize));
			this.addRenderable(controlThreat);
		}
		if (this.showTacLineControls) {
			// TacLine
			controlTacLine = new ScreenAnnotation(NOTEXT, ORIGIN, ca);
			controlTacLine.setValue(AVKey.VIEW_OPERATION, AVKey.VIEW_TAC_LINE);
			controlTacLine.getAttributes().setImageSource(
					getImageSource(AVKey.VIEW_TAC_LINE));
			controlTacLine.getAttributes().setSize(
					new Dimension(alarmSize, alarmSize));
			this.addRenderable(controlTacLine);
		}
		if (this.showInstallationControls) {
			controlInstallation = new ScreenAnnotation(NOTEXT, ORIGIN, ca);
			controlInstallation.setValue(AVKey.VIEW_OPERATION,
					AVKey.VIEW_INSTALLATION);
			controlInstallation.getAttributes().setImageSource(
					getImageSource(AVKey.VIEW_INSTALLATION));
			controlInstallation.getAttributes().setSize(
					new Dimension(alarmSize, alarmSize));

			this.addRenderable(controlInstallation);
		}
		// if (this.showVeControls)
		// {
		// // Vertical Exaggeration
		// controlVeUp = new ScreenAnnotation(NOTEXT, ORIGIN, ca);
		// controlVeUp.setValue(AVKey.VIEW_OPERATION,
		// AVKey.VERTICAL_EXAGGERATION_UP);
		// controlVeUp.getAttributes().setImageSource(getImageSource(AVKey.VERTICAL_EXAGGERATION_UP));
		// this.addRenderable(controlVeUp);
		// controlVeDown = new ScreenAnnotation(NOTEXT, ORIGIN, ca);
		// controlVeDown.setValue(AVKey.VIEW_OPERATION,
		// AVKey.VERTICAL_EXAGGERATION_DOWN);
		// controlVeDown.getAttributes().setImageSource(getImageSource(AVKey.VERTICAL_EXAGGERATION_DOWN));
		// this.addRenderable(controlVeDown);
		// }

		// Place controls according to layout and viewport dimension
		updatePositions(dc);

		this.initialized = true;
	}

	/**
	 * Get a control image source.
	 * 
	 * @param control
	 *            the control type. Can be one of {@link AVKey#VIEW_UNIT},
	 *            {@link AVKey#VIEW_ALARM}, {@link AVKey#VIEW_THREAT},
	 *            {@link AVKey#VIEW_TAC_LINE}, {@link AVKey#VIEW_ZOOM_IN},
	 *            {@link AVKey#VIEW_INSTALLATION}
	 * 
	 * @return the image source associated with the given control type.
	 */
	protected Object getImageSource(String control) {
		if (control.equals(AVKey.VIEW_UNIT))
			return IMAGE_UNIT;
		else if (control.equals(AVKey.VIEW_ALARM))
			return IMAGE_ALARM;
		else if (control.equals(AVKey.VIEW_THREAT))
			return IMAGE_THREAT;
		else if (control.equals(AVKey.VIEW_TAC_LINE))
			return IMAGE_TAC_LINE;
		else if (control.equals(AVKey.VIEW_INSTALLATION))
			return IMAGE_INSTALLATION;

		return null;
	}

	// Set controls positions according to layout and viewport dimension
	protected void updatePositions(DrawContext dc) {
		boolean horizontalLayout = this.layout.equals(AVKey.HORIZONTAL);

		// horizontal layout: pan button + look button beside 2 rows of 4
		// buttons
		int width = (showUnitControls ? alarmSize+iconSeparation : 0)
				+ (showAlarmControls ? alarmSize+iconSeparation : 0)
				+ (showThreatControls ? alarmSize+iconSeparation : 0)
				+ (showTacLineControls ? alarmSize+iconSeparation : 0)
				+ (showInstallationControls ? alarmSize+iconSeparation : 0);
		int height = Math.max(alarmSize, buttonSize * 2);
		width = (int) (width * scale);
		height = (int) (height * scale);
		int xOffset = 0;
		int yOffset = (int) (buttonSize * scale);

		if (!horizontalLayout) {
			// vertical layout: pan button above look button above 4 rows of 2
			// buttons
			int temp = height;
			// noinspection SuspiciousNameCombination
			height = width;
			width = temp;
			xOffset = (int) (buttonSize * scale);
			yOffset = 0;
		}

		int halfalarmSize = (int) (alarmSize * scale / 2);
		int halfButtonSize = (int) (buttonSize * scale / 2);

		Rectangle controlsRectangle = new Rectangle(width, height);
		Point locationSW = computeLocation(dc.getView().getViewport(),
				controlsRectangle);

		// Layout start point
		int x = locationSW.x;
		int y = horizontalLayout ? locationSW.y : locationSW.y + height;

		if (this.showUnitControls) {
			if (!horizontalLayout)
				y -= (int) (alarmSize * scale);
			controlUnit.setScreenPoint(new Point(x + halfalarmSize, y));
			if (horizontalLayout)
				x += (int) (alarmSize * scale) + iconSeparation;
		}
		if (this.showAlarmControls) {

			if (!horizontalLayout)
				y -= (int) (alarmSize * scale);
			controlAlarm.setScreenPoint(new Point(x + halfalarmSize, y));
			if (horizontalLayout)
				x += (int) (alarmSize * scale)+iconSeparation;
		}
		if (this.showThreatControls) {

			if (!horizontalLayout)
				y -= (int) (alarmSize * scale);
			controlThreat.setScreenPoint(new Point(x + halfalarmSize, y));
			if (horizontalLayout)
				x += (int) (alarmSize * scale)+iconSeparation;
		}
		if (this.showTacLineControls) {

			if (!horizontalLayout)
				y -= (int) (alarmSize * scale);
			controlTacLine.setScreenPoint(new Point(x + halfalarmSize, y));
			if (horizontalLayout)
				x += (int) (alarmSize * scale)+iconSeparation;
		}
		if (this.showInstallationControls) {

			if (!horizontalLayout)
				y -= (int) (alarmSize * scale);
			controlInstallation.setScreenPoint(new Point(x + halfalarmSize, y));
			if (horizontalLayout)
				x += (int) (alarmSize * scale)+iconSeparation;
		}		

		this.referenceViewport = dc.getView().getViewport();
	}

	/**
	 * Compute the screen location of the controls overall rectangle bottom
	 * right corner according to either the location center if not null, or the
	 * screen position.
	 * 
	 * @param viewport
	 *            the current viewport rectangle.
	 * @param controls
	 *            the overall controls rectangle
	 * 
	 * @return the screen location of the bottom left corner - south west
	 *         corner.
	 */
	protected Point computeLocation(Rectangle viewport, Rectangle controls) {
		double x;
		double y;

		if (this.locationCenter != null) {
			x = this.locationCenter.x - controls.width / 2;
			y = this.locationCenter.y - controls.height / 2;
		} else if (this.position.equals(AVKey.NORTHEAST)) {
			x = viewport.getWidth() - controls.width - this.borderWidth;
			y = viewport.getHeight() - controls.height - this.borderWidth;
		} else if (this.position.equals(AVKey.SOUTHEAST)) {
			x = viewport.getWidth() - controls.width - this.borderWidth;
			y = 0d + this.borderWidth;
		} else if (this.position.equals(AVKey.NORTHWEST)) {
			x = 0d + this.borderWidth;
			y = viewport.getHeight() - controls.height - this.borderWidth;
		} else if (this.position.equals(AVKey.SOUTHWEST)) {
			x = 0d + this.borderWidth;
			y = 0d + this.borderWidth;
		} else // use North East as default
		{
			x = viewport.getWidth() - controls.width - this.borderWidth;
			y = viewport.getHeight() - controls.height - this.borderWidth;
		}

		if (this.locationOffset != null) {
			x += this.locationOffset.x;
			y += this.locationOffset.y;
		}

		return new Point((int) x, (int) y);
	}

	protected void clearControls() {
		this.removeAllRenderables();

		this.controlUnit = null;
		this.controlAlarm = null;
		this.controlThreat = null;
		this.controlTacLine = null;
		this.controlInstallation = null;

		this.initialized = false;
	}

	@Override
	public String toString() {
		return Logging.getMessage("layers.ViewControlsLayer.Name");
	}
}
