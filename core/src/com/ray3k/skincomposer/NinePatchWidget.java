/*
 * The MIT License
 *
 * Copyright 2018 Raymond Buckley.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.ray3k.skincomposer;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;

/**
 *
 * @author Raymond
 */
public class NinePatchWidget extends Stack {
    private int zoom;
    private NinePatchWidgetStyle style;
    private TilePatternDrawable tile;
    private float positionX;
    private float positionY;
    private int paddingTop, paddingBottom, paddingLeft, paddingRight;
    private int contentTop, contentBottom, contentLeft, contentRight;
    private boolean showContent;
    private Button paddingButton;
    public static enum HandleType {
        NONE, PADDING_TOP, PADDING_BOTTOM, PADDING_LEFT, PADDING_RIGHT, CONTENT_TOP, CONTENT_BOTTOM, CONTENT_LEFT, CONTENT_RIGHT
    }
    private HandleType currentHandle;
    private boolean dragging;
    private int regionWidth, regionHeight;
    private Array<NinePatchWidgetListener> listeners;
    public static enum GridType {
        NONE, LIGHT, DARK
    }
    private GridType gridType;
    
    public NinePatchWidget(NinePatchWidgetStyle style) {
        listeners = new Array<>();
        this.style = style;
        
        setDefaults();
        populate();
        updateTileOffset();
    }
    
    public NinePatchWidget(Skin skin, String styleName) {
        this(skin.get(styleName, NinePatchWidgetStyle.class));
    }
    
    public NinePatchWidget(Skin skin) {
        this(skin, "default");
    }
    
    private void setDefaults() {
        zoom = 1;
        showContent = false;
        dragging = false;
        gridType = GridType.NONE;
    }
    
