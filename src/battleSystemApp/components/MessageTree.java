package battleSystemApp.components;

import java.util.Map.Entry;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.render.Offset;
import gov.nasa.worldwind.util.layertree.LayerTreeModel;
import gov.nasa.worldwind.util.tree.BasicFrameAttributes;
import gov.nasa.worldwind.util.tree.BasicTree;
import gov.nasa.worldwind.util.tree.BasicTreeAttributes;
import gov.nasa.worldwind.util.tree.BasicTreeLayout;
import gov.nasa.worldwind.util.tree.BasicTreeModel;
import gov.nasa.worldwind.util.tree.BasicTreeNode;
import gov.nasa.worldwind.util.tree.TreeLayout;

/**
 * Representa una ventana superponible para mostrar mensajes
 * @author vgonllo
 *
 */
public class MessageTree extends BasicTree {
    /** La localización por defecto es la zona derecha de la pantalla */
    protected static final Offset DEFAULT_OFFSET = Offset.fromFraction(0.85, 0.74);
    protected static final String DEFAULT_FRAME_IMAGE = "images/info-20x20.png";  
    protected static final String DEFAULT_FRAME_TITLE = "Mensajes";
    protected BasicTreeLayout layout=null;
    public MessageTree(){
    	this.initialize(null);
    }
    
    public MessageTree(Offset offset){
    	this.initialize(offset);
    }
    
    protected void initialize(Offset offset)
    {
        if (model == null)
            model = this.createTreeModel();
        
        this.setModel(model);
        this.setLayout(this.createTreeLayout(offset));
        this.expandPath(this.getModel().getRoot().getPath());
    }
    
    protected LayerTreeModel createTreeModel()
    {
        return new LayerTreeModel();
    }
    
    protected TreeLayout createTreeLayout(Offset offset)
    {
        if (offset == null)
            offset = DEFAULT_OFFSET;

        layout = new BasicTreeLayout(this, offset);
        layout.getFrame().setFrameTitle(DEFAULT_FRAME_TITLE);
        layout.getFrame().setIconImageSource(DEFAULT_FRAME_IMAGE);

        BasicTreeAttributes attributes = new BasicTreeAttributes();
        attributes.setRootVisible(false);
        layout.setAttributes(attributes);

        BasicFrameAttributes frameAttributes = new BasicFrameAttributes();
        frameAttributes.setBackgroundOpacity(0.7);
        layout.getFrame().setAttributes(frameAttributes);

        BasicTreeAttributes highlightAttributes = new BasicTreeAttributes(attributes);
        layout.setHighlightAttributes(highlightAttributes);

        BasicFrameAttributes highlightFrameAttributes = new BasicFrameAttributes(frameAttributes);
        highlightFrameAttributes.setForegroundOpacity(1.0);
        highlightFrameAttributes.setBackgroundOpacity(1.0);
        layout.getFrame().setHighlightAttributes(highlightFrameAttributes);

        return layout;
    }
    
    /** {@inheritDoc} */
    public BasicTreeModel getModel()
    {
        return (BasicTreeModel) super.getModel();
    }
    
    public void addMessage(String message){
    	BasicTreeNode mensaje = new BasicTreeNode(message);
    	this.getModel().getRoot().addChild(mensaje);
    }
}
