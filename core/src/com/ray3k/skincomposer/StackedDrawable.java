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
 * A Drawable that renders one drawable directly under another. These can be
 * linked together to create a chain of any length.
 * 
 * MIT License

Copyright (c) 2024 Raymond Buckley

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

 * @author Raymond Buckley
 */
public class StackedDrawable extends BaseDrawable{
    private Drawable drawable1, drawable2;
    
    /**
     * 
     * @param drawable1 the drawable to be drawn on the bottom of the stack
     * @param drawable2 the drawable to be drawn on the top of the stack
     */
    public StackedDrawable(Drawable drawable1, Drawable drawable2) {
        this.drawable1 = drawable1;
        this.drawable2 = drawable2;
    }

    public Drawable getDrawable1() {
        return drawable1;
    }

    public void setDrawable1(Drawable drawable1) {
        this.drawable1 = drawable1;
    }

    public Drawable getDrawable2() {
        return drawable2;
    }

    public void setDrawable2(Drawable drawable2) {
        this.drawable2 = drawable2;
    }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        drawable1.draw(batch, x, y, width, height);
        drawable2.draw(batch, x, y, width, height);
    }

    @Override
    public float getMinHeight() {
        return drawable2.getMinHeight();
    }

    @Override
    public float getMinWidth() {
        return drawable2.getMinWidth();
    }

    @Override
    public float getBottomHeight() {
        return drawable2.getBottomHeight();
    }

    @Override
    public float getTopHeight() {
        return drawable2.getTopHeight();
    }

    @Override
    public float getRightWidth() {
        return drawable2.getRightWidth();
    }

    @Override
    public float getLeftWidth() {
        return drawable2.getLeftWidth();
    }

    @Override
    public void setMinHeight(float minHeight) {
        drawable1.setMinHeight(minHeight);
        drawable2.setMinHeight(minHeight);
    }

    @Override
    public void setMinWidth(float minWidth) {
        drawable1.setMinWidth(minWidth);
        drawable2.setMinWidth(minWidth);
    }

    @Override
    public void setBottomHeight(float bottomHeight) {
        drawable1.setBottomHeight(bottomHeight);
        drawable2.setBottomHeight(bottomHeight);
    }

    @Override
    public void setTopHeight(float topHeight) {
        drawable1.setTopHeight(topHeight);
        drawable2.setTopHeight(topHeight);
    }

    @Override
    public void setRightWidth(float rightWidth) {
        drawable1.setRightWidth(rightWidth);
        drawable2.setRightWidth(rightWidth);
    }

    @Override
    public void setLeftWidth(float leftWidth) {
        drawable1.setLeftWidth(leftWidth);
        drawable2.setLeftWidth(leftWidth);
    }
}
