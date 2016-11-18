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
package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.IbeamListener;

public class DialogNewProperty extends Dialog {
    private TextField propertyField;
    private SelectBox<String> propertyBox;
    private Array<NewPropertyListener> listeners;
    private TextButton okButton;
    
    public DialogNewProperty(Skin skin) {
        this(skin, "default");
    }
    public DialogNewProperty(Skin skin, String windowStyleName) {
        super("New Property...", skin, windowStyleName);
        
        listeners = new Array<>();
        
        text("Property Name");
        
        getContentTable().row();
        text("ex. ImageUp");
        
        getContentTable().row();
        propertyField = new TextField("", skin);
        propertyField.addListener(IbeamListener.get());
        getContentTable().add(propertyField);
        propertyField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                updateOkButton();
            }
        });
        
        
        getContentTable().row();
        text("Property Type");
        
        getContentTable().row();
        propertyBox = new SelectBox<>(skin);
        propertyBox.setItems("float", "text", "drawable", "color");
        getContentTable().add(propertyBox);
        
        button("OK", true).key(Keys.ENTER, true);
        button("Cancel", false).key(Keys.ESCAPE, false);
    }
    
    @Override
    public Dialog show(Stage stage) {
        Dialog dialog = super.show(stage);
        
        stage.setKeyboardFocus(propertyField);
        
        return dialog;
    }

    @Override
    protected void result(Object object) {
        super.result(object);
        
        if ((Boolean) object) {
            for (NewPropertyListener listener : listeners) {
                Class clazz;
                String value = propertyBox.getSelected();
                switch (value) {
                    case "float":
                        clazz = Float.class;
                        break;
                    case "text":
                        clazz = String.class;
                        break;
                    case "drawable":
                        clazz = Drawable.class;
                        break;
                    case "color":
                        clazz = Color.class;
                        break;
                    default:
                        clazz = null;
                }
                listener.approved(propertyField.getText(), clazz);
            }
        } else {
            for (NewPropertyListener listener : listeners) {
                listener.cancelled();
            }
        }
    }
    
    private void updateOkButton() {
        if (!propertyField.getText().equals("")) {
            okButton.setDisabled(false);
        }
    }
    
    public interface NewPropertyListener {
        /**
         * 
         * @param propertyName
         * @param propertyType Can be of the following types: Drawable, Color, Float, String.
         */
        public void approved(String propertyName, Class propertyType);
        public void cancelled();
    }
    
    public void addNewPropertyListener(NewPropertyListener listener) {
        listeners.add(listener);
    }
}
