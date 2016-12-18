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
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Sort;
import com.ray3k.skincomposer.FilesDroppedListener;
import com.ray3k.skincomposer.IbeamListener;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.data.AtlasData;
import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.data.FontData;
import com.ray3k.skincomposer.data.JsonData;
import com.ray3k.skincomposer.data.ProjectData;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.data.StyleProperty;
import com.ray3k.skincomposer.panel.PanelClassBar;
import com.ray3k.skincomposer.panel.PanelPreviewProperties;
import com.ray3k.skincomposer.panel.PanelStatusBar;
import com.ray3k.skincomposer.panel.PanelStyleProperties;
import com.ray3k.skincomposer.utils.Utils;
import java.io.File;
import java.util.Iterator;
import java.util.List;

public class DialogFonts extends Dialog {

    private Skin skin;
    private StyleProperty styleProperty;
    private Array<FontData> fonts;
    private Array<DrawableData> drawables;
    private Table fontsTable;
    private SelectBox<String> selectBox;
    private ObjectMap<FontData, BitmapFont> fontMap;
    private TextureAtlas atlas;
    private EventListener listener;
    private FilesDroppedListener filesDroppedListener;
    private ScrollPane scrollPane;
    private JsonData jsonData;
    private ProjectData projectData;
    private AtlasData atlasData;

    public DialogFonts(Skin skin, StyleProperty styleProperty, JsonData jsonData, ProjectData projectData, AtlasData atlasData, EventListener listener) {
        this(skin, "default", styleProperty, jsonData, projectData, atlasData, listener);
    }

