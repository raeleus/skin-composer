package dev.lyze.gdxtinyvg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Array;
import dev.lyze.gdxtinyvg.commands.Command;
import dev.lyze.gdxtinyvg.drawers.TinyVGShapeDrawer;

public class TinyVG {
    /**
     * Each TVG file starts with a header defining some global values for the file
     * like scale and image size.
     */
    private final TinyVGHeader header;
    /**
     * The color table encodes the palette for this file. It’s binary content is
     * defined by the color_encoding field in the header. For the three defined
     * color encodings, each will yield a list of color_count RGBA tuples.
     */
    private final Color[] colorTable;
    /**
     * TinyVG files contain a sequence of draw commands that must be executed in the
     * defined order to get the final result. Each draw command adds a new 2D
     * primitive to the graphic.
     */
    private final Array<Command> commands = new Array<>();
    /**
     * Global position offset value.
     */
    private float positionX;
    private float positionY;
    /**
     * Global scale value.
     */
    private float scaleX = 1;
    private float scaleY = 1;
    /**
     * Global rotation value.
     */
    private float rotation;
    /**
     * Global origin value.
     */
    private float originX;
    private float originY;
    /**
     * Global shear value.
     */
    private float shearX;
    private float shearY;
    /**
     * Amount of points every curve generates.
     */
    private int curvePoints = 24;
    /**
     * Clips the TVG based on the provided image size. Requires depth buffer.
     */
    private boolean clipBasedOnTVGSize = true;
    /**
     * Next time render gets called and the tvg is dirty, it recalculates all point
     * positions in paths. (Slow)
     */
    private boolean dirtyCurves;
    /**
     * Next time render gets called and the tvg is dirty, it recalculates the
     * transformation matrix. (Fast)
     */
    private boolean dirtyTransformationMatrix;
    private final Matrix4 backupBatchTransform = new Matrix4();
    private final Matrix4 computedTransform = new Matrix4();
    private final Matrix4 compositeTransform = new Matrix4();
    private final Matrix4 bufferTransform = new Matrix4();
    private final Affine2 affine = new Affine2();

    public TinyVG(TinyVGHeader header, Color[] colorTable) {
        this.header = header;
        this.colorTable = colorTable;
    }

    /**
     * Draws the tvg to the screen.
     */
    public void draw(TinyVGShapeDrawer drawer) {
        backupBatchTransform.set(drawer.getBatch().getTransformMatrix());
        if (dirtyTransformationMatrix) {
            updateTransformationMatrix();
            dirtyTransformationMatrix = false;
        }
        compositeTransform.set(backupBatchTransform);
        compositeTransform.mul(computedTransform);
        drawer.getBatch().setTransformMatrix(compositeTransform);
        if (clipBasedOnTVGSize) {
            Gdx.gl.glDepthMask(true);
            Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
            Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
            Gdx.gl.glDepthFunc(GL20.GL_ALWAYS);
            Gdx.gl.glColorMask(false, false, false, false);
            drawer.filledRectangle(0, 0, getUnscaledWidth(), getUnscaledHeight());
            drawer.getBatch().flush();
        }
        drawer.beginShader();
        if (clipBasedOnTVGSize) {
            Gdx.gl.glColorMask(true, true, true, true);
            Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
            Gdx.gl.glDepthFunc(GL20.GL_EQUAL);
        }
        for (Command command : commands) {
            if (dirtyCurves) command.onPropertiesChanged();
            command.draw(drawer);
        }
        dirtyCurves = false;
        drawer.getBatch().flush();
        drawer.getBatch().setTransformMatrix(backupBatchTransform);
        drawer.endShader();
        if (clipBasedOnTVGSize) {
            Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
            Gdx.gl.glDepthMask(false);
        }
    }

