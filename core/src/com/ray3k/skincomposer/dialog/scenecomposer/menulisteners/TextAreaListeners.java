package com.ray3k.skincomposer.dialog.scenecomposer.menulisteners;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.ray3k.skincomposer.PopTable;
import com.ray3k.skincomposer.RangeSlider;
import com.ray3k.skincomposer.RangeSlider.ValueBeginChangeEvent;
import com.ray3k.skincomposer.RangeSlider.ValueBeginChangeListener;
import com.ray3k.skincomposer.RangeSlider.ValueEndChangeEvent;
import com.ray3k.skincomposer.RangeSlider.ValueEndChangeListener;
import com.ray3k.skincomposer.Spinner;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerEvents;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimActor;
import com.ray3k.skincomposer.dialog.scenecomposer.StyleSelectorPopTable;

public class TextAreaListeners {
    public static EventListener textAreaNameListener(final DialogSceneComposer dialogSceneComposer) {
        var simTextArea = (DialogSceneComposerModel.SimTextArea) dialogSceneComposer.simActor;
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
                textField.setText(simTextArea.name);
                popTable.add(textField).minWidth(150);
                textField.addListener(DialogSceneComposer.main.getIbeamListener());
                textField.addListener(new TextTooltip("The name of the TextArea to allow for convenient searching via Group#findActor().", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        dialogSceneComposer.events.textAreaName(textField.getText());
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
    
    public static EventListener textAreaStyleListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simTextArea = (DialogSceneComposerModel.SimTextArea) simActor;
        var popTableClickListener = new StyleSelectorPopTable(TextField.class, simTextArea.style == null ? "default" : simTextArea.style.name) {
            @Override
            public void accepted(StyleData styleData) {
                events.textAreaStyle(styleData);
            }
        };
        
        return popTableClickListener;
    }
    
    public static EventListener textAreaTextListener(final DialogSceneComposer dialogSceneComposer) {
        var simTextArea = (DialogSceneComposerModel.SimTextArea) dialogSceneComposer.simActor;
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
                
                var label = new Label("Text:", DialogSceneComposer.skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setText(simTextArea.text);
                popTable.add(textField).minWidth(150);
                textField.addListener(DialogSceneComposer.main.getIbeamListener());
                textField.addListener(new TextTooltip("The default text inside the TextArea.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        dialogSceneComposer.events.textAreaText(textField.getText());
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
    
    public static EventListener textAreaMessageTextListener(final DialogSceneComposer dialogSceneComposer) {
        var simTextField = (DialogSceneComposerModel.SimTextArea) dialogSceneComposer.simActor;
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
                
                var label = new Label("Message Text:", DialogSceneComposer.skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setText(simTextField.messageText);
                popTable.add(textField).minWidth(150);
                textField.addListener(DialogSceneComposer.main.getIbeamListener());
                textField.addListener(new TextTooltip("The message inside the TextArea when nothing is inputted and the TextArea does not have focus.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        dialogSceneComposer.events.textAreaMessageText(textField.getText());
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
    
    public static EventListener textAreaPasswordListener(final DialogSceneComposer dialogSceneComposer) {
        var simTextArea = (DialogSceneComposerModel.SimTextArea) dialogSceneComposer.simActor;
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
                
                var label = new Label("Password Character:", DialogSceneComposer.skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setText(Character.toString(simTextArea.passwordCharacter));
                textField.setMaxLength(1);
                popTable.add(textField).minWidth(150);
                textField.addListener(DialogSceneComposer.main.getIbeamListener());
                textField.addListener(new TextTooltip("The character used to obscure text when password mode is enabled.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (textField.getText().length() > 0) {
                            dialogSceneComposer.events.textAreaPasswordCharacter(textField.getText().charAt(0));
                        } else {
                            dialogSceneComposer.events.textAreaPasswordCharacter('*');
                        }
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
                
                popTable.row();
                label = new Label("Password Mode:", DialogSceneComposer.skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var textButton = new TextButton(simTextArea.passwordMode ? "TRUE" : "FALSE", DialogSceneComposer.skin, "scene-small");
                textButton.setChecked(simTextArea.passwordMode);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(DialogSceneComposer.main.getHandListener());
                textButton.addListener(new TextTooltip("Whether password mode is enabled for the TextArea.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        dialogSceneComposer.events.textAreaPasswordMode(textButton.isChecked());
                    }
                });
                
                dialogSceneComposer.getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener textAreaSelectionListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simTextArea = (DialogSceneComposerModel.SimTextArea) simActor;
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
                
                var positionLabel = new Label("Cursor Position: (" + simTextArea.cursorPosition + ")", DialogSceneComposer.skin, "scene-label-colored");
                table.add(positionLabel);
                
                table.row();
                var slider = new Slider(0, simTextArea.text == null ? 0 : simTextArea.text.length(), 1, false, DialogSceneComposer.skin, "scene");
                slider.setValue(simTextArea.cursorPosition);
                table.add(slider).minWidth(200);
                slider.addListener(DialogSceneComposer.main.getHandListener());
                slider.addListener(new TextTooltip("The cursor position when the TextArea has keyboard focus.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                slider.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textAreaCursorPosition(MathUtils.round(slider.getValue()));
                        positionLabel.setText("Cursor Position: (" + simTextArea.cursorPosition + ")");
                    }
                });
                
                table.row();
                var selectionLabel = new Label("Selection: (" + simTextArea.selectionStart + ", " + simTextArea.selectionEnd + ")", DialogSceneComposer.skin, "scene-label-colored");
                table.add(selectionLabel);
                
                table.row();
                var rangeSlider = new RangeSlider(DialogSceneComposer.skin, "scene");
                rangeSlider.setMinimum(0);
                rangeSlider.setMaximum(simTextArea.text == null ? 0 : simTextArea.text.length());
                rangeSlider.setIncrement(1);
                rangeSlider.setValueBegin(simTextArea.selectionStart);
                rangeSlider.setValueEnd(simTextArea.selectionEnd);
                rangeSlider.setDisabled(simTextArea.selectAll);
                table.add(rangeSlider).minWidth(200);
                rangeSlider.getKnobBegin().addListener(DialogSceneComposer.main.getHandListener());
                rangeSlider.getKnobEnd().addListener(DialogSceneComposer.main.getHandListener());
                rangeSlider.addListener(new TextTooltip("The text range to be selected if this TextArea has keyboard focus.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                rangeSlider.addListener(new ValueBeginChangeListener() {
                    @Override
                    public void changed(ValueBeginChangeEvent event, float value, Actor actor) {
                        events.textAreaSelectionStart(MathUtils.round(value));
                    }
                });
                rangeSlider.addListener(new ValueEndChangeListener() {
                    @Override
                    public void changed(ValueEndChangeEvent event, float value, Actor actor) {
                        events.textAreaSelectionEnd(MathUtils.round(value));
                    }
                });
                rangeSlider.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        selectionLabel.setText("Selection: (" + simTextArea.selectionStart + ", " + simTextArea.selectionEnd + ")");
                    }
                });
                
                table.row();
                var label = new Label("Select All: ", DialogSceneComposer.skin, "scene-label-colored");
                table.add(label);
                
                table.row();
                var textButton = new TextButton(simTextArea.selectAll ? "TRUE" : "FALSE", DialogSceneComposer.skin, "scene-small");
                textButton.setChecked(simTextArea.selectAll);
                table.add(textButton).minWidth(100);
                textButton.addListener(DialogSceneComposer.main.getHandListener());
                textButton.addListener(new TextTooltip("Convenience option to select all text.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        events.textAreaSelectAll(textButton.isChecked());
                        rangeSlider.setDisabled(textButton.isChecked());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener textAreaAlignmentListener(final DialogSceneComposerEvents events,
                                                          final SimActor simActor) {
        var popTableClickListener = new PopTable.PopTableClickListener(DialogSceneComposer.skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var simTextArea = (DialogSceneComposerModel.SimTextArea) simActor;
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var table = new Table();
                popTable.add(table);
                
                var label = new Label("Alignment:", DialogSceneComposer.skin, "scene-label-colored");
                table.add(label).colspan(3);
                
                table.row();
                table.defaults().space(10).left().uniformX();
                var buttonGroup = new ButtonGroup<ImageTextButton>();
                var imageTextButton = new ImageTextButton("Top-Left", DialogSceneComposer.skin, "scene-checkbox-colored");
                var topLeft = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(DialogSceneComposer.main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the TextArea to the top left.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textAreaAlignment(Align.topLeft);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Top", DialogSceneComposer.skin, "scene-checkbox-colored");
                var top = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(DialogSceneComposer.main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the TextArea to the top center.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textAreaAlignment(Align.top);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Top-Right", DialogSceneComposer.skin, "scene-checkbox-colored");
                var topRight = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(DialogSceneComposer.main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the TextArea to the top right.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textAreaAlignment(Align.topRight);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                table.row();
                imageTextButton = new ImageTextButton("Left", DialogSceneComposer.skin, "scene-checkbox-colored");
                var left = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(DialogSceneComposer.main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the TextArea to the middle left.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textAreaAlignment(Align.left);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Center", DialogSceneComposer.skin, "scene-checkbox-colored");
                var center = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(DialogSceneComposer.main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the TextArea to the center.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textAreaAlignment(Align.center);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Right", DialogSceneComposer.skin, "scene-checkbox-colored");
                var right = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(DialogSceneComposer.main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the TextArea to the middle right.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textAreaAlignment(Align.right);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                table.row();
                imageTextButton = new ImageTextButton("Bottom-Left", DialogSceneComposer.skin, "scene-checkbox-colored");
                var bottomLeft = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(DialogSceneComposer.main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the TextArea to the bottom left.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textAreaAlignment(Align.bottomLeft);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Bottom", DialogSceneComposer.skin, "scene-checkbox-colored");
                var bottom = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(DialogSceneComposer.main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the TextArea to the bottom center.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textAreaAlignment(Align.bottom);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Bottom-Right", DialogSceneComposer.skin, "scene-checkbox-colored");
                var bottomRight = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(DialogSceneComposer.main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the TextArea to the bottom right.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textAreaAlignment(Align.bottomRight);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                switch (simTextArea.alignment) {
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
    
    public static EventListener textAreaFocusTraversalListener(final DialogSceneComposerEvents events,
                                                               SimActor simActor) {
        var simTextArea = (DialogSceneComposerModel.SimTextArea) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(DialogSceneComposer.skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Focus Traversal:", DialogSceneComposer.skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var textButton = new TextButton(simTextArea.focusTraversal ? "TRUE" : "FALSE", DialogSceneComposer.skin, "scene-small");
                textButton.setChecked(simTextArea.focusTraversal);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(DialogSceneComposer.main.getHandListener());
                textButton.addListener(new TextTooltip("Whether the TextArea allows for the use of focus traversal keys.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        events.textAreaFocusTraversal(textButton.isChecked());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener textAreaMaxLengthListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simTextArea = (DialogSceneComposerModel.SimTextArea) simActor;
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
                
                var label = new Label("Max Length:", DialogSceneComposer.skin, "scene-label-colored");
                table.add(label).right();
                
                var valueSpinner = new Spinner(simTextArea.maxLength, 1, true, Spinner.Orientation.RIGHT_STACK, DialogSceneComposer.skin, "scene");
                valueSpinner.setMinimum(0);
                table.add(valueSpinner).width(100).left();
                valueSpinner.getTextField().addListener(DialogSceneComposer.main.getIbeamListener());
                valueSpinner.getButtonMinus().addListener(DialogSceneComposer.main.getHandListener());
                valueSpinner.getButtonPlus().addListener(DialogSceneComposer.main.getHandListener());
                valueSpinner.addListener(new TextTooltip("The maximum length of characters allowed in the TextArea.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                valueSpinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textAreaMaxLength(valueSpinner.getValueAsInt());
                    }
                });
                
                table.row();
                label = new Label("(Set to 0 to show as many as possible)", DialogSceneComposer.skin, "scene-label-colored");
                table.add(label).colspan(2);
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener textAreaPreferredRowsListener(final DialogSceneComposerEvents events,
                                                              SimActor simActor) {
        var simTextArea = (DialogSceneComposerModel.SimTextArea) simActor;
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
                
                var label = new Label("Preferred Rows:", DialogSceneComposer.skin, "scene-label-colored");
                table.add(label).right();
                
                var valueSpinner = new Spinner(simTextArea.preferredRows, 1, true, Spinner.Orientation.RIGHT_STACK, DialogSceneComposer.skin, "scene");
                valueSpinner.setMinimum(1);
                table.add(valueSpinner).width(100).left();
                valueSpinner.getTextField().addListener(DialogSceneComposer.main.getIbeamListener());
                valueSpinner.getButtonMinus().addListener(DialogSceneComposer.main.getHandListener());
                valueSpinner.getButtonPlus().addListener(DialogSceneComposer.main.getHandListener());
                valueSpinner.addListener(new TextTooltip("The number of lines of text to help determine preferred height.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                valueSpinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textAreaPreferredRows(valueSpinner.getValueAsInt());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener textAreaDisabledListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simTextArea = (DialogSceneComposerModel.SimTextArea) simActor;
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
                var textButton = new TextButton(simTextArea.disabled ? "TRUE" : "FALSE", DialogSceneComposer.skin, "scene-small");
                textButton.setChecked(simTextArea.disabled);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(DialogSceneComposer.main.getHandListener());
                textButton.addListener(new TextTooltip("Whether the TextArea is disabled initially.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        events.textAreaDisabled(textButton.isChecked());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
}
