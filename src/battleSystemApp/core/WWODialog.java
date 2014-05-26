/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package battleSystemApp.core;

import battleSystemApp.features.Feature;

import javax.swing.*;

/**
 * @author tag
 * @version $Id: WWODialog.java 1171 2013-02-11 21:45:02Z dcollins $
 */
public interface WWODialog extends Feature
{
    JDialog getJDialog();

    void setVisible(boolean tf);
}
