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
package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.*;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.data.FontData;
import com.ray3k.skincomposer.data.FreeTypeFontData;
import com.ray3k.skincomposer.data.JsonData.ExportFormat;
import java.nio.file.Paths;

/**
 *
 * @author Raymond
 */
public class DialogExport extends Dialog {
    private Main main;

    public DialogExport(Main main) {
        super("Export skin...", main.getSkin(), "bg");
        this.main = main;
        
        populate();
    }
    
    public void populate() {
        getTitleTable().padLeft(5);
        getContentTable().pad(15.0f);
        
        getContentTable().defaults().left().space(10);
        var table = new Table();
        getContentTable().add(table).padBottom(10);
        
        table.defaults().space(5);
        var label = new Label("Export Path:", main.getSkin());
        table.add(label);
        
        var textField = new TextField("", main.getSkin());
        textField.setName("path");
        textField.setText(main.getProjectData().getLastImportExportPath());
        textField.setCursorPosition(Math.max(0, textField.getText().length() - 1));
        table.add(textField).minWidth(400);
        textField.addListener(main.getIbeamListener());
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                TextButton textButton = findActor("export");
                textButton.setDisabled(!checkPath());
            }
        });
        
        var textButton = new TextButton("Browse...", main.getSkin());
        table.add(textButton);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                showFileBrowser();
            }
        });
        
        getContentTable().row();
        var textureAtlasCheckBox = new CheckBox("Generate texture atlas", main.getSkin());
        textureAtlasCheckBox.setChecked(main.getProjectData().isExportingAtlas());
        getContentTable().add(textureAtlasCheckBox);
        textureAtlasCheckBox.addListener(main.getHandListener());
        textureAtlasCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                main.getProjectData().setExportingAtlas(textureAtlasCheckBox.isChecked());
            }
        });
        
        getContentTable().row();
        var fontCheckBox = new CheckBox("Copy font files to destination", main.getSkin());
        fontCheckBox.setChecked(main.getProjectData().isExportingFonts());
        getContentTable().add(fontCheckBox);
        fontCheckBox.addListener(main.getHandListener());
        fontCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                main.getProjectData().setExportingFonts(fontCheckBox.isChecked());
            }
        });
        
        getContentTable().row();
        var simpleNamesCheckBox = new CheckBox("Export with simple names", main.getSkin());
        simpleNamesCheckBox.setChecked(main.getProjectData().isUsingSimpleNames());
        getContentTable().add(simpleNamesCheckBox);
        simpleNamesCheckBox.addListener(main.getHandListener());
        simpleNamesCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                main.getProjectData().setUsingSimpleNames(simpleNamesCheckBox.isChecked());
            }
        });
        
        getContentTable().row();
        table = new Table();
        getContentTable().add(table);
        
        table.defaults().space(5);
        label = new Label("Exported JSON Format:", main.getSkin());
        table.add(label);
        
        var selectBox = new SelectBox<ExportFormat>(main.getSkin());
        selectBox.setItems(ExportFormat.MINIMAL, ExportFormat.JAVASCRIPT, ExportFormat.JSON);
        selectBox.setSelected(main.getProjectData().getExportFormat());
        table.add(selectBox);
        selectBox.addListener(main.getHandListener());
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                main.getProjectData().setExportFormat(selectBox.getSelected());
            }
        });
        
        getButtonTable().add().expandX();
        getButtonTable().pad(5.0f);
        
        getButtonTable().defaults().minWidth(75);
        textButton = new TextButton("Export", main.getSkin());
        textButton.setName("export");
        textButton.setDisabled(!checkPath());
        button(textButton, true);
        textButton.addListener(main.getHandListener());
        
        textButton = new TextButton("Cancel", main.getSkin());
        button(textButton, false);
        textButton.addListener(main.getHandListener());
        
        key(Keys.ENTER, true).key(Keys.ESCAPE, false);
    }
    
    private void showFileBrowser() {
        String[] filterPatterns = {"*.json"};

        TextField textField  = findActor("path");
        var file = main.getDesktopWorker().saveDialog("Export skin...", textField.getText(), filterPatterns, "Json files");
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
                main.getProjectData().setLastImportExportPath(fileHandle.path());
                
                writeFile(fileHandle);
            }
        }
        super.result(object);
    }
    
    private void writeFile(FileHandle fileHandle) {
        main.getDialogFactory().showDialogLoading(() -> {
            Array<String> warnings = new Array<>();

            Array<String> newWarnings = main.getProjectData().getJsonData().writeFile(fileHandle);
            warnings.addAll(newWarnings);

            if (main.getProjectData().isExportingAtlas()) {
                try {
                    newWarnings = main.getProjectData().getAtlasData().writeAtlas(fileHandle.parent().child(fileHandle.nameWithoutExtension() + ".atlas"));
                    warnings.addAll(newWarnings);
                } catch (Exception ex) {
                    Gdx.app.error(getClass().getName(), "Error while writing texture atlas", ex);
                    main.getDialogFactory().showDialogError("Atlas Error...", "Error while writing texture atlas.\n\nOpen log?");
                }
            }

            if (main.getProjectData().isExportingFonts()) {
                for (FontData font : main.getProjectData().getJsonData().getFonts()) {
                    if (!font.file.parent().equals(fileHandle.parent())) {
                        font.file.copyTo(fileHandle.parent());
                    }
                }

                for (FreeTypeFontData font : main.getProjectData().getJsonData().getFreeTypeFonts()) {
                    if (font.useCustomSerializer && !font.file.parent().equals(fileHandle.parent())) {
                        font.file.copyTo(fileHandle.parent());
                    }
                }
            }

            if (warnings.size > 0) {
                main.getDialogFactory().showWarningDialog(warnings);
            }
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
