package org.terasology.nui.util;

import org.joml.Rectanglef;
import org.joml.Rectanglei;
import org.joml.Vector2f;
import org.joml.Vector2i;

public final class RectUtility {
    private RectUtility() {
    }

    public static Rectanglei createFromMinAndSize(int minX, int minY, int width, int height) {
        return new Rectanglei(minX, minY, minX + width, minY + height);
    }

    public static Rectanglei createFromMinAndSize(Vector2i min, Vector2i size) {
        return new Rectanglei(min, min.add(size, new Vector2i()));
    }

    public static Rectanglef createFromMinAndSize(float minX, float minY, float width, float height) {
        return new Rectanglef(minX, minY, minX + width, minY + height);
    }

    public static Rectanglef createFromMinAndSize(Vector2f min, Vector2f size) {
        return new Rectanglef(min, min.add(size, new Vector2f()));
    }

    public static boolean isEmpty(Rectanglei rect) {
        return rect.lengthX() == 0 || rect.lengthY() == 0;
    }

    public static boolean isEmpty(Rectanglef rect) {
        return rect.lengthX() == 0 || rect.lengthY() == 0;
    }

    public static boolean contains(Rectanglei rect, Vector2i point) {
        return point.x >= rect.minX && point.x < rect.maxX && point.y >= rect.minY && point.y < rect.maxY;
    }
}
