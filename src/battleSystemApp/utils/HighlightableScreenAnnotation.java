package battleSystemApp.utils;

import java.awt.Point;

import gov.nasa.worldwind.render.AnnotationAttributes;
import gov.nasa.worldwind.render.Highlightable;
import gov.nasa.worldwind.render.ScreenAnnotation;

public class HighlightableScreenAnnotation extends ScreenAnnotation implements
		Highlightable {
	protected boolean highlighted;

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

	@Override
	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
		if (this.highlighted == true) {
			this.getAttributes().setImageOpacity(1);
		} else {
			this.getAttributes().setImageOpacity(-1);
		}
	}

}
