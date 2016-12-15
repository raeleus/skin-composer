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

import com.ray3k.skincomposer.data.StyleData;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.IbeamListener;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.data.JsonData;
import com.ray3k.skincomposer.Undoable;

public class PanelClassBar {    
    public SelectBox<String> classSelectBox;
    private SelectBox<StyleData> styleSelectBox;
    public static PanelClassBar instance;
    private Table table;
    private Skin skin;
    private Stage stage;
    
    public PanelClassBar(final Table table, final Skin skin, final Stage stage) {
        instance = this;
        
        this.table = table;
        this.skin = skin;
        this.stage = stage;
        populate();
    }
    
    public void populate() {
        table.clear();
        table.defaults().padTop(5.0f).padBottom(5.0f);
        table.add(new Label("Class:", skin, "white")).padLeft(2.0f).padRight(5.0f);
        table.setBackground("maroon");
        
        classSelectBox = new SelectBox<>(skin, "slim-alt");
        Array<String> names = new Array<>();
        for (Class clazz : Main.BASIC_CLASSES) {
            names.add(clazz.getSimpleName());
        }
        classSelectBox.setItems(names);
        table.add(classSelectBox).padRight(30.0f);
        
        table.add(new Label("Style:", skin, "white")).padRight(5.0f);
        styleSelectBox = new SelectBox<>(skin, "slim-alt");
        styleSelectBox.setItems(JsonData.getInstance().getClassStyleMap().get(Main.BASIC_CLASSES[0]));
        table.add(styleSelectBox).padRight(10.0f).minWidth(200.0f);
        
        classSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                styleSelectBox.setItems(JsonData.getInstance().getClassStyleMap().get(Main.BASIC_CLASSES[classSelectBox.getSelectedIndex()]));
                styleSelectBox.setSelected(styleSelectBox.getItems().first());
                PanelPreviewProperties.instance.populate();
            }
        });
        
        
        ImageButtonStyle style = new ImageButton.ImageButtonStyle(skin.get("menu-left", ImageButton.ImageButtonStyle.class));
        style.imageUp = skin.getDrawable("image-plus");
        style.imageDown = skin.getDrawable("image-plus-down");
        ImageButton imageButton = new ImageButton(style);
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                showNewStyleDialog(skin, stage);
            }
        });
        table.add(imageButton);
        
        style = new ImageButton.ImageButtonStyle(skin.get("menu-center", ImageButton.ImageButtonStyle.class));
        style.imageUp = skin.getDrawable("image-duplicate");
        style.imageDown = skin.getDrawable("image-duplicate-down");
        imageButton = new ImageButton(style);
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                showDuplicateStyleDialog(skin, stage);
            }
        });
        table.add(imageButton);
        
        style = new ImageButton.ImageButtonStyle(skin.get("menu-right", ImageButton.ImageButtonStyle.class));
        style.imageUp = skin.getDrawable("image-delete");
        style.imageDown = skin.getDrawable("image-delete-down");
        style.imageDisabled = skin.getDrawable("image-delete-disabled");
        final ImageButton deleteButton = new ImageButton(style);
        deleteButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                showDeleteStyleDialog(skin, stage);
            }
        });
        deleteButton.setDisabled(!styleSelectBox.getSelected().deletable);
        table.add(deleteButton);
        
        table.add().growX();
        
        styleSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                PanelStyleProperties.instance.populate(styleSelectBox.getSelected());
                deleteButton.setDisabled(!styleSelectBox.getSelected().deletable);
                PanelPreviewProperties.instance.populate();
            }
        });
    }

    public SelectBox<StyleData> getStyleSelectBox() {
        return styleSelectBox;
    }
    
    private void showNewStyleDialog(Skin skin, Stage stage) {
        final TextField textField = new TextField("", skin);
        Dialog dialog = new Dialog("New Style", skin, "dialog") {
            @Override
            protected void result(Object object) {
                if ((Boolean)object) {
                    Main.instance().getUndoableManager().addUndoable(new NewStyleUndoable(classSelectBox, styleSelectBox, textField), true);
                }
            }
        };
        dialog.button("OK", true).button("Cancel", false);
        final TextButton okButton = (TextButton) dialog.getButtonTable().getCells().get(0).getActor();
        
        textField.setTextFieldListener((TextField textField1, char c) -> {
            if (c == '\n') {
                if (!okButton.isDisabled()) {
                    Main.instance().getUndoableManager().addUndoable(new NewStyleUndoable(classSelectBox, styleSelectBox, textField1), true);
                    dialog.hide();
                }
                Main.instance().getStage().setKeyboardFocus(textField1);
            }
        });
        
        textField.addListener(IbeamListener.get());
        
        dialog.getTitleLabel().setAlignment(Align.center);
        dialog.getContentTable().defaults().padLeft(10.0f).padRight(10.0f);
        dialog.text("What is the name of the new style?");
        dialog.getContentTable().row();
        dialog.getContentTable().add(textField).growX();
        okButton.setDisabled(true);
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                boolean disable = !StyleData.validate(textField.getText());
                
                if (!disable) {
                    for (StyleData data : styleSelectBox.getItems()) {
                        if (data.name.equals(textField.getText())) {
                            disable = true;
                            break;
                        }
                    }
                }
                
                okButton.setDisabled(disable);
            }
        });
        
        
        dialog.key(Keys.ESCAPE, false);
        
        dialog.show(stage);
        stage.setKeyboardFocus(textField);
    }
    
    private void showDuplicateStyleDialog(Skin skin, Stage stage) {
        final TextField textField = new TextField("", skin);
        Dialog dialog = new Dialog("Duplicate Style", skin, "dialog") {
            @Override
            protected void result(Object object) {
                if ((Boolean)object) {
                    Main.instance().getUndoableManager().addUndoable(new DuplicateStyleUndoable(styleSelectBox, classSelectBox, textField), true);
                }
            }
        };
        
        dialog.button("OK", true).button("Cancel", false);
        final TextButton okButton = (TextButton) dialog.getButtonTable().getCells().get(0).getActor();
        
        textField.setTextFieldListener((TextField textField1, char c) -> {
            if (c == '\n') {
                if (!okButton.isDisabled()) {
                    Main.instance().getUndoableManager().addUndoable(new DuplicateStyleUndoable(styleSelectBox, classSelectBox, textField1), true);
                    dialog.hide();
                }
                Main.instance().getStage().setKeyboardFocus(textField1);
            }
        });
        
        textField.addListener(IbeamListener.get());
        
        dialog.getTitleLabel().setAlignment(Align.center);
        dialog.getContentTable().defaults().padLeft(10.0f).padRight(10.0f);
        dialog.text("What is the name of the new, duplicated style?");
        dialog.getContentTable().row();
        dialog.getContentTable().add(textField).growX();
        okButton.setDisabled(true);
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                boolean disable = !StyleData.validate(textField.getText());
                
                if (!disable) {
                    for (StyleData data : styleSelectBox.getItems()) {
                        if (data.name.equals(textField.getText())) {
                            disable = true;
                            break;
                        }
                    }
                }
                
                okButton.setDisabled(disable);
            }
        });
        
        
        dialog.key(Keys.ESCAPE, false);
        
        dialog.show(stage);
        stage.setKeyboardFocus(textField);
    }
    
    private void showDeleteStyleDialog(Skin skin, Stage stage) {
        Dialog dialog = new Dialog("Delete Style", skin, "dialog") {
            @Override
            protected void result(Object object) {
                if ((Boolean)object) {
                    Main.instance().getUndoableManager().addUndoable(new DeleteStyleUndoable(styleSelectBox, classSelectBox), true);
                }
            }
        };
        dialog.getTitleLabel().setAlignment(Align.center);
        dialog.getContentTable().defaults().padLeft(10.0f).padRight(10.0f);
        dialog.text("Are you sure you want to delete style " + styleSelectBox.getSelected().name + "?");
        dialog.button("Yes, delete the style", true).button("No", false);
        
        dialog.key(Keys.ENTER, true).key(Keys.ESCAPE, false);
        
        dialog.show(stage);
    }
    
    private static class NewStyleUndoable implements Undoable {
        private SelectBox classSelectBox, styleSelectBox;
        private TextField textField;
        private StyleData styleData;
        private int previousIndex;

        public NewStyleUndoable(SelectBox classSelectBox, SelectBox styleSelectBox, TextField textField) {
            this.classSelectBox = classSelectBox;
            this.styleSelectBox = styleSelectBox;
            this.textField = textField;
            previousIndex = styleSelectBox.getSelectedIndex();
        }
        
        @Override
        public void undo() {
            JsonData.getInstance().deleteStyle(styleData);
            styleSelectBox.setItems(JsonData.getInstance().getClassStyleMap().get(Main.BASIC_CLASSES[classSelectBox.getSelectedIndex()]));
            styleSelectBox.setSelectedIndex(previousIndex);
        }

        @Override
        public void redo() {
            styleData = JsonData.getInstance().newStyle(Main.BASIC_CLASSES[classSelectBox.getSelectedIndex()], textField.getText());
            styleSelectBox.setItems(JsonData.getInstance().getClassStyleMap().get(Main.BASIC_CLASSES[classSelectBox.getSelectedIndex()]));
            styleSelectBox.setSelected(styleData);
        }

        @Override
        public String getUndoText() {
            return "Create Style \"" + styleData.name + "\"";
        }
    }
    
    private static class DuplicateStyleUndoable implements Undoable {
        private SelectBox<StyleData> styleSelectBox;
        private SelectBox classSelectBox;
        private TextField textField;
        private StyleData styleData; 
        private int previousIndex;

        public DuplicateStyleUndoable(SelectBox<StyleData> styleSelectBox, SelectBox classSelectBox, TextField textField) {
            this.styleSelectBox = styleSelectBox;
            this.classSelectBox = classSelectBox;
            this.textField = textField;
            previousIndex = styleSelectBox.getSelectedIndex();
        }
        
        @Override
        public void undo() {
            JsonData.getInstance().deleteStyle(styleData);
            styleSelectBox.setItems(JsonData.getInstance().getClassStyleMap().get(Main.BASIC_CLASSES[classSelectBox.getSelectedIndex()]));
            styleSelectBox.setSelectedIndex(previousIndex);
        }

        @Override
        public void redo() {
            styleData = JsonData.getInstance().copyStyle(styleSelectBox.getSelected(), textField.getText());
            styleSelectBox.setItems(JsonData.getInstance().getClassStyleMap().get(Main.BASIC_CLASSES[classSelectBox.getSelectedIndex()]));
            styleSelectBox.setSelected(styleData);
        }

        @Override
        public String getUndoText() {
            return "Duplicate Style \"" + styleData.name + "\"";
        }
    }
    
    private static class DeleteStyleUndoable implements Undoable {
        SelectBox<StyleData> styleSelectBox;
        SelectBox classSelectBox;
        StyleData styleData;

        public DeleteStyleUndoable(SelectBox<StyleData> styleSelectBox, SelectBox classSelectBox) {
            this.styleSelectBox = styleSelectBox;
            this.classSelectBox = classSelectBox;
            styleData = styleSelectBox.getSelected();
        }

        @Override
        public void undo() {
            JsonData.getInstance().copyStyle(styleData, styleData.name);
            styleSelectBox.setItems(JsonData.getInstance().getClassStyleMap().get(Main.BASIC_CLASSES[classSelectBox.getSelectedIndex()]));
            styleSelectBox.setSelected(styleData);
        }

        @Override
        public void redo() {
            JsonData.getInstance().deleteStyle(styleSelectBox.getSelected());
            styleSelectBox.setItems(JsonData.getInstance().getClassStyleMap().get(Main.BASIC_CLASSES[classSelectBox.getSelectedIndex()]));
        }

        @Override
        public String getUndoText() {
            return "Delete Style \"" + styleData.name + "\"";
        }
    }
}
