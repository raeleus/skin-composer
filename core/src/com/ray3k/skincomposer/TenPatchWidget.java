/*
 * The MIT License
 *
 * Copyright 2024 Raymond Buckley.
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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.ray3k.skincomposer.dialog.tenpatch.DialogTenPatch.TenPatchData;

import static com.ray3k.skincomposer.Main.skin;

/**
 *
 * @author Raymond
 */
public class TenPatchWidget extends Stack {
    private static final float MINIMUM_HANDLE_RANGE = 4;
    private TenPatchWidgetStyle style;
    private TextureRegion textureRegion;
    private Button stretchSwitchButton;
    private Button modeSwitchButton;
    private TenPatchData tenPatchData;
    private float zoomScale;
    private Vector2 position;
    private boolean stretchMode;
    private boolean horizontalMode;
    private GridMode gridMode;
    private boolean newHandle;
    private int newHandleStart;
    private int newHandleEnd;
    private static Label numberLabel;
    private Color bgColor = new Color(Color.WHITE);
    
    public static enum GridMode {
        NONE, LIGHT, DARK
    }
    
    public TenPatchWidget(Skin skin) {
        this(skin, "default");
    }
    
    public TenPatchWidget(Skin skin, String style) {
        this(skin.get(style, TenPatchWidgetStyle.class));
    }
    
    public TenPatchWidget(TenPatchWidgetStyle style) {
        this.style = style;
        tenPatchData = new TenPatchData();
        zoomScale = 1f;
        position = new Vector2();
        gridMode = GridMode.NONE;
        horizontalMode = true;
        stretchMode = true;
        newHandle = false;
        
        populate();
    }
    
