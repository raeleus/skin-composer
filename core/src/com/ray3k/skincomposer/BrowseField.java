package com.ray3k.skincomposer;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class BrowseField extends Table {
    private TextField textField;
    private TextButton textButton;
    
    public BrowseField(BrowseFieldStyle style) {
        textField = new TextField("", style.textFieldStyle);
        textField.setTouchable(Touchable.disabled);
        add(textField).minWidth(35.0f).prefWidth(35.0f).growX();
        textButton = new TextButton("...", style.textButtonStyle);
        addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if (actor.getClass() != BrowseField.class) {
                    event.stop();
                    actor.getParent().fire(new ChangeEvent());
                }
            }
        });
        add(textButton).padLeft(5.0f);
    }

    public TextField getTextField() {
        return textField;
    }

    public TextButton getTextButton() {
        return textButton;
    }
    
    static public class BrowseFieldStyle {
        public TextButtonStyle textButtonStyle;
        public TextFieldStyle textFieldStyle;

        public BrowseFieldStyle(TextButtonStyle textButtonStyle, TextFieldStyle textFieldStyle) {
            this.textButtonStyle = textButtonStyle;
            this.textFieldStyle = textFieldStyle;
        }

        public BrowseFieldStyle(BrowseFieldStyle style) {
            textButtonStyle = style.textButtonStyle;
            textFieldStyle = style.textFieldStyle;
        }
    }
}
