/*
 * The MIT License
 *
 * Copyright 2018 Raymond Buckley.
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
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.ray3k.skincomposer.Main;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 *
 * @author Raymond
 */
public class DialogDrawablesFilter extends Dialog {
    private Skin skin;
    private Main main;
    private DialogDrawables.FilterOptions filterOptions, appliedFilterOptions;
    
    public static enum Selection {
        APPLY, DISABLE, CANCEL
    }

    public DialogDrawablesFilter(DialogDrawables.FilterOptions filterOptions, Main main) {
        super("", main.getSkin(), "dialog");
        this.main = main;
        this.skin = main.getSkin();
        this.filterOptions = new DialogDrawables.FilterOptions();
        appliedFilterOptions = filterOptions;
        this.filterOptions.set(filterOptions);
        
        var table = getContentTable();
        table.pad(10);
        
        var label = new Label("Filter by type:", skin);
        table.add(label);
        
        table.row();
        var subTable = new Table();
        table.add(subTable);
        
        subTable.defaults().space(10).align(Align.left);
        var checkBox = new CheckBox("Texture", skin);
        checkBox.setChecked(this.filterOptions.texture);
        subTable.add(checkBox);
        checkBox.addListener(main.getHandListener());
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                DialogDrawablesFilter.this.filterOptions.texture = ((CheckBox) actor).isChecked();
            }
        });
        
        checkBox = new CheckBox("NinePatch", skin);
        checkBox.setChecked(this.filterOptions.ninePatch);
        subTable.add(checkBox);
        checkBox.addListener(main.getHandListener());
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                DialogDrawablesFilter.this.filterOptions.ninePatch = ((CheckBox) actor).isChecked();
            }
        });
        
        subTable.row();
        checkBox = new CheckBox("Tinted", skin);
        checkBox.setChecked(this.filterOptions.tinted);
        subTable.add(checkBox);
        checkBox.addListener(main.getHandListener());
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                DialogDrawablesFilter.this.filterOptions.tinted = ((CheckBox) actor).isChecked();
            }
        });
        
        checkBox = new CheckBox("Tiled", skin);
        checkBox.setChecked(this.filterOptions.tiled);
        subTable.add(checkBox);
        checkBox.addListener(main.getHandListener());
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                DialogDrawablesFilter.this.filterOptions.tiled = ((CheckBox) actor).isChecked();
            }
        });
        
        subTable.row();
        checkBox = new CheckBox("Custom", skin);
        checkBox.setChecked(this.filterOptions.custom);
        subTable.add(checkBox);
        checkBox.addListener(main.getHandListener());
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                DialogDrawablesFilter.this.filterOptions.custom = ((CheckBox) actor).isChecked();
            }
        });
        
        table.row();
        var image = new Image(skin, "welcome-separator");
        table.add(image).growX();
        
        table.row();
        label = new Label("Filter by name:", skin);
        table.add(label).padTop(10);
        
        table.row();
        checkBox = new CheckBox("Use regular expression", skin);
        checkBox.setName("regular-expression");
        checkBox.setChecked(this.filterOptions.regularExpression);
        table.add(checkBox);
        checkBox.addListener(main.getHandListener());
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                DialogDrawablesFilter.this.filterOptions.regularExpression = ((CheckBox) actor).isChecked();
                updateApplyButton();
            }
        });
        
        table.row();
        var textField = new TextField("", skin);
        textField.setName("text");
        textField.setText(this.filterOptions.name);
        table.add(textField);
        textField.addListener(main.getIbeamListener());
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                DialogDrawablesFilter.this.filterOptions.name = ((TextField) actor).getText();
                updateApplyButton();
            }
        });
        
        getButtonTable().pad(10);
        getButtonTable().defaults().space(5);
        var textButton = new TextButton("Apply", skin);
        textButton.setName("apply");
        button(textButton, Selection.APPLY);
        textButton.addListener(main.getHandListener());
        
        textButton = new TextButton("Disable", skin);
        button(textButton, Selection.DISABLE);
        textButton.addListener(main.getHandListener());
        
        textButton = new TextButton("Cancel", skin);
        button(textButton, Selection.CANCEL);
        textButton.addListener(main.getHandListener());
        
        key(Keys.ENTER, Selection.APPLY).key(Keys.ESCAPE, Selection.CANCEL);
        updateApplyButton();
    }

    @Override
    public Dialog show(Stage stage, Action action) {
        super.show(stage, action);
        
        
        stage.setKeyboardFocus(findActor("text"));
        ((TextField) findActor("text")).selectAll();
        
        return this;
    }
    
    private void updateApplyButton() {
        var disabled = true;
        CheckBox checkBox = findActor("regular-expression");
        TextField textField = findActor("text");
        if (!checkBox.isChecked()) {
            disabled = false;
        } else {
            try {
                Pattern.compile(textField.getText());
                disabled = false;
            } catch (PatternSyntaxException e) {}
        }
        
        TextButton textButton = findActor("apply");
        textButton.setDisabled(disabled);
    }

    @Override
    protected void result(Object object) {
        super.result(object);
        
        switch ((Selection) object) {
            case APPLY:
                appliedFilterOptions.set(filterOptions);
                appliedFilterOptions.applied = true;
                break;
            case DISABLE:
                appliedFilterOptions.applied = false;
                break;
            case CANCEL:
                break;
        }
        
        fire(new FilterEvent((Selection) object));
    }
    
    public static abstract class FilterListener implements EventListener {
        public abstract void applied();
        public abstract void disabled();
        public abstract void cancelled();
        
        @Override
        public boolean handle(Event event) {
            if (event instanceof FilterEvent) {
                var filterEvent = (FilterEvent) event;
                switch (filterEvent.selection) {
                    case APPLY:
                        applied();
                        break;
                    case DISABLE:
                        disabled();
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
    }
    
    public static class FilterEvent extends Event {
        public Selection selection;
        public FilterEvent(Selection selection) {
            this.selection = selection;
        }
    }
}