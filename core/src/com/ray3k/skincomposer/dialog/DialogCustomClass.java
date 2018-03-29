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
package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.data.CustomClass;
import com.ray3k.skincomposer.data.DrawableData;

public class DialogCustomClass extends Dialog {
    private boolean customNameEntered;
    private final TextField classField, displayField;
    private final TextButton okButton;
    private Main main;
    private boolean allowSameName;
    private String originalFullyQualifiedName, originalDisplayName;
    
    public DialogCustomClass(Main main, String title, boolean allowSameName) {
        this(main, title, allowSameName, null, null, false);
    }
    
    public DialogCustomClass(Main main, String title, boolean allowSameName, String fullyQualifiedName, String displayName, boolean declareAfterUIclasses) {
        super(title, main.getSkin(), "bg");
        getTitleLabel().setAlignment(Align.center);
        
        this.main = main;
        
        if (fullyQualifiedName == null && displayName == null) {
            customNameEntered = false;
        } else {
            customNameEntered = true;
        }
        
        InputListener enterListener = new InputListener() {
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
            
        };
        
        this.allowSameName = allowSameName;
        originalFullyQualifiedName = fullyQualifiedName;
        originalDisplayName = displayName;
        
        Label label = new Label("What is the fully qualified name?", getSkin());
        getContentTable().add(label).pad(10.0f).padBottom(0.0f);
        
        getContentTable().row();
        label = new Label("(ex. com.badlogic.gdx.scenes.scene2d.ui.List$ListStyle)", getSkin());
        getContentTable().add(label).pad(10.0f).padTop(0.0f).padBottom(5.0f);
        
        getContentTable().row();
        classField = new TextField("", getSkin()) {
            @Override
            public void next(boolean up) {
                getStage().setKeyboardFocus(displayField);
                displayField.selectAll();
            }
        };
        classField.setText(fullyQualifiedName);
        classField.addCaptureListener(enterListener);
        classField.selectAll();
        classField.addListener(main.getIbeamListener());
        getContentTable().add(classField).growX().padLeft(10.0f).padRight(10.0f);
        
        getContentTable().row();
        label = new Label("What is the display name?", getSkin());
        getContentTable().add(label).pad(10.0f).padBottom(0.0f);
        getContentTable().row();
        label = new Label("(ex. List)", getSkin());
        getContentTable().add(label).pad(10.0f).padTop(0.0f).padBottom(5.0f);
        
        getContentTable().row();
        displayField = new TextField("", main.getSkin()) {
            @Override
            public void next(boolean up) {
                getStage().setKeyboardFocus(classField);
                classField.selectAll();
            }
        };
        displayField.setText(displayName);
        displayField.addCaptureListener(enterListener);
        displayField.addListener(main.getIbeamListener());
        getContentTable().add(displayField).growX().padLeft(10.0f).padRight(10.0f);
        
        getContentTable().row();
        CheckBox checkBox = new CheckBox("Declare after UI classes", getSkin());
        checkBox.setChecked(declareAfterUIclasses);
        checkBox.setName("declareAfterUIcheckBox");
        getContentTable().add(checkBox).padLeft(10.0f).padRight(10.0f).left().padTop(10.0f);
        
        getButtonTable().defaults().padBottom(10.0f).minWidth(50.0f);
        button("OK", true);
        button("Cancel", false).key(Keys.ESCAPE, false);
        
        okButton = (TextButton) getButtonTable().getCells().first().getActor();
        updateOkButton();
        
        getButtonTable().getCells().get(1).getActor().addListener(main.getHandListener());
        
        classField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if (!customNameEntered) {
                    if (!customNameEntered) {
                        String text = classField.getText();
                        text = text.replaceFirst(".*(\\.|\\$)", "");
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
        fire(new CustomClassEvent((boolean) object, classField.getText(), displayField.getText(), ((CheckBox) findActor("declareAfterUIcheckBox")).isChecked()));
    }
    
    private void updateOkButton() {
        if (classField.getText().matches("^.*[^\\.]$") && DrawableData.validate(displayField.getText())) {
            boolean buttonDisabled = false;
            
            if (!allowSameName || !classField.getText().equals(originalFullyQualifiedName) || !displayField.getText().equals(originalDisplayName)) {
                for (CustomClass otherClass : main.getJsonData().getCustomClasses()) {
                    if (!allowSameName) {
                        if ((otherClass.getDisplayName().equals(displayField.getText()))
                                || otherClass.getFullyQualifiedName().equals(classField.getText())) {
                                buttonDisabled = true;
                            break;
                        }
                    } else {
                        if (otherClass.getDisplayName().equals(displayField.getText()) && !otherClass.getDisplayName().equals(originalDisplayName)) {
                            buttonDisabled = true;
                            break;
                        } else if (otherClass.getFullyQualifiedName().equals(classField.getText()) && !otherClass.getFullyQualifiedName().equals(originalFullyQualifiedName)) {
                            buttonDisabled = true;
                            break;
                        }
                    }
                }
                
                for (Class clazz : Main.STYLE_CLASSES) {
                    if (classField.getText().equals(clazz.getName())) {
                        buttonDisabled = true;
                    }
                }
                
                for (Class clazz : Main.BASIC_CLASSES) {
                    if (displayField.getText().equals(clazz.getSimpleName())) {
                        buttonDisabled = true;
                    }
                }
            }
            
            okButton.setDisabled(buttonDisabled);
            if (!buttonDisabled && !okButton.getListeners().contains(main.getHandListener(), true)) {
                okButton.addListener(main.getHandListener());
            }
        } else {
            okButton.setDisabled(true);
            if (okButton.getListeners().contains(main.getHandListener(), true)) {
                okButton.removeListener(main.getHandListener());
            }
        }
    }
    
    private static class CustomClassEvent extends Event {
        boolean result;
        String fullyQualifiedName;
        String displayName;
        boolean declareAfterUIclasses;
        
        public CustomClassEvent(boolean result, String fullyQualifiedName, String displayName, boolean declareAfterUIclasses) {
            this.result = result;
            this.fullyQualifiedName = fullyQualifiedName;
            this.displayName = displayName;
            this.declareAfterUIclasses = declareAfterUIclasses;
        }
    }
    
    public static abstract class CustomClassListener implements EventListener {
        @Override
        public boolean handle(Event event) {
            if (event instanceof CustomClassEvent) {
                CustomClassEvent newClassEvent = (CustomClassEvent) event;
                if (newClassEvent.result) {
                    newClassEntered(newClassEvent.fullyQualifiedName, newClassEvent.displayName, newClassEvent.declareAfterUIclasses);
                } else {
                    cancelled();
                }
            }
            return false;
        }
        
        public abstract void newClassEntered(String fullyQualifiedName, String displayName, boolean declareAfterUIclasses);
        
        public abstract void cancelled();
    }
}