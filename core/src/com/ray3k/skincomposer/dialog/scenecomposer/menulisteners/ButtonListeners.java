package com.ray3k.skincomposer.dialog.scenecomposer.menulisteners;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.ray3k.skincomposer.PopTable;
import com.ray3k.skincomposer.Spinner;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.data.StyleProperty;
import com.ray3k.skincomposer.dialog.DialogListener;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerEvents;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimActor;
import com.ray3k.skincomposer.dialog.scenecomposer.StyleSelectorPopTable;

public class ButtonListeners {
    public static EventListener buttonCheckedListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simButton = (DialogSceneComposerModel.SimButton) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(DialogSceneComposer.skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Checked:", DialogSceneComposer.skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var textButton = new TextButton(simButton.checked ? "TRUE" : "FALSE", DialogSceneComposer.skin, "scene-small");
                textButton.setChecked(simButton.checked);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(DialogSceneComposer.main.getHandListener());
                textButton.addListener(new TextTooltip("Whether the button is checked initially.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
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
        var textField = new TextField("", DialogSceneComposer.skin, "scene");
        var popTableClickListener = new PopTable.PopTableClickListener(DialogSceneComposer.skin) {
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
                
                var label = new Label("Name:", DialogSceneComposer.skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setText(simButton.name);
                popTable.add(textField).minWidth(150);
                textField.addListener(DialogSceneComposer.main.getIbeamListener());
                textField.addListener(new TextTooltip("The name of the button to allow for convenient searching via Group#findActor().", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        dialogSceneComposer.events.buttonName(textField.getText());
                    }
                });
                textField.addListener(new InputListener() {
                    @Override
                    public boolean keyDown(InputEvent event, int keycode) {
                        if (keycode == Input.Keys.ENTER) {
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
        var popTableClickListener = new PopTable.PopTableClickListener(DialogSceneComposer.skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Disabled:", DialogSceneComposer.skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var textButton = new TextButton(simButton.disabled ? "TRUE" : "FALSE", DialogSceneComposer.skin, "scene-small");
                textButton.setChecked(simButton.disabled);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(DialogSceneComposer.main.getHandListener());
                textButton.addListener(new TextTooltip("Whether the button is disabled initially.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
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
        var popTableClickListener = new PopTable.PopTableClickListener(DialogSceneComposer.skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Color:", DialogSceneComposer.skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var imageButton = new ImageButton(DialogSceneComposer.skin, "scene-color");
                imageButton.getImage().setColor(simButton.color == null ? Color.WHITE : simButton.color.color);
                popTable.add(imageButton).minWidth(100);
                imageButton.addListener(DialogSceneComposer.main.getHandListener());
                imageButton.addListener(new TextTooltip("Select the color of the button.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                imageButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        popTable.hide();
                        DialogSceneComposer.main.getDialogFactory().showDialogColors(new StyleProperty(), (colorData, pressedCancel) -> {
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
                label = new Label(simButton.color == null ? "white" : simButton.color.getName(), DialogSceneComposer.skin, "scene-label-colored");
                popTable.add(label);
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener buttonPaddingListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simButton = (DialogSceneComposerModel.SimButton) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(DialogSceneComposer.skin) {
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
                
                var label = new Label("Padding:", DialogSceneComposer.skin, "scene-label-colored");
                popTable.add(label).colspan(2);
                
                popTable.row();
                popTable.defaults().right().spaceRight(5);
                label = new Label("Left:", DialogSceneComposer.skin, "scene-label-colored");
                popTable.add(label);
                
                var spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, DialogSceneComposer.skin, "scene");
                spinner.setName("pad-left");
                spinner.setValue(simButton.padLeft);
                popTable.add(spinner);
                spinner.getTextField().addListener(DialogSceneComposer.main.getIbeamListener());
                spinner.getButtonMinus().addListener(DialogSceneComposer.main.getHandListener());
                spinner.getButtonPlus().addListener(DialogSceneComposer.main.getHandListener());
                spinner.addListener(new TextTooltip("The padding on the left of the contents.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                spinner.addListener(changeListener);
                
                popTable.row();
                label = new Label("Right:", DialogSceneComposer.skin, "scene-label-colored");
                popTable.add(label);
                
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, DialogSceneComposer.skin, "scene");
                spinner.setName("pad-right");
                spinner.setValue(simButton.padRight);
                popTable.add(spinner);
                spinner.getTextField().addListener(DialogSceneComposer.main.getIbeamListener());
                spinner.getButtonMinus().addListener(DialogSceneComposer.main.getHandListener());
                spinner.getButtonPlus().addListener(DialogSceneComposer.main.getHandListener());
                spinner.addListener(new TextTooltip("The padding on the right of the contents.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                spinner.addListener(changeListener);
                
                popTable.row();
                label = new Label("Top:", DialogSceneComposer.skin, "scene-label-colored");
                popTable.add(label);
                
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, DialogSceneComposer.skin, "scene");
                spinner.setName("pad-top");
                spinner.setValue(simButton.padTop);
                popTable.add(spinner);
                spinner.getTextField().addListener(DialogSceneComposer.main.getIbeamListener());
                spinner.getButtonMinus().addListener(DialogSceneComposer.main.getHandListener());
                spinner.getButtonPlus().addListener(DialogSceneComposer.main.getHandListener());
                spinner.addListener(new TextTooltip("The padding on the top of the contents.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                spinner.addListener(changeListener);
                
                popTable.row();
                label = new Label("Bottom:", DialogSceneComposer.skin, "scene-label-colored");
                popTable.add(label);
                
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, DialogSceneComposer.skin, "scene");
                spinner.setName("pad-bottom");
                spinner.setValue(simButton.padBottom);
                popTable.add(spinner);
                spinner.getTextField().addListener(DialogSceneComposer.main.getIbeamListener());
                spinner.getButtonMinus().addListener(DialogSceneComposer.main.getHandListener());
                spinner.getButtonPlus().addListener(DialogSceneComposer.main.getHandListener());
                spinner.addListener(new TextTooltip("The padding on the bottom of the contents.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                spinner.addListener(changeListener);
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
}
