package battleSystemApp.features;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.layers.Layer;
import battleSystemApp.core.Constants;
import battleSystemApp.core.Controller;
import battleSystemApp.core.Registry;

public class MilitaryControls extends AbstractFeatureLayer
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 573873613007735844L;

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
        layer.setLayout(AVKey.HORIZONTAL);

        controller.addInternalLayer(layer);

        UnitsControlsSelectListener listener = new UnitsControlsSelectListener(this.controller, layer);
        listener.setRepeatTimerDelay(30);
       
        this.controller.getWWd().addSelectListener(listener);

        return layer;
    }

    private UnitsControlLayer getLayer()
    {
        return (UnitsControlLayer) this.layer;
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

    public boolean isShowUnit()
    {
        return this.getLayer().isShowUnitControls();
    }

    public boolean isShowAlarm()
    {
        return this.getLayer().isShowAlarmControls();
    }

    public boolean isShowThreat()
    {
        return this.getLayer().isShowThreatControls();
    }

    public boolean isShowTacLine()
    {
        return this.getLayer().isShowTacLineControls();
    }
    
    public boolean isShowInstallation()
    {
        return this.getLayer().isShowInstallationControls();
    }
}
