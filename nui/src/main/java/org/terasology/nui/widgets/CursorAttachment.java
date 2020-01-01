/*
 * Copyright 2014 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.nui.widgets;

import org.terasology.input.device.MouseDevice;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Vector2i;
import org.terasology.nui.Canvas;
import org.terasology.nui.CoreWidget;
import org.terasology.nui.FocusManager;
import org.terasology.nui.InteractionListener;
import org.terasology.nui.LayoutConfig;
import org.terasology.nui.SubRegion;
import org.terasology.nui.UIWidget;
import org.terasology.nui.events.NUIMouseClickEvent;
import org.terasology.nui.events.NUIMouseDoubleClickEvent;
import org.terasology.nui.events.NUIMouseDragEvent;
import org.terasology.nui.events.NUIMouseOverEvent;
import org.terasology.nui.events.NUIMouseReleaseEvent;
import org.terasology.nui.events.NUIMouseWheelEvent;
import org.terasology.nui.skin.UIStyle;

/**
 */
public class CursorAttachment extends CoreWidget implements InteractionListener {
    private static final int MOUSE_CURSOR_HEIGHT = 18;

    @LayoutConfig
    private UIWidget attachment;

    private Vector2i mousePosition;
    private boolean mouseVisible;

    public UIWidget getAttachment() {
        return attachment;
    }

    public void setAttachment(UIWidget attachment) {
        this.attachment = attachment;
    }

    @Override
    public void onDraw(Canvas canvas) {
        UIStyle style = canvas.getCurrentStyle();
        Vector2i attachmentSize = canvas.calculatePreferredSize(attachment);
        attachmentSize.add(style.getMargin().getTotals());

        int top;
        switch (style.getVerticalAlignment()) {
            case TOP:
                top = mousePosition.y - attachmentSize.y;
                break;
            case MIDDLE:
                top = mousePosition.y - attachmentSize.y / 2;
                break;
            default:
                top = mousePosition.y + MOUSE_CURSOR_HEIGHT;
                break;
        }
        top = TeraMath.clamp(top, 0, canvas.size().y - attachmentSize.y);
        int left;
        switch (style.getHorizontalAlignment()) {
            case RIGHT:
                left = mousePosition.x - attachmentSize.x;
                break;
            case CENTER:
                left = mousePosition.x - attachmentSize.x / 2;
                break;
            default:
                left = mousePosition.x;
                break;
        }
        left = TeraMath.clamp(left, 0, canvas.size().x - attachmentSize.x);


        try (SubRegion ignored = canvas.subRegion(Rect2i.createFromMinAndSize(left, top, attachmentSize.x, attachmentSize.y), false)) {
            canvas.drawBackground();
            canvas.drawWidget(attachment, style.getBackgroundBorder().shrink(canvas.getRegion()));
        }
    }

    @Override
    public boolean isSkinAppliedByCanvas() {
        return false;
    }

    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i sizeHint) {
        return canvas.calculateRestrictedSize(attachment, sizeHint);
    }

    @Override
    public boolean isVisible() {
        return super.isVisible() && mouseVisible && getAttachment() != null && getAttachment().isVisible();
    }

    @Override
    public void setFocusManager(FocusManager focusManager) {

    }

    /**
     * Called every frame the mouse is over the interaction region
     *
     * @param event
     */
    @Override
    public void onMouseOver(NUIMouseOverEvent event) {
        mousePosition = event.getMouse().getPosition();
        mouseVisible = event.getMouse().isVisible();
    }

    /**
     * Called if the mouse ceases to be over the interaction region
     */
    @Override
    public void onMouseLeave() {

    }

    /**
     * Called when the mouse is clicked over an interaction region associated with this listener
     *
     * @param event
     * @return Whether the mouse input should be consumed, and thus not propagated to other interaction regions
     */
    @Override
    public boolean onMouseClick(NUIMouseClickEvent event) {
        return false;
    }

    /**
     * Called when the mouse is double-clicked over an interaction region associated with this listener.
     * Double clicks occur if the same mouse button is clicked twice with minimal movement and over the same region in a short period.
     *
     * @param event
     * @return Whether the input should be consumed, and thus not propagated to other interaction regions
     */
    @Override
    public boolean onMouseDoubleClick(NUIMouseDoubleClickEvent event) {
        return false;
    }

    /**
     * Called when the mouse is moved after clicking on the interaction region
     *
     * @param event
     */
    @Override
    public void onMouseDrag(NUIMouseDragEvent event) {

    }

    /**
     * Called when the mouse is wheeled while over the interaction region
     *
     * @param event
     * @return Whether the mouse input should be consumed, and thus not propagated to other interaction regions
     */
    @Override
    public boolean onMouseWheel(NUIMouseWheelEvent event) {
        return false;
    }

    /**
     * Called when the mouse is released after clicking on the interaction region
     *
     * @param event
     */
    @Override
    public void onMouseRelease(NUIMouseReleaseEvent event) {

    }

    /**
     * @return True if the mouse was over the interaction region last frame
     */
    @Override
    public boolean isMouseOver() {
        return false;
    }
}
