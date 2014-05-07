package listeners;

import gov.nasa.worldwind.Movable;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.event.DragSelectEvent;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.geom.Intersection;
import gov.nasa.worldwind.geom.Line;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.util.BasicDragger;
import gov.nasa.worldwind.util.Logging;

/**
 * Implementa la funcionalidad de la clase Dragger de World Wind enviando la
 * actualización de las posiciones de los objetos movidos
 * 
 * @author vgonllo
 * 
 */
public class DDSDragger extends BasicDragger {

	public DDSDragger(WorldWindow wwd) {
		super(wwd);
		// TODO Auto-generated constructor stub
	}

	public DDSDragger(WorldWindow wwd, boolean useTerrain) {
		super(wwd, useTerrain);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void selected(SelectEvent event) {
		if (event == null) {
			String msg = Logging.getMessage("nullValue.EventIsNull");
			Logging.logger().severe(msg);
			throw new IllegalArgumentException(msg);
		}

		if (event.getEventAction().equals(SelectEvent.DRAG_END)) {
			this.dragging = false;
			event.consume();
			//TODO: introducir aquí los datos de comunicación DDS
		} else if (event.getEventAction().equals(SelectEvent.DRAG)) {
			DragSelectEvent dragEvent = (DragSelectEvent) event;
			Object topObject = dragEvent.getTopObject();
			if (topObject == null)
				return;

			if (!(topObject instanceof Movable))
				return;

			Movable dragObject = (Movable) topObject;
			View view = wwd.getView();
			Globe globe = wwd.getModel().getGlobe();

			// Compute dragged object ref-point in model coordinates.
			// Use the Icon and Annotation logic of elevation as offset above
			// ground when below max elevation.
			Position refPos = dragObject.getReferencePosition();
			if (refPos == null)
				return;

			Vec4 refPoint = globe.computePointFromPosition(refPos);

			if (!this.isDragging()) // Dragging started
			{
				// Save initial reference points for object and cursor in screen
				// coordinates
				// Note: y is inverted for the object point.
				this.dragRefObjectPoint = view.project(refPoint);
				// Save cursor position
				this.dragRefCursorPoint = dragEvent.getPreviousPickPoint();
				// Save start altitude
				this.dragRefAltitude = globe.computePositionFromPoint(refPoint)
						.getElevation();
			}

			// Compute screen-coord delta since drag started.
			int dx = dragEvent.getPickPoint().x - this.dragRefCursorPoint.x;
			int dy = dragEvent.getPickPoint().y - this.dragRefCursorPoint.y;

			// Find intersection of screen coord (refObjectPoint + delta) with
			// globe.
			double x = this.dragRefObjectPoint.x + dx;
			double y = event.getMouseEvent().getComponent().getSize().height
					- this.dragRefObjectPoint.y + dy - 1;
			Line ray = view.computeRayFromScreenPoint(x, y);
			Position pickPos = null;
			// Use intersection with sphere at reference altitude.
			Intersection inters[] = globe.intersect(ray, this.dragRefAltitude);
			if (inters != null)
				pickPos = globe.computePositionFromPoint(inters[0]
						.getIntersectionPoint());

			if (pickPos != null) {
				// Intersection with globe. Move reference point to the
				// intersection point,
				// but maintain current altitude.
				Position p = new Position(pickPos, dragObject
						.getReferencePosition().getElevation());
				dragObject.moveTo(p);
			}
			this.dragging = true;
			event.consume();
		}
	}

}
