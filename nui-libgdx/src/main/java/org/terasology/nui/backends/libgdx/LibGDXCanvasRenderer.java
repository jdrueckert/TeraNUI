/*
 * Copyright 2020 MovingBlocks
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
package org.terasology.nui.backends.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Border;
import org.terasology.math.geom.Rect2f;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;
import org.terasology.nui.Color;
import org.terasology.nui.FontColor;
import org.terasology.nui.HorizontalAlign;
import org.terasology.nui.ScaleMode;
import org.terasology.nui.UITextureRegion;
import org.terasology.nui.VerticalAlign;
import org.terasology.nui.asset.font.Font;
import org.terasology.nui.canvas.CanvasRenderer;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LibGDXCanvasRenderer implements CanvasRenderer {
    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    private int cropCount = 0;
    private int screenWidth;
    private int screenHeight;
    private boolean controlSpriteBatch;
    private boolean controlShapeRenderer;
    private Matrix4 lastMatrix;

    // NOTE: These constants were taken from Terasology's FontMeshBuilder class
    private static final int SHADOW_HORIZONTAL_OFFSET = 1;
    private static final int SHADOW_VERTICAL_OFFSET = 1;

    public LibGDXCanvasRenderer() {
        this(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new SpriteBatch(), new ShapeRenderer(), true, true);
    }

    public LibGDXCanvasRenderer(SpriteBatch spriteBatch) {
        this(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), spriteBatch, new ShapeRenderer(), false, true);
    }

    public LibGDXCanvasRenderer(int width, int height) {
        this(width, height, new SpriteBatch(), new ShapeRenderer(), true, true);
    }

    public LibGDXCanvasRenderer(int width, int height, SpriteBatch spriteBatch) {
        this(width, height, spriteBatch, new ShapeRenderer(), false, true);
    }

    public LibGDXCanvasRenderer(int width, int height, SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        this(width, height, spriteBatch, shapeRenderer, false, false);
    }

    public LibGDXCanvasRenderer(int width, int height, SpriteBatch spriteBatch, ShapeRenderer shapeRenderer, boolean controlSpriteBatch, boolean controlShapeRenderer) {
        this.spriteBatch = spriteBatch;
        this.shapeRenderer = shapeRenderer;
        this.controlSpriteBatch = controlSpriteBatch;
        this.controlShapeRenderer = controlShapeRenderer;

        resize(width, height);
    }

    public void resize(int width, int height) {
        screenWidth = width;
        screenHeight = height;

        if (controlSpriteBatch) {
            spriteBatch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, screenWidth, screenHeight));
            shapeRenderer.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, screenWidth, screenHeight));
        }
    }

    @Override
    public void preRender() {
        for (int cropNo = 0; cropNo < cropCount; cropNo++) {
            ScissorStack.popScissors();
        }
        cropCount = 0;

        if (controlSpriteBatch) {
            spriteBatch.begin();
        } else {
            lastMatrix = spriteBatch.getProjectionMatrix().cpy();
            spriteBatch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, screenWidth, screenHeight));
        }
    }

    @Override
    public void postRender() {
        spriteBatch.flush();
        if (controlSpriteBatch) {
            spriteBatch.end();
        } else {
            spriteBatch.setProjectionMatrix(lastMatrix);
        }
    }

    @Override
    public Vector2i getTargetSize() {
        return new Vector2i(screenWidth, screenHeight);
    }

    @Override
    public void crop(Rect2i cropRegion) {
        if (cropRegion.equals(Rect2i.createFromMinAndSize(Vector2i.ZERO, getTargetSize()))) {
            spriteBatch.flush();
            for (int cropNo = 0; cropNo < cropCount; cropNo++) {
                ScissorStack.popScissors();
            }
            cropCount = 0;
            return;
        }

        spriteBatch.flush();
        if (ScissorStack.pushScissors(new Rectangle(cropRegion.minX(), screenHeight - cropRegion.maxY(),
                cropRegion.width(), cropRegion.height()))) {
            cropCount++;
        } else {
            // TODO: Error Handling
            return;
        }
    }

    @Override
    public void drawLine(int sx, int sy, int ex, int ey, Color color) {
        spriteBatch.flush();
        spriteBatch.end();

        if (controlShapeRenderer) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        }

        shapeRenderer.setColor(GdxColorUtil.terasologyToGDXColor(color));
        // NOTE: The constant line width 2 is used in Terasology's rendering code (which NUI's implementation relied on)
        shapeRenderer.rectLine(sx, screenHeight - sy, ex, screenHeight - ey, 2);

        shapeRenderer.flush();
        if (controlShapeRenderer) {
            shapeRenderer.end();
        }

        spriteBatch.begin();
    }

    @Override
    public void drawTexture(UITextureRegion texture, Color color, ScaleMode mode, Rect2i absoluteRegion, float ux, float uy, float uw, float uh, float alpha) {
        if (!(texture instanceof LibGDXTexture)) {
            // TODO: Wrong rendering back-end ?
            return;
        }

        LibGDXTexture gdxTexture = (LibGDXTexture) texture;

        spriteBatch.setColor(new com.badlogic.gdx.graphics.Color(color.rf(), color.gf(), color.bf(), color.af() * alpha));

        Texture.TextureWrap previousUWrap = gdxTexture.getGdxTexture().getTexture().getUWrap();
        Texture.TextureWrap previousVWrap = gdxTexture.getGdxTexture().getTexture().getVWrap();

        switch (mode) {
            case STRETCH:
                Vector2 stretchRegion = Scaling.stretch.apply(uw * texture.getWidth(), uh * texture.getHeight(), absoluteRegion.width(), absoluteRegion.height());
                absoluteRegion = Rect2i.createFromMinAndSize(absoluteRegion.minX(), absoluteRegion.minY(), (int)stretchRegion.x, (int)stretchRegion.y);
                break;
            case TILED:
                gdxTexture.getGdxTexture().getTexture().setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
                break;
            case SCALE_FIT:
                Vector2 fitRegion = Scaling.fit.apply(uw * texture.getWidth(), uh * texture.getHeight(), absoluteRegion.width(), absoluteRegion.height());
                absoluteRegion = Rect2i.createFromMinAndSize(absoluteRegion.minX(), absoluteRegion.minY(), (int)fitRegion.x, (int)fitRegion.y);
                break;
            case SCALE_FILL:
                Vector2 fillRegion = Scaling.fill.apply(uw * texture.getWidth(), uh * texture.getHeight(), absoluteRegion.width(), absoluteRegion.height());
                absoluteRegion = Rect2i.createFromMinAndSize(absoluteRegion.minX(), absoluteRegion.minY(), (int)fillRegion.x, (int)fillRegion.y);
                break;
        }

        Rect2f textureOffset = texture.getRegion();
        spriteBatch.draw(gdxTexture.getGdxTexture().getTexture(), absoluteRegion.minX(),
                screenHeight - absoluteRegion.minY() - absoluteRegion.height(),
                absoluteRegion.width(), absoluteRegion.height(),
                (int) (ux * gdxTexture.getWidth()), (int) (uy * gdxTexture.getHeight()),
                (int) (textureOffset.minX() + uw * gdxTexture.getWidth()), (int) (textureOffset.minY() + uh * gdxTexture.getHeight()), false, false);

        gdxTexture.getGdxTexture().getTexture().setWrap(previousUWrap, previousVWrap);
    }

    @Override
    public void drawText(String text, Font font, HorizontalAlign hAlign, VerticalAlign vAlign, Rect2i absoluteRegion, Color color, Color shadowColor, float alpha, boolean underlined) {
        if (!(font instanceof LibGDXFont)) {
            return;
        }

        int gdxAlignment = 0;
        switch (hAlign) {
            case LEFT:
                gdxAlignment = Align.left;
                break;
            case RIGHT:
                gdxAlignment = Align.right;
                break;
            case CENTER:
                gdxAlignment = Align.center;
                break;
        }

        switch (vAlign) {
            case TOP:
                gdxAlignment |= Align.top;
                break;
            case MIDDLE:
                gdxAlignment |= Align.center;
                break;
            case BOTTOM:
                gdxAlignment |= Align.bottom;
                break;
        }

        LibGDXFont gdxFont = (LibGDXFont) font;

        // Shadow drawing
        gdxFont.getGlyphLayout().setText(gdxFont.getGdxFont(), text,
                new com.badlogic.gdx.graphics.Color(shadowColor.rf(), shadowColor.gf(), shadowColor.bf(), shadowColor.af() * alpha), absoluteRegion.width(),
                gdxAlignment, true);
        gdxFont.getGdxFont().draw(spriteBatch, gdxFont.getGlyphLayout(),
                absoluteRegion.minX() - SHADOW_HORIZONTAL_OFFSET,
                screenHeight - absoluteRegion.minY() - SHADOW_VERTICAL_OFFSET
                        - vAlign.getOffset(Math.abs((int)gdxFont.getGlyphLayout().height), absoluteRegion.height()));

        Deque<Color> colorStack = new LinkedList<Color>();
        colorStack.push(color);

        List<Map.Entry<String, Color>> textSegments = new ArrayList<>();
        StringBuilder currentSegment = new StringBuilder();
        for (char character : text.toCharArray()) {
            if (FontColor.isValid(character)) {
                if (character != FontColor.getReset()) {
                    colorStack.push(FontColor.toColor(character));
                } else if (currentSegment.length() != 0) {
                    textSegments.add(new AbstractMap.SimpleEntry<>(currentSegment.toString(), colorStack.pop()));
                    currentSegment.delete(0, currentSegment.length());
                }
            } else {
                currentSegment.append(character);
            }
        }

        if (currentSegment.length() != 0) {
            textSegments.add(new AbstractMap.SimpleEntry<>(currentSegment.toString(), colorStack.pop()));
        }

        int textX = absoluteRegion.minX();
        int textY = screenHeight - absoluteRegion.minY()
                - vAlign.getOffset(Math.abs((int)gdxFont.getGlyphLayout().height), absoluteRegion.height());

        int lineHeight = Math.abs(gdxFont.getLineHeight());

        for (Map.Entry<String, Color> textSegment : textSegments) {
            String contents = FontColor.stripColor(textSegment.getKey());
            Color segmentColor = textSegment.getValue();

            if (contents.startsWith("\n")) {
                textX = absoluteRegion.minX();
                textY += lineHeight;
            }

            // Standard drawing
            gdxFont.getGlyphLayout().setText(gdxFont.getGdxFont(), contents,
                    new com.badlogic.gdx.graphics.Color(segmentColor.rf(), segmentColor.gf(), segmentColor.bf(), segmentColor.af() * alpha), absoluteRegion.width(),
                    gdxAlignment, true);
            gdxFont.getGdxFont().draw(spriteBatch, gdxFont.getGlyphLayout(), textX, textY);

            textX += gdxFont.getGlyphLayout().width;

            int textHeight = (int) Math.abs(gdxFont.getGlyphLayout().height);
            textY -= Math.max(textHeight, lineHeight);

            if (contents.endsWith("\n")) {
                textX = absoluteRegion.minX();
            }
        }
    }

    @Override
    public void drawTextureBordered(UITextureRegion texture, Rect2i absoluteRegion, Border border, boolean tile, float ux, float uy, float uw, float uh, float alpha) {
        // See https://github.com/Terasology/TutorialNui/wiki/Skinning#background-options for border rendering information

        Vector2i textureSize = new Vector2i(TeraMath.ceilToInt(texture.getWidth() * uw), TeraMath.ceilToInt(texture.getHeight() * uh));
        // Draw texture without borders
        drawTexture(texture, Color.WHITE, tile ? ScaleMode.TILED : ScaleMode.STRETCH, absoluteRegion,
                ux + (float)border.getLeft() / textureSize.x, uy + (float)border.getTop() / textureSize.y,
                uw - (float)border.getTotalWidth() / textureSize.x,
                uh - (float)border.getTotalHeight() / textureSize.y, alpha);

        // Draw borders around texture

        // Left border
        drawTexture(texture, Color.WHITE, tile ? ScaleMode.TILED : ScaleMode.STRETCH, Rect2i.createFromMinAndSize(absoluteRegion.minX(), absoluteRegion.minY(), border.getLeft(), absoluteRegion.height()),
                ux, uy,(float)border.getLeft() / textureSize.x, uh, alpha);

        // Right border
        drawTexture(texture, Color.WHITE, tile ? ScaleMode.TILED : ScaleMode.STRETCH, Rect2i.createFromMinAndSize(absoluteRegion.maxX() - border.getRight(), absoluteRegion.minY(), border.getRight(), absoluteRegion.height()),
                ux + uw - ((float)border.getRight() / textureSize.x), uy,
                (float)border.getRight() / textureSize.x,
                uh, alpha);

        // Top border
        drawTexture(texture, Color.WHITE, tile ? ScaleMode.TILED : ScaleMode.STRETCH, Rect2i.createFromMinAndSize(absoluteRegion.minX(), absoluteRegion.minY(), absoluteRegion.width(), border.getTop()),
                ux, uy,
                uw,
                (float)border.getTop() / textureSize.y, alpha);

        // Bottom border
        drawTexture(texture, Color.WHITE, tile ? ScaleMode.TILED : ScaleMode.STRETCH, Rect2i.createFromMinAndSize(absoluteRegion.minX(), absoluteRegion.maxY() - border.getBottom(), absoluteRegion.width(), border.getBottom()),
                ux, uy + uh - ((float)border.getBottom() / textureSize.y),
                uw,
                (float)border.getBottom() / textureSize.y, alpha);
    }
}
