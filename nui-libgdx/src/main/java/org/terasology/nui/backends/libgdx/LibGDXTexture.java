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

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.terasology.assets.AssetData;
import org.terasology.math.geom.Rect2f;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;
import org.terasology.nui.UITextureRegion;

public class LibGDXTexture implements UITextureRegion, AssetData {
    private final TextureRegion texture;

    public LibGDXTexture(TextureRegion texture) {
        this.texture = texture;
    }

    /**
     * @return The region of the texture represented by this asset
     */
    @Override
    public Rect2f getRegion() {
        return Rect2f.createFromMinAndSize(texture.getRegionX(), texture.getRegionY(), texture.getRegionWidth(), texture.getRegionHeight());
    }

    /**
     * @return The pixel region of the texture represented by this asset
     */
    @Override
    public Rect2i getPixelRegion() {
        return Rect2i.createFromMinAndSize(texture.getRegionX(), texture.getRegionY(), texture.getRegionWidth(), texture.getRegionHeight());
    }

    @Override
    public int getWidth() {
        return texture.getRegionWidth();
    }

    @Override
    public int getHeight() {
        return texture.getRegionHeight();
    }

    @Override
    public Vector2i size() {
        return new Vector2i(texture.getRegionWidth(), texture.getRegionHeight());
    }

    public TextureRegion getGdxTexture() {
        return texture;
    }
}
