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
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.*;
import com.ray3k.skincomposer.FilesDroppedListener;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.UndoableManager.CustomFontUndoable;
import com.ray3k.skincomposer.UndoableManager.FontUndoable;
import com.ray3k.skincomposer.data.*;
import com.ray3k.skincomposer.data.DrawableData.DrawableType;
import com.ray3k.skincomposer.utils.Utils;
import com.ray3k.stripe.PopTableClickListener;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import static com.ray3k.skincomposer.Main.*;

public class DialogFonts extends Dialog {
    private StyleProperty styleProperty;
    private CustomProperty customProperty;
    private Array<FontData> fonts;
    private Array<FreeTypeFontData> freeTypeFonts;
    private Array<DrawableData> drawables;
    private Array<DrawableData> fontDrawables;
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
        
        //extract max texture dimensions from atlas-export-settings.json
        FileHandle defaultsFile = Main.appFolder.child("texturepacker/atlas-export-settings.json");
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
        
        fonts = jsonData.getFonts();
        freeTypeFonts = jsonData.getFreeTypeFonts();
        drawables = atlasData.getDrawables();
        fontDrawables = atlasData.getFontDrawables();

        fontMap = new ObjectMap<>();
        produceAtlas();
        
        filesDroppedListener = (Array<FileHandle> files) -> {
            Iterator<FileHandle> iter = files.iterator();
            while (iter.hasNext()) {
                FileHandle file = iter.next();
                if (file.isDirectory()) {
                    files.addAll(file.list());
                    iter.remove();
                } else if (!file.name().toLowerCase().endsWith(".fnt")) {
                    iter.remove();
                }
            }
            
            if (files.size > 0) {
                fontSettingsDialog(files, 0);
            }
        };
        
        desktopWorker.addFilesDroppedListener(filesDroppedListener);

