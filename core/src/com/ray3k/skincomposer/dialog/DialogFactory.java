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
package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.UndoableManager;
import com.ray3k.skincomposer.UndoableManager.DeleteStyleUndoable;
import com.ray3k.skincomposer.UndoableManager.DuplicateStyleUndoable;
import com.ray3k.skincomposer.UndoableManager.NewStyleUndoable;
import com.ray3k.skincomposer.data.AtlasData;
import com.ray3k.skincomposer.data.CustomClass;
import com.ray3k.skincomposer.data.JsonData;
import com.ray3k.skincomposer.data.ProjectData;
import com.ray3k.skincomposer.data.ProjectData.RecentFile;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.data.StyleProperty;
import com.ray3k.skincomposer.dialog.DialogCustomClass.CustomClassListener;
import com.ray3k.skincomposer.dialog.DialogCustomProperty.CustomStylePropertyListener;
import com.ray3k.skincomposer.dialog.DialogCustomProperty.PropertyType;

public class DialogFactory {

    private static DialogFactory instance;
    private final Skin skin;
    private final Stage stage;
    private boolean showingCloseDialog;
    private JsonData jsonData;
    private ProjectData projectData;
    private AtlasData atlasData;
    private Main main;

    public DialogFactory(Skin skin, Stage stage, JsonData jsonData,
            ProjectData projectData, AtlasData atlasData, Main main) {
        instance = this;
        this.skin = skin;
        this.stage = stage;
        this.jsonData = jsonData;
        this.projectData = projectData;
        this.atlasData = atlasData;
        showingCloseDialog = false;
        this.main = main;
    }

    public void showAbout() {
        DialogAbout dialog = new DialogAbout(main, skin, "dialog");
        dialog.show(stage);
    }

    public void showDialogColors(StyleProperty styleProperty,
            DialogColors.DialogColorsListener listener) {
        DialogColors dialog = new DialogColors(skin, "dialog", styleProperty, this, jsonData, projectData, atlasData, main, listener);
        dialog.setFillParent(true);
        dialog.show(stage);
        dialog.populate();
    }

    public void showDialogColors(StyleProperty styleProperty) {
        showDialogColors(styleProperty, null);
    }

    public void showColors() {
        showDialogColors(null);
    }

    public void showDialogDrawables(StyleProperty property,
            EventListener listener) {
        DialogDrawables dialog = new DialogDrawables(skin, "dialog", property, this, jsonData, projectData, atlasData, main, listener);
        dialog.setFillParent(true);
        dialog.show(stage);
    }

    public void showDialogDrawables(StyleProperty property) {
        showDialogDrawables(property, null);
    }

    public void showDrawables() {
        showDialogDrawables(null);
    }

    public void showDialogFonts(StyleProperty styleProperty,
            EventListener listener) {
        DialogFonts dialog = new DialogFonts(skin, "dialog", styleProperty, jsonData, projectData, atlasData, main, listener);
        dialog.setFillParent(true);
        dialog.show(stage);
        dialog.populate();
    }

    public void showDialogFonts(StyleProperty styleProperty) {
        showDialogFonts(styleProperty, null);
    }

    public void showFonts() {
        showDialogFonts(null);
    }

    public void showSettings() {
        DialogSettings dialog = new DialogSettings("", "dialog", main);
        dialog.show(stage);
    }

    public void showDialogColorPicker(DialogColorPicker.ColorListener listener) {
        showDialogColorPicker(null, listener);
    }

    public void showDialogColorPicker(Color previousColor,
            DialogColorPicker.ColorListener listener) {
        DialogColorPicker dialog = new DialogColorPicker(main, "dialog", listener, previousColor);
        dialog.show(stage);
    }

