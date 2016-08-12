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
package com.ray3k.skincomposer.panel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.contrib.widget.file.ImgScalrFileChooserIconProvider;
import com.kotcrab.vis.ui.widget.file.FileChooser.Mode;
import com.kotcrab.vis.ui.widget.file.FileChooser.ViewMode;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.kotcrab.vis.ui.widget.file.FileTypeFilter;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.MenuList;
import com.ray3k.skincomposer.data.AtlasData;
import com.ray3k.skincomposer.data.FontData;
import com.ray3k.skincomposer.data.JsonData;
import com.ray3k.skincomposer.data.ProjectData;
import com.ray3k.skincomposer.utils.SynchronousJFXFileChooser;
import com.ray3k.skincomposer.utils.Utils;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.stage.FileChooser;

public class PanelMenuBar {
    private Skin skin;
    private Stage stage;
    private TextButton undoButton, redoButton;
    private static PanelMenuBar instance;
    private com.kotcrab.vis.ui.widget.file.FileChooser fileChooser;
    
    public PanelMenuBar(final Table table, final Skin skin, final Stage stage) {
        instance = this;
        
        if (!Utils.isWindows()) {
           fileChooser = new com.kotcrab.vis.ui.widget.file.FileChooser(Mode.OPEN);
           fileChooser.setIconProvider(new ImgScalrFileChooserIconProvider(fileChooser));
        }
        
        this.skin = skin;
        this.stage = stage;
        final Array<TextButton> menuButtons = new Array<>();
        
        table.defaults().padTop(1.0f).padBottom(1.0f);
        table.setBackground("dark-orange");
        
        TextButton textButton = new TextButton("File", skin, "menu");
        Table menuItemTable = new Table();
        menuItemTable.defaults().growX();
        
        TextButton menuItemTextButton = new TextButton("New", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.add(new Label(getShortcutNames().get("new"), skin, "shortcut")).padLeft(5.0f);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                newDialog();
            }
        });
        menuItemTable.add(menuItemTextButton);
        
        menuItemTable.row();
        menuItemTextButton = new TextButton("Open...", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.add(new Label(getShortcutNames().get("open"), skin, "shortcut")).padLeft(5.0f);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                openDialog();
            }
        });
        menuItemTable.add(menuItemTextButton);
        
        menuItemTable.row();
        menuItemTextButton = new TextButton("Save", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.add(new Label(getShortcutNames().get("save"), skin, "shortcut")).padLeft(5.0f);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                save(null);
            }
        });
        menuItemTable.add(menuItemTextButton);
        
        menuItemTable.row();
        menuItemTextButton = new TextButton("Save As...", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.add(new Label(getShortcutNames().get("save as"), skin, "shortcut")).padLeft(5.0f);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                saveAsDialog(null);
            }
        });
        menuItemTable.add(menuItemTextButton);
        
        menuItemTable.row();
        menuItemTextButton = new TextButton("Import...", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                importDialog();
            }
        });
        menuItemTable.add(menuItemTextButton);
        
        menuItemTable.row();
        menuItemTextButton = new TextButton("Export...", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                exportDialog();
            }
        });
        menuItemTable.add(menuItemTextButton);
        
        menuItemTable.row();
        menuItemTextButton = new TextButton("Exit", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Main.instance.showCloseDialog();
            }
        });
        menuItemTable.add(menuItemTextButton);
        
        final MenuList menuList1 = new MenuList(textButton, menuItemTable);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if (((TextButton) actor).isChecked()) {
                    menuList1.show(stage);
                } else {
                    menuList1.hide();
                }
            }
        });
        table.add(textButton).padLeft(1.0f);
        menuButtons.add(textButton);
        
        textButton = new TextButton("Edit", skin, "menu");
        menuItemTable = new Table();
        menuItemTable.defaults().growX();
        undoButton = new TextButton("Undo", skin, "menu-item");
        undoButton.getLabel().setAlignment(Align.left);
        undoButton.add(new Label(getShortcutNames().get("undo"), skin, "shortcut")).padLeft(5.0f);
        undoButton.setDisabled(true);
        undoButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Main.instance.undo();
            }
        });
        menuItemTable.add(undoButton);
        
        menuItemTable.row();
        redoButton = new TextButton("Redo", skin, "menu-item");
        redoButton.getLabel().setAlignment(Align.left);
        redoButton.add(new Label(getShortcutNames().get("redo"), skin, "shortcut")).padLeft(5.0f);
        redoButton.setDisabled(true);
        redoButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Main.instance.redo();
            }
        });
        menuItemTable.add(redoButton);
        
        final MenuList menuList2 = new MenuList(textButton, menuItemTable);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if (((TextButton) actor).isChecked()) {
                    menuList2.show(stage);
                } else {
                    menuList2.hide();
                }
            }
        });
        table.add(textButton);
        menuButtons.add(textButton);
        
        textButton = new TextButton("Project", skin, "menu");
        menuItemTable = new Table();
        
        menuItemTable.defaults().growX();
        menuItemTextButton = new TextButton("Settings...", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Main.instance.showDialogSettings();
            }
        });
        menuItemTable.add(menuItemTextButton);
        
        menuItemTable.row();
        menuItemTable.defaults().growX();
        menuItemTextButton = new TextButton("Colors...", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Main.instance.showDialogColors();
            }
        });
        menuItemTable.add(menuItemTextButton);
        
        menuItemTable.row();
        menuItemTextButton = new TextButton("Fonts...", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Main.instance.showDialogFonts();
            }
        });
        menuItemTable.add(menuItemTextButton);
        
        menuItemTable.row();
        menuItemTextButton = new TextButton("Drawables...", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Main.instance.showDialogDrawables();
            }
        });
        menuItemTable.add(menuItemTextButton);
        final MenuList menuList3 = new MenuList(textButton, menuItemTable);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if (((TextButton) actor).isChecked()) {
                    menuList3.show(stage);
                } else {
                    menuList3.hide();
                }
            }
        });
        table.add(textButton);
        menuButtons.add(textButton);
        
        textButton = new TextButton("Help", skin, "menu");
        menuItemTable = new Table();
        menuItemTable.defaults().growX();
        menuItemTextButton = new TextButton("About", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Main.instance.showDialogAbout();
            }
        });
        menuItemTable.add(menuItemTextButton);
        final MenuList menuList4 = new MenuList(textButton, menuItemTable);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if (((TextButton) actor).isChecked()) {
                    menuList4.show(stage);
                } else {
                    menuList4.hide();
                }
            }
        });
        table.add(textButton);
        menuButtons.add(textButton);
        table.add().growX();

        //deselect menu buttons if escape is pressed or if stage is clicked anywhere else
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    for (TextButton button : menuButtons) {
                        button.setChecked(false);
                    }
                }
                return false;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                for (TextButton textButton : menuButtons) {
                    if (!textButton.isAscendantOf(event.getTarget())) {
                        textButton.setChecked(false);
                    }
                }
                return false;
            }
        });
    }

    public static PanelMenuBar instance() {
        return instance;
    }

    public TextButton getUndoButton() {
        return undoButton;
    }

    public TextButton getRedoButton() {
        return redoButton;
    }
    
    public void newDialog() {
        if (!ProjectData.instance().areChangesSaved() && !ProjectData.instance().isNewProject()) {
            yesNoCancelDialog("Save Changes?",
                    "Do you want to save changes to the existing project?"
                            + "\nAll unsaved changes will be lost.",
                    (int selection) -> {
                        if (selection == 0) {
                            save(() -> {
                                ProjectData.instance().clear();
                            });
                        } else if (selection == 1) {
                            ProjectData.instance().clear();
                        }
                    });
        } else {
            ProjectData.instance().clear();
        }
    }
    
    private void yesNoCancelDialog(String title, String text, ConfirmationListener listener) {
        Dialog dialog = new Dialog(title, skin, "dialog") {
            @Override
            protected void result(Object object) {
                listener.selected((int) object);
            }
        };
        Label label = new Label(text, skin);
        label.setAlignment(Align.center);
        dialog.text(label);
        dialog.getContentTable().getCells().first().pad(10.0f);
        dialog.button("Yes", 0);
        dialog.button("No", 1);
        dialog.button("Cancel", 2);
        dialog.key(Keys.ESCAPE, 2);
        dialog.show(stage);
    }
    
    private void yesNoDialog(String title, String text, ConfirmationListener listener) {
        Dialog dialog = new Dialog(title, skin, "dialog") {
            @Override
            protected void result(Object object) {
                listener.selected((int) object);
            }
        };
        Label label = new Label(text, skin);
        label.setAlignment(Align.center);
        dialog.text(label);
        dialog.getContentTable().getCells().first().pad(10.0f);
        dialog.button("Yes", 0);
        dialog.button("No", 1);
        dialog.key(Keys.ESCAPE, 1);
        dialog.show(stage);
    }
    
    private interface ConfirmationListener {
        public void selected(int selection);
    }
    
    public void openDialog() {
        if (Utils.isWindows()) {
            openDialogWindows();
        } else {
            openDialogVisUI();
        }
    }
    
    public void openDialogWindows() {
        Runnable runnable = () -> {
            SynchronousJFXFileChooser chooser = new SynchronousJFXFileChooser(() -> {
                FileChooser ch = new FileChooser();
                FileChooser.ExtensionFilter ex = new FileChooser.ExtensionFilter("Skin Composer files", "*.scmp");
                ch.getExtensionFilters().add(ex);
                ch.setTitle("Open skin file...");
                File file = new File(ProjectData.instance().getBestSaveDirectory());
                if (file.exists()) {
                    ch.setInitialDirectory(file);
                }
                return ch;
            });
            File file = chooser.showOpenDialog();
            if (file != null) {
                FileHandle fileHandle = new FileHandle(file);
                ProjectData.instance().load(fileHandle);
            }
        };
        
        if (!ProjectData.instance().areChangesSaved() && !ProjectData.instance().isNewProject()) {
            yesNoCancelDialog("Save Changes?",
                    "Do you want to save changes to the existing project?"
                    + "\nAll unsaved changes will be lost.",
                    (int selection) -> {
                        if (selection == 0) {
                            save(runnable);
                        } else if (selection == 1) {
                            Main.instance.showDialogLoading(runnable);
                        }
                    });
        } else {
            Main.instance.showDialogLoading(runnable);
        }
    }
    
    public void openDialogVisUI() {
        Runnable runnable = () -> {
            fileChooser.setMode(Mode.OPEN);
            fileChooser.setMultiSelectionEnabled(false);
            FileHandle defaultFile = new FileHandle(ProjectData.instance().getBestSaveDirectory());
            if (defaultFile.exists()) {
                if (defaultFile.isDirectory()) {
                    fileChooser.setDirectory(defaultFile);
                } else {
                    fileChooser.setDirectory(defaultFile.parent());
                }
            }
            FileTypeFilter typeFilter = new FileTypeFilter(false);
            typeFilter.addRule("Skin Composer files (*.scmp)", "scmp");
            fileChooser.setFileTypeFilter(typeFilter);
            fileChooser.setViewMode(ViewMode.DETAILS);
            fileChooser.setViewModeButtonVisible(true);
            fileChooser.setWatchingFilesEnabled(true);
            fileChooser.getTitleLabel().setText("Open skin file...");
            fileChooser.setListener(new FileChooserAdapter() {
                @Override
                public void selected(Array<FileHandle> files) {
                    if (files.size > 0) {
                        ProjectData.instance().load(files.first());
                    }
                }
            });
            stage.addActor(fileChooser.fadeIn());
        };
        
        if (!ProjectData.instance().areChangesSaved() && !ProjectData.instance().isNewProject()) {
            yesNoCancelDialog("Save Changes?",
                    "Do you want to save changes to the existing project?"
                    + "\nAll unsaved changes will be lost.",
                    (int selection) -> {
                        if (selection == 0) {
                            save(runnable);
                        } else if (selection == 1) {
                            runnable.run();
                        }
                    });
        } else {
            runnable.run();
        }
    }
    
    public void save(Runnable runnable) {
        if (ProjectData.instance().getSaveFile() != null) {
            
            Main.instance.showDialogLoading(() -> {
                ProjectData.instance().save();
                if (runnable != null) {
                    runnable.run();
                }
            });
        } else {
            saveAsDialog(runnable);
        }
    }
    
    public void saveAsDialog(Runnable runnable) {
        if (Utils.isWindows()) {
            saveAsDialogWindows(runnable);
        } else {
            saveAsDialogVisUI(runnable);
        }
    }
    
    public void saveAsDialogWindows(Runnable runnable) {
        SynchronousJFXFileChooser chooser = new SynchronousJFXFileChooser(() -> {
            FileChooser ch = new FileChooser();
            FileChooser.ExtensionFilter ex = new FileChooser.ExtensionFilter("Skin Composer files", "*.scmp");
            ch.getExtensionFilters().add(ex);
            ch.setTitle("Save skin file as...");
            File file = new File(ProjectData.instance().getBestSaveDirectory());
            if (file.exists()) {
                ch.setInitialDirectory(file);
            }
            return ch;
        });
        
        Main.instance.showDialogLoading(() -> {
            File file = chooser.showSaveDialog();
            if (file != null) {
                FileHandle fileHandle = new FileHandle(file);
                ProjectData.instance().save(fileHandle);
                if (runnable != null) {
                    runnable.run();
                }
            }
        });
    }
    
    public void saveAsDialogVisUI(Runnable runnable) {
        fileChooser.setMode(Mode.SAVE);
        fileChooser.setMultiSelectionEnabled(false);
        FileHandle defaultFile = new FileHandle(ProjectData.instance().getBestSaveDirectory());
        if (defaultFile.exists()) {
            if (defaultFile.isDirectory()) {
                fileChooser.setDirectory(defaultFile);
            } else {
                fileChooser.setDirectory(defaultFile.parent());
            }
        }
        FileTypeFilter typeFilter = new FileTypeFilter(false);
        typeFilter.addRule("Skin Composer files (*.scmp)", "scmp");
        typeFilter.setAllTypesAllowed(false);
        fileChooser.setFileTypeFilter(typeFilter);
        fileChooser.setViewMode(ViewMode.DETAILS);
        fileChooser.setViewModeButtonVisible(true);
        fileChooser.setWatchingFilesEnabled(true);
        fileChooser.getTitleLabel().setText("Save skin file as...");
        fileChooser.setListener(new FileChooserAdapter() {
            @Override
            public void selected(Array<FileHandle> files) {
                if (files.size > 0) {
                    if (!files.first().extension().equalsIgnoreCase("scmp")) {
                        files.set(0, files.first().sibling(files.first().name() + ".scmp"));
                    }
                    
                    Main.instance.showDialogLoading(() -> {
                        ProjectData.instance().save(files.first());
                        if (runnable != null) {
                            runnable.run();
                        }
                    });
                }
            }
        });
        stage.addActor(fileChooser.fadeIn());
    }
    
    public void importDialog() {
        if (Utils.isWindows()) {
            importDialogWindows();
        } else {
            importDialogVisUI();
        }
    }
    
    public void importDialogWindows() {
        Runnable runnable = () -> {
            SynchronousJFXFileChooser chooser = new SynchronousJFXFileChooser(() -> {
                FileChooser ch = new FileChooser();
                FileChooser.ExtensionFilter ex = new FileChooser.ExtensionFilter("Json files", "*.json");
                ch.getExtensionFilters().add(ex);
                ch.setTitle("Import skin...");
                if (ProjectData.instance().getLastDirectory() != null) {
                    File file = new File(ProjectData.instance().getLastDirectory());
                    if (file.exists()) {
                        ch.setInitialDirectory(file);
                    }
                }
                return ch;
            });
            File file = chooser.showOpenDialog();
            if (file != null) {
                FileHandle fileHandle = new FileHandle(file);
                ProjectData.instance().setLastDirectory(fileHandle.parent().path());
                try {
                    JsonData.getInstance().readFile(fileHandle);
                    PanelClassBar.instance.populate();
                    PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
                    AtlasData.getInstance().atlasCurrent = false;
                    PanelPreviewProperties.instance.produceAtlas();
                    PanelPreviewProperties.instance.populate();
                } catch (Exception e) {
                    Gdx.app.error(getClass().getName(), "Error attempting to import JSON", e);
                }
            }
        };
        
        if (!ProjectData.instance().areChangesSaved()) {
            yesNoDialog("Save Changes?",
                    "The project must be saved before import."
                    + "\nDo you want to save?",
                    (int selection) -> {
                        if (selection == 0) {
                            save(runnable);
                        }
                    });
        } else {
            Main.instance.showDialogLoading(runnable);
        }
    }
    
    public void importDialogVisUI() {
        Runnable runnable = () -> {
            fileChooser.setMode(Mode.OPEN);
            fileChooser.setMultiSelectionEnabled(false);
            FileHandle defaultFile = new FileHandle(ProjectData.instance().getLastDirectory());
            if (defaultFile.exists()) {
                if (defaultFile.isDirectory()) {
                    fileChooser.setDirectory(defaultFile);
                } else {
                    fileChooser.setDirectory(defaultFile.parent());
                }
            }
            FileTypeFilter typeFilter = new FileTypeFilter(false);
            typeFilter.addRule("Json files (*.json)", "json");
            fileChooser.setFileTypeFilter(typeFilter);
            fileChooser.setViewMode(ViewMode.DETAILS);
            fileChooser.setViewModeButtonVisible(true);
            fileChooser.setWatchingFilesEnabled(true);
            fileChooser.getTitleLabel().setText("Import skin...");
            fileChooser.setListener(new FileChooserAdapter() {
                @Override
                public void selected(Array<FileHandle> files) {
                    if (files.size > 0) {
                        ProjectData.instance().setLastDirectory(files.first().toString());
                        try {
                            JsonData.getInstance().readFile(files.first());
                            PanelClassBar.instance.populate();
                            PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
                            AtlasData.getInstance().atlasCurrent = false;
                            PanelPreviewProperties.instance.produceAtlas();
                            PanelPreviewProperties.instance.populate();
                        } catch (Exception e) {
                            Gdx.app.error(getClass().getName(), "Error attempting to import JSON", e);
                        }
                    }
                }
            });
            stage.addActor(fileChooser.fadeIn());
        };
        
        if (!ProjectData.instance().areChangesSaved()) {
            yesNoDialog("Save Changes?",
                    "The project must be saved before import."
                    + "\nDo you want to save?",
                    (int selection) -> {
                        if (selection == 0) {
                            save(runnable);
                        }
                    });
        } else {
            runnable.run();
        }
    }
    
    public void exportDialog() {
        if (Utils.isWindows()) {
            exportDialogWindows();
        } else {
            exportDialogVisUI();
        }
    }
    
    public void exportDialogWindows() {
        SynchronousJFXFileChooser chooser = new SynchronousJFXFileChooser(() -> {
            FileChooser ch = new FileChooser();
            FileChooser.ExtensionFilter ex = new FileChooser.ExtensionFilter("Json files", "*.json");
            ch.getExtensionFilters().add(ex);
            ch.setTitle("Export skin...");
            
            if (ProjectData.instance().getLastDirectory() != null) {
                File file = new File(ProjectData.instance().getLastDirectory());
                if (file.exists()) {
                    ch.setInitialDirectory(file);
                }
            }
            return ch;
        });
        
        Main.instance.showDialogLoading(() -> {
            File file = chooser.showSaveDialog();
            if (file != null) {
                FileHandle fileHandle = new FileHandle(file);
                ProjectData.instance().setLastDirectory(fileHandle.parent().path());
                JsonData.getInstance().writeFile(fileHandle);
                try {
                    AtlasData.getInstance().writeAtlas(fileHandle.parent().child(fileHandle.nameWithoutExtension() + ".atlas"));
                } catch (Exception ex) {
                    Logger.getLogger(PanelMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                }
                for (FontData font : JsonData.getInstance().getFonts()) {
                    font.file.copyTo(fileHandle.parent());
                }
            }
        });
    }
    
    public void exportDialogVisUI() {
        fileChooser.setMode(Mode.SAVE);
        fileChooser.setMultiSelectionEnabled(false);
        FileHandle defaultFile = new FileHandle(ProjectData.instance().getBestSaveDirectory());
        if (defaultFile.exists()) {
            if (defaultFile.isDirectory()) {
                fileChooser.setDirectory(defaultFile);
            } else {
                fileChooser.setDirectory(defaultFile.parent());
            }
        }
        FileTypeFilter typeFilter = new FileTypeFilter(false);
        typeFilter.addRule("Json files (*.json)", "json");
        typeFilter.setAllTypesAllowed(false);
        fileChooser.setFileTypeFilter(typeFilter);
        fileChooser.setViewMode(ViewMode.DETAILS);
        fileChooser.setViewModeButtonVisible(true);
        fileChooser.setWatchingFilesEnabled(true);
        fileChooser.getTitleLabel().setText("Export skin...");
        fileChooser.setListener(new FileChooserAdapter() {
            @Override
            public void selected(Array<FileHandle> files) {
                if (files.size > 0) {
                    if (!files.first().extension().equalsIgnoreCase("json")) {
                        files.set(0, files.first().sibling(files.first().name() + ".json"));
                    }
                    
                    Main.instance.showDialogLoading(() -> {
                        FileHandle fileHandle = files.first();
                        ProjectData.instance().setLastDirectory(fileHandle.parent().path());
                        JsonData.getInstance().writeFile(fileHandle);
                        try {
                            AtlasData.getInstance().writeAtlas(fileHandle.parent().child(fileHandle.nameWithoutExtension() + ".atlas"));
                        } catch (Exception ex) {
                            Logger.getLogger(PanelMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        for (FontData font : JsonData.getInstance().getFonts()) {
                            font.file.copyTo(fileHandle.parent());
                        }
                    });
                }
            }
        });
        stage.addActor(fileChooser.fadeIn());
    }
    
    private static ObjectMap<String, String> shortcutNames;
    
    private static ObjectMap<String, String> getShortcutNames() {
        if (shortcutNames == null) {
            shortcutNames = new ObjectMap();
            
            if (Utils.isMac()) {
                shortcutNames.put("new", "⌘+N");
                shortcutNames.put("open", "⌘+O");
                shortcutNames.put("save", "⌘+S");
                shortcutNames.put("save as", "Shift+⌘+S");
                shortcutNames.put("undo", "⌘+Z");
                shortcutNames.put("redo", "⌘+Y");
            } else {
                shortcutNames.put("new", "Ctrl+N");
                shortcutNames.put("open", "Ctrl+O");
                shortcutNames.put("save", "Ctrl+S");
                shortcutNames.put("save as", "Shift+Ctrl+S");
                shortcutNames.put("undo", "Ctrl+Z");
                shortcutNames.put("redo", "Ctrl+Y");
            }
        }
        
        return shortcutNames;
    }

    public com.kotcrab.vis.ui.widget.file.FileChooser getFileChooser() {
        return fileChooser;
    }
}
