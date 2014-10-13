package battleSystemApp.features;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Intersection;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Line;
import gov.nasa.worldwind.geom.Matrix;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.render.ScreenAnnotation;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.view.orbit.OrbitView;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import battleSystemApp.core.Controller;
import battleSystemApp.utils.UnitCreator;

/**
 * Controla los iconos de la parte inferior derecha de la interfaz Son controles
 * relacionados con el manejo de operaciones sobre unidades
 * 
 * @author xs491004
 *
 */
public class UnitsControlsSelectListener implements SelectListener {
	protected static final int DEFAULT_TIMER_DELAY = 50;

	protected WorldWindow wwd;
	protected UnitsControlLayer unitsControlsLayer;

	protected ScreenAnnotation pressedControl;
	protected String pressedControlType;
	protected Point lastPickPoint = null;
	protected Controller controller;
	protected Timer repeatTimer;

	protected UnitCreator unitCreator;

	/**
	 * Construct a controller for specified <code>WorldWindow</code> and
	 * <code>unitsControlsLayer<c/code>.
	 * <p/>
	 * <code>ViewControlLayer</code>s are not sharable among
	 * <code>WorldWindow</code>s. A separate layer and controller must be
	 * established for each window that's to have view controls.
	 *
	 * @param wwd
	 *            the <code>WorldWindow</code> the specified layer is associated
	 *            with.
	 * @param layer
	 *            the layer to control.
	 */
	public UnitsControlsSelectListener(Controller controller,
			UnitsControlLayer layer) {
		this.controller = controller;
		WorldWindow wwd = controller.getWWd();
		if (wwd == null) {
			String msg = Logging.getMessage("nullValue.WorldWindow");
			Logging.logger().severe(msg);
			throw new IllegalArgumentException(msg);
		}
		if (layer == null) {
			String msg = Logging.getMessage("nullValue.LayerIsNull");
			Logging.logger().severe(msg);
			throw new IllegalArgumentException(msg);
		}

		this.wwd = wwd;
		this.unitsControlsLayer = layer;
		this.unitCreator = new UnitCreator(this.controller);
		// Setup repeat timer
		this.repeatTimer = new Timer(DEFAULT_TIMER_DELAY, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (pressedControl != null)
					updateView(pressedControl, pressedControlType);
			}
		});
		this.repeatTimer.start();
	}

	/**
	 * Set the repeat timer delay in milliseconds.
	 *
	 * @param delay
	 *            the repeat timer delay in milliseconds.
	 *
	 * @throws IllegalArgumentException
	 */
	public void setRepeatTimerDelay(int delay) {
		if (delay <= 0) {
			String message = Logging.getMessage("generic.ArgumentOutOfRange",
					delay);
			Logging.logger().severe(message);
			throw new IllegalArgumentException(message);
		}
		this.repeatTimer.setDelay(delay);
	}

	/**
	 * Get the repeat timer delay in milliseconds.
	 *
	 * @return the repeat timer delay in milliseconds.
	 */
	public int getRepeatTimerDelay() {
		return this.repeatTimer.getDelay();
	}

	public void selected(SelectEvent event) {

		if (this.wwd == null)
			return;

		if (!(this.wwd.getView() instanceof OrbitView))
			return;

		OrbitView view = (OrbitView) this.wwd.getView();

		// if (this.unitsControlsLayer.getHighlightedObject() != null) {
		// this.unitsControlsLayer.highlight(null);
		// this.wwd.redraw(); // must redraw so the de-highlight can take
		// // effect
		// }

		if (event.getMouseEvent() != null && event.getMouseEvent().isConsumed())
			return;

		if (event.getTopObject() == null
				|| event.getTopPickedObject().getParentLayer() != this
						.getParentLayer()
				|| !(event.getTopObject() instanceof AVList))
			return;

		String controlType = ((AVList) event.getTopObject())
				.getStringValue(AVKey.VIEW_OPERATION);
		if (controlType == null)
			return;

		ScreenAnnotation selectedObject = (ScreenAnnotation) event
				.getTopObject();

		this.lastPickPoint = event.getPickPoint();

		if (event.getEventAction().equals(SelectEvent.LEFT_CLICK)) {

			if (controlType.equals(AVKey.VIEW_UNIT)
					&& !controlType.equals(this.pressedControlType)) {
				this.unitCreator.enable();
			//	this.unitsControlsLayer.highlight(selectedObject);
				this.wwd.redraw();
				this.pressedControl = selectedObject;
				this.pressedControlType = controlType;
				
			} else if (this.pressedControlType.equals(AVKey.VIEW_UNIT)
					&& controlType.equals(this.pressedControlType)) {
				this.unitCreator.disable();
				this.unitCreator.setCursor(Cursor
						.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			//	this.unitsControlsLayer.highlight(null);
				this.wwd.redraw();
				this.pressedControl = null;
				this.pressedControlType = null;
			}

		}
	}

	/**
	 * Returns this ViewControlsSelectListener's parent layer. The parent layer
	 * is associated with picked objects, and is used to determine which
	 * SelectEvents thsi ViewControlsSelectListner responds to.
	 *
	 * @return this ViewControlsSelectListener's parent layer.
	 */
	protected Layer getParentLayer() {
		return this.unitsControlsLayer;
	}

	protected void updateView(ScreenAnnotation control, String controlType) {
		if (this.wwd == null)
			return;
		if (!(this.wwd.getView() instanceof OrbitView))
			return;

		OrbitView view = (OrbitView) this.wwd.getView();
		view.stopAnimations();
		view.stopMovement();

		if (controlType.equals(AVKey.VIEW_UNIT)) {

			this.unitCreator.setCursor(Cursor
					.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

		} else if (controlType.equals(AVKey.VIEW_ALARM)) {

		} else if (controlType.equals(AVKey.VIEW_THREAT)) {

		} else if (controlType.equals(AVKey.VIEW_TAC_LINE)) {

		} else if (controlType.equals(AVKey.VIEW_INSTALLATION)) {

		}

		view.firePropertyChange(AVKey.VIEW, null, view);
	}

	protected boolean isPathCrossingAPole(LatLon p1, LatLon p2) {
		return Math.abs(p1.getLongitude().degrees - p2.getLongitude().degrees) > 20
				&& Math.abs(p1.getLatitude().degrees - 90
						* Math.signum(p1.getLatitude().degrees)) < 10;
	}

	protected double computeNewZoom(OrbitView view, double amount) {
		double coeff = 0.05;
		double change = coeff * amount;
		double logZoom = view.getZoom() != 0 ? Math.log(view.getZoom()) : 0;
		// Zoom changes are treated as logarithmic values. This accomplishes two
		// things:
		// 1) Zooming is slow near the globe, and fast at great distances.
		// 2) Zooming in then immediately zooming out returns the viewer to the
		// same zoom value.
		return Math.exp(logZoom + change);
	}

	protected Angle computePanHeading(OrbitView view, ScreenAnnotation control) {
		// Compute last pick point 'heading' relative to pan control center
		double size = control.getAttributes().getSize().width
				* control.getAttributes().getScale();
		Vec4 center = new Vec4(control.getScreenPoint().x,
				control.getScreenPoint().y + size / 2, 0);
		double px = lastPickPoint.x - center.x;
		double py = view.getViewport().getHeight() - lastPickPoint.y - center.y;
		Angle heading = view.getHeading().add(
				Angle.fromRadians(Math.atan2(px, py)));
		heading = heading.degrees >= 0 ? heading : heading.addDegrees(360);
		return heading;
	}

	protected Angle computePanAmount(Globe globe, OrbitView view,
			ScreenAnnotation control, double panStep) {
		// Compute last pick point distance relative to pan control center
		double size = control.getAttributes().getSize().width
				* control.getAttributes().getScale();
		Vec4 center = new Vec4(control.getScreenPoint().x,
				control.getScreenPoint().y + size / 2, 0);
		double px = lastPickPoint.x - center.x;
		double py = view.getViewport().getHeight() - lastPickPoint.y - center.y;
		double pickDistance = Math.sqrt(px * px + py * py);
		double pickDistanceFactor = Math.min(pickDistance / 10, 5);

		// Compute globe angular distance depending on eye altitude
		Position eyePos = view.getEyePosition();
		double radius = globe.getRadiusAt(eyePos);
		double minValue = 0.5 * (180.0 / (Math.PI * radius)); // Minimum change
																// ~0.5 meters
		double maxValue = 1.0; // Maximum change ~1 degree

		// Compute an interpolated value between minValue and maxValue, using
		// (eye altitude)/(globe radius) as
		// the interpolant. Interpolation is performed on an exponential curve,
		// to keep the value from
		// increasing too quickly as eye altitude increases.
		double a = eyePos.getElevation() / radius;
		a = (a < 0 ? 0 : (a > 1 ? 1 : a));
		double expBase = 2.0; // Exponential curve parameter.
		double value = minValue + (maxValue - minValue)
				* ((Math.pow(expBase, a) - 1.0) / (expBase - 1.0));

		return Angle.fromDegrees(value * pickDistanceFactor * panStep);
	}

	protected Angle computeLookHeading(OrbitView view,
			ScreenAnnotation control, double headingStep) {
		// Compute last pick point 'heading' relative to look control center on
		// x
		double size = control.getAttributes().getSize().width
				* control.getAttributes().getScale();
		Vec4 center = new Vec4(control.getScreenPoint().x,
				control.getScreenPoint().y + size / 2, 0);
		double px = lastPickPoint.x - center.x;
		double pickDistanceFactor = Math.min(Math.abs(px) / 3000, 5)
				* Math.signum(px);
		// New heading
		Angle heading = view.getHeading().add(
				Angle.fromRadians(headingStep * pickDistanceFactor));
		heading = heading.degrees >= 0 ? heading : heading.addDegrees(360);
		return heading;
	}

	protected Angle computeLookPitch(OrbitView view, ScreenAnnotation control,
			double pitchStep) {
		// Compute last pick point 'pitch' relative to look control center on y
		double size = control.getAttributes().getSize().width
				* control.getAttributes().getScale();
		Vec4 center = new Vec4(control.getScreenPoint().x,
				control.getScreenPoint().y + size / 2, 0);
		double py = view.getViewport().getHeight() - lastPickPoint.y - center.y;
		double pickDistanceFactor = Math.min(Math.abs(py) / 3000, 5)
				* Math.signum(py);
		// New pitch
		Angle pitch = view.getPitch().add(
				Angle.fromRadians(pitchStep * pickDistanceFactor));
		pitch = pitch.degrees >= 0 ? (pitch.degrees <= 90 ? pitch : Angle
				.fromDegrees(90)) : Angle.ZERO;
		return pitch;
	}

	/**
	 * Reset the view to an orbit view state if in first person mode (zoom = 0)
	 *
	 * @param view
	 *            the orbit view to reset
	 */
	protected void resetOrbitView(OrbitView view) {
		if (view.getZoom() > 0) // already in orbit view mode
			return;

		// Find out where on the terrain the eye is looking at in the viewport
		// center
		// TODO: if no terrain is found in the viewport center, iterate toward
		// viewport bottom until it is found
		Vec4 centerPoint = computeSurfacePoint(view, view.getHeading(),
				view.getPitch());
		// Reset the orbit view center point heading, pitch and zoom
		if (centerPoint != null) {
			Vec4 eyePoint = view.getEyePoint();
			// Center pos on terrain surface
			Position centerPosition = wwd.getModel().getGlobe()
					.computePositionFromPoint(centerPoint);
			// Compute pitch and heading relative to center position
			Vec4 normal = wwd
					.getModel()
					.getGlobe()
					.computeSurfaceNormalAtLocation(
							centerPosition.getLatitude(),
							centerPosition.getLongitude());
			Vec4 north = wwd
					.getModel()
					.getGlobe()
					.computeNorthPointingTangentAtLocation(
							centerPosition.getLatitude(),
							centerPosition.getLongitude());
			// Pitch
			view.setPitch(Angle.POS180.subtract(view.getForwardVector()
					.angleBetween3(normal)));
			// Heading
			Vec4 perpendicular = view.getForwardVector().perpendicularTo3(
					normal);
			Angle heading = perpendicular.angleBetween3(north);
			double direction = Math.signum(-normal.cross3(north).dot3(
					perpendicular));
			view.setHeading(heading.multiply(direction));
			// Zoom
			view.setZoom(eyePoint.distanceTo3(centerPoint));
			// Center pos
			view.setCenterPosition(centerPosition);
		}
	}

	/**
	 * Setup the view to a first person mode (zoom = 0)
	 *
	 * @param view
	 *            the orbit view to set into a first person view.
	 */
	protected void setupFirstPersonView(OrbitView view) {
		if (view.getZoom() == 0) // already in first person mode
			return;

		Vec4 eyePoint = view.getEyePoint();
		// Center pos at eye pos
		Position centerPosition = wwd.getModel().getGlobe()
				.computePositionFromPoint(eyePoint);
		// Compute pitch and heading relative to center position
		Vec4 normal = wwd
				.getModel()
				.getGlobe()
				.computeSurfaceNormalAtLocation(centerPosition.getLatitude(),
						centerPosition.getLongitude());
		Vec4 north = wwd
				.getModel()
				.getGlobe()
				.computeNorthPointingTangentAtLocation(
						centerPosition.getLatitude(),
						centerPosition.getLongitude());
		// Pitch
		view.setPitch(Angle.POS180.subtract(view.getForwardVector()
				.angleBetween3(normal)));
		// Heading
		Vec4 perpendicular = view.getForwardVector().perpendicularTo3(normal);
		Angle heading = perpendicular.angleBetween3(north);
		double direction = Math.signum(-normal.cross3(north)
				.dot3(perpendicular));
		view.setHeading(heading.multiply(direction));
		// Zoom
		view.setZoom(0);
		// Center pos
		view.setCenterPosition(centerPosition);
	}

	/**
	 * Find out where on the terrain surface the eye would be looking at with
	 * the given heading and pitch angles.
	 *
	 * @param view
	 *            the orbit view
	 * @param heading
	 *            heading direction clockwise from north.
	 * @param pitch
	 *            view pitch angle from the surface normal at the center point.
	 *
	 * @return the terrain surface point the view would be looking at in the
	 *         viewport center.
	 */
	protected Vec4 computeSurfacePoint(OrbitView view, Angle heading,
			Angle pitch) {
		Globe globe = wwd.getModel().getGlobe();
		// Compute transform to be applied to north pointing Y so that it would
		// point in the view direction
		// Move coordinate system to view center point
		Matrix transform = globe.computeSurfaceOrientationAtPosition(view
				.getCenterPosition());
		// Rotate so that the north pointing axes Y will point in the look at
		// direction
		transform = transform.multiply(Matrix.fromRotationZ(heading
				.multiply(-1)));
		transform = transform.multiply(Matrix.fromRotationX(Angle.NEG90
				.add(pitch)));
		// Compute forward vector
		Vec4 forward = Vec4.UNIT_Y.transformBy4(transform);
		// Return intersection with terrain
		Intersection[] intersections = wwd.getSceneController().getTerrain()
				.intersect(new Line(view.getEyePoint(), forward));
		return (intersections != null && intersections.length != 0) ? intersections[0]
				.getIntersectionPoint() : null;
	}

}
