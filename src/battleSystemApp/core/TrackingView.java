package battleSystemApp.core;

import java.awt.Rectangle;
import java.util.ArrayList;

import gov.nasa.worldwind.Disposable;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.animation.BasicAnimator;
import gov.nasa.worldwind.animation.Interpolator;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.ExtentHolder;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.symbology.AbstractTacticalSymbol;
import gov.nasa.worldwind.util.WWMath;
import gov.nasa.worldwind.view.orbit.OrbitView;
import gov.nasa.worldwindx.examples.util.ExtentVisibilitySupport;
import battleSystemApp.features.AbstractFeature;

public class TrackingView extends AbstractFeature implements Disposable {
	
	protected static final double SMOOTHING_FACTOR = 0.96;

	protected boolean enabled = true;
	protected ViewAnimator animator;
	protected Iterable<?> objectsToTrack;

	public TrackingView(Registry registry) {
		super("Tracking View", Constants.FEATURE_TRACKING_VIEW, registry);
	}
	
	protected TrackingView(String s, String featureID, Registry registry) {
		super(s, featureID, registry);
		// TODO Auto-generated constructor stub
	}

	public void initialize(Controller controller) {
		super.initialize(controller);

	}
	
	@Override
	public void dispose() {
		//TODO eliminar recursos
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;

		if (this.animator != null) {
			this.animator.stop();
			this.animator = null;
		}
	}

	public Iterable<?> getObjectsToTrack() {
		return this.objectsToTrack;
	}

	public void setObjectsToTrack(Iterable<?> iterable) {
		this.objectsToTrack = iterable;
	}

	public boolean isSceneContained(View view) {
		ExtentVisibilitySupport vs = new ExtentVisibilitySupport();
		this.addExtents(vs);

		return vs.areExtentsContained(view);
	}

	public Vec4[] computeViewLookAtForScene(View view) {
		Globe globe = controller.getWWd().getModel().getGlobe();
		double ve = controller.getWWd().getSceneController().getVerticalExaggeration();

		ExtentVisibilitySupport vs = new ExtentVisibilitySupport();
		this.addExtents(vs);

		return vs.computeViewLookAtContainingExtents(globe, ve, view);
	}

	public Position computePositionFromPoint(Vec4 point) {
		return controller.getWWd().getModel().getGlobe().computePositionFromPoint(point);
	}

	public void gotoScene() {
		Vec4[] lookAtPoints = this
				.computeViewLookAtForScene(controller.getWWd().getView());
		if (lookAtPoints == null || lookAtPoints.length != 3)
			return;

		Position centerPos = controller.getWWd().getModel().getGlobe()
				.computePositionFromPoint(lookAtPoints[1]);
		double zoom = lookAtPoints[0].distanceTo3(lookAtPoints[1]);

		controller.getWWd().getView().stopAnimations();
		controller.getWWd().getView().goTo(centerPos, zoom);
	}

	public void sceneChanged() {
		OrbitView view = (OrbitView) controller.getWWd().getView();

		if (!this.isEnabled())
			return;

		if (this.isSceneContained(view))
			return;

		if (this.animator == null || !this.animator.hasNext()) {
			this.animator = new ViewAnimator(SMOOTHING_FACTOR, view, this);
			this.animator.start();
			view.stopAnimations();
			view.addAnimator(this.animator);
			view.firePropertyChange(AVKey.VIEW, null, view);
		}
	}

	protected void addExtents(ExtentVisibilitySupport vs) {
		// Compute screen extents for WWIcons which have feedback information
		// from their IconRenderer.
		Iterable<?> iterable = this.getObjectsToTrack();
		if (iterable == null)
			return;

		ArrayList<ExtentHolder> extentHolders = new ArrayList<ExtentHolder>();
		ArrayList<ExtentVisibilitySupport.ScreenExtent> screenExtents = new ArrayList<ExtentVisibilitySupport.ScreenExtent>();

		for (Object o : iterable) {
			if (o == null)
				continue;

			if (o instanceof ExtentHolder) {
				extentHolders.add((ExtentHolder) o);
			} else if (o instanceof AbstractTacticalSymbol) {
				AbstractTacticalSymbol avl = (AbstractTacticalSymbol) o;
				Rectangle screenExtent = avl.computeScreenExtent();
				
				screenExtents
						.add(new ExtentVisibilitySupport.ScreenExtent(
								avl.getScreenPoint(),
								screenExtent));
			}
		}

		if (!extentHolders.isEmpty()) {
			Globe globe = controller.getWWd().getModel().getGlobe();
			double ve = controller.getWWd().getSceneController().getVerticalExaggeration();
			vs.setExtents(ExtentVisibilitySupport.extentsFromExtentHolders(
					extentHolders, globe, ve));
		}

		if (!screenExtents.isEmpty()) {
			vs.setScreenExtents(screenExtents);
		}
	}

	/**********************************************************/
	private class ViewAnimator extends BasicAnimator
	{
		private static final double LOCATION_EPSILON = 1.0e-9;
	    private static final double ALTITUDE_EPSILON = 0.1;

	    private OrbitView view;
	    private boolean haveTargets;
	    private Position centerPosition;
	    private double zoom;
		private TrackingView viewController;

	    public ViewAnimator(final double smoothing, OrbitView view, TrackingView viewController)
	    {
	        super(new Interpolator()
	        {
	            public double nextInterpolant()
	            {
	                return 1d - smoothing;
	            }
	        });

	        this.view = view;
	        this.viewController = viewController;
	    }

	    public void stop()
	    {
	        super.stop();
	        this.haveTargets = false;
	    }

	    protected void setImpl(double interpolant)
	    {
	        this.updateTargetValues();

	        if (!this.haveTargets)
	        {
	            this.stop();
	            return;
	        }

	        if (this.valuesMeetCriteria(this.centerPosition, this.zoom))
	        {
	            this.view.setCenterPosition(this.centerPosition);
	            this.view.setZoom(this.zoom);
	            this.stop();
	        }
	        else
	        {
	            Position newCenterPos = Position.interpolateGreatCircle(interpolant, this.view.getCenterPosition(),
	                this.centerPosition);
	            double newZoom = WWMath.mix(interpolant, this.view.getZoom(), this.zoom);
	            this.view.setCenterPosition(newCenterPos);
	            this.view.setZoom(newZoom);
	        }

	        this.view.firePropertyChange(AVKey.VIEW, null, this);
	    }

	    protected void updateTargetValues()
	    {
	        if (this.viewController.isSceneContained(this.view))
	            return;

	        Vec4[] lookAtPoints = this.viewController.computeViewLookAtForScene(this.view);
	        if (lookAtPoints == null || lookAtPoints.length != 3)
	            return;

	        this.centerPosition = this.viewController.computePositionFromPoint(lookAtPoints[1]);
	        this.zoom = lookAtPoints[0].distanceTo3(lookAtPoints[1]);
	        if (this.zoom < view.getZoom())
	            this.zoom = view.getZoom();

	        this.haveTargets = true;
	    }

	    protected boolean valuesMeetCriteria(Position centerPos, double zoom)
	    {
	        Angle cd = LatLon.greatCircleDistance(this.view.getCenterPosition(), centerPos);
	        double ed = Math.abs(this.view.getCenterPosition().getElevation() - centerPos.getElevation());
	        double zd = Math.abs(this.view.getZoom() - zoom);

	        return cd.degrees < LOCATION_EPSILON
	            && ed < ALTITUDE_EPSILON
	            && zd < ALTITUDE_EPSILON;
	    }
	}
}
