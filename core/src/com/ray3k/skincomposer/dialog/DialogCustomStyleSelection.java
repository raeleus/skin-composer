/*
 * The MIT License
 *
 * Copyright (c) 2024 Raymond Buckley.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.data.CustomClass;

import static com.ray3k.skincomposer.Main.*;

/**
 *
 * @author Raymond
 */
public class DialogCustomStyleSelection extends Dialog {
    
    public DialogCustomStyleSelection() {
        super("Select a style...", skin, "bg");
        
        populate();
    }
    
    private void populate() {
        var t = getContentTable();
        t.pad(5);
        
        var table = new Table();
        t.add(table).expandY();
        
        table.defaults().space(5);
        var label = new Label("Class:", getSkin());
        table.add(label).right();
        
        var selectBox = new SelectBox<String>(getSkin());
        selectBox.setName("classes");
        table.add(selectBox).growX().minWidth(100);
        selectBox.addListener(handListener);
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                var selectBox = (SelectBox<String>) actor;
                populateStyles(selectBox.getSelectedIndex());
            }
        });
        
        table.row();
        label = new Label("Style:", getSkin());
        table.add(label).right();
        
        selectBox = new SelectBox<>(getSkin());
        selectBox.setName("styles");
        table.add(selectBox).growX().minWidth(100);
        selectBox.addListener(handListener);
        
        populateClasses();
        populateStyles(0);
        
        t = getButtonTable();
        t.pad(5);
        
        t.defaults().space(5).minWidth(75);
        var textButton = new TextButton("OK", getSkin());
        button(textButton, true);
        textButton.addListener(handListener);
        
        textButton = new TextButton("Cancel", getSkin());
        button(textButton, false);
        textButton.addListener(handListener);
        
        key(Keys.ENTER, true).key(Keys.NUMPAD_ENTER, true).key(Keys.ESCAPE, false);
    }
    
    private void populateClasses() {
        var classNames = new Array<String>();
        for (Class clazz : Main.BASIC_CLASSES) {
            classNames.add(clazz.getSimpleName());
        }
        for (CustomClass clazz : jsonData.getCustomClasses()) {
            classNames.add(clazz.getDisplayName());
        }
        
        SelectBox<String> selectBox = findActor("classes");
        selectBox.setItems(classNames);
    }
    
    private void populateStyles(int index) {
        var styleNames = new Array<String>();
        if (index < Main.BASIC_CLASSES.length) {
            var styles = jsonData.getClassStyleMap().get(Main.BASIC_CLASSES[index]);
            for (var style : styles) {
                styleNames.add(style.name);
            }
        } else {
            var styles = jsonData.getCustomClasses().get(index - Main.BASIC_CLASSES.length).getStyles();
            for (var style : styles) {
                styleNames.add(style.getName());
            }
        }
        
        SelectBox<String> selectBox = findActor("styles");
        selectBox.setItems(styleNames);
    }

    @Override
    public Dialog show(Stage stage, Action action) {
        fire(new DialogEvent(DialogEvent.Type.OPEN));
        return super.show(stage, action);
    }

    @Override
    public boolean remove() {
        fire(new DialogEvent(DialogEvent.Type.CLOSE));
        return super.remove();
    }

    @Override
    protected void result(Object object) {
        if ((boolean) object) {
            var style = ((SelectBox<String>) findActor("styles")).getSelected();
            fire(new DialogCustomStyleSelectionEvent(DialogCustomStyleSelectionEvent.Type.CONFIRM, style));
        } else {
            fire(new DialogCustomStyleSelectionEvent(DialogCustomStyleSelectionEvent.Type.CANCEL));
        }
    }
    
    public static class DialogCustomStyleSelectionEvent extends Event {
        public static enum Type {
            CONFIRM, CANCEL
        }
        
        private Type type;
        private String style;

        public DialogCustomStyleSelectionEvent(Type type) {
            this.type = type;
        }

        public DialogCustomStyleSelectionEvent(Type type, String style) {
            this.type = type;
            this.style = style;
        }
    }
    
    public abstract static class DialogCustomStyleSelectionListener implements EventListener {
        @Override
        public boolean handle(Event event) {
            if (event instanceof DialogCustomStyleSelectionEvent) {
                var dialogEvent = (DialogCustomStyleSelectionEvent) event;
                switch (dialogEvent.type) {
                    case CONFIRM:
                        confirmed(dialogEvent.style);
                        break;
                    case CANCEL:
                        cancelled();
                        break;
                }
                return true;
            } else {
                return false;
            }
        }
        
        public abstract void confirmed(String style);
        
        public abstract void cancelled();
    }
}
