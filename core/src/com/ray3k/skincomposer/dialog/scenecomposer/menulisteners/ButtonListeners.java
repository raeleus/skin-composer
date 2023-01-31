package com.ray3k.skincomposer.dialog.scenecomposer.menulisteners;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.data.StyleProperty;
import com.ray3k.skincomposer.dialog.DialogListener;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerEvents;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimActor;
import com.ray3k.skincomposer.dialog.scenecomposer.StyleSelectorPopTable;
import com.ray3k.stripe.PopTableClickListener;
import com.ray3k.stripe.Spinner;

import static com.ray3k.skincomposer.dialog.scenecomposer.menulisteners.ListenersUtils.TEXT_FIELD_WIDTH;
import static com.ray3k.skincomposer.Main.*;

public class ButtonListeners {
    public static EventListener buttonCheckedListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simButton = (DialogSceneComposerModel.SimButton) simActor;
        var popTableClickListener = new PopTableClickListener(skin) {
            {
                getPopTable().key(Keys.ESCAPE, popTable::hide);
            }
            
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Checked:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var textButton = new TextButton(simButton.checked ? "TRUE" : "FALSE", skin, "scene-small");
                textButton.setChecked(simButton.checked);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(handListener);
                textButton.addListener((Main.makeTooltip("Whether the button is checked initially.", tooltipManager, skin, "scene")));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        events.buttonChecked(textButton.isChecked());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener buttonNameListener(final DialogSceneComposer dialogSceneComposer) {
        var simButton = (DialogSceneComposerModel.SimButton) dialogSceneComposer.simActor;
        var textField = new TextField("", skin, "scene");
        var popTableClickListener = new PopTableClickListener(skin) {
            {
                getPopTable().key(Keys.ESCAPE, popTable::hide);
            }
            
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                dialogSceneComposer.getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
                
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Name:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setText(simButton.name);
                popTable.add(textField).minWidth(TEXT_FIELD_WIDTH);
                textField.addListener(ibeamListener);
                textField.addListener((Main.makeTooltip("The name of the button to allow for convenient searching via Group#findActor().", tooltipManager, skin, "scene")));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        dialogSceneComposer.events.buttonName(textField.getText());
                    }
                });
                textField.addListener(new InputListener() {
                    @Override
                    public boolean keyDown(InputEvent event, int keycode) {
                        if (keycode == Input.Keys.ENTER || keycode == Keys.NUMPAD_ENTER) {
                            popTable.hide();
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
                
                dialogSceneComposer.getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener buttonStyleListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simButton = (DialogSceneComposerModel.SimButton) simActor;
        var popTableClickListener = new StyleSelectorPopTable(Button.class, simButton.style == null ? "default" : simButton.style.name) {
            @Override
            public void accepted(StyleData styleData) {
                events.buttonStyle(styleData);
            }
        };
        
        return popTableClickListener;
    }
    
    public static EventListener buttonDisabledListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simButton = (DialogSceneComposerModel.SimButton) simActor;
        var popTableClickListener = new PopTableClickListener(skin) {
            {
                getPopTable().key(Keys.ESCAPE, popTable::hide);
            }
            
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Disabled:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var textButton = new TextButton(simButton.disabled ? "TRUE" : "FALSE", skin, "scene-small");
                textButton.setChecked(simButton.disabled);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(handListener);
                textButton.addListener((Main.makeTooltip("Whether the button is disabled initially.", tooltipManager, skin, "scene")));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        events.buttonDisabled(textButton.isChecked());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener buttonColorListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simButton = (DialogSceneComposerModel.SimButton) simActor;
        var popTableClickListener = new PopTableClickListener(skin) {
            {
                getPopTable().key(Keys.ESCAPE, popTable::hide);
            }
            
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Color:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var imageButton = new ImageButton(skin, "scene-color");
                imageButton.getImage().setColor(simButton.color == null ? Color.WHITE : simButton.color.color);
                popTable.add(imageButton).minWidth(100);
                imageButton.addListener(handListener);
                imageButton.addListener((Main.makeTooltip("Select the color of the button.", tooltipManager, skin, "scene")));
                imageButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        popTable.hide();
                        dialogFactory.showDialogColors(new StyleProperty(), (colorData, pressedCancel) -> {
                            if (!pressedCancel) {
                                events.buttonColor(colorData);
                            }
                        }, new DialogListener() {
                            @Override
                            public void opened() {
                            
                            }
                            
                            @Override
                            public void closed() {
                            
                            }
                        });
                    }
                });
                
                popTable.row();
                label = new Label(simButton.color == null ? "white" : simButton.color.getName(), skin, "scene-label-colored");
                popTable.add(label);
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener buttonPaddingListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simButton = (DialogSceneComposerModel.SimButton) simActor;
        var popTableClickListener = new PopTableClickListener(skin) {
            {
                getPopTable().key(Keys.ENTER, popTable::hide);
                getPopTable().key(Keys.NUMPAD_ENTER, popTable::hide);
                getPopTable().key(Keys.ESCAPE, popTable::hide);
            }
            
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var changeListener = new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        Spinner padLeft = popTable.findActor("pad-left");
                        Spinner padRight = popTable.findActor("pad-right");
                        Spinner padTop = popTable.findActor("pad-top");
                        Spinner padBottom = popTable.findActor("pad-bottom");
                        events.buttonPadding((float) padLeft.getValue(), (float) padRight.getValue(), (float) padTop.getValue(), (float) padBottom.getValue());
                    }
                };
                
                var label = new Label("Padding:", skin, "scene-label-colored");
                popTable.add(label).colspan(2);
                
                popTable.row();
                popTable.defaults().right().spaceRight(5);
                label = new Label("Left:", skin, "scene-label-colored");
                popTable.add(label);
                
                var spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("pad-left");
                spinner.setValue(simButton.padLeft);
                popTable.add(spinner);
                spinner.getTextField().addListener(ibeamListener);
                spinner.getButtonMinus().addListener(handListener);
                spinner.getButtonPlus().addListener(handListener);
                spinner.addListener((Main.makeTooltip("The padding on the left of the contents.", tooltipManager, skin, "scene")));
                spinner.addListener(changeListener);
                
                popTable.row();
                label = new Label("Right:", skin, "scene-label-colored");
                popTable.add(label);
                
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("pad-right");
                spinner.setValue(simButton.padRight);
                popTable.add(spinner);
                spinner.getTextField().addListener(ibeamListener);
                spinner.getButtonMinus().addListener(handListener);
                spinner.getButtonPlus().addListener(handListener);
                spinner.addListener((Main.makeTooltip("The padding on the right of the contents.", tooltipManager, skin, "scene")));
                spinner.addListener(changeListener);
                
                popTable.row();
                label = new Label("Top:", skin, "scene-label-colored");
                popTable.add(label);
                
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("pad-top");
                spinner.setValue(simButton.padTop);
                popTable.add(spinner);
                spinner.getTextField().addListener(ibeamListener);
                spinner.getButtonMinus().addListener(handListener);
                spinner.getButtonPlus().addListener(handListener);
                spinner.addListener((Main.makeTooltip("The padding on the top of the contents.", tooltipManager, skin, "scene")));
                spinner.addListener(changeListener);
                
                popTable.row();
                label = new Label("Bottom:", skin, "scene-label-colored");
                popTable.add(label);
                
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("pad-bottom");
                spinner.setValue(simButton.padBottom);
                popTable.add(spinner);
                spinner.getTextField().addListener(ibeamListener);
                spinner.getButtonMinus().addListener(handListener);
                spinner.getButtonPlus().addListener(handListener);
                spinner.addListener((Main.makeTooltip("The padding on the bottom of the contents.", tooltipManager, skin, "scene")));
                spinner.addListener(changeListener);
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
}
