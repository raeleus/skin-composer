/*
 * The MIT License
 *
 * Copyright 2019 Raymond Buckley.
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
package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Scaling;
import com.ray3k.skincomposer.*;
import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.data.StyleProperty;
import com.ray3k.skincomposer.utils.Utils;
import com.ray3k.tenpatch.TenPatchDrawable;

import java.util.Locale;

/**
 * @author Raymond Buckley
 */
public class DialogTenPatch extends Dialog {
    public static final int SCROLL_AMOUNT = 1;
    private Main main;
    private Skin skin;
    private TenPatchDrawable tenPatchDrawable;
    private TenPatchWidget tenPatchWidget;
    private boolean zoomToMouse;
    private Vector2 temp;
    private FileHandle fileHandle;
    private StageResizeListener stageResizeListener;
    private Color previewColor;
    private static final Color DEFAULT_PREVIEW_COLOR = Color.WHITE;
    private ObjectMap<DrawableData, Drawable> drawablePairs;
    private DrawableData drawableData;
    private String originalName;
    
    public DialogTenPatch(Main main, DrawableData drawableData, boolean newDrawable, ObjectMap<DrawableData, Drawable> drawablePairs) {
        super("", main.getSkin(), "dialog");
        this.main = main;
        skin = main.getSkin();
        this.drawableData = drawableData;
        originalName = newDrawable ? "" : drawableData.name;
        zoomToMouse = false;
        temp = new Vector2();
        this.fileHandle = drawableData.file;
        previewColor = new Color(DEFAULT_PREVIEW_COLOR);
        this.drawablePairs = drawablePairs;
        
        setFillParent(true);
        populate();
        
        stageResizeListener = new StageResizeListener() {
            @Override
            public void resized(int width, int height) {
                pack();
                tenPatchWidget.center();
            }
        };
        main.getStage().addListener(stageResizeListener);
    }
    
    private void populate() {
        var root = getContentTable();
        root.pad(10);
        root.clear();
        
        var top = new Table();
        top.setTouchable(Touchable.enabled);
        var bottom = new Table();
        bottom.setTouchable(Touchable.enabled);
        var splitPane = new SplitPane(top, bottom, true, skin);
        root.add(splitPane).grow();
        splitPane.addListener(main.getVerticalResizeArrowListener());
        splitPane.addListener(new DragListener() {
            {
                setTapSquareSize(0f);
            }
            
            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                super.drag(event, x, y, pointer);
                
                if (event.getListenerActor().equals(event.getTarget())) {
                    tenPatchWidget.center();
                }
            }
        });
        
        var label = new Label("Ten Patch Editor", skin, "title-no-line");
        top.add(label);
        
        top.row();
        var table = new Table();
        top.add(table).growX().space(5);
        
        table.defaults().space(5);
        label = new Label("Name:", skin, "white");
        label.setName("nameLabel");
        table.add(label);
        
