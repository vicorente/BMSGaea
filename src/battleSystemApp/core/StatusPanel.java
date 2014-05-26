/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package battleSystemApp.core;

import battleSystemApp.features.FeaturePanel;


/**
 * @author tag
 * @version $Id: StatusPanel.java 1171 2013-02-11 21:45:02Z dcollins $
 */
public interface StatusPanel extends FeaturePanel
{
    String setStatusMessage(String message);
}
