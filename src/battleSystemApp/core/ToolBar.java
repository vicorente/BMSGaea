/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package battleSystemApp.core;


import javax.swing.*;

import battleSystemApp.features.Feature;

/**
 * @author tag
 * @version $Id: ToolBar.java 1171 2013-02-11 21:45:02Z dcollins $
 */
public interface ToolBar
{
    JToolBar getJToolBar();

    void addFeature(Feature feature);
}
