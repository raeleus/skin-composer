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
import com.ray3k.skincomposer.FilesDroppedListener;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.utils.Utils;

import java.nio.file.Paths;
import java.util.Locale;

import static com.ray3k.skincomposer.Main.*;

/**
 *
 * @author Raymond
 */
public class DialogImport extends Dialog {
    private enum Result {
        NEW, CURRENT, CANCEL
    }
    private FilesDroppedListener filesDroppedListener;
    
    public DialogImport() {
        super("Import...", skin, "bg");
    
        filesDroppedListener = (Array<FileHandle> files) -> {
            if (files.size > 0 && files.first().name().toLowerCase(Locale.ROOT).endsWith(".json")) {
                Runnable runnable = () -> {
                    Gdx.app.postRunnable(() -> {
                        TextField textField  = findActor("path");
                        textField.setText(files.first().path());
                        textField.setCursorPosition(Math.max(0, textField.getText().length() - 1));
    
                        TextButton textButton = findActor("new");
                        textButton.setDisabled(!checkPath());
    
                        textButton = findActor("current");
                        textButton.setDisabled(!checkPath());
                    });
                };
            
                dialogFactory.showDialogLoading(runnable);
            }
        };
        
        populate();
    }
    
    private void populate() {
        desktopWorker.addFilesDroppedListener(filesDroppedListener);
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
        textField.setText(projectData.getLastImportExportPath());
        textField.setCursorPosition(Math.max(0, textField.getText().length() - 1));
        table.add(textField).minWidth(400);
        textField.addListener(ibeamListener);
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                TextButton textButton = findActor("new");
                textButton.setDisabled(!checkPath());
                
                textButton = findActor("current");
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
        
        t = getButtonTable();
        t.pad(5.0f);
        
        t.add().expandX();
        
        t.defaults().minWidth(75);
        textButton = new TextButton("Import to New Project", skin);
        textButton.setName("new");
        textButton.setDisabled(!checkPath());
        button(textButton, Result.NEW);
        textButton.addListener(handListener);
        
        textButton = new TextButton("Import into Current Project", skin);
        textButton.setName("current");
        textButton.setDisabled(!checkPath());
        button(textButton, Result.CURRENT);
        textButton.addListener(handListener);
        
        textButton = new TextButton("Cancel", skin);
        button(textButton, Result.CANCEL);
        textButton.addListener(handListener);
        
        key(Keys.ENTER, Result.CURRENT).key(Keys.NUMPAD_ENTER, Result.CURRENT).key(Keys.ESCAPE, Result.CANCEL);
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
                    projectData.setLastImportExportPath(fileHandle.path());

                    newFile(fileHandle);
                }
                break;
            case CURRENT:
                if (textButton.isDisabled()) {
                    cancel();
                } else {
                    TextField textField = findActor("path");
                    var fileHandle = Gdx.files.absolute(textField.getText());
                    projectData.setLastImportExportPath(fileHandle.path());

                    importFile(fileHandle);
                }
                break;
        }
        super.result(object);
    }
    
    private void newFile(FileHandle fileHandle) {
        if (!projectData.areChangesSaved() && !projectData.isNewProject()) {
            dialogFactory.yesNoCancelDialog("Save Changes?",
                    "Do you want to save changes to the existing project?"
                            + "\nAll unsaved changes will be lost.",
                    (int selection) -> {
                        if (selection == 0) {
                            mainListener.saveFile(() -> {
                                Gdx.app.postRunnable(() -> {
                                    projectData.clear();
                                    importFile(fileHandle);
                                });
                            });
                        } else if (selection == 1) {
                            projectData.clear();
                            importFile(fileHandle);
                        }
                    }, null);
        } else {
            projectData.clear();
            importFile(fileHandle);
        }
    }
    
    private void importFile(FileHandle fileHandle) {
        dialogFactory.showDialogLoading(() -> {
            Gdx.app.postRunnable(() -> {
                Array<String> warnings = new Array<>();

                try {
                    Array<String> newWarnings = jsonData.readFile(fileHandle);
                    warnings.addAll(newWarnings);
                    projectData.getAtlasData().atlasCurrent = false;
                    jsonData.checkForPropertyConsistency();
                    atlasData.produceAtlas();
                    rootTable.populate();
                } catch (Exception e) {
                    Gdx.app.error(getClass().getName(), "Error attempting to import JSON", e);
                    dialogFactory.showDialogError("Import Error...", "Error while attempting to import a skin.\nPlease check that all files exist.\n\nOpen log?");
                }

                if (warnings.size > 0) {
                    dialogFactory.showWarningDialog(false, warnings);
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
        desktopWorker.removeFilesDroppedListener(filesDroppedListener);
        return super.remove();
    }
    
    private void showFileBrowser() {
        TextField textField  = findActor("path");
        var file = desktopWorker.openDialog("Import skin...", textField.getText(), "json", "Json files");
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
