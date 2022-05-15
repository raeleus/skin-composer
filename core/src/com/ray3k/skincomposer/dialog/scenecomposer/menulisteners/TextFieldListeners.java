package com.ray3k.skincomposer.dialog.scenecomposer.menulisteners;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.ray3k.stripe.PopTableClickListener;
import com.ray3k.stripe.RangeSlider;
import com.ray3k.stripe.RangeSlider.ValueBeginChangeEvent;
import com.ray3k.stripe.RangeSlider.ValueBeginChangeListener;
import com.ray3k.stripe.RangeSlider.ValueEndChangeEvent;
import com.ray3k.stripe.RangeSlider.ValueEndChangeListener;
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

public class TextFieldListeners {
    public static EventListener textFieldNameListener(final DialogSceneComposer dialogSceneComposer) {
        var simTextField = (DialogSceneComposerModel.SimTextField) dialogSceneComposer.simActor;
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
                textField.setText(simTextField.name);
                popTable.add(textField).minWidth(TEXT_FIELD_WIDTH);
                textField.addListener(ibeamListener);
                textField.addListener((Main.makeTooltip("The name of the TextField to allow for convenient searching via Group#findActor().", tooltipManager, skin, "scene")));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        dialogSceneComposer.events.textFieldName(textField.getText());
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
    
    public static EventListener textFieldStyleListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simTextField = (DialogSceneComposerModel.SimTextField) simActor;
        var popTableClickListener = new StyleSelectorPopTable(TextField.class, simTextField.style == null ? "default" : simTextField.style.name) {
            @Override
            public void accepted(StyleData styleData) {
                events.textFieldStyle(styleData);
            }
        };
        
        return popTableClickListener;
    }
    
