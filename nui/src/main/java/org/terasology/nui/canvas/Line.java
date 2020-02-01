/*
 * Copyright 2013 MovingBlocks
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
package org.terasology.nui.canvas;

import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;

import java.util.Objects;

/**
 *
 */
public final class Line {

    private Line() {

    }

    public static LineCoordinates getLineCoordinates(int startX, int startY, int endX, int endY, Rect2i baseRegion, Rect2i cropRegion) {
        Rect2i region = Rect2i.createFromMinAndMax(Math.min(startX, endX), Math.min(startY, endY),
            Math.max(startX, endX), Math.max(startY, endY));
        Rect2i absoluteRegion = relativeToAbsolute(region, baseRegion);
        Rect2i finalRegion = cropRegion.intersect(absoluteRegion);

        if (!finalRegion.isEmpty()) {
            int sx = startX > endX ? finalRegion.maxX() : finalRegion.minX();
            int sy = startY > endY ? finalRegion.maxY() : finalRegion.minY();
            int ex = startX > endX ? finalRegion.minX() : finalRegion.maxX();
            int ey = startY > endY ? finalRegion.minY() : finalRegion.maxY();
            return new LineCoordinates(new Vector2i(sx, sy), new Vector2i(ex, ey));
        } else {
            return null;
        }
    }

    public static Rect2i relativeToAbsolute(Rect2i region, Rect2i baseRegion) {
        return Rect2i.createFromMinAndSize(region.minX() + baseRegion.minX(), region.minY() + baseRegion.minY(), region.width(), region.height());
    }

    /**
     * Helper class that wraps a line's start and end points.
     */
    public static class LineCoordinates {
        /**
         * The start point.
         */
        private Vector2i start;
        /**
         * The end point.
         */
        private Vector2i end;

        public LineCoordinates(Vector2i start, Vector2i end) {
            this.start = start;
            this.end = end;
        }

        public Vector2i getStart() {
            return this.start;
        }

        public Vector2i getEnd() {
            return this.end;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.start, this.end);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || !(o instanceof LineCoordinates)) {
                return false;
            }
            if (o == this) {
                return true;
            }
            LineCoordinates other = (LineCoordinates) o;
            return this.start.equals(other.start) && this.end.equals(other.end);
        }

        @Override
        public String toString() {
            return String.format("[%s %s]", this.start, this.end);
        }
    }
}
