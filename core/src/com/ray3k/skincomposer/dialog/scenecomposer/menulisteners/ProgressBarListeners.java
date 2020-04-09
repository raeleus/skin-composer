package com.ray3k.skincomposer.dialog.scenecomposer.menulisteners;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.ray3k.skincomposer.DraggableTextList;
import com.ray3k.skincomposer.PopTable;
import com.ray3k.skincomposer.Spinner;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerEvents;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimActor;
import com.ray3k.skincomposer.dialog.scenecomposer.StyleSelectorPopTable;

public class ProgressBarListeners {
    public static EventListener progressBarNameListener(final DialogSceneComposer dialogSceneComposer) {
        var simList = (DialogSceneComposerModel.SimProgressBar) dialogSceneComposer.simActor;
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
                textField.setText(simList.name);
                popTable.add(textField).minWidth(150);
                textField.addListener(DialogSceneComposer.main.getIbeamListener());
                textField.addListener(new TextTooltip("The name of the ProgressBar to allow for convenient searching via Group#findActor().", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        dialogSceneComposer.events.progressBarName(textField.getText());
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
        var popTableClickListener = new PopTable.PopTableClickListener(DialogSceneComposer.skin) {
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
                
                var label = new Label("Value:", DialogSceneComposer.skin, "scene-label-colored");
                table.add(label);
                
                var valueSpinner = new Spinner(simProgressBar.value, simProgressBar.increment, false, Spinner.Orientation.RIGHT_STACK, DialogSceneComposer.skin, "scene");
                valueSpinner.setMinimum(simProgressBar.minimum);
                valueSpinner.setMaximum(simProgressBar.maximum);
                table.add(valueSpinner).width(100).uniformX();
                valueSpinner.getTextField().addListener(DialogSceneComposer.main.getIbeamListener());
                valueSpinner.getButtonMinus().addListener(DialogSceneComposer.main.getHandListener());
                valueSpinner.getButtonPlus().addListener(DialogSceneComposer.main.getHandListener());
                valueSpinner.addListener(new TextTooltip("The value of the ProgressBar.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                valueSpinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.progressBarValue((float) valueSpinner.getValue());
                    }
                });
    
                table.row();
                label = new Label("Minimum:", DialogSceneComposer.skin, "scene-label-colored");
                table.add(label);
    
                var minimumSpinner = new Spinner(simProgressBar.minimum, 1, false, Spinner.Orientation.RIGHT_STACK, DialogSceneComposer.skin, "scene");
                minimumSpinner.setMaximum(simProgressBar.maximum);
                var maximumSpinner = new Spinner(simProgressBar.maximum, 1, false, Spinner.Orientation.RIGHT_STACK, DialogSceneComposer.skin, "scene");
                maximumSpinner.setMinimum(simProgressBar.minimum);
                table.add(minimumSpinner).uniformX().fillX();
                minimumSpinner.getTextField().addListener(DialogSceneComposer.main.getIbeamListener());
                minimumSpinner.getButtonMinus().addListener(DialogSceneComposer.main.getHandListener());
                minimumSpinner.getButtonPlus().addListener(DialogSceneComposer.main.getHandListener());
                minimumSpinner.addListener(new TextTooltip("The minimum value of the ProgressBar.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
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
                label = new Label("Maximum:", DialogSceneComposer.skin, "scene-label-colored");
                table.add(label);
    
                maximumSpinner.setValue(simProgressBar.maximum);
                table.add(maximumSpinner).uniformX().fillX();
                maximumSpinner.getTextField().addListener(DialogSceneComposer.main.getIbeamListener());
                maximumSpinner.getButtonMinus().addListener(DialogSceneComposer.main.getHandListener());
                maximumSpinner.getButtonPlus().addListener(DialogSceneComposer.main.getHandListener());
                maximumSpinner.addListener(new TextTooltip("The maximum value of the ProgressBar.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
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
                label = new Label("Increment:", DialogSceneComposer.skin, "scene-label-colored");
                table.add(label);
    
                var incrementSpinner = new Spinner(0, 1, false, Spinner.Orientation.RIGHT_STACK, DialogSceneComposer.skin, "scene");
                incrementSpinner.setValue(simProgressBar.increment);
                table.add(incrementSpinner).uniformX().fillX();
                incrementSpinner.getTextField().addListener(DialogSceneComposer.main.getIbeamListener());
                incrementSpinner.getButtonMinus().addListener(DialogSceneComposer.main.getHandListener());
                incrementSpinner.getButtonPlus().addListener(DialogSceneComposer.main.getHandListener());
                incrementSpinner.addListener(new TextTooltip("The increment value of the ProgressBar.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
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
        var popTableClickListener = new PopTable.PopTableClickListener(DialogSceneComposer.skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Orientation:", DialogSceneComposer.skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var draggableTextList = new DraggableTextList(true, DialogSceneComposer.skin, "scene");
                draggableTextList.setDraggable(false);
                draggableTextList.addAllTexts("Horizontal", "Vertical");
                draggableTextList.setSelected(simProgressBar.vertical ? 1 : 0);
                popTable.add(draggableTextList);
                draggableTextList.addListener(DialogSceneComposer.main.getHandListener());
                draggableTextList.addListener(new TextTooltip("The orientation of the ProgressBar.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
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
        var popTableClickListener = new PopTable.PopTableClickListener(DialogSceneComposer.skin) {
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
                var textButton = new TextButton("Animate Interpolation", DialogSceneComposer.skin, "scene-med");
                table.add(textButton).uniformX().fillX().colspan(2);
                textButton.addListener(DialogSceneComposer.main.getHandListener());
                textButton.addListener(
                        GeneralListeners.interpolationListener(dialogSceneComposer, selection -> dialogSceneComposer.events.progressBarAnimateInterpolation(selection)));
                
                table.row();
                textButton = new TextButton("Visual Interpolation", DialogSceneComposer.skin, "scene-med");
                table.add(textButton).uniformX().fillX().colspan(2);
                textButton.addListener(DialogSceneComposer.main.getHandListener());
                textButton.addListener(
                        GeneralListeners.interpolationListener(dialogSceneComposer, selection -> dialogSceneComposer.events.progressBarVisualInterpolation(selection)));
                
                table.row();
                var label = new Label("Animation Duration:", DialogSceneComposer.skin, "scene-label-colored");
                table.add(label).right();
                
                var durationSpinner = new Spinner(simProgressBar.animationDuration, 1, false, Spinner.Orientation.RIGHT_STACK, DialogSceneComposer.skin, "scene");
                durationSpinner.setMinimum(0);
                table.add(durationSpinner).left();
                durationSpinner.getTextField().addListener(DialogSceneComposer.main.getIbeamListener());
                durationSpinner.getButtonMinus().addListener(DialogSceneComposer.main.getHandListener());
                durationSpinner.getButtonPlus().addListener(DialogSceneComposer.main.getHandListener());
                durationSpinner.addListener(new TextTooltip("The animation duration of the ProgressBar as the value changes.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
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
        var popTableClickListener = new PopTable.PopTableClickListener(DialogSceneComposer.skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Round:", DialogSceneComposer.skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var textButton = new TextButton(simProgressBar.round ? "TRUE" : "FALSE", DialogSceneComposer.skin, "scene-small");
                textButton.setChecked(simProgressBar.round);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(DialogSceneComposer.main.getHandListener());
                textButton.addListener(new TextTooltip("Whether the ProgressBar inner positions are rounded to integer.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
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
                var textButton = new TextButton(simProgressBar.disabled ? "TRUE" : "FALSE", DialogSceneComposer.skin, "scene-small");
                textButton.setChecked(simProgressBar.disabled);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(DialogSceneComposer.main.getHandListener());
                textButton.addListener(new TextTooltip("Whether the ProgressBar is disabled initially.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
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
