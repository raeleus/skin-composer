package com.ray3k.skincomposer.dialog.scenecomposer.menulisteners;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.ray3k.stripe.DraggableTextList;
import com.ray3k.stripe.PopTableClickListener;
import com.ray3k.stripe.Spinner;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerEvents;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimActor;
import com.ray3k.skincomposer.dialog.scenecomposer.StyleSelectorPopTable;
import static com.ray3k.skincomposer.Main.*;

import static com.ray3k.skincomposer.dialog.scenecomposer.menulisteners.ListenersUtils.TEXT_FIELD_WIDTH;
import com.ray3k.skincomposer.Main;

public class ProgressBarListeners {
    public static EventListener progressBarNameListener(final DialogSceneComposer dialogSceneComposer) {
        var simList = (DialogSceneComposerModel.SimProgressBar) dialogSceneComposer.simActor;
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
                textField.setText(simList.name);
                popTable.add(textField).minWidth(TEXT_FIELD_WIDTH);
                textField.addListener(ibeamListener);
                textField.addListener((Main.makeTooltip("The name of the ProgressBar to allow for convenient searching via Group#findActor().", tooltipManager, skin, "scene")));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        dialogSceneComposer.events.progressBarName(textField.getText());
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
    
    public static EventListener progressBarStyleListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simProgressBar = (DialogSceneComposerModel.SimProgressBar) simActor;
        var popTableClickListener = new StyleSelectorPopTable(ProgressBar.class, simProgressBar.style == null ? "default-horizontal" : simProgressBar.style.name) {
            @Override
            public void accepted(StyleData styleData) {
                events.progressBarStyle(styleData);
            }
        };
        
        return popTableClickListener;
    }
    
