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
package com.ray3k.skincomposer;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.ray3k.skincomposer.RootTable.RootTableListener;
import com.ray3k.skincomposer.data.ProjectData;
import java.io.File;

public class MainListener extends RootTableListener {
    private RootTable root;
    private DialogFactory dialogFactory;
    private DesktopWorker desktopWorker;
    
    public MainListener(RootTable root, DialogFactory dialogFactory, DesktopWorker desktopWorker) {
        this.root = root;
        this.dialogFactory = dialogFactory;
        this.desktopWorker = desktopWorker;
    }
    
    @Override
    public void rootEvent(RootTable.RootTableEvent event) {
        switch (event.rootTableEnum) {
            case NEW:
                newFile();
                break;
            case OPEN:
                openFile();
                break;
            case RECENT_FILES:
                dialogFactory.recentFiles();
                break;
            case SAVE:
                saveFile(null);
                break;
            case SAVE_AS:
                saveAsFile(null);
                break;
            case IMPORT:
                importFile();
                break;
            case EXPORT:
                exportFile();
                break;
            case EXIT:
                dialogFactory.showCloseDialog();
                break;
            case UNDO:
                break;
            case REDO:
                break;
            case SETTINGS:
                dialogFactory.showSettings();
                break;
            case COLORS:
                dialogFactory.showColors();
                break;
            case FONTS:
                dialogFactory.showFonts();
                break;
            case DRAWABLES:
                dialogFactory.showDrawables();
                break;
            case ABOUT:
                dialogFactory.showAbout();
                break;
            case CLASS_SELECTED:
                //set root table's style list
                //set root table's style properties
                //set scrollpanestyles, liststyles, and label styles
                break;
            case NEW_CLASS:
                //set root table's class list
                break;
            case DELETE_CLASS:
                break;
            case STYLE_SELECTED:
                //set root table's style properties
                //set scrollpanestyles, liststyles, and label styles
                break;
            case NEW_STYLE:
                break;
            case DUPLICATE_STYLE:
                break;
            case DELETE_STYLE:
                break;
            case RENAME_STYLE:
                break;
            case STYLE_PROPERTY:
                if (event.styleProperty.type == Drawable.class) {
                    //show drawable dialog
                } else if (event.styleProperty.type == Color.class) {
                    //show color dialog
                } else if (event.styleProperty.type == BitmapFont.class) {
                    //show fonts dialog
                } else if (event.styleProperty.type == Float.TYPE) {
                    //apply value
                } else if (event.styleProperty.type == ScrollPaneStyle.class) {
                    //apply value
                } else if (event.styleProperty.type == LabelStyle.class) {
                    //apply value
                } else if (event.styleProperty.type == ListStyle.class) {
                    //apply value
                } else if (event.styleProperty.type == CustomStyle.class) {
                    //show custom style dialog
                }
                break;
            case PREVIEW_PROPERTY:
                break;
        }
    }
    
