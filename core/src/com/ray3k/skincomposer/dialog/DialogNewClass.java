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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.IbeamListener;

public class DialogNewClass extends Dialog {
    private boolean customNameEntered;
    private TextField classField, displayField;
    private Array<NewClassListener> listeners;
    private TextButton okButton;
    
    public DialogNewClass(Skin skin) {
        this(skin, "default");
    }
    public DialogNewClass(Skin skin, String windowStyleName) {
        super("New Class...", skin, windowStyleName);
        
        customNameEntered = false;
        listeners = new Array<>();
        
        text("Full Class Name:");
        getContentTable().row();
        text("ex. com.badlogic.gdx.scenes.scene2d.ui.List$ListStyle");
        
        getContentTable().row();
        classField = new TextField("", skin);
        classField.addListener(IbeamListener.get());
        getContentTable().add(classField);
        
        getContentTable().row();
        text("Display Name:");
        getContentTable().row();
        text("ex. List");
        
        getContentTable().row();
        displayField = new TextField("", skin);
        displayField.addListener(IbeamListener.get());
        getContentTable().add(displayField);
        
        button("OK", true).key(Keys.ENTER, true);
        button("Cancel", false).key(Keys.ESCAPE, false);
        
        okButton = (TextButton) getButtonTable().getCells().first().getActor();
        okButton.setDisabled(true);
        
        classField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if (!customNameEntered) {
                    if (!customNameEntered) {
                        String text = classField.getText();
                        text = text.replaceFirst(".*\\.", "");
                        displayField.setText(text);
                    }
                }
                
                updateOkButton();
            }
        });
        
        displayField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                customNameEntered = true;
                updateOkButton();
            }
        });
    }

    @Override
    public Dialog show(Stage stage) {
        Dialog dialog = super.show(stage);
        
        stage.setKeyboardFocus(classField);
        
        return dialog;
    }

    @Override
    protected void result(Object object) {
        super.result(object);
        
        if ((boolean) object) {
            for (NewClassListener listener : listeners) {
                listener.approved(classField.getText(), displayField.getText());
            }
        } else {
            for (NewClassListener listener : listeners) {
                listener.cancelled();
            }
        }
    }
    
    private void updateOkButton() {
        if (classField.getText().matches("^.*[^\\.]$") && !displayField.getText().equals("")) {
            okButton.setDisabled(false);
        } else {
            okButton.setDisabled(true);
        }
    }
    
    public interface NewClassListener {
        public void approved(String className, String displayName);
        public void cancelled();
    }
    
    public void addNewClassListener(NewClassListener listener) {
        listeners.add(listener);
    }
}
