/*******************************************************************************
 * MIT License
 * 
 * Copyright (c) 2016 Raymond Buckley
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
package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Values;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Sort;
import com.ray3k.skincomposer.DialogFactory;
import com.ray3k.skincomposer.FilesDroppedListener;
import com.ray3k.skincomposer.IbeamListener;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.UndoableManager.DrawableUndoable;
import com.ray3k.skincomposer.data.AtlasData;
import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.data.JsonData;
import com.ray3k.skincomposer.data.ProjectData;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.data.StyleProperty;
import com.ray3k.skincomposer.utils.Utils;
import java.io.File;
import java.util.Iterator;
import java.util.List;

public class DialogDrawables extends Dialog {
    public static DialogDrawables instance;
    private static int[] sizes = {125, 150, 200, 250};
    private SelectBox sizeSelectBox;
    private ScrollPane scrollPane;
    private Slider zoomSlider;
    private StyleProperty property;
    private Array<DrawableData> drawables;
    private ObjectMap<DrawableData, Drawable> drawablePairs;
    private TextureAtlas atlas;
    private HorizontalGroup contentGroup;
    private FilesDroppedListener filesDroppedListener;
    private EventListener listener;
    private DialogFactory dialogFactory;
    private JsonData jsonData;
    private ProjectData projectData;
    private AtlasData atlasData;
    private Main main;
    
    public DialogDrawables(Skin skin, StyleProperty property, DialogFactory dialogFactory, JsonData jsonData, ProjectData projectData, AtlasData atlasData, Main main, EventListener listener) {
        this(skin, "default", property, dialogFactory, jsonData, projectData, atlasData, main, listener);
    }
    
    public DialogDrawables(Skin skin, String windowStyleName, StyleProperty property, DialogFactory dialogFactory, JsonData jsonData, ProjectData projectData, AtlasData atlasData, Main main, EventListener listener) {
        super("", skin, windowStyleName);
        
        this.dialogFactory = dialogFactory;
        this.jsonData = jsonData;
        this.projectData = projectData;
        this.atlasData = atlasData;
        this.main = main;
        
        instance = this;
        
        this.listener = listener;
        
        filesDroppedListener = (Array<FileHandle> files) -> {
            Iterator<FileHandle> iter = files.iterator();
            while (iter.hasNext()) {
                FileHandle file = iter.next();
                if (file.isDirectory() || !(file.name().toLowerCase().endsWith(".png") || file.name().toLowerCase().endsWith(".jpg") || file.name().toLowerCase().endsWith(".jpeg") || file.name().toLowerCase().endsWith(".bmp") || file.name().toLowerCase().endsWith(".gif"))) {
                    iter.remove();
                }
            }
            if (files.size > 0) {
                drawablesSelected(files);
            }
        };
        
        main.getDesktopWorker().addFilesDroppedListener(filesDroppedListener);
        
        this.property = property;
        drawablePairs = new ObjectMap<>();
        
        gatherDrawables();
        
        produceAtlas();
        
        populate();
    }
    
    /**
     * Recreates the drawables array only including visible drawables.
     */
    private void gatherDrawables() {
        drawables = new Array<>(atlasData.getDrawables());
        Iterator<DrawableData> iter = drawables.iterator();
        while(iter.hasNext()) {
            DrawableData drawable = iter.next();
            if (!drawable.visible) iter.remove();
        }
    }
    
    /**
     * Writes a TextureAtlas based on drawables list. Creates drawables to be
     * displayed on screen
     * @return 
     */
    private boolean produceAtlas() {
        try {
            if (atlas != null) {
                atlas.dispose();
                atlas = null;
            }
            if (!atlasData.atlasCurrent) {
                atlasData.writeAtlas();
                atlasData.atlasCurrent = true;
            }
            atlas = atlasData.getAtlas();

            for (DrawableData data : atlasData.getDrawables()) {
                String name = data.file.name();
                name = DrawableData.proper(name);
                
                Drawable drawable;
                if (data.file.name().matches(".*\\.9\\.[a-zA-Z0-9]*$")) {
                    drawable = new NinePatchDrawable(atlas.createPatch(name));
                    if (data.tint != null) {
                        drawable = ((NinePatchDrawable) drawable).tint(data.tint);
                    } else if (data.tintName != null) {
                        drawable = ((NinePatchDrawable) drawable).tint(jsonData.getColorByName(data.tintName).color);
                    }
                } else {
                    drawable = new SpriteDrawable(atlas.createSprite(name));
                    if (data.tint != null) {
                        drawable = ((SpriteDrawable) drawable).tint(data.tint);
                    } else if (data.tintName != null) {
                        drawable = ((SpriteDrawable) drawable).tint(jsonData.getColorByName(data.tintName).color);
                    }
                }
                
                drawablePairs.put(data, drawable);
            }
            return true;
        } catch (Exception e) {
            Gdx.app.error(getClass().getName(), "Error while attempting to generate drawables.", e);
            main.getDialogFactory().showDialogError("Atlas Error...","Error while attempting to generate drawables.\n\nOpen log?");
            return false;
        }
    }
    
    public void populate() {
        getContentTable().clear();
        
        getButtonTable().padBottom(15.0f);
        
        if (property == null) {
            getContentTable().add(new Label("Drawables", getSkin(), "title"));
        } else {
            getContentTable().add(new Label("Select a Drawables", getSkin(), "title"));
        }
        
        getContentTable().row();
        Table table = new Table(getSkin());
        table.defaults().pad(10.0f);
        getContentTable().add(table).growX();
        
        table.add("Sort by:");
        
        sizeSelectBox = new SelectBox(getSkin());
        sizeSelectBox.setItems("A-Z", "Z-A", "Oldest", "Newest");
        sizeSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                sortBySelectedMode();
            }
        });
        table.add(sizeSelectBox);
        
        TextButton textButton = new TextButton("Add Drawable", getSkin());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                newDrawableDialog();
            }
        });
        table.add(textButton);
        
        table.add(new Label("Zoom:", getSkin())).right().expandX();
        zoomSlider = new Slider(0, 3, 1, false, getSkin());
        zoomSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                refreshDrawableDisplay();
            }
        });
        table.add(zoomSlider);
        
        getContentTable().row();
        contentGroup = new HorizontalGroup();
        contentGroup.center().wrap(true).space(5.0f).wrapSpace(5.0f).rowAlign(Align.left);
        sortBySelectedMode();
        scrollPane = new ScrollPane(contentGroup, getSkin());
        scrollPane.setFadeScrollBars(false);
        scrollPane.setFlickScroll(false);
        getContentTable().add(scrollPane).grow();
        
        getContentTable().row();
        if (property != null) {
            button("Clear Drawable", true);
            button("Cancel", false);
        } else {
            button("Close", false);
        }
    }

    @Override
    public Dialog show(Stage stage, Action action) {
        Dialog dialog = super.show(stage, action);
        stage.setScrollFocus(scrollPane);
        return dialog;
    }
    
    private void refreshDrawableDisplay() {
        contentGroup.clear();
        
        TooltipManager manager = new TooltipManager();
        manager.animations = false;
        manager.initialTime = .4f;
        manager.resetTime = 0.0f;
        manager.subsequentTime = 0.0f;
        manager.hideAll();
        manager.instant();
        
        for (DrawableData drawable : drawables) {
            Button drawableButton;
            
            if (property != null) {
                drawableButton = new Button(getSkin(), "color-base");
                drawableButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        result(drawable);
                        hide();
                    }
                });
            } else {
                drawableButton = new Button(getSkin(), "color-base-static");
            }
            contentGroup.addActor(drawableButton);
            
            Table table = new Table();
            drawableButton.add(table).width(sizes[MathUtils.floor(zoomSlider.getValue())]).height(sizes[MathUtils.floor(zoomSlider.getValue())]);

            ClickListener fixDuplicateTouchListener = new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    event.setBubbles(false);
                    return super.touchDown(event, x, y, pointer, button);
                }
            };
            
            //color wheel
            Button button = new Button(getSkin(), "colorwheel");
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    newTintedDrawable(drawable);
                    event.setBubbles(false);
                }
            });
            button.addListener(fixDuplicateTouchListener);
            table.add(button);

            //swatches
            button = new Button(getSkin(), "swatches");
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    colorSwatchesDialog(drawable);
                    event.setBubbles(false);
                }
            });
            button.addListener(fixDuplicateTouchListener);
            table.add(button);
            
            //rename (ONLY FOR TINTS)
            if (drawable.tint != null || drawable.tintName != null) {
                button = new Button(getSkin(), "settings-small");
                button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        renameDrawableDialog(drawable);
                        event.setBubbles(false);
                    }
                });
                button.addListener(fixDuplicateTouchListener);
                table.add(button);
            } else {
                table.add();
            }

            //delete
            button = new Button(getSkin(), "delete-small");
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    deleteDrawable(drawable);
                    event.setBubbles(false);
                }
            });
            button.addListener(fixDuplicateTouchListener);
            table.add(button).expandX().right();

            //preview
            table.row();
            Container bg = new Container();
            bg.setClip(true);
            bg.setBackground(getSkin().getDrawable("white"));
            bg.setColor(drawable.bgColor);
            Image image = new Image(drawablePairs.get(drawable));
            if (MathUtils.isZero(zoomSlider.getValue())) {
                image.setScaling(Scaling.fit);
                bg.fill(false);
            } else {
                image.setScaling(Scaling.stretch);
                bg.fill();
            }
            bg.setActor(image);
            table.add(bg).colspan(4).grow();

            //name
            table.row();
            Label label = new Label(drawable.name, getSkin());
            label.setEllipsis("...");
            label.setEllipsis(true);
            label.setAlignment(Align.center);
            table.add(label).colspan(4).growX().width(sizes[MathUtils.floor(zoomSlider.getValue())]);
            
            //Tooltip
            TextTooltip toolTip = new TextTooltip(drawable.name, manager, getSkin());
            label.addListener(toolTip);
        }
    }
    
    private void colorSwatchesDialog(DrawableData drawableData) {
        DialogColors dialog = new DialogColors(getSkin(), "dialog", null, true, dialogFactory, jsonData, projectData, atlasData, main, (ColorData colorData) -> {
            if (colorData != null) {
                final DrawableData tintedDrawable = new DrawableData(drawableData.file);
                    tintedDrawable.tintName = colorData.getName();
                    
                    //Fix background color for new, tinted drawable
                    Color temp = Utils.averageEdgeColor(tintedDrawable.file, colorData.color);
                    
                    if (Utils.brightness(temp) > .5f) {
                        tintedDrawable.bgColor = Color.BLACK;
                    } else {
                        tintedDrawable.bgColor = Color.WHITE;
                    }
                    
                    final TextField textField = new TextField(drawableData.name, getSkin());
                    final TextButton button = new TextButton("OK", getSkin());
                    button.setDisabled(!DrawableData.validate(textField.getText()) || checkIfNameExists(textField.getText()));
                    textField.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            button.setDisabled(!DrawableData.validate(textField.getText()) || checkIfNameExists(textField.getText()));
                        }
                    });
                    textField.addListener(IbeamListener.get());

                    Dialog approveDialog = new Dialog("TintedDrawable...", getSkin()) {
                        @Override
                        protected void result(Object object) {
                            if (object instanceof Boolean && (boolean) object) {
                                tintedDrawable.name = textField.getText();
                                atlasData.getDrawables().add(tintedDrawable);
                                projectData.setChangesSaved(false);
                            }
                        }

                        @Override
                        public boolean remove() {
                            gatherDrawables();
                            produceAtlas();
                            sortBySelectedMode();
                            return super.remove();
                        }
                    };
                    approveDialog.addCaptureListener(new InputListener() {
                        @Override
                        public boolean keyDown(InputEvent event, int keycode2) {
                            if (keycode2 == Input.Keys.ENTER) {
                                if (!button.isDisabled()) {
                                    tintedDrawable.name = textField.getText();
                                    atlasData.getDrawables().add(tintedDrawable);
                                    projectData.setChangesSaved(false);
                                    approveDialog.hide();
                                }
                            }
                            return false;
                        }
                    });

                    approveDialog.getTitleTable().padLeft(5.0f);
                    approveDialog.getContentTable().padLeft(10.0f).padRight(10.0f).padTop(5.0f);
                    approveDialog.getButtonTable().padBottom(15.0f);
                    
                    approveDialog.text("What is the name of the new tinted drawable?");

                    Drawable drawable = drawablePairs.get(drawableData);
                    Drawable preview = null;
                    if (drawable instanceof SpriteDrawable) {
                        preview = ((SpriteDrawable) drawable).tint(colorData.color);
                    } else if (drawable instanceof NinePatchDrawable) {
                        preview = ((NinePatchDrawable) drawable).tint(colorData.color);
                    }
                    if (preview != null) {
                        approveDialog.getContentTable().row();
                        Table table = new Table();
                        table.setBackground(preview);
                        approveDialog.getContentTable().add(table);
                    }

                    approveDialog.getContentTable().row();
                    approveDialog.getContentTable().add(textField).growX();

                    approveDialog.button(button, true);
                    approveDialog.button("Cancel", false);
                    approveDialog.key(Input.Keys.ESCAPE, false);
                    approveDialog.show(getStage());
                    getStage().setKeyboardFocus(textField);
                    textField.selectAll();
            }
        });
        dialog.setFillParent(true);
        dialog.show(getStage());
        dialog.populate();
    }
    
    private void renameDrawableDialog(DrawableData drawable) {
        TextField textField = new TextField("", getSkin());
        Dialog dialog = new Dialog("Rename drawable?", getSkin()) {
            @Override
            protected void result(Object object) {
                super.result(object);
                
                if (object instanceof Boolean && (boolean) object == true) {
                    renameDrawable(drawable, textField.getText());
                }
            }

            @Override
            public Dialog show(Stage stage) {
                Dialog dialog = super.show(stage);
                stage.setKeyboardFocus(textField);
                return dialog;
            }
        };
        
        dialog.getTitleTable().padLeft(5.0f);
        dialog.getContentTable().padLeft(10.0f).padRight(10.0f).padTop(5.0f);
        dialog.getButtonTable().padBottom(15.0f);
        
        dialog.getContentTable().add(new Label("Please enter a new name for the drawable: ", getSkin()));
        
        dialog.button("OK", true);
        dialog.button("Cancel", false).key(Keys.ESCAPE, false);
        TextButton okButton = (TextButton) dialog.getButtonTable().getCells().first().getActor();
        okButton.setDisabled(true);
        
        dialog.getContentTable().row();
        textField.setText(drawable.name);
        textField.selectAll();
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                boolean disable = !DrawableData.validate(textField.getText());
                if (!disable) {
                    for (DrawableData data : atlasData.getDrawables()) {
                        if (data.name.equals(textField.getText())) {
                            disable = true;
                            break;
                        }
                    }
                }
                okButton.setDisabled(disable);
            }
        });
        textField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                if (c == '\n') {
                    if (!okButton.isDisabled()) {
                        renameDrawable(drawable, textField.getText());
                        dialog.hide();
                    }
                }
            }
        });
        dialog.getContentTable().add(textField);
        
        dialog.show(getStage());
    }
    
    private void renameDrawable(DrawableData drawable, String name) {
        String oldName = drawable.name;
        drawable.name = name;

        main.getUndoableManager().clearUndoables();
        updateStyleValuesForRename(oldName, name);
        
        main.getRootTable().refreshStyleProperties(true);
//        PanelPreviewProperties.instance.produceAtlas();
//        PanelPreviewProperties.instance.refreshPreview();
        
        projectData.setChangesSaved(false);
        
        sortBySelectedMode();
    }
    
    private void updateStyleValuesForRename(String oldName, String newName) {
        Values<Array<StyleData>> values = jsonData.getClassStyleMap().values();
        for (Array<StyleData> styles : values) {
            for (StyleData style : styles) {
                for (StyleProperty styleProperty : style.properties.values()) {
                    if (Drawable.class.isAssignableFrom(styleProperty.type)) {
                        if (styleProperty.value != null && styleProperty.value.equals(oldName)) {
                            styleProperty.value = newName;
                        }
                    }
                }
            }
        }
    }
    
    private void deleteDrawable(DrawableData drawable) {
        if (drawable.tint == null && drawable.tintName == null && checkDuplicateDrawables(drawable.file, 1)) {
            showConfirmDeleteDialog(drawable);
        } else {
            atlasData.getDrawables().removeValue(drawable, true);

            for (Array<StyleData> datas : jsonData.getClassStyleMap().values()) {
                for (StyleData data : datas) {
                    for (StyleProperty styleProperty : data.properties.values()) {
                        if (styleProperty != null && styleProperty.type.equals(Drawable.class) && styleProperty.value != null && styleProperty.value.equals(drawable.toString())) {
                            styleProperty.value = null;
                        }
                    }
                }
            }

//            PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
//            PanelPreviewProperties.instance.refreshPreview();

            main.getUndoableManager().clearUndoables();

            gatherDrawables();
            sortBySelectedMode();
        }
    }

    /**
     * Shows a dialog to confirm deletion of all TintedDrawables based on the
     * provided drawable data. This is called when the delete button is pressed
     * on a drawable in the drawable list.
     * @param drawable 
     */
    private void showConfirmDeleteDialog(DrawableData drawable) {
        Dialog dialog = new Dialog("Delete duplicates?", getSkin()){
            @Override
            protected void result(Object object) {
                if ((boolean) object) {
                    removeDuplicateDrawables(drawable.file);
                    gatherDrawables();
                    sortBySelectedMode();
                }
            }
        };
        
        dialog.getTitleTable().padLeft(5.0f);
        dialog.getContentTable().padLeft(10.0f).padRight(10.0f).padTop(5.0f);
        dialog.getButtonTable().padBottom(15.0f);
        
        dialog.text("Deleting this drawable will also delete one or more tinted drawables.\n"
                + "Delete duplicates?");
        dialog.button("OK", true);
        dialog.button("Cancel", false);
        dialog.key(Input.Keys.ENTER, true);
        dialog.key(Input.Keys.ESCAPE, false);
        dialog.show(getStage());
    }
    
    /**
     * Sorts by selected sort order and populates the list.
     */
    private void sortBySelectedMode() {
        switch (sizeSelectBox.getSelectedIndex()) {
            case 0:
                sortDrawablesAZ();
                break;
            case 1:
                sortDrawablesZA();
                break;
            case 2:
                sortDrawablesOldest();
                break;
            case 3:
                sortDrawablesNewest();
                break;
        }
    }
    
    /**
     * Sorts alphabetically from A to Z.
     */
    private void sortDrawablesAZ() {
        Sort.instance().sort(drawables, (DrawableData o1, DrawableData o2) -> o1.toString().compareToIgnoreCase(o2.toString()));
        refreshDrawableDisplay();
    }
    
    /**
     * Sorts alphabetically from Z to A.
     */
    private void sortDrawablesZA() {
        Sort.instance().sort(drawables, (DrawableData o1, DrawableData o2) -> o1.toString().compareToIgnoreCase(o2.toString()) * -1);
        refreshDrawableDisplay();
    }
    
    /**
     * Sorts by modified date with oldest first.
     */
    private void sortDrawablesOldest() {
        Sort.instance().sort(drawables, (DrawableData o1, DrawableData o2) -> {
            if (o1.file.lastModified() < o2.file.lastModified()) {
                return -1;
            } else if (o1.file.lastModified() > o2.file.lastModified()) {
                return 1;
            } else {
                return 0;
            }
        });
        refreshDrawableDisplay();
    }
    
    /**
     * Sorts by modified date with newest first.
     */
    private void sortDrawablesNewest() {
        Sort.instance().sort(drawables, (DrawableData o1, DrawableData o2) -> {
            if (o1.file.lastModified() < o2.file.lastModified()) {
                return 1;
            } else if (o1.file.lastModified() > o2.file.lastModified()) {
                return -1;
            } else {
                return 0;
            }
        });
        refreshDrawableDisplay();
    }
    
    /**
     * Checks if there are any drawables that have the same file name as the specified file.
     * This ignores the file extension.
     * @param handle
     * @param minimum The minimum allowed matches before it's considered a duplicate
     * @return 
     */
    private boolean checkDuplicateDrawables(FileHandle handle, int minimum) {
        int count = 0;
        String name = DrawableData.proper(handle.name());
        for (int i = 0; i < atlasData.getDrawables().size; i++) {
            DrawableData data = atlasData.getDrawables().get(i);
            if (name.equals(DrawableData.proper(data.file.name()))) {
                count++;
            }
        }
        
        return count > minimum;
    }
    
    /**
     * Removes any duplicate drawables that share the same file name. This
     * ignores the file extension and also deletes TintedDrawables from the
     * same file.
     * @param handle 
     */
    private void removeDuplicateDrawables(FileHandle handle) {
        boolean refreshDrawables = false;
        String name = DrawableData.proper(handle.name());
        for (int i = 0; i < atlasData.getDrawables().size; i++) {
            DrawableData data = atlasData.getDrawables().get(i);
            if (name.equals(DrawableData.proper(data.file.name()))) {
                atlasData.getDrawables().removeValue(data, true);
                
                for (Array<StyleData> datas : jsonData.getClassStyleMap().values()) {
                    for (StyleData tempData : datas) {
                        for (StyleProperty prop : tempData.properties.values()) {
                            if (prop != null && prop.type.equals(Drawable.class) && prop.value != null && prop.value.equals(data.toString())) {
                                prop.value = null;
                            }
                        }
                    }
                }
                
                refreshDrawables = true;
                i--;
            }
        }
        
//        PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
//        PanelPreviewProperties.instance.refreshPreview();
        
        if (refreshDrawables) {
            gatherDrawables();
        }
    }
    
    /**
     * Show an error indicating a drawable that exceeds project specifications
     */
    private void showDrawableError() {
        Dialog dialog = new Dialog("Error...", getSkin());
        
        dialog.getTitleTable().padLeft(5.0f);
        dialog.getContentTable().padLeft(10.0f).padRight(10.0f).padTop(5.0f);
        dialog.getButtonTable().padBottom(15.0f);
        
        Label label = new Label("Error while adding new drawables.\nEnsure that image dimensions are\nless than maximums specified in project.\nRolling back changes...", getSkin());
        label.setAlignment(Align.center);
        dialog.text(label);
        dialog.button("OK");
        dialog.show(getStage());
    }
    
    /**
     * Show a Java FX file chooser for new drawables. This allows selection of
     * multiple files at once.
     */
    private void newDrawableDialog() {
        String defaultPath = "";
        
        if (projectData.getLastDirectory() != null) {
            FileHandle fileHandle = new FileHandle(defaultPath);
            if (fileHandle.exists()) {
                defaultPath = projectData.getLastDirectory();
            }
        }
        
        String[] filterPatterns = {"*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif"};
        
        List<File> files = main.getDesktopWorker().openMultipleDialog("Choose drawable file(s)...", defaultPath, filterPatterns, "Image files");
        if (files != null && files.size() > 0) {
            drawablesSelected(files);
        }
    }

    /**
     * Called when a selection of drawables has been chosen from the
     * newDrawablesDialog(). Adds the new drawables to the project.
     * @param files 
     */
    private void drawablesSelected(List<File> files) {
        Array<FileHandle> fileHandles = new Array<>();
        
        for (File file : files) {
            fileHandles.add(new FileHandle(file));
        }
        
        drawablesSelected(fileHandles);
    }
    
    private void drawablesSelected(Array<FileHandle> files) {
        atlasData.atlasCurrent = false;
        Array<DrawableData> backup = new Array<>(atlasData.getDrawables());
        Array<FileHandle> unhandledFiles = new Array<>();
        Array<FileHandle> filesToProcess = new Array<>();
        
        projectData.setLastDirectory(files.get(0).parent().path());
        for (FileHandle fileHandle : files) {
            if (checkDuplicateDrawables(fileHandle, 0)) {
                unhandledFiles.add(fileHandle);
            } else {
                filesToProcess.add(fileHandle);
            }
        }
        
        if (unhandledFiles.size > 0) {
            showRemoveDuplicatesDialog(unhandledFiles, backup, filesToProcess);
        } else {
            finalizeDrawables(backup, filesToProcess);
        }
    }
    
    /**
     * Shows a dialog to confirm removal of duplicate drawables that have the
     * same name without extension. This is called after selecting new drawables.
     * @param unhandledFiles
     * @param backup
     * @param filesToProcess 
     */
    private void showRemoveDuplicatesDialog(Array<FileHandle> unhandledFiles, Array<DrawableData> backup, Array<FileHandle> filesToProcess) {
        Dialog dialog = new Dialog("Delete duplicates?", getSkin()){
            @Override
            protected void result(Object object) {
                if ((boolean) object) {
                    for (FileHandle fileHandle : unhandledFiles) {
                        removeDuplicateDrawables(fileHandle);
                        filesToProcess.add(fileHandle);
                    }
                }
                finalizeDrawables(backup, filesToProcess);
            }
        };
        
        dialog.getTitleTable().padLeft(5.0f);
        dialog.getContentTable().padLeft(10.0f).padRight(10.0f).padTop(5.0f);
        dialog.getButtonTable().padBottom(15.0f);
        
        dialog.text("Adding this drawable will overwrite one or more drawables\n"
                + "Delete duplicates?");
        dialog.button("OK", true);
        dialog.button("Cancel", false);
        dialog.key(Input.Keys.ENTER, true);
        dialog.key(Input.Keys.ESCAPE, false);
        dialog.show(getStage());
    }
    
    /**
     * Adds the drawables to the project.
     * @param backup If there is a failure, the drawable list will be rolled
     * back to the provided backup.
     * @param filesToProcess 
     */
    private void finalizeDrawables(Array<DrawableData> backup, Array<FileHandle> filesToProcess) {
        for (FileHandle file : filesToProcess) {
            DrawableData data = new DrawableData(file);
            if (!checkIfNameExists(data.name)) {
                atlasData.getDrawables().add(data);
            }
        }        
        
        gatherDrawables();

        dialogFactory.showDialogLoading(() -> {
            if (!produceAtlas()) {
                showDrawableError();
                Gdx.app.log(getClass().getName(), "Attempting to reload drawables backup...");
                atlasData.getDrawables().clear();
                atlasData.getDrawables().addAll(backup);
                gatherDrawables();
                if (produceAtlas()) {
                    Gdx.app.log(getClass().getName(), "Successfully rolled back changes to drawables");
                } else {
                    Gdx.app.error(getClass().getName(), "Critical failure, could not roll back changes to drawables");
                }
            } else {
                projectData.setChangesSaved(false);
            }

            sortBySelectedMode();
        });
    }
    
    /**
     * Creates a TintedDrawable based on the provided DrawableData. Prompts
     * user for a Color and name.
     * @param drawableData 
     */
    private void newTintedDrawable(DrawableData drawableData) {
        Color previousColor = Color.WHITE;
        if (drawableData.tint != null) {
            previousColor = drawableData.tint;
        }
        dialogFactory.showDialogColorPicker(previousColor, new DialogColorPicker.ColorListener() {
            @Override
            public void selected(Color color) {
                if (color != null) {
                    final DrawableData tintedDrawable = new DrawableData(drawableData.file);
                    tintedDrawable.tint = color;
                    
                    //Fix background color for new, tinted drawable
                    Color temp = Utils.averageEdgeColor(tintedDrawable.file, tintedDrawable.tint);
                    
                    if (Utils.brightness(temp) > .5f) {
                        tintedDrawable.bgColor = Color.BLACK;
                    } else {
                        tintedDrawable.bgColor = Color.WHITE;
                    }
                    
                    final TextField textField = new TextField(drawableData.name, getSkin());
                    final TextButton button = new TextButton("OK", getSkin());
                    button.setDisabled(!DrawableData.validate(textField.getText()) || checkIfNameExists(textField.getText()));
                    textField.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            button.setDisabled(!DrawableData.validate(textField.getText()) || checkIfNameExists(textField.getText()));
                        }
                    });
                    textField.addListener(IbeamListener.get());

                    Dialog dialog = new Dialog("TintedDrawable...", getSkin()) {
                        @Override
                        protected void result(Object object) {
                            if (object instanceof Boolean && (boolean) object) {
                                tintedDrawable.name = textField.getText();
                                atlasData.getDrawables().add(tintedDrawable);
                                projectData.setChangesSaved(false);
                            }
                        }

                        @Override
                        public boolean remove() {
                            gatherDrawables();
                            produceAtlas();
                            sortBySelectedMode();
                            return super.remove();
                        }
                    };
                    dialog.addCaptureListener(new InputListener() {
                        @Override
                        public boolean keyDown(InputEvent event, int keycode2) {
                            if (keycode2 == Input.Keys.ENTER) {
                                if (!button.isDisabled()) {
                                    tintedDrawable.name = textField.getText();
                                    atlasData.getDrawables().add(tintedDrawable);
                                    projectData.setChangesSaved(false);
                                    dialog.hide();
                                }
                            }
                            return false;
                        }
                    });
                    dialog.text("What is the name of the new tinted drawable?");

                    Drawable drawable = drawablePairs.get(drawableData);
                    Drawable preview = null;
                    if (drawable instanceof SpriteDrawable) {
                        preview = ((SpriteDrawable) drawable).tint(color);
                    } else if (drawable instanceof NinePatchDrawable) {
                        preview = ((NinePatchDrawable) drawable).tint(color);
                    }
                    if (preview != null) {
                        dialog.getContentTable().row();
                        Table table = new Table();
                        table.setBackground(preview);
                        dialog.getContentTable().add(table);
                    }

                    dialog.getContentTable().row();
                    dialog.getContentTable().add(textField).growX();

                    dialog.button(button, true);
                    dialog.button("Cancel", false);
                    dialog.key(Input.Keys.ESCAPE, false);
                    dialog.show(getStage());
                    getStage().setKeyboardFocus(textField);
                    textField.selectAll();
                }
            }
        });
    }
    
    /**
     * Returns true if any existing drawable has the indicated name.
     * @param name
     * @return 
     */
    private boolean checkIfNameExists(String name) {
        boolean returnValue = false;
        
        for (DrawableData drawable : drawables) {
            if (drawable.name.equals(name)) {
                returnValue = true;
                break;
            }
        }
        
        return returnValue;
    }
    
    @Override
    public boolean remove() {        
        main.getDesktopWorker().removeFilesDroppedListener(filesDroppedListener);
        
        try {
            if (!atlasData.atlasCurrent) {
                atlasData.writeAtlas();
                atlasData.atlasCurrent = true;
            }
        } catch (Exception e) {
            Gdx.app.error(getClass().getName(), "Error creating atlas upon drawable dialog exit", e);
            main.getDialogFactory().showDialogError("Atlas Error...", "Error creating atlas upon drawable dialog exit.\n\nOpen log?");
        }
        
        if (atlas != null) {
            atlas.dispose();
            atlas = null;
        }
        return super.remove();
    }
    
    @Override
    protected void result(Object object) {
        instance = null;
        if (object != null) {
            if (object instanceof DrawableData) {
                projectData.setChangesSaved(false);
                if (object instanceof DrawableData) {
                    DrawableData drawable = (DrawableData) object;
                    
                    DrawableUndoable undoable =
                            new DrawableUndoable(main.getRootTable(), atlasData,
                                    property, property.value, drawable.name);
                    main.getUndoableManager().addUndoable(undoable, true);
                }
            } else if (object instanceof Boolean && property != null) {
                if ((boolean) object) {
                    projectData.setChangesSaved(false);
                    DrawableUndoable undoable =
                            new DrawableUndoable(main.getRootTable(), atlasData,
                                    property, property.value, null);
                    main.getUndoableManager().addUndoable(undoable, true);
//                    PanelStatusBar.instance.message("Drawable emptied for \"" + property.name + "\"");
                } else {
                    boolean hasDrawable = false;
                    for (DrawableData drawable : atlasData.getDrawables()) {
                        if (drawable.name.equals(property.value)) {
                            hasDrawable = true;
                            break;
                        }
                    }
                    
                    if (!hasDrawable) {
                        property.value = null;
//                        PanelStatusBar.instance.message("Drawable deleted for \"" + property.name + "\"");
                        main.getRootTable().refreshStyleProperties(true);
                    }
                }
            }
        }
        
        //todo: do proper implementation of event handling with fire(), etc.
        if (listener != null) {
            listener.handle(null);
            listener = null;
        }
    }
}