    public void newFile() {
        if (!ProjectData.instance().areChangesSaved() && !ProjectData.instance().isNewProject()) {
            dialogFactory.yesNoCancelDialog("Save Changes?",
                    "Do you want to save changes to the existing project?"
                            + "\nAll unsaved changes will be lost.",
                    (int selection) -> {
                        if (selection == 0) {
                            saveFile(() -> {
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
    
    public void openFile() {
        Runnable runnable = () -> {
            String defaultPath = "";

            if (ProjectData.instance().getLastDirectory() != null) {
                FileHandle fileHandle = new FileHandle(defaultPath);
                if (fileHandle.exists()) {
                    defaultPath = ProjectData.instance().getLastDirectory();
                }
            }

            String[] filterPatterns = {"*.scmp"};

            File file = desktopWorker.openDialog("Open skin file...", defaultPath, filterPatterns, "Skin Composer files");
            if (file != null) {
                FileHandle fileHandle = new FileHandle(file);
                ProjectData.instance().load(fileHandle);
            }
        };
        
        if (!ProjectData.instance().areChangesSaved() && !ProjectData.instance().isNewProject()) {
            dialogFactory.yesNoCancelDialog("Save Changes?",
                    "Do you want to save changes to the existing project?"
                    + "\nAll unsaved changes will be lost.",
                    (int selection) -> {
                        if (selection == 0) {
                            saveFile(runnable);
                        } else if (selection == 1) {
//                            Main.instance().showDialogLoading(runnable);
                        }
                    });
        } else {
//            Main.instance().showDialogLoading(runnable);
        }
    }
    
    public void saveFile(Runnable runnable) {
        if (ProjectData.instance().getSaveFile() != null) {
            
//            Main.instance().showDialogLoading(() -> {
//                ProjectData.instance().saveFile();
//                if (runnable != null) {
//                    runnable.run();
//                }
//            });
        } else {
            saveAsFile(runnable);
        }
    }
    
    public void saveAsFile(Runnable runnable) {
//        Main.instance().showDialogLoading(() -> {
//            String defaultPath = "";
//
//            if (ProjectData.instance().getLastDirectory() != null) {
//                FileHandle fileHandle = new FileHandle(defaultPath);
//                if (fileHandle.exists()) {
//                    defaultPath = ProjectData.instance().getLastDirectory();
//                }
//            }
//
//            String[] filterPatterns = {"*.scmp"};
//
//            File file = desktopWorker.saveDialog("Save skin file as...", defaultPath, filterPatterns, "Skin Composer files");
//            if (file != null) {
//                FileHandle fileHandle = new FileHandle(file);
//                if (fileHandle.extension() == null || !fileHandle.extension().equals(".scmp")) {
//                    fileHandle = fileHandle.sibling(fileHandle.nameWithoutExtension() + ".scmp");
//                }
//                ProjectData.instance().saveFile(fileHandle);
//                if (runnable != null) {
//                    runnable.run();
//                }
//            }
//        });
    }
    
    public void importFile() {
//        Main.instance().showDialogLoading(() -> {
//            String defaultPath = "";
//
//            if (ProjectData.instance().getLastDirectory() != null) {
//                FileHandle fileHandle = new FileHandle(defaultPath);
//                if (fileHandle.exists()) {
//                    defaultPath = ProjectData.instance().getLastDirectory();
//                }
//            }
//
//            String[] filterPatterns = {"*.json"};
//
//            File file = desktopWorker.openDialog("Import skin...", defaultPath, filterPatterns, "Json files");
//            if (file != null) {
//                FileHandle fileHandle = new FileHandle(file);
//                ProjectData.instance().setLastDirectory(fileHandle.parent().path());
//                try {
//                    JsonData.getInstance().readFile(fileHandle);
//                    PanelClassBar.instance.populate();
//                    PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
//                    AtlasData.getInstance().atlasCurrent = false;
//                    PanelPreviewProperties.instance.produceAtlas();
//                    PanelPreviewProperties.instance.populate();
//                } catch (Exception e) {
//                    Gdx.app.error(getClass().getName(), "Error attempting to import JSON", e);
//                    DialogError.showError("Import Error...", "Error while attempting to import a skin.\nPlease check that all files exist.\n\nOpen log?");
//                }
//            }
//        });
    }
    
    public void exportFile() {
//        Main.instance().showDialogLoading(() -> {
//            String defaultPath = "";
//
//            if (ProjectData.instance().getLastDirectory() != null) {
//                FileHandle fileHandle = new FileHandle(defaultPath);
//                if (fileHandle.exists()) {
//                    defaultPath = ProjectData.instance().getLastDirectory();
//                }
//            }
//
//            String[] filterPatterns = {"*.json"};
//
//            File file = desktopWorker.saveDialog("Export skin...", defaultPath, filterPatterns, "Json files");
//            if (file != null) {
//                FileHandle fileHandle = new FileHandle(file);
//                if (fileHandle.extension() == null || !fileHandle.extension().equals(".json")) {
//                    fileHandle = fileHandle.sibling(fileHandle.nameWithoutExtension() + ".json");
//                }
//                ProjectData.instance().setLastDirectory(fileHandle.parent().path());
//                JsonData.getInstance().writeFile(fileHandle);
//                
//                try {
//                    AtlasData.getInstance().writeAtlas(fileHandle.parent().child(fileHandle.nameWithoutExtension() + ".atlas"));
//                } catch (Exception ex) {
//                    Gdx.app.error(PanelMenuBar.class.getName(), "Error while writing texture atlas", ex);
//                    DialogError.showError("Atlas Error...", "Error while writing texture atlas.\n\nOpen log?");
//                }
//                
//                for (FontData font : JsonData.getInstance().getFonts()) {
//                    if (!font.file.parent().equals(fileHandle.parent())) {
//                        font.file.copyTo(fileHandle.parent());
//                    }
//                }
//            }
//        });
    }
}
