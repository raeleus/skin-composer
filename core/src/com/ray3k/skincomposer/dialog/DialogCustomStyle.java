/*******************************************************************************
 * MIT License
 *
 * Copyright (c) 2024 Raymond Buckley
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

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.ray3k.skincomposer.data.CustomClass;
import com.ray3k.skincomposer.data.CustomStyle;
import com.ray3k.skincomposer.data.StyleData;

import static com.ray3k.skincomposer.Main.*;

public class DialogCustomStyle extends Dialog {
    private final TextField nameField;
    private final TextButton okButton;
    private boolean allowSameName;
    private String originalName;
    
    public DialogCustomStyle(String title, boolean allowSameName) {
        this(title, allowSameName, null);
    }
    
    public DialogCustomStyle(String title, boolean allowSameName, String name) {
        super(title, skin, "bg");
        getTitleLabel().setAlignment(Align.center);
        
        this.allowSameName = allowSameName;
        originalName = name;
        
        Label label = new Label("What is the name of the new style?", getSkin());
        getContentTable().add(label).pad(10.0f).padBottom(5.0f);
        
        getContentTable().row();
        nameField = new TextField("", skin);
        nameField.setFocusTraversal(false);
        nameField.setText(name);
        nameField.addCaptureListener(new InputListener() {
            @Override
            public boolean keyTyped(InputEvent event, char character) {
                if (character == '\n') {
                    event.stop();
                    
                    if (!okButton.isDisabled()) {
                        result(true);
                        hide();
                    }
                }
                return false;
            }
            
        });
        nameField.addListener(ibeamListener);
        getContentTable().add(nameField).growX().padLeft(10.0f).padRight(10.0f);
        
        getButtonTable().defaults().padBottom(10.0f).minWidth(50.0f);
        button("OK", true);
        button("Cancel", false).key(Input.Keys.ESCAPE, false);
        
        okButton = (TextButton) getButtonTable().getCells().first().getActor();
        updateOkButton();
        
        getButtonTable().getCells().get(1).getActor().addListener(handListener);
        
        nameField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                updateOkButton();
            }
        });
    }

    @Override
    public Dialog show(Stage stage) {
        Dialog dialog = super.show(stage);
        
        stage.setKeyboardFocus(nameField);
        nameField.selectAll();
        
        return dialog;
    }

    @Override
    protected void result(Object object) {
        super.result(object);
        
        fire(new CustomStyleEvent((boolean) object, nameField.getText()));
    }
    
    private void updateOkButton() {
        if (StyleData.validate(nameField.getText())) {
            boolean buttonDisabled = false;
            
            CustomClass customClass = (CustomClass) rootTable.getClassSelectBox().getSelected();
            
            if (!allowSameName || !nameField.getText().equals(originalName)) {
                for (CustomStyle otherStyle : customClass.getStyles()) {
                    if (otherStyle.getName().equals(nameField.getText())) {
                            buttonDisabled = true;
                        break;
                    }
                }
            }
            
            okButton.setDisabled(buttonDisabled);
            if (!buttonDisabled && !okButton.getListeners().contains(handListener, true)) {
                okButton.addListener(handListener);
            }
        } else {
            okButton.setDisabled(true);
            if (okButton.getListeners().contains(handListener, true)) {
                okButton.removeListener(handListener);
            }
        }
    }
    
    private static class CustomStyleEvent extends Event {
        boolean result;
        String name;
        
        public CustomStyleEvent(boolean result, String displayName) {
            this.result = result;
            this.name = displayName;
        }
    }
    
    public static abstract class CustomStyleListener implements EventListener {
        @Override
        public boolean handle(Event event) {
            if (event instanceof CustomStyleEvent) {
                CustomStyleEvent newStyleEvent = (CustomStyleEvent) event;
                if (newStyleEvent.result) {
                    newStyleEntered(newStyleEvent.name);
                } else {
                    cancelled();
                }
            }
            return false;
        }
        
        public abstract void newStyleEntered(String displayName);
        
        public abstract void cancelled();
    }
}
