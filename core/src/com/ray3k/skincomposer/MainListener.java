/*******************************************************************************
 * MIT License
 * 
 * Copyright (c) 2018 Raymond Buckley
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
import com.ray3k.skincomposer.data.CustomStyle;
import com.ray3k.skincomposer.dialog.DialogFactory;
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
import com.ray3k.skincomposer.UndoableManager.DeleteCustomClassUndoable;
import com.ray3k.skincomposer.UndoableManager.DuplicateCustomClassUndoable;
import com.ray3k.skincomposer.data.FontData;
import com.ray3k.skincomposer.data.JsonData;
import com.ray3k.skincomposer.data.ProjectData;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.data.StyleProperty;
import com.ray3k.skincomposer.dialog.DialogCustomClass;
import com.ray3k.skincomposer.dialog.DialogCustomProperty.CustomStylePropertyListener;
import com.ray3k.skincomposer.data.CustomClass;
import com.ray3k.skincomposer.data.CustomProperty;
import com.ray3k.skincomposer.UndoableManager.NewCustomClassUndoable;
import com.ray3k.skincomposer.data.CustomProperty.PropertyType;
import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.data.FreeTypeFontData;
import com.ray3k.skincomposer.dialog.DialogCustomClass.CustomClassListener;
import com.ray3k.skincomposer.dialog.DialogCustomStyle;
import com.ray3k.skincomposer.dialog.DialogWelcome.WelcomeListener;
import com.ray3k.skincomposer.utils.Utils;
import java.io.File;

public class MainListener extends RootTableListener {
    private final RootTable root;
    private final DialogFactory dialogFactory;
    private final DesktopWorker desktopWorker;
    private final ProjectData projectData;
    private final JsonData jsonData;
    private final Main main;
    private WelcomeDialogListener welcomeListener;
    
    public MainListener(Main main) {
        this.root = main.getRootTable();
        this.dialogFactory = main.getDialogFactory();
        this.desktopWorker = main.getDesktopWorker();
        this.projectData = main.getProjectData();
        this.jsonData = main.getProjectData().getJsonData();
        this.main = main;
    }
    
    public void createWelcomeListener() {
        welcomeListener = new WelcomeDialogListener();
        if (main.getProjectData().isAllowingWelcome()) {
            dialogFactory.showWelcomeDialog(welcomeListener);
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
            case RECENT_FILES:
                dialogFactory.recentFiles();
                break;
            case SAVE:
                saveFile(null);
                break;
            case SAVE_AS:
                saveAsFile(null);
                break;
            case WELCOME:
                main.getDialogFactory().showWelcomeDialog(getWelcomeListener());
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
                dialogFactory.showDialogColors();
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
                            String displayName, boolean declareAfterUIclasses) {
                        main.getUndoableManager().addUndoable(
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
                        main.getUndoableManager().addUndoable(new DuplicateCustomClassUndoable(main, displayName, fullyQualifiedName, declareAfterUIclasses), true);
                    }

                    @Override
                    public void cancelled() {
                        
                    }
                });
                break;
            case DELETE_CLASS:
                main.getUndoableManager().addUndoable(new DeleteCustomClassUndoable(main), true);
                break;
            case RENAME_CLASS:
                dialogFactory.showRenameClassDialog(new DialogCustomClass.CustomClassListener() {
                    @Override
                    public void newClassEntered(String fullyQualifiedName,
                            String displayName, boolean declareAfterUIclasses) {
                        main.getUndoableManager().addUndoable(new UndoableManager.RenameCustomClassUndoable(main, displayName, fullyQualifiedName, declareAfterUIclasses), true);
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
                if (main.getRootTable().getClassSelectBox().getSelectedIndex() < Main.BASIC_CLASSES.length) {
                    dialogFactory.showNewStyleDialog(main.getSkin(), main.getStage());
                } else {
                    dialogFactory.showNewCustomStyleDialog(new DialogCustomStyle.CustomStyleListener() {
                        @Override
                        public void newStyleEntered(String name) {
                            main.getUndoableManager().addUndoable(
                                    new UndoableManager.NewCustomStyleUndoable(main,
                                            name,
                                            (CustomClass) main.getRootTable().getClassSelectBox().getSelected()), true);
                        }

                        @Override
                        public void cancelled() {
                        }
                    });
                }
                break;
            case DUPLICATE_STYLE:
                if (main.getRootTable().getClassSelectBox().getSelectedIndex() < Main.BASIC_CLASSES.length) {
                    dialogFactory.showDuplicateStyleDialog(main.getSkin(), main.getStage());
                } else {
                    dialogFactory.showDuplicateCustomStyleDialog(new DialogCustomStyle.CustomStyleListener() {
                        @Override
                        public void newStyleEntered(String name) {
                            main.getUndoableManager().addUndoable(
                                    new UndoableManager.DuplicateCustomStyleUndoable(main,
                                            name,
                                            (CustomStyle) main.getRootTable().getStyleSelectBox().getSelected()), true);
                        }

                        @Override
                        public void cancelled() {
                        }
                    });
                }
                break;
            case DELETE_STYLE:
                if (main.getRootTable().getClassSelectBox().getSelectedIndex() < Main.BASIC_CLASSES.length) {
                    dialogFactory.showDeleteStyleDialog(main.getSkin(), main.getStage());
                } else {
                    main.getUndoableManager().addUndoable(new UndoableManager.DeleteCustomStyleUndoable(main, (CustomStyle) main.getRootTable().getStyleSelectBox().getSelected()), true);
                }
                break;
            case RENAME_STYLE:
                if (main.getRootTable().getClassSelectBox().getSelectedIndex() < Main.BASIC_CLASSES.length) {
                    dialogFactory.showRenameStyleDialog(main.getSkin(), main.getStage());
                } else {
                    dialogFactory.showRenameCustomStyleDialog(new DialogCustomStyle.CustomStyleListener() {
                        @Override
                        public void newStyleEntered(String name) {
                            main.getUndoableManager().addUndoable(
                                    new UndoableManager.RenameCustomStyleUndoable(main,
                                            name,
                                            (CustomStyle) main.getRootTable().getStyleSelectBox().getSelected()), true);
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

            String[] filterPatterns = null;
            if (!Utils.isMac()) {
                filterPatterns = new String[] {"*.scmp"};
            }

            File file = desktopWorker.openDialog("Open skin file...", defaultPath, filterPatterns, "Skin Composer files");
            if (file != null) {
                FileHandle fileHandle = new FileHandle(file);
                projectData.load(fileHandle);
                Array<DrawableData> drawableErrors = projectData.verifyDrawablePaths();
                Array<FontData> fontErrors = projectData.verifyFontPaths();
                if (drawableErrors.size > 0 || fontErrors.size > 0) {
                    dialogFactory.showDialogPathErrors(drawableErrors, fontErrors);
                }
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
    
    public void openFile(FileHandle fileHandle) {
        Runnable runnable = () -> {
            String defaultPath = projectData.getLastOpenSavePath();

            String[] filterPatterns = null;
            if (!Utils.isMac()) {
                filterPatterns = new String[] {"*.scmp"};
            }

            if (fileHandle != null) {
                projectData.load(fileHandle);
                Array<DrawableData> drawableErrors = projectData.verifyDrawablePaths();
                Array<FontData> fontErrors = projectData.verifyFontPaths();
                if (drawableErrors.size > 0 || fontErrors.size > 0) {
                    dialogFactory.showDialogPathErrors(drawableErrors, fontErrors);
                }
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
            Array<String> warnings = new Array<>();
            String defaultPath = projectData.getLastImportExportPath();

            String[] filterPatterns = null;
            if (!Utils.isMac()) {
                filterPatterns = new String[] {"*.json"};
            }

            File file = desktopWorker.openDialog("Import skin...", defaultPath, filterPatterns, "Json files");
            if (file != null) {
                FileHandle fileHandle = new FileHandle(file);
                projectData.setLastImportExportPath(fileHandle.parent().path() + "/");
                try {
                    Array<String> newWarnings = jsonData.readFile(fileHandle);
                    warnings.addAll(newWarnings);
                    main.getProjectData().getAtlasData().atlasCurrent = false;
                    jsonData.checkForPropertyConsistency();
                    main.getRootTable().produceAtlas();
                    main.getRootTable().populate();
                } catch (Exception e) {
                    Gdx.app.error(getClass().getName(), "Error attempting to import JSON", e);
                    dialogFactory.showDialogError("Import Error...", "Error while attempting to import a skin.\nPlease check that all files exist.\n\nOpen log?");
                }
            }
            
            if (warnings.size > 0) {
                main.getDialogFactory().showWarningDialog(warnings);
            }
        });
    }
    
    public void exportFile() {
        dialogFactory.showDialogLoading(() -> {
            Array<String> warnings = new Array<>();
            String defaultPath = projectData.getLastImportExportPath();

            String[] filterPatterns = {"*.json"};

            File file = desktopWorker.saveDialog("Export skin...", defaultPath, filterPatterns, "Json files");
            if (file != null) {
                FileHandle fileHandle = new FileHandle(file);
                if (fileHandle.extension() == null || !fileHandle.extension().equals(".json")) {
                    fileHandle = fileHandle.sibling(fileHandle.nameWithoutExtension() + ".json");
                }
                projectData.setLastImportExportPath(fileHandle.parent().path() + "/");
                Array<String> newWarnings = main.getProjectData().getJsonData().writeFile(fileHandle);
                warnings.addAll(newWarnings);
                
                try {
                    newWarnings = main.getProjectData().getAtlasData().writeAtlas(fileHandle.parent().child(fileHandle.nameWithoutExtension() + ".atlas"));
                    warnings.addAll(newWarnings);
                } catch (Exception ex) {
                    Gdx.app.error(getClass().getName(), "Error while writing texture atlas", ex);
                    dialogFactory.showDialogError("Atlas Error...", "Error while writing texture atlas.\n\nOpen log?");
                }
                
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
    public void loadClasses(SelectBox classSelectBox) {
        Array names = new Array<>();
        for (Class clazz : Main.BASIC_CLASSES) {
            names.add(clazz.getSimpleName());
        }
        for (CustomClass customClass : main.getJsonData().getCustomClasses()) {
            names.add(customClass);
        }
        classSelectBox.setItems(names);
    }

    @Override
    public void loadStyles(SelectBox classSelectBox, SelectBox styleSelectBox) {
        int selection = classSelectBox.getSelectedIndex();
        if (selection < Main.BASIC_CLASSES.length) { 
            Class selectedClass = Main.BASIC_CLASSES[selection];
            styleSelectBox.setItems(jsonData.getClassStyleMap().get(selectedClass));
            styleSelectBox.setSelectedIndex(0);
        } else {
            CustomClass customClass = (CustomClass) classSelectBox.getSelected();
            styleSelectBox.setItems(customClass.getStyles());
        }
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
            main.getUndoableManager().addUndoable(new UndoableManager.DoubleUndoable(main, styleProperty, ((Spinner) styleActor).getValue()), false);
        } else if (styleProperty.type == ScrollPaneStyle.class) {
            main.getUndoableManager().addUndoable(new UndoableManager.SelectBoxUndoable(root, styleProperty, (SelectBox) styleActor), true);
        } else if (styleProperty.type == LabelStyle.class) {
            main.getUndoableManager().addUndoable(new UndoableManager.SelectBoxUndoable(root, styleProperty, (SelectBox) styleActor), true);
        } else if (styleProperty.type == ListStyle.class) {
            main.getUndoableManager().addUndoable(new UndoableManager.SelectBoxUndoable(root, styleProperty, (SelectBox) styleActor), true);
        }
    }
    
    private void updateStyleProperties() {
        int classIndex = root.getClassSelectBox().getSelectedIndex();
        if (classIndex >= 0 && classIndex < Main.BASIC_CLASSES.length) {
            root.setClassDuplicateButtonDisabled(true);
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
            main.getRootTable().setClassDuplicateButtonDisabled(false);
            main.getRootTable().setClassDeleteButtonDisabled(false);
            main.getRootTable().setClassRenameButtonDisabled(false);
            
            Object selected = main.getRootTable().getStyleSelectBox().getSelected();
            
            if (selected instanceof CustomStyle) {
                CustomStyle customStyle = (CustomStyle) selected;
                
                main.getRootTable().setStyleDeleteButtonDisabled(!customStyle.isDeletable());
                main.getRootTable().setStyleRenameButtonDisabled(!customStyle.isDeletable());
                
                root.setCustomStyleProperties(customStyle.getProperties());
                root.refreshStyleProperties(false);
                root.refreshPreviewProperties();
                root.refreshPreview();
            }
        }
    }

    @Override
    public void newCustomProperty() {
        dialogFactory.showNewStylePropertyDialog(new CustomStylePropertyListener() {
            @Override
            public void newPropertyEntered(String propertyName, PropertyType propertyType) {
                main.getUndoableManager().addUndoable(new UndoableManager.NewCustomPropertyUndoable(main, propertyName, propertyType), true);
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
                main.getUndoableManager().addUndoable(new UndoableManager.DuplicateCustomPropertyUndoable(main, customProperty, propertyName, propertyType), true);
            }

            @Override
            public void cancelled() {
            }
        });
    }

    @Override
    public void deleteCustomProperty(CustomProperty customProperty) {
        main.getUndoableManager().addUndoable(new UndoableManager.DeleteCustomPropertyUndoable(main, customProperty), true);
    }

    @Override
    public void renameCustomProperty(CustomProperty customProperty) {
        dialogFactory.showRenameStylePropertyDialog(customProperty.getName(), customProperty.getType(), new CustomStylePropertyListener() {
            @Override
            public void newPropertyEntered(String propertyName, PropertyType propertyType) {
                main.getUndoableManager().addUndoable(new UndoableManager.RenameCustomPropertyUndoable(main, customProperty, propertyName, propertyType), true);
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
                dialogFactory.showDialogDrawables(customProperty);
                break;
            case COLOR:
                dialogFactory.showDialogColors(customProperty);
                break;
            case FONT:
                dialogFactory.showDialogFonts(customProperty);
                break;
            case NUMBER:
                main.getUndoableManager().addUndoable(new UndoableManager.CustomDoubleUndoable(main, customProperty, ((Spinner) styleActor).getValue()), false);
                break;
            case TEXT:
            case RAW_TEXT:
                main.getUndoableManager().addUndoable(new UndoableManager.CustomTextUndoable(main, customProperty, ((TextField) styleActor).getText()), false);
                break;
            case BOOL:
                main.getUndoableManager().addUndoable(new UndoableManager.CustomBoolUndoable(main, customProperty, ((Button) styleActor).isChecked()), false);
                break;
            default:
                break;
        }  
    }
    
    public void refreshTextureAtlas() {
        main.getDialogFactory().showDialogLoading(() -> {
            try {
                main.getProjectData().getAtlasData().writeAtlas();
                main.getProjectData().getAtlasData().atlasCurrent = true;
                main.getRootTable().produceAtlas();
                main.getRootTable().refreshPreview();
            } catch (Exception e) {
                main.getDialogFactory().showDialogError("Error", "Unable to write texture atlas to temporary storage!", null);
                Gdx.app.error(getClass().getName(), "Unable to write texture atlas to temporary storage!", e);
                main.getDialogFactory().showDialogError("Atlas Error...", "Unable to write texture atlas to temporary storage.\n\nOpen log?");
            }
        });
    }
    
    public WelcomeListener getWelcomeListener() {
        return welcomeListener;
    }
    
    private class WelcomeDialogListener extends WelcomeListener {
        @Override
        public void cancelled() {
        }

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
            Gdx.files.internal("templates/vis-ui.scmp").copyTo(Gdx.files.local("temp/"));
            openFile(Gdx.files.local("temp/vis-ui.scmp"));
        }

        @Override
        public void plainJamesClicked() {
            Gdx.files.internal("templates/plain-james-ui.zip").copyTo(Gdx.files.local("temp/"));
            try {
                Utils.unzip(Gdx.files.local("temp/plain-james-ui.zip"), Gdx.files.local("temp/"));
                openFile(Gdx.files.local("temp/plain-james-ui.scmp"));
            } catch (Exception e) {
                Gdx.app.error(getClass().getName(), "Error attempting to open template Plain-James.", e);
                dialogFactory.showDialogError("Template Error...", "Error attempting to open template Plain-James.\n\nOpen log?");
            }
        }

        @Override
        public void neonClicked() {
            Gdx.files.internal("templates/neon-ui.zip").copyTo(Gdx.files.local("temp/"));
            try {
                Utils.unzip(Gdx.files.local("temp/neon-ui.zip"), Gdx.files.local("temp/"));
                openFile(Gdx.files.local("temp/neon-ui.scmp"));
            } catch (Exception e) {
                Gdx.app.error(getClass().getName(), "Error attempting to open template Neon.", e);
                dialogFactory.showDialogError("Template Error...", "Error attempting to open template Neon.\n\nOpen log?");
            }
        }

        @Override
        public void neutralizerClicked() {
            Gdx.files.internal("templates/neutralizer-ui.zip").copyTo(Gdx.files.local("temp/"));
            try {
                Utils.unzip(Gdx.files.local("temp/neutralizer-ui.zip"), Gdx.files.local("temp/"));
                openFile(Gdx.files.local("temp/neutralizer-ui.scmp"));
            } catch (Exception e) {
                Gdx.app.error(getClass().getName(), "Error attempting to open template Neutralizer.", e);
                dialogFactory.showDialogError("Template Error...", "Error attempting to open template Neutralizer.\n\nOpen log?");
            }
        }

        @Override
        public void dontShowClicked(boolean value) {
            main.getProjectData().setAllowingWelcome(!value);
        }
    }
}