    public static EventListener textFieldMessageTextListener(final DialogSceneComposer dialogSceneComposer) {
        var simTextField = (DialogSceneComposerModel.SimTextField) dialogSceneComposer.simActor;
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
                
                var label = new Label("Message Text:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setText(simTextField.messageText);
                popTable.add(textField).minWidth(TEXT_FIELD_WIDTH);
                textField.addListener(ibeamListener);
                textField.addListener((Main.makeTooltip("The message inside the field when nothing is inputted and the field does not have focus.", tooltipManager, skin, "scene")));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        dialogSceneComposer.events.textFieldMessageText(textField.getText());
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
    
    public static EventListener textFieldPasswordListener(final DialogSceneComposer dialogSceneComposer) {
        var simTextField = (DialogSceneComposerModel.SimTextField) dialogSceneComposer.simActor;
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
                
                var label = new Label("Password Character:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setText(Character.toString(simTextField.passwordCharacter));
                textField.setMaxLength(1);
                popTable.add(textField).minWidth(TEXT_FIELD_WIDTH);
                textField.addListener(ibeamListener);
                textField.addListener((Main.makeTooltip("The character used to obscure text when password mode is enabled.", tooltipManager, skin, "scene")));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (textField.getText().length() > 0) {
                            dialogSceneComposer.events.textFieldPasswordCharacter(textField.getText().charAt(0));
                        } else {
                            dialogSceneComposer.events.textFieldPasswordCharacter('*');
                        }
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
                
                popTable.row();
                label = new Label("Password Mode:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var textButton = new TextButton(simTextField.passwordMode ? "TRUE" : "FALSE", skin, "scene-small");
                textButton.setChecked(simTextField.passwordMode);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(handListener);
                textButton.addListener((Main.makeTooltip("Whether password mode is enabled for the TextField.", tooltipManager, skin, "scene")));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        dialogSceneComposer.events.textFieldPasswordMode(textButton.isChecked());
                    }
                });
                
                dialogSceneComposer.getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener textFieldSelectionListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simTextField = (DialogSceneComposerModel.SimTextField) simActor;
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
                
                var positionLabel = new Label("Cursor Position: (" + simTextField.cursorPosition + ")", skin, "scene-label-colored");
                table.add(positionLabel);
                
                table.row();
                var slider = new Slider(0, simTextField.text == null ? 0 : simTextField.text.length(), 1, false, skin, "scene");
                slider.setValue(simTextField.cursorPosition);
                table.add(slider).minWidth(200);
                slider.addListener(handListener);
                slider.addListener((Main.makeTooltip("The cursor position when the textfield has keyboard focus.", tooltipManager, skin, "scene")));
                slider.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textFieldCursorPosition(MathUtils.round(slider.getValue()));
                        positionLabel.setText("Cursor Position: (" + simTextField.cursorPosition + ")");
                    }
                });
                
                table.row();
                var selectionLabel = new Label("Selection: (" + simTextField.selectionStart + ", " + simTextField.selectionEnd + ")", skin, "scene-label-colored");
                table.add(selectionLabel);
                
                table.row();
                var rangeSlider = new RangeSlider(skin, "scene");
                rangeSlider.setMinimum(0);
                rangeSlider.setMaximum(simTextField.text == null ? 0 : simTextField.text.length());
                rangeSlider.setIncrement(1);
                rangeSlider.setValueBegin(simTextField.selectionStart);
                rangeSlider.setValueEnd(simTextField.selectionEnd);
                rangeSlider.setDisabled(simTextField.selectAll);
                table.add(rangeSlider).minWidth(200);
                rangeSlider.getKnobBegin().addListener(handListener);
                rangeSlider.getKnobEnd().addListener(handListener);
                rangeSlider.addListener((Main.makeTooltip("The text range to be selected if this textField has keyboard focus.", tooltipManager, skin, "scene")));
                rangeSlider.addListener(new ValueBeginChangeListener() {
                    @Override
                    public void changed(ValueBeginChangeEvent event, float value, Actor actor) {
                        events.textFieldSelectionStart(MathUtils.round(value));
                    }
                });
                rangeSlider.addListener(new ValueEndChangeListener() {
                    @Override
                    public void changed(ValueEndChangeEvent event, float value, Actor actor) {
                        events.textFieldSelectionEnd(MathUtils.round(value));
                    }
                });
                rangeSlider.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        selectionLabel.setText("Selection: (" + simTextField.selectionStart + ", " + simTextField.selectionEnd + ")");
                    }
                });
                
                table.row();
                var label = new Label("Select All: ", skin, "scene-label-colored");
                table.add(label);
                
                table.row();
                var textButton = new TextButton(simTextField.selectAll ? "TRUE" : "FALSE", skin, "scene-small");
                textButton.setChecked(simTextField.selectAll);
                table.add(textButton).minWidth(100);
                textButton.addListener(handListener);
                textButton.addListener((Main.makeTooltip("Convenience option to select all text.", tooltipManager, skin, "scene")));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        events.textFieldSelectAll(textButton.isChecked());
                        rangeSlider.setDisabled(textButton.isChecked());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener textFieldAlignmentListener(final DialogSceneComposerEvents events,
                                                           final SimActor simActor) {
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
                var simTextField = (DialogSceneComposerModel.SimTextField) simActor;
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var table = new Table();
                popTable.add(table);
                
                var label = new Label("Alignment:", skin, "scene-label-colored");
                table.add(label).colspan(3);
                
                table.row();
                table.defaults().space(10).left().uniformX();
                var buttonGroup = new ButtonGroup<ImageTextButton>();
                var imageTextButton = new ImageTextButton("Top-Left", skin, "scene-checkbox-colored");
                var topLeft = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the contents of the TextField to the top left.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textFieldAlignment(Align.topLeft);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Top", skin, "scene-checkbox-colored");
                var top = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the contents of the TextField to the top center.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textFieldAlignment(Align.top);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Top-Right", skin, "scene-checkbox-colored");
                var topRight = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the contents of the TextField to the top right.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textFieldAlignment(Align.topRight);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                table.row();
                imageTextButton = new ImageTextButton("Left", skin, "scene-checkbox-colored");
                var left = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the contents of the TextField to the middle left.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textFieldAlignment(Align.left);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Center", skin, "scene-checkbox-colored");
                var center = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the contents of the TextField to the center.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textFieldAlignment(Align.center);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Right", skin, "scene-checkbox-colored");
                var right = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the contents of the TextField to the middle right.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textFieldAlignment(Align.right);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                table.row();
                imageTextButton = new ImageTextButton("Bottom-Left", skin, "scene-checkbox-colored");
                var bottomLeft = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the contents of the TextField to the bottom left.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textFieldAlignment(Align.bottomLeft);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Bottom", skin, "scene-checkbox-colored");
                var bottom = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the contents of the TextField to the bottom center.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textFieldAlignment(Align.bottom);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Bottom-Right", skin, "scene-checkbox-colored");
                var bottomRight = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the contents of the TextField to the bottom right.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textFieldAlignment(Align.bottomRight);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                switch (simTextField.alignment) {
                    case Align.topLeft:
                        topLeft.setChecked(true);
                        break;
                    case Align.top:
                        top.setChecked(true);
                        break;
                    case Align.topRight:
                        topRight.setChecked(true);
                        break;
                    case Align.right:
                        right.setChecked(true);
                        break;
                    case Align.bottomRight:
                        bottomRight.setChecked(true);
                        break;
                    case Align.bottom:
                        bottom.setChecked(true);
                        break;
                    case Align.bottomLeft:
                        bottomLeft.setChecked(true);
                        break;
                    case Align.left:
                        left.setChecked(true);
                        break;
                    case Align.center:
                        center.setChecked(true);
                        break;
                }
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener textFieldFocusTraversalListener(final DialogSceneComposerEvents events,
                                                                SimActor simActor) {
        var simTextField = (DialogSceneComposerModel.SimTextField) simActor;
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
                
                var label = new Label("Focus Traversal:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var textButton = new TextButton(simTextField.focusTraversal ? "TRUE" : "FALSE", skin, "scene-small");
                textButton.setChecked(simTextField.focusTraversal);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(handListener);
                textButton.addListener((Main.makeTooltip("Whether the text field allows for the use of focus traversal keys.", tooltipManager, skin, "scene")));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        events.textFieldFocusTraversal(textButton.isChecked());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener textFieldMaxLengthListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simTextField = (DialogSceneComposerModel.SimTextField) simActor;
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
                
                var label = new Label("Max Length:", skin, "scene-label-colored");
                table.add(label).right();
                
                var valueSpinner = new Spinner(simTextField.maxLength, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                valueSpinner.setMinimum(0);
                table.add(valueSpinner).width(100).left();
                valueSpinner.getTextField().addListener(ibeamListener);
                valueSpinner.getButtonMinus().addListener(handListener);
                valueSpinner.getButtonPlus().addListener(handListener);
                valueSpinner.addListener((Main.makeTooltip("The maximum length of characters allowed in the TextField.", tooltipManager, skin, "scene")));
                valueSpinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textFieldMaxLength(valueSpinner.getValueAsInt());
                    }
                });
                
                table.row();
                label = new Label("(Set to 0 to show as many as possible)", skin, "scene-label-colored");
                table.add(label).colspan(2);
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener textFieldDisabledListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simTextField = (DialogSceneComposerModel.SimTextField) simActor;
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
                var textButton = new TextButton(simTextField.disabled ? "TRUE" : "FALSE", skin, "scene-small");
                textButton.setChecked(simTextField.disabled);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(handListener);
                textButton.addListener((Main.makeTooltip("Whether the TextField is disabled initially.", tooltipManager, skin, "scene")));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        events.textFieldDisabled(textButton.isChecked());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener textFieldTextListener(final DialogSceneComposer dialogSceneComposer) {
        var simTextField = (DialogSceneComposerModel.SimTextField) dialogSceneComposer.simActor;
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
                
                var label = new Label("Text:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setText(simTextField.text);
                popTable.add(textField).minWidth(TEXT_FIELD_WIDTH);
                textField.addListener(ibeamListener);
                textField.addListener((Main.makeTooltip("The default text inside the TextField.", tooltipManager, skin, "scene")));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        dialogSceneComposer.events.textFieldText(textField.getText());
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
}
