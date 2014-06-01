package battleSystemApp.core;

/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

import gov.nasa.worldwind.Configuration;
import battleSystemApp.utils.*;

import java.awt.*;
import java.net.Authenticator;
import java.util.logging.Level;

/**
 * @author tag
 * @version $Id: WorldWindow.java 1171 2013-02-11 21:45:02Z dcollins $
 */
public class BMS
{
    static
    {
        System.setProperty("gov.nasa.worldwind.app.config.document",
            "configuration/worldwindow.worldwind.xml");
        if (Configuration.isMacOS())
        {
        	System.setProperty(
					"com.apple.mrj.application.apple.menu.about.name", "BMS");
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.growbox.intrudes", "false");
            String s = Configuration.getStringValue(Constants.APPLICATION_DISPLAY_NAME);
        }
        else if (Configuration.isWindowsOS())
        {
        	ProxyConfigurationManager proxyConfigurationManager = new ProxyConfigurationManager();
    		// Autenticamos la app contra el proxy
    		Authenticator
    				.setDefault(new ProxyAuthenticator(
    						proxyConfigurationManager
    								.getProperty(proxyConfigurationManager.PROXY_USERNAME),
    						proxyConfigurationManager
    								.getProperty(proxyConfigurationManager.PROXY_PASSWORD)));
    		System.getProperties().put("http.proxyHost", proxyConfigurationManager
    				.getProperty(proxyConfigurationManager.PROXY_HOST));
    		System.getProperties().put("http.proxyPort", proxyConfigurationManager
    				.getProperty(proxyConfigurationManager.PROXY_PORT));
    		System.getProperties().put("https.proxyHost", proxyConfigurationManager
    				.getProperty(proxyConfigurationManager.PROXY_HOST));
    		System.getProperties().put("https.proxyPort",proxyConfigurationManager
    				.getProperty(proxyConfigurationManager.PROXY_PORT));
            System.setProperty("sun.awt.noerasebackground", "true"); // prevents flashing during window resizing
        }
    }

    private static final String APP_CONFIGURATION
        = "configuration/AppConfiguration.xml";

    public static void main(String[] args)
    {
        Controller controller = new Controller();

        Dimension appSize = null;
        if (args.length >= 2) // The first two arguments are the application width and height.
            appSize = new Dimension(Integer.parseInt(args[0]), Integer.parseInt(args[1]));

        try
        {
            controller.start(APP_CONFIGURATION, appSize);
        }
        catch (Exception e)
        {
            String msg = "Fatal application error";
            controller.showErrorDialog(null, "Cannot Start Application", msg);
            Util.getLogger().log(Level.SEVERE, msg);
        }
    }
}
