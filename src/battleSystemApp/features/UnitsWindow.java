package battleSystemApp.features;

import battleSystemApp.core.Constants;
import battleSystemApp.core.Registry;
import battleSystemApp.utils.UnitsPanel;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.Offset;

public class UnitsWindow extends AbstractFeatureLayer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8931066367593112209L;
	private UnitsPanel unitsPanel;

	protected UnitsWindow(){
		this(null);
	}
	
	public UnitsWindow(Registry registry)
    {
        super("Units Window", Constants.FEATURE_UNITS_WINDOW, null, true, registry);
    }

	@Override
	protected Layer doAddLayer()
    {
        this.unitsPanel = new UnitsPanel(Offset.RIGHT_CENTER);
        RenderableLayer hiddenLayer = new RenderableLayer();
        hiddenLayer.addRenderable(unitsPanel);
        hiddenLayer.setValue(Constants.ACTIVE_LAYER, true);
     
        this.controller.addInternalActiveLayer(hiddenLayer);

        return hiddenLayer;
    }

}
