package dev.lyze.gdxtinyvg.scene2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TransformDrawable;
import dev.lyze.gdxtinyvg.TinyVG;
import dev.lyze.gdxtinyvg.drawers.TinyVGShapeDrawer;

/**
 * Allows TinyVG to be implemented in Scene2D.UI Stages via the Drawable
 * interface. The Batch used by the provided the
 * TinyVGShapeDrawer must be the same Batch used by the Scene2D Stage.
 */
public class TinyVGDrawable extends BaseDrawable implements TransformDrawable {
    public TinyVG tvg;
    public transient TinyVGShapeDrawer shapeDrawer;

    /**
     * A no-argument constructor necessary for serialization.
     * {@link TinyVGDrawable#shapeDrawer} must be defined before
     * this Drawable is drawn.
     */
    public TinyVGDrawable() {
    }

    /**
     * Constructs a TinyVGDrawable. The Batch of the provided TinyVGShapeDrawer must
     * be the same Batch at rendering time.
     * 
     * @param tvg
     * @param shapeDrawer
     */
    public TinyVGDrawable(TinyVG tvg, TinyVGShapeDrawer shapeDrawer) {
        this.tvg = tvg;
        this.shapeDrawer = shapeDrawer;
        setMinSize(tvg.getUnscaledWidth(), tvg.getUnscaledHeight());
    }

    /**
     * Creates a new TinyVGDrawable with the same sizing information and tvg values
     * as the specified drawable.
     *
     * @param drawable
     */
    public TinyVGDrawable(TinyVGDrawable drawable) {
        super(drawable);
        tvg = drawable.tvg;
        shapeDrawer = drawable.shapeDrawer;
    }

    /**
     * Draws the user defined TinyVG to the specified position and dimensions. May
     * throw an
     * {@link IllegalArgumentException} if the argument batch and shapeDrawer.batch
     * are not the same.
     * 
     * @param batch
     * @param x
     * @param y
     * @param width
     * @param height
     */
    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        if (shapeDrawer == null) {
            throw new NullPointerException("shapeDrawer must be defined before the Drawable can be drawn.");
        }
        if (!batch.equals(shapeDrawer.getBatch())) {
            throw new IllegalArgumentException("Argument \"batch\" does not match \"shapeDrawer.batch\"");
        }
        tvg.setPosition(x, y);
        tvg.setSize(width, height);
        float previousColor = shapeDrawer.getPackedColor();
        shapeDrawer.setColor(batch.getColor());
        tvg.draw(shapeDrawer);
        shapeDrawer.setColor(previousColor);
    }

    /**
     * Draws the user defined TinyVG to the specified transform. May throw an
     * {@link IllegalArgumentException} if the argument batch and shapeDrawer.batch
     * are not the same.
     * 
     * @param batch
     * @param x
     * @param y
     * @param originX
     * @param originY
     * @param width
     * @param height
     * @param scaleX
     * @param scaleY
     * @param rotation
     */
    @Override
    public void draw(Batch batch, float x, float y, float originX, float originY, float width, float height,
            float scaleX, float scaleY, float rotation) {
        tvg.setPosition(x, y);
        tvg.setOrigin(originX, originY);
        tvg.setSize(width, height);
        tvg.setScale(scaleX, scaleY);
        tvg.setRotation(rotation);
        float previousColor = shapeDrawer.getPackedColor();
        shapeDrawer.setColor(batch.getColor());
        tvg.draw(shapeDrawer);
        shapeDrawer.setColor(previousColor);
    }

    public TinyVG getTvg() {
        return tvg;
    }

    public void setTvg(TinyVG tvg) {
        this.tvg = tvg;
        setMinSize(tvg.getUnscaledWidth(), tvg.getUnscaledHeight());
    }

    public TinyVGShapeDrawer getShapeDrawer() {
        return shapeDrawer;
    }

    public void setShapeDrawer(TinyVGShapeDrawer shapeDrawer) {
        this.shapeDrawer = shapeDrawer;
    }
}
