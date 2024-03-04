/*******************************************************************************
 * MIT License
 * 
 * Copyright (c) 2024 Raymond Buckley
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/

package com.ray3k.skincomposer;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;

/**
 * A Drawable that renders an array of NinePatchDrawables in sequence. Ensure
 * that update() is called once per Drawable in the render() method.
 * 
 * @author Raymond Buckley
 */
public class AnimatedNinePatchDrawable extends NinePatchDrawable {
    private float t, frameDuration;
    private Array<NinePatch> patches;
    private int index;

    /**
     * 
     * @param patches the frames to be drawn
     * @param frameDuration the delay between frames in seconds.
     */
    public AnimatedNinePatchDrawable(Array<NinePatch> patches, float frameDuration) {
        this.patches = new Array<>(patches);
        t = 0.0f;
        this.frameDuration = frameDuration;
        index = 0;
        super.setPatch(patches.get(0));
    }
    
    @Override
    public void setPatch(NinePatch patch) {
        patches.clear();
        patches.add(patch);
        super.setPatch(patch);
    }
    
    public void setPatches(Array<NinePatch> patches) {
        this.patches.clear();
        this.patches.addAll(patches);
    }

    public float getFrameDuration() {
        return frameDuration;
    }

    public void setFrameDuration(float frameDuration) {
        this.frameDuration = frameDuration;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    
    public void update(float delta) {
        t += delta;
        if (t > frameDuration) {
            t = 0.0f;
            index++;
            if (index >= patches.size) {
                index = 0;
            }
            super.setPatch(patches.get(index));
        }
    }
}
