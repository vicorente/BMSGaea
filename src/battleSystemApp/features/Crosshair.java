/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package battleSystemApp.features;

import gov.nasa.worldwind.layers.*;
import battleSystemApp.core.*;

/**
 * @author tag
 * @version $Id: Crosshair.java 1171 2013-02-11 21:45:02Z dcollins $
 */
public class Crosshair extends AbstractOnDemandLayerFeature
{
    public Crosshair(Registry registry)
    {
        super("Crosshair", Constants.FEATURE_CROSSHAIR, null, null, registry);
    }

    @Override
    protected Layer createLayer()
    {
        Layer layer = this.doCreateLayer();

        layer.setPickEnabled(false);

        return layer;
    }

    protected Layer doCreateLayer()
    {
        return new CrosshairLayer();
    }

    @Override
    public void turnOn(boolean tf)
    {
        if (tf == this.on || this.layer == null)
            return;

        if (tf)
            controller.addInternalActiveLayer(this.layer);
        else
            this.controller.getActiveLayers().remove(this.layer);

        this.on = tf;
    }
}
