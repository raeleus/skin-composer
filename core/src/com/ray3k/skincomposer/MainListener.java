/*******************************************************************************
 * MIT License
 * 
 * Copyright (c) 2017 Raymond Buckley
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

import com.ray3k.skincomposer.data.CustomStyle;
import com.ray3k.skincomposer.dialog.DialogFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.RootTable.RootTableListener;
import com.ray3k.skincomposer.data.FontData;
import com.ray3k.skincomposer.data.JsonData;
import com.ray3k.skincomposer.data.ProjectData;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.data.StyleProperty;
import com.ray3k.skincomposer.dialog.DialogCustomClass;
import com.ray3k.skincomposer.dialog.DialogCustomProperty;
import com.ray3k.skincomposer.dialog.DialogCustomProperty.CustomStylePropertyListener;
import com.ray3k.skincomposer.dialog.DialogCustomProperty.PropertyType;
import java.io.File;

public class MainListener extends RootTableListener {
    private final RootTable root;
    private final DialogFactory dialogFactory;
    private final DesktopWorker desktopWorker;
    private final ProjectData projectData;
    private final JsonData jsonData;
    private final Main main;
    
    public MainListener(Main main) {
        this.root = main.getRootTable();
        this.dialogFactory = main.getDialogFactory();
        this.desktopWorker = main.getDesktopWorker();
        this.projectData = main.getProjectData();
        this.jsonData = main.getProjectData().getJsonData();
        this.main = main;
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
                main.getUndoableManager().undo();
                break;
            case REDO:
                main.getUndoableManager().redo();
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
                updateStyleProperties();
                break;
            case NEW_CLASS:
                dialogFactory.showNewClassDialog(new DialogCustomClass.CustomClassListener() {
                    @Override
                    public void newClassEntered(String fullyQualifiedName,
                            String displayName) {
                        newClass(fullyQualifiedName, displayName);
                    }

                    @Override
                    public void cancelled() {
                        
                    }
                });
                break;
            case DUPLICATE_CLASS:
                dialogFactory.showDuplicateClassDialog("test.class", "test", new DialogCustomClass.CustomClassListener() {
                    @Override
                    public void newClassEntered(String fullyQualifiedName,
                            String displayName) {
                        duplicateClass(fullyQualifiedName, displayName);
                    }

                    @Override
                    public void cancelled() {
                        
                    }
                });
                break;
            case DELETE_CLASS:
                deleteClass();
                break;
            case RENAME_CLASS:
                dialogFactory.showRenameClassDialog("test.class", "test", new DialogCustomClass.CustomClassListener() {
                    @Override
                    public void newClassEntered(String fullyQualifiedName,
                            String displayName) {
                        renameClass(fullyQualifiedName, displayName);
                    }

                    @Override
                    public void cancelled() {
                        
                    }
                });
                break;
            case STYLE_SELECTED:
                updateStyleProperties();
                break;
            case NEW_STYLE:
                dialogFactory.showNewStyleDialog(main.getSkin(), main.getStage());
                break;
            case DUPLICATE_STYLE:
                dialogFactory.showDuplicateStyleDialog(main.getSkin(), main.getStage());
                break;
            case DELETE_STYLE:
                dialogFactory.showDeleteStyleDialog(main.getSkin(), main.getStage());
                break;
            case RENAME_STYLE:
                dialogFactory.showRenameStyleDialog(main.getSkin(), main.getStage());
                break;
            case NEW_STYLE_PROPERTY:
                dialogFactory.showNewStylePropertyDialog(new CustomStylePropertyListener() {
                    @Override
                    public void newPropertyEntered(String propertyName, PropertyType propertyType) {
                        newProperty(propertyName, propertyType);
                    }

                    @Override
                    public void cancelled() {
                    }
                });
                break;
            case DUPLICATE_STYLE_PROPERTY:
                dialogFactory.showDuplicateStylePropertyDialog("test", PropertyType.TEXT, new CustomStylePropertyListener() {
                    @Override
                    public void newPropertyEntered(String propertyName, PropertyType propertyType) {
                        duplicateProperty(propertyName, propertyType);
                    }

                    @Override
                    public void cancelled() {
                    }
                });
                break;
            case DELETE_STYLE_PROPERTY:
                deleteProperty();
                break;
            case RENAME_STYLE_PROPERTY:
                dialogFactory.showRenameStylePropertyDialog("test", PropertyType.TEXT, new CustomStylePropertyListener() {
                    @Override
                    public void newPropertyEntered(String propertyName, PropertyType propertyType) {
                        renameProperty(propertyName, propertyType);
                    }

                    @Override
                    public void cancelled() {
                    }
                });
                break;
            case PREVIEW_PROPERTY:
                break;
        }
    }
    
    public void newFile() {
        if (!projectData.areChangesSaved() && !projectData.isNewProject()) {
            dialogFactory.yesNoCancelDialog("Save Changes?",
                    "Do you want to save changes to the existing project?"
                            + "\nAll unsaved changes will be lost.",
                    (int selection) -> {
                        if (selection == 0) {
                            saveFile(() -> {
                                projectData.clear();
                            });
                        } else if (selection == 1) {
                            projectData.clear();
                        }
                    });
        } else {
            projectData.clear();
        }
    }
    
    public void openFile() {
        Runnable runnable = () -> {
            String defaultPath = projectData.getLastOpenSavePath();

            String[] filterPatterns = {"*.scmp"};

            File file = desktopWorker.openDialog("Open skin file...", defaultPath, filterPatterns, "Skin Composer files");
            if (file != null) {
                FileHandle fileHandle = new FileHandle(file);
                projectData.load(fileHandle);
                projectData.setLastOpenSavePath(fileHandle.parent().path() + "/");
                root.populate();
                root.setRecentFilesDisabled(projectData.getRecentFiles().size == 0);
            }
        };
        
        if (!projectData.areChangesSaved() && !projectData.isNewProject()) {
            dialogFactory.yesNoCancelDialog("Save Changes?",
                    "Do you want to save changes to the existing project?"
                    + "\nAll unsaved changes will be lost.",
                    (int selection) -> {
                        if (selection == 0) {
                            saveFile(runnable);
                        } else if (selection == 1) {
                            dialogFactory.showDialogLoading(runnable);
                        }
                    });
        } else {
            dialogFactory.showDialogLoading(runnable);
        }
    }
    
    public void saveFile(Runnable runnable) {
        if (projectData.getSaveFile() != null) {
            
            dialogFactory.showDialogLoading(() -> {
                projectData.save();
                if (runnable != null) {
                    runnable.run();
                }
            });
        } else {
            saveAsFile(runnable);
        }
    }
    
    public void saveAsFile(Runnable runnable) {
        dialogFactory.showDialogLoading(() -> {
            String defaultPath = projectData.getLastOpenSavePath();

            String[] filterPatterns = {"*.scmp"};

            File file = desktopWorker.saveDialog("Save skin file as...", defaultPath, filterPatterns, "Skin Composer files");
            if (file != null) {
                FileHandle fileHandle = new FileHandle(file);
                if (fileHandle.extension() == null || !fileHandle.extension().equals(".scmp")) {
                    fileHandle = fileHandle.sibling(fileHandle.nameWithoutExtension() + ".scmp");
                }
                projectData.save(fileHandle);
                projectData.setLastOpenSavePath(fileHandle.parent().path() + "/");
                if (runnable != null) {
                    runnable.run();
                }
            }
        });
    }
    
    public void importFile() {
        dialogFactory.showDialogLoading(() -> {
            String defaultPath = projectData.getLastImportExportPath();

            String[] filterPatterns = {"*.json"};

            File file = desktopWorker.openDialog("Import skin...", defaultPath, filterPatterns, "Json files");
            if (file != null) {
                FileHandle fileHandle = new FileHandle(file);
                projectData.setLastImportExportPath(fileHandle.parent().path() + "/");
                try {
                    jsonData.readFile(fileHandle);
                    main.getProjectData().getAtlasData().atlasCurrent = false;
                    main.getRootTable().produceAtlas();
                    main.getRootTable().populate();
                } catch (Exception e) {
                    Gdx.app.error(getClass().getName(), "Error attempting to import JSON", e);
                    dialogFactory.showDialogError("Import Error...", "Error while attempting to import a skin.\nPlease check that all files exist.\n\nOpen log?");
                }
            }
        });
    }
    
    public void exportFile() {
        dialogFactory.showDialogLoading(() -> {
            String defaultPath = projectData.getLastImportExportPath();

            String[] filterPatterns = {"*.json"};

            File file = desktopWorker.saveDialog("Export skin...", defaultPath, filterPatterns, "Json files");
            if (file != null) {
                FileHandle fileHandle = new FileHandle(file);
                if (fileHandle.extension() == null || !fileHandle.extension().equals(".json")) {
                    fileHandle = fileHandle.sibling(fileHandle.nameWithoutExtension() + ".json");
                }
                projectData.setLastImportExportPath(fileHandle.parent().path() + "/");
                main.getProjectData().getJsonData().writeFile(fileHandle);
                
                try {
                    main.getProjectData().getAtlasData().writeAtlas(fileHandle.parent().child(fileHandle.nameWithoutExtension() + ".atlas"));
                } catch (Exception ex) {
                    Gdx.app.error(getClass().getName(), "Error while writing texture atlas", ex);
                    dialogFactory.showDialogError("Atlas Error...", "Error while writing texture atlas.\n\nOpen log?");
                }
                
                for (FontData font : main.getProjectData().getJsonData().getFonts()) {
                    if (!font.file.parent().equals(fileHandle.parent())) {
                        font.file.copyTo(fileHandle.parent());
                    }
                }
            }
        });
    }

    public void newClass(String fullyQualifiedName, String displayName) {
        //create custom class instance.
    }
    
    public void renameClass(String fullyQualifiedname, String displayName) {
        
    }
    
    public void deleteClass() {
        
    }
    
    public void duplicateClass(String fullyQualifiedname, String displayName) {
        
    }
    
    public void newProperty(String propertyName, PropertyType propertyType) {
        //add a new custom property to the template style of the current custom class.
        //go through all styles of custom class and add the custom property to each.
    }
    
    public void renameProperty(String propertyName, PropertyType propertyType) {
        
    }
    
    public void deleteProperty() {
        
    }
    
    public void duplicateProperty(String propertyName, PropertyType propertyType) {
        
    }
    
    @Override
    public void loadClasses(SelectBox<String> classSelectBox) {
        Array<String> names = new Array<>();
        for (Class clazz : Main.BASIC_CLASSES) {
            names.add(clazz.getSimpleName());
        }
        classSelectBox.setItems(names);
    }

    @Override
    public void loadStyles(SelectBox<String> classSelectBox, SelectBox<StyleData> styleSelectBox) {
        Class selectedClass = Main.BASIC_CLASSES[classSelectBox.getSelectedIndex()];
        styleSelectBox.setItems(jsonData.getClassStyleMap().get(selectedClass));
        styleSelectBox.setSelectedIndex(0);
    }

    @Override
    public void stylePropertyChanged(StyleProperty styleProperty,
            Actor styleActor) {
        if (styleProperty.type == Drawable.class) {
            dialogFactory.showDialogDrawables(styleProperty);
        } else if (styleProperty.type == Color.class) {
            dialogFactory.showDialogColors(styleProperty);
        } else if (styleProperty.type == BitmapFont.class) {
            dialogFactory.showDialogFonts(styleProperty);
        } else if (styleProperty.type == Float.TYPE) {
            main.getUndoableManager().addUndoable(new UndoableManager.DoubleUndoable(root, styleProperty, ((Spinner) styleActor).getValue()), true);
        } else if (styleProperty.type == ScrollPaneStyle.class) {
            main.getUndoableManager().addUndoable(new UndoableManager.SelectBoxUndoable(root, styleProperty, (SelectBox) styleActor), true);
        } else if (styleProperty.type == LabelStyle.class) {
            main.getUndoableManager().addUndoable(new UndoableManager.SelectBoxUndoable(root, styleProperty, (SelectBox) styleActor), true);
        } else if (styleProperty.type == ListStyle.class) {
            main.getUndoableManager().addUndoable(new UndoableManager.SelectBoxUndoable(root, styleProperty, (SelectBox) styleActor), true);
        } else if (styleProperty.type == CustomStyle.class) {
            //show custom style dialog
        }
    }
    
    private void updateStyleProperties() {
        int classIndex = root.getClassSelectBox().getSelectedIndex();
        if (classIndex >= 0 && classIndex < Main.BASIC_CLASSES.length) {
            root.setClassDeleteButtonDisabled(true);
            root.setClassRenameButtonDisabled(true);

            Array<StyleData> styleDatas = jsonData.getClassStyleMap().values().toArray().get(classIndex);
            
            int styleIndex = root.getStyleSelectBox().getSelectedIndex();
            if (styleIndex >= 0 && styleIndex < styleDatas.size) {
                StyleData styleData = styleDatas.get(styleIndex);

                root.setStyleDeleteButtonDisabled(!styleData.deletable);
                root.setStyleRenameButtonDisabled(!styleData.deletable);

                root.setStyleProperties(styleData.properties.values().toArray());
                root.refreshStyleProperties(false);
                root.refreshPreviewProperties();
                root.refreshPreview();
            }
        } else {
            root.setClassDeleteButtonDisabled(false);
            root.setClassRenameButtonDisabled(false);
        }
    }
}