        var textField = new TextField(drawableData.name, skin);
        textField.setName("nameField");
        table.add(textField);
        textField.addListener(main.getIbeamListener());
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                drawableData.name = textField.getText();
                validateName();
            }
        });
        
        table.defaults().uniform().fill();
        var textButton = new TextButton("Load Patches", skin);
        table.add(textButton);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Runnable runnable = () -> {
                    String[] filterPatterns = null;
                    if (!Utils.isMac()) {
                        filterPatterns = new String[]{"*.9.png"};
                    }
                    var file = main.getDesktopWorker().openDialog("Load patches from file...", main.getProjectData().getLastDrawablePath(), filterPatterns, "Nine patch files");
                    if (file != null) {
                        Gdx.app.postRunnable(() -> {
                            var fileHandle = new FileHandle(file);
                            if (!fileHandle.name().toLowerCase(Locale.ROOT).endsWith(".9.png")) {
                                fileHandle = fileHandle.sibling(fileHandle.nameWithoutExtension() + ".9.png");
                            }
                            loadPatchesFromFile(fileHandle);
                        });
                    }
                };
    
                main.getDialogFactory().showDialogLoading(runnable);
            }
        });
        
        textButton = new TextButton("Save to File", skin);
        table.add(textButton);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Runnable runnable = () -> {
                    String[] filterPatterns = null;
                    if (!Utils.isMac()) {
                        filterPatterns = new String[]{"*.png"};
                    }
                    var file = main.getDesktopWorker().saveDialog("Save as 9patch...", main.getProjectData().getLastDrawablePath(), filterPatterns, "Image files");
                    if (file != null) {
                        Gdx.app.postRunnable(() -> {
                            var fileHandle = new FileHandle(file);
                            if (!fileHandle.name().toLowerCase(Locale.ROOT).endsWith(".9.png")) {
                                fileHandle = fileHandle.sibling(fileHandle.nameWithoutExtension() + ".9.png");
                            }
                            saveToImageFile(fileHandle);
                        });
                    }
                };
                
                main.getDialogFactory().showDialogLoading(runnable);
            }
        });
        
        textButton = new TextButton("Clear", skin);
        table.add(textButton);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                tenPatchWidget.getTenPatchData().clear();
            }
        });
        
        table.defaults().reset();
        textButton = new TextButton("More info...", skin);
        table.add(textButton).expandX().right();
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showAboutDialog();
            }
        });
        
        top.row();
        
        tenPatchWidget = new TenPatchWidget(skin);
        var tenPatchData = new TenPatchData();
        tenPatchWidget.setTenPatchData(tenPatchData);
        
        var pixmap = loadTextureFile(fileHandle);
        
        var texture = new Texture(pixmap);
        pixmap.dispose();
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        tenPatchWidget.setTextureRegion(new TextureRegion(texture));
        
        top.add(tenPatchWidget).grow();
        tenPatchWidget.getStretchSwitchButton().addListener(main.getHandListener());
        tenPatchWidget.getModeSwitchButton().addListener(main.getHandListener());
        tenPatchWidget.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                getStage().setScrollFocus(tenPatchWidget);
            }
            
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                zoomToMouse = false;
                if (pointer == -1) {
                    if (getStage() != null) {
                        getStage().setScrollFocus(null);
                    }
                }
            }
            
            @Override
            public boolean scrolled(InputEvent event, float x, float y, int amount) {
                zoomToMouse = true;
                Slider slider = findActor("ten-patch-zoom");
                slider.setValue(slider.getValue() - SCROLL_AMOUNT * amount);
                return true;
            }
        });
        tenPatchWidget.addListener(new TenPatchWidget.TenPatchListener() {
            @Override
            public void valueChanged(TenPatchData tenPatchData) {
                tenPatchDrawable.setHorizontalStretchAreas(sanitizeStretchAreas(tenPatchData.horizontalStretchAreas, true));
                tenPatchDrawable.setVerticalStretchAreas(sanitizeStretchAreas(tenPatchData.verticalStretchAreas, false));
                tenPatchDrawable.setLeftWidth(tenPatchData.contentLeft);
                tenPatchDrawable.setRightWidth(tenPatchData.contentRight);
                tenPatchDrawable.setTopHeight(tenPatchData.contentTop);
                tenPatchDrawable.setBottomHeight(tenPatchData.contentBottom);
                
                updatePreview();
            }
        });
        
        top.row();
        table = new Table();
        table.pad(5);
        top.add(table).growX();
        
        table.defaults().space(5);
        var imageButton = new ImageButton(skin, "grid-light");
        imageButton.setName("grid-light");
        imageButton.setProgrammaticChangeEvents(false);
        table.add(imageButton);
        imageButton.addListener(main.getHandListener());
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                var imageButton = (ImageButton) actor;
                if (imageButton.isChecked()) {
                    tenPatchWidget.setGridMode(TenPatchWidget.GridMode.LIGHT);
                } else {
                    tenPatchWidget.setGridMode(TenPatchWidget.GridMode.NONE);
                }
                
                imageButton = findActor("grid-dark");
                imageButton.setChecked(false);
            }
        });
        
        imageButton = new ImageButton(skin, "grid-dark");
        imageButton.setName("grid-dark");
        imageButton.setProgrammaticChangeEvents(false);
        table.add(imageButton);
        imageButton.addListener(main.getHandListener());
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                var imageButton = (ImageButton) actor;
                if (imageButton.isChecked()) {
                    tenPatchWidget.setGridMode(TenPatchWidget.GridMode.DARK);
                } else {
                    tenPatchWidget.setGridMode(TenPatchWidget.GridMode.NONE);
                }
                
                imageButton = findActor("grid-light");
                imageButton.setChecked(false);
            }
        });
        
        var checkBox = new CheckBox("Tile", skin);
        table.add(checkBox).expandX().right();
        checkBox.addListener(main.getHandListener());
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                tenPatchData.tile = ((CheckBox) actor).isChecked();
                tenPatchDrawable.setTiling(tenPatchData.tile);
            }
        });
        
        imageButton = new ImageButton(skin, "color");
        table.add(imageButton).spaceLeft(10);
        imageButton.addListener(main.getHandListener());
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                main.getDialogFactory().showDialogColors(new StyleProperty(), colorData -> {
                    Label label = findActor("color-label");
                    Container<Label> container = findActor("color-label-container");
                    if (colorData == null) {
                        label.setText("Color: none");
                        label.setColor(Color.WHITE);
                        container.setColor(Color.BLACK);
                        tenPatchData.color.set(Color.WHITE);
                        tenPatchData.colorName = null;
                        tenPatchDrawable.getColor().set(Color.WHITE);
                    } else {
                        label.setText("Color: " + colorData.getName());
                        label.setColor(colorData.color);
                        container.setColor(Utils.blackOrWhiteBgColor(colorData.color));
                        tenPatchData.color.set(colorData.color);
                        tenPatchData.colorName = colorData.getName();
                        tenPatchDrawable.getColor().set(colorData.color);
                    }
                }, null);
            }
        });
        
        var container = new Container<Label>();
        container.setName("color-label-container");
        container.setBackground(skin.getDrawable("white"));
        container.pad(5);
        container.setColor(Color.BLACK);
        table.add(container).expandX().left();
        
        label = new Label("Color: None", skin, "white");
        label.setName("color-label");
        container.setActor(label);
        
        imageButton = new ImageButton(skin, "resize");
        table.add(imageButton);
        imageButton.addListener(main.getHandListener());
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                tenPatchWidget.zoomAndCenter();
                Slider slider = findActor("ten-patch-zoom");
                slider.setValue(tenPatchWidget.getZoomScale());
            }
        });
        
        var slider = new Slider(1, 100, 1f, false, skin, "zoom-horizontal");
        slider.setName("ten-patch-zoom");
        slider.setValue(1);
        table.add(slider);
        slider.addListener(main.getHandListener());
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                var slider = (Slider) actor;
                
                if (!zoomToMouse) {
                    tenPatchWidget.setZoomScale(slider.getValue());
                } else {
                    temp.set(Gdx.input.getX(), Gdx.input.getY());
                    tenPatchWidget.screenToLocalCoordinates(temp);
                    tenPatchWidget.setZoomScale(temp.x, temp.y, slider.getValue());
                }
            }
        });
        
        label = new Label("Preview", skin, "title-no-line");
        bottom.add(label);
        
        bottom.row();
        table = new Table();
        bottom.add(table).grow();
        
        var resizer = new ResizeWidget(null, skin);
        resizer.setTouchable(Touchable.enabled);
        resizer.setResizeFromCenter(true);
        
        var cursor = Utils.textureRegionToCursor(main.getSkin().getRegion("cursor_resize_ne"), 16, 16);
        var resizeFourArrowListener = new ResizeFourArrowListener(cursor);
        resizer.getBottomLeftHandle().addListener(resizeFourArrowListener);
        resizer.getTopRightHandle().addListener(resizeFourArrowListener);
        
        cursor = Utils.textureRegionToCursor(main.getSkin().getRegion("cursor_resize_nw"), 16, 16);
        resizeFourArrowListener = new ResizeFourArrowListener(cursor);
        resizer.getTopLeftHandle().addListener(resizeFourArrowListener);
        resizer.getBottomRightHandle().addListener(resizeFourArrowListener);
        
        cursor = Utils.textureRegionToCursor(main.getSkin().getRegion("cursor_resize_vertical"), 16, 16);
        resizeFourArrowListener = new ResizeFourArrowListener(cursor);
        resizer.getBottomHandle().addListener(resizeFourArrowListener);
        resizer.getTopHandle().addListener(resizeFourArrowListener);
        
        cursor = Utils.textureRegionToCursor(main.getSkin().getRegion("cursor_resize_horizontal"), 16, 16);
        resizeFourArrowListener = new ResizeFourArrowListener(cursor);
        resizer.getLeftHandle().addListener(resizeFourArrowListener);
        resizer.getRightHandle().addListener(resizeFourArrowListener);
        table.add(resizer).grow();
        
        tenPatchDrawable = new TenPatchDrawable(new int[0], new int[0], false, tenPatchWidget.getTextureRegion());
        table = new Table();
        table.setName("tenPatchTable");
        table.setBackground(tenPatchDrawable);
        resizer.setActor(table);
        
        bottom.row();
        table = new Table();
        bottom.add(table).growX();
        
        table.defaults().space(5);
        label = new Label("Content:", skin);
        table.add(label);
        
        var selectBox = new SelectBox<String>(skin);
        selectBox.setName("contentSelectBox");
        selectBox.setItems("None", "Text", "Color", "Drawable");
        table.add(selectBox);
        selectBox.addListener(main.getHandListener());
        selectBox.getList().addListener(main.getHandListener());
        selectBox.getList().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectBox.fire(new ChangeListener.ChangeEvent());
            }
        });
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                createPreview();
            }
        });
        
        imageButton = new ImageButton(skin, "color");
        table.add(imageButton).expandX().left();
        imageButton.addListener(main.getHandListener());
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                main.getDialogFactory().showDialogColors(new StyleProperty(), colorData -> {
                    if (colorData == null) {
                        previewColor.set(DEFAULT_PREVIEW_COLOR);
                    } else {
                        previewColor.set(colorData.color);
                    }
                    updatePreview();
                }, null);
                
            }
        });
        
        root = getButtonTable();
        root.pad(10);
        root.clearChildren();
        
        root.defaults().uniform().fill();
        textButton = new TextButton("OK", skin);
        textButton.setName("okayButton");
        button(textButton, true);
        validateName();
        textButton.addListener(main.getHandListener());
        
        textButton = new TextButton("Cancel", skin);
        button(textButton, false);
        textButton.addListener(main.getHandListener());
        
        tenPatchDrawable.setHorizontalStretchAreas(sanitizeStretchAreas(tenPatchWidget.getTenPatchData().horizontalStretchAreas, true));
        tenPatchDrawable.setVerticalStretchAreas(sanitizeStretchAreas(tenPatchWidget.getTenPatchData().verticalStretchAreas, false));
    
        if (fileHandle.name().matches("(?i:.*\\.9\\.png)")) {
            loadPatchesFromFile(fileHandle);
        }
    }
    
    private void createPreview() {
        Table table = findActor("tenPatchTable");
        table.clearChildren();
        
        SelectBox<String> selectBox = findActor("contentSelectBox");
        
        switch (selectBox.getSelected()) {
            case "Text":
                main.getDialogFactory().showInputDialog("Text Content", "Enter the text to be displayed inside of the preview:", "Lorem Ipsum", new DialogFactory.InputDialogListener() {
                    @Override
                    public void confirmed(String text) {
                        var label = new Label(text, skin, "white");
                        label.setAlignment(Align.center);
                        label.setColor(previewColor);
                        label.setWrap(true);
                        table.add(label).grow();
                    }
                    
                    @Override
                    public void cancelled() {
                        selectBox.setSelected("None");
                    }
                });
                break;
            case "Color":
                var image = new Image(skin, "white");
                image.setScaling(Scaling.stretch);
                image.setColor(previewColor);
                table.add(image).grow();
                break;
            case "Drawable":
                var dialog = main.getDialogFactory().showDialogDrawables(true, new DialogDrawables.DialogDrawablesListener() {
                    @Override
                    public void confirmed(DrawableData drawable) {
                        var image = new Image(drawablePairs.get(drawable));
                        image.setScaling(Scaling.none);
                        image.setColor(previewColor);
                        table.add(image).grow();
                    }
                    
                    @Override
                    public void emptied() {
                        selectBox.setSelected("None");
                    }
                    
                    @Override
                    public void cancelled() {
                        selectBox.setSelected("None");
                    }
                }, null);
                
                dialog.setShowing9patchButton(false);
                break;
        }
    }
    
    private void updatePreview() {
        Table table = findActor("tenPatchTable");
        if (table.getCells().size > 0) {
            var actor = table.getCells().first().getActor();
            table.clearChildren();
            
            SelectBox<String> selectBox = findActor("contentSelectBox");
            
            switch (selectBox.getSelected()) {
                case "Text":
                    var label = (Label) actor;
                    label.setColor(previewColor);
                    table.add(label).grow();
                    break;
                case "Color":
                    var image = (Image) actor;
                    image.setColor(previewColor);
                    table.add(image).grow();
                    break;
                case "Drawable":
                    image = (Image) actor;
                    image.setColor(previewColor);
                    table.add(image).grow();
                    break;
            }
        }
    }
    
    private Pixmap loadTextureFile(FileHandle fileHandle) {
        if (!fileHandle.name().matches("(?i:.*\\.9\\.png)")) {
            return new Pixmap(fileHandle);
        } else {
            var pixmap = new Pixmap(fileHandle);
            var returnValue = new Pixmap(Math.max(pixmap.getWidth() - 2, 1), Math.max(pixmap.getHeight() - 2, 1), pixmap.getFormat());
            returnValue.setBlending(Pixmap.Blending.None);
            returnValue.drawPixmap(pixmap, -1, -1);
            pixmap.dispose();
            return returnValue;
        }
    }
    
    private void saveToImageFile(FileHandle fileHandle) {
        var source = loadTextureFile(this.fileHandle);
        var pixmap = new Pixmap(source.getWidth() + 2, source.getHeight() + 2, source.getFormat());
        pixmap.setBlending(Pixmap.Blending.None);
        
        pixmap.drawPixmap(source, 1, 1);
        source.dispose();
        
        var tenPatchData = tenPatchWidget.getTenPatchData();
        
        pixmap.setColor(Color.BLACK);
        var stretchAreas = tenPatchData.horizontalStretchAreas;
        for (var i = 0; i + 1 < stretchAreas.size; i += 2) {
            pixmap.drawRectangle(stretchAreas.get(i) + 1, 0, stretchAreas.get(i + 1) - stretchAreas.get(i) + 1, 1);
        }
        
        stretchAreas = tenPatchData.verticalStretchAreas;
        for (var i = 0; i + 1 < stretchAreas.size; i += 2) {
            pixmap.drawRectangle(0, pixmap.getHeight() - stretchAreas.get(i + 1) - 2, 1, stretchAreas.get(i + 1) - stretchAreas.get(i) + 1);
        }
        
        pixmap.drawRectangle(tenPatchData.contentLeft, pixmap.getHeight() - 1, (pixmap.getWidth() - tenPatchData.contentRight) - tenPatchData.contentLeft, 1);
        
        pixmap.drawRectangle(pixmap.getWidth() - 1, tenPatchData.contentTop, 1, (pixmap.getHeight() - tenPatchData.contentBottom) - tenPatchData.contentTop);
        
        PixmapIO.writePNG(fileHandle, pixmap);
        pixmap.dispose();
    }
    
    private void loadPatchesFromFile(FileHandle fileHandle) {
        var tenPatchData = tenPatchWidget.getTenPatchData();
        tenPatchData.clear();
        var pixmap = new Pixmap(fileHandle);
        
        int x, y;
        y = pixmap.getHeight() - 1;
        var color = new Color();
        for (x = 1; x < pixmap.getWidth() - 1; x++) {
            color.set(pixmap.getPixel(x, y));
            if (color.a > 0) {
                tenPatchData.contentLeft = x;
                break;
            }
        }
        
        for (x = pixmap.getWidth() - 2; x >= 0; x--) {
            color.set(pixmap.getPixel(x, y));
            if (color.a > 0) {
                tenPatchData.contentRight = pixmap.getWidth() - x - 1;
                break;
            }
        }
        
        x = pixmap.getWidth() - 1;
        for (y = 1; y < pixmap.getHeight() - 1; y++) {
            color.set(pixmap.getPixel(x, y));
            if (color.a > 0) {
                tenPatchData.contentTop = y;
                break;
            }
        }
    
        x = pixmap.getWidth() - 1;
        for (y = pixmap.getHeight() - 2; y >= 0; y--) {
            color.set(pixmap.getPixel(x, y));
            if (color.a > 0) {
                tenPatchData.contentBottom = pixmap.getHeight() - y - 1;
                break;
            }
        }
        
        y = 0;
        for (x = 1; x < pixmap.getWidth() - 1; x++) {
            color.set(pixmap.getPixel(x, y));
            if (color.a > 0) {
                tenPatchData.horizontalStretchAreas.add(x - 1);
                tenPatchData.horizontalStretchAreas.add(x - 1);
            }
        }
        tenPatchWidget.getTenPatchData().combineContiguousSretchAreas(true);
        tenPatchWidget.getTenPatchData().removeInvalidStretchAreas(true);
    
        x = 0;
        for (y = pixmap.getHeight() - 2; y > 0; y--) {
            color.set(pixmap.getPixel(x, y));
            if (color.a > 0) {
                tenPatchData.verticalStretchAreas.add(pixmap.getHeight() - y - 2);
                tenPatchData.verticalStretchAreas.add(pixmap.getHeight() - y - 2);
            }
        }
        
        tenPatchWidget.getTenPatchData().combineContiguousSretchAreas(false);
        tenPatchWidget.getTenPatchData().removeInvalidStretchAreas(false);
    
        tenPatchDrawable.setHorizontalStretchAreas(sanitizeStretchAreas(tenPatchData.horizontalStretchAreas, true));
        tenPatchDrawable.setVerticalStretchAreas(sanitizeStretchAreas(tenPatchData.verticalStretchAreas, false));
        tenPatchDrawable.setLeftWidth(tenPatchData.contentLeft);
        tenPatchDrawable.setRightWidth(tenPatchData.contentRight);
        tenPatchDrawable.setTopHeight(tenPatchData.contentTop);
        tenPatchDrawable.setBottomHeight(tenPatchData.contentBottom);
        updatePreview();
    }
    
    public int[] sanitizeStretchAreas(IntArray stretchAreas, boolean horizontal) {
        var max = horizontal ? tenPatchDrawable.getRegion().getRegionWidth() - 1 : tenPatchDrawable.getRegion().getRegionHeight() - 1;
        var stretches = new IntArray(stretchAreas);
        
        if (stretches.size == 0) {
            stretches.add(0);
            stretches.add(max);
        }
        
        for (int i = 0; i < stretches.size; i++) {
            var value = stretches.get(i);
            if (value < 0) {
                stretches.set(i, 0);
            } else if (value > max) {
                stretches.set(i, max);
            }
        }
        
        stretches.sort();
        
        return stretches.toArray();
    }
    
    @Override
    public void act(float delta) {
        super.act(delta);
        if (Gdx.input.isKeyJustPressed(Keys.F5)) {
            populate();
            pack();
            tenPatchWidget.zoomAndCenter();
            Slider slider = findActor("ten-patch-zoom");
            slider.setValue(tenPatchWidget.getZoomScale());
        }
    }
    
    @Override
    public Dialog show(Stage stage, Action action) {
        super.show(stage, action);
        pack();
        tenPatchWidget.zoomAndCenter();
        Slider slider = findActor("ten-patch-zoom");
        slider.setValue(tenPatchWidget.getZoomScale());
        return this;
    }
    
    @Override
    protected void result(Object object) {
        super.result(object);
        if ((boolean) object == true) {
            fire(new DialogTenPatchEvent(tenPatchWidget.getTenPatchData()));
        } else {
            fire(new DialogTenPatchEvent(null));
        }
        
        main.getStage().removeListener(stageResizeListener);
    }
    
    public static abstract class DialogTenPatchListener implements EventListener {
        public abstract void selected(TenPatchData tenPatch);
        
        public abstract void cancelled();
        
        @Override
        public boolean handle(Event event) {
            if (event instanceof DialogTenPatchEvent) {
                selected(((DialogTenPatchEvent) event).tenPatch);
                return true;
            } else {
                cancelled();
                return false;
            }
        }
    }
    
    private static class DialogTenPatchEvent extends Event {
        public TenPatchData tenPatch;
        
        public DialogTenPatchEvent(TenPatchData tenPatch) {
            this.tenPatch = tenPatch;
        }
    }
    
    public static class TenPatchData {
        public IntArray horizontalStretchAreas = new IntArray();
        public IntArray verticalStretchAreas = new IntArray();
        public int contentLeft;
        public int contentRight;
        public int contentTop;
        public int contentBottom;
        public boolean tile;
        public transient Color color = new Color(Color.WHITE);
        public String colorName;
        
        public void clear() {
            horizontalStretchAreas.clear();
            verticalStretchAreas.clear();
            contentLeft = 0;
            contentRight = 0;
            contentTop = 0;
            contentBottom = 0;
            tile = false;
            color.set(Color.WHITE);
        }
    
        public void removeInvalidStretchAreas(boolean horizontal) {
            var stretchAreas = horizontal ? horizontalStretchAreas : verticalStretchAreas;
            for (var i = 0; i + 1 < stretchAreas.size; i += 2) {
                if (stretchAreas.get(i) > stretchAreas.get(i + 1)) {
                    stretchAreas.removeRange(i, i + 1);
                    i -= 2;
                }
            }
        }
    
        public void combineContiguousSretchAreas(boolean horizontal) {
            var stretchAreas = horizontal ? horizontalStretchAreas : verticalStretchAreas;
            for (var i = 1; i + 1 < stretchAreas.size; i += 2) {
            
                if (stretchAreas.get(i) == stretchAreas.get(i + 1) - 1) {
                    stretchAreas.removeRange(i, i + 1);
                    i -= 2;
                }
            }
        }
    
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof TenPatchData) {
                var other = (TenPatchData) obj;
                return horizontalStretchAreas.equals(other.horizontalStretchAreas) &&
                        verticalStretchAreas.equals(other.verticalStretchAreas) &&
                        contentLeft == other.contentLeft && contentRight == other.contentRight &&
                        contentTop == other.contentTop && contentBottom == other.contentBottom && tile == other.tile &&
                        (color == null && other.color == null || color != null && color.equals(other.color));
            } else {
                return false;
            }
        }
    }
    
    public void showAboutDialog() {
        var dialog = new Dialog("", skin, "dialog");
        
        var table = dialog.getContentTable();
        table.pad(10);
        
        var label = new Label("About Ten Patch", skin, "title-no-line");
        table.add(label);
        
        table.row();
        label = new Label("TenPatchDrawable is an alternative to libGDX's default implementation of 9patch. Added features include tiling and multiple stretch areas. These features are seamlessly integrated into your skin files. You must add the TenPatch dependency to your project.", skin);
        label.setWrap(true);
        table.add(label).growX();
        
        table.row();
        var subTable = new Table();
        table.add(subTable);
        
        label = new Label("See ", skin);
        subTable.add(label);
        
        var textButton = new TextButton("TenPatch on GitHub", skin, "link");
        subTable.add(textButton);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.net.openURI("https://github.com/raeleus/TenPatch");
            }
        });
        
        label = new Label(" for more information", skin);
        subTable.add(label);
        
        table = dialog.getButtonTable();
        table.pad(5);
        
        textButton = new TextButton("OK", skin);
        dialog.button(textButton);
        textButton.addListener(main.getHandListener());
        
        dialog.key(Keys.ENTER, true).key(Keys.ESCAPE, false);
        
        dialog.show(getStage());
        dialog.setWidth(425);
    }
    
    public void validateName() {
        var valid = drawableData.name != null && !drawableData.name.matches("^\\d.*|^-.*|.*\\s.*|.*[^a-zA-Z\\d\\s-_ñáéíóúüÑÁÉÍÓÚÜ].*|^$");
        
        if (valid && main.getProjectData().getAtlasData().getDrawable(drawableData.name) != null) {
            if (!drawableData.name.equals(originalName)) {
                valid = false;
            }
        }
        
        Button button = findActor("okayButton");
        button.setDisabled(!valid);
        
        Label label = findActor("nameLabel");
        label.setColor(valid ? skin.getColor("button") : Color.RED);
    }
}