        populate();
    }
    
    public DialogFonts(StyleProperty styleProperty, EventListener listener) {
        super("", skin, "dialog");
        this.styleProperty = styleProperty;
        initialize(main, listener);
    }
    
    public DialogFonts(CustomProperty customProperty, EventListener listener) {
        super("", skin, "dialog");
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
        selectBox.addListener(handListener);
        selectBox.getList().addListener(handListener);
        table.add(selectBox);
    
        TextButton textButton = new TextButton("Add...", getSkin());
        table.add(textButton);
        textButton.addListener(handListener);
        textButton.addListener(new AddListener());
        
        getContentTable().add(table).expandX().left();
        getContentTable().row();

        key(Keys.ESCAPE, false);
        if (styleProperty != null || customProperty != null) {
            button("Clear Font", true);
            button("Cancel", false);
            getButtonTable().getCells().first().getActor().addListener(handListener);
            getButtonTable().getCells().get(1).getActor().addListener(handListener);
        } else {
            button("Close", false);
            getButtonTable().getCells().first().getActor().addListener(handListener);
        }
        
        getButtonTable().padBottom(15.0f);

        fontsTable = new Table();

        table = new Table();
        table.add(fontsTable).pad(5.0f);
        scrollPane = new ScrollPane(table, getSkin());
        scrollPane.setFadeScrollBars(false);
        getContentTable().add(scrollPane).grow();
    }
    
    public class AddListener extends PopTableClickListener {
        public AddListener() {
            super(getSkin(), "more");
            
            populate();
        }
        
        private void populate() {
            var table = getPopTable();
            table.clearChildren();
            
            table.pad(10);
            table.defaults().space(10).fillX();
            var hideListener = new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    table.hide();
                }
            };
    
            var imageButton = new TextButton("Open FNT", getSkin(), "new");
            table.add(imageButton).expandX();
            imageButton.addListener(handListener);
            imageButton.addListener(hideListener);
            imageButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    newFontDialog();
                }
            });
    
            table.row();
            imageButton = new TextButton("Create FNT", getSkin(), "new");
            imageButton.addListener(hideListener);
            imageButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    newBitmapFontDialog();
                }
            });
            imageButton.addListener(handListener);
            table.add(imageButton).expandX();
    
            table.row();
            imageButton = new TextButton("FreeType Font", getSkin(), "new");
            table.add(imageButton);
            imageButton.addListener(handListener);
            imageButton.addListener(hideListener);
            imageButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    newFreeTypeFontDialog();
                }
            });
    
            table.row();
            imageButton = new TextButton("Create from Image", getSkin(), "new");
            table.add(imageButton);
            imageButton.addListener(handListener);
            imageButton.addListener(hideListener);
            imageButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    newImageFontDialog();
                }
            });
        }
    }

    @Override
    public Dialog show(Stage stage) {
        Dialog dialog = super.show(stage);
        stage.setScrollFocus(scrollPane);
        
        fire(new DialogEvent(DialogEvent.Type.OPEN));
        return dialog;
    }

    private boolean addFont(String name, int scaling, boolean markupEnabled, boolean flip, FileHandle file) {
        if (FontData.validate(name)) {
            try {
                projectData.setChangesSaved(false);
                FontData font = new FontData(name, scaling, markupEnabled, flip, file);
                
                //remove any existing FontData that shares the same name.
                if (fonts.contains(font, false)) {
                    FontData deleteFont = fonts.get(fonts.indexOf(font, false));
                    
                    BitmapFontData deleteFontData = new BitmapFontData(deleteFont.file, false);
                    for (String path : deleteFontData.imagePaths) {
                        FileHandle imagefile = new FileHandle(path);
                        var drawable = atlasData.getFontDrawable(imagefile.nameWithoutExtension());
                        if (drawable != null) {
                            fontDrawables.removeValue(drawable, false);
                        }
                    }
                    
                    fonts.removeValue(font, false);
                }
                
                var bitmapFontData = new BitmapFontData(file, font.isFlip());
                for (String path : bitmapFontData.imagePaths) {
                    //remove any existing drawables that share the name
                    FileHandle imagefile = new FileHandle(path);
                    var duplicateDrawable = atlasData.getDrawable(imagefile.nameWithoutExtension());
                    if (duplicateDrawable != null) {
                        drawables.removeValue(duplicateDrawable, false);
                        for (Array<StyleData> datas : jsonData.getClassStyleMap().values()) {
                            for (StyleData data : datas) {
                                for (StyleProperty styleProperty : data.properties.values()) {
                                    if (styleProperty != null && styleProperty.type.equals(Drawable.class) && styleProperty.value != null && styleProperty.value.equals(duplicateDrawable.toString())) {
                                        styleProperty.value = null;
                                    }
                                }
                            }
                        }
                        rootTable.refreshStyleProperties(true);
                        rootTable.refreshPreview();
                    }
                    
                    var drawable = new DrawableData(new FileHandle(path));
                    drawable.type = DrawableType.FONT;
                    
                    if (!fontDrawables.contains(drawable, false)) {
                        atlasData.atlasCurrent = false;
                        fontDrawables.add(drawable);
                    }
                }
                produceAtlas();
                fonts.add(font);
                
                Array<TextureRegion> regions = new Array<>();
                for (String path : bitmapFontData.imagePaths) {
                    FileHandle imageFile = new FileHandle(path);
                    regions.add(atlas.findRegion(imageFile.nameWithoutExtension()));
                }
                var bitmapFont = new BitmapFont(bitmapFontData, regions, true);
                if (scaling != -1) bitmapFontData.setScale(scaling / bitmapFont.getCapHeight());
                bitmapFontData.markupEnabled = markupEnabled;
                bitmapFontData.flipped = flip;
                fontMap.put(font, bitmapFont);
                
                sortBySelectedMode();
                refreshTable();
            } catch (FontData.NameFormatException e) {
                Gdx.app.error(getClass().getName(), "Error creating font from file", e);
                dialogFactory.showDialogError("Font Error...", "Error creating font from file. Check file paths.\n\nOpen log?");
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
            fontsTable.add(new Label("No fonts have been set!", getSkin(), "black"));
        } else {
            
            if (fonts.size > 0) {
                Label label = new Label("Bitmap Fonts", getSkin(), "black");
                label.setAlignment(Align.center);
                fontsTable.add(label);
                fontsTable.row();
            }
            
            for (FontData font : fonts) {
                Button button = new Button(getSkin(), "color-base");
                Label label = new Label(font.getName(), getSkin());
                label.setTouchable(Touchable.disabled);
                button.add(label).left();
                button.addListener(handListener);
                
                Button settingsButton = new Button(getSkin(), "settings-small");
                settingsButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        changeSettingsDialog(font);
                        
                        event.setBubbles(false);
                    }
                });
                settingsButton.addListener(new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        event.setBubbles(false);
                        return true;
                    }
                    
                });
                button.add(settingsButton).padLeft(15.0f);
                
                TextTooltip toolTip = Main.fixTooltip(new TextTooltip("Font Settings", tooltipManager, getSkin()));
                settingsButton.addListener(toolTip);
                
                LabelStyle style = new LabelStyle();
                style.font = fontMap.get(font);
                style.fontColor = Color.WHITE;
                var previewText = "Lorem Ipsum";
                if (!Utils.fontHasAllChars(style.font.getData(), previewText)) {
                    previewText = "LOREM IPSUM";
                }
                if (!Utils.fontHasAllChars(style.font.getData(), previewText)) {
                    previewText = "lorem ipsum";
                }
                if (!Utils.fontHasAllChars(style.font.getData(), previewText)) {
                    previewText = "0123456789";
                }
                if (!Utils.fontHasAllChars(style.font.getData(), previewText)) {
                    previewText = "Lorem Ipsum";
                }
                label = new Label(previewText, style);
                label.setAlignment(Align.center);
                label.setTouchable(Touchable.disabled);
                Table bg = new Table(getSkin());
                bg.setBackground("white");
                BitmapFontData bf = new BitmapFontData(font.file, false);
                if (bf.imagePaths.length > 0) {
                    FileHandle file = new FileHandle(bf.imagePaths[0]);
                    if (!file.exists()) {
                        file = font.file.sibling(file.name());
                    }
                    System.out.println(font.file.name() + " " + bf.imagePaths[0] + " " + file.exists());
                    if (!file.exists()) {
                        bg.setColor(Color.BLACK);
                    } else if (Utils.brightness(Utils.averageEdgeColor(file)) < .5f) {
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
                        projectData.setChangesSaved(false);
                        BitmapFontData bitmapFontData = new BitmapFontData(deleteFont.file, false);
                        for (String path : bitmapFontData.imagePaths) {
                            FileHandle imagefile = new FileHandle(path);
                            fontDrawables.removeValue(new DrawableData(imagefile), false);
                        }
                        
                        for (Array<StyleData> datas : jsonData.getClassStyleMap().values()) {
                            for (StyleData data : datas) {
                                for (StyleProperty property : data.properties.values()) {
                                    if (property != null && property.type.equals(BitmapFont.class) && property.value != null && property.value.equals(deleteFont.getName())) {
                                        property.value = null;
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
                button.add(closeButton).padLeft(5.0f).right();
                
                toolTip = (Main.makeTooltip("Delete Font", tooltipManager, getSkin()));
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
                Label label = new Label("FreeType Fonts", getSkin(), "black");
                label.setAlignment(Align.center);
                fontsTable.add(label).spaceTop(20.0f);
                fontsTable.row();
            }
            
            for (FreeTypeFontData font : freeTypeFonts) {
                Button button = new Button(getSkin(), "color-base");
                Label label = new Label(font.name, getSkin());
                label.setTouchable(Touchable.disabled);
                button.add(label).left();
                button.addListener(handListener);
                
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
                
                TextTooltip toolTip = (Main.makeTooltip("Change Freetype Settings", tooltipManager, getSkin()));
                renameButton.addListener(toolTip);
                
                LabelStyle style = new LabelStyle();
                style.font = font.bitmapFont;
                style.fontColor = Color.WHITE;
                var previewText = "Lorem Ipsum";
                if (!Utils.fontHasAllChars(style.font.getData(), previewText)) {
                    previewText = "LOREM IPSUM";
                }
                if (!Utils.fontHasAllChars(style.font.getData(), previewText)) {
                    previewText = "lorem ipsum";
                }
                if (!Utils.fontHasAllChars(style.font.getData(), previewText)) {
                    previewText = "0123456789";
                }
                if (!Utils.fontHasAllChars(style.font.getData(), previewText)) {
                    previewText = "Lorem Ipsum";
                }
                label = new Label(previewText, style);
                label.setAlignment(Align.center);
                label.setTouchable(Touchable.disabled);
                Table bg = new Table(getSkin());
                bg.setBackground("white");
                if (font.color == null || Utils.brightness(jsonData.getColorByName(font.color).color) < .5f) {
                    bg.setColor(Color.WHITE);
                } else {
                    bg.setColor(Color.BLACK);
                }
                bg.add(label).pad(5.0f).grow();
                button.add(bg).padLeft(15).growX();

                Button closeButton = new Button(getSkin(), "delete-small");
                final FreeTypeFontData deleteFont = font;
                closeButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        freeTypeFonts.removeValue(deleteFont, true);
                        projectData.setChangesSaved(false);
                        
                        for (Array<StyleData> datas : jsonData.getClassStyleMap().values()) {
                            for (StyleData data : datas) {
                                for (StyleProperty property : data.properties.values()) {
                                    if (property != null && property.type.equals(BitmapFont.class) && property.value != null && property.value.equals(deleteFont.name)) {
                                        property.value = null;
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
                button.add(closeButton).padLeft(5.0f).right();
                
                toolTip = (Main.makeTooltip("Delete Font", tooltipManager, getSkin()));
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
        dialogFactory.showDialogFreeTypeFont(font, new DialogFreeTypeFont.DialogFreeTypeFontListener() {
            @Override
            public void fontAdded(FreeTypeFontData font) {
                sortBySelectedMode();
                refreshTable();
            }

            @Override
            public void cancelled() {
                
            }
        });
    }
    
    private void changeSettingsDialog(final FontData font) {
        TextField nameField = new TextField(font.getName(), getSkin());
        var scalingField = new TextField(Integer.toString(font.getScaling()), skin);
        final var markupCheckBox = new CheckBox("markupEnabled", skin);
        markupCheckBox.setChecked(font.isMarkupEnabled());
        final var flipCheckBox = new CheckBox("flip", skin);
        flipCheckBox.setChecked(font.isFlip());
        TextButton okButton;
        
        Dialog dialog = new Dialog("Font Settings...", getSkin(), "bg") {
            @Override
            protected void result(Object object) {
                if ((boolean) object) {
                    var scaling = Integer.parseInt(scalingField.getText());
                    if (scaling < 1) scaling = -1;
                    changeFontSettings(font, nameField.getText(), scaling, markupCheckBox.isChecked(), flipCheckBox.isChecked());
                }
            }

            @Override
            public Dialog show(Stage stage) {
                Dialog dialog = super.show(stage);
                stage.setKeyboardFocus(nameField);
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
        nameField.selectAll();
        nameField.addListener(ibeamListener);
        dialog.getContentTable().add(nameField);
    
        dialog.getContentTable().row();
        label = new Label("Enter scaling value (-1 for default):", getSkin());
        dialog.getContentTable().add(label);
        
        dialog.getContentTable().row();
        scalingField.setTextFieldFilter((textField, c) -> Character.isDigit(c) || c == '-');
        dialog.getContentTable().add(scalingField);
        scalingField.addListener(ibeamListener);
    
        dialog.getContentTable().row();
        LabelStyle previewStyle = new LabelStyle();
        previewStyle.font = new BitmapFont(font.file, font.isFlip());
        final var previewCapHeight = previewStyle.font.getCapHeight();
        if (font.getScaling() != -1) previewStyle.font.getData().setScale(font.getScaling() / previewStyle.font.getCapHeight());
        
        dialog.getContentTable().row();
        Table table = new Table();
        dialog.getContentTable().add(table);
        
        table.defaults().left();
        table.add(markupCheckBox);
        markupCheckBox.addListener(handListener);
    
        table.row();
        table.add(flipCheckBox);
        flipCheckBox.addListener(handListener);
    
        dialog.getContentTable().row();
        table = new Table(getSkin());
        table.setBackground("white");
        var imageFile = new FileHandle(previewStyle.font.getData().imagePaths[0]);
        imageFile = font.file.sibling(imageFile.name());
        if (Utils.brightness(Utils.averageEdgeColor(imageFile)) > .5f) {
            table.setColor(Color.BLACK);
        } else {
            table.setColor(Color.WHITE);
        }
        dialog.getContentTable().add(table).grow();
    
        final var previewLabel = new Label("Lorem Ipsum", previewStyle);
        previewLabel.setAlignment(Align.center);
    
        var previewScrollPane = new ScrollPane(previewLabel, skin);
        previewScrollPane.setFadeScrollBars(false);
        table.add(previewScrollPane).pad(5.0f).grow().minHeight(100);
        
        dialog.button("OK", true);
        dialog.button("Cancel", false).key(Keys.ESCAPE, false);
        okButton = (TextButton) dialog.getButtonTable().getCells().first().getActor();
        okButton.addListener(handListener);
        dialog.getButtonTable().getCells().get(1).getActor().addListener(handListener);
        
        dialog.getButtonTable().padBottom(15.0f);
        
        nameField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                okButton.setDisabled(!checkIfFontNameValid(nameField.getText(), font.getName()));
            }
        });
        nameField.setTextFieldListener((TextField textField1, char c) -> {
            if (c == '\n') {
                if (!okButton.isDisabled()) {
                    var scaling = Integer.parseInt(scalingField.getText());
                    if (scaling < 1) scaling = -1;
                    changeFontSettings(font, textField1.getText(), scaling, markupCheckBox.isChecked(), flipCheckBox.isChecked());
                    dialog.hide();
                }
            }
        });
        scalingField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                var isNumeric = Utils.isNumeric(scalingField.getText());
                okButton.setDisabled(!isNumeric);
                if (isNumeric) {
                    var scaling = Integer.parseInt(scalingField.getText());
                    if (scaling > 0) {
                        previewStyle.font.getData().setScale(scaling / previewCapHeight);
                        previewLabel.setStyle(previewStyle);
                    } else {
                        previewStyle.font.getData().setScale(1);
                        previewLabel.setStyle(previewStyle);
                    }
                } else {
                    previewStyle.font.getData().setScale(1);
                    previewLabel.setStyle(previewStyle);
                }
            }
        });
        
        Utils.onChange(markupCheckBox, () -> {
            previewStyle.font.getData().markupEnabled = markupCheckBox.isChecked();
            previewLabel.setStyle(previewStyle);
        });
    
        Utils.onChange(flipCheckBox, () -> {
            previewStyle.font = new BitmapFont(font.file, flipCheckBox.isChecked());
            previewLabel.setStyle(previewStyle);
        });
        
        dialog.show(getStage());
    }
    
    private void changeFontSettings(FontData font, String newName, int newScaling, boolean newMarkupEnabled, boolean newFlip) {
        for (Array<StyleData> datas : jsonData.getClassStyleMap().values()) {
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
            dialogFactory.showDialogError("Rename Font Error...", "Error trying to rename a font.\n\nOpen log?");
        }
        
        font.setScaling(newScaling);
        font.setMarkupEnabled(newMarkupEnabled);
        font.setFlip(newFlip);

        undoableManager.clearUndoables();

        rootTable.refreshStyleProperties(true);
        rootTable.refreshPreview();
        
        projectData.setChangesSaved(false);
        
        refreshTable();
    }

    @Override
    protected void result(Object object) {
        if (styleProperty != null) {
            if (object instanceof FontData) {
                projectData.setChangesSaved(false);
                FontData font = (FontData) object;
                FontUndoable undoable = new FontUndoable(rootTable,
                        jsonData, styleProperty, styleProperty.value, font.getName());
                undoableManager.addUndoable(undoable, true);
            } else if (object instanceof FreeTypeFontData) {
                projectData.setChangesSaved(false);
                FreeTypeFontData font = (FreeTypeFontData) object;
                FontUndoable undoable = new FontUndoable(rootTable,
                        jsonData, styleProperty, styleProperty.value, font.name);
                undoableManager.addUndoable(undoable, true);
            } else if (object instanceof Boolean) {
                if ((boolean) object) {
                    FontUndoable undoable = new FontUndoable(rootTable,
                            jsonData, styleProperty, styleProperty.value, null);
                    undoableManager.addUndoable(undoable, true);
                    projectData.setChangesSaved(false);
                    rootTable.refreshStyleProperties(true);
                } else {
                    boolean hasFont = false;
                    for (FontData font : jsonData.getFonts()) {
                        if (font.getName().equals(styleProperty.value)) {
                            hasFont = true;
                            break;
                        }
                    }
                    
                    for (FreeTypeFontData font : jsonData.getFreeTypeFonts()) {
                        if (font.name.equals(styleProperty.value)) {
                            hasFont = true;
                            break;
                        }
                    }

                    if (!hasFont) {
                        styleProperty.value = null;
                        projectData.setChangesSaved(false);
                        rootTable.refreshStyleProperties(true);
                    }
                }
            }
        } else if (customProperty != null) {
            if (object instanceof FontData) {
                projectData.setChangesSaved(false);
                FontData font = (FontData) object;
                CustomFontUndoable undoable = new CustomFontUndoable(main, customProperty, font.getName());
                undoableManager.addUndoable(undoable, true);
            } else if (object instanceof FreeTypeFontData) {
                projectData.setChangesSaved(false);
                FreeTypeFontData font = (FreeTypeFontData) object;
                CustomFontUndoable undoable = new CustomFontUndoable(main, customProperty, font.name);
                undoableManager.addUndoable(undoable, true);
            } else if (object instanceof Boolean) {
                if ((boolean) object) {
                    CustomFontUndoable undoable = new CustomFontUndoable(main, customProperty, null);
                    undoableManager.addUndoable(undoable, true);
                    projectData.setChangesSaved(false);
                    rootTable.refreshStyleProperties(true);
                } else {
                    boolean hasFont = false;
                    for (FontData font : jsonData.getFonts()) {
                        if (font.getName().equals(customProperty.getValue())) {
                            hasFont = true;
                            break;
                        }
                    }

                    if (!hasFont) {
                        customProperty.setValue(null);
                        projectData.setChangesSaved(false);
                        rootTable.refreshStyleProperties(true);
                    }
                }
            }
        }
        
        if (listener != null) {
            listener.handle(null);
        }
        
        rootTable.refreshPreview();
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
        desktopWorker.removeFilesDroppedListener(filesDroppedListener);
        
        produceAtlas();
        
        for (BitmapFont font : fontMap.values()) {
            font.dispose();
        }
        fontMap.clear();
        
        fire(new DialogEvent(DialogEvent.Type.CLOSE));
        return super.remove();
    }

    private boolean produceAtlas() {
        try {
            if (atlas != null) {
                atlas.dispose();
                atlas = null;
            }
            
            if (!atlasData.atlasCurrent) {
                FileHandle defaultsFile = Main.appFolder.child("texturepacker/atlas-internal-settings.json");
                atlasData.writeAtlas(defaultsFile);
                atlasData.atlasCurrent = true;
            }
            atlas = atlasData.getAtlas();

            for (FontData font : fonts) {
                BitmapFontData fontData = new BitmapFontData(font.file, font.isFlip());
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
                var bitmapFont = new BitmapFont(fontData, regions, true);
                if (font.getScaling() != -1) bitmapFont.getData().setScale(font.getScaling() / bitmapFont.getCapHeight());
                bitmapFont.getData().markupEnabled = font.isMarkupEnabled();
                fontMap.put(font, bitmapFont);
            }
            return true;
        } catch (Exception e) {
            Gdx.app.error(getClass().getName(), "Error while attempting to generate drawables.", e);
            dialogFactory.showDialogError("Drawables Error...", "Error while attempting to generate drawables. Open log?");
            return false;
        }
    }
    
    private void newFontDialog() {
        dialogFactory.showDialogLoading(() -> {
            String defaultPath = "";

            if (projectData.getLastFontPath() != null) {
                FileHandle fileHandle = new FileHandle(projectData.getLastFontPath());
                if (fileHandle.exists()) {
                    defaultPath = projectData.getLastFontPath();
                }
            }

            List<File> files = desktopWorker.openMultipleDialog("Choose font file(s)...", defaultPath, "fnt", "Font files (*.fnt)");
            if (files != null && files.size() > 0) {
                Gdx.app.postRunnable(() -> {
                    FileHandle fileHandle = new FileHandle(files.get(0).getParentFile());
                    projectData.setLastFontPath(fileHandle.path() + "/");
                    checkExistingDrawablesDialog(files, () -> {
                        fontSettingsDialog(files, 0);
                    });
                });
            }
        });
    }
    
    private void checkExistingDrawablesDialog(List<File> files, Runnable runnable) {
        boolean execute = true;
        
        fontLoop : for (var fontFile : files) {
            var bitmapFontData = new BitmapFontData(new FileHandle(fontFile), false);
            for (var imagePath : bitmapFontData.imagePaths) {
                var imageFile = new FileHandle(imagePath);
                if (atlasData.getDrawable(imageFile.nameWithoutExtension()) != null) {
                    execute = false;
                    break fontLoop;
                }
            }
        }
        
        if (execute) {
            runnable.run();
        } else {
            var dialog = new Dialog("Override Drawables?", getSkin()) {
                @Override
                protected void result(Object object) {
                    if ((Boolean) object) {
                        runnable.run();
                    }
                }
            };
            dialog.getTitleTable().padLeft(5);
            dialog.getContentTable().pad(5).padBottom(0);
            dialog.getButtonTable().pad(5);
            
            dialog.text("This font will override existing drawables.\n"
                    + "Proceed?");
            
            var textButton = new TextButton("OK", getSkin());
            textButton.addListener(handListener);
            dialog.button(textButton, true);
            
            textButton = new TextButton("Cancel", getSkin());
            textButton.addListener(handListener);
            dialog.button(textButton, false);
            
            dialog.key(Keys.ENTER, true).key(Keys.NUMPAD_ENTER, true).key(Keys.ESCAPE, false);
            
            dialog.show(getStage());
        }
    }
    
    private void newBitmapFontDialog() {
        desktopWorker.removeFilesDroppedListener(filesDroppedListener);
        
        dialogFactory.showDialogBitmapFont((FileHandle file) -> {
            var files = new Array<FileHandle>();
            files.add(file);
            fontSettingsDialog(files, 0);
        

            desktopWorker.addFilesDroppedListener(filesDroppedListener);
        });
    }
    
    private void newFreeTypeFontDialog() {
        desktopWorker.removeFilesDroppedListener(filesDroppedListener);
        
        dialogFactory.showDialogFreeTypeFont(new DialogFreeTypeFont.DialogFreeTypeFontListener() {
            @Override
            public void fontAdded(FreeTypeFontData font) {
                sortBySelectedMode();
                refreshTable();
                
                desktopWorker.addFilesDroppedListener(filesDroppedListener);
                dialogFactory.showTipFreeType();
            }

            @Override
            public void cancelled() {
                desktopWorker.addFilesDroppedListener(filesDroppedListener);
            }
        });
    }
    
    private void newImageFontDialog() {
        desktopWorker.removeFilesDroppedListener(filesDroppedListener);
        dialogFactory.showDialogImageFont((FileHandle file) -> {
            var files = new Array<FileHandle>();
            files.add(file);
            fontSettingsDialog(files, 0);
            desktopWorker.addFilesDroppedListener(filesDroppedListener);
        });
    }
    
    private void fontSettingsDialog(List<File> files, int index) {
        Array<FileHandle> handles = new Array<>();
        files.forEach((file) -> {
            handles.add(new FileHandle(file));
        });
        
        fontSettingsDialog(handles, index);
    }
    
    private void fontSettingsDialog(Array<FileHandle> files, int index) {
        if (index < files.size) {
            try {
                final FileHandle fileHandle = files.get(index);
                
                final var nameField = new TextField(FontData.filter(fileHandle.nameWithoutExtension()), getSkin());
                final var scalingField = new TextField("-1", skin);
                final var markupCheckBox = new CheckBox("markupEnabled", skin);
                final var flipCheckBox = new CheckBox("flip", skin);
                final Dialog nameDialog = new Dialog("Font settings...", getSkin(), "bg") {
                    @Override
                    protected void result(Object object) {
                        if ((Boolean) object) {
                            String name = nameField.getText();
    
                            var scaling = Integer.parseInt(scalingField.getText());
                            if (scaling < 1) scaling = -1;
                            addFont(name, MathUtils.round(scaling), markupCheckBox.isChecked(), flipCheckBox.isChecked(), fileHandle);
                        }
                    }

                    @Override
                    public boolean remove() {
                        fontSettingsDialog(files, index + 1);
                        return super.remove();
                    }
                };
                
                nameDialog.getTitleTable().padLeft(5.0f);
                
                nameDialog.button("OK", true);
                nameDialog.button("Cancel", false);
                nameDialog.getButtonTable().getCells().first().getActor().addListener(handListener);
                nameDialog.getButtonTable().getCells().get(1).getActor().addListener(handListener);
                final TextButton button = (TextButton) nameDialog.getButtonTable().getCells().first().getActor();
                
                nameDialog.getButtonTable().padBottom(15.0f);
                
                nameField.setTextFieldListener((TextField textField1, char c) -> {
                    if (c == '\n') {
                        if (!button.isDisabled()) {
                            String name1 = textField1.getText();
                            var scaling = Integer.parseInt(scalingField.getText());
                            if (scaling < 1) scaling = -1;
                            if (addFont(name1, MathUtils.round(scaling), markupCheckBox.isChecked(), flipCheckBox.isChecked(), fileHandle)) {
                                nameDialog.hide();
                            }
                        }
                        stage.setKeyboardFocus(textField1);
                    }
                });
                
                nameField.addListener(ibeamListener);
                
                nameDialog.getContentTable().defaults().padLeft(10.0f).padRight(10.0f).padTop(5.0f);
                nameDialog.text("Please enter a name for the new font: ");
                
                nameDialog.getContentTable().row();
                nameDialog.getContentTable().add(nameField).growX();
                
                nameDialog.getContentTable().row();
                nameDialog.text("Enter scaling value (-1 for default):");
                
                nameDialog.getContentTable().row();
                scalingField.setTextFieldFilter((textField, c) -> Character.isDigit(c) || c == '-');
                nameDialog.getContentTable().add(scalingField);
                scalingField.addListener(ibeamListener);
    
                nameDialog.getContentTable().row();
                Table table = new Table();
                nameDialog.getContentTable().add(table);
    
                table.defaults().left();
                table.add(markupCheckBox);
                markupCheckBox.addListener(handListener);
    
                table.row();
                table.add(flipCheckBox);
                flipCheckBox.addListener(handListener);
                
                nameDialog.getContentTable().row();
                nameDialog.text("Preview:");
                nameDialog.getContentTable().row();

                LabelStyle previewStyle = new LabelStyle();
                previewStyle.font = new BitmapFont(fileHandle, false);
                final var previewCapHeight = previewStyle.font.getCapHeight();
                table = new Table(getSkin());
                table.setBackground("white");
                if (Utils.brightness(Utils.averageEdgeColor(new FileHandle(previewStyle.font.getData().imagePaths[0]))) > .5f) {
                    table.setColor(Color.BLACK);
                } else {
                    table.setColor(Color.WHITE);
                }
                nameDialog.getContentTable().add(table).grow();
                
                final var previewLabel = new Label("Lorem Ipsum", previewStyle);
                previewLabel.setAlignment(Align.center);
                
                var previewScrollPane = new ScrollPane(previewLabel, skin);
                previewScrollPane.setFadeScrollBars(false);
                table.add(previewScrollPane).pad(5.0f).grow().minHeight(100);
                
                nameDialog.key(Keys.ESCAPE, false);
                button.setDisabled(!checkIfFontNameValid(nameField.getText(), null));
                nameField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        button.setDisabled(!checkIfFontNameValid(nameField.getText(), null));
                    }
                });
                nameDialog.setColor(1.0f, 1.0f, 1.0f, 0.0f);
                
                scalingField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        var isNumeric = Utils.isNumeric(scalingField.getText());
                        button.setDisabled(!isNumeric);
                        if (isNumeric) {
                            var scaling = Integer.parseInt(scalingField.getText());
                            if (scaling > 0) {
                                previewStyle.font.getData().setScale(scaling / previewCapHeight);
                                previewLabel.setStyle(previewStyle);
                            } else {
                                previewStyle.font.getData().setScale(1);
                                previewLabel.setStyle(previewStyle);
                            }
                        } else {
                            previewStyle.font.getData().setScale(1);
                            previewLabel.setStyle(previewStyle);
                        }
                    }
                });
    
                Utils.onChange(markupCheckBox, () -> {
                    previewStyle.font.getData().markupEnabled = markupCheckBox.isChecked();
                    previewLabel.setStyle(previewStyle);
                });
    
                Utils.onChange(flipCheckBox, () -> {
                    previewStyle.font = new BitmapFont(previewStyle.font.getData().fontFile, flipCheckBox.isChecked());
                    
                    previewLabel.setStyle(previewStyle);
                });
                
                if (!Utils.doesImageFitBox(new FileHandle(previewStyle.font.getData().imagePaths[0]), maxTextureWidth, maxTextureHeight)) {
                    showAddFontSizeError(fileHandle.nameWithoutExtension());
                } else {
                    nameDialog.show(getStage());
                    getStage().setKeyboardFocus(nameField);
                    nameField.selectAll();
                }
            } catch (Exception e) {
                Gdx.app.error(getClass().getName(), "Error creating preview font from file", e);
                dialogFactory.showDialogError("Preview Error...", "Error creating preview font from file. Check file paths.\n\nOpen log?");
            }
        } else {
            //after all fonts are processed
            if (projectData.areResourcesRelative()) {
                projectData.makeResourcesRelative();
            }
        }
    }
    
    private boolean checkIfFontNameValid(String name, String originalName) {
        boolean valid = FontData.validate(name);
        if (valid || originalName != null && !name.equals(originalName)) {
            for (var data : jsonData.getFonts()) {
                if (data.getName().equals(name)) {
                    valid = false;
                    break;
                }
            }
        }
        
        if (valid) for (var data : jsonData.getFreeTypeFonts()) {
            if (data.name.equals(name)) {
                valid = false;
                break;
            }
        }
        return valid;
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
                maxTextureHeight + ").\nSee Project > Settings > Texture Packer Settings.");
        
        dialog.getButtonTable().defaults().padBottom(10.0f).minWidth(50.0f);
        dialog.button("Ok");
        dialog.key(Keys.ENTER, null).key(Keys.NUMPAD_ENTER, null).key(Keys.ESCAPE, null);
        dialog.show(getStage());
    }
}