    private void populate() {
        clearChildren();
        
        var table = new Table();
        table.setClip(true);
        add(table);
        
        tile = new TilePatternDrawable(this, style);
        tile.style = style;
        var image = new Image(tile);
        table.add(image).grow();
        DragListener dragListener = new DragListener() {
            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                var worldX = (x - image.getWidth() / 2.0f) / zoom - positionX;
                var worldY = (y - image.getHeight() / 2.0f) / zoom - positionY;
                
                switch (currentHandle) {
                    case PADDING_LEFT:
                        if (worldX > regionWidth - getPaddingRight()) {
                            worldX = regionWidth - getPaddingRight();
                        }
                        
                        if (worldX < 0) {
                            worldX = 0;
                        }
                        
                        setPaddingLeft(MathUtils.ceil(worldX));
                        break;
                    case PADDING_RIGHT:
                        if (worldX < getPaddingLeft()) {
                            worldX = getPaddingLeft();
                        }
                        
                        if (worldX > regionWidth) {
                            worldX = regionWidth;
                        }
                        
                        setPaddingRight(regionWidth - MathUtils.floor(worldX));
                        break;
                    case PADDING_TOP:
                        if (worldY < getPaddingBottom()) {
                            worldY = getPaddingBottom();
                        }
                        
                        if (worldY > regionHeight) {
                            worldY = regionHeight;
                        }
                        
                        setPaddingTop(regionHeight - MathUtils.floor(worldY));
                        break;
                    case PADDING_BOTTOM:
                        if (worldY > regionHeight - getPaddingTop()) {
                            worldY = regionHeight - getPaddingTop();
                        }
                        
                        if (worldY < 0) {
                            worldY = 0;
                        }
                        
                        setPaddingBottom(MathUtils.ceil(worldY));
                        break;
                    case CONTENT_LEFT:
                        if (worldX > regionWidth - getContentRight()) {
                            worldX = regionWidth - getContentRight();
                        }
                        
                        if (worldX < 0) {
                            worldX = 0;
                        }
                        
                        setContentLeft(MathUtils.ceil(worldX));
                        break;
                    case CONTENT_RIGHT:
                        if (worldX < getContentLeft()) {
                            worldX = getContentLeft();
                        }
                        
                        if (worldX > regionWidth) {
                            worldX = regionWidth;
                        }
                        
                        setContentRight(regionWidth - MathUtils.floor(worldX));
                        break;
                    case CONTENT_TOP:
                        if (worldY < getContentBottom()) {
                            worldY = getContentBottom();
                        }
                        
                        if (worldY > regionHeight) {
                            worldY = regionHeight;
                        }
                        
                        setContentTop(regionHeight - MathUtils.floor(worldY));
                        break;
                    case CONTENT_BOTTOM:
                        if (worldY > regionHeight - getContentTop()) {
                            worldY = regionHeight - getContentTop();
                        }
                        
                        if (worldY < 0) {
                            worldY = 0;
                        }
                        
                        setContentBottom(MathUtils.ceil(worldY));
                        break;
                }
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer) {
                if (dragging) {
                    dragging = false;
                    currentHandle = HandleType.NONE;
                }
            }

            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                if (currentHandle != HandleType.NONE) {
                    dragging = true;
                }
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                var worldX = (x - image.getWidth() / 2.0f) / zoom - positionX;
                var worldY = (y - image.getHeight() / 2.0f) / zoom - positionY;
                
                if (!showContent) {
                    if (MathUtils.ceil(worldX) == paddingLeft) {
                        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.HorizontalResize);
                        currentHandle = HandleType.PADDING_LEFT;
                    } else if (MathUtils.floor(worldX) == regionWidth - paddingRight) {
                        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.HorizontalResize);
                        currentHandle = HandleType.PADDING_RIGHT;
                    } else if (MathUtils.floor(worldY) == regionHeight - paddingTop) {
                        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.VerticalResize);
                        currentHandle = HandleType.PADDING_TOP;
                    } else if (MathUtils.ceil(worldY) == paddingBottom) {
                        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.VerticalResize);
                        currentHandle = HandleType.PADDING_BOTTOM;
                    } else {
                        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                        currentHandle = HandleType.NONE;
                    }
                } else {
                    if (MathUtils.ceil(worldX) == contentLeft) {
                        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.HorizontalResize);
                        currentHandle = HandleType.CONTENT_LEFT;
                    } else if (MathUtils.floor(worldX) == regionWidth - contentRight) {
                        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.HorizontalResize);
                        currentHandle = HandleType.CONTENT_RIGHT;
                    } else if (MathUtils.floor(worldY) == regionHeight - contentTop) {
                        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.VerticalResize);
                        currentHandle = HandleType.CONTENT_TOP;
                    } else if (MathUtils.ceil(worldY) == contentBottom) {
                        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.VerticalResize);
                        currentHandle = HandleType.CONTENT_BOTTOM;
                    } else {
                        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                        currentHandle = HandleType.NONE;
                    }
                }
                return false;
            };

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (!dragging) {
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                    currentHandle = HandleType.NONE;
                }
            }
        };
        dragListener.setTapSquareSize(0);
        image.addListener(dragListener);
        
        table = new Table();
        add(table);
        
        var buttonStyle = new Button.ButtonStyle();
        buttonStyle.up = style.switchOff;
        buttonStyle.over = style.switchOffOver;
        buttonStyle.checked = style.switchOn;
        buttonStyle.checkedOver = style.switchOnOver;
        paddingButton = new Button(buttonStyle);
        table.add(paddingButton).expand().top().left().pad(15);
        
        paddingButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                showContent = paddingButton.isChecked();
            }
        });
        
        dragListener = new DragListener() {
            private float startX;
            private float startY;
            private float widgetX;
            private float widgetY;
            
            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                if (!dragging) {
                    setPositionX(widgetX + (x - startX) / getZoom());
                    setPositionY(widgetY + (y - startY) / getZoom());
                }
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer) {
            }

            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                startX = x;
                startY = y;
                widgetX = getPositionX();
                widgetY = getPositionY();
            }
        };
        dragListener.setTapSquareSize(0);
        addCaptureListener(dragListener);
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        if (zoom < 1) zoom = 1;
        
        this.zoom = zoom;
        updateTileOffset();
    }
    
    private void updateTileOffset() {
        if (tile != null) {
            tile.setOffsetX((int) (positionX * zoom + getWidth() / 2));
            tile.setOffsetY((int) (positionY * zoom + getHeight() / 2));
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        updateTileOffset();
        super.draw(batch, parentAlpha);
    }

    public float getPositionX() {
        return positionX;
    }

    public void setPositionX(float positionX) {
        this.positionX = positionX;
        updateTileOffset();
    }

    public float getPositionY() {
        return positionY;
    }

    public void setPositionY(float positionY) {
        this.positionY = positionY;
        updateTileOffset();
    }
    
    public void setDrawable(Drawable drawable) {
        tile.setDrawable(drawable);
    }
    
    public Drawable getDrawable() {
        return tile.getDrawable();
    }

    public int getPaddingTop() {
        return paddingTop;
    }

    public void setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
        for (var listener : listeners) {
            listener.valueChange(HandleType.PADDING_TOP, paddingTop);
        }
    }

    public int getPaddingBottom() {
        return paddingBottom;
    }

    public void setPaddingBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
        for (var listener : listeners) {
            listener.valueChange(HandleType.PADDING_BOTTOM, paddingBottom);
        }
    }

    public int getPaddingLeft() {
        return paddingLeft;
    }

    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
        for (var listener : listeners) {
            listener.valueChange(HandleType.PADDING_LEFT, paddingLeft);
        }
    }

    public int getPaddingRight() {
        return paddingRight;
    }

    public void setPaddingRight(int paddingRight) {
        this.paddingRight = paddingRight;
        for (var listener : listeners) {
            listener.valueChange(HandleType.PADDING_RIGHT, paddingRight);
        }
    }

    public int getContentTop() {
        return contentTop;
    }

    public void setContentTop(int contentTop) {
        this.contentTop = contentTop;
        for (var listener : listeners) {
            listener.valueChange(HandleType.CONTENT_TOP, contentTop);
        }
    }

    public int getContentBottom() {
        return contentBottom;
    }

    public void setContentBottom(int contentBottom) {
        this.contentBottom = contentBottom;
        for (var listener : listeners) {
            listener.valueChange(HandleType.CONTENT_BOTTOM, contentBottom);
        }
    }

    public int getContentLeft() {
        return contentLeft;
    }

    public void setContentLeft(int contentLeft) {
        this.contentLeft = contentLeft;
        for (var listener : listeners) {
            listener.valueChange(HandleType.CONTENT_LEFT, contentLeft);
        }
    }

    public int getContentRight() {
        return contentRight;
    }

    public void setContentRight(int contentRight) {
        this.contentRight = contentRight;
        for (var listener : listeners) {
            listener.valueChange(HandleType.CONTENT_RIGHT, contentRight);
        }
    }

    public Button getPaddingButton() {
        return paddingButton;
    }

    public int getRegionWidth() {
        return regionWidth;
    }

    public void setRegionWidth(int regionWidth) {
        this.regionWidth = regionWidth;
    }

    public int getRegionHeight() {
        return regionHeight;
    }

    public void setRegionHeight(int regionHeight) {
        this.regionHeight = regionHeight;
    }
    
    public void addListener(NinePatchWidgetListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(NinePatchWidgetListener listener) {
        listeners.removeValue(listener, false);
    }
    
    public Array<NinePatchWidgetListener> getNinePatchWidgetListeners() {
        return new Array<>(listeners);
    }

    public GridType getGridType() {
        return gridType;
    }

    public void setGridType(GridType gridType) {
        this.gridType = gridType;
    }
    
    public static class NinePatchWidgetStyle {
        public Drawable lightTile;
        public Drawable darkTile;
        public Drawable paddingHandle;
        public Drawable paddingHandleOver;
        public Drawable paddingHandlePressed;
        public Drawable contentHandle;
        public Drawable contentHandleOver;
        public Drawable contentHandlePressed;
        public Drawable border;
        public Drawable switchOn;
        public Drawable switchOnOver;
        public Drawable switchOff;
        public Drawable switchOffOver;
        public Drawable black;
        public Drawable gridLight;
        public Drawable gridDark;
    }
    
    public static class TilePatternDrawable extends BaseDrawable {
        private Drawable drawable;
        private int offsetX, offsetY;
        private NinePatchWidgetStyle style;
        private NinePatchWidget widget;

        public TilePatternDrawable(NinePatchWidget widget, NinePatchWidgetStyle style) {
            this.style = style;
            this.widget = widget;
        }

        @Override
        public void draw(Batch batch, float x, float y, float width, float height) {
            if (widget.zoom == 1) {
                style.lightTile.draw(batch, x, y, width, height);
            } else {
                drawTiles(batch, x, y, width, height);
            }
            
            if (drawable != null) {
                drawDrawable(batch, x, y, width, height);
            }
            
            drawHandles(batch, x, y, width, height);
            
            drawBlackBars(batch, x, y, width, height);
            
            drawGrid(batch, x, y, width, height);
            
            drawBorder(batch, x, y, width, height);
        }
        
        private void drawTiles(Batch batch, float x, float y, float width, float height) {
            if (offsetX % widget.zoom > 0) {
                var flip = (offsetX / widget.zoom) % 2 == 0;
                if ((offsetY / widget.zoom) % 2 == 0) flip = !flip;
                
                var posX = (int) x + offsetX % widget.zoom - widget.zoom;
                for (var posY = (int) y + offsetY % widget.zoom - widget.zoom; posY < y + height; posY += widget.zoom) {
                    flip = !flip;
                    
                    if (flip) {
                        style.lightTile.draw(batch, posX, posY, widget.zoom, widget.zoom);
                        style.darkTile.draw(batch, posX + widget.zoom, posY, widget.zoom, widget.zoom);
                    } else {
                        style.lightTile.draw(batch, posX + widget.zoom, posY, widget.zoom, widget.zoom);
                        style.darkTile.draw(batch, posX, posY, widget.zoom, widget.zoom);
                    }
                }
            }
            
            if (offsetY % widget.zoom > 0) {
                var flip = (offsetX / widget.zoom) % 2 == 0;
                if ((offsetY / widget.zoom) % 2 == 0) flip = !flip;
            
                var posY = (int) y + offsetY % widget.zoom - widget.zoom;
                for (var posX = (int) x + offsetX % widget.zoom; posX < x + width; posX += widget.zoom) {
                    flip = !flip;
                    if (!flip) {
                        style.lightTile.draw(batch, posX, posY, widget.zoom, widget.zoom);
                        style.darkTile.draw(batch, posX + widget.zoom, posY, widget.zoom, widget.zoom);
                    } else {
                        style.lightTile.draw(batch, posX + widget.zoom, posY, widget.zoom, widget.zoom);
                        style.darkTile.draw(batch, posX, posY, widget.zoom, widget.zoom);
                    }
                }
            }
            
            var flip = (offsetX / widget.zoom) % 2 == 0;
            if ((offsetY / widget.zoom) % 2 == 0) flip = !flip;
            
            for (var posY = (int) y + offsetY % widget.zoom; posY < y + height; posY += widget.zoom) {
                flip = !flip;
                
                for (var posX = (int) x + offsetX % widget.zoom; posX < x + width; posX += widget.zoom * 2) {
                    if (flip) {
                        style.lightTile.draw(batch, posX, posY, widget.zoom, widget.zoom);
                        style.darkTile.draw(batch, posX + widget.zoom, posY, widget.zoom, widget.zoom);
                    } else {
                        style.lightTile.draw(batch, posX + widget.zoom, posY, widget.zoom, widget.zoom);
                        style.darkTile.draw(batch, posX, posY, widget.zoom, widget.zoom);
                    }
                }
            }
        }
        
        private void drawDrawable(Batch batch, float x, float y, float width, float height) {
            drawable.draw(batch, x + offsetX, y + offsetY, drawable.getMinWidth() * widget.zoom, drawable.getMinHeight() * widget.zoom);
        }
        
        private void drawBlackBars(Batch batch, float x, float y, float width, float height) {
            if (!widget.showContent) {
                var barWidth = (widget.regionWidth - widget.paddingLeft - widget.paddingRight) * widget.zoom;
                if (barWidth > 0) {
                    style.black.draw(batch, x + offsetX + widget.paddingLeft * widget.zoom, y + height - widget.zoom, barWidth, widget.zoom);
                }

                var barHeight = (widget.regionHeight - widget.paddingBottom - widget.paddingTop) * widget.zoom;
                if (barHeight > 0) {
                    style.black.draw(batch, x, y + offsetY + widget.paddingBottom * widget.zoom, widget.zoom, barHeight);
                }
            } else {
                var barWidth = (widget.regionWidth - widget.contentLeft - widget.contentRight) * widget.zoom;
                if (barWidth > 0) {
                    style.black.draw(batch, x + offsetX + widget.contentLeft * widget.zoom, y, barWidth, widget.zoom);
                }

                var barHeight = (widget.regionHeight - widget.contentBottom - widget.contentTop) * widget.zoom;
                if (barHeight > 0) {
                    style.black.draw(batch, x + width - widget.zoom, y + offsetY + widget.contentBottom * widget.zoom, widget.zoom, barHeight);
                }
            }
        }
        
        private void drawHandles(Batch batch, float x, float y, float width, float height) {
            if (!widget.showContent) {
                if (widget.currentHandle == HandleType.PADDING_BOTTOM) {
                    if (widget.dragging) {
                        style.paddingHandlePressed.draw(batch, x, y + offsetY + (widget.paddingBottom - 1) * widget.zoom, width, widget.zoom);
                    } else {
                        style.paddingHandleOver.draw(batch, x, y + offsetY + (widget.paddingBottom - 1) * widget.zoom, width, widget.zoom);
                    }
                } else {
                    style.paddingHandle.draw(batch, x, y + offsetY + (widget.paddingBottom - 1) * widget.zoom, width, widget.zoom);
                }
                
                if (widget.currentHandle == HandleType.PADDING_LEFT) {
                    if (widget.dragging) {
                        style.paddingHandlePressed.draw(batch, x + offsetX + (widget.paddingLeft - 1) * widget.zoom, y, widget.zoom, height);
                    } else {
                        style.paddingHandleOver.draw(batch, x + offsetX + (widget.paddingLeft - 1) * widget.zoom, y, widget.zoom, height);
                    }
                } else {
                    style.paddingHandle.draw(batch, x + offsetX + (widget.paddingLeft - 1) * widget.zoom, y, widget.zoom, height);
                }
                
                if (widget.currentHandle == HandleType.PADDING_TOP) {
                    if (widget.dragging) {
                        style.paddingHandlePressed.draw(batch, x, y + offsetY + (widget.regionHeight - widget.paddingTop) * widget.zoom, width, widget.zoom);
                    } else {
                        style.paddingHandleOver.draw(batch, x, y + offsetY + (widget.regionHeight - widget.paddingTop) * widget.zoom, width, widget.zoom);
                    }
                } else {
                    style.paddingHandle.draw(batch, x, y + offsetY + (widget.regionHeight - widget.paddingTop) * widget.zoom, width, widget.zoom);
                }
                
                if (widget.currentHandle == HandleType.PADDING_RIGHT) {
                    if (widget.dragging) {
                        style.paddingHandlePressed.draw(batch, x + offsetX + (widget.regionWidth - widget.paddingRight) * widget.zoom, y, widget.zoom, height);
                    } else {
                        style.paddingHandleOver.draw(batch, x + offsetX + (widget.regionWidth - widget.paddingRight) * widget.zoom, y, widget.zoom, height);
                    }
                } else {
                    style.paddingHandle.draw(batch, x + offsetX + (widget.regionWidth - widget.paddingRight) * widget.zoom, y, widget.zoom, height);
                }
            } else {
                if (widget.currentHandle == HandleType.CONTENT_BOTTOM) {
                    if (widget.dragging) {
                        style.contentHandlePressed.draw(batch, x, y + offsetY + (widget.contentBottom - 1) * widget.zoom, width, widget.zoom);
                    } else {
                        style.contentHandleOver.draw(batch, x, y + offsetY + (widget.contentBottom - 1) * widget.zoom, width, widget.zoom);
                    }
                } else {
                    style.contentHandle.draw(batch, x, y + offsetY + (widget.contentBottom - 1) * widget.zoom, width, widget.zoom);
                }
                
                if (widget.currentHandle == HandleType.CONTENT_LEFT) {
                    if (widget.dragging) {
                        style.contentHandlePressed.draw(batch, x + offsetX + (widget.contentLeft - 1) * widget.zoom, y, widget.zoom, height);
                    } else {
                        style.contentHandleOver.draw(batch, x + offsetX + (widget.contentLeft - 1) * widget.zoom, y, widget.zoom, height);
                    }
                } else {
                    style.contentHandle.draw(batch, x + offsetX + (widget.contentLeft - 1) * widget.zoom, y, widget.zoom, height);
                }
                
                if (widget.currentHandle == HandleType.CONTENT_TOP) {
                    if (widget.dragging) {
                        style.contentHandlePressed.draw(batch, x, y + offsetY + (widget.regionHeight - widget.contentTop) * widget.zoom, width, widget.zoom);
                    } else {
                        style.contentHandleOver.draw(batch, x, y + offsetY + (widget.regionHeight - widget.contentTop) * widget.zoom, width, widget.zoom);
                    }
                } else {
                    style.contentHandle.draw(batch, x, y + offsetY + (widget.regionHeight - widget.contentTop) * widget.zoom, width, widget.zoom);
                }
                
                if (widget.currentHandle == HandleType.CONTENT_RIGHT) {
                    if (widget.dragging) {
                        style.contentHandlePressed.draw(batch, x + offsetX + (widget.regionWidth - widget.contentRight) * widget.zoom, y, widget.zoom, height);
                    } else {
                        style.contentHandleOver.draw(batch, x + offsetX + (widget.regionWidth - widget.contentRight) * widget.zoom, y, widget.zoom, height);
                    }
                } else {
                    style.contentHandle.draw(batch, x + offsetX + (widget.regionWidth - widget.contentRight) * widget.zoom, y, widget.zoom, height);
                }
            }
        }
        
        private void drawGrid(Batch batch, float x, float y, float width, float height) {
            Drawable drawable;
            
            switch (widget.gridType) {
                case LIGHT:
                    drawable = style.gridLight;
                    break;
                case DARK:
                    drawable = style.gridDark;
                    break;
                default:
                    drawable = null;
                    break;
            }
            
            if (drawable != null) {
                for (var drawY = 0; drawY <= widget.getRegionHeight(); drawY++) {
                    drawable.draw(batch, x, y + drawY * widget.zoom + offsetY, width, 1);
                }
                
                for (var drawX = 0; drawX <= widget.getRegionWidth(); drawX++) {
                    drawable.draw(batch, x + drawX * widget.zoom + offsetX, y, 1, height);
                }
            }
        }
        
        private void drawBorder(Batch batch, float x, float y, float width, float height) {
            style.border.draw(batch, x, y, width, height);
        }

        public Drawable getDrawable() {
            return drawable;
        }

        public void setDrawable(Drawable drawable) {
            this.drawable = drawable;
        }

        public int getOffsetX() {
            return offsetX;
        }

        public void setOffsetX(int offsetX) {
            this.offsetX = offsetX;
        }

        public int getOffsetY() {
            return offsetY;
        }

        public void setOffsetY(int offsetY) {
            this.offsetY = offsetY;
        }
    }
    
    public interface NinePatchWidgetListener {
        public void valueChange(HandleType handle, int value);
    }
}

