/*******************************************************************************
 * MIT License
 * 
 * Copyright (c) 2018 Raymond Buckley
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
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Sort;
import com.ray3k.skincomposer.FilesDroppedListener;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.UndoableManager.CustomFontUndoable;
import com.ray3k.skincomposer.UndoableManager.FontUndoable;
import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.data.CustomProperty;
import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.data.FontData;
import com.ray3k.skincomposer.data.FreeTypeFontData;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.data.StyleProperty;
import com.ray3k.skincomposer.utils.Utils;
import java.io.File;
import java.util.Iterator;
import java.util.List;

public class DialogFonts extends Dialog {
    private StyleProperty styleProperty;
    private CustomProperty customProperty;
    private Array<FontData> fonts;
    private Array<FreeTypeFontData> freeTypeFonts;
    private Array<DrawableData> drawables;
    private Table fontsTable;
    private SelectBox<String> selectBox;
    private ObjectMap<FontData, BitmapFont> fontMap;
    private TextureAtlas atlas;
    private EventListener listener;
    private FilesDroppedListener filesDroppedListener;
    private ScrollPane scrollPane;
    private Main main;
    private int maxTextureWidth;
    private int maxTextureHeight;

    public void initialize(Main main, EventListener listener) {
        this.main = main;
        
        maxTextureWidth = 1024;
        maxTextureHeight = 1024;
        
        //extract max texture dimensions from defaults.json
        FileHandle defaultsFile = Gdx.files.local("texturepacker/defaults.json");
        if (defaultsFile.exists()) {
            JsonReader reader = new JsonReader();
            JsonValue val = reader.parse(defaultsFile);

            for (JsonValue child : val.iterator()) {
                if (child.name.equals("maxWidth") && child.isNumber()) {
                    maxTextureWidth = child.asInt();
                } else if (child.name.equals("maxHeight") && child.isNumber()) {
                    maxTextureHeight = child.asInt();
                }
            }
        }
        
        this.listener = listener;
        
        fonts = main.getJsonData().getFonts();
        freeTypeFonts = main.getJsonData().getFreeTypeFonts();
        drawables = main.getAtlasData().getDrawables();

        fontMap = new ObjectMap<>();
        produceAtlas();
        
        filesDroppedListener = (Array<FileHandle> files) -> {
            Iterator<FileHandle> iter = files.iterator();
            while (iter.hasNext()) {
                FileHandle file = iter.next();
                if (file.isDirectory() || !file.name().toLowerCase().endsWith(".fnt")) {
                    iter.remove();
                }
            }
            
            if (files.size > 0) {
                fontNameDialog(files, 0);
            }
        };
        
        main.getDesktopWorker().addFilesDroppedListener(filesDroppedListener);

        populate();
    }
    
    public DialogFonts(Main main, StyleProperty styleProperty, EventListener listener) {
        super("", main.getSkin(), "dialog");
        this.styleProperty = styleProperty;
        initialize(main, listener);
    }
    
    public DialogFonts(Main main, CustomProperty customProperty, EventListener listener) {
        super("", main.getSkin(), "dialog");
        this.customProperty = customProperty;
        initialize(main, listener);
    }
    
    private void populate() {
        setFillParent(true);

        if (styleProperty != null || customProperty != null) {
            getContentTable().add(new Label("Select a Font...", getSkin(), "title"));
            getContentTable().row();
        } else {
            getContentTable().add(new Label("Fonts", getSkin(), "title"));
            getContentTable().row();
        }

        Table table = new Table();
        table.pad(2.0f);

        table.defaults().space(15.0f);
        table.add(new Label("Sort by: ", getSkin())).padLeft(20.0f);
        selectBox = new SelectBox<>(getSkin());
        selectBox.setItems(new String[]{"A-Z", "Z-A", "Oldest", "Newest"});
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                sortBySelectedMode();
            }
        });
        selectBox.addListener(main.getHandListener());
        selectBox.getList().addListener(main.getHandListener());
        table.add(selectBox);

        TextButton imageButton = new TextButton("New Font", getSkin(), "new");
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                newFontDialog();
            }
        });
        imageButton.addListener(main.getHandListener());
        table.add(imageButton).expandX();
        
        imageButton = new TextButton("FreeType Font", getSkin(), "new");
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                newFreeTypeFontDialog();
            }
        });
        imageButton.addListener(main.getHandListener());
        table.add(imageButton).expandX();
        
        imageButton = new TextButton("Create from Image", getSkin(), "new");
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                newImageFontDialog();
            }
        });
        imageButton.addListener(main.getHandListener());
        table.add(imageButton).expandX();
        
        getContentTable().add(table).expandX().left();
        getContentTable().row();

        key(Keys.ESCAPE, false);
        if (styleProperty != null || customProperty != null) {
            button("Clear Font", true);
            button("Cancel", false);
            getButtonTable().getCells().first().getActor().addListener(main.getHandListener());
            getButtonTable().getCells().get(1).getActor().addListener(main.getHandListener());
        } else {
            button("Close", false);
            getButtonTable().getCells().first().getActor().addListener(main.getHandListener());
        }
        
        getButtonTable().padBottom(15.0f);

        fontsTable = new Table();

        table = new Table();
        table.add(fontsTable).pad(5.0f);
        scrollPane = new ScrollPane(table, getSkin());
        scrollPane.setFadeScrollBars(false);
        getContentTable().add(scrollPane).grow();
    }

    @Override
    public Dialog show(Stage stage) {
        Dialog dialog = super.show(stage);
        stage.setScrollFocus(scrollPane);
        return dialog;
    }

    private boolean addFont(String name, FileHandle file) {
        if (FontData.validate(name)) {
            try {
                main.getProjectData().setChangesSaved(false);
                FontData font = new FontData(name, file);
                
                //remove any existing FontData that shares the same name.
                if (fonts.contains(font, false)) {
                    FontData deleteFont = fonts.get(fonts.indexOf(font, false));
                    
                    BitmapFontData deleteFontData = new BitmapFontData(deleteFont.file, false);
                    for (String path : deleteFontData.imagePaths) {
                        FileHandle imagefile = new FileHandle(path);
                        drawables.removeValue(new DrawableData(imagefile), false);
                    }
                    
                    fonts.removeValue(font, false);
                }
                
                BitmapFontData bitmapFontData = new BitmapFontData(file, false);
                for (String path : bitmapFontData.imagePaths) {
                    DrawableData drawable = new DrawableData(new FileHandle(path));
                    drawable.visible = false;
                    if (!drawables.contains(drawable, false)) {
                        main.getAtlasData().atlasCurrent = false;
                        drawables.add(drawable);
                    }
                }
                produceAtlas();
                fonts.add(font);
                
                Array<TextureRegion> regions = new Array<>();
                for (String path : bitmapFontData.imagePaths) {
                    FileHandle imageFile = new FileHandle(path);
                    regions.add(atlas.findRegion(imageFile.nameWithoutExtension()));
                }
                fontMap.put(font, new BitmapFont(bitmapFontData, regions, true));
                
                
                
                sortBySelectedMode();
                refreshTable();
            } catch (Exception e) {
                Gdx.app.error(getClass().getName(), "Error creating font from file", e);
                main.getDialogFactory().showDialogError("Font Error...", "Error creating font from file. Check file paths.\n\nOpen log?");
            }
            return true;
        } else {
            return false;
        }
    }

    public void refreshTable() {
        fontsTable.clear();
        fontsTable.defaults().growX().pad(5.0f);

        if (fonts.size == 0 && freeTypeFonts.size == 0) {
            fontsTable.add(new Label("No fonts have been set!", getSkin()));
        } else {
            
            if (fonts.size > 0) {
                Label label = new Label("Bitmap Fonts", getSkin(), "required");
                label.setAlignment(Align.center);
                fontsTable.add(label);
                fontsTable.row();
            }
            
            for (FontData font : fonts) {
                Button button = new Button(getSkin(), "color-base");
                Label label = new Label(font.getName(), getSkin());
                label.setTouchable(Touchable.disabled);
                button.add(label).left();
                button.addListener(main.getHandListener());
                
                Button renameButton = new Button(getSkin(), "settings-small");
                renameButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        renameDialog(font);
                        
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
                button.add(renameButton).padLeft(15.0f);
                
                TextTooltip toolTip = new TextTooltip("Rename Font", main.getTooltipManager(), getSkin());
                renameButton.addListener(toolTip);
                
                LabelStyle style = new LabelStyle();
                style.font = fontMap.get(font);
                style.fontColor = Color.WHITE;
                label = new Label("Lorem Ipsum", style);
                label.setAlignment(Align.center);
                label.setTouchable(Touchable.disabled);
                Table bg = new Table(getSkin());
                bg.setBackground("white");
                BitmapFontData bf = new BitmapFontData(font.file, false);
                if (bf.imagePaths.length > 0) {
                    FileHandle file = new FileHandle(bf.imagePaths[0]);
                    if (!file.exists()) {
                        file = bf.fontFile.sibling(bf.fontFile.nameWithoutExtension() + ".png");
                    }
                    if (Utils.brightness(Utils.averageEdgeColor(file)) < .5f) {
                        bg.setColor(Color.WHITE);
                    } else {
                        bg.setColor(Color.BLACK);
                    }
                }
                bg.add(label).pad(5.0f).grow();
                button.add(bg).padLeft(15).growX();

                Button closeButton = new Button(getSkin(), "delete-small");
                final FontData deleteFont = font;
                closeButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        fonts.removeValue(deleteFont, true);
                        main.getProjectData().setChangesSaved(false);
                        BitmapFontData bitmapFontData = new BitmapFontData(deleteFont.file, false);
                        for (String path : bitmapFontData.imagePaths) {
                            FileHandle imagefile = new FileHandle(path);
                            drawables.removeValue(new DrawableData(imagefile), false);
                        }
                        
                        for (Array<StyleData> datas : main.getJsonData().getClassStyleMap().values()) {
                            for (StyleData data : datas) {
                                for (StyleProperty property : data.properties.values()) {
                                    if (property != null && property.type.equals(BitmapFont.class) && property.value != null && property.value.equals(deleteFont.getName())) {
                                        property.value = null;
                                    }
                                }
                            }
                        }
                        
                        main.getUndoableManager().clearUndoables();
                        
                        main.getRootTable().refreshStyleProperties(true);
                        main.getRootTable().refreshPreview();
                        
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
                button.add(closeButton).padLeft(5.0f).right();
                
                toolTip = new TextTooltip("Delete Font", main.getTooltipManager(), getSkin());
                closeButton.addListener(toolTip);

                if (styleProperty == null && customProperty == null) {
                    button.setTouchable(Touchable.childrenOnly);
                } else {
                    final FontData fontResult = font;
                    button.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            result(fontResult);
                            hide();
                        }
                    });
                }

                fontsTable.add(button);
                fontsTable.row();
            }
            
            if (freeTypeFonts.size > 0) {
                Label label = new Label("FreeType Fonts", getSkin(), "required");
                label.setAlignment(Align.center);
                fontsTable.add(label).spaceTop(20.0f);
                fontsTable.row();
            }
            
            for (FreeTypeFontData font : freeTypeFonts) {
                Button button = new Button(getSkin(), "color-base");
                Label label = new Label(font.name, getSkin());
                label.setTouchable(Touchable.disabled);
                button.add(label).left();
                button.addListener(main.getHandListener());
                
                Button renameButton = new Button(getSkin(), "settings-small");
                renameButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        freeTypeSettingsDialog(font);
                        
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
                button.add(renameButton).padLeft(15.0f);
                
                TextTooltip toolTip = new TextTooltip("Rename Font", main.getTooltipManager(), getSkin());
                renameButton.addListener(toolTip);
                
                LabelStyle style = new LabelStyle();
                style.font = font.bitmapFont;
                style.fontColor = Color.WHITE;
                label = new Label("Lorem Ipsum", style);
                label.setAlignment(Align.center);
                label.setTouchable(Touchable.disabled);
                Table bg = new Table(getSkin());
                bg.setBackground("white");
                bg.add(label).pad(5.0f).grow();
                button.add(bg).padLeft(15).growX();

                Button closeButton = new Button(getSkin(), "delete-small");
                final FreeTypeFontData deleteFont = font;
                closeButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        freeTypeFonts.removeValue(deleteFont, true);
                        main.getProjectData().setChangesSaved(false);
                        
                        for (Array<StyleData> datas : main.getJsonData().getClassStyleMap().values()) {
                            for (StyleData data : datas) {
                                for (StyleProperty property : data.properties.values()) {
                                    if (property != null && property.type.equals(BitmapFont.class) && property.value != null && property.value.equals(deleteFont.name)) {
                                        property.value = null;
                                    }
                                }
                            }
                        }
                        
                        main.getUndoableManager().clearUndoables();
                        
                        main.getRootTable().refreshStyleProperties(true);
                        main.getRootTable().refreshPreview();
                        
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
                button.add(closeButton).padLeft(5.0f).right();
                
                toolTip = new TextTooltip("Delete Font", main.getTooltipManager(), getSkin());
                closeButton.addListener(toolTip);

                if (styleProperty == null && customProperty == null) {
                    button.setTouchable(Touchable.childrenOnly);
                } else {
                    final FreeTypeFontData fontResult = font;
                    button.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            result(fontResult);
                            hide();
                        }
                    });
                }

                fontsTable.add(button);
                fontsTable.row();
            }
        }
    }
    
    private void freeTypeSettingsDialog(FreeTypeFontData font) {
        main.getDialogFactory().showDialogFreeTypeFont(font, (FreeTypeFontData font1) -> {
            sortBySelectedMode();
            refreshTable();
        });
    }
    
    private void renameDialog(FontData font) {
        TextField textField = new TextField("", getSkin());
        TextButton okButton;
        
        Dialog dialog = new Dialog("Rename Font?", getSkin(), "bg") {
            @Override
            protected void result(Object object) {
                if ((boolean) object) {
                    renameFont(font, textField.getText());
                }
            }

            @Override
            public Dialog show(Stage stage) {
                Dialog dialog = super.show(stage);
                main.getStage().setKeyboardFocus(textField);
                return dialog;
            }
        };
        dialog.getContentTable().padLeft(10.0f).padRight(10.0f);
        
        dialog.getTitleTable().padLeft(5.0f);
        
        Table bg = new  Table(getSkin());
        bg.setBackground("white");
        bg.setColor(Color.WHITE);
        dialog.getContentTable().add(bg);
        
        Label label = new Label(font.getName(), getSkin(), "white");
        label.setColor(Color.BLACK);
        bg.add(label).pad(10);
        
        dialog.getContentTable().row();
        label = new Label("What do you want to rename the font to?", getSkin());
        dialog.getContentTable().add(label);
        
        dialog.getContentTable().row();
        textField.setText(font.getName());
        textField.selectAll();
        textField.addListener(main.getIbeamListener());
        dialog.getContentTable().add(textField);
        
        dialog.button("OK", true);
        dialog.button("Cancel", false).key(Keys.ESCAPE, false);
        okButton = (TextButton) dialog.getButtonTable().getCells().first().getActor();
        okButton.setDisabled(true);
        okButton.addListener(main.getHandListener());
        dialog.getButtonTable().getCells().get(1).getActor().addListener(main.getHandListener());
        
        dialog.getButtonTable().padBottom(15.0f);
        
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                boolean disable = !FontData.validate(textField.getText());
                if (!disable) {
                    for (ColorData data : main.getJsonData().getColors()) {
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
                        renameFont(font, textField.getText());
                        dialog.hide();
                    }
                }
            }
        });
        
        textField.setFocusTraversal(false);
        
        dialog.show(getStage());
    }
    
    private void renameFont(FontData font, String newName) {
        for (Array<StyleData> datas : main.getJsonData().getClassStyleMap().values()) {
            for (StyleData data : datas) {
                for (StyleProperty property : data.properties.values()) {
                    if (property != null && property.type.equals(BitmapFont.class) && property.value != null && property.value.equals(font.getName())) {
                        property.value = newName;
                    }
                }
            }
        }
        
        try {
            font.setName(newName);
        } catch (FontData.NameFormatException ex) {
            Gdx.app.error(getClass().getName(), "Error trying to rename a font.", ex);
            main.getDialogFactory().showDialogError("Rename Font Error...", "Error trying to rename a font.\n\nOpen log?");
        }

        main.getUndoableManager().clearUndoables();

        main.getRootTable().refreshStyleProperties(true);
        main.getRootTable().refreshPreview();
        
        main.getProjectData().setChangesSaved(false);
        
        refreshTable();
    }

    @Override
    protected void result(Object object) {
        if (styleProperty != null) {
            if (object instanceof FontData) {
                main.getProjectData().setChangesSaved(false);
                FontData font = (FontData) object;
                FontUndoable undoable = new FontUndoable(main.getRootTable(),
                        main.getJsonData(), styleProperty, styleProperty.value, font.getName());
                main.getUndoableManager().addUndoable(undoable, true);
            } else if (object instanceof FreeTypeFontData) {
                main.getProjectData().setChangesSaved(false);
                FreeTypeFontData font = (FreeTypeFontData) object;
                FontUndoable undoable = new FontUndoable(main.getRootTable(),
                        main.getJsonData(), styleProperty, styleProperty.value, font.name);
                main.getUndoableManager().addUndoable(undoable, true);
            } else if (object instanceof Boolean) {
                if ((boolean) object) {
                    FontUndoable undoable = new FontUndoable(main.getRootTable(),
                            main.getJsonData(), styleProperty, styleProperty.value, null);
                    main.getUndoableManager().addUndoable(undoable, true);
                    main.getProjectData().setChangesSaved(false);
                    main.getRootTable().setStatusBarMessage("Drawable emptied for \"" + styleProperty.name + "\"");
                    main.getRootTable().refreshStyleProperties(true);
                } else {
                    boolean hasFont = false;
                    for (FontData font : main.getJsonData().getFonts()) {
                        if (font.getName().equals(styleProperty.value)) {
                            hasFont = true;
                            break;
                        }
                    }
                    
                    for (FreeTypeFontData font : main.getJsonData().getFreeTypeFonts()) {
                        if (font.name.equals(styleProperty.value)) {
                            hasFont = true;
                            break;
                        }
                    }

                    if (!hasFont) {
                        styleProperty.value = null;
                        main.getProjectData().setChangesSaved(false);
                        main.getRootTable().setStatusBarMessage("Drawable deleted for \"" + styleProperty.name + "\"");
                        main.getRootTable().refreshStyleProperties(true);
                    }
                }
            }
        } else if (customProperty != null) {
            if (object instanceof FontData) {
                main.getProjectData().setChangesSaved(false);
                FontData font = (FontData) object;
                CustomFontUndoable undoable = new CustomFontUndoable(main, customProperty, font.getName());
                main.getUndoableManager().addUndoable(undoable, true);
            } else if (object instanceof FreeTypeFontData) {
                main.getProjectData().setChangesSaved(false);
                FreeTypeFontData font = (FreeTypeFontData) object;
                CustomFontUndoable undoable = new CustomFontUndoable(main, customProperty, font.name);
                main.getUndoableManager().addUndoable(undoable, true);
            } else if (object instanceof Boolean) {
                if ((boolean) object) {
                    CustomFontUndoable undoable = new CustomFontUndoable(main, customProperty, null);
                    main.getUndoableManager().addUndoable(undoable, true);
                    main.getProjectData().setChangesSaved(false);
                    main.getRootTable().setStatusBarMessage("Drawable emptied for \"" + customProperty.getName() + "\"");
                    main.getRootTable().refreshStyleProperties(true);
                } else {
                    boolean hasFont = false;
                    for (FontData font : main.getJsonData().getFonts()) {
                        if (font.getName().equals(customProperty.getValue())) {
                            hasFont = true;
                            break;
                        }
                    }

                    if (!hasFont) {
                        customProperty.setValue(null);
                        main.getProjectData().setChangesSaved(false);
                        main.getRootTable().setStatusBarMessage("Drawable deleted for \"" + customProperty.getName() + "\"");
                        main.getRootTable().refreshStyleProperties(true);
                    }
                }
            }
        }
        
        if (listener != null) {
            listener.handle(null);
        }
        
        main.getRootTable().refreshPreview();
    }

    private void sortBySelectedMode() {
        switch (selectBox.getSelectedIndex()) {
            case 0:
                sortFontsAZ();
                break;
            case 1:
                sortFontsZA();
                break;
            case 2:
                sortFontsOldest();
                break;
            case 3:
                sortFontsNewest();
                break;
        }
    }

    private void sortFontsAZ() {
        Sort.instance().sort(fonts, (FontData o1, FontData o2) -> o1.toString().compareToIgnoreCase(o2.toString()));
        Sort.instance().sort(freeTypeFonts, (FreeTypeFontData o1, FreeTypeFontData o2) -> o1.name.compareToIgnoreCase(o2.name));
        refreshTable();
    }

    private void sortFontsZA() {
        Sort.instance().sort(fonts, (FontData o1, FontData o2) -> o1.toString().compareToIgnoreCase(o2.toString()) * -1);
        Sort.instance().sort(freeTypeFonts, (FreeTypeFontData o1, FreeTypeFontData o2) -> o1.name.compareToIgnoreCase(o2.name) * -1);
        refreshTable();
    }

    private void sortFontsOldest() {
        Sort.instance().sort(fonts, (FontData o1, FontData o2) -> {
            if (o1.file.lastModified() < o2.file.lastModified()) {
                return -1;
            } else if (o1.file.lastModified() > o2.file.lastModified()) {
                return 1;
            } else {
                return 0;
            }
        });
        
        Sort.instance().sort(freeTypeFonts, (FreeTypeFontData o1, FreeTypeFontData o2) -> {
            if (o1.file.lastModified() < o2.file.lastModified()) {
                return -1;
            } else if (o1.file.lastModified() > o2.file.lastModified()) {
                return 1;
            } else {
                return 0;
            }
        });

        refreshTable();
    }

    private void sortFontsNewest() {
        Sort.instance().sort(fonts, (FontData o1, FontData o2) -> {
            if (o1.file.lastModified() < o2.file.lastModified()) {
                return 1;
            } else if (o1.file.lastModified() > o2.file.lastModified()) {
                return -1;
            } else {
                return 0;
            }
        });
        
        Sort.instance().sort(freeTypeFonts, (FreeTypeFontData o1, FreeTypeFontData o2) -> {
            if (o1.file.lastModified() < o2.file.lastModified()) {
                return 1;
            } else if (o1.file.lastModified() > o2.file.lastModified()) {
                return -1;
            } else {
                return 0;
            }
        });
        
        refreshTable();
    }

    @Override
    public boolean remove() {
        main.getDesktopWorker().removeFilesDroppedListener(filesDroppedListener);
        
        produceAtlas();
        
        for (BitmapFont font : fontMap.values()) {
            font.dispose();
        }
        fontMap.clear();
        return super.remove();
    }

    private boolean produceAtlas() {
        try {
            if (atlas != null) {
                atlas.dispose();
                atlas = null;
            }
            
            if (!main.getAtlasData().atlasCurrent) {
                main.getAtlasData().writeAtlas();
                main.getAtlasData().atlasCurrent = true;
            }
            atlas = main.getAtlasData().getAtlas();

            for (FontData font : fonts) {
                BitmapFontData fontData = new BitmapFontData(font.file, false);
                Array<TextureRegion> regions = new Array<>();
                for (String path : fontData.imagePaths) {
                    FileHandle file = new FileHandle(path);
                    if (!file.exists()) {
                        file = fontData.fontFile.sibling(fontData.fontFile.nameWithoutExtension() + ".png");
                    }
                    TextureRegion region = atlas.findRegion(file.nameWithoutExtension());
                    if (region != null) {
                        regions.add(region);
                    }
                }
                fontMap.put(font, new BitmapFont(fontData, regions, true));
            }
            return true;
        } catch (Exception e) {
            Gdx.app.error(getClass().getName(), "Error while attempting to generate drawables.", e);
            main.getDialogFactory().showDialogError("Drawables Error...", "Error while attempting to generate drawables. Open log?");
            return false;
        }
    }
    
    private void newFontDialog() {
        String defaultPath = "";
        
        if (main.getProjectData().getLastFontPath() != null) {
            FileHandle fileHandle = new FileHandle(main.getProjectData().getLastFontPath());
            if (fileHandle.exists()) {
                defaultPath = main.getProjectData().getLastFontPath();
            }
        }
        
        String[] filterPatterns = null;
        if (!Utils.isMac()) {
            filterPatterns = new String[] {"*.fnt"};
        }
        
        List<File> files = main.getDesktopWorker().openMultipleDialog("Choose font file(s)...", defaultPath, filterPatterns, "Font files (*.fnt)");
        if (files != null && files.size() > 0) {
            FileHandle fileHandle = new FileHandle(files.get(0).getParentFile());
            main.getProjectData().setLastFontPath(fileHandle.path() + "/");
            fontNameDialog(files, 0);
        }
    }
    
    private void newFreeTypeFontDialog() {
        main.getDialogFactory().showDialogFreeTypeFont(new DialogFreeTypeFont.DialogFreeTypeFontListener() {
            @Override
            public void fontAdded(FreeTypeFontData font) {
                sortBySelectedMode();
                refreshTable();
            }
        });
    }
    
    private void newImageFontDialog() {
        main.getDesktopWorker().removeFilesDroppedListener(filesDroppedListener);
        main.getDialogFactory().showDialogImageFont((FileHandle file) -> {
            var files = new Array<FileHandle>();
            files.add(file);
            fontNameDialog(files, 0);
            main.getDesktopWorker().addFilesDroppedListener(filesDroppedListener);
        });
    }
    
    private void fontNameDialog(List<File> files, int index) {
        Array<FileHandle> handles = new Array<>();
        for (File file : files) {
            handles.add(new FileHandle(file));
        }
        
        fontNameDialog(handles, index);
    }
    
    private void fontNameDialog(Array<FileHandle> files, int index) {
        if (index < files.size) {
            try {
                final FileHandle fileHandle = files.get(index);

                final TextField textField = new TextField(FontData.filter(fileHandle.nameWithoutExtension()), getSkin());
                final Dialog nameDialog = new Dialog("Enter a name...", getSkin(), "bg") {
                    @Override
                    protected void result(Object object) {
                        if ((Boolean) object) {
                            String name = textField.getText();

                            addFont(name, fileHandle);

                        }
                    }

                    @Override
                    public boolean remove() {
                        fontNameDialog(files, index + 1);
                        return super.remove();
                    }
                };
                
                nameDialog.getTitleTable().padLeft(5.0f);
                
                nameDialog.button("OK", true);
                nameDialog.button("Cancel", false);
                nameDialog.getButtonTable().getCells().first().getActor().addListener(main.getHandListener());
                nameDialog.getButtonTable().getCells().get(1).getActor().addListener(main.getHandListener());
                final TextButton button = (TextButton) nameDialog.getButtonTable().getCells().first().getActor();
                
                nameDialog.getButtonTable().padBottom(15.0f);
                
                textField.setTextFieldListener((TextField textField1, char c) -> {
                    if (c == '\n') {
                        if (!button.isDisabled()) {
                            String name1 = textField1.getText();
                            if (addFont(name1, fileHandle)) {
                                nameDialog.hide();
                            }
                        }
                        main.getStage().setKeyboardFocus(textField1);
                    }
                });
                
                textField.addListener(main.getIbeamListener());
                
                nameDialog.getContentTable().defaults().padLeft(10.0f).padRight(10.0f).padTop(5.0f);
                nameDialog.text("Please enter a name for the new font: ");
                nameDialog.getContentTable().row();
                nameDialog.getContentTable().add(textField).growX();
                nameDialog.getContentTable().row();
                nameDialog.text("Preview:");
                nameDialog.getContentTable().row();

                LabelStyle previewStyle = new LabelStyle();
                previewStyle.font = new BitmapFont(fileHandle);
                Table table = new Table(getSkin());
                table.setBackground("white");
                BitmapFontData bitmapFontData = new BitmapFontData(fileHandle, false);
                if (Utils.brightness(Utils.averageEdgeColor(new FileHandle(bitmapFontData.imagePaths[0]))) > .5f) {
                    table.setColor(Color.BLACK);
                } else {
                    table.setColor(Color.WHITE);
                }
                table.add(new Label("Lorem Ipsum", previewStyle)).pad(5.0f);

                nameDialog.getContentTable().add(table);
                nameDialog.key(Keys.ESCAPE, false);
                button.setDisabled(!FontData.validate(textField.getText()));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        boolean disable = !FontData.validate(textField.getText());
                        if (!disable) {
                            for (FontData data : main.getJsonData().getFonts()) {
                                if (data.getName().equals(textField.getText())) {
                                    disable = true;
                                    break;
                                }
                            }
                        }
                        button.setDisabled(disable);
                    }
                });
                nameDialog.setColor(1.0f, 1.0f, 1.0f, 0.0f);
                
                textField.setFocusTraversal(false);
                
                if (!Utils.doesImageFitBox(new FileHandle(bitmapFontData.imagePaths[0]), maxTextureWidth, maxTextureHeight)) {
                    showAddFontSizeError(fileHandle.nameWithoutExtension());
                } else {
                    nameDialog.show(getStage());
                    getStage().setKeyboardFocus(textField);
                    textField.selectAll();
                }
            } catch (Exception e) {
                Gdx.app.error(getClass().getName(), "Error creating preview font from file", e);
                main.getDialogFactory().showDialogError("Preview Error...", "Error creating preview font from file. Check file paths.\n\nOpen log?");
            }
        } else {
            //after all fonts are processed
            if (main.getProjectData().areResourcesRelative()) {
                main.getProjectData().makeResourcesRelative();
            }
        }
    }
    
    private void showAddFontSizeError(String name) {
        Dialog dialog = new Dialog("", getSkin(), "bg");
        
        dialog.getContentTable().defaults().pad(10.0f);
        Label label = new Label("Error adding font...", getSkin(), "title");
        dialog.getContentTable().add(label);
        
        dialog.getContentTable().row();
        dialog.text("Unable to add font \"" + name +
                "\". Ensure image dimensions\nare less than max texture dimensions (" +
                maxTextureWidth + "x" + 
                maxTextureHeight + ").\nSee project settings.");
        
        dialog.getButtonTable().defaults().padBottom(10.0f).minWidth(50.0f);
        dialog.button("Ok");
        dialog.key(Keys.ENTER, null).key(Keys.ESCAPE, null);
        dialog.show(getStage());
    }
}
