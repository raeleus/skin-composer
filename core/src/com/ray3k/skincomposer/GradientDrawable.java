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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;

/**
 * Drawable that renders a gradient defined by four corner colors.
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
public class GradientDrawable extends BaseDrawable {
    private static ShapeRenderer g;
    private Color col1, col2, col3, col4;
    private float borderLeft, borderTop, borderRight, borderBottom;

    /**
     * 
     * @param col1 bottom left color
     * @param col2 bottom right color
     * @param col3 top right color
     * @param col4 top left color
     */
    public GradientDrawable(Color col1, Color col2, Color col3, Color col4) {
        if (g == null) g = new ShapeRenderer();
        
        this.col1 = new Color();
        this.col2 = new Color();
        this.col3 = new Color();
        this.col4 = new Color();
        setCol1(col1);
        setCol2(col2);
        setCol3(col3);
        setCol4(col4);
        
        borderLeft = 0.0f;
        borderTop = 0.0f;
        borderRight = 0.0f;
        borderBottom = 0.0f;
    }

    public float getBorderLeft() {
        return borderLeft;
    }

    public void setBorderLeft(float borderLeft) {
        this.borderLeft = borderLeft;
    }

    public float getBorderTop() {
        return borderTop;
    }

    public void setBorderTop(float borderTop) {
        this.borderTop = borderTop;
    }

    public float getBorderRight() {
        return borderRight;
    }

    public void setBorderRight(float borderRight) {
        this.borderRight = borderRight;
    }

    public float getBorderBottom() {
        return borderBottom;
    }

    public void setBorderBottom(float borderBottom) {
        this.borderBottom = borderBottom;
    }
    
    public void setBorder(float border) {
        setBorderLeft(border);
        setBorderTop(border);
        setBorderRight(border);
        setBorderBottom(border);
    }

    public Color getCol1() {
        return col1;
    }

    public void setCol1(Color col1) {
        this.col1.set(col1);
    }

    public Color getCol2() {
        return col2;
    }

    public void setCol2(Color col2) {
        this.col2.set(col2);
    }

    public Color getCol3() {
        return col3;
    }

    public void setCol3(Color col3) {
        this.col3.set(col3);
    }

    public Color getCol4() {
        return col4;
    }

    public void setCol4(Color col4) {
        this.col4.set(col4);
    }
    
    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        float[] alphas = {col1.a, col2.a, col3.a, col4.a};
        col1.a = batch.getColor().a * col1.a;
        col2.a = batch.getColor().a * col2.a;
        col3.a = batch.getColor().a * col3.a;
        col4.a = batch.getColor().a * col4.a;
        
        g.begin(ShapeRenderer.ShapeType.Filled);
        g.setProjectionMatrix(batch.getProjectionMatrix());
        g.setTransformMatrix(batch.getTransformMatrix());
        batch.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        g.rect(x + borderLeft, y + borderBottom, width - borderLeft - borderRight, height - borderBottom - borderTop, col1, col2, col3, col4);
        g.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        batch.begin();
        
        col1.a = alphas[0];
        col2.a = alphas[1];
        col3.a = alphas[2];
        col4.a = alphas[3];
    }
}
