package battleSystemApp.utils;

import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import gov.nasa.worldwind.render.AnnotationAttributes;
import gov.nasa.worldwind.render.Highlightable;
import gov.nasa.worldwind.render.ScreenAnnotation;

public class HighlightableScreenAnnotation extends ScreenAnnotation implements
		Highlightable {
	protected boolean highlighted;
	protected boolean armed = false;
	protected AbstractAction action=null;
	
	public HighlightableScreenAnnotation(String text, Point position) {
		super(text, position);
	}

	public HighlightableScreenAnnotation(String nOTEXT, Point oRIGIN,
			AnnotationAttributes ca) {
		super(nOTEXT, oRIGIN, ca);
	}

	@Override
	public boolean isHighlighted() {
		return highlighted;
	}

	/**
	 * Al hacer clic sobre el elemento lo activa (Armed).
	 * 
	 */
	@Override
	public synchronized void setHighlighted(boolean highlighted) {
		if (highlighted == true && this.isArmed() == true) {
			this.getAttributes().setImageOpacity(1);
			this.highlighted = true;
		} else {
			if (!isArmed() == true) {
				this.getAttributes().setImageOpacity(-1);
				this.highlighted = false;
			}
		}
	}

	public boolean isArmed() {
		return armed;
	}

	public synchronized void setArmed(boolean armed) {
		this.armed = armed;
	}

	public void setAction(AbstractAction action) {
		this.action = action;
	}
	// Ejecuta la acción determinada para este control
	public void doClick(){
		if(this.action!=null){
			if (isArmed()==true){
				setArmed(false);
				setHighlighted(false);				
			} else {
				setArmed(true);
				setHighlighted(true);
			}
			this.action.actionPerformed(new ActionEvent(this, 0, "newUnit"));
		}
	}

}
