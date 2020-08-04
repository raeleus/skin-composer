package com.ray3k.skincomposer.dialog.scenecomposer.menulisteners;

import com.badlogic.gdx.Input;
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

import static com.ray3k.skincomposer.dialog.scenecomposer.menulisteners.ListenersUtils.TEXT_FIELD_WIDTH;

public class SliderListeners {
    public static EventListener sliderNameListener(final DialogSceneComposer dialogSceneComposer) {
        var simSlider = (DialogSceneComposerModel.SimSlider) dialogSceneComposer.simActor;
        var textField = new TextField("", DialogSceneComposer.skin, "scene");
        var popTableClickListener = new PopTableClickListener(DialogSceneComposer.skin) {
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
                textField.setText(simSlider.name);
                popTable.add(textField).minWidth(TEXT_FIELD_WIDTH);
                textField.addListener(DialogSceneComposer.main.getIbeamListener());
                textField.addListener(new TextTooltip("The name of the Slider to allow for convenient searching via Group#findActor().", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        dialogSceneComposer.events.sliderName(textField.getText());
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
    
    public static EventListener sliderStyleListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simSlider = (DialogSceneComposerModel.SimSlider) simActor;
        var popTableClickListener = new StyleSelectorPopTable(Slider.class, simSlider.style == null ? "default-horizontal" : simSlider.style.name) {
            @Override
            public void accepted(StyleData styleData) {
                events.sliderStyle(styleData);
            }
        };
        
        return popTableClickListener;
    }
    
    public static EventListener sliderValueSettingsListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simSlider = (DialogSceneComposerModel.SimSlider) simActor;
        var popTableClickListener = new PopTableClickListener(DialogSceneComposer.skin) {
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
                
                var valueSpinner = new Spinner(simSlider.value, simSlider.increment, false, Spinner.Orientation.RIGHT_STACK, DialogSceneComposer.skin, "scene");
                valueSpinner.setMinimum(simSlider.minimum);
                valueSpinner.setMaximum(simSlider.maximum);
                table.add(valueSpinner).width(100).uniformX();
                valueSpinner.getTextField().addListener(DialogSceneComposer.main.getIbeamListener());
                valueSpinner.getButtonMinus().addListener(DialogSceneComposer.main.getHandListener());
                valueSpinner.getButtonPlus().addListener(DialogSceneComposer.main.getHandListener());
                valueSpinner.addListener(new TextTooltip("The value of the Slider.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                valueSpinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.sliderValue((float) valueSpinner.getValue());
                    }
                });
                
                table.row();
                label = new Label("Minimum:", DialogSceneComposer.skin, "scene-label-colored");
                table.add(label);
                
                var minimumSpinner = new Spinner(simSlider.minimum, 1, false, Spinner.Orientation.RIGHT_STACK, DialogSceneComposer.skin, "scene");
                minimumSpinner.setMaximum(simSlider.maximum);
                var maximumSpinner = new Spinner(simSlider.maximum, 1, false, Spinner.Orientation.RIGHT_STACK, DialogSceneComposer.skin, "scene");
                maximumSpinner.setMinimum(simSlider.minimum);
                table.add(minimumSpinner).uniformX().fillX();
                minimumSpinner.getTextField().addListener(DialogSceneComposer.main.getIbeamListener());
                minimumSpinner.getButtonMinus().addListener(DialogSceneComposer.main.getHandListener());
                minimumSpinner.getButtonPlus().addListener(DialogSceneComposer.main.getHandListener());
                minimumSpinner.addListener(new TextTooltip("The minimum value of the Slider.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                minimumSpinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.sliderMinimum((float) minimumSpinner.getValue());
                        if (valueSpinner.getValue() < minimumSpinner.getValue()) {
                            valueSpinner.setValue(simSlider.minimum);
                        }
                        maximumSpinner.setMinimum(simSlider.minimum);
                    }
                });
                
                table.row();
                label = new Label("Maximum:", DialogSceneComposer.skin, "scene-label-colored");
                table.add(label);
                
                maximumSpinner.setValue(simSlider.maximum);
                table.add(maximumSpinner).uniformX().fillX();
                maximumSpinner.getTextField().addListener(DialogSceneComposer.main.getIbeamListener());
                maximumSpinner.getButtonMinus().addListener(DialogSceneComposer.main.getHandListener());
                maximumSpinner.getButtonPlus().addListener(DialogSceneComposer.main.getHandListener());
                maximumSpinner.addListener(new TextTooltip("The maximum value of the Slider.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                maximumSpinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.sliderMaximum((float) maximumSpinner.getValue());
                        if (valueSpinner.getValue() > maximumSpinner.getValue()) {
                            valueSpinner.setValue(simSlider.maximum);
                        }
                        minimumSpinner.setMaximum(simSlider.maximum);
                    }
                });
                
                table.row();
                label = new Label("Increment:", DialogSceneComposer.skin, "scene-label-colored");
                table.add(label);
                
                var incrementSpinner = new Spinner(0, 1, false, Spinner.Orientation.RIGHT_STACK, DialogSceneComposer.skin, "scene");
                incrementSpinner.setValue(simSlider.increment);
                incrementSpinner.setMinimum(.0000000001);
                table.add(incrementSpinner).uniformX().fillX();
                incrementSpinner.getTextField().addListener(DialogSceneComposer.main.getIbeamListener());
                incrementSpinner.getButtonMinus().addListener(DialogSceneComposer.main.getHandListener());
                incrementSpinner.getButtonPlus().addListener(DialogSceneComposer.main.getHandListener());
                incrementSpinner.addListener(new TextTooltip("The increment value of the Slider.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                incrementSpinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.sliderIncrement((float) incrementSpinner.getValue());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener sliderOrientationListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simSlider = (DialogSceneComposerModel.SimSlider) simActor;
        var popTableClickListener = new PopTableClickListener(DialogSceneComposer.skin) {
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
                draggableTextList.setSelected(simSlider.vertical ? 1 : 0);
                popTable.add(draggableTextList);
                draggableTextList.addListener(DialogSceneComposer.main.getHandListener());
                draggableTextList.addListener(new TextTooltip("The orientation of the Slider.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                draggableTextList.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.sliderVertical(draggableTextList.getSelectedIndex() == 1);
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener sliderAnimationListener(final DialogSceneComposer dialogSceneComposer) {
        var simSlider = (DialogSceneComposerModel.SimSlider) dialogSceneComposer.simActor;
        var popTableClickListener = new PopTableClickListener(DialogSceneComposer.skin) {
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
                        GeneralListeners.interpolationListener(dialogSceneComposer, selection -> dialogSceneComposer.events.sliderAnimateInterpolation(selection)));
                
                table.row();
                textButton = new TextButton("Visual Interpolation", DialogSceneComposer.skin, "scene-med");
                table.add(textButton).uniformX().fillX().colspan(2);
                textButton.addListener(DialogSceneComposer.main.getHandListener());
                textButton.addListener(
                        GeneralListeners.interpolationListener(dialogSceneComposer, selection -> dialogSceneComposer.events.sliderVisualInterpolation(selection)));
                
                table.row();
                var label = new Label("Animation Duration:", DialogSceneComposer.skin, "scene-label-colored");
                table.add(label).right();
                
                var durationSpinner = new Spinner(simSlider.animationDuration, 1, false, Spinner.Orientation.RIGHT_STACK, DialogSceneComposer.skin, "scene");
                durationSpinner.setMinimum(0);
                table.add(durationSpinner).left();
                durationSpinner.getTextField().addListener(DialogSceneComposer.main.getIbeamListener());
                durationSpinner.getButtonMinus().addListener(DialogSceneComposer.main.getHandListener());
                durationSpinner.getButtonPlus().addListener(DialogSceneComposer.main.getHandListener());
                durationSpinner.addListener(new TextTooltip("The animation duration of the Slider as the value changes.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                durationSpinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        dialogSceneComposer.events.sliderAnimationDuration((float) durationSpinner.getValue());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener sliderRoundListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simSlider = (DialogSceneComposerModel.SimSlider) simActor;
        var popTableClickListener = new PopTableClickListener(DialogSceneComposer.skin) {
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
                var textButton = new TextButton(simSlider.round ? "TRUE" : "FALSE", DialogSceneComposer.skin, "scene-small");
                textButton.setChecked(simSlider.round);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(DialogSceneComposer.main.getHandListener());
                textButton.addListener(new TextTooltip("Whether the Slider inner positions are rounded to integer.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        events.sliderRound(textButton.isChecked());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener sliderDisabledListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simSlider = (DialogSceneComposerModel.SimSlider) simActor;
        var popTableClickListener = new PopTableClickListener(DialogSceneComposer.skin) {
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
                var textButton = new TextButton(simSlider.disabled ? "TRUE" : "FALSE", DialogSceneComposer.skin, "scene-small");
                textButton.setChecked(simSlider.disabled);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(DialogSceneComposer.main.getHandListener());
                textButton.addListener(new TextTooltip("Whether the Slider is disabled initially.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        events.sliderDisabled(textButton.isChecked());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
}
