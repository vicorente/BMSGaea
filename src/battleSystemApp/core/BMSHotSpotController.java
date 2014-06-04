package battleSystemApp.core;

import gov.nasa.worldwind.Disposable;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.pick.PickedObject;
import gov.nasa.worldwind.pick.PickedObjectList;
import gov.nasa.worldwind.util.HotSpot;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import battleSystemApp.features.AbstractFeature;
import battleSystemApp.utils.Util;
/**
 * Controla los eventos de teclado y ratón para los menús flotantes en pantalla
 * @author vgonllo
 *
 */
public class BMSHotSpotController extends AbstractFeature implements SelectListener, MouseMotionListener, Disposable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -5074483044888873947L;
	protected HotSpot activeHotSpot;
    protected boolean dragging = false;
    /** Indicates that the active HotSpot has set a custom cursor that must be reset when the HotSpot is deactivated. */
    protected boolean customCursor;
    
    public BMSHotSpotController(Registry registry) {
		super("BMSHotSpotController", Constants.FEATURE_HOTSPOT_CONTROLLER, registry);
		
	}
    
    public void initialize(Controller controller) {
		super.initialize(controller);
		controller.getWWd().addSelectListener(this);
		controller.getWWd().getInputHandler().addMouseMotionListener(this);		
	}

    public void dispose() {
		controller.getWWd().removeSelectListener(this);
	}
	 /**
     * Updates the active {@link gov.nasa.worldwind.util.HotSpot} if necessary, and forwards the select event to the
     * active HotSpot. This does nothing if the select event is {@code null}.
     * <p/>
     * This forwards the select event to {@link #doSelected(gov.nasa.worldwind.event.SelectEvent)}, and catches and logs
     * any exceptions thrown by {@code doSelected}.
     *
     * @param event A select event on the World Window we're monitoring.
     */
    public void selected(SelectEvent event)
    {
        if (event == null)
            return;

        try
        {
        	
            this.doSelected(event);
        }
        catch (Exception e)
        {
            // Wrap the handler in a try/catch to keep exceptions from bubbling up.
        	Util.getLogger().warning(e.getMessage() != null ? e.getMessage() : e.toString());
        }
    }

    /**
     * Updates the active {@link gov.nasa.worldwind.util.HotSpot} depending on the specified select event action: <ul>
     * <li>{@link gov.nasa.worldwind.event.SelectEvent#DRAG_END} - Forwards the event to the active {@link
     * gov.nasa.worldwind.util.HotSpot}, then updates the active HotSpot.</li> <li>{@link
     * gov.nasa.worldwind.event.SelectEvent#ROLLOVER} while not dragging - Updates the active HotSpot, then forwards the
     * event to the active HotSpot.</li> <li>Other event types - forwards the event to the active HotSpot</li> </ul>
     *
     * @param event A select event on the World Window we're monitoring.
     */
    protected void doSelected(SelectEvent event)
    {
        HotSpot activeHotSpot = this.getActiveHotSpot();

        if (event.isDragEnd())
        {
            // Forward the drag end event to the active HotSpot (if any), and mark the controller as not dragging.
            // We forward the drag end event here because the active HotSpot potentially changes on a drag end, and
            // the currently active HotSpot might need to know the drag ended.
            if (activeHotSpot != null)
                activeHotSpot.selected(event);

            this.setDragging(false);

            PickedObjectList list = controller.getWWd().getObjectsAtCurrentPosition();
            PickedObject po = list != null ? list.getTopPickedObject() : null;

            this.updateActiveHotSpot(po);
        }
        else if (!this.isDragging() && event.isRollover())
        {
            // Update the active HotSpot and the currently displayed cursor on drag end events and and on rollover
            // events when we're not dragging. This ensures that the active HotSpot remains active while it's being
            // dragged, regardless of what's under the cursor.
            PickedObject po = event.getTopPickedObject();
            this.updateActiveHotSpot(po);
        }

        // Forward the event to the active HotSpot
        if (activeHotSpot != null)
        {
            if (event.isDrag())
            {
                boolean wasConsumed = event.isConsumed();

                // Forward the drag event to the active HotSpot. If the HotSpot consumes the event, track that the
                // HotSpot is dragging so that we can continue to deliver events to the HotSpot for the duration of the drag.
                activeHotSpot.selected(event);
                this.setDragging(event.isConsumed() && !wasConsumed);
            }
            else if (!event.isDragEnd())
            {
                // Forward all other the select event (except drag end) to the active HotSpot. We ignore drag end events
                // because we've already forwarded them to the previously active HotSpot in the logic above.
                activeHotSpot.selected(event);
            }
        }
    }

    /**
     * Update the cursor when the mouse moves.
     *
     * @param e Mouse event.
     */
    public void mouseMoved(MouseEvent e)
    {
        // Give the active HotSpot a chance to set a custom cursor based on the new mouse position.
        HotSpot hotSpot = this.getActiveHotSpot();
        if (hotSpot != null)
        {
            Cursor cursor = hotSpot.getCursor();

            if (cursor != null)
            {
                ((Component) controller.getWWd()).setCursor(cursor);
                this.customCursor = true;
            }
        }
    }

    /**
     * Returns whether the user is dragging the object under the cursor.
     *
     * @return {@code true} if the user is dragging the object under the cursor, otherwise {@code false}.
     */
    protected boolean isDragging()
    {
        return this.dragging;
    }

    /**
     * Specifies whether the user is dragging the object under the cursor.
     *
     * @param dragging {@code true} if the user is dragging the object under the cursor, otherwise {@code false}.
     */
    protected void setDragging(boolean dragging)
    {
        this.dragging = dragging;
    }

    /**
     * Returns the currently active {@link gov.nasa.worldwind.util.HotSpot}, or {@code null} if there is no active
     * HotSpot.
     *
     * @return The currently active HotSpot, or {@code null}.
     */
    protected HotSpot getActiveHotSpot()
    {
        return this.activeHotSpot;
    }

    /**
     * Sets the active {@link gov.nasa.worldwind.util.HotSpot} to the specified HotSpot. The HotSpot may be {@code
     * null}, indicating that there is no active HotSpot. This registers the new HotSpot as key listener, mouse
     * listener, mouse motion listener, and mouse wheel listener on the World Window's {@link
     * gov.nasa.worldwind.event.InputHandler}. This removes the previously active HotSpot as a listener on the World
     * Window's InputHandler. This does nothing if the active HotSpot and the specified HotSpot are the same object.
     * </p> Additionally, this updates the World Window's {@link java.awt.Cursor} to the value returned by {@code
     * hotSpot.getCursor()}, or {@code null} if the specified hotSpot is {@code null}.
     *
     * @param hotSpot The HotSpot that becomes the active HotSpot. {@code null} to indicate that there is no active
     *                HotSpot.
     */
    protected void setActiveHotSpot(HotSpot hotSpot)
    {
        // Update the World Window's cursor to the cursor associated with the active HotSpot. We
        // specify null if there's no active HotSpot, which tells the World Window to use the default
        // cursor, or inherit its cursor from the parent Component.
        if (controller.getWWd() instanceof Component)
        {
            // If the active HotSpot is changing, and a custom cursor was set by the previous HotSpot, reset the cursor.
            if (this.activeHotSpot != hotSpot && this.customCursor)
            {
                ((Component) controller.getWWd()).setCursor(null);
                this.customCursor = false;
            }

            // Give the new HotSpot a chance to set a custom cursor.
            if (hotSpot != null)
            {
                Cursor cursor = hotSpot.getCursor();

                if (cursor != null)
                {
                    ((Component) controller.getWWd()).setCursor(cursor);
                    this.customCursor = true;
                }
            }
        }

        if (this.activeHotSpot == hotSpot) // The specified HotSpot is already active.
            return;

        if (this.activeHotSpot != null)
        {
            controller.getWWd().getInputHandler().removeKeyListener(this.activeHotSpot);
            controller.getWWd().getInputHandler().removeMouseListener(this.activeHotSpot);
            controller.getWWd().getInputHandler().removeMouseMotionListener(this.activeHotSpot);
            controller.getWWd().getInputHandler().removeMouseWheelListener(this.activeHotSpot);
            this.activeHotSpot.setActive(false);
        }

        this.activeHotSpot = hotSpot;

        if (this.activeHotSpot != null)
        {
            this.activeHotSpot.setActive(true);
            controller.getWWd().getInputHandler().addKeyListener(this.activeHotSpot);
            controller.getWWd().getInputHandler().addMouseListener(this.activeHotSpot);
            controller.getWWd().getInputHandler().addMouseMotionListener(this.activeHotSpot);
            controller.getWWd().getInputHandler().addMouseWheelListener(this.activeHotSpot);
        }
    }

    /**
     * Updates the active {@link gov.nasa.worldwind.util.HotSpot} and the currently displayed cursor according to the
     * picked objects in the specified event. The active HotSpot is assigned as follows: <ul> <li>The value for {@code
     * event.getTopPickedObject().getValue(AVKey.HOT_SPOT)}, if the value for the key {@link
     * gov.nasa.worldwind.avlist.AVKey#HOT_SPOT} implements HotSpot.</li> <li>The event's top picked object, if the top
     * picked object implements HotSpot.</li> <li>{@code null} if neither of the above conditions are true, or if the
     * event is {@code null}.</li> </ul>
     *
     * @param po Top picked object, which will provide the active HotSpot.
     */
    protected void updateActiveHotSpot(PickedObject po)
    {
        if (po != null && po.getValue(AVKey.HOT_SPOT) instanceof HotSpot)
        {
            this.setActiveHotSpot((HotSpot) po.getValue(AVKey.HOT_SPOT));
        }
        else if (po != null && po.getObject() instanceof HotSpot)
        {
            this.setActiveHotSpot((HotSpot) po.getObject());
        }
        else
        {
            this.setActiveHotSpot(null);
        }
    }

    /** {@inheritDoc} */
    public void mouseDragged(MouseEvent e)
    {
        // No action
    }

}
