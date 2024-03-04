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
package com.ray3k.skincomposer;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.RootTable.RootTableListener;
import com.ray3k.skincomposer.UndoableManager.*;
import com.ray3k.skincomposer.data.*;
import com.ray3k.skincomposer.data.CustomProperty.PropertyType;
import com.ray3k.skincomposer.dialog.DialogCustomClass;
import com.ray3k.skincomposer.dialog.DialogCustomClass.CustomClassListener;
import com.ray3k.skincomposer.dialog.DialogCustomProperty.CustomStylePropertyListener;
import com.ray3k.skincomposer.dialog.DialogCustomStyle;
import com.ray3k.skincomposer.dialog.DialogFactory;
import com.ray3k.skincomposer.dialog.DialogListener;
import com.ray3k.skincomposer.dialog.PopWelcome.WelcomeListener;
import com.ray3k.stripe.DraggableSelectBox;
import com.ray3k.stripe.DraggableTextList;
import com.ray3k.stripe.Spinner;
import com.ray3k.skincomposer.utils.Utils;

import static com.ray3k.skincomposer.Main.*;

import java.io.File;
import java.util.Locale;

public class MainListener extends RootTableListener {
    private WelcomeDialogListener welcomeListener;
    private DialogListener dialogListener;
    
    public MainListener() {
        dialogListener = new DialogListener() {
            @Override
            public void opened() {
                desktopWorker.removeFilesDroppedListener(rootTable.getFilesDroppedListener());
            }

            @Override
            public void closed() {
                desktopWorker.addFilesDroppedListener(rootTable.getFilesDroppedListener());
            }
        };
    }
    
