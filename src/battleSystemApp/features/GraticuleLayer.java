/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package battleSystemApp.features;

import battleSystemApp.core.Registry;
import battleSystemApp.core.layermanager.LayerPath;
import gov.nasa.worldwind.layers.Layer;


/**
 * @author tag
 * @version $Id: GraticuleLayer.java 1171 2013-02-11 21:45:02Z dcollins $
 */
public abstract class GraticuleLayer extends AbstractOnDemandLayerFeature
{
    protected abstract Layer doCreateLayer();

    public GraticuleLayer(String name, String featureID, String iconPath, String group, Registry registry)
    {
        super(name, featureID, iconPath, group, registry);
    }

    @Override
    protected Layer createLayer()
    {
        Layer layer = this.doCreateLayer();

        layer.setPickEnabled(false);

        return layer;
    }

    @Override
    protected void addLayer(LayerPath path)
    {
        controller.addInternalActiveLayer(this.layer);
    }

    @Override
    protected void removeLayer()
    {
        this.controller.getWWPanel().removeLayer(this.layer);
    }
}