    public static EventListener progressBarValueSettingsListener(final DialogSceneComposerEvents events,
                                                                 SimActor simActor) {
        var simProgressBar = (DialogSceneComposerModel.SimProgressBar) simActor;
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
                
                var table = new Table();
                popTable.add(table);
                
                var label = new Label("Value:", skin, "scene-label-colored");
                table.add(label);
                
                var valueSpinner = new Spinner(simProgressBar.value, simProgressBar.increment, false, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                valueSpinner.setMinimum(simProgressBar.minimum);
                valueSpinner.setMaximum(simProgressBar.maximum);
                table.add(valueSpinner).width(100).uniformX();
                valueSpinner.getTextField().addListener(ibeamListener);
                valueSpinner.getButtonMinus().addListener(handListener);
                valueSpinner.getButtonPlus().addListener(handListener);
                valueSpinner.addListener((Main.makeTooltip("The value of the ProgressBar.", tooltipManager, skin, "scene")));
                valueSpinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.progressBarValue((float) valueSpinner.getValue());
                    }
                });
    
                table.row();
                label = new Label("Minimum:", skin, "scene-label-colored");
                table.add(label);
    
                var minimumSpinner = new Spinner(simProgressBar.minimum, 1, false, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                minimumSpinner.setMaximum(simProgressBar.maximum);
                var maximumSpinner = new Spinner(simProgressBar.maximum, 1, false, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                maximumSpinner.setMinimum(simProgressBar.minimum);
                table.add(minimumSpinner).uniformX().fillX();
                minimumSpinner.getTextField().addListener(ibeamListener);
                minimumSpinner.getButtonMinus().addListener(handListener);
                minimumSpinner.getButtonPlus().addListener(handListener);
                minimumSpinner.addListener((Main.makeTooltip("The minimum value of the ProgressBar.", tooltipManager, skin, "scene")));
                minimumSpinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.progressBarMinimum((float) minimumSpinner.getValue());
                        if (valueSpinner.getValue() < minimumSpinner.getValue()) {
                            valueSpinner.setValue(simProgressBar.minimum);
                        }
                        maximumSpinner.setMinimum(simProgressBar.minimum);
                    }
                });
    
                table.row();
                label = new Label("Maximum:", skin, "scene-label-colored");
                table.add(label);
    
                maximumSpinner.setValue(simProgressBar.maximum);
                table.add(maximumSpinner).uniformX().fillX();
                maximumSpinner.getTextField().addListener(ibeamListener);
                maximumSpinner.getButtonMinus().addListener(handListener);
                maximumSpinner.getButtonPlus().addListener(handListener);
                maximumSpinner.addListener((Main.makeTooltip("The maximum value of the ProgressBar.", tooltipManager, skin, "scene")));
                maximumSpinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.progressBarMaximum((float) maximumSpinner.getValue());
                        if (valueSpinner.getValue() > maximumSpinner.getValue()) {
                            valueSpinner.setValue(simProgressBar.maximum);
                        }
                        minimumSpinner.setMaximum(simProgressBar.maximum);
                    }
                });
    
                table.row();
                label = new Label("Increment:", skin, "scene-label-colored");
                table.add(label);
    
                var incrementSpinner = new Spinner(0, 1, false, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                incrementSpinner.setValue(simProgressBar.increment);
                incrementSpinner.setMinimum(.0000000001);
                table.add(incrementSpinner).uniformX().fillX();
                incrementSpinner.getTextField().addListener(ibeamListener);
                incrementSpinner.getButtonMinus().addListener(handListener);
                incrementSpinner.getButtonPlus().addListener(handListener);
                incrementSpinner.addListener((Main.makeTooltip("The increment value of the ProgressBar.", tooltipManager, skin, "scene")));
                incrementSpinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.progressBarIncrement((float) incrementSpinner.getValue());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener progressBarOrientationListener(final DialogSceneComposerEvents events,
                                                               SimActor simActor) {
        var simProgressBar = (DialogSceneComposerModel.SimProgressBar) simActor;
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
                
                var label = new Label("Orientation:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var draggableTextList = new DraggableTextList(true, skin, "scene");
                draggableTextList.setDraggable(false);
                draggableTextList.addAllTexts("Horizontal", "Vertical");
                draggableTextList.setSelected(simProgressBar.vertical ? 1 : 0);
                popTable.add(draggableTextList);
                draggableTextList.addListener(handListener);
                draggableTextList.addListener((Main.makeTooltip("The orientation of the ProgressBar.", tooltipManager, skin, "scene")));
                draggableTextList.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.progressBarVertical(draggableTextList.getSelectedIndex() == 1);
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener progressBarAnimationListener(final DialogSceneComposer dialogSceneComposer) {
        var simProgressBar = (DialogSceneComposerModel.SimProgressBar) dialogSceneComposer.simActor;
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
                
                var table = new Table();
                popTable.add(table);
                
                table.defaults().spaceRight(5);
                var textButton = new TextButton("Animate Interpolation", skin, "scene-med");
                table.add(textButton).uniformX().fillX().colspan(2);
                textButton.addListener(handListener);
                textButton.addListener(
                        GeneralListeners.interpolationListener(dialogSceneComposer, selection -> dialogSceneComposer.events.progressBarAnimateInterpolation(selection)));
                
                table.row();
                textButton = new TextButton("Visual Interpolation", skin, "scene-med");
                table.add(textButton).uniformX().fillX().colspan(2);
                textButton.addListener(handListener);
                textButton.addListener(
                        GeneralListeners.interpolationListener(dialogSceneComposer, selection -> dialogSceneComposer.events.progressBarVisualInterpolation(selection)));
                
                table.row();
                var label = new Label("Animation Duration:", skin, "scene-label-colored");
                table.add(label).right();
                
                var durationSpinner = new Spinner(simProgressBar.animationDuration, 1, false, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                durationSpinner.setMinimum(0);
                table.add(durationSpinner).left();
                durationSpinner.getTextField().addListener(ibeamListener);
                durationSpinner.getButtonMinus().addListener(handListener);
                durationSpinner.getButtonPlus().addListener(handListener);
                durationSpinner.addListener((Main.makeTooltip("The animation duration of the ProgressBar as the value changes.", tooltipManager, skin, "scene")));
                durationSpinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        dialogSceneComposer.events.progressBarAnimationDuration((float) durationSpinner.getValue());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener progressBarRoundListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simProgressBar = (DialogSceneComposerModel.SimProgressBar) simActor;
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
                
                var label = new Label("Round:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var textButton = new TextButton(simProgressBar.round ? "TRUE" : "FALSE", skin, "scene-small");
                textButton.setChecked(simProgressBar.round);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(handListener);
                textButton.addListener((Main.makeTooltip("Whether the ProgressBar inner positions are rounded to integer.", tooltipManager, skin, "scene")));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        events.progressBarRound(textButton.isChecked());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener progressBarDisabledListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simProgressBar = (DialogSceneComposerModel.SimProgressBar) simActor;
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
                var textButton = new TextButton(simProgressBar.disabled ? "TRUE" : "FALSE", skin, "scene-small");
                textButton.setChecked(simProgressBar.disabled);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(handListener);
                textButton.addListener((Main.makeTooltip("Whether the ProgressBar is disabled initially.", tooltipManager, skin, "scene")));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        events.progressBarDisabled(textButton.isChecked());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
}
