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
import com.badlogic.gdx.utils.Align;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.data.StyleProperty;
import com.ray3k.skincomposer.dialog.DialogListener;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerEvents;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimActor;
import com.ray3k.skincomposer.dialog.scenecomposer.StyleSelectorPopTable;
import com.ray3k.stripe.PopTableClickListener;
import static com.ray3k.skincomposer.Main.*;

import static com.ray3k.skincomposer.dialog.scenecomposer.menulisteners.ListenersUtils.TEXT_FIELD_WIDTH;
import com.ray3k.skincomposer.Main;

public class LabelListeners {
    public static EventListener labelNameListener(final DialogSceneComposer dialogSceneComposer) {
        var simLabel = (DialogSceneComposerModel.SimLabel) dialogSceneComposer.simActor;
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
                textField.setText(simLabel.name);
                popTable.add(textField).minWidth(TEXT_FIELD_WIDTH);
                textField.addListener(ibeamListener);
                textField.addListener((Main.makeTooltip("The name of the Label to allow for convenient searching via Group#findActor().", tooltipManager, skin, "scene")));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        dialogSceneComposer.events.labelName(textField.getText());
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
    
    public static EventListener labelTextListener(final DialogSceneComposer dialogSceneComposer) {
        var simLabel = (DialogSceneComposerModel.SimLabel) dialogSceneComposer.simActor;
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
                textField.setText(simLabel.text);
                popTable.add(textField).minWidth(TEXT_FIELD_WIDTH);
                textField.addListener(ibeamListener);
                textField.addListener((Main.makeTooltip("The text for the Label.", tooltipManager, skin, "scene")));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        dialogSceneComposer.events.labelText(textField.getText());
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
    
    public static EventListener labelStyleListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simLabel = (DialogSceneComposerModel.SimLabel) simActor;
        var popTableClickListener = new StyleSelectorPopTable(Label.class, simLabel.style == null ? "default" : simLabel.style.name) {
            @Override
            public void accepted(StyleData styleData) {
                events.labelStyle(styleData);
            }
        };
        
        return popTableClickListener;
    }
    
    public static EventListener labelColorListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simLabel = (DialogSceneComposerModel.SimLabel) simActor;
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
                imageButton.getImage().setColor(simLabel.color == null ? Color.WHITE : simLabel.color.color);
                popTable.add(imageButton).minWidth(100);
                imageButton.addListener(handListener);
                imageButton.addListener((Main.makeTooltip("Select the color of the Label.", tooltipManager, skin, "scene")));
                imageButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        popTable.hide();
                        dialogFactory.showDialogColors(new StyleProperty(), (colorData, pressedCancel) -> {
                            if (!pressedCancel) {
                                events.labelColor(colorData);
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
                label = new Label(simLabel.color == null ? "white" : simLabel.color.getName(), skin, "scene-label-colored");
                popTable.add(label);
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener labelTextAlignmentListener(final DialogSceneComposerEvents events,
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
                var simLabel = (DialogSceneComposerModel.SimLabel) simActor;
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var table = new Table();
                popTable.add(table);
                
                var label = new Label("Text Alignment:", skin, "scene-label-colored");
                table.add(label).colspan(3);
                
                table.row();
                table.defaults().space(10).left().uniformX();
                var buttonGroup = new ButtonGroup<ImageTextButton>();
                var imageTextButton = new ImageTextButton("Top-Left", skin, "scene-checkbox-colored");
                var topLeft = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the text of the label to the top left.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.labelAlignment(Align.topLeft);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Top", skin, "scene-checkbox-colored");
                var top = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the text of the label to the top center.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.labelAlignment(Align.top);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Top-Right", skin, "scene-checkbox-colored");
                var topRight = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the text of the label to the top right.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.labelAlignment(Align.topRight);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                table.row();
                imageTextButton = new ImageTextButton("Left", skin, "scene-checkbox-colored");
                var left = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the text of the label to the middle left.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.labelAlignment(Align.left);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Center", skin, "scene-checkbox-colored");
                var center = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the text of the label to the center.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.labelAlignment(Align.center);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Right", skin, "scene-checkbox-colored");
                var right = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the text of the label to the middle right.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.labelAlignment(Align.right);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                table.row();
                imageTextButton = new ImageTextButton("Bottom-Left", skin, "scene-checkbox-colored");
                var bottomLeft = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the text of the label the bottom left.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.labelAlignment(Align.bottomLeft);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Bottom", skin, "scene-checkbox-colored");
                var bottom = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the text of the label to the bottom center.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.labelAlignment(Align.bottom);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Bottom-Right", skin, "scene-checkbox-colored");
                var bottomRight = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the text of the label to the bottom right.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.labelAlignment(Align.bottomRight);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                switch (simLabel.textAlignment) {
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
    
    public static EventListener labelEllipsisListener(final DialogSceneComposer dialogSceneComposer) {
        var simLabel = (DialogSceneComposerModel.SimLabel) dialogSceneComposer.simActor;
        var textField = new TextField("", skin, "scene");
        var popTableClickListener = new PopTableClickListener(skin) {
            {
                getPopTable().key(Keys.ENTER, popTable::hide);
                getPopTable().key(Keys.NUMPAD_ENTER, popTable::hide);
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
    
                var label = new Label("Ellipsis:", skin, "scene-label-colored");
                popTable.add(label);
    
                popTable.row();
                var textButton = new TextButton(simLabel.ellipsis ? "TRUE" : "FALSE", skin, "scene-small");
                textButton.setChecked(simLabel.ellipsis);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(handListener);
                textButton.addListener((Main.makeTooltip("Whether ellipsis mode is activated.", tooltipManager, skin, "scene")));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        textField.setDisabled(!textButton.isChecked());
                        dialogSceneComposer.events.labelEllipsis(textButton.isChecked(), textField.getText());
                    }
                });
                
                label = new Label("Ellipsis String:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setDisabled(!textButton.isChecked());
                textField.setText(simLabel.ellipsisString);
                popTable.add(textField).minWidth(TEXT_FIELD_WIDTH);
                textField.addListener(ibeamListener);
                textField.addListener((Main.makeTooltip("The String to punctuate the label with when the text is too long.", tooltipManager, skin, "scene")));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        dialogSceneComposer.events.labelEllipsis(textButton.isChecked(), textField.getText());
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
    
    public static EventListener labelWrapListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simLabel = (DialogSceneComposerModel.SimLabel) simActor;
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
                
                var label = new Label("Wrap:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var textButton = new TextButton(simLabel.wrap ? "TRUE" : "FALSE", skin, "scene-small");
                textButton.setChecked(simLabel.wrap);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(handListener);
                textButton.addListener((Main.makeTooltip("Whether the Label wraps when the the text exceeds the width of the label.", tooltipManager, skin, "scene")));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        events.labelWrap(textButton.isChecked());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
}
