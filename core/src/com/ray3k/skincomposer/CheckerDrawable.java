package com.ray3k.skincomposer;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Draws a checkerboard pattern with the provided drawables, tinted with the
 * provided colors. Ensure that the widget using this drawable is set to clip.
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
     * @param col1
     * @param col2
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
