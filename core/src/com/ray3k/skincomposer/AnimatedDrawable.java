/*******************************************************************************
 * MIT License
 * 
 * Copyright (c) 2016 Raymond Buckley
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

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;

/**
 * A drawable class that plays a sequence of images with the specified frame delay.
 * Update must be called regularly in order for the animation to occur.
 * 
 * @author Raymond Buckley
 */
public class AnimatedDrawable extends BaseDrawable {
    private float t, frameDuration;
    private Array<Drawable> drawables;
    private int index;

    /**
     * 
     * @param drawables the frames to be drawn
     * @param frameDuration the delay between frames in seconds.
     */
    public AnimatedDrawable(Array<Drawable> drawables, float frameDuration) {
        if (drawables != null) {
            this.drawables = new Array<>(drawables);
            recalcSize();
        } else {
            this.drawables = new Array<>();
        }
        
        t = 0.0f;
        this.frameDuration = frameDuration;
        index = 0;
    }
    
    public AnimatedDrawable(float frameDuration) {
        this(null, frameDuration);
    }

    public float getFrameDuration() {
        return frameDuration;
    }

    public void setFrameDuration(float frameDuration) {
        this.frameDuration = frameDuration;
    }

    public Array<Drawable> getDrawables() {
        return drawables;
    }

    public void setDrawables(Array<Drawable> drawables) {
        this.drawables.clear();
        this.drawables.addAll(drawables);
        recalcSize();
    }
    
    public void addDrawable(Drawable drawable) {
        drawables.add(drawable);
        recalcSize();
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
            if (index >= drawables.size) {
                index = 0;
            }
        }
    }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        if (drawables.size > 0) {
            drawables.get(index).draw(batch, x, y, width, height);
        }
    }
    
    private void recalcSize() {
        if (drawables.size > 0) {
            float bottomHeight = 0.0f;
            float leftWidth = 0.0f;
            float minHeight = 0.0f;
            float minWidth = 0.0f;
            float rightWidth = 0.0f;
            float topHeight = 0.0f;
            
            for (Drawable drawable : drawables) {
                bottomHeight = Math.max(bottomHeight, drawable.getBottomHeight());
                leftWidth = Math.max(leftWidth, drawable.getLeftWidth());
                minHeight = Math.max(minHeight, drawable.getMinHeight());
                minWidth = Math.max(minWidth, drawable.getMinWidth());
                rightWidth = Math.max(rightWidth, drawable.getRightWidth());
                topHeight = Math.max(topHeight, drawable.getTopHeight());
            }
            
            setBottomHeight(bottomHeight);
            setLeftWidth(leftWidth);
            setMinHeight(minHeight);
            setMinWidth(minWidth);
            setRightWidth(rightWidth);
            setTopHeight(topHeight);
        }
    }
}
