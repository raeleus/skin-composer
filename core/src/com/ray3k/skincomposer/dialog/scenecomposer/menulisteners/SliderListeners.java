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

public class SliderListeners {
    public static EventListener sliderNameListener(final DialogSceneComposer dialogSceneComposer) {
        var simSlider = (DialogSceneComposerModel.SimSlider) dialogSceneComposer.simActor;
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
                textField.setText(simSlider.name);
                popTable.add(textField).minWidth(TEXT_FIELD_WIDTH);
                textField.addListener(ibeamListener);
                textField.addListener((Main.makeTooltip("The name of the Slider to allow for convenient searching via Group#findActor().", tooltipManager, skin, "scene")));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        dialogSceneComposer.events.sliderName(textField.getText());
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
                
                var table = new Table();
                popTable.add(table);
                
                var label = new Label("Value:", skin, "scene-label-colored");
                table.add(label);
                
                var valueSpinner = new Spinner(simSlider.value, simSlider.increment, false, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                valueSpinner.setMinimum(simSlider.minimum);
                valueSpinner.setMaximum(simSlider.maximum);
                table.add(valueSpinner).width(100).uniformX();
                valueSpinner.getTextField().addListener(ibeamListener);
                valueSpinner.getButtonMinus().addListener(handListener);
                valueSpinner.getButtonPlus().addListener(handListener);
                valueSpinner.addListener((Main.makeTooltip("The value of the Slider.", tooltipManager, skin, "scene")));
                valueSpinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.sliderValue((float) valueSpinner.getValue());
                    }
                });
                
                table.row();
                label = new Label("Minimum:", skin, "scene-label-colored");
                table.add(label);
                
                var minimumSpinner = new Spinner(simSlider.minimum, 1, false, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                minimumSpinner.setMaximum(simSlider.maximum);
                var maximumSpinner = new Spinner(simSlider.maximum, 1, false, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                maximumSpinner.setMinimum(simSlider.minimum);
                table.add(minimumSpinner).uniformX().fillX();
                minimumSpinner.getTextField().addListener(ibeamListener);
                minimumSpinner.getButtonMinus().addListener(handListener);
                minimumSpinner.getButtonPlus().addListener(handListener);
                minimumSpinner.addListener((Main.makeTooltip("The minimum value of the Slider.", tooltipManager, skin, "scene")));
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
                label = new Label("Maximum:", skin, "scene-label-colored");
                table.add(label);
                
                maximumSpinner.setValue(simSlider.maximum);
                table.add(maximumSpinner).uniformX().fillX();
                maximumSpinner.getTextField().addListener(ibeamListener);
                maximumSpinner.getButtonMinus().addListener(handListener);
                maximumSpinner.getButtonPlus().addListener(handListener);
                maximumSpinner.addListener((Main.makeTooltip("The maximum value of the Slider.", tooltipManager, skin, "scene")));
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
                label = new Label("Increment:", skin, "scene-label-colored");
                table.add(label);
                
                var incrementSpinner = new Spinner(0, 1, false, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                incrementSpinner.setValue(simSlider.increment);
                incrementSpinner.setMinimum(.0000000001);
                table.add(incrementSpinner).uniformX().fillX();
                incrementSpinner.getTextField().addListener(ibeamListener);
                incrementSpinner.getButtonMinus().addListener(handListener);
                incrementSpinner.getButtonPlus().addListener(handListener);
                incrementSpinner.addListener((Main.makeTooltip("The increment value of the Slider.", tooltipManager, skin, "scene")));
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
                draggableTextList.setSelected(simSlider.vertical ? 1 : 0);
                popTable.add(draggableTextList);
                draggableTextList.addListener(handListener);
                draggableTextList.addListener((Main.makeTooltip("The orientation of the Slider.", tooltipManager, skin, "scene")));
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
                
                var table = new Table();
                popTable.add(table);
                
                table.defaults().spaceRight(5);
                var textButton = new TextButton("Animate Interpolation", skin, "scene-med");
                table.add(textButton).uniformX().fillX().colspan(2);
                textButton.addListener(handListener);
                textButton.addListener(
                        GeneralListeners.interpolationListener(dialogSceneComposer, selection -> dialogSceneComposer.events.sliderAnimateInterpolation(selection)));
                
                table.row();
                textButton = new TextButton("Visual Interpolation", skin, "scene-med");
                table.add(textButton).uniformX().fillX().colspan(2);
                textButton.addListener(handListener);
                textButton.addListener(
                        GeneralListeners.interpolationListener(dialogSceneComposer, selection -> dialogSceneComposer.events.sliderVisualInterpolation(selection)));
                
                table.row();
                var label = new Label("Animation Duration:", skin, "scene-label-colored");
                table.add(label).right();
                
                var durationSpinner = new Spinner(simSlider.animationDuration, 1, false, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                durationSpinner.setMinimum(0);
                table.add(durationSpinner).left();
                durationSpinner.getTextField().addListener(ibeamListener);
                durationSpinner.getButtonMinus().addListener(handListener);
                durationSpinner.getButtonPlus().addListener(handListener);
                durationSpinner.addListener((Main.makeTooltip("The animation duration of the Slider as the value changes.", tooltipManager, skin, "scene")));
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
                var textButton = new TextButton(simSlider.round ? "TRUE" : "FALSE", skin, "scene-small");
                textButton.setChecked(simSlider.round);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(handListener);
                textButton.addListener((Main.makeTooltip("Whether the Slider inner positions are rounded to integer.", tooltipManager, skin, "scene")));
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
                var textButton = new TextButton(simSlider.disabled ? "TRUE" : "FALSE", skin, "scene-small");
                textButton.setChecked(simSlider.disabled);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(handListener);
                textButton.addListener((Main.makeTooltip("Whether the Slider is disabled initially.", tooltipManager, skin, "scene")));
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