    public DialogFonts(final Skin skin, String styleName, StyleProperty styleProperty, JsonData jsonData, ProjectData projectData, AtlasData atlasData, EventListener listener) {
        super("", skin, styleName);
        
        this.jsonData = jsonData;
        this.projectData = projectData;
        this.atlasData = atlasData;
        
        this.listener = listener;
        this.skin = skin;
        this.styleProperty = styleProperty;
        fonts = jsonData.getFonts();
        drawables = atlasData.getDrawables();

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
        
        Main.instance().getDesktopWorker().addFilesDroppedListener(filesDroppedListener);

        setFillParent(true);

        if (styleProperty != null) {
            getContentTable().add(new Label("Select a Font...", skin, "title"));
            getContentTable().row();
        } else {
            getContentTable().add(new Label("Fonts", skin, "title"));
            getContentTable().row();
        }

        Table table = new Table();
        table.defaults().pad(2.0f);

        table.add(new Label("Sort by: ", skin)).padLeft(20.0f);
        selectBox = new SelectBox<>(skin);
        selectBox.setItems(new String[]{"A-Z", "Z-A", "Oldest", "Newest"});
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                sortBySelectedMode();
            }
        });
        table.add(selectBox);

        TextButton imageButton = new TextButton("New Font", skin, "new");
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                newFontDialog();
            }
        });
        table.add(imageButton).expandX();
        getContentTable().add(table).expandX().left();
        getContentTable().row();

        key(Keys.ESCAPE, false);
        if (styleProperty != null) {
            button("Clear Font", true);
            button("Cancel", false);
        } else {
            button("Close", false);
        }
        
        getButtonTable().padBottom(15.0f);

        fontsTable = new Table();

        table = new Table();
        table.add(fontsTable).pad(5.0f);
        scrollPane = new ScrollPane(table, skin);
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
                projectData.setChangesSaved(false);
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
                        atlasData.atlasCurrent = false;
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
                populate();
            } catch (Exception e) {
                Gdx.app.error(getClass().getName(), "Error creating font from file", e);
                DialogError.showError("Font Error...", "Error creating font from file. Check file paths.\n\nOpen log?");
            }
            return true;
        } else {
            return false;
        }
    }

    public void populate() {
        fontsTable.clear();
        fontsTable.defaults().growX().pad(5.0f);

        if (fonts.size == 0) {
            fontsTable.add(new Label("No fonts have been set!", skin));
        } else {
            for (FontData font : fonts) {
                Button button = new Button(skin, "color-base");
                Label label = new Label(font.getName(), skin);
                label.setTouchable(Touchable.disabled);
                button.add(label).left();
                
                Button renameButton = new Button(skin, "settings-small");
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
                LabelStyle style = new LabelStyle();
                style.font = fontMap.get(font);
                style.fontColor = Color.WHITE;
                label = new Label("Lorem Ipsum", style);
                label.setAlignment(Align.center);
                label.setTouchable(Touchable.disabled);
                Table bg = new Table(skin);
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

                Button closeButton = new Button(skin, "delete-small");
                final FontData deleteFont = font;
                closeButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        fonts.removeValue(deleteFont, true);
                        BitmapFontData bitmapFontData = new BitmapFontData(deleteFont.file, false);
                        for (String path : bitmapFontData.imagePaths) {
                            FileHandle imagefile = new FileHandle(path);
                            drawables.removeValue(new DrawableData(imagefile), false);
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
                        
                        Main.instance().getUndoableManager().clearUndoables();
                        
                        PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
                        PanelPreviewProperties.instance.render();
                        
                        event.setBubbles(false);
                        populate();
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

                if (styleProperty == null) {
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
        }
    }
    
    private void renameDialog(FontData font) {
        TextField textField = new TextField("", skin);
        TextButton okButton;
        
        Dialog dialog = new Dialog("Rename Font?", skin, "bg") {
            @Override
            protected void result(Object object) {
                if ((boolean) object) {
                    renameFont(font, textField.getText());
                }
            }

            @Override
            public Dialog show(Stage stage) {
                Dialog dialog = super.show(stage);
                Main.instance().getStage().setKeyboardFocus(textField);
                return dialog;
            }
        };
        dialog.getContentTable().padLeft(10.0f).padRight(10.0f);
        
        dialog.getTitleTable().padLeft(5.0f);
        
        Table bg = new  Table(skin);
        bg.setBackground("white");
        bg.setColor(Color.WHITE);
        dialog.getContentTable().add(bg);
        
        Label label = new Label(font.getName(), skin, "white");
        label.setColor(Color.BLACK);
        bg.add(label).pad(10);
        
        dialog.getContentTable().row();
        label = new Label("What do you want to rename the font to?", skin);
        dialog.getContentTable().add(label);
        
        dialog.getContentTable().row();
        textField.setText(font.getName());
        textField.selectAll();
        dialog.getContentTable().add(textField);
        
        dialog.button("OK", true);
        dialog.button("Cancel", false).key(Keys.ESCAPE, false);
        okButton = (TextButton) dialog.getButtonTable().getCells().first().getActor();
        okButton.setDisabled(true);
        
        dialog.getButtonTable().padBottom(15.0f);
        
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                boolean disable = !FontData.validate(textField.getText());
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
                        renameFont(font, textField.getText());
                        dialog.hide();
                    }
                }
            }
        });
        
        dialog.show(getStage());
    }
    
    private void renameFont(FontData font, String newName) {
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
            DialogError.showError("Rename Font Error...", "Error trying to rename a font.\n\nOpen log?");
        }

        Main.instance().getUndoableManager().clearUndoables();

        PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
        PanelPreviewProperties.instance.render();
        
        projectData.setChangesSaved(false);
        
        populate();
    }

    @Override
    protected void result(Object object) {
        if (styleProperty != null) {
            if (object instanceof FontData) {
                projectData.setChangesSaved(false);
                FontData font = (FontData) object;
                PanelStatusBar.instance.message("Selected Font: " + font.getName() + " for \"" + styleProperty.name + "\"");
                styleProperty.value = font.getName();
                PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
            } else if (object instanceof Boolean) {
                if ((boolean) object) {
                    styleProperty.value = null;
                    projectData.setChangesSaved(false);
                    PanelStatusBar.instance.message("Drawable emptied for \"" + styleProperty.name + "\"");
                    PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
                } else {
                    boolean hasFont = false;
                    for (FontData font : jsonData.getFonts()) {
                        if (font.getName().equals(styleProperty.value)) {
                            hasFont = true;
                            break;
                        }
                    }

                    if (!hasFont) {
                        styleProperty.value = null;
                        projectData.setChangesSaved(false);
                        PanelStatusBar.instance.message("Drawable deleted for \"" + styleProperty.name + "\"");
                        PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
                    }
                }
            }
        }
        
        if (listener != null) {
            listener.handle(null);
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
        populate();
    }

    private void sortFontsZA() {
        Sort.instance().sort(fonts, (FontData o1, FontData o2) -> o1.toString().compareToIgnoreCase(o2.toString()) * -1);
        populate();
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

        populate();
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
        populate();
    }

    @Override
    public boolean remove() {
        Main.instance().getDesktopWorker().removeFilesDroppedListener(filesDroppedListener);
        
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
            
            if (!atlasData.atlasCurrent) {
                atlasData.writeAtlas();
                atlasData.atlasCurrent = true;
            }
            atlas = atlasData.getAtlas();

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
            DialogError.showError("Drawables Error...", "Error while attempting to generate drawables. Open log?");
            return false;
        }
    }
    
    private void newFontDialog() {
        String defaultPath = "";
        
        if (projectData.getLastDirectory() != null) {
            FileHandle fileHandle = new FileHandle(defaultPath);
            if (fileHandle.exists()) {
                defaultPath = projectData.getLastDirectory();
            }
        }
        
        String[] filterPatterns = {"*.fnt"};
        
        List<File> files = Main.instance().getDesktopWorker().openMultipleDialog("Choose font file(s)...", defaultPath, filterPatterns, "Font files (*.fnt)");
        if (files != null && files.size() > 0) {
            projectData.setLastDirectory(files.get(0).getParentFile().getPath());
            fontNameDialog(files, 0);
        }
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

                final TextField textField = new TextField(FontData.filter(fileHandle.nameWithoutExtension()), skin);
                final Dialog nameDialog = new Dialog("Enter a name...", skin, "bg") {
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
                        Main.instance().getStage().setKeyboardFocus(textField1);
                    }
                });
                
                textField.addListener(IbeamListener.get());
                
                nameDialog.getContentTable().defaults().padLeft(10.0f).padRight(10.0f).padTop(5.0f);
                nameDialog.text("Please enter a name for the new font: ");
                nameDialog.getContentTable().row();
                nameDialog.getContentTable().add(textField).growX();
                nameDialog.getContentTable().row();
                nameDialog.text("Preview:");
                nameDialog.getContentTable().row();

                LabelStyle previewStyle = new LabelStyle();
                previewStyle.font = new BitmapFont(fileHandle);
                Table table = new Table(skin);
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
                            for (FontData data : jsonData.getFonts()) {
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
                
                if (!Utils.doesImageFitBox(new FileHandle(bitmapFontData.imagePaths[0]), projectData.getMaxTextureWidth(), projectData.getMaxTextureHeight())) {
                    showAddFontSizeError(fileHandle.nameWithoutExtension());
                } else {
                    nameDialog.show(getStage());
                    getStage().setKeyboardFocus(textField);
                    textField.selectAll();
                }
            } catch (Exception e) {
                Gdx.app.error(getClass().getName(), "Error creating preview font from file", e);
                DialogError.showError("Preview Error...", "Error creating preview font from file. Check file paths.\n\nOpen log?");
            }
        }
    }
    
    private void showAddFontSizeError(String name) {
        Dialog dialog = new Dialog("", skin, "dialog");
        
        Label label = new Label("Error adding font...", skin, "title");
        dialog.getContentTable().add(label);
        
        dialog.getContentTable().row();
        dialog.text("Unable to add font \"" + name +
                "\". Ensure image dimensions\nare less than max texture dimensions (" +
                projectData.getMaxTextureWidth() + "x" + 
                projectData.getMaxTextureHeight() + ").\nSee project settings.");
        dialog.button("Ok");
        dialog.show(getStage());
    }
}
