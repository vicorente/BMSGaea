/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package battleSystemApp.core;


import javax.swing.*;

import battleSystemApp.features.AbstractFeature;

import java.awt.*;

/**
 * @author tag
 * @version $Id: AppPanelImpl.java 1171 2013-02-11 21:45:02Z dcollins $
 */
public class AppPanelImpl extends AbstractFeature implements AppPanel
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 4703645550367910482L;
	private JPanel panel;

    public AppPanelImpl(Registry registry)
    {
        super("App Panel", Constants.APP_PANEL, registry);

        this.panel = new JPanel(new BorderLayout());
        this.panel.setPreferredSize(new Dimension(1280, 800));
    }

    public void initialize(final Controller controller)
    {
        super.initialize(controller);

        Dimension appSize = controller.getAppSize();
        if (appSize != null)
            this.panel.setPreferredSize(appSize);

        WWPanel wwPanel = controller.getWWPanel();
        if (wwPanel != null)
            this.panel.add(wwPanel.getJPanel(), BorderLayout.CENTER);
        
       
        
    }
    
    public JPanel getJPanel()
    {
        return this.panel;
    }
}
