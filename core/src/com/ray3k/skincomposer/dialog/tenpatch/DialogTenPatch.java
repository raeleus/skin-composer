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
package com.ray3k.skincomposer.dialog.tenpatch;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Scaling;
import com.ray3k.skincomposer.FilesDroppedListener;
import com.ray3k.skincomposer.ResizeFourArrowListener;
import com.ray3k.skincomposer.StageResizeListener;
import com.ray3k.skincomposer.TenPatchWidget;
import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.data.StyleProperty;
import com.ray3k.skincomposer.dialog.DialogDrawables;
import com.ray3k.skincomposer.dialog.DialogDrawables.DialogDrawablesListener;
import com.ray3k.skincomposer.dialog.DialogFactory.InputDialogListener;
import com.ray3k.skincomposer.utils.Utils;
import com.ray3k.stripe.PopTable;
import com.ray3k.stripe.ResizeWidget;
import com.ray3k.tenpatch.TenPatchDrawable;
import com.ray3k.tenpatch.TenPatchDrawable.CrushMode;

import java.util.Locale;
import java.util.regex.Pattern;

import static com.ray3k.skincomposer.Main.*;

/**
 * @author Raymond Buckley
 */
public class DialogTenPatch extends Dialog {
    public static final int SCROLL_AMOUNT = 1;
    private TenPatchDrawable tenPatchDrawable;
    private TenPatchWidget tenPatchWidget;
    private boolean zoomToMouse;
    private Vector2 temp;
    private FileHandle fileHandle;
    private StageResizeListener stageResizeListener;
    private Color previewColor;
    private static final Color DEFAULT_PREVIEW_COLOR = Color.WHITE;
    private DrawableData drawableData;
    private String originalName;
    private FilesDroppedListener filesDroppedListener;
    private static DrawableData copiedDrawableData;
    private float splitValue;
    private TextureRegion originalRegion;
    
