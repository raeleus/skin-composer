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
import com.badlogic.gdx.utils.Align;
import com.ray3k.skincomposer.dialog.DialogDrawables.FilterOptions;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static com.ray3k.skincomposer.Main.*;

/**
 *
 * @author Raymond
 */
public class DialogDrawablesFilter extends Dialog {
    private DialogDrawables.FilterOptions filterOptions, appliedFilterOptions, defaultFilterOptions;
    
    public static enum Selection {
        APPLY, RESET, CANCEL
    }

    public DialogDrawablesFilter(DialogDrawables.FilterOptions filterOptions) {
        super("", skin, "dialog");
        this.filterOptions = new FilterOptions();
        defaultFilterOptions = new FilterOptions();
        appliedFilterOptions = filterOptions;
        this.filterOptions.set(filterOptions);
        
        populate();
    }
    
    private void populate() {
        var table = getContentTable();
        table.clearChildren();
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
        checkBox.addListener(handListener);
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                DialogDrawablesFilter.this.filterOptions.texture = ((CheckBox) actor).isChecked();
            }
        });
    
        checkBox = new CheckBox("NinePatch", skin);
        checkBox.setChecked(this.filterOptions.ninePatch);
        subTable.add(checkBox);
        checkBox.addListener(handListener);
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
        checkBox.addListener(handListener);
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                DialogDrawablesFilter.this.filterOptions.tinted = ((CheckBox) actor).isChecked();
            }
        });
    
        checkBox = new CheckBox("Tiled", skin);
        checkBox.setChecked(this.filterOptions.tiled);
        subTable.add(checkBox);
        checkBox.addListener(handListener);
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                DialogDrawablesFilter.this.filterOptions.tiled = ((CheckBox) actor).isChecked();
            }
        });
    
        subTable.row();
        checkBox = new CheckBox("TenPatch", skin);
        checkBox.setChecked(this.filterOptions.tenPatch);
        subTable.add(checkBox);
        checkBox.addListener(handListener);
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                DialogDrawablesFilter.this.filterOptions.tenPatch = ((CheckBox) actor).isChecked();
            }
        });
        
        checkBox = new CheckBox("Custom", skin);
        checkBox.setChecked(this.filterOptions.custom);
        subTable.add(checkBox);
        checkBox.addListener(handListener);
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                DialogDrawablesFilter.this.filterOptions.custom = ((CheckBox) actor).isChecked();
            }
        });
    
        subTable.row();
        checkBox = new CheckBox("Pixel", skin);
        checkBox.setChecked(this.filterOptions.pixel);
        subTable.add(checkBox);
        checkBox.addListener(handListener);
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                DialogDrawablesFilter.this.filterOptions.pixel = ((CheckBox) actor).isChecked();
            }
        });
        
        checkBox = new CheckBox("Font", skin);
        checkBox.setChecked(this.filterOptions.font);
        subTable.add(checkBox);
        checkBox.addListener(handListener);
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                DialogDrawablesFilter.this.filterOptions.font = ((CheckBox) actor).isChecked();
            }
        });
    
        subTable.row();
        checkBox = new CheckBox("TinyVG", skin);
        checkBox.setChecked(this.filterOptions.tvg);
        subTable.add(checkBox);
        checkBox.addListener(handListener);
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                DialogDrawablesFilter.this.filterOptions.tvg = ((CheckBox) actor).isChecked();
            }
        });
        
        checkBox = new CheckBox("Hidden", skin);
        checkBox.setChecked(this.filterOptions.hidden);
        subTable.add(checkBox);
        checkBox.addListener(handListener);
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                DialogDrawablesFilter.this.filterOptions.hidden = ((CheckBox) actor).isChecked();
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
        checkBox.addListener(handListener);
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
        textField.addListener(ibeamListener);
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
        textButton.addListener(handListener);
    
        textButton = new TextButton("Reset", skin);
        button(textButton, Selection.RESET);
        textButton.addListener(handListener);
    
        textButton = new TextButton("Cancel", skin);
        button(textButton, Selection.CANCEL);
        textButton.addListener(handListener);
    
        key(Keys.ENTER, Selection.APPLY).key(Keys.NUMPAD_ENTER, Selection.APPLY).key(Keys.ESCAPE, Selection.CANCEL);
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
                break;
            case RESET:
                filterOptions.set(defaultFilterOptions);
                populate();
                appliedFilterOptions.set(defaultFilterOptions);
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
                    case RESET:
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