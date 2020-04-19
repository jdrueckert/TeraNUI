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
package org.terasology.nui;

import org.terasology.nui.events.NUIBindButtonEvent;
import org.joml.Vector2i;
import org.terasology.nui.events.NUIKeyEvent;
import org.terasology.nui.events.NUIMouseButtonEvent;
import org.terasology.nui.events.NUIMouseWheelEvent;

import java.util.Collections;
import java.util.Iterator;

/**
 */
public abstract class CoreWidget extends AbstractWidget {

    public CoreWidget() {
    }

    public CoreWidget(String id) {
        super(id);
    }

    @Override
    public Iterator<UIWidget> iterator() {
        return Collections.emptyIterator();
    }

    @Override
    public void onMouseButtonEvent(NUIMouseButtonEvent event) {
    }

    @Override
    public void onMouseWheelEvent(NUIMouseWheelEvent event) {
    }

    @Override
    public boolean onKeyEvent(NUIKeyEvent event) {
        return super.onKeyEvent(event);
    }

    @Override
    public void onBindEvent(NUIBindButtonEvent event) {
    }

    @Override
    public Vector2i getMaxContentSize(Canvas canvas) {
        return new Vector2i(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

}
