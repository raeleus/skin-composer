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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class BrowseField extends Table {
    private TextButton textButton;
    private Button button;
    private Label label;
    
    public BrowseField(String valueText, String labelText, BrowseFieldStyle style) {
        setTouchable(Touchable.enabled);
        
        if (labelText != null && !labelText.equals("")) {
            label = new Label(labelText, style.labelStyle);
            add(label).colspan(2);
        }
        
        row();
        if (valueText == null) valueText = "";
        textButton = new TextButton(valueText, style.mainButtonStyle);
        textButton.setTouchable(Touchable.disabled);
        textButton.getLabel().setAlignment(Align.left);
        textButton.getLabelCell().padLeft(5).padRight(5);
        add(textButton).minWidth(35.0f).prefWidth(35.0f).growX();
        
        button = new ImageButton(style.rightButtonStyle);
        
        button.setTouchable(Touchable.disabled);
        add(button).padLeft(5.0f).bottom();
        
        textButton.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                event.setBubbles(false);
                BrowseField.this.fire(new ChangeEvent());
            }
        });
        
        button.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                event.setBubbles(false);
//                BrowseField.this.fire(new ChangeEvent());
            }
        });
        
        addListener(new ClickListener() {
            @Override
            public void exit(InputEvent event, float x, float y, int pointer,
                    Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                textButton.getClickListener().exit(event, x, y, pointer, toActor);
                button.getClickListener().exit(event, x, y, pointer, toActor);
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer,
                    Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                textButton.getClickListener().enter(event, x, y, pointer, fromActor);
                button.getClickListener().enter(event, x, y, pointer, fromActor);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer,
                    int buttonNum) {
                super.touchUp(event, x, y, pointer, buttonNum);
                textButton.getClickListener().touchUp(event, x, y, pointer, buttonNum);
                button.getClickListener().touchUp(event, x, y, pointer, buttonNum);
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y,
                    int pointer) {
                super.touchDragged(event, x, y, pointer);
                textButton.getClickListener().touchDragged(event, x, y, pointer);
                button.getClickListener().touchDragged(event, x, y, pointer);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y,
                    int pointer, int buttonNum) {
                textButton.getClickListener().touchDown(event, x, y, pointer, buttonNum);
                button.getClickListener().touchDown(event, x, y, pointer, buttonNum);
                return super.touchDown(event, x, y, pointer, buttonNum);
            }
        });
    }

    public BrowseField(String valueText, String labelText, Skin skin, String style) {
        this(valueText, labelText, skin.get(style, BrowseFieldStyle.class));
    }
    
    public BrowseField(String valueText, String labelText, Skin skin) {
        this(valueText, labelText, skin, "default");
    }
    
    public TextButton getTextButton() {
        return textButton;
    }

    public Button getButton() {
        return button;
    }
    
    public void setValueText(String valueText) {
        textButton.setText(valueText);
    }
    
    static public class BrowseFieldStyle {
        public ImageButtonStyle rightButtonStyle;
        public TextButtonStyle mainButtonStyle;
        public LabelStyle labelStyle;

        public BrowseFieldStyle() {
            
        }
        
        public BrowseFieldStyle(ImageButtonStyle imageButtonStyle, TextButtonStyle textButtonStyle, LabelStyle labelStyle) {
            this.mainButtonStyle = textButtonStyle;
            this.rightButtonStyle = imageButtonStyle;
            this.labelStyle = labelStyle;
        }
        
        public BrowseFieldStyle(BrowseFieldStyle style) {
            mainButtonStyle = style.mainButtonStyle;
            rightButtonStyle = style.rightButtonStyle;
            labelStyle = style.labelStyle;
        }
    }
}
