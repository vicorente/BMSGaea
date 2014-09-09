package battleSystemApp.utils;

import gov.nasa.worldwind.WWObject;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.PreRenderable;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.util.tree.ScrollFrame;
import gov.nasa.worldwind.util.tree.Scrollable;

public interface PanelLayout extends WWObject, Renderable, PreRenderable, Scrollable{
	 /**
     * Render a ScrollFrame
     *
     * @param dc Draw context to draw in.
     */
    void render(DrawContext dc);

    /**
     * Set the ScrollFrameAttributes .
     *
     * @param attributes New ScrollFrameAttributes.
     *
     * @see #getAttributes()
     */
    void setAttributes(ScrollFrameAttributes attributes);

    /**
     * Get the ScrollFrame attributes.
     *
     * @return ScrollFrame attributes.
     *
     * @see #setAttributes(ScrollFrameAttributes)
     */
    ScrollFrameAttributes getAttributes();

    ScrollFrame getFrame();

	void setHighlightAttributes(BasicScrollFrameAttributes highlightAttributes);

}