    /**
     * Draws the bounding box of the shape drawer
     */
    public void drawBoundingBox(TinyVGShapeDrawer drawer, Color color) {
        backupBatchTransform.set(drawer.getBatch().getTransformMatrix());
        if (dirtyTransformationMatrix) {
            updateTransformationMatrix();
            dirtyTransformationMatrix = false;
        }
        drawer.getBatch().setTransformMatrix(computedTransform);
        drawer.rectangle(0, 0, getUnscaledWidth(), getUnscaledHeight(), color);
        drawer.getBatch().setTransformMatrix(backupBatchTransform);
    }

    /**
     * Checks if the given point is inside the bounding box.
     */
    public boolean isInBoundingBox(Vector2 point) {
        if (dirtyTransformationMatrix) {
            updateTransformationMatrix();
            dirtyTransformationMatrix = false;
        }
        float pointX = point.x;
        float pointY = point.y;
        point.set(0, 0);
        affine.applyTo(point);
        float topLeftX = point.x;
        float topLeftY = point.y;
        point.set(getUnscaledWidth(), 0);
        affine.applyTo(point);
        float topRightX = point.x;
        float topRightY = point.y;
        point.set(0, getUnscaledHeight());
        affine.applyTo(point);
        float bottomLeftX = point.x;
        float bottomLeftY = point.y;
        point.set(getUnscaledWidth(), getUnscaledHeight());
        affine.applyTo(point);
        float bottomRightX = point.x;
        float bottomRightY = point.y;
        point.set(pointX, pointY);
        return Intersector.isPointInTriangle(pointX, pointY, topLeftX, topLeftY, topRightX, topRightY, bottomRightX, bottomRightY) || Intersector.isPointInTriangle(pointX, pointY, topLeftX, topLeftY, bottomLeftX, bottomLeftY, bottomRightX, bottomRightY);
    }

    private void updateTransformationMatrix() {
        affine.idt();
        affine.shear(shearX, shearY);
        affine.translate(positionX, positionY);
        affine.translate(originX, originY);
        affine.scale(scaleX, scaleY);
        affine.rotate(rotation);
        affine.translate(-originX, -originY);
        computedTransform.set(affine);
    }

    public void addCommand(Command command) {
        this.commands.add(command);
    }

    /**
     * Sets the size of the TVG based on a specified width and height.
     */
    public void setSize(float width, float height) {
        scaleX = width / header.getWidth();
        scaleY = height / header.getHeight();
        dirtyTransformationMatrix = true;
    }

    /**
     * Sets the scale of the TVG based on the specified float value.
     */
    public void setScale(float scale) {
        setScale(scale, scale);
        dirtyTransformationMatrix = true;
    }

    /**
     * Sets the scale of the TVG based on the specified float value.
     */
    public void setScale(float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        dirtyTransformationMatrix = true;
    }

    /**
     * Sets the position of the TVG based on the specified x and y values.
     */
    public void setPosition(float x, float y) {
        this.positionX = x;
        this.positionY = y;
        dirtyTransformationMatrix = true;
    }

    /**
     * Sets the position where the TVG will be drawn, relative to tits current
     * origin.
     */
    public void setOriginBasedPosition(float x, float y) {
        setPosition(x - this.originX, y - this.originY);
    }

    /**
     * Sets the origin of the TVG based on the specified x and y values.
     */
    public void setOrigin(float x, float y) {
        this.originX = x;
        this.originY = y;
        dirtyTransformationMatrix = true;
    }

    /**
     * Sets the shear of the TVG based on the specified x and y values.
     */
    public void setShear(float x, float y) {
        this.shearX = x;
        this.shearY = y;
        dirtyTransformationMatrix = true;
    }

    /**
     * Sets the amount of curve points used to generate curves and triggers an
     * update to recalculate commands.
     */
    public void setCurvePoints(int curvePoints) {
        this.curvePoints = curvePoints;
        dirtyCurves = true;
        dirtyTransformationMatrix = true;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation % 360;
        dirtyTransformationMatrix = true;
    }

