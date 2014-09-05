package battleSystemApp.features;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import gov.nasa.worldwind.WWObjectImpl;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.PreRenderable;
import gov.nasa.worldwind.util.tree.Scrollable;
/**
 * Ventana scrollable con las operaciones para informar sobre unidades
 * @author vgonllo
 *
 */
public class UnitsWindow extends WWObjectImpl implements Scrollable, PreRenderable{

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

}
