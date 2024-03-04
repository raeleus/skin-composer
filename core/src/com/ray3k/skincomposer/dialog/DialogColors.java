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
package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Sort;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.UndoableManager;
import com.ray3k.skincomposer.UndoableManager.ColorUndoable;
import com.ray3k.skincomposer.UndoableManager.CustomColorUndoable;
import com.ray3k.skincomposer.data.*;
import com.ray3k.skincomposer.utils.Utils;
import com.ray3k.stripe.PopColorPicker.PopColorPickerAdapter;

import java.util.Comparator;

import static com.ray3k.skincomposer.Main.*;

public class DialogColors extends Dialog {
    private Array<ColorData> colors;
    private Table colorTable;
    private StyleProperty styleProperty;
    private CustomProperty customProperty;
    private boolean selectingForTintedDrawable;
    private SelectBox<String> selectBox;
    private DialogColorsListener listener;
    private ScrollPane scrollPane;
    private Main main;
    
    public DialogColors(Main main, StyleProperty styleProperty, boolean selectingForTintedDrawable, DialogColorsListener listener) {
        super("", skin, "dialog");
        this.styleProperty = styleProperty;
        populate(main, selectingForTintedDrawable, listener);
    }
    
    public DialogColors(Main main, StyleProperty styleProperty, DialogColorsListener listener) {
        this(main, styleProperty, false, listener);
    }
    
    public DialogColors(Main main, CustomProperty customProperty, boolean selectingForTintedDrawable, DialogColorsListener listener) {
        super("", skin, "dialog");
        this.customProperty = customProperty;
        populate(main, selectingForTintedDrawable, listener);
    }
    
    public DialogColors(Main main, CustomProperty customProperty, DialogColorsListener listener) {
        this(main, customProperty, false, listener);
    }
    