    public void setShearY(float shearY) {
        this.shearY = shearY;
        dirtyTransformationMatrix = true;
    }

    public void setShearX(float shearX) {
        this.shearX = shearX;
        dirtyTransformationMatrix = true;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
        dirtyTransformationMatrix = true;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
        dirtyTransformationMatrix = true;
    }

    public void setPositionY(float positionY) {
        this.positionY = positionY;
        dirtyTransformationMatrix = true;
    }

    public void setPositionX(float positionX) {
        this.positionX = positionX;
        dirtyTransformationMatrix = true;
    }

    public void setOriginX(float originX) {
        this.originX = originX;
        dirtyTransformationMatrix = true;
    }

    public void setOriginY(float originY) {
        this.originY = originY;
        dirtyTransformationMatrix = true;
    }

    /**
     * @return Returns the unscaled width of the tvg.
     */
    public float getUnscaledWidth() {
        return header.getWidth();
    }

    /**
     * @return Returns the unscaled width of the tvg.
     */
    public float getUnscaledHeight() {
        return header.getHeight();
    }

    /**
     * @return Returns the scaled width of the tvg.
     */
    public float getScaledWidth() {
        return header.getWidth() * scaleX;
    }

    /**
     * @return Returns the scaled height of the tvg.
     */
    public float getScaledHeight() {
        return header.getHeight() * scaleY;
    }

    public void centerOrigin() {
        setOrigin(getUnscaledWidth() / 2.0F, getUnscaledHeight() / 2.0F);
    }

    /**
     * Each TVG file starts with a header defining some global values for the file
     * like scale and image size.
     */
    public TinyVGHeader getHeader() {
        return this.header;
    }

    /**
     * The color table encodes the palette for this file. It’s binary content is
     * defined by the color_encoding field in the header. For the three defined
     * color encodings, each will yield a list of color_count RGBA tuples.
     */
    public Color[] getColorTable() {
        return this.colorTable;
    }

    /**
     * TinyVG files contain a sequence of draw commands that must be executed in the
     * defined order to get the final result. Each draw command adds a new 2D
     * primitive to the graphic.
     */
    public Array<Command> getCommands() {
        return this.commands;
    }

    /**
     * Global position offset value.
     */
    public float getPositionX() {
        return this.positionX;
    }

    public float getPositionY() {
        return this.positionY;
    }

    /**
     * Global scale value.
     */
    public float getScaleX() {
        return this.scaleX;
    }

    public float getScaleY() {
        return this.scaleY;
    }

    /**
     * Global rotation value.
     */
    public float getRotation() {
        return this.rotation;
    }

    /**
     * Global origin value.
     */
    public float getOriginX() {
        return this.originX;
    }

    public float getOriginY() {
        return this.originY;
    }

    /**
     * Global shear value.
     */
    public float getShearX() {
        return this.shearX;
    }

    public float getShearY() {
        return this.shearY;
    }

    /**
     * Amount of points every curve generates.
     */
    public int getCurvePoints() {
        return this.curvePoints;
    }

    /**
     * Clips the TVG based on the provided image size. Requires depth buffer.
     */
    public boolean isClipBasedOnTVGSize() {
        return this.clipBasedOnTVGSize;
    }

    /**
     * Clips the TVG based on the provided image size. Requires depth buffer.
     */
    public void setClipBasedOnTVGSize(final boolean clipBasedOnTVGSize) {
        this.clipBasedOnTVGSize = clipBasedOnTVGSize;
    }

    /**
     * Next time render gets called and the tvg is dirty, it recalculates all point
     * positions in paths. (Slow)
     */
    public boolean isDirtyCurves() {
        return this.dirtyCurves;
    }

    /**
     * Next time render gets called and the tvg is dirty, it recalculates the
     * transformation matrix. (Fast)
     */
    public boolean isDirtyTransformationMatrix() {
        return this.dirtyTransformationMatrix;
    }

    public Affine2 getAffine() {
        return this.affine;
    }
}