    public DialogTenPatch(DrawableData drawableData, boolean newDrawable) {
        super("", skin, "dialog");
        this.drawableData = drawableData;
        originalName = newDrawable ? "" : drawableData.name;
        zoomToMouse = false;
        temp = new Vector2();
        this.fileHandle = drawableData.file;
        previewColor = new Color(DEFAULT_PREVIEW_COLOR);
        splitValue = .75f;
        
        setFillParent(true);
        populate();
        
        stageResizeListener = new StageResizeListener() {
            @Override
            public void resized(int width, int height) {
                pack();
                tenPatchWidget.center();
            }
        };
        stage.addListener(stageResizeListener);
    
        filesDroppedListener = (Array<FileHandle> files) -> {
            if (files.size > 0 && files.first().name().toLowerCase(Locale.ROOT).endsWith(".9.png")) {
                Runnable runnable = () -> {
                    Gdx.app.postRunnable(() -> {
                        loadPatchesFromFile(files.first());
                    });
                };
            
                dialogFactory.showDialogLoading(runnable);
            }
        };
    
        desktopWorker.addFilesDroppedListener(filesDroppedListener);
    
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!(event.getTarget() instanceof TextField)) {
                    getStage().setKeyboardFocus(DialogTenPatch.this);
                }
                return super.touchDown(event, x, y, pointer, button);
            }
    
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                if (getStage().getKeyboardFocus().equals(DialogTenPatch.this)) {
                    if (keycode == Input.Keys.C) {
                        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)) {
                            showToast("Ten Patch data copied to clipboard!");
                            copiedDrawableData = new DrawableData(drawableData);
                        }
                    } else if (keycode == Keys.V) {
                        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)) {
                            if (copiedDrawableData != null) {
                                showToast("Ten Patch data pasted!");
                                
                                var file = drawableData.file;
                                drawableData.set(copiedDrawableData);
                                
                                TextField textField = findActor("nameField");
                                drawableData.name = textField.getText();
                                drawableData.file = file;
                                
                                var iter = drawableData.tenPatchData.regionNames.iterator();
                                while (iter.hasNext()) {
                                    var region = iter.next();
                                    if (atlasData.getDrawable(region) == null) {
                                        iter.remove();
                                    }
                                }
                                
                                if (drawableData.tenPatchData.contentLeft >= tenPatchWidget.getTextureRegion().getRegionWidth()) {
                                    drawableData.tenPatchData.contentLeft = tenPatchWidget.getTextureRegion().getRegionWidth() - 1;
                                }
    
                                if (drawableData.tenPatchData.contentRight >= tenPatchWidget.getTextureRegion().getRegionWidth() - drawableData.tenPatchData.contentLeft) {
                                    drawableData.tenPatchData.contentRight = tenPatchWidget.getTextureRegion().getRegionWidth() - drawableData.tenPatchData.contentLeft - 1;
                                }
    
                                if (drawableData.tenPatchData.contentBottom >= tenPatchWidget.getTextureRegion().getRegionHeight()) {
                                    drawableData.tenPatchData.contentBottom = tenPatchWidget.getTextureRegion().getRegionHeight() - 1;
                                }
    
                                if (drawableData.tenPatchData.contentTop >= tenPatchWidget.getTextureRegion().getRegionHeight() - drawableData.tenPatchData.contentBottom) {
                                    drawableData.tenPatchData.contentTop = tenPatchWidget.getTextureRegion().getRegionHeight() - drawableData.tenPatchData.contentBottom - 1;
                                }
    
                                var values = new IntArray();
                                for (int i = 0; i + 1 < drawableData.tenPatchData.horizontalStretchAreas.size; i += 2) {
                                    var value = drawableData.tenPatchData.horizontalStretchAreas.get(i);
                                    if (value < tenPatchWidget.getTextureRegion().getRegionWidth()) {
                                        values.add(value);
    
                                        value = drawableData.tenPatchData.horizontalStretchAreas.get(i + 1);
                                        if (value >= tenPatchWidget.getTextureRegion().getRegionWidth()) {
                                            value = tenPatchWidget.getTextureRegion().getRegionWidth();
                                        }
                                        values.add(value);
                                    } else {
                                        break;
                                    }
                                }
                                drawableData.tenPatchData.horizontalStretchAreas = values;
    
                                values = new IntArray();
                                for (int i = 0; i + 1 < drawableData.tenPatchData.verticalStretchAreas.size; i += 2) {
                                    var value = drawableData.tenPatchData.verticalStretchAreas.get(i);
                                    if (value < tenPatchWidget.getTextureRegion().getRegionHeight()) {
                                        values.add(value);
            
                                        value = drawableData.tenPatchData.verticalStretchAreas.get(i + 1);
                                        if (value >= tenPatchWidget.getTextureRegion().getRegionHeight()) {
                                            value = tenPatchWidget.getTextureRegion().getRegionHeight();
                                        }
                                        values.add(value);
                                    } else {
                                        break;
                                    }
                                }
                                drawableData.tenPatchData.verticalStretchAreas = values;
                                
                                populate();
                                tenPatchWidget.zoomAndCenter();
                            } else {
                                showToast("No Ten Patch data to paste!");
                            }
                        }
                    }
                }
                return true;
            }
        });
    
        if (fileHandle.name().matches("(?i:.*\\.9\\.png)") && newDrawable) {
            loadPatchesFromFile(fileHandle);
        }
    }
    
    private void populate() {
        setSize(stage.getWidth(), stage.getHeight());
        
        var root = getContentTable();
        root.pad(10);
        root.clear();
        
        var top = new Table();
        top.setTouchable(Touchable.enabled);
        var bottom = new Table();
        bottom.setBackground(skin.getDrawable("white"));
        bottom.setTouchable(Touchable.enabled);
        var splitPane = new SplitPane(top, bottom, true, skin);
        splitPane.setSplitAmount(splitValue);
        root.add(splitPane).grow();
        splitPane.addListener(verticalResizeArrowListener);
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
                
                splitValue = splitPane.getSplitAmount();
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
        table.add(textField).growX();
        textField.addListener(ibeamListener);
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                drawableData.name = textField.getText();
                validateName();
            }
        });
        
        table.defaults().uniform().fill();
        
        var textButton = new TextButton("Save to File", skin);
        table.add(textButton);
        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Runnable runnable = () -> {
                    var file = desktopWorker.saveDialog("Save as 9patch...", projectData.getLastDrawablePath(), "png", "Image files");
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
                
                dialogFactory.showDialogLoading(runnable);
            }
        });
        
        textButton = new TextButton("Clear", skin);
        table.add(textButton);
        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                drawableData.tenPatchData.clear();
            }
        });
        
        textButton = new TextButton("More info...", skin);
        table.add(textButton);
        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showAboutDialog();
            }
        });
        
        top.row();
        
        tenPatchWidget = new TenPatchWidget(skin);
        tenPatchWidget.setTenPatchData(drawableData.tenPatchData);
        
        tenPatchWidget.setTextureRegion(loadTextureFile(drawableData.file));
        
        top.add(tenPatchWidget).grow();
        tenPatchWidget.getStretchSwitchButton().addListener(handListener);
        tenPatchWidget.getModeSwitchButton().addListener(handListener);
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
            public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
                zoomToMouse = true;
                Slider slider = findActor("ten-patch-zoom");
                slider.setValue(slider.getValue() - SCROLL_AMOUNT * amountY);
                return true;
            }
        });
        tenPatchWidget.addListener(new TenPatchWidget.TenPatchListener() {
            @Override
            public void valueChanged(TenPatchData tenPatchData) {
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
        imageButton.addListener(handListener);
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
        imageButton.addListener(handListener);
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
        
        imageButton = new ImageButton(skin, "resize");
        table.add(imageButton).expandX().right();
        imageButton.addListener(handListener);
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
        slider.addListener(handListener);
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
        
        top.row();
        table = new Table();
        top.add(table).growX();
    
        table.defaults().space(3);
        table.pad(5);
    
        var imageTextButton = new ImageTextButton("Animation...", skin, "ten-patch-animation");
        table.add(imageTextButton);
        imageTextButton.addListener(handListener);
        imageTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                var dialog = new DialogTenPatchAnimation(drawableData);
                dialog.addListener(new DialogTenPatchAnimation.DialogTenPatchAnimationListener() {
                    @Override
                    public void animationUpdated(DialogTenPatchAnimation.DialogTenPatchAnimationEvent event) {
                        updatePreview();
                    }
                });
                dialog.show(getStage());
            }
        });
        
        textButton = new TextButton("Load Patches", skin);
        table.add(textButton);
        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Runnable runnable = () -> {
                    var file = desktopWorker.openDialog("Load patches from file...", projectData.getLastDrawablePath(), "9.png", "Nine patch files");
                    if (file != null) {
                        Gdx.app.postRunnable(() -> {
                            var fileHandle = new FileHandle(file);
                            if (fileHandle.name().toLowerCase(Locale.ROOT).endsWith(".9.png")) {
                                loadPatchesFromFile(fileHandle);
                            }
                        });
                    }
                };
            
                dialogFactory.showDialogLoading(runnable);
            }
        });
    
        textButton = new TextButton("Auto Patches", skin);
        table.add(textButton);
        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                var pixmap = new Pixmap(fileHandle);
    
                if (fileHandle.path().toLowerCase(Locale.ROOT).endsWith(".9.png")) {
                    var cropped = new Pixmap(pixmap.getWidth() - 2, pixmap.getHeight() - 2, Pixmap.Format.RGBA8888);
                    cropped.setBlending(Pixmap.Blending.None);
        
                    cropped.drawPixmap(pixmap, 0, 0, 1, 1, pixmap.getWidth() - 2, pixmap.getHeight() - 2);
                    pixmap.dispose();
                    pixmap = cropped;
                }
    
                var patches = Utils.calculatePatches(pixmap);
    
                drawableData.tenPatchData.horizontalStretchAreas.clear();
                drawableData.tenPatchData.horizontalStretchAreas.add(patches.left);
                drawableData.tenPatchData.horizontalStretchAreas.add(pixmap.getWidth() - patches.right - 1);
    
                drawableData.tenPatchData.verticalStretchAreas.clear();
                drawableData.tenPatchData.verticalStretchAreas.add(patches.bottom);
                drawableData.tenPatchData.verticalStretchAreas.add(pixmap.getHeight() - patches.top - 1);
                
                drawableData.tenPatchData.combineContiguousSretchAreas(true);
                drawableData.tenPatchData.removeInvalidStretchAreas(true);
                drawableData.tenPatchData.combineContiguousSretchAreas(false);
                drawableData.tenPatchData.removeInvalidStretchAreas(false);
    
                updatePreview();
                
                pixmap.dispose();
            }
        });
    
        imageTextButton = new ImageTextButton("More settings...", skin, "ten-patch-scrolling");
        table.add(imageTextButton);
        imageTextButton.addListener(handListener);
        imageTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                var dialog = new DialogTenPatchSettings(drawableData, tenPatchDrawable);
                dialog.addListener(new DialogTenPatchSettings.DialogTenPatchSettingsListener() {
                    @Override
                    public void settingsUpdated(DialogTenPatchSettings.DialogTenPatchSettingsEvent event) {
                        updatePreview();
                    }
                });
                dialog.show(getStage());
            }
        });
        
        label = new Label("Preview", skin, "title-no-line");
        bottom.add(label);
        
        bottom.row();
        table = new Table();
        bottom.add(table).grow();
        
        var resizer = new ResizeWidget(null, skin);
        resizer.setName("resizer");
        resizer.setTouchable(Touchable.enabled);
        resizer.setResizingFromCenter(true);
        resizer.setAllowDragging(false);
        
        var resizeFourArrowListener = new ResizeFourArrowListener(SystemCursor.NESWResize);
        resizer.getBottomLeftHandle().addListener(resizeFourArrowListener);
        resizer.getTopRightHandle().addListener(resizeFourArrowListener);
        
        resizeFourArrowListener = new ResizeFourArrowListener(SystemCursor.NWSEResize);
        resizer.getTopLeftHandle().addListener(resizeFourArrowListener);
        resizer.getBottomRightHandle().addListener(resizeFourArrowListener);
        
        resizeFourArrowListener = new ResizeFourArrowListener(SystemCursor.VerticalResize);
        resizer.getBottomHandle().addListener(resizeFourArrowListener);
        resizer.getTopHandle().addListener(resizeFourArrowListener);
        
        resizeFourArrowListener = new ResizeFourArrowListener(SystemCursor.HorizontalResize);
        resizer.getLeftHandle().addListener(resizeFourArrowListener);
        resizer.getRightHandle().addListener(resizeFourArrowListener);
        table.add(resizer).grow();
    
        String name = drawableData.file.nameWithoutExtension();
        var matcher = Pattern.compile(".*(?=\\.9$)").matcher(name);
        if (matcher.find()) {
            name = matcher.group();
        }
        originalRegion = atlasData.getAtlas().findRegion(name);
        tenPatchDrawable = new TenPatchDrawable(new int[0], new int[0], false, originalRegion);
        if (drawableData.tenPatchData.colorName != null) {
            tenPatchDrawable.getColor().set(jsonData.getColorByName(drawableData.tenPatchData.colorName).color);
        }
        tenPatchDrawable.setTiling(drawableData.tenPatchData.tile);
        table = new Table();
        table.setName("tenPatchTable");
        table.setBackground(tenPatchDrawable);
        resizer.setActor(table);
        layout();
        resizer.getStack().setSize(100, 100);
        
        bottom.row();
        table = new Table();
        table.setBackground(skin.getDrawable("white"));
        table.pad(5);
        bottom.add(table).growX();
        
        table.defaults().space(5);
        label = new Label("Content:", skin);
        table.add(label).right();
        
        var selectBox = new SelectBox<String>(skin);
        selectBox.setName("contentSelectBox");
        selectBox.setItems("None", "Text", "Color", "Drawable");
        table.add(selectBox);
        selectBox.addListener(handListener);
        selectBox.getList().addListener(handListener);
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
        table.add(imageButton);
        imageButton.addListener(handListener);
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dialogFactory.showDialogColors(new StyleProperty(), (colorData, pressedCancel) -> {
                    if (colorData == null) {
                        previewColor.set(DEFAULT_PREVIEW_COLOR);
                    } else {
                        previewColor.set(colorData.color);
                    }
                    updatePreview();
                }, null);
                
            }
        });
        
        label = new Label("Background:", skin);
        table.add(label).padLeft(15);
    
        imageButton = new ImageButton(skin, "color");
        table.add(imageButton);
        imageButton.addListener(handListener);
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dialogFactory.showDialogColors(new StyleProperty(), (colorData, pressedCancel) -> {
                    if (colorData == null) {
                        bottom.setColor(Color.WHITE);
                    } else {
                        bottom.setColor(colorData.color);
                    }
                    updatePreview();
                    tenPatchWidget.setBgColor(bottom.getColor());
                }, null);
            
            }
        });
    
        table.add().expandX();
        
        root = getButtonTable();
        root.pad(10);
        root.clearChildren();
        
        root.defaults().uniform().fill();
        textButton = new TextButton("OK", skin);
        textButton.setName("okayButton");
        button(textButton, true);
        validateName();
        textButton.addListener(handListener);
        
        textButton = new TextButton("Cancel", skin);
        button(textButton, false);
        textButton.addListener(handListener);
        
        updatePreview();
        layout();
        tenPatchWidget.zoomAndCenter();
    }
    
    private void createPreview() {
        Table table = findActor("tenPatchTable");
        table.clearChildren();
        
        SelectBox<String> selectBox = findActor("contentSelectBox");
        
        switch (selectBox.getSelected()) {
            case "Text":
                dialogFactory.showInputDialog("Text Content", "Enter the text to be displayed inside of the preview:", "Lorem Ipsum", new InputDialogListener() {
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
                var dialog = dialogFactory.showDialogDrawables(true, new DialogDrawablesListener() {
                    @Override
                    public void confirmed(DrawableData drawable, DialogDrawables dialog) {
                        var image = new Image(atlasData.getDrawablePairs().get(drawable));
                        image.setScaling(Scaling.none);
                        image.setColor(previewColor);
                        table.add(image).grow();
                    }
                    
                    @Override
                    public void emptied(DialogDrawables dialog) {
                        selectBox.setSelected("None");
                    }
                    
                    @Override
                    public void cancelled(DialogDrawables dialog) {
                        selectBox.setSelected("None");
                    }
                }, null);
                
                dialog.setShowing9patchButton(false);
                break;
        }
    }
    
    private void updatePreview() {
        tenPatchDrawable.setHorizontalStretchAreas(sanitizeStretchAreas(drawableData.tenPatchData.horizontalStretchAreas, true));
        tenPatchDrawable.setVerticalStretchAreas(sanitizeStretchAreas(drawableData.tenPatchData.verticalStretchAreas, false));
        tenPatchDrawable.setLeftWidth(drawableData.tenPatchData.contentLeft);
        tenPatchDrawable.setRightWidth(drawableData.tenPatchData.contentRight);
        tenPatchDrawable.setTopHeight(drawableData.tenPatchData.contentTop);
        tenPatchDrawable.setBottomHeight(drawableData.tenPatchData.contentBottom);
        tenPatchDrawable.setMinWidth(drawableData.minWidth);
        tenPatchDrawable.setMinHeight(drawableData.minHeight);
        tenPatchDrawable.setTiling(drawableData.tenPatchData.tile);
        if (drawableData.tenPatchData.colorName != null) tenPatchDrawable.setColor(jsonData.getColorByName(drawableData.tenPatchData.colorName).color);
        if (drawableData.tenPatchData.color1Name != null) tenPatchDrawable.setColor1(jsonData.getColorByName(drawableData.tenPatchData.color1Name).color);
        if (drawableData.tenPatchData.color2Name != null) tenPatchDrawable.setColor2(jsonData.getColorByName(drawableData.tenPatchData.color2Name).color);
        if (drawableData.tenPatchData.color3Name != null) tenPatchDrawable.setColor3(jsonData.getColorByName(drawableData.tenPatchData.color3Name).color);
        if (drawableData.tenPatchData.color4Name != null) tenPatchDrawable.setColor4(jsonData.getColorByName(drawableData.tenPatchData.color4Name).color);
        tenPatchDrawable.setOffsetX(drawableData.tenPatchData.offsetX);
        tenPatchDrawable.setOffsetY(drawableData.tenPatchData.offsetY);
        tenPatchDrawable.setOffsetXspeed(drawableData.tenPatchData.offsetXspeed);
        tenPatchDrawable.setOffsetYspeed(drawableData.tenPatchData.offsetYspeed);
        if (drawableData.tenPatchData.regions != null && drawableData.tenPatchData.regions.size > 0) {
            tenPatchDrawable.setRegions(drawableData.tenPatchData.regions);
        } else {
            tenPatchDrawable.setRegions(null);
            tenPatchDrawable.setRegion(originalRegion);
        }
        tenPatchDrawable.setFrameDuration(drawableData.tenPatchData.frameDuration);
        tenPatchDrawable.setPlayMode(drawableData.tenPatchData.playMode);
        tenPatchDrawable.crushMode = drawableData.tenPatchData.crushMode;
        
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
    
    private Pixmap loadPixmapFile(FileHandle fileHandle) {
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
    
    private TextureRegion loadTextureFile(FileHandle fileHandle) {
        if (!fileHandle.name().matches("(?i:.*\\.9\\.png)")) {
            var texture = new Texture(fileHandle);
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            return new TextureRegion(texture);
        } else {
            var texture = new Texture(fileHandle);
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            return new TextureRegion(texture, 1, 1, texture.getWidth() - 2, texture.getHeight() - 2);
        }
    }
    
    private void saveToImageFile(FileHandle fileHandle) {
        var source = loadPixmapFile(this.fileHandle);
        var pixmap = new Pixmap(source.getWidth() + 2, source.getHeight() + 2, source.getFormat());
        pixmap.setBlending(Pixmap.Blending.None);
        
        pixmap.drawPixmap(source, 1, 1);
        source.dispose();
        
        var tenPatchData = drawableData.tenPatchData;
        
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
        var tenPatchData = drawableData.tenPatchData;
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
        drawableData.tenPatchData.combineContiguousSretchAreas(true);
        drawableData.tenPatchData.removeInvalidStretchAreas(true);
    
        x = 0;
        for (y = pixmap.getHeight() - 2; y > 0; y--) {
            color.set(pixmap.getPixel(x, y));
            if (color.a > 0) {
                tenPatchData.verticalStretchAreas.add(pixmap.getHeight() - y - 2);
                tenPatchData.verticalStretchAreas.add(pixmap.getHeight() - y - 2);
            }
        }
        
        drawableData.tenPatchData.combineContiguousSretchAreas(false);
        drawableData.tenPatchData.removeInvalidStretchAreas(false);
    
        updatePreview();
        pixmap.dispose();
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
        Slider slider = findActor("ten-patch-zoom");
        slider.setValue(tenPatchWidget.getZoomScale());
        return this;
    }
    
    @Override
    protected void result(Object object) {
        super.result(object);
        if ((boolean) object == true) {
            fire(new DialogTenPatchEvent(drawableData));
        } else {
            fire(new DialogTenPatchEvent(null));
        }
        
        stage.removeListener(stageResizeListener);
    }
    
    @Override
    public boolean remove() {
        desktopWorker.removeFilesDroppedListener(filesDroppedListener);
        return super.remove();
    }
    
    public static abstract class DialogTenPatchListener implements EventListener {
        public abstract void selected(DrawableData drawableData);
        
        public abstract void cancelled();
        
        @Override
        public boolean handle(Event event) {
            if (event instanceof DialogTenPatchEvent) {
                if (((DialogTenPatchEvent) event).drawableData != null) {
                    selected(((DialogTenPatchEvent) event).drawableData);
                } else {
                    cancelled();
                }
                return true;
            } else {
                return false;
            }
        }
    }
    
    private static class DialogTenPatchEvent extends Event {
        public DrawableData drawableData;
        
        public DialogTenPatchEvent(DrawableData drawableData) {
            this.drawableData = drawableData;
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
        public String colorName;
        public String color1Name;
        public String color2Name;
        public String color3Name;
        public String color4Name;
        public int offsetX;
        public int offsetY;
        public int offsetXspeed;
        public int offsetYspeed;
        public float frameDuration;
        public Array<String> regionNames = new Array<>();
        public transient Array<TextureRegion> regions;
        public int playMode;
        public int crushMode;
    
        public TenPatchData() {
            clear();
        }
        
        public TenPatchData(TenPatchData other) {
            set(other);
        }
    
        public void clear() {
            horizontalStretchAreas.clear();
            verticalStretchAreas.clear();
            contentLeft = 0;
            contentRight = 0;
            contentTop = 0;
            contentBottom = 0;
            tile = false;
            colorName = null;
            color1Name = null;
            color2Name = null;
            color3Name = null;
            color4Name = null;
            offsetX = 0;
            offsetY = 0;
            offsetXspeed = 0;
            offsetYspeed = 0;
            frameDuration = .03f;
            playMode = TenPatchDrawable.PlayMode.LOOP;
            crushMode = CrushMode.SHRINK;
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
                        offsetX == other.offsetX && offsetY == other.offsetY && offsetXspeed == other.offsetXspeed && offsetYspeed == other.offsetYspeed &&
                        (colorName == null && other.colorName == null || colorName != null && colorName.equals(other.colorName)) &&
                        (color1Name == null && other.color1Name == null || color1Name != null && color1Name.equals(other.color1Name)) &&
                        (color2Name == null && other.color2Name == null || color2Name != null && color2Name.equals(other.color2Name)) &&
                        (color3Name == null && other.color3Name == null || color3Name != null && color3Name.equals(other.color3Name)) &&
                        (color4Name == null && other.color4Name == null || color4Name != null && color4Name.equals(other.color4Name));
            } else {
                return false;
            }
        }
    
        public void set(TenPatchData other) {
            horizontalStretchAreas = new IntArray(other.horizontalStretchAreas);
            verticalStretchAreas = new IntArray(other.verticalStretchAreas);
            contentLeft = other.contentLeft;
            contentRight = other.contentRight;
            contentTop = other.contentTop;
            contentBottom = other.contentBottom;
            tile = other.tile;
            colorName = other.colorName;
            color1Name = other.color1Name;
            color2Name = other.color2Name;
            color3Name = other.color3Name;
            color4Name = other.color4Name;
            offsetX = other.offsetX;
            offsetY = other.offsetY;
            offsetXspeed = other.offsetXspeed;
            offsetYspeed = other.offsetYspeed;
            frameDuration = other.frameDuration;
            regionNames = new Array<>(other.regionNames);
            regions = other.regions == null ? null : new Array<>(other.regions);
            playMode = other.playMode;
            crushMode = other.crushMode;
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
        textButton.addListener(handListener);
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
        textButton.addListener(handListener);
        
        dialog.key(Keys.ENTER, true).key(Keys.NUMPAD_ENTER, true).key(Keys.ESCAPE, false);
        
        dialog.show(getStage());
        dialog.setWidth(425);
    }
    
    public void validateName() {
        var valid = drawableData.name != null && !drawableData.name.matches("^\\d.*|^-.*|.*\\s.*|.*[^a-zA-Z\\d\\s-_ñáéíóúüÑÁÉÍÓÚÜ].*|^$");
        
        if (valid && projectData.getAtlasData().getDrawable(drawableData.name) != null) {
            if (!drawableData.name.equals(originalName)) {
                valid = false;
            }
        }
        
        Button button = findActor("okayButton");
        button.setDisabled(!valid);
        
        Label label = findActor("nameLabel");
        label.setStyle(valid ? skin.get(LabelStyle.class) : skin.get("dialog-required", LabelStyle.class));
    }
    
    public void showToast(String message) {
        var popTable = new PopTable();
        popTable.setHideOnUnfocus(true);
        popTable.setBackground(skin.getDrawable("textfield"));
        
        popTable.pad(25);
        var label = new Label(message, skin);
        label.setTouchable(Touchable.disabled);
        popTable.add(label);
        
        popTable.show(getStage(), Actions.sequence(Actions.color(Color.CLEAR), Actions.fadeIn(.4f), Actions.delay(2f), Actions.run(() -> {
            popTable.hide(Actions.fadeOut(.5f));
        })));
    }
}