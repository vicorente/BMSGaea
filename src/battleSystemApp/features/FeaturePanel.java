/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package battleSystemApp.features;


import javax.swing.*;

import battleSystemApp.core.WWOPanel;

/**
 * @author tag
 * @version $Id: FeaturePanel.java 1171 2013-02-11 21:45:02Z dcollins $
 */
public interface FeaturePanel extends WWOPanel, Feature
{
    JComponent[] getDialogControls();
}
