package battleSystemApp.features;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.ViewControlsLayer;
import java.beans.PropertyChangeEvent;

import battleSystemApp.core.Constants;
import battleSystemApp.core.Controller;
import battleSystemApp.core.Registry;

public class MilitaryControls extends AbstractFeatureLayer
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 573873613007735844L;
	public static final String POSITION_PROPERTY = "battleSystemApp.features.Navegacion.PostionProperty";
    public static final String ORIENTATION_PROPERTY = "battleSystemApp.features.Navegacion.OrientationProperty";
    public static final String SIZE_PROPERTY = "battleSystemApp.features.Navegacion.SizeProperty";
    public static final String OPACITY_PROPERTY = "battleSystemApp.features.Navegacion.OpacityProperty";

    public static final String PAN_CONTROLS_PROPERTY = "battleSystemApp.features.Navegacion.PanControlS";
    public static final String ZOOM_CONTROLS_PROPERTY = "battleSystemApp.features.Navegacion.ZoomControlS";
    public static final String TILT_CONTROLS_PROPERTY = "battleSystemApp.features.Navegacion.TiltControlS";
    public static final String HEADING_CONTROLS_PROPERTY = "battleSystemApp.features.Navegacion.HeadingControlS";

    public MilitaryControls()
    {
        this(null);
    }

    public MilitaryControls(Registry registry)
    {
        super("MilitaryControls", Constants.FEATURE_MILITARY_CONTROLS,
            "images/navegacion-64x64.png", true, registry);
    }

    public void initialize(Controller controller)
    {
        super.initialize(controller);
    }

    protected Layer doAddLayer()
    {
        UnitsControlLayer layer = new UnitsControlLayer();

        layer.setValue(Constants.SCREEN_LAYER, true);
        layer.setValue(Constants.INTERNAL_LAYER, true);
        layer.setLayout(AVKey.VERTICAL);

        controller.addInternalLayer(layer);

        UnitsControlsSelectListener listener = new UnitsControlsSelectListener(this.controller.getWWd(), layer);
        listener.setRepeatTimerDelay(30);
        listener.setZoomIncrement(0.5);
        listener.setPanIncrement(0.5);
        this.controller.getWWd().addSelectListener(listener);

        return layer;
    }

    private ViewControlsLayer getLayer()
    {
        return (ViewControlsLayer) this.layer;
    }

    @Override
    public void doPropertyChange(PropertyChangeEvent event)
    {
        if (event.getPropertyName().equals(POSITION_PROPERTY))
        {
            if (event.getNewValue() != null && event.getNewValue() instanceof String)
            {
                this.getLayer().setPosition((String) event.getNewValue());
                this.controller.redraw();
            }
        }
        else if (event.getPropertyName().equals(ORIENTATION_PROPERTY))
        {
            if (event.getNewValue() != null && event.getNewValue() instanceof String)
            {
                this.getLayer().setLayout((String) event.getNewValue());
                this.controller.redraw();
            }
        }
        else if (event.getPropertyName().equals(PAN_CONTROLS_PROPERTY))
        {
            if (event.getNewValue() != null && event.getNewValue() instanceof Boolean)
            {
                this.getLayer().setShowPanControls((Boolean) event.getNewValue());
                this.controller.redraw();
            }
        }
        else if (event.getPropertyName().equals(ZOOM_CONTROLS_PROPERTY))
        {
            if (event.getNewValue() != null && event.getNewValue() instanceof Boolean)
            {
                this.getLayer().setShowZoomControls((Boolean) event.getNewValue());
                this.controller.redraw();
            }
        }
        else if (event.getPropertyName().equals(HEADING_CONTROLS_PROPERTY))
        {
            if (event.getNewValue() != null && event.getNewValue() instanceof Boolean)
            {
                this.getLayer().setShowHeadingControls((Boolean) event.getNewValue());
                this.controller.redraw();
            }
        }
        else if (event.getPropertyName().equals(TILT_CONTROLS_PROPERTY))
        {
            if (event.getNewValue() != null && event.getNewValue() instanceof Boolean)
            {
                this.getLayer().setShowPitchControls((Boolean) event.getNewValue());
                this.controller.redraw();
            }
        }
    }

    public double getSize()
    {
        return this.layer.getScale();
    }

    public double getOpacity()
    {
        return this.layer.getOpacity();
    }

    public String getOrientation()
    {
        return this.getLayer().getLayout();
    }

    public String getPosition()
    {
        return this.getLayer().getPosition();
    }

    public boolean isShowPan()
    {
        return this.getLayer().isShowPanControls();
    }

    public boolean isShowZoom()
    {
        return this.getLayer().isShowZoomControls();
    }

    public boolean isShowTilt()
    {
        return this.getLayer().isShowPitchControls();
    }

    public boolean isShowHeading()
    {
        return this.getLayer().isShowHeadingControls();
    }
}
