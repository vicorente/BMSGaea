/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package battleSystemApp.core;

import battleSystemApp.features.AbstractFeature;
import gov.nasa.worldwind.Disposable;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.event.*;

/**
 * @author tag
 * @version $Id: ExternalLinkController.java 1171 2013-02-11 21:45:02Z dcollins $
 */
public class ExternalLinkController extends AbstractFeature implements SelectListener, Disposable
{
    public ExternalLinkController(Registry registry)
    {
        super("External Link Controller", Constants.FEATURE_EXTERNAL_LINK_CONTROLLER, registry);
    }

    public void initialize(Controller controller)
    {
        super.initialize(controller);

        this.controller.getWWd().addSelectListener(this);
    }

    public void dispose()
    {
        this.controller.getWWd().removeSelectListener(this);
    }

    public void selected(SelectEvent event)
    {
        if (event.isLeftDoubleClick() && event.getTopObject() instanceof AVList)
        {
            String link = ((AVList) event.getTopObject()).getStringValue(AVKey.EXTERNAL_LINK);
            if (link == null)
                return;

            this.controller.openLink(link);
        }
    }
}
