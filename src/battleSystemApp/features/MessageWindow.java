package battleSystemApp.features;

import gov.nasa.worldwind.layers.*;
import battleSystemApp.components.MessagePanel;
import battleSystemApp.core.*;

/**
 * @author vgonllo
 * Crea la ventana de mensajes como un superponible en la ventana del globo
 */
public class MessageWindow extends AbstractFeatureLayer
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5727249567363991741L;
	private MessagePanel msgTree;
	public MessageWindow()
    {
        this(null);
    }

    public MessageWindow(Registry registry)
    {
        super("Message Window", Constants.FEATURE_MESSAGE_WINDOW, null, true, registry);
    }

    protected Layer doAddLayer()
    {
        this.msgTree = new MessagePanel();
        RenderableLayer hiddenLayer = new RenderableLayer();
        hiddenLayer.addRenderable(msgTree);
        hiddenLayer.setValue(Constants.ACTIVE_LAYER, true);
     
        this.controller.addInternalActiveLayer(hiddenLayer);

        return hiddenLayer;
    }
    
    /**
     * Aniade un mensaje a la ventana de mensajes
     * @param msg
     */
    public void addMessage(String msg, int messageType){
    	msgTree.addMessage(msg, messageType);
    }
}