    public void populate() {
        clearChildren();
        
        var bgTable = new Table();
        bgTable.setBackground(style.border);
        bgTable.setClip(true);
        add(bgTable);
        
        //display
        var display = new TenPatchDisplay(this, style);
        bgTable.add(display).grow();
        
        //switch button
        var table = new Table();
        table.pad(15);
        add(table);
        var subTable = new Table();
        table.add(subTable).expand().top().left();
        
        subTable.defaults().space(5);
        var buttonStyle = new ButtonStyle();
        buttonStyle.up = style.stretchSwitchOff;
        buttonStyle.over = style.stretchSwitchOffOver;
        buttonStyle.checked = style.stretchSwitchOn;
        buttonStyle.checkedOver = style.stretchSwitchOnOver;
        stretchSwitchButton = new Button(buttonStyle);
        subTable.add(stretchSwitchButton);
        stretchSwitchButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stretchMode = !stretchSwitchButton.isChecked();
            }
        });
        
        subTable.row();
        buttonStyle = new ButtonStyle();
        buttonStyle.up = style.modeSwitchOff;
        buttonStyle.over = style.modeSwitchOffOver;
        buttonStyle.checked = style.modeSwitchOn;
        buttonStyle.checkedOver = style.modeSwitchOnOver;
        modeSwitchButton = new Button(buttonStyle);
        subTable.add(modeSwitchButton);
        modeSwitchButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                horizontalMode = !modeSwitchButton.isChecked();
            }
        });
    
        numberLabel = new Label("52", skin, "filter");
        numberLabel.setTouchable(Touchable.disabled);
        numberLabel.setColor(1, 1, 1, 0);
        numberLabel.setAlignment(Align.center);
        table.addActor(numberLabel);
        var dragListener = new DragListener() {
            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                super.drag(event, x, y, pointer);
                numberLabel.pack();
                numberLabel.setPosition(x, y, Align.bottom);
            }
        
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                numberLabel.pack();
                numberLabel.setPosition(x, y, Align.bottom);
                return super.mouseMoved(event, x, y);
            }
        };
        dragListener.setButton(-1);
        addListener(dragListener);
    }

    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    public void setTextureRegion(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }

    public Button getStretchSwitchButton() {
        return stretchSwitchButton;
    }

    public Button getModeSwitchButton() {
        return modeSwitchButton;
    }

    public TenPatchData getTenPatchData() {
        return tenPatchData;
    }
    
    public void setTenPatchData(TenPatchData tenPatchData) {
        this.tenPatchData = tenPatchData;
    }

    public float getZoomScale() {
        return zoomScale;
    }

    public void setZoomScale(float zoomScale) {
        setZoomScale(getWidth() / 2, getHeight() / 2, zoomScale);
    }
    
    public void setZoomScale(float zoomPointX, float zoomPointY, float zoomScale) {
        position.x -= (int) ((zoomScale - this.zoomScale) * (zoomPointX - position.x) / this.zoomScale);
        position.y -= (int) ((zoomScale - this.zoomScale) * (zoomPointY - position.y) / this.zoomScale);
        this.zoomScale = Math.max(zoomScale, 1);
    }

    public Vector2 getPosition() {
        return position;
    }
    
    public void zoomAndCenter() {
        if (textureRegion != null) {
            final var BORDER = 10;
            var zoomFactor = (int) ((getHeight() - BORDER) / textureRegion.getRegionHeight());
            
            if (zoomFactor * textureRegion.getRegionWidth() > getWidth() - BORDER) {
                zoomFactor = (int) ((getWidth() - BORDER) / textureRegion.getRegionWidth());
            }
            
            setZoomScale(zoomFactor);
            
            position.x = (int) (getWidth() / 2 - textureRegion.getRegionWidth() / 2 * zoomScale - zoomScale / 2);
            position.y = (int) (getHeight() / 2 - textureRegion.getRegionHeight() / 2 * zoomScale - zoomScale / 2);
        }
    }
    
    public void center() {
        if (textureRegion != null) {
            position.x = (int) (getWidth() / 2 - textureRegion.getRegionWidth() / 2 * zoomScale - zoomScale / 2);
            position.y = (int) (getHeight() / 2 - textureRegion.getRegionHeight() / 2 * zoomScale - zoomScale / 2);
        }
    }
    
    public GridMode getGridMode() {
        return gridMode;
    }
    
    public void setGridMode(GridMode gridMode) {
        this.gridMode = gridMode;
    }
    
    public Color getBgColor() {
        return bgColor;
    }
    
    public void setBgColor(Color bgColor) {
        this.bgColor.set(bgColor);
    }
    
    public TenPatchWidgetStyle getStyle() {
        return style;
    }
    
    public static class TenPatchWidgetStyle {
        //required
        public Drawable border;
        public Drawable stretchAreaHorizontal;
        public Drawable stretchAreaVertical;
        public Drawable contentAreaHorizontal;
        public Drawable contentAreaVertical;
        public Drawable gridDark;
        public Drawable gridLight;
        public Drawable stretchSwitchOn;
        public Drawable stretchSwitchOnOver;
        public Drawable stretchSwitchOff;
        public Drawable stretchSwitchOffOver;
        public Drawable modeSwitchOn;
        public Drawable modeSwitchOnOver;
        public Drawable modeSwitchOff;
        public Drawable modeSwitchOffOver;
    }
    
    private class TenPatchDisplay extends Widget {
        private TenPatchWidgetStyle style;
        private TenPatchWidget widget;

        public TenPatchDisplay(TenPatchWidget widget, TenPatchWidgetStyle style) {
            this.style = style;
            this.widget = widget;
            var panDragListener = new DragListener() {
                float startX;
                float startY;
                private float widgetX;
                private float widgetY;
                private boolean dragging = false;

                @Override
                public void drag(InputEvent event, float x, float y, int pointer) {
                    widget.getPosition().x = widgetX + (x - startX);
                    widget.getPosition().y = widgetY + (y - startY);
                }

                @Override
                public void dragStart(InputEvent event, float x, float y, int pointer) {
                    startX = x;
                    startY = y;
                    widgetX = widget.getPosition().x;
                    widgetY = widget.getPosition().y;
                    dragging = true;
                }
    
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    super.touchUp(event, x, y, pointer, button);
                }
            };
            panDragListener.setTapSquareSize(0);
            panDragListener.setButton(Input.Buttons.RIGHT);
            addListener(panDragListener);
            
            var clickListener = new ClickListener(Input.Buttons.RIGHT){
                boolean flipOrientation;
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    switch(this.getTapCount()) {
                        case 1:
                            if (!panDragListener.dragging) {
                                flipOrientation = true;
                                addAction(Actions.delay(.3f, new Action() {
                                    @Override
                                    public boolean act(float delta) {
                                        if (flipOrientation) {
                                            widget.modeSwitchButton.setChecked(!widget.modeSwitchButton.isChecked());
                                        }
                                        return true;
                                    }
                                }));
                            }
                            panDragListener.dragging = false;
                            break;
                        case 2:
                            this.flipOrientation = false;
                            widget.stretchSwitchButton.setChecked(!widget.stretchSwitchButton.isChecked());
                            break;
                    }
                }
            };
            clickListener.setTapSquareSize(0);
            clickListener.setTapCountInterval(.3f);
            addListener(clickListener);
            
            var handleDragListener = new DragListener() {
                float startX;
                float startY;
                boolean draggingHandle = false;
                private int dragIndex;
                
                @Override
                public void dragStart(InputEvent event, float x, float y, int pointer) {
                    super.dragStart(event, x, y, pointer);
                    startX = x;
                    startY = y;
    
                    if (widget.stretchMode) {
                        if (!draggingHandle) {
                            widget.newHandle = true;
                            if (widget.horizontalMode) {
                                widget.newHandleStart = (int) ((x - widget.position.x) / widget.zoomScale);
                                if (widget.newHandleStart < 0) widget.newHandleStart = 0;
                                if (widget.newHandleStart >= widget.textureRegion.getRegionWidth())
                                    widget.newHandleStart = widget.textureRegion.getRegionWidth() - 1;
                                widget.newHandleEnd = widget.newHandleStart;
                            } else {
                                widget.newHandleStart = (int) ((y - widget.position.y) / widget.zoomScale);
                                if (widget.newHandleStart < 0) widget.newHandleStart = 0;
                                if (widget.newHandleStart >= widget.textureRegion.getRegionHeight())
                                    widget.newHandleStart = widget.textureRegion.getRegionHeight() - 1;
                                widget.newHandleEnd = widget.newHandleStart;
                            }
    
                            var stretchAreas = widget.horizontalMode ? widget.tenPatchData.horizontalStretchAreas : widget.tenPatchData.verticalStretchAreas;
                            for (int i = 0; i + 1 < stretchAreas.size; i += 2) {
                                if (widget.newHandleStart >= stretchAreas.get(i) && widget.newHandleStart <= stretchAreas.get(i + 1)) {
                                    widget.newHandle = false;
                                    break;
                                }
                            }
                        } else {
                            widget.newHandle = false;
                        }
        
                        if (widget.newHandle) {
                            numberLabel.setColor(1, 1, 1, 1);
                        }
                    }
                }
    
                @Override
                public void drag(InputEvent event, float x, float y, int pointer) {
                    super.drag(event, x, y, pointer);
                    
                    if (draggingHandle) {
                        if (widget.stretchMode) {
                            if (widget.horizontalMode) {
                                var stretchAreas = widget.tenPatchData.horizontalStretchAreas;
                                var newValue = (int) ((x - widget.position.x) / widget.zoomScale);
                                if ((dragIndex & 1) == 0) {
                                    if (newValue < 0) newValue = 0;
                                    if (newValue > widget.textureRegion.getRegionWidth()) newValue = widget.textureRegion.getRegionWidth();
                                } else {
                                    if (newValue < -1) newValue = -1;
                                    if (newValue >= widget.textureRegion.getRegionWidth()) newValue = widget.textureRegion.getRegionWidth() - 1;
                                }
                                
                                if (dragIndex - 1 >= 0) {
                                    if ((dragIndex & 1) == 0) {
                                        if (newValue < stretchAreas.get(dragIndex - 1) + 1) {
                                            newValue = stretchAreas.get(dragIndex - 1) + 1;
                                        }
                                    } else {
                                        if (newValue < stretchAreas.get(dragIndex - 1) - 1) {
                                            newValue = stretchAreas.get(dragIndex - 1) - 1;
                                        }
                                    }
                                }
                                if (dragIndex + 1 < stretchAreas.size) {
                                    if ((dragIndex & 1) == 0) {
                                        if (newValue > stretchAreas.get(dragIndex + 1) + 1) {
                                            newValue = stretchAreas.get(dragIndex + 1) + 1;
                                        }
                                    } else {
                                        if (newValue > stretchAreas.get(dragIndex + 1) - 1) {
                                            newValue = stretchAreas.get(dragIndex + 1) - 1;
                                        }
                                    }
                                }
                                
                                stretchAreas.set(dragIndex, newValue);
                                numberLabel.setText(stretchAreas.get(dragIndex));
                                fire(new TenPatchEvent(widget.tenPatchData));
                            } else {
                                var stretchAreas = widget.tenPatchData.verticalStretchAreas;
                                var newValue = (int) ((y - widget.position.y) / widget.zoomScale);
                                if ((dragIndex & 1) == 0) {
                                    if (newValue < 0) newValue = 0;
                                    if (newValue > widget.textureRegion.getRegionHeight()) newValue = widget.textureRegion.getRegionHeight();
                                } else {
                                    if (newValue < -1) newValue = -1;
                                    if (newValue >= widget.textureRegion.getRegionHeight()) newValue = widget.textureRegion.getRegionHeight() - 1;
                                }
    
                                if (dragIndex - 1 >= 0) {
                                    if ((dragIndex & 1) == 0) {
                                        if (newValue < stretchAreas.get(dragIndex - 1) + 1) {
                                            newValue = stretchAreas.get(dragIndex - 1) + 1;
                                        }
                                    } else {
                                        if (newValue < stretchAreas.get(dragIndex - 1) - 1) {
                                            newValue = stretchAreas.get(dragIndex - 1) - 1;
                                        }
                                    }
                                }
                                if (dragIndex + 1 < stretchAreas.size) {
                                    if ((dragIndex & 1) == 0) {
                                        if (newValue > stretchAreas.get(dragIndex + 1) + 1) {
                                            newValue = stretchAreas.get(dragIndex + 1) + 1;
                                        }
                                    } else {
                                        if (newValue > stretchAreas.get(dragIndex + 1) - 1) {
                                            newValue = stretchAreas.get(dragIndex + 1) - 1;
                                        }
                                    }
                                }
                                
                                stretchAreas.set(dragIndex, newValue);
                                numberLabel.setText(stretchAreas.get(dragIndex));
                                fire(new TenPatchEvent(widget.tenPatchData));
                            }
                        } else {
                            if (widget.horizontalMode) {
                                if (dragIndex == 0) {
                                    var newValue = (int) ((x - widget.position.x) / widget.zoomScale);
                                    if (newValue < 0) newValue = 0;
                                    if (newValue > widget.textureRegion.getRegionWidth()) newValue = widget.textureRegion.getRegionWidth();
                                    if (newValue > widget.textureRegion.getRegionWidth() - widget.tenPatchData.contentRight - 1) newValue = widget.textureRegion.getRegionWidth() - widget.tenPatchData.contentRight - 1;
                                    widget.tenPatchData.contentLeft = newValue;
                                    numberLabel.setText(widget.tenPatchData.contentLeft);
                                    fire(new TenPatchEvent(widget.tenPatchData));
                                } else {
                                    var newValue = widget.textureRegion.getRegionWidth() - (int) ((x - widget.position.x) / widget.zoomScale);
                                    if (newValue < 0) newValue = 0;
                                    if (newValue > widget.textureRegion.getRegionWidth()) newValue = widget.textureRegion.getRegionWidth();
                                    if (newValue > widget.textureRegion.getRegionWidth() - widget.tenPatchData.contentLeft - 1) newValue = widget.textureRegion.getRegionWidth() - widget.tenPatchData.contentLeft - 1;
                                    widget.tenPatchData.contentRight = newValue;
                                    numberLabel.setText(widget.tenPatchData.contentRight);
                                    fire(new TenPatchEvent(widget.tenPatchData));
                                }
                            } else {
                                if (dragIndex == 0) {
                                    var newValue = (int) ((y - widget.position.y) / widget.zoomScale);
                                    if (newValue < 0) newValue = 0;
                                    if (newValue > widget.textureRegion.getRegionHeight()) newValue = widget.textureRegion.getRegionHeight();
                                    if (newValue > widget.textureRegion.getRegionHeight() - widget.tenPatchData.contentTop - 1) newValue = widget.textureRegion.getRegionHeight() - widget.tenPatchData.contentTop - 1;
                                    widget.tenPatchData.contentBottom = newValue;
                                    numberLabel.setText(widget.tenPatchData.contentBottom);
                                    fire(new TenPatchEvent(widget.tenPatchData));
                                } else {
                                    var newValue = widget.textureRegion.getRegionHeight() - (int) ((y - widget.position.y) / widget.zoomScale);
                                    if (newValue < 0) newValue = 0;
                                    if (newValue > widget.textureRegion.getRegionHeight()) newValue = widget.textureRegion.getRegionHeight();
                                    if (newValue > widget.textureRegion.getRegionHeight() - widget.tenPatchData.contentBottom - 1) newValue = widget.textureRegion.getRegionHeight() - widget.tenPatchData.contentBottom - 1;
                                    widget.tenPatchData.contentTop = newValue;
                                    numberLabel.setText(widget.tenPatchData.contentTop);
                                    fire(new TenPatchEvent(widget.tenPatchData));
                                }
                            }
                        }
                    } else if (widget.newHandle) {
                        if (widget.horizontalMode) {
                            var minX = 0;
                            for (var i = 1; i < widget.tenPatchData.horizontalStretchAreas.size; i += 2) {
                                if (widget.newHandleEnd > widget.tenPatchData.horizontalStretchAreas.get(i)) {
                                    minX = widget.tenPatchData.horizontalStretchAreas.get(i) + 1;
                                } else {
                                    break;
                                }
                            }
                            
                            var maxX = widget.textureRegion.getRegionWidth() - 1;
                            for (int i = widget.tenPatchData.horizontalStretchAreas.size - 2; i >= 0; i -= 2) {
                                if (widget.newHandleEnd < widget.tenPatchData.horizontalStretchAreas.get(i)) {
                                    maxX = widget.tenPatchData.horizontalStretchAreas.get(i) - 1;
                                } else {
                                    break;
                                }
                            }
                            
                            var newValue = (int) ((x - widget.position.x) / widget.zoomScale);
                            if (newValue < minX) newValue = minX;
                            if (newValue > maxX) newValue = maxX;
                            widget.newHandleEnd = newValue;
                            numberLabel.setText(newValue);
                        } else {
                            var minY = 0;
                            for (var i = 1; i < widget.tenPatchData.verticalStretchAreas.size; i += 2) {
                                if (widget.newHandleEnd > widget.tenPatchData.verticalStretchAreas.get(i)) {
                                    minY = widget.tenPatchData.verticalStretchAreas.get(i) + 1;
                                } else {
                                    break;
                                }
                            }

                            var maxY = widget.textureRegion.getRegionHeight() - 1;
                            for (int i = widget.tenPatchData.verticalStretchAreas.size - 2; i >= 0; i -= 2) {
                                if (widget.newHandleEnd < widget.tenPatchData.verticalStretchAreas.get(i)) {
                                    maxY = widget.tenPatchData.verticalStretchAreas.get(i) - 1;
                                } else {
                                    break;
                                }
                            }

                            var newValue = (int) ((y - widget.position.y) / widget.zoomScale);
                            if (newValue < minY) newValue = minY;
                            if (newValue > maxY) newValue = maxY;
                            widget.newHandleEnd = newValue;
                            numberLabel.setText(newValue);
                        }
                    }
                }
    
                @Override
                public void dragStop(InputEvent event, float x, float y, int pointer) {
                    super.dragStop(event, x, y, pointer);
                    if (widget.newHandle) {
                        addNewStretchArea(widget.horizontalMode, widget.newHandleStart, widget.newHandleEnd);
                        widget.getTenPatchData().removeInvalidStretchAreas(widget.horizontalMode);
                        widget.getTenPatchData().combineContiguousSretchAreas(widget.horizontalMode);
                        
                        widget.newHandle = false;
                        fire(new TenPatchEvent(widget.tenPatchData));
                        numberLabel.setColor(1,1,1,0);
                    }
                    
                    draggingHandle = false;
                    
                    if (widget.stretchMode) {
                        widget.getTenPatchData().removeInvalidStretchAreas(widget.horizontalMode);
                        widget.getTenPatchData().combineContiguousSretchAreas(widget.horizontalMode);
                        fire(new TenPatchEvent(widget.tenPatchData));
                    }
                }
    
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    super.enter(event, x, y, pointer, fromActor);
                    if (draggingHandle || widget.newHandle) {
                        numberLabel.setColor(1,1,1,1);
                    }
                }
    
                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    super.exit(event, x, y, pointer, toActor);
                    numberLabel.setColor(1,1,1,0);
                }
    
                @Override
                public boolean mouseMoved(InputEvent event, float x, float y) {
                    
                    var resizeCursor = false;
                    if (widget.stretchMode) {
                        if (widget.horizontalMode) {
                            var stretchAreas = widget.tenPatchData.horizontalStretchAreas;
                            for (var index = 0; index < stretchAreas.size && !resizeCursor; index++) {
                                var handleX = stretchAreas.get(index);
                                if ((index & 1) == 0) {
                                    var min = MathUtils.floor((handleX - .25f) * widget.zoomScale + widget.position.x);
                                    var max = MathUtils.ceil((handleX + .25f) * widget.zoomScale + widget.position.x);
                                    
                                    if (max - min < MINIMUM_HANDLE_RANGE) {
                                        min = MathUtils.floor((handleX) * widget.zoomScale + widget.position.x - MINIMUM_HANDLE_RANGE / 2f);
                                        max = MathUtils.floor((handleX) * widget.zoomScale + widget.position.x + MINIMUM_HANDLE_RANGE / 2f);
                                    }
                                    
                                    if (x >= min && x < max) {
                                        resizeCursor = true;
                                        draggingHandle = true;
                                        dragIndex = index;
                                        break;
                                    } else {
                                        draggingHandle = false;
                                    }
                                } else {
                                    var min = MathUtils.floor((handleX + .75f) * widget.zoomScale + widget.position.x);
                                    var max = MathUtils.ceil((handleX + 1.25f) * widget.zoomScale + widget.position.x);
    
                                    if (max - min < MINIMUM_HANDLE_RANGE) {
                                        min = MathUtils.floor((handleX + 1) * widget.zoomScale + widget.position.x - MINIMUM_HANDLE_RANGE / 2f);
                                        max = MathUtils.floor((handleX + 1) * widget.zoomScale + widget.position.x + MINIMUM_HANDLE_RANGE / 2f);
                                    }
                                    
                                    if (x >= min && x < max) {
                                        resizeCursor = true;
                                        draggingHandle = true;
                                        dragIndex = index;
                                        break;
                                    } else {
                                        draggingHandle = false;
                                    }
                                }
                            }
                
                            if (resizeCursor) {
                                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.HorizontalResize);
                                numberLabel.setColor(1,1,1,1);
                                numberLabel.setText(stretchAreas.get(dragIndex));
                            } else {
                                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                                numberLabel.setColor(1,1,1,0);
                            }
                        } else {
                            var stretchAreas = widget.tenPatchData.verticalStretchAreas;
                            for (var index = 0; index < stretchAreas.size && !resizeCursor; index++) {
                                var handleY = stretchAreas.get(index);
                                if ((index & 1) == 0) {
                                    var min = MathUtils.floor((handleY - .25f) * widget.zoomScale + widget.position.y);
                                    var max = MathUtils.ceil((handleY + .25f) * widget.zoomScale + widget.position.y);
    
                                    if (max - min < MINIMUM_HANDLE_RANGE) {
                                        min = MathUtils.floor((handleY) * widget.zoomScale + widget.position.y - MINIMUM_HANDLE_RANGE / 2f);
                                        max = MathUtils.floor((handleY) * widget.zoomScale + widget.position.y + MINIMUM_HANDLE_RANGE / 2f);
                                    }
                                    
                                    if (y >= min && y < max) {
                                        resizeCursor = true;
                                        draggingHandle = true;
                                        dragIndex = index;
                                        break;
                                    } else {
                                        draggingHandle = false;
                                    }
                                } else {
                                    var min = MathUtils.floor((handleY + .75f) * widget.zoomScale + widget.position.y);
                                    var max = MathUtils.ceil((handleY + 1.25f) * widget.zoomScale + widget.position.y);
    
                                    if (max - min < MINIMUM_HANDLE_RANGE) {
                                        min = MathUtils.floor((handleY + 1) * widget.zoomScale + widget.position.y - MINIMUM_HANDLE_RANGE / 2f);
                                        max = MathUtils.floor((handleY + 1) * widget.zoomScale + widget.position.y + MINIMUM_HANDLE_RANGE / 2f);
                                    }
    
                                    if (y >= min && y < max) {
                                        resizeCursor = true;
                                        draggingHandle = true;
                                        dragIndex = index;
                                        break;
                                    } else {
                                        draggingHandle = false;
                                    }
                                }
                            }
                
                            if (resizeCursor) {
                                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.VerticalResize);
                                numberLabel.setColor(1,1,1,1);
                                numberLabel.setText(stretchAreas.get(dragIndex));
                            } else {
                                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                                numberLabel.setColor(1,1,1,0);
                            }
                        }
                    } else {
                        var region = widget.textureRegion;
                        if (widget.horizontalMode) {
                            var minLeft = widget.position.x + (widget.tenPatchData.contentLeft - .5f) * widget.zoomScale;
                            var maxLeft = widget.position.x + (widget.tenPatchData.contentLeft  + .5f) * widget.zoomScale;
    
                            if (maxLeft - minLeft < MINIMUM_HANDLE_RANGE) {
                                minLeft = widget.position.x + (widget.tenPatchData.contentLeft) * widget.zoomScale - MINIMUM_HANDLE_RANGE / 2;
                                maxLeft = widget.position.x + (widget.tenPatchData.contentLeft) * widget.zoomScale + MINIMUM_HANDLE_RANGE / 2;
                            }
    
                            var minRight = widget.position.x + (region.getRegionWidth() - widget.tenPatchData.contentRight - .5f) * widget.zoomScale;
                            var maxRight = widget.position.x + (region.getRegionWidth() - widget.tenPatchData.contentRight + .5f) * widget.zoomScale;
    
                            if (maxRight - minRight < MINIMUM_HANDLE_RANGE) {
                                minRight = widget.position.x + (region.getRegionWidth() - widget.tenPatchData.contentRight - .5f) * widget.zoomScale - MINIMUM_HANDLE_RANGE / 2;
                                maxRight = widget.position.x + (region.getRegionWidth() - widget.tenPatchData.contentRight + .5f) * widget.zoomScale + MINIMUM_HANDLE_RANGE / 2;
                            }
                            
                            if (x >= minLeft && x < maxLeft) {
                                resizeCursor = true;
                                draggingHandle = true;
                                dragIndex = 0;
                            } else if (x >= minRight && x < maxRight) {
                                resizeCursor = true;
                                draggingHandle = true;
                                dragIndex = 1;
                            } else {
                                draggingHandle = false;
                            }
                
                            if (resizeCursor) {
                                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.HorizontalResize);
                                numberLabel.setColor(1,1,1,1);
                                numberLabel.setText(dragIndex == 0 ? widget.tenPatchData.contentLeft : widget.tenPatchData.contentRight);
                            } else {
                                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                                numberLabel.setColor(1,1,1,0);
                            }
                        } else {
                            var minBottom = widget.position.y + (widget.tenPatchData.contentBottom - .5f) * widget.zoomScale;
                            var maxBottom = widget.position.y + (widget.tenPatchData.contentBottom  + .5f) * widget.zoomScale;
    
                            if (maxBottom - minBottom < MINIMUM_HANDLE_RANGE) {
                                minBottom = widget.position.y + (widget.tenPatchData.contentBottom) * widget.zoomScale - MINIMUM_HANDLE_RANGE / 2;
                                maxBottom = widget.position.y + (widget.tenPatchData.contentBottom) * widget.zoomScale + MINIMUM_HANDLE_RANGE / 2;
                            }
    
                            var minTop = widget.position.y + (region.getRegionHeight() - widget.tenPatchData.contentTop - .5f) * widget.zoomScale;
                            var maxTop = widget.position.y + (region.getRegionHeight() - widget.tenPatchData.contentTop + .5f) * widget.zoomScale;
    
                            if (maxTop - minTop < MINIMUM_HANDLE_RANGE) {
                                minTop = widget.position.y + (region.getRegionHeight() - widget.tenPatchData.contentTop - .5f) * widget.zoomScale - MINIMUM_HANDLE_RANGE / 2;
                                maxTop = widget.position.y + (region.getRegionHeight() - widget.tenPatchData.contentTop + .5f) * widget.zoomScale + MINIMUM_HANDLE_RANGE / 2;
                            }
                            
                            if (y >= minBottom && y < maxBottom) {
                                resizeCursor = true;
                                draggingHandle = true;
                                dragIndex = 0;
                            } else if (y >= minTop && y < maxTop) {
                                resizeCursor = true;
                                draggingHandle = true;
                                dragIndex = 1;
                            } else {
                                draggingHandle = false;
                            }
                
                            if (resizeCursor) {
                                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.VerticalResize);
                                numberLabel.setColor(1,1,1,1);
                                numberLabel.setText(dragIndex == 0 ? widget.tenPatchData.contentBottom : widget.tenPatchData.contentTop);
                            } else {
                                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                                numberLabel.setColor(1,1,1,0);
                            }
                        }
                    }
        
                    return super.mouseMoved(event, x, y);
                }
            };
            handleDragListener.setTapSquareSize(0);
            addListener(handleDragListener);
        }
        
        public void addNewStretchArea(boolean horizontal, int start, int end) {
            var stretchAreas = horizontal ? widget.tenPatchData.horizontalStretchAreas : widget.tenPatchData.verticalStretchAreas;
            
            stretchAreas.add(start);
            stretchAreas.add(end);
            stretchAreas.sort();
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            drawTiles(batch);
            drawRegion(batch);
            drawGrid(batch);
            drawHandles(batch);
            
            super.draw(batch, parentAlpha);
        }
        
        private void drawTiles(Batch batch) {
            var oldColor = new Color(batch.getColor());
            batch.setColor(bgColor);
            if (MathUtils.isEqual(1f, widget.getZoomScale())) {
                style.gridLight.draw(batch, getX(), getY(), getWidth(), getHeight());
            } else {
                var even = (int) (widget.position.y / widget.getZoomScale()) % 2 == 0;
                for (var y = -widget.getZoomScale() + widget.position.y % widget.getZoomScale(); y < getHeight(); y += widget.getZoomScale()) {
                    var drawLight = (int) (widget.position.x / widget.getZoomScale()) % 2 == 0 ? even : !even;
                    for (var x = -widget.getZoomScale() + widget.position.x % widget.getZoomScale(); x < getWidth(); x += widget.getZoomScale()) {
                        var drawable = drawLight ? style.gridLight : style.gridDark;
                        drawable.draw(batch, x + getX(), y + getY(), widget.getZoomScale(), widget.getZoomScale());
                        drawLight = !drawLight;
                    }
                    even = !even;
                }
            }
            batch.setColor(oldColor);
        }
        
        private void drawRegion(Batch batch) {
            var region = widget.textureRegion;
            if (region != null) {
                batch.draw(region, getX() + widget.position.x, getY() + widget.position.y, region.getRegionWidth() * widget.zoomScale, region.getRegionHeight() * widget.zoomScale);
            }
        }
        
        private void drawGrid(Batch batch) {
            var region = widget.textureRegion;
            if (region != null && widget.zoomScale > 2 && (widget.getGridMode() == GridMode.DARK || widget.getGridMode() == GridMode.LIGHT)) {
                var grid =  widget.getGridMode() == GridMode.LIGHT ? widget.getStyle().gridLight : widget.getStyle().gridDark;
                
                for (var x = 0; x < region.getRegionWidth() * widget.zoomScale; x += widget.zoomScale) {
                    grid.draw(batch, getX() + widget.position.x + x, getY() + widget.position.y, 1f, region.getRegionHeight() * widget.zoomScale);
                }
                
                for (var y = 0; y < region.getRegionHeight() * widget.zoomScale; y += widget.zoomScale) {
                    grid.draw(batch, getX() + widget.position.x, getY() + widget.position.y + y, region.getRegionWidth() * widget.zoomScale, 1f);
                }
            }
        }
        
        private void drawHandles(Batch batch) {
            var region = widget.textureRegion;
            if (widget.stretchMode) {
                if (widget.horizontalMode) {
                    var stretchAreas = widget.tenPatchData.horizontalStretchAreas;
                    for (var index = 0; index + 1 < stretchAreas.size; index += 2) {
                        var x = stretchAreas.get(index) * widget.zoomScale;
                        var width = (stretchAreas.get(index + 1) + 1) * widget.zoomScale - x;
                        widget.getStyle().stretchAreaHorizontal.draw(batch, getX() + widget.position.x + x, getY(), width, widget.getHeight());
                    }
                } else {
                    var stretchAreas = widget.tenPatchData.verticalStretchAreas;
                    for (var index = 0; index + 1 < stretchAreas.size; index += 2) {
                        var y = stretchAreas.get(index) * widget.zoomScale;
                        var height = (stretchAreas.get(index + 1) + 1) * widget.zoomScale - y;
                        widget.getStyle().stretchAreaVertical.draw(batch, getX(), getY() + widget.position.y + y, widget.getWidth(), height);
                    }
                }
                
                if (widget.newHandle) {
                    if (widget.horizontalMode) {
                        var x = Math.min(widget.newHandleStart, widget.newHandleEnd) * widget.zoomScale;
                        var width = (Math.max(widget.newHandleStart, widget.newHandleEnd) + 1) * widget.zoomScale - x;
                        widget.getStyle().stretchAreaHorizontal.draw(batch, getX() + widget.position.x + x, getY(), width, widget.getHeight());
                    } else {
                        var y = Math.min(widget.newHandleStart, widget.newHandleEnd) * widget.zoomScale;
                        var height = (Math.max(widget.newHandleStart, widget.newHandleEnd) + 1) * widget.zoomScale - y;
                        widget.getStyle().stretchAreaVertical.draw(batch, getX(), getY() + widget.position.y + y, widget.getWidth(), height);
                    }
                }
            } else {
                if (widget.horizontalMode) {
                    widget.getStyle().contentAreaHorizontal.draw(batch, getX() + widget.position.x + widget.tenPatchData.contentLeft * widget.zoomScale, getY(), (region.getRegionWidth() - widget.tenPatchData.contentRight - widget.tenPatchData.contentLeft) * widget.zoomScale, widget.getHeight());
                } else {
                    widget.getStyle().contentAreaVertical.draw(batch, getX(), getY() + widget.position.y + widget.tenPatchData.contentBottom * widget.zoomScale, widget.getWidth(), (region.getRegionHeight() - widget.tenPatchData.contentTop - widget.tenPatchData.contentBottom) * widget.zoomScale);
                }
            }
        }
    }
    
    private static class TenPatchEvent extends Event {
        private TenPatchData tenPatchData;
        public TenPatchEvent(TenPatchData tenPatchData) {
            this.tenPatchData = tenPatchData;
        }
    }
    
    public static abstract class TenPatchListener implements EventListener {
        @Override
        public boolean handle(Event event) {
            if (event instanceof TenPatchEvent) {
                var tenPatchEvent = (TenPatchEvent) event;
                valueChanged(tenPatchEvent.tenPatchData);
                return true;
            } else {
                return false;
            }
        }
    
        public abstract void valueChanged(TenPatchData tenPatchData);
    }
}