/*******************************************************************************
 * MIT License
 *
 * Copyright (c) 2017 Raymond Buckley
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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.data.CustomProperty.PropertyType;

public class DialogCustomProperty extends Dialog {
    private final TextField classField;
    private final TextButton okButton;
    private final SelectBox<PropertyType> propertyTypeBox;
    private Main main;
    
    public DialogCustomProperty(Main main, String title) {
        this(main, title, null, PropertyType.NONE);
    }
    
    public DialogCustomProperty(Main main, String title, String propertyName, PropertyType propertyType) {
        super(title, main.getSkin(), "bg");
        getTitleLabel().setAlignment(Align.center);
        
        this.main = main;
        
        Label label = new Label("What is the property name?", getSkin());
        getContentTable().add(label).pad(10.0f).padBottom(0.0f);
        
        getContentTable().row();
        label = new Label("(ex. up)", getSkin());
        getContentTable().add(label).pad(10.0f).padTop(0.0f).padBottom(5.0f);
        
        getContentTable().row();
        classField = new TextField("", getSkin());
        classField.setFocusTraversal(false);
        classField.setText(propertyName);
        classField.selectAll();
        classField.addListener(main.getIbeamListener());
        getContentTable().add(classField).growX().padLeft(10.0f).padRight(10.0f);
        
        getContentTable().row();
        label = new Label("What is the property type?", getSkin());
        getContentTable().add(label).pad(10.0f).padBottom(5.0f);

        getContentTable().row();
        propertyTypeBox = new SelectBox<>(getSkin());
        propertyTypeBox.setItems(PropertyType.TEXT, PropertyType.FLOAT, PropertyType.FONT, PropertyType.DRAWABLE, PropertyType.COLOR);
        propertyTypeBox.addListener(main.getHandListener());
        if (propertyType != null && propertyType != PropertyType.NONE) {
            propertyTypeBox.setSelected(propertyType);
        }
        getContentTable().add(propertyTypeBox).growX().padLeft(10.0f).padRight(10.0f);

        getButtonTable().defaults().padBottom(10.0f).minWidth(50.0f);
        button("OK", true).key(Keys.ENTER, true);
        button("Cancel", false).key(Keys.ESCAPE, false);
        
        okButton = (TextButton) getButtonTable().getCells().first().getActor();
        okButton.setDisabled(true);
        
        getButtonTable().getCells().get(1).getActor().addListener(main.getHandListener());
        
        classField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {                
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
        
        fire(new CustomPropertyEvent((boolean) object, classField.getText(), propertyTypeBox.getSelected()));
    }
    
    private void updateOkButton() {
        if (validate(classField.getText())) {
            okButton.setDisabled(false);
            if (!okButton.getListeners().contains(main.getHandListener(), true)) {
                okButton.addListener(main.getHandListener());
            }
        } else {
            okButton.setDisabled(true);
            if (okButton.getListeners().contains(main.getHandListener(), true)) {
                okButton.removeListener(main.getHandListener());
            }
        }
    }
    
    private static class CustomPropertyEvent extends Event {
        boolean result;
        String propertyName;
        PropertyType propertyType;
        
        public CustomPropertyEvent(boolean result, String propertyName, PropertyType propertyType) {
            this.result = result;
            this.propertyName = propertyName;
            this.propertyType = propertyType;
        }
    }
    
    public static abstract class CustomStylePropertyListener implements EventListener {
        @Override
        public boolean handle(Event event) {
            if (event instanceof CustomPropertyEvent) {
                CustomPropertyEvent newPropertyEvent = (CustomPropertyEvent) event;
                if (newPropertyEvent.result) {
                    newPropertyEntered(newPropertyEvent.propertyName, newPropertyEvent.propertyType);
                } else {
                    cancelled();
                }
            }
            return false;
        }
        
        public abstract void newPropertyEntered(String propertyName, PropertyType propertyType);
        
        public abstract void cancelled();
    }
    
    public static boolean validate(String name) {
        return name != null && !name.matches("^\\d.*|^-.*|.*\\s.*|.*[^a-zA-Z\\d\\s-_].*|^$");
    }
}
