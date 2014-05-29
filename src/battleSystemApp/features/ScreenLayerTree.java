/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package battleSystemApp.features;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.render.Offset;
import gov.nasa.worldwind.util.layertree.LayerTree;
import battleSystemApp.core.*;

/**
 * @author vgonllo
 * Crea el arbol de capas como un superponible en la ventana del globo
 */
public class ScreenLayerTree extends AbstractFeatureLayer
{
    public ScreenLayerTree()
    {
        this(null);
    }

    public ScreenLayerTree(Registry registry)
    {
        super("Screen Layer Tree", Constants.FEATURE_SCREEN_LAYER_TREE, null, true, registry);
    }

    protected Layer doAddLayer()
    {
        LayerTree layerTree= new  LayerTree(new Offset(20d, 160d, AVKey.PIXELS,
				AVKey.INSET_PIXELS));
        layerTree.getModel().refresh(this.controller.getWWd().getModel().getLayers());
        RenderableLayer hiddenLayer = new RenderableLayer();
        hiddenLayer.addRenderable(layerTree);
        hiddenLayer.setValue(Constants.SCREEN_LAYER, true);
        
        this.controller.addInternalLayer(hiddenLayer);

        return hiddenLayer;
    }
}