    public void showNewStyleDialog(Skin skin, Stage stage) {
        Class selectedClass = main.getRootTable().getSelectedClass();

        final TextField textField = new TextField("", skin);
        Dialog dialog = new Dialog("New Style", skin, "bg") {
            @Override
            protected void result(Object object) {
                if ((Boolean) object) {
                    main.getUndoableManager().addUndoable(new NewStyleUndoable(selectedClass, textField.getText(), main), true);
                }
            }
        };
        dialog.getButtonTable().defaults().padBottom(10.0f).minWidth(50.0f);
        dialog.button("OK", true).button("Cancel", false);
        dialog.getButtonTable().getCells().first().getActor().addListener(main.getHandListener());
        dialog.getButtonTable().getCells().get(1).getActor().addListener(main.getHandListener());
        final TextButton okButton = (TextButton) dialog.getButtonTable().getCells().get(0).getActor();

        textField.setTextFieldListener((TextField textField1, char c) -> {
            if (c == '\n') {
                if (!okButton.isDisabled()) {
                    main.getUndoableManager().addUndoable(new NewStyleUndoable(selectedClass, textField1.getText(), main), true);
                    dialog.hide();
                }
                main.getStage().setKeyboardFocus(textField1);
            }
        });

        textField.addListener(main.getIbeamListener());

        dialog.getTitleLabel().setAlignment(Align.center);
        dialog.getContentTable().defaults().padLeft(10.0f).padRight(10.0f);
        dialog.text("What is the name of the new style?");
        dialog.getContentTable().getCells().first().pad(10.0f);
        dialog.getContentTable().row();
        dialog.getContentTable().add(textField).growX();
        okButton.setDisabled(true);

        Array<StyleData> currentStyles = main.getProjectData().getJsonData().getClassStyleMap().get(selectedClass);
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                boolean disable = !StyleData.validate(textField.getText());

                if (!disable) {
                    for (StyleData data : currentStyles) {
                        if (data.name.equals(textField.getText())) {
                            disable = true;
                            break;
                        }
                    }
                }

                okButton.setDisabled(disable);
            }
        });

        dialog.key(Input.Keys.ESCAPE, false);

        dialog.show(stage);
        stage.setKeyboardFocus(textField);
        textField.setFocusTraversal(false);
    }

    public void showDuplicateStyleDialog(Skin skin, Stage stage) {
        Class selectedClass = main.getRootTable().getSelectedClass();
        StyleData originalStyle = main.getRootTable().getSelectedStyle();

        final TextField textField = new TextField("", skin);
        Dialog dialog = new Dialog("Duplicate Style", skin, "bg") {
            @Override
            protected void result(Object object) {
                if ((Boolean) object) {
                    main.getUndoableManager().addUndoable(new DuplicateStyleUndoable(originalStyle, textField.getText(), main), true);
                }
            }
        };

        dialog.getButtonTable().defaults().padBottom(10.0f).minWidth(50.0f);
        dialog.button("OK", true).button("Cancel", false);
        final TextButton okButton = (TextButton) dialog.getButtonTable().getCells().get(0).getActor();
        okButton.addListener(main.getHandListener());
        dialog.getButtonTable().getCells().get(1).getActor().addListener(main.getHandListener());

        textField.setTextFieldListener((TextField textField1, char c) -> {
            if (c == '\n') {
                if (!okButton.isDisabled()) {
                    main.getUndoableManager().addUndoable(new DuplicateStyleUndoable(originalStyle, textField.getText(), main), true);
                    dialog.hide();
                }
                main.getStage().setKeyboardFocus(textField1);
            }
        });

        textField.addListener(main.getIbeamListener());

        dialog.getTitleLabel().setAlignment(Align.center);
        dialog.getContentTable().defaults().padLeft(10.0f).padRight(10.0f);
        dialog.text("What is the name of the new, duplicated style?");
        dialog.getContentTable().getCells().first().pad(10.0f);
        dialog.getContentTable().row();
        dialog.getContentTable().add(textField).growX();
        okButton.setDisabled(true);

        Array<StyleData> currentStyles = main.getProjectData().getJsonData().getClassStyleMap().get(selectedClass);
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                boolean disable = !StyleData.validate(textField.getText());

                if (!disable) {
                    for (StyleData data : currentStyles) {
                        if (data.name.equals(textField.getText())) {
                            disable = true;
                            break;
                        }
                    }
                }

                okButton.setDisabled(disable);
            }
        });

        dialog.key(Input.Keys.ESCAPE, false);

        dialog.show(stage);
        stage.setKeyboardFocus(textField);

        textField.setFocusTraversal(false);
    }

    public void showDeleteStyleDialog(Skin skin, Stage stage) {
        StyleData styleData = main.getRootTable().getSelectedStyle();

        Dialog dialog = new Dialog("Delete Style", skin, "bg") {
            @Override
            protected void result(Object object) {
                if ((Boolean) object) {
                    main.getUndoableManager().addUndoable(new DeleteStyleUndoable(styleData, main), true);
                }
            }
        };
        dialog.getTitleLabel().setAlignment(Align.center);
        dialog.getContentTable().defaults().padLeft(10.0f).padRight(10.0f);
        dialog.text("Are you sure you want to delete style " + styleData.name + "?");
        dialog.getContentTable().getCells().first().pad(10.0f);

        dialog.getButtonTable().defaults().padBottom(10.0f).minWidth(50.0f);
        dialog.button("Yes, delete the style", true).button("No", false);
        dialog.getButtonTable().getCells().first().getActor().addListener(main.getHandListener());
        dialog.getButtonTable().getCells().get(1).getActor().addListener(main.getHandListener());

        dialog.key(Input.Keys.ENTER, true).key(Input.Keys.ESCAPE, false);

        dialog.show(stage);
    }

    public void showRenameStyleDialog(Skin skin, Stage stage) {
        Class selectedClass = main.getRootTable().getSelectedClass();

        final TextField textField = new TextField(main.getRootTable().getSelectedStyle().name, skin);
        Dialog dialog = new Dialog("Rename Style", skin, "bg") {
            @Override
            protected void result(Object object) {
                if ((Boolean) object) {
                    main.getUndoableManager().addUndoable(new UndoableManager.RenameStyleUndoable(main.getRootTable().getSelectedStyle(), main, textField.getText()), true);
                }
            }
        };
        dialog.getButtonTable().defaults().padBottom(10.0f).minWidth(50.0f);
        dialog.button("OK", true).button("Cancel", false);
        dialog.getButtonTable().getCells().first().getActor().addListener(main.getHandListener());
        dialog.getButtonTable().getCells().get(1).getActor().addListener(main.getHandListener());
        final TextButton okButton = (TextButton) dialog.getButtonTable().getCells().get(0).getActor();

        textField.setTextFieldListener((TextField textField1, char c) -> {
            if (c == '\n') {
                if (!okButton.isDisabled()) {
                    main.getUndoableManager().addUndoable(new UndoableManager.RenameStyleUndoable(main.getRootTable().getSelectedStyle(), main, textField1.getText()), true);
                    dialog.hide();
                }
                main.getStage().setKeyboardFocus(textField1);
            }
        });

        textField.addListener(main.getIbeamListener());

        dialog.getTitleLabel().setAlignment(Align.center);
        dialog.getContentTable().defaults().padLeft(10.0f).padRight(10.0f);
        dialog.text("What would you like to rename the style \"" + main.getRootTable().getSelectedStyle().name + "\" to?");
        dialog.getContentTable().getCells().first().pad(10.0f);
        dialog.getContentTable().row();
        dialog.getContentTable().add(textField).growX();
        okButton.setDisabled(true);

        Array<StyleData> currentStyles = main.getProjectData().getJsonData().getClassStyleMap().get(selectedClass);
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                boolean disable = !StyleData.validate(textField.getText());

                if (!disable) {
                    for (StyleData data : currentStyles) {
                        if (data.name.equals(textField.getText())) {
                            disable = true;
                            break;
                        }
                    }
                }

                okButton.setDisabled(disable);
            }
        });

        dialog.key(Input.Keys.ESCAPE, false);

        dialog.show(stage);
        stage.setKeyboardFocus(textField);
        textField.selectAll();
        textField.setFocusTraversal(false);
    }

    public void showCloseDialog() {
        if (projectData.areChangesSaved() || projectData.isNewProject()) {
            Gdx.app.exit();
        } else {
            if (!showingCloseDialog) {
                showingCloseDialog = true;
                Dialog dialog = new Dialog("Save Changes?", skin, "bg") {
                    @Override
                    protected void result(Object object) {
                        if ((int) object == 0) {
                            main.getMainListener().saveFile(() -> {
                                if (projectData.areChangesSaved()) {
                                    Gdx.app.exit();
                                }
                            });
                        } else if ((int) object == 1) {
                            Gdx.app.exit();
                        }

                        showingCloseDialog = false;
                    }
                };
                dialog.getTitleTable().getCells().first().padLeft(5.0f);
                Label label = new Label("Do you want to save\nyour changes before you quit?", skin);
                label.setAlignment(Align.center);
                dialog.text(label);
                dialog.getContentTable().getCells().first().pad(10.0f);
                dialog.getButtonTable().defaults().padBottom(10.0f).minWidth(50.0f);
                dialog.button("Yes", 0).button("No", 1).button("Cancel", 2);
                dialog.getButtonTable().getCells().first().getActor().addListener(main.getHandListener());
                dialog.getButtonTable().getCells().get(1).getActor().addListener(main.getHandListener());
                dialog.getButtonTable().getCells().get(2).getActor().addListener(main.getHandListener());
                java.awt.Toolkit.getDefaultToolkit().beep();
                dialog.show(stage);
            }
        }
    }

    public void showDialogLoading(Runnable runnable) {
        DialogLoading dialog = new DialogLoading("", runnable, main);
        dialog.show(stage);
    }

    public void yesNoDialog(String title, String text,
            ConfirmationListener listener) {
        Dialog dialog = new Dialog(title, skin, "bg") {
            @Override
            protected void result(Object object) {
                listener.selected((int) object);
            }
        };
        dialog.getTitleTable().getCells().first().padLeft(5.0f);
        Label label = new Label(text, skin);
        label.setAlignment(Align.center);
        dialog.text(label);
        dialog.getContentTable().getCells().first().pad(10.0f);
        dialog.getButtonTable().defaults().padBottom(10.0f).minWidth(50.0f);
        dialog.button("Yes", 0);
        dialog.button("No", 1);
        dialog.getButtonTable().getCells().first().getActor().addListener(main.getHandListener());
        dialog.getButtonTable().getCells().get(1).getActor().addListener(main.getHandListener());
        dialog.key(Input.Keys.ESCAPE, 1);
        dialog.show(stage);
    }

    public void yesNoCancelDialog(String title, String text,
            ConfirmationListener listener) {
        Dialog dialog = new Dialog(title, skin, "bg") {
            @Override
            protected void result(Object object) {
                listener.selected((int) object);
            }
        };
        dialog.getTitleTable().getCells().first().padLeft(5.0f);
        Label label = new Label(text, skin);
        label.setAlignment(Align.center);
        dialog.text(label);
        dialog.getContentTable().getCells().first().pad(10.0f);
        dialog.getButtonTable().defaults().padBottom(10.0f).minWidth(50.0f);
        dialog.button("Yes", 0);
        dialog.button("No", 1);
        dialog.button("Cancel", 2);
        dialog.getButtonTable().getCells().first().getActor().addListener(main.getHandListener());
        dialog.getButtonTable().getCells().get(1).getActor().addListener(main.getHandListener());
        dialog.getButtonTable().getCells().get(2).getActor().addListener(main.getHandListener());
        dialog.key(Input.Keys.ESCAPE, 2);
        dialog.show(stage);
    }

    public void showDialogError(String title, String message, Runnable runnable) {
        Dialog dialog = new Dialog(title, skin, "bg") {
            @Override
            public boolean remove() {
                if (runnable != null) {
                    runnable.run();
                }
                return super.remove();
            }

        };

        dialog.text(message);
        dialog.button("OK");
        dialog.getButtonTable().getCells().first().getActor().addListener(main.getHandListener());
        dialog.show(stage);
    }

    public void showDialogError(String title, String message) {
        DialogError dialog = new DialogError(title, message, main);
        dialog.show(main.getStage());
    }

    public static void showDialogErrorStatic(String title, String message) {
        if (instance != null) {
            instance.showDialogError(title, message);
        }
    }

    public interface ConfirmationListener {

        public void selected(int selection);
    }

    public void recentFiles() {
        SelectBox<RecentFile> selectBox = new SelectBox(skin);
        Dialog dialog = new Dialog("Recent Files...", skin, "bg") {
            @Override
            protected void result(Object object) {
                super.result(object);
                if ((boolean) object) {
                    if (selectBox.getSelected() != null) {
                        FileHandle file = selectBox.getSelected().getFileHandle();
                        if (file.exists()) {
                            projectData.load(file);
                        }
                    }
                }
            }
        };
        dialog.getTitleTable().getCells().first().padLeft(5.0f);
        selectBox.setItems(projectData.getRecentFiles());

        dialog.text("Select a file to open");
        dialog.getContentTable().row();
        dialog.getContentTable().add(selectBox).padLeft(10.0f).padRight(10.0f);
        dialog.getContentTable().getCells().first().pad(10.0f);
        dialog.getButtonTable().defaults().padBottom(10.0f).minWidth(50.0f);
        dialog.button("OK", true).key(Input.Keys.ENTER, true);
        dialog.button("Cancel", false).key(Input.Keys.ESCAPE, false);
        dialog.getButtonTable().getCells().first().getActor().addListener(main.getHandListener());
        dialog.getButtonTable().getCells().get(1).getActor().addListener(main.getHandListener());
        dialog.show(stage);
    }

    public void showNewClassDialog(CustomClassListener listener) {
        DialogCustomClass dialog = new DialogCustomClass(main, "New Custom Class", false);
        dialog.addListener(listener);
        dialog.show(stage);
    }
    
    public void showRenameClassDialog(CustomClassListener listener) {
        Object selected = main.getRootTable().getClassSelectBox().getSelected();
        if (selected instanceof CustomClass) {
            DialogCustomClass dialog = new DialogCustomClass(main, "Rename Custom Class", true, 
                    ((CustomClass) selected).getFullyQualifiedName(), 
                    ((CustomClass) selected).getDisplayName());
            dialog.addListener(listener);
            dialog.show(stage);
        }
    }
    
    public void showDuplicateClassDialog(CustomClassListener listener) {
        DialogCustomClass dialog;
        
        Object selected = main.getRootTable().getClassSelectBox().getSelected();
        if (selected instanceof CustomClass) {
            dialog = new DialogCustomClass(main, "Duplicate Class", false,
                    ((CustomClass) selected).getFullyQualifiedName(), 
                    ((CustomClass) selected).getDisplayName());
        } else {
            dialog = new DialogCustomClass(main, "Duplicate Class", false, null, null);
        }
        
        dialog.addListener(listener);
        dialog.show(stage);
    }

    public void showNewStylePropertyDialog(CustomStylePropertyListener listener) {
        DialogCustomProperty dialog = new DialogCustomProperty(main, "New Custom Property");
        dialog.addListener(listener);
        dialog.show(stage);
    }
    
    public void showRenameStylePropertyDialog(String propertyName, PropertyType propertyType, CustomStylePropertyListener listener) {
        DialogCustomProperty dialog = new DialogCustomProperty(main, "Rename Custom Property", propertyName, propertyType);
        dialog.addListener(listener);
        dialog.show(stage);
    }
    
    public void showDuplicateStylePropertyDialog(String propertyName, PropertyType propertyType, CustomStylePropertyListener listener) {
        DialogCustomProperty dialog = new DialogCustomProperty(main, "Duplicate Custom Property", propertyName, propertyType);
        dialog.addListener(listener);
        dialog.show(stage);
    }
}
