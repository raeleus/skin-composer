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

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class BrowseField extends Table {
    private TextField textField;
    private Button button;
    
    public BrowseField(String valueText, BrowseFieldStyle style) {
        if (valueText == null) valueText = "";
        textField = new TextField(valueText, style.textFieldStyle);
        textField.setAlignment(Align.center);
        textField.setFocusTraversal(false);
        textField.setDisabled(true);
        textField.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                fire(new ChangeListener.ChangeEvent());
            }
        });
        add(textField).minWidth(35.0f).prefWidth(35.0f).growX();
        
        if (style.textButtonStyle != null) {
            button = new TextButton("...", style.textButtonStyle);
        } else {
            button = new ImageButton(style.imageButtonStyle);
        }
        
        add(button).padLeft(5.0f).bottom();
    }

    public TextField getTextField() {
        return textField;
    }

    public Button getButton() {
        return button;
    }
    
    public void setValueText(String valueText) {
        textField.setText(valueText);
    }
    
    static public class BrowseFieldStyle {
        public TextButtonStyle textButtonStyle;
        public ImageButtonStyle imageButtonStyle;
        public TextFieldStyle textFieldStyle;
        public LabelStyle labelStyle;

        public BrowseFieldStyle(TextButtonStyle textButtonStyle, TextFieldStyle textFieldStyle, LabelStyle labelStyle) {
            this.textButtonStyle = textButtonStyle;
            this.textFieldStyle = textFieldStyle;
            this.labelStyle = labelStyle;
            imageButtonStyle = null;
        }

        public BrowseFieldStyle(ImageButtonStyle imageButtonStyle, TextFieldStyle textFieldStyle, LabelStyle labelStyle) {
            textButtonStyle = null;
            this.textFieldStyle = textFieldStyle;
            this.imageButtonStyle = imageButtonStyle;
            this.labelStyle = labelStyle;
        }
        
        public BrowseFieldStyle(BrowseFieldStyle style) {
            textButtonStyle = style.textButtonStyle;
            textFieldStyle = style.textFieldStyle;
            imageButtonStyle = style.imageButtonStyle;
            labelStyle = style.labelStyle;
        }
    }
}