    public void createWelcomeListener() {
        welcomeListener = new WelcomeDialogListener();
        if (projectData.isAllowingWelcome()) {
            dialogFactory.showDialogWelcome(welcomeListener, dialogListener);
        }
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
            case SAVE:
                saveFile(null);
                break;
            case SAVE_AS:
                saveAsFile(null);
                break;
            case WELCOME:
                dialogFactory.showDialogWelcome(getWelcomeListener(), dialogListener);
                break;
            case IMPORT:
                dialogFactory.showDialogImport(dialogListener);
                break;
            case EXPORT:
                dialogFactory.showDialogExport(dialogListener);
                break;
            case EXIT:
                dialogFactory.showCloseDialog(dialogListener);
                break;
            case UNDO:
                undoableManager.undo();
                break;
            case REDO:
                undoableManager.redo();
                break;
            case SETTINGS:
                dialogFactory.showDialogSettings(dialogListener);
                break;
            case COLORS:
                dialogFactory.showDialogColors(dialogListener);
                break;
            case FONTS:
                dialogFactory.showFonts(dialogListener);
                break;
            case DRAWABLES:
                dialogFactory.showDialogDrawables(dialogListener);
                break;
            case ABOUT:
                dialogFactory.showDialogAbout(dialogListener);
                break;
            case CLASS_SELECTED:
                updateStyleProperties();
                break;
            case NEW_CLASS:
                dialogFactory.showNewClassDialog(new DialogCustomClass.CustomClassListener() {
                    @Override
                    public void newClassEntered(String fullyQualifiedName,
                            String displayName, boolean declareAfterUIclasses) {
                        undoableManager.addUndoable(
                                new NewCustomClassUndoable(fullyQualifiedName, displayName, declareAfterUIclasses, main), true);
                    }

                    @Override
                    public void cancelled() {
                        
                    }
                });
                break;
            case DUPLICATE_CLASS:
                dialogFactory.showDuplicateClassDialog(new CustomClassListener() {
                    @Override
                    public void newClassEntered(String fullyQualifiedName,
                            String displayName, boolean declareAfterUIclasses) {
                        undoableManager.addUndoable(new DuplicateCustomClassUndoable(main, displayName, fullyQualifiedName, declareAfterUIclasses), true);
                    }

                    @Override
                    public void cancelled() {
                        
                    }
                });
                break;
            case DELETE_CLASS:
                undoableManager.addUndoable(new DeleteCustomClassUndoable(main), true);
                break;
            case RENAME_CLASS:
                dialogFactory.showRenameClassDialog(new DialogCustomClass.CustomClassListener() {
                    @Override
                    public void newClassEntered(String fullyQualifiedName,
                            String displayName, boolean declareAfterUIclasses) {
                        undoableManager.addUndoable(new UndoableManager.RenameCustomClassUndoable(main, displayName, fullyQualifiedName, declareAfterUIclasses), true);
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
                if (rootTable.getClassSelectBox().getSelectedIndex() < Main.BASIC_CLASSES.length) {
                    dialogFactory.showNewStyleDialog(skin, stage);
                } else {
                    dialogFactory.showNewCustomStyleDialog(new DialogCustomStyle.CustomStyleListener() {
                        @Override
                        public void newStyleEntered(String name) {
                            undoableManager.addUndoable(
                                    new UndoableManager.NewCustomStyleUndoable(main,
                                            name,
                                            (CustomClass) rootTable.getClassSelectBox().getSelected()), true);
                        }

                        @Override
                        public void cancelled() {
                        }
                    });
                }
                break;
            case DUPLICATE_STYLE:
                if (rootTable.getClassSelectBox().getSelectedIndex() < Main.BASIC_CLASSES.length) {
                    dialogFactory.showDuplicateStyleDialog(skin, stage);
                } else {
                    dialogFactory.showDuplicateCustomStyleDialog(new DialogCustomStyle.CustomStyleListener() {
                        @Override
                        public void newStyleEntered(String name) {
                            undoableManager.addUndoable(
                                    new UndoableManager.DuplicateCustomStyleUndoable(main,
                                            name,
                                            rootTable.getSelectedCustomStyle()), true);
                        }

                        @Override
                        public void cancelled() {
                        }
                    });
                }
                break;
            case DELETE_STYLE:
                if (rootTable.getClassSelectBox().getSelectedIndex() < Main.BASIC_CLASSES.length) {
                    dialogFactory.showDeleteStyleDialog(skin, stage);
                } else {
                    undoableManager.addUndoable(new UndoableManager.DeleteCustomStyleUndoable(main, rootTable.getSelectedCustomStyle()), true);
                }
                break;
            case RENAME_STYLE:
                if (rootTable.getClassSelectBox().getSelectedIndex() < Main.BASIC_CLASSES.length) {
                    dialogFactory.showRenameStyleDialog(skin, stage);
                } else {
                    dialogFactory.showRenameCustomStyleDialog(new DialogCustomStyle.CustomStyleListener() {
                        @Override
                        public void newStyleEntered(String name) {
                            undoableManager.addUndoable(
                                    new UndoableManager.RenameCustomStyleUndoable(main,
                                            name,
                                            rootTable.getSelectedCustomStyle()), true);
                        }

                        @Override
                        public void cancelled() {
                        }
                    });
                }
                break;
            case PREVIEW_PROPERTY:
                break;
            case REFRESH_ATLAS:
                refreshTextureAtlas();
                break;
            case SCENE_COMPOSER:
                showSceneComposer();
                break;
            case TEXTRA_TYPIST:
                showTextraTypist();
                break;
            case DOWNLOAD_UPDATE:
                downloadUpdate();
                break;
            case CHECK_FOR_UPDATES_COMPLETE:
                rootTable.findActor("downloadButton").setVisible(!Main.VERSION.equals(Main.newVersion));
                break;
        }
    }
    
    public void newFile() {
        if (!projectData.areChangesSaved() && !projectData.isNewProject()) {
            var dialog = dialogFactory.yesNoCancelDialog("Save Changes?",
                    "Do you want to save changes to the existing project?"
                            + "\nAll unsaved changes will be lost.",
                    (int selection) -> {
                        if (selection == 0) {
                            saveFile(() -> {
                                Gdx.app.postRunnable(() -> {
                                    projectData.clear();
                                });
                            });
                        } else if (selection == 1) {
                            projectData.clear();
                        }
                    }, dialogListener);
            dialog.addListener(new DialogListener() {
                @Override
                public void opened() {
                    desktopWorker.removeFilesDroppedListener(rootTable.getFilesDroppedListener());
                }

                @Override
                public void closed() {
                    desktopWorker.addFilesDroppedListener(rootTable.getFilesDroppedListener());
                }
            });
        } else {
            projectData.clear();
        }
    }
    
    public void openFile() {
        Runnable runnable = () -> {
            String defaultPath = projectData.getLastOpenSavePath();

            File file = desktopWorker.openDialog("Open Skin Composer file...", defaultPath, "scmp", "Skin Composer files");
            if (file != null) {
                Gdx.app.postRunnable(() -> {
                    FileHandle fileHandle = new FileHandle(file);
                    projectData.load(fileHandle);
                    Array<DrawableData> drawableErrors = projectData.verifyDrawablePaths();
                    Array<FontData> fontErrors = projectData.verifyFontPaths();
                    var freeTypeFontErrors = projectData.verifyFreeTypeFontPaths();
                    if (drawableErrors.size > 0 || fontErrors.size > 0 || freeTypeFontErrors.size > 0) {
                        dialogFactory.showDialogPathErrors(drawableErrors, fontErrors, freeTypeFontErrors);
                    }
                    
                    if (projectData.checkForInvalidMinWidthHeight()) {
                        projectData.setLoadedVersion(Main.VERSION);
                        dialogFactory.yesNoDialog("Fix minWidth and minHeight errors?", "Old project (< v.30) detected.\nResolve minWidth and minHeight errors?", new DialogFactory.ConfirmationListener() {
                            @Override
                            public void selected(int selection) {
                                if (selection == 0) {
                                    projectData.fixInvalidMinWidthHeight();
                                    refreshTextureAtlas();
                                }
                            }
                        }, null);
                    }
                    
                    projectData.setLastOpenSavePath(fileHandle.parent().path() + "/");
                    rootTable.populate();
                    rootTable.updateRecentFiles();
                });
            }
        };
        
        if (!projectData.areChangesSaved() && !projectData.isNewProject()) {
            var dialog = dialogFactory.yesNoCancelDialog("Save Changes?",
                    "Do you want to save changes to the existing project?"
                    + "\nAll unsaved changes will be lost.",
                    (int selection) -> {
                        if (selection == 0) {
                            saveFile(runnable);
                        } else if (selection == 1) {
                            dialogFactory.showDialogLoading(runnable);
                        }
                    }, dialogListener);
            
            dialog.addListener(new DialogListener() {
                @Override
                public void opened() {
                    desktopWorker.removeFilesDroppedListener(rootTable.getFilesDroppedListener());
                }

                @Override
                public void closed() {
                    desktopWorker.addFilesDroppedListener(rootTable.getFilesDroppedListener());
                }
            });
        } else {
            dialogFactory.showDialogLoading(runnable);
        }
    }
    
    public void openFile(FileHandle fileHandle) {
        Runnable runnable = () -> {
            if (fileHandle != null) {
                Gdx.app.postRunnable(() -> {
                    projectData.load(fileHandle);
                    Array<DrawableData> drawableErrors = projectData.verifyDrawablePaths();
                    Array<FontData> fontErrors = projectData.verifyFontPaths();
                    var freeTypeFontErrors = projectData.verifyFreeTypeFontPaths();
                    if (drawableErrors.size > 0 || fontErrors.size > 0 || freeTypeFontErrors.size > 0) {
                        dialogFactory.showDialogPathErrors(drawableErrors, fontErrors, freeTypeFontErrors);
                    }
    
                    if (projectData.checkForInvalidMinWidthHeight()) {
                        projectData.setLoadedVersion(Main.VERSION);
                        dialogFactory.yesNoDialog("Fix minWidth and minHeight errors?", "Old project (< v.30) detected.\nResolve minWidth and minHeight errors?", new DialogFactory.ConfirmationListener() {
                            @Override
                            public void selected(int selection) {
                                if (selection == 0) {
                                    projectData.fixInvalidMinWidthHeight();
                                    refreshTextureAtlas();
                                }
                            }
                        }, null);
                    }
                    
                    projectData.setLastOpenSavePath(fileHandle.parent().path() + "/");
                    rootTable.populate();
                    rootTable.updateRecentFiles();
                });
            }
        };
        
        if (!projectData.areChangesSaved() && !projectData.isNewProject()) {
            var dialog = dialogFactory.yesNoCancelDialog("Save Changes?",
                    "Do you want to save changes to the existing project?"
                    + "\nAll unsaved changes will be lost.",
                    (int selection) -> {
                        if (selection == 0) {
                            saveFile(runnable);
                        } else if (selection == 1) {
                            dialogFactory.showDialogLoading(runnable);
                        }
                    }, dialogListener);
            
            dialog.addListener(new DialogListener() {
                @Override
                public void opened() {
                    desktopWorker.removeFilesDroppedListener(rootTable.getFilesDroppedListener());
                }

                @Override
                public void closed() {
                    desktopWorker.addFilesDroppedListener(rootTable.getFilesDroppedListener());
                }
            });
        } else {
            dialogFactory.showDialogLoading(runnable);
        }
    }
    
    public void saveFile(Runnable runnable) {
        if (projectData.getSaveFile() != null && projectData.getSaveFile().type() != Files.FileType.Local) {
            
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

            File file = desktopWorker.saveDialog("Save Skin Composer file as...", defaultPath, "scmp", "Skin Composer files");
            if (file != null) {
                Gdx.app.postRunnable(() -> {
                    FileHandle fileHandle = new FileHandle(file);
                    if (fileHandle.extension() == null || !fileHandle.extension().equals(".scmp")) {
                        fileHandle = fileHandle.sibling(fileHandle.nameWithoutExtension() + ".scmp");
                    }
                    projectData.save(fileHandle);
                    projectData.setLastOpenSavePath(fileHandle.parent().path() + "/");
                    if (runnable != null) {
                        runnable.run();
                    }
                });
            }
        });
    }
    
    @Override
    public void loadClasses(SelectBox classSelectBox) {
        Array names = new Array<>();
        for (Class clazz : Main.BASIC_CLASSES) {
            names.add(clazz.getSimpleName());
        }
        for (CustomClass customClass : jsonData.getCustomClasses()) {
            names.add(customClass);
        }
        classSelectBox.setItems(names);
    }

    @Override
    public void loadStyles(SelectBox classSelectBox, DraggableSelectBox styleBox) {
        int selection = classSelectBox.getSelectedIndex();
        if (selection < Main.BASIC_CLASSES.length) { 
            Class selectedClass = Main.BASIC_CLASSES[selection];
            styleBox.clearItems();
            for (var style : jsonData.getClassStyleMap().get(selectedClass)) {
                styleBox.addItem(style.toString());
            }
            styleBox.setSelected(0);
        } else {
            CustomClass customClass = (CustomClass) classSelectBox.getSelected();
            styleBox.clearItems();
            for (var style : customClass.getStyles()) {
                styleBox.addItem(style.toString());
            }
        }
    }
    
    @Override
    public void reorderStyles(Class widgetClass, int indexBefore, int indexAfter) {
        undoableManager.addUndoable(new ReorderStylesUndoable(widgetClass, indexBefore, indexAfter), true);
    }
    
    @Override
    public void reorderCustomStyles(CustomClass customClass, int indexBefore, int indexAfter) {
        undoableManager.addUndoable(new ReorderCustomStylesUndoable(customClass, indexBefore, indexAfter), true);
    }
    
    @Override
    public void stylePropertyChanged(StyleProperty styleProperty,
            Actor styleActor) {
        if (styleProperty.type == Drawable.class) {
            dialogFactory.showDialogDrawables(styleProperty, dialogListener);
        } else if (styleProperty.type == Color.class) {
            dialogFactory.showDialogColors(styleProperty, dialogListener);
        } else if (styleProperty.type == BitmapFont.class) {
            dialogFactory.showDialogFonts(styleProperty, dialogListener);
        } else if (styleProperty.type == Float.TYPE) {
            undoableManager.addUndoable(new UndoableManager.DoubleUndoable(main, styleProperty, ((Spinner) styleActor).getValue()), false);
        } else if (styleProperty.type == ScrollPaneStyle.class) {
            undoableManager.addUndoable(new UndoableManager.SelectBoxUndoable(rootTable, styleProperty, (SelectBox) styleActor), true);
        } else if (styleProperty.type == LabelStyle.class) {
            undoableManager.addUndoable(new UndoableManager.SelectBoxUndoable(rootTable, styleProperty, (SelectBox) styleActor), true);
        } else if (styleProperty.type == ListStyle.class) {
            undoableManager.addUndoable(new UndoableManager.SelectBoxUndoable(rootTable, styleProperty, (SelectBox) styleActor), true);
        }
    }

    @Override
    public void styleParentChanged(StyleData style, SelectBox<String> selectBox) {
        undoableManager.addUndoable(new UndoableManager.ParentUndoable(rootTable, style, selectBox), true);
    }
    
    private void updateStyleProperties() {
        int classIndex = rootTable.getClassSelectBox().getSelectedIndex();
        if (classIndex >= 0 && classIndex < Main.BASIC_CLASSES.length) {
            rootTable.setClassDuplicateButtonDisabled(true);
            rootTable.setClassDeleteButtonDisabled(true);
            rootTable.setClassRenameButtonDisabled(true);

            Array<StyleData> styleDatas = jsonData.getClassStyleMap().get(Main.BASIC_CLASSES[classIndex]);
            
            int styleIndex = rootTable.getStyleSelectBox().getSelectedIndex();
            if (styleIndex >= 0 && styleIndex < styleDatas.size) {
                StyleData styleData = styleDatas.get(styleIndex);

                rootTable.setStyleDeleteButtonDisabled(!styleData.deletable);
                rootTable.setStyleRenameButtonDisabled(!styleData.deletable);
                
                rootTable.refreshStyleProperties(false);
                rootTable.refreshPreviewProperties();
                rootTable.refreshPreview();
            }
        } else {
            rootTable.setClassDuplicateButtonDisabled(false);
            rootTable.setClassDeleteButtonDisabled(false);
            rootTable.setClassRenameButtonDisabled(false);
            
            var customStyle = rootTable.getSelectedCustomStyle();
            
            if (customStyle != null) {
                rootTable.setStyleDeleteButtonDisabled(!customStyle.isDeletable());
                rootTable.setStyleRenameButtonDisabled(!customStyle.isDeletable());
                
                rootTable.refreshStyleProperties(false);
                rootTable.refreshPreviewProperties();
                rootTable.refreshPreview();
            }
        }
    }

    @Override
    public void newCustomProperty() {
        dialogFactory.showNewStylePropertyDialog(new CustomStylePropertyListener() {
            @Override
            public void newPropertyEntered(String propertyName, PropertyType propertyType) {
                undoableManager.addUndoable(new UndoableManager.NewCustomPropertyUndoable(main, propertyName, propertyType), true);
            }

            @Override
            public void cancelled() {
            }
        });
    }

    @Override
    public void duplicateCustomProperty(CustomProperty customProperty) {
        dialogFactory.showDuplicateStylePropertyDialog(customProperty.getName(), customProperty.getType(), new CustomStylePropertyListener() {
            @Override
            public void newPropertyEntered(String propertyName, PropertyType propertyType) {
                undoableManager.addUndoable(new UndoableManager.DuplicateCustomPropertyUndoable(customProperty, propertyName, propertyType), true);
            }

            @Override
            public void cancelled() {
            }
        });
    }

    @Override
    public void deleteCustomProperty(CustomProperty customProperty) {
        undoableManager.addUndoable(new UndoableManager.DeleteCustomPropertyUndoable(main, customProperty), true);
    }

    @Override
    public void renameCustomProperty(CustomProperty customProperty) {
        dialogFactory.showRenameStylePropertyDialog(customProperty.getName(), customProperty.getType(), new CustomStylePropertyListener() {
            @Override
            public void newPropertyEntered(String propertyName, PropertyType propertyType) {
                undoableManager.addUndoable(new UndoableManager.RenameCustomPropertyUndoable(main, customProperty, propertyName, propertyType), true);
            }

            @Override
            public void cancelled() {
            }
        });
    }

    @Override
    public void customPropertyValueChanged(CustomProperty customProperty,
            Actor styleActor) {
        if (null != customProperty.getType()) switch (customProperty.getType()) {
            case DRAWABLE:
                dialogFactory.showDialogDrawables(customProperty, dialogListener);
                break;
            case COLOR:
                dialogFactory.showDialogColors(customProperty, dialogListener);
                break;
            case FONT:
                dialogFactory.showDialogFonts(customProperty, dialogListener);
                break;
            case NUMBER:
                undoableManager.addUndoable(new UndoableManager.CustomDoubleUndoable(main, customProperty, ((Spinner) styleActor).getValue()), false);
                break;
            case TEXT:
            case RAW_TEXT:
                undoableManager.addUndoable(new UndoableManager.CustomTextUndoable(main, customProperty, ((TextField) styleActor).getText()), false);
                break;
            case BOOL:
                undoableManager.addUndoable(new UndoableManager.CustomBoolUndoable(main, customProperty, ((Button) styleActor).isChecked()), false);
                break;
            case STYLE:
                dialogFactory.showDialogCustomStyleSelection(customProperty, dialogListener);
                break;
            default:
                break;
        }  
    }

    @Override
    public void droppedScmpFile(FileHandle fileHandle) {
        openFile(fileHandle);
    }
    
    public void refreshTextureAtlas() {
        dialogFactory.showDialogLoading(() -> {
            Gdx.app.postRunnable(() -> {
                try {
                    FileHandle defaultsFile = Main.appFolder.child("texturepacker/atlas-internal-settings.json");
                    projectData.getAtlasData().writeAtlas(defaultsFile);
                    projectData.getAtlasData().atlasCurrent = true;
                    atlasData.produceAtlas();
                    rootTable.refreshPreview();
                } catch (Exception e) {
                    dialogFactory.showDialogError("Error", "Unable to write texture atlas to temporary storage!", null);
                    Gdx.app.error(getClass().getName(), "Unable to write texture atlas to temporary storage!", e);
                    dialogFactory.showDialogError("Atlas Error...", "Unable to write texture atlas to temporary storage.\n\nOpen log?");
                }
            });
        });
    }
    
    public void showSceneComposer() {
        dialogFactory.showSceneComposerDialog();
    }
    
    public void showTextraTypist() {
        dialogFactory.showTextraTypist();
    }
    
    public void downloadUpdate() {
        dialogFactory.showDialogUpdate(skin, stage);
    }
    
    public WelcomeListener getWelcomeListener() {
        return welcomeListener;
    }

    public boolean argumentsPassed(String[] args) {
        var validArgument = false;
        if (args != null && args.length > 0) {
            var fileHandle = Gdx.files.absolute(args[0]);
            if (fileHandle.exists() && fileHandle.extension().toLowerCase(Locale.ROOT).equals("scmp")) {
                validArgument = true;
                openFile(fileHandle);
            }
        }
        
        return validArgument;
    }
    
    private class WelcomeDialogListener extends WelcomeListener {
        @Override
        public void videoClicked() {
            Gdx.net.openURI("https://www.youtube.com/watch?v=78amAV0_e24&list=PLl-_-0fPSXFfHiRAFpmLCuQup10MUJwcA");
        }

        @Override
        public void blankClicked() {
            newFile();
        }

        @Override
        public void visUIclicked() {
            Gdx.files.internal("templates/vis-ui.scmp").copyTo(Main.appFolder.child("temp/"));
            openFile(Main.appFolder.child("temp/vis-ui.scmp"));
        }

        @Override
        public void plainJamesClicked() {
            Gdx.files.internal("templates/plain-james-ui.zip").copyTo(Main.appFolder.child("temp/"));
            try {
                Utils.unzip(Main.appFolder.child("temp/plain-james-ui.zip"), Main.appFolder.child("temp/"));
                openFile(Main.appFolder.child("temp/plain-james-ui.scmp"));
            } catch (Exception e) {
                Gdx.app.error(getClass().getName(), "Error attempting to open template Plain-James.", e);
                dialogFactory.showDialogError("Template Error...", "Error attempting to open template Plain-James.\n\nOpen log?");
            }
        }

        @Override
        public void neonClicked() {
            Gdx.files.internal("templates/neon-ui.zip").copyTo(Main.appFolder.child("temp/"));
            try {
                Utils.unzip(Main.appFolder.child("temp/neon-ui.zip"), Main.appFolder.child("temp/"));
                openFile(Main.appFolder.child("temp/neon-ui.scmp"));
            } catch (Exception e) {
                Gdx.app.error(getClass().getName(), "Error attempting to open template Neon.", e);
                dialogFactory.showDialogError("Template Error...", "Error attempting to open template Neon.\n\nOpen log?");
            }
        }

        @Override
        public void neutralizerClicked() {
            Gdx.files.internal("templates/neutralizer-ui.zip").copyTo(Main.appFolder.child("temp/"));
            try {
                Utils.unzip(Main.appFolder.child("temp/neutralizer-ui.zip"), Main.appFolder.child("temp/"));
                openFile(Main.appFolder.child("temp/neutralizer-ui.scmp"));
            } catch (Exception e) {
                Gdx.app.error(getClass().getName(), "Error attempting to open template Neutralizer.", e);
                dialogFactory.showDialogError("Template Error...", "Error attempting to open template Neutralizer.\n\nOpen log?");
            }
        }

        @Override
        public void dontShowClicked(boolean value) {
            projectData.setAllowingWelcome(!value);
        }
    }
}