    private void populate(Main main, boolean selectingForTintedDrawable, DialogColorsListener listener) {
        this.main = main;
        
        this.listener = listener;
        this.selectingForTintedDrawable = selectingForTintedDrawable;
        colors = jsonData.getColors();
        
        getContentTable().defaults().expandX();
        if (styleProperty != null || customProperty != null) {
            Label label = new Label("Select a color...", getSkin(), "title");
            label.setAlignment(Align.center);
            getContentTable().add(label);
            getContentTable().row();
        } else if (selectingForTintedDrawable) {
            Label label = new Label("Select a color for tinted drawable...", getSkin(), "title");
            label.setAlignment(Align.center);
            getContentTable().add(label);
            getContentTable().row();
        } else {
            Label label = new Label("Colors", getSkin(), "title");
            label.setAlignment(Align.center);
            getContentTable().add(label);
            getContentTable().row();
        }
        
        Table table = new Table();
        
        table.defaults().pad(2.0f);
        
        table.add(new Label("Sort by: ", getSkin())).padLeft(20.0f);
        selectBox = new SelectBox<String>(getSkin());
        selectBox.setItems(new String[] {"A-Z", "Z-A"});
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                sortBySelectedMode();
            }
        });
        selectBox.addListener(handListener);
        selectBox.getList().addListener(handListener);
        table.add(selectBox);
        
        TextButton imageButton = new TextButton("New Color", getSkin(), "new");
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                showColorPicker();
            }
        });
        imageButton.addListener(handListener);
        table.add(imageButton).expandX().left();
        getContentTable().add(table).left().expandX();
        getContentTable().row();
        colorTable = new Table();
        refreshTable();
        
        table = new Table();
        table.add(colorTable).pad(5.0f);
        scrollPane = new ScrollPane(table, getSkin());
        scrollPane.setFadeScrollBars(false);
        getContentTable().add(scrollPane).grow();
        
        if (styleProperty != null || customProperty != null) {
            button("Clear Color", true);
            button("Cancel", false);
            getButtonTable().getCells().get(0).getActor().addListener(handListener);
            getButtonTable().getCells().get(1).getActor().addListener(handListener);
        } else if (selectingForTintedDrawable) {
            button("Cancel", false);
            getButtonTable().getCells().get(0).getActor().addListener(handListener);
        } else {
            button("Close", false);
            getButtonTable().getCells().get(0).getActor().addListener(handListener);
        }
        getButtonTable().padBottom(15.0f);
        key(Keys.ESCAPE, false);
    }
    
    @Override
    public Dialog show(Stage stage) {
        Dialog dialog = super.show(stage);
        stage.setScrollFocus(scrollPane);
        return dialog;
    }
    
    private void showColorPicker() {
        dialogFactory.showDialogColorPicker(new PopColorPickerAdapter() {
            @Override
            public void picked(Color color) {
                final TextField field = new TextField("RGBA_" + (int) (color.r * 255) + "_" + (int) (color.g * 255) + "_" + (int) (color.b * 255) + "_" + (int) (color.a * 255), getSkin());
                final Dialog dialog = new Dialog("Color name...", getSkin(), "bg") {
                    @Override
                    protected void result(Object object) {
                        if ((Boolean) object == true) {
                            newColor(field.getText(), color);
                        }
                    }
                };
    
                dialog.getTitleTable().padLeft(5.0f);
    
                dialog.button("Ok", true).button("Cancel", false).key(Keys.ESCAPE, false);
                final TextButton button = (TextButton) dialog.getButtonTable().getCells().first().getActor();
                button.addListener(handListener);
                dialog.getButtonTable().getCells().get(1).getActor().addListener(handListener);
                dialog.getButtonTable().pad(15.0f);
    
                field.setTextFieldListener(new TextField.TextFieldListener() {
                    @Override
                    public void keyTyped(TextField textField, char c) {
                        if (c == '\n') {
                            if (!button.isDisabled()) {
                                String name = field.getText();
                                if (newColor(name, color)) {
                                    dialog.hide();
                                }
                            }
                            stage.setKeyboardFocus(textField);
                        }
                    }
                });
    
                field.addListener(ibeamListener);
    
                dialog.getContentTable().padLeft(10.0f).padRight(10.0f).padTop(5.0f);
                dialog.text("Please enter a name for the new color: ");
                dialog.getContentTable().row();
                dialog.getContentTable().add(field).growX();
                dialog.getContentTable().row();
                dialog.text("Preview:");
                dialog.getContentTable().row();
                Table table = new Table(getSkin());
                table.setBackground("white");
                table.setColor(color);
                dialog.getContentTable().add(table).minSize(50.0f);
                boolean disable = !ColorData.validate(field.getText());
                if (!disable) {
                    for (ColorData data : jsonData.getColors()) {
                        if (data.getName().equals(field.getText())) {
                            disable = true;
                            break;
                        }
                    }
                }
                button.setDisabled(disable);
                field.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        boolean disable = !ColorData.validate(field.getText());
                        if (!disable) {
                            for (ColorData data : jsonData.getColors()) {
                                if (data.getName().equals(field.getText())) {
                                    disable = true;
                                    break;
                                }
                            }
                        }
                        button.setDisabled(disable);
                    }
                });
                dialog.show(getStage());
                getStage().setKeyboardFocus(field);
                field.selectAll();
                field.setFocusTraversal(false);
            }
        });
    }
    
    public void refreshTable() {
        colorTable.clear();
        
        if (colors.size > 0) {
            colorTable.defaults().padTop(5.0f);
            for (ColorData color : colors) {
                Button button = new Button(getSkin(), "color-base");
                button.addListener(handListener);
                Label label = new Label(color.toString(), getSkin(), "white");
                label.setTouchable(Touchable.disabled);
                
                float brightness = Utils.brightness(color.color);
                Color borderColor;
                if (brightness > .35f) {
                    borderColor = Color.BLACK;
                    label.setColor(borderColor);
                } else {
                    borderColor = Color.WHITE;
                    label.setColor(borderColor);
                }
                
                Color bgColor = new Color(color.color.r, color.color.g, color.color.b, 1.0f);
                Table table = new Table(getSkin());
                table.setBackground("white");
                table.setColor(bgColor);
                table.add(label).pad(3.0f);
                
                if (styleProperty == null && customProperty == null && !selectingForTintedDrawable) {
                    table.addCaptureListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            event.setBubbles(false);
                            refreshTable();
                        }
                    });
                    table.addCaptureListener(new InputListener() {
                        @Override
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                            event.setBubbles(false);
                            return true;
                        }
                    });
                }
                Table borderTable = new Table(getSkin());
                borderTable.setBackground("white");
                borderTable.setColor(borderColor);
                borderTable.add(table).growX().pad(1.0f);
                
                button.add(borderTable).growX();
                
                //rename button
                Button renameButton = new Button(getSkin(), "settings-small");
                renameButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        renameDialog(color);
                        
                        event.setBubbles(false);
                    }
                });
                renameButton.addListener(new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        event.setBubbles(false);
                        return true;
                    }
                    
                });
                button.add(renameButton).padLeft(10.0f);
                
                TextTooltip toolTip = (Main.makeTooltip("Rename Color", tooltipManager, getSkin()));
                renameButton.addListener(toolTip);
                
                //recolor button
                Button recolorButton = new Button(getSkin(), "colorwheel");
                recolorButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        recolorDialog(color);
                        
                        event.setBubbles(false);
                    }
                });
                recolorButton.addListener(new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        event.setBubbles(false);
                        return true;
                    }
                    
                });
                button.add(recolorButton);
                
                toolTip = (Main.makeTooltip("Change Color", tooltipManager, getSkin()));
                recolorButton.addListener(toolTip);
                
                label = new Label("(" + ((int)(color.color.r * 255)) + ", " + ((int)(color.color.g * 255)) + ", " + ((int)(color.color.b * 255)) + ", " + ((int)(color.color.a * 255)) + ")", getSkin());
                label.setTouchable(Touchable.disabled);
                label.setAlignment(Align.center);
                
                if (styleProperty == null && customProperty == null && !selectingForTintedDrawable) {
                    label.addCaptureListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            event.setBubbles(false);
                            refreshTable();
                        }
                    });
                    label.addCaptureListener(new InputListener() {
                        @Override
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                            event.setBubbles(false);
                            return true;
                        }

                    });
                }
                button.add(label).padLeft(5.0f).minWidth(160.0f);
                
                //delete color button
                Button closeButton = new Button(getSkin(), "delete-small");
                final ColorData deleteColor = color;
                closeButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        colors.removeValue(deleteColor, true);
                        projectData.setChangesSaved(false);
                        
                        //clear style properties that use this color.
                        for (Array<StyleData> datas : jsonData.getClassStyleMap().values()) {
                            for (StyleData data : datas) {
                                for (StyleProperty property : data.properties.values()) {
                                    if (property != null && property.type.equals(Color.class) && property.value != null && property.value.equals(deleteColor.getName())) {
                                        property.value = null;
                                    }
                                }
                            }
                        }
                        
                        //delete tinted drawables based on this color.
                        for(DrawableData drawableData : new Array<>(projectData.getAtlasData().getDrawables())) {
                            if (drawableData.tintName != null && drawableData.tintName.equals(deleteColor.getName())) {
                                projectData.getAtlasData().getDrawables().removeValue(drawableData, true);
                                
                                //clear any style properties based on this tinted drawable.
                                for (Array<StyleData> styleDatas : jsonData.getClassStyleMap().values()) {
                                    for (StyleData styleData : styleDatas) {
                                        for (StyleProperty styleProperty : styleData.properties.values()) {
                                            if (styleProperty != null && styleProperty.type.equals(Drawable.class) && styleProperty.value != null && styleProperty.value.equals(drawableData.toString())) {
                                                styleProperty.value = null;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        
                        undoableManager.clearUndoables();
                        
                        rootTable.refreshStyleProperties(true);
                        rootTable.refreshPreview();
                        
                        event.setBubbles(false);
                        refreshTable();
                    }
                });
                closeButton.addListener(new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        event.setBubbles(false);
                        return true;
                    }
                    
                });
                
                toolTip = (Main.makeTooltip("Delete Color", tooltipManager, getSkin()));
                closeButton.addListener(toolTip);
                
                button.add(closeButton).padLeft(5.0f);
                if (styleProperty == null && customProperty == null && !selectingForTintedDrawable) {
                    button.setTouchable(Touchable.childrenOnly);
                } else {
                    setObject(button, color);
                    final ColorData result = color;
                    button.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            result(result);
                            hide();
                        }
                    });
                }
                colorTable.add(button).growX();
                colorTable.row();
            }
        } else {
            colorTable.add(new Label("No colors have been set!", getSkin(), "black"));
        }
    }
    
    private void recolorDialog(ColorData colorData) {
        dialogFactory.showDialogColorPicker(colorData.color, new PopColorPickerAdapter() {
            @Override
            public void picked(Color color) {
                recolorColor(colorData, color);
            }
        });
    }
    
    private void recolorColor(ColorData colorData, Color color) {
        colorData.color = color;

        undoableManager.clearUndoables();

        rootTable.refreshStyleProperties(true);
        atlasData.produceAtlas();
        rootTable.refreshPreview();
        
        projectData.setChangesSaved(false);
        
        refreshTable();
    }
    
    private void renameDialog(ColorData color) {
        TextField textField = new TextField("", getSkin());
        TextButton okButton;
        
        Dialog dialog = new Dialog("Rename Color?", getSkin(), "bg") {
            @Override
            protected void result(Object object) {
                if ((boolean) object) {
                    renameColor(color, textField.getText());
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
        
        float brightness = Utils.brightness(color.color);
        Color borderColor;
        if (brightness > .35f) {
            borderColor = Color.BLACK;
        } else {
            borderColor = Color.WHITE;
        }
        
        Table bg = new  Table(getSkin());
        bg.setBackground("white");
        bg.setColor(borderColor);
        dialog.getContentTable().add(bg);
        
        Label label = new Label(color.getName(), getSkin(), "white");
        label.setColor(color.color);
        bg.add(label).pad(10);
        
        dialog.getContentTable().row();
        label = new Label("What do you want to rename the color to?", getSkin());
        dialog.getContentTable().add(label);
        
        dialog.getContentTable().row();
        textField.setText(color.getName());
        textField.selectAll();
        textField.addListener(ibeamListener);
        dialog.getContentTable().add(textField);
        dialog.getCell(dialog.getContentTable()).pad(15.0f);
        
        dialog.button("OK", true);
        dialog.button("Cancel", false).key(Keys.ESCAPE, false);
        okButton = (TextButton) dialog.getButtonTable().getCells().first().getActor();
        okButton.setDisabled(true);
        okButton.addListener(handListener);
        dialog.getButtonTable().getCells().get(1).getActor().addListener(handListener);
        dialog.getButtonTable().padBottom(15.0f);
        
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                boolean disable = !ColorData.validate(textField.getText());
                if (!disable) {
                    for (ColorData data : jsonData.getColors()) {
                        if (data.getName().equals(textField.getText())) {
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
                        renameColor(color, textField.getText());
                        dialog.hide();
                    }
                }
            }
        });
        
        textField.setFocusTraversal(false);
        dialog.show(getStage());
    }
    
    private void renameColor(ColorData color, String newName) {
        //style properties
        for (Array<StyleData> datas : jsonData.getClassStyleMap().values()) {
            for (StyleData data : datas) {
                for (StyleProperty property : data.properties.values()) {
                    if (property != null && property.type.equals(Color.class) && property.value != null && property.value.equals(color.getName())) {
                        property.value = newName;
                    }
                }
            }
        }
        
        for (DrawableData drawableData : atlasData.getDrawables()) {
            //tinted drawables
            if (drawableData.tintName != null && drawableData.tintName.equals(color.getName())) {
                drawableData.tintName = newName;
            }
            
            //ten patch drawables
            else if (drawableData.tenPatchData != null && drawableData.tenPatchData.colorName != null && drawableData.tenPatchData.colorName.equals(color.getName())) {
                drawableData.tenPatchData.colorName = newName;
            }
        }
        
        try {
            color.setName(newName);
        } catch (ColorData.NameFormatException ex) {
            Gdx.app.error(getClass().getName(), "Error trying to rename a color.", ex);
            dialogFactory.showDialogError("Name Error...","Error while naming a color.\\nPlease ensure name is formatted appropriately:\\nNo spaces, don't start with a number, - and _ acceptable.\n\nOpen log?");
        }

        undoableManager.clearUndoables();

        rootTable.refreshStyleProperties(true);
        rootTable.refreshPreview();
        
        projectData.setChangesSaved(false);
        
        refreshTable();
    }
    
    private boolean newColor(String name, Color color) {
        if (ColorData.validate(name)) {
            try {
                projectData.setChangesSaved(false);
                colors.add(new ColorData(name, color));
                sortBySelectedMode();
                refreshTable();
                return true;
            } catch (Exception e) {
                Gdx.app.log(getClass().getName(), "Error trying to add color.", e);
                dialogFactory.showDialogError("Error creating color...", "Error while attempting to create color.\n\nOpen log?");
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    protected void result(Object object) {
        boolean pressedCancel = false;
        if (styleProperty != null) {
            if (object instanceof ColorData) {
                projectData.setChangesSaved(false);
                ColorData color = (ColorData) object;
                ColorUndoable undoable = new ColorUndoable(rootTable, jsonData, styleProperty, styleProperty.value, color.getName());
                undoableManager.addUndoable(undoable, true);
            } else if (object instanceof Boolean) {
                if ((boolean) object) {
                    projectData.setChangesSaved(false);
                    ColorUndoable undoable = new ColorUndoable(rootTable, jsonData, styleProperty, styleProperty.value, null);
                    undoableManager.addUndoable(undoable, true);
                } else {
                    pressedCancel = true;
                    
                    boolean hasColor = false;
                    for (ColorData color : jsonData.getColors()) {
                        if (color.getName().equals(styleProperty.value)) {
                            hasColor = true;
                            break;
                        }
                    }

                    if (!hasColor) {
                        projectData.setChangesSaved(false);
                        styleProperty.value = null;
                        rootTable.refreshStyleProperties(true);
                    }
                }
            }
        } else if (customProperty != null) {
            if (object instanceof ColorData) {
                projectData.setChangesSaved(false);
                ColorData color = (ColorData) object;
                CustomColorUndoable undoable = new UndoableManager.CustomColorUndoable(main, customProperty, color.getName());
                undoableManager.addUndoable(undoable, true);
            } else if (object instanceof Boolean) {
                if ((boolean) object) {
                    projectData.setChangesSaved(false);

                    CustomColorUndoable undoable = new UndoableManager.CustomColorUndoable(main, customProperty, null);
                    undoableManager.addUndoable(undoable, true);
                    rootTable.refreshStyleProperties(true);
                } else {
                    pressedCancel = true;
                    
                    boolean hasColor = false;
                    for (ColorData color : jsonData.getColors()) {
                        if (color.getName().equals(customProperty.getValue())) {
                            hasColor = true;
                            break;
                        }
                    }

                    if (!hasColor) {
                        projectData.setChangesSaved(false);
                        customProperty.setValue(null);
                        rootTable.refreshStyleProperties(true);
                    }
                }
            }
        }
        
        if (listener != null) {
            if (object instanceof ColorData) {
                listener.handle((ColorData) object, pressedCancel);
            } else {
                listener.handle(null, pressedCancel);
            }
        }
    }
    
    private void sortBySelectedMode() {
        switch (selectBox.getSelectedIndex()) {
            case 0:
                sortFontsAZ();
                break;
            case 1:
                sortFontsZA();
                break;
        }
    }
    
    private void sortFontsAZ() {
        Sort.instance().sort(colors, new Comparator<ColorData>() {
            @Override
            public int compare(ColorData o1, ColorData o2) {
                return o1.toString().compareToIgnoreCase(o2.toString());
            }
        });
        refreshTable();
    }
    
    private void sortFontsZA() {
        Sort.instance().sort(colors, new Comparator<ColorData>() {
            @Override
            public int compare(ColorData o1, ColorData o2) {
                return o1.toString().compareToIgnoreCase(o2.toString()) * -1;
            }
        });
        refreshTable();
    }
    
    public static interface DialogColorsListener {
        public void handle(ColorData colorData, boolean cancelled);
    }

    @Override
    public Dialog show(Stage stage, Action action) {
        fire(new DialogEvent(DialogEvent.Type.OPEN));
        return super.show(stage, action);
    }

    @Override
    public boolean remove() {
        fire(new DialogEvent(DialogEvent.Type.CLOSE));
        return super.remove();
    }
}
