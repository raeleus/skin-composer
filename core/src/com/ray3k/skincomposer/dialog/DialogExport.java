/*
 * The MIT License
 *
 * Copyright (c) 2024 Raymond Buckley.
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
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.data.DrawableData.DrawableType;
import com.ray3k.skincomposer.data.FontData;
import com.ray3k.skincomposer.data.FreeTypeFontData;
import com.ray3k.skincomposer.data.JsonData.ExportFormat;
import com.ray3k.skincomposer.utils.Utils;

import java.nio.file.Paths;

import static com.ray3k.skincomposer.Main.*;

/**
 *
 * @author Raymond
 */
public class DialogExport extends Dialog {
    public DialogExport() {
        super("Export skin...", skin, "bg");
        
        populate();
    }
    
    public void populate() {
        getTitleTable().padLeft(5);
        getContentTable().pad(15.0f);
        
        getContentTable().defaults().left().space(10);
        var table = new Table();
        getContentTable().add(table).padBottom(10);
        
        table.defaults().space(5);
        var label = new Label("Export Path:", skin);
        table.add(label);
        
        var textField = new TextField("", skin);
        textField.setName("path");
        textField.setText(projectData.getLastImportExportPath());
        textField.setCursorPosition(Math.max(0, textField.getText().length() - 1));
        table.add(textField).minWidth(400);
        textField.addListener(ibeamListener);
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                TextButton textButton = findActor("export");
                textButton.setDisabled(!checkPath());
            }
        });
        
        var textButton = new TextButton("Browse...", skin);
        table.add(textButton);
        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                showFileBrowser();
            }
        });
        
        getContentTable().row();
        var textureAtlasCheckBox = new CheckBox("Generate texture atlas", skin);
        textureAtlasCheckBox.setChecked(projectData.isExportingAtlas());
        getContentTable().add(textureAtlasCheckBox);
        textureAtlasCheckBox.addListener(handListener);
        textureAtlasCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                projectData.setExportingAtlas(textureAtlasCheckBox.isChecked());
            }
        });
        
        getContentTable().row();
        var fontCheckBox = new CheckBox("Copy font files to destination", skin);
        fontCheckBox.setChecked(projectData.isExportingFonts());
        getContentTable().add(fontCheckBox);
        fontCheckBox.addListener(handListener);
        fontCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                projectData.setExportingFonts(fontCheckBox.isChecked());
            }
        });
    
        getContentTable().row();
        var tvgCheckBox = new CheckBox("Copy TVG files to destination", skin);
        tvgCheckBox.setChecked(projectData.isExportingTVG());
        getContentTable().add(tvgCheckBox);
        tvgCheckBox.addListener(handListener);
        tvgCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                projectData.setExportingTVG(tvgCheckBox.isChecked());
            }
        });
        
        getContentTable().row();
        var simpleNamesCheckBox = new CheckBox("Export with simple names", skin);
        simpleNamesCheckBox.setChecked(projectData.isUsingSimpleNames());
        getContentTable().add(simpleNamesCheckBox);
        simpleNamesCheckBox.addListener(handListener);
        simpleNamesCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                projectData.setUsingSimpleNames(simpleNamesCheckBox.isChecked());
            }
        });
        
        getContentTable().row();
        var hexCheckBox = new CheckBox("Export colors as hexadecimal", skin);
        hexCheckBox.setChecked(projectData.isExportingHex());
        getContentTable().add(hexCheckBox);
        hexCheckBox.addListener(handListener);
        hexCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                projectData.setExportingHex(hexCheckBox.isChecked());
            }
        });
        
        getContentTable().row();
        table = new Table();
        getContentTable().add(table);
        
        table.defaults().space(5);
        label = new Label("Exported JSON Format:", skin);
        table.add(label);
        
        var selectBox = new SelectBox<ExportFormat>(skin);
        selectBox.setItems(ExportFormat.MINIMAL, ExportFormat.JAVASCRIPT, ExportFormat.JSON);
        selectBox.setSelected(projectData.getExportFormat());
        table.add(selectBox);
        selectBox.addListener(handListener);
        selectBox.getList().addListener(handListener);
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                projectData.setExportFormat(selectBox.getSelected());
            }
        });
        
        getButtonTable().add().expandX();
        getButtonTable().pad(5.0f);
        
        getButtonTable().defaults().minWidth(75);
        textButton = new TextButton("Export", skin);
        textButton.setName("export");
        textButton.setDisabled(!checkPath());
        button(textButton, true);
        textButton.addListener(handListener);
        
        textButton = new TextButton("Cancel", skin);
        button(textButton, false);
        textButton.addListener(handListener);
        
        key(Keys.ENTER, true).key(Keys.NUMPAD_ENTER, true).key(Keys.ESCAPE, false);
    }
    
    private void showFileBrowser() {
        TextField textField  = findActor("path");
        var file = desktopWorker.saveDialog("Export skin...", textField.getText(), "json", "Json files");
        if (file != null) {
            var fileHandle = new FileHandle(file);
            if (fileHandle.extension().equals("")) {
                fileHandle = fileHandle.sibling(fileHandle.nameWithoutExtension() + ".json");
            }
            textField.setText(fileHandle.path());
            textField.setCursorPosition(Math.max(0, textField.getText().length() - 1));
            TextButton textButton = findActor("export");
            textButton.setDisabled(!checkPath());
        }
    }

    @Override
    protected void result(Object object) {
        if ((boolean) object) {
            TextButton textButton = findActor("export");
            if (textButton.isDisabled()) {
                cancel();
            } else {
                TextField textField = findActor("path");
                var fileHandle = Gdx.files.absolute(textField.getText());
                projectData.setLastImportExportPath(fileHandle.path());
                
                writeFile(fileHandle);
            }
        }
        super.result(object);
    }
    
    private void writeFile(FileHandle fileHandle) {
        dialogFactory.showDialogLoading(() -> {
            Gdx.app.postRunnable(() -> {
                Array<String> warnings = new Array<>();

                Array<String> newWarnings = projectData.getJsonData().writeFile(fileHandle);
                warnings.addAll(newWarnings);

                if (projectData.isExportingAtlas()) {
                    try {
                        newWarnings = projectData.getAtlasData().writeAtlas(fileHandle.parent().child(fileHandle.nameWithoutExtension() + ".atlas"), Main.appFolder.child("texturepacker/atlas-export-settings.json"));
                        warnings.addAll(newWarnings);
                    } catch (Exception ex) {
                        Gdx.app.error(getClass().getName(), "Error while writing texture atlas", ex);
                        dialogFactory.showDialogError("Atlas Error...", "Error while writing texture atlas.\n\nOpen log?");
                    }
                }

                if (projectData.isExportingFonts()) {
                    for (FontData font : projectData.getJsonData().getFonts()) {
                        if (!font.file.parent().equals(fileHandle.parent())) {
                            font.file.copyTo(fileHandle.parent());
                        }
                    }

                    for (FreeTypeFontData font : projectData.getJsonData().getFreeTypeFonts()) {
                        if (font.useCustomSerializer && !font.file.parent().equals(fileHandle.parent())) {
                            font.file.copyTo(fileHandle.parent());
                        }
                    }
                }
    
                if (projectData.isExportingTVG()) {
                    for (DrawableData drawableData : projectData.getAtlasData().getDrawables()) {
                        if (drawableData.type == DrawableType.TVG && !drawableData.file.parent().equals(fileHandle.parent())) {
                            drawableData.file.copyTo(fileHandle.parent());
                        }
                    }
                }

                if (warnings.size > 0 && projectData.isShowingExportWarnings()) {
                    dialogFactory.showWarningDialog(warnings);
                }
            });
        });
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
    
    private boolean checkPath() {
        TextField textField = findActor("path");
        try {
            var path = Paths.get(textField.getText());
            var fileHandle = Gdx.files.absolute(path.toString());
            return !fileHandle.isDirectory();
        } catch (Exception e) {
            return false;
        }
    }
}
