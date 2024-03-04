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

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Draws a checkerboard pattern with the provided drawables, tinted with the
 * provided colors. Ensure that the widget using this drawable is set to clip.
 * 
 * @author Raymond
 */
public class CheckerDrawable extends BaseDrawable {
    private Drawable drawable1;
    private Drawable drawable2;
    private float w;
    private float h;
    private float cellWidth;
    private float cellHeight;

    /**
     * cellWidth and cellHeight are only suggested values. These are overridden
     * by the minimum width and height of the drawables if necessary
     * @param drawable1
     * @param drawable2
     * @param cellWidth
     * @param cellHeight 
     */
    public CheckerDrawable(Drawable drawable1, Drawable drawable2, float cellWidth, float cellHeight) {
        this.drawable1 = drawable1;
        this.drawable2 = drawable2;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        calcDimensions();
    }

    public Drawable getDrawable1() {
        return drawable1;
    }

    public void setDrawable1(Drawable drawable1) {
        this.drawable1 = drawable1;
        calcDimensions();
    }

    public Drawable getDrawable2() {
        return drawable2;
    }

    public void setDrawable2(Drawable drawable2) {
        this.drawable2 = drawable2;
        calcDimensions();
    }

    public float getCellWidth() {
        return cellWidth;
    }

    public void setCellWidth(float cellWidth) {
        this.cellWidth = cellWidth;
        calcDimensions();
    }

    public float getCellHeight() {
        return cellHeight;
    }

    public void setCellHeight(float cellHeight) {
        this.cellHeight = cellHeight;
        calcDimensions();
    }
    
    private void calcDimensions() {
        w = Math.max(drawable1.getMinWidth(), drawable2.getMinWidth());
        w = Math.max(w, cellWidth);
        h = Math.max(drawable1.getMinHeight(), drawable2.getMinHeight());
        h = Math.max(h, cellHeight);
    }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        boolean changeY = false;
        for (float y1 = y; y1 < y + height; y1 += h) {
            boolean changeX = changeY;
            for (float x1 = x; x1 < x + width; x1 += w) {
                if (changeX) {
                    drawable2.draw(batch, x1, y1, Math.min(w, x + width - x1), Math.min(h, y + height - y1));
                } else {
                    drawable1.draw(batch, x1, y1, Math.min(w, x + width - x1), Math.min(h, y + height - y1));
                }
                
                changeX = !changeX;
            }
            changeY = !changeY;
        }
    }
}
