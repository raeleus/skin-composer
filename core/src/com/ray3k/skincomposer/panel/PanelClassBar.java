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
        for (Class clazz : StyleData.classes) {
            names.add(clazz.getSimpleName());
        }
        classSelectBox.setItems(names);
        table.add(classSelectBox).padRight(30.0f);
        
        table.add(new Label("Style:", skin, "white")).padRight(5.0f);
        styleSelectBox = new SelectBox<>(skin, "slim-alt");
        styleSelectBox.setItems(JsonData.getInstance().getClassStyleMap().get(StyleData.classes[0]));
        table.add(styleSelectBox).padRight(10.0f).minWidth(200.0f);
        
        classSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                styleSelectBox.setItems(JsonData.getInstance().getClassStyleMap().get(StyleData.classes[classSelectBox.getSelectedIndex()]));
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
                    StyleData styleData = Main.instance.newStyle(StyleData.classes[classSelectBox.getSelectedIndex()], textField.getText());
                    styleSelectBox.setItems(JsonData.getInstance().getClassStyleMap().get(StyleData.classes[classSelectBox.getSelectedIndex()]));
                    styleSelectBox.setSelected(styleData);
                }
            }
        };
        dialog.button("OK", true).button("Cancel", false);
        final TextButton okButton = (TextButton) dialog.getButtonTable().getCells().get(0).getActor();
        
        textField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                if (c == '\n') {
                    if (!okButton.isDisabled()) {
                        StyleData styleData = Main.instance.newStyle(StyleData.classes[classSelectBox.getSelectedIndex()], textField.getText());
                        styleSelectBox.setItems(JsonData.getInstance().getClassStyleMap().get(StyleData.classes[classSelectBox.getSelectedIndex()]));
                        styleSelectBox.setSelected(styleData);

                        dialog.hide();
                    }
                    Main.instance.getStage().setKeyboardFocus(textField);
                }
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
                    StyleData styleData = Main.instance.copyStyle(styleSelectBox.getSelected(), textField.getText());
                    styleSelectBox.setItems(JsonData.getInstance().getClassStyleMap().get(StyleData.classes[classSelectBox.getSelectedIndex()]));
                    styleSelectBox.setSelected(styleData);
                }
            }
        };
        
        dialog.button("OK", true).button("Cancel", false);
        final TextButton okButton = (TextButton) dialog.getButtonTable().getCells().get(0).getActor();
        
        textField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                if (c == '\n') {
                    if (!okButton.isDisabled()) {
                        StyleData styleData = Main.instance.copyStyle(styleSelectBox.getSelected(), textField.getText());
                        styleSelectBox.setItems(JsonData.getInstance().getClassStyleMap().get(StyleData.classes[classSelectBox.getSelectedIndex()]));
                        styleSelectBox.setSelected(styleData);

                        dialog.hide();
                    }
                    Main.instance.getStage().setKeyboardFocus(textField);
                }
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
                    Main.instance.deleteStyle(styleSelectBox.getSelected());
                    styleSelectBox.setItems(JsonData.getInstance().getClassStyleMap().get(StyleData.classes[classSelectBox.getSelectedIndex()]));
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
}
