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
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.Main;
import java.nio.file.Paths;

/**
 *
 * @author Raymond
 */
public class DialogImport extends Dialog {
    private Main main;
    private enum Result {
        NEW, CURRENT, CANCEL
    }
    
    public DialogImport(Main main) {
        super("Import...", main.getSkin());
        this.main = main;
        
        populate();
    }
    
    private void populate() {
        getTitleTable().padLeft(5);
        
        var t = getContentTable();
        t.pad(15);
        
        var table = new Table();
        t.add(table);
        
        table.defaults().space(5);
        var label = new Label("Import Path:", getSkin());
        table.add(label);
        
        var textField = new TextField("", getSkin());
        textField.setName("path");
        textField.setText(main.getProjectData().getLastImportExportPath());
        textField.setCursorPosition(Math.max(0, textField.getText().length() - 1));
        table.add(textField).minWidth(400);
        textField.addListener(main.getIbeamListener());
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                TextButton textButton = findActor("new");
                textButton.setDisabled(!checkPath());
                
                textButton = findActor("current");
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
        
        t = getButtonTable();
        t.pad(5.0f);
        
        t.add().expandX();
        
        t.defaults().minWidth(75);
        textButton = new TextButton("Import to New Project", main.getSkin());
        textButton.setName("new");
        textButton.setDisabled(!checkPath());
        button(textButton, Result.NEW);
        textButton.addListener(main.getHandListener());
        
        textButton = new TextButton("Import into Current Project", main.getSkin());
        textButton.setName("current");
        textButton.setDisabled(!checkPath());
        button(textButton, Result.CURRENT);
        textButton.addListener(main.getHandListener());
        
        textButton = new TextButton("Cancel", main.getSkin());
        button(textButton, Result.CANCEL);
        textButton.addListener(main.getHandListener());
        
        key(Keys.ENTER, Result.CURRENT).key(Keys.ESCAPE, Result.CANCEL);
    }

    @Override
    protected void result(Object object) {
        TextButton textButton = findActor("new");
        
        switch ((Result) object) {
            case NEW:
                if (textButton.isDisabled()) {
                    cancel();
                } else {
                    TextField textField = findActor("path");
                    var fileHandle = Gdx.files.absolute(textField.getText());
                    main.getProjectData().setLastImportExportPath(fileHandle.path());

                    newFile(fileHandle);
                }
                break;
            case CURRENT:
                if (textButton.isDisabled()) {
                    cancel();
                } else {
                    TextField textField = findActor("path");
                    var fileHandle = Gdx.files.absolute(textField.getText());
                    main.getProjectData().setLastImportExportPath(fileHandle.path());

                    importFile(fileHandle);
                }
                break;
        }
        super.result(object);
    }
    
    private void newFile(FileHandle fileHandle) {
        if (!main.getProjectData().areChangesSaved() && !main.getProjectData().isNewProject()) {
            main.getDialogFactory().yesNoCancelDialog("Save Changes?",
                    "Do you want to save changes to the existing project?"
                            + "\nAll unsaved changes will be lost.",
                    (int selection) -> {
                        if (selection == 0) {
                            main.getMainListener().saveFile(() -> {
                                main.getProjectData().clear();
                                importFile(fileHandle);
                            });
                        } else if (selection == 1) {
                            main.getProjectData().clear();
                            importFile(fileHandle);
                        }
                    }, null);
        } else {
            main.getProjectData().clear();
            importFile(fileHandle);
        }
    }
    
    private void importFile(FileHandle fileHandle) {
        main.getDialogFactory().showDialogLoading(() -> {
            Array<String> warnings = new Array<>();

            try {
                Array<String> newWarnings = main.getJsonData().readFile(fileHandle);
                warnings.addAll(newWarnings);
                main.getProjectData().getAtlasData().atlasCurrent = false;
                main.getJsonData().checkForPropertyConsistency();
                main.getRootTable().produceAtlas();
                main.getRootTable().populate();
            } catch (Exception e) {
                Gdx.app.error(getClass().getName(), "Error attempting to import JSON", e);
                main.getDialogFactory().showDialogError("Import Error...", "Error while attempting to import a skin.\nPlease check that all files exist.\n\nOpen log?");
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
    
    private void showFileBrowser() {
        String[] filterPatterns = {"*.json"};

        TextField textField  = findActor("path");
        var file = main.getDesktopWorker().openDialog("Import skin...", textField.getText(), filterPatterns, "Json files");
        if (file != null) {
            var fileHandle = new FileHandle(file);
            
            textField.setText(fileHandle.path());
            textField.setCursorPosition(Math.max(0, textField.getText().length() - 1));
            
            TextButton textButton = findActor("new");
            textButton.setDisabled(!checkPath());

            textButton = findActor("current");
            textButton.setDisabled(!checkPath());
        }
    }
    
    private boolean checkPath() {
        TextField textField = findActor("path");
        try {
            var path = Paths.get(textField.getText());
            var fileHandle = Gdx.files.absolute(path.toString());
            return !fileHandle.isDirectory() && fileHandle.exists();
        } catch (Exception e) {
            return false;
        }
    }
}
