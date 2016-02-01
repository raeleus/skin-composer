package com.ray3k.skincomposer;

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
import com.badlogic.gdx.utils.OrderedMap;
import com.ray3k.skincomposer.StyleData.ClassName;

public class PanelClassBar {    
    private SelectBox<ClassName> classSelectBox;
    private SelectBox<StyleData> styleSelectBox;
    
    public PanelClassBar(final Table table, final OrderedMap<StyleData.ClassName, Array<StyleData>> classStyleMap, final Skin skin, final Stage stage) {
        table.defaults().padTop(5.0f).padBottom(5.0f);
        table.add(new Label("Class:", skin, "white")).padLeft(2.0f).padRight(5.0f);
        table.setBackground("maroon");
        
        classSelectBox = new SelectBox<ClassName>(skin);
        classSelectBox.setItems(ClassName.values());
        table.add(classSelectBox).padRight(30.0f);
        
        table.add(new Label("Style:", skin, "white")).padRight(5.0f);
        styleSelectBox = new SelectBox<StyleData>(skin);
        styleSelectBox.setItems(classStyleMap.get(classSelectBox.getSelected()));
        table.add(styleSelectBox).padRight(10.0f).minWidth(200.0f);
        
        classSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                styleSelectBox.setItems(classStyleMap.get(classSelectBox.getSelected()));
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
                PanelStyleProperties.actor.populate(styleSelectBox.getSelected());
                deleteButton.setDisabled(!styleSelectBox.getSelected().deletable);
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
                    StyleData styleData = Main.instance.newStyle(classSelectBox.getSelected(), textField.getText());
                    styleSelectBox.setItems(Main.instance.getClassStyleMap().get(classSelectBox.getSelected()));
                    styleSelectBox.setSelected(styleData);
                }
            }
        };
        dialog.getTitleLabel().setAlignment(Align.center);
        dialog.getContentTable().defaults().padLeft(10.0f).padRight(10.0f);
        dialog.text("What is the name of the new style?");
        dialog.getContentTable().row();
        dialog.getContentTable().add(textField).growX();
        dialog.button("OK", true).button("Cancel", false);
        final TextButton okButton = (TextButton) dialog.getButtonTable().getCells().get(0).getActor();
        okButton.setDisabled(true);
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                okButton.setDisabled(textField.getText().length() <= 0);
            }
        });
        
        
        dialog.key(Keys.ENTER, true).key(Keys.ESCAPE, false);
        
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
                    styleSelectBox.setItems(Main.instance.getClassStyleMap().get(classSelectBox.getSelected()));
                    styleSelectBox.setSelected(styleData);
                }
            }
        };
        dialog.getTitleLabel().setAlignment(Align.center);
        dialog.getContentTable().defaults().padLeft(10.0f).padRight(10.0f);
        dialog.text("What is the name of the new, duplicated style?");
        dialog.getContentTable().row();
        dialog.getContentTable().add(textField).growX();
        dialog.button("OK", true).button("Cancel", false);
        final TextButton okButton = (TextButton) dialog.getButtonTable().getCells().get(0).getActor();
        okButton.setDisabled(true);
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                okButton.setDisabled(textField.getText().length() <= 0);
            }
        });
        
        
        dialog.key(Keys.ENTER, true).key(Keys.ESCAPE, false);
        
        dialog.show(stage);
        stage.setKeyboardFocus(textField);
    }
    
    private void showDeleteStyleDialog(Skin skin, Stage stage) {
        Dialog dialog = new Dialog("Delete Style", skin, "dialog") {
            @Override
            protected void result(Object object) {
                if ((Boolean)object) {
                    Main.instance.deleteStyle(styleSelectBox.getSelected());
                    styleSelectBox.setItems(Main.instance.getClassStyleMap().get(classSelectBox.getSelected()));
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
