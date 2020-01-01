/*
 * Copyright 2013 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.input.events;

import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;


public abstract class InputEvent {
    private float delta;
    private boolean consumed;

    public InputEvent(float delta) {
        this.delta = delta;
    }

    /**
     *
     * @return The time since the event was fired (also the game update loop's delta time).
     */
    public float getDelta() {
        return delta;
    }

    public boolean isConsumed() {
        return consumed;
    }

    public void consume() {
        this.consumed = true;
    }

    protected void reset(float newDelta) {
        consumed = false;
        this.delta = newDelta;
    }
}
