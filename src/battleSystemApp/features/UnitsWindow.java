package battleSystemApp.features;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import gov.nasa.worldwind.WWObjectImpl;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.PreRenderable;
import gov.nasa.worldwind.util.tree.ScrollFrame;
import gov.nasa.worldwind.util.tree.Scrollable;
import gov.nasa.worldwind.util.tree.TreeAttributes;
import gov.nasa.worldwind.util.tree.TreeNode;
import gov.nasa.worldwind.util.tree.TreePath;
/**
 * Ventana scrollable con las operaciones para informar sobre unidades
 * @author vgonllo
 *
 */
public class UnitsWindow extends WWObjectImpl implements Scrollable, PreRenderable{

    /**
     * This field is set by {@link #makeVisible(TreePath)}, and read by {@link #scrollToNode(gov.nasa.worldwind.render.DrawContext)}
     * during rendering.
     */
    protected TreeNode scrollToNode;
    
    /** Frame that contains the tree. */
    protected ScrollFrame frame;
	@Override
	public void preRender(DrawContext dc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void renderScrollable(DrawContext dc, Point location,
			Dimension frameSize, Rectangle clipBounds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Dimension getSize(DrawContext dc, Dimension frameSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setHighlighted(boolean highlighted) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getUpdateTime() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	 /**
     * Scroll the frame to make a the node set in {@link #scrollToNode} node visible. Does nothing if {@link
     * #scrollToNode} is null.
     *
     * @param dc Draw context.
     */
    protected synchronized void scrollToNode(DrawContext dc)
    {
        if (this.scrollToNode != null)
        {
            // Update the frame bounds to make sure that the frame's scroll model includes the full extent of the tree
            ScrollFrame frame = this.getFrame();
            frame.updateBounds(dc);

            Point drawPoint = new Point(0, 0);
            Rectangle bounds = this.findNodeBounds(this.scrollToNode, this.tree.getModel().getRoot(), dc,
                frame.getBounds(dc).getSize(), drawPoint, 1);

            // Calculate a scroll position that will bring the node to the top of the visible area. Subtract the row spacing
            // to avoid clipping off the top of the node.
            int scroll = (int) Math.abs(bounds.getMaxY()) - this.getActiveAttributes().getRowSpacing();
            this.frame.getScrollBar(AVKey.VERTICAL).setValue(scroll);

            this.scrollToNode = null;
        }
    }
    
    public ScrollFrame getFrame()
    {
        return this.frame;
    }

    protected TreeAttributes getActiveAttributes()
    {
        return this.activeAttributes;
    }
}
