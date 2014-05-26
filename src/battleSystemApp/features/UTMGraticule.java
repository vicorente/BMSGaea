/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package battleSystemApp.features;

import gov.nasa.worldwind.layers.Earth.UTMGraticuleLayer;
import gov.nasa.worldwind.layers.Layer;
import battleSystemApp.core.*;

/**
 * @author tag
 * @version $Id: UTMGraticule.java 1171 2013-02-11 21:45:02Z dcollins $
 */
public class UTMGraticule extends GraticuleLayer
{
    public UTMGraticule()
    {
        this(null);
    }

    public UTMGraticule(Registry registry)
    {
        super("UTM Graticule", Constants.FEATURE_UTM_GRATICULE, null, null, registry);
    }

    @Override
    protected Layer doCreateLayer()
    {
        return new UTMGraticuleLayer();
    }
}
