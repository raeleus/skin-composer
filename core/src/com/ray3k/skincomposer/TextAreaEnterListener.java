package com.ray3k.skincomposer;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;

public class TextAreaEnterListener implements TextFieldListener {
    private static TextAreaEnterListener instance;
    public static TextAreaEnterListener get() {
        if (instance == null) {
            instance = new TextAreaEnterListener();
        }
        return instance;
    }
    
    private TextAreaEnterListener() {
        super();
    }

    @Override
    public void keyTyped(TextField textField, char c) {
        if (c == '\n') {
            String text = textField.getText();
            int position = textField.getCursorPosition();
            text = text.substring(0, position) + "\n" + text.substring(position);
            textField.setText(text);
            textField.setCursorPosition(position + 1);
            Main.instance.getStage().setKeyboardFocus(textField);
        }
    }
}
