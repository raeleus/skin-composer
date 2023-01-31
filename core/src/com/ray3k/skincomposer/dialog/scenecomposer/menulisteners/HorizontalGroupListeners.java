package com.ray3k.skincomposer.dialog.scenecomposer.menulisteners;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.ray3k.skincomposer.Main;
import com.ray3k.stripe.PopTable;
import com.ray3k.stripe.PopTableClickListener;
import com.ray3k.stripe.Spinner;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerEvents;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerEvents.WidgetType;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimActor;
import static com.ray3k.skincomposer.Main.*;

import static com.ray3k.skincomposer.dialog.scenecomposer.menulisteners.ListenersUtils.TEXT_FIELD_WIDTH;

public class HorizontalGroupListeners {
    public static EventListener horizontalGroupNameListener(final DialogSceneComposer dialogSceneComposer) {
        var simHorizontalGroup = (DialogSceneComposerModel.SimHorizontalGroup) dialogSceneComposer.simActor;
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
                textField.setText(simHorizontalGroup.name);
                popTable.add(textField).minWidth(TEXT_FIELD_WIDTH);
                textField.addListener(ibeamListener);
                textField.addListener((Main.makeTooltip("The name of the HorizontalGroup to allow for convenient searching via Group#findActor().", tooltipManager, skin, "scene")));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        dialogSceneComposer.events.horizontalGroupName(textField.getText());
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
    
    public static EventListener horizontalGroupExpandFillGrowListener(final DialogSceneComposerEvents events,
                                                                      SimActor simActor) {
        var simHorizontalGroup = (DialogSceneComposerModel.SimHorizontalGroup) simActor;
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
                
                var expand = new ImageTextButton("Expand", skin, "scene-checkbox-colored");
                var fill = new ImageTextButton("Fill", skin, "scene-checkbox-colored");
                var grow = new ImageTextButton("Grow", skin, "scene-checkbox-colored");
                
                var changeListener = new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.horizontalGroupExpand(expand.isChecked());
                        events.horizontalGroupFill(fill.isChecked());
                    }
                };
                
                var table = new Table();
                popTable.add(table);
                
                table.defaults().left().spaceRight(5);
                expand.setChecked(simHorizontalGroup.expand);
                expand.setProgrammaticChangeEvents(false);
                table.add(expand);
                expand.addListener(handListener);
                expand.addListener((Main.makeTooltip("Set the widgets to expand to the available space.", tooltipManager, skin, "scene")));
                expand.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (expand.isChecked() && fill.isChecked()) {
                            grow.setChecked(true);
                        } else {
                            grow.setChecked(false);
                        }
                    }
                });
                expand.addListener(changeListener);
                
                table.row();
                fill.setChecked(simHorizontalGroup.fill);
                fill.setProgrammaticChangeEvents(false);
                table.add(fill);
                fill.addListener(handListener);
                fill.addListener((Main.makeTooltip("Sets the widgets to fill the entire space.", tooltipManager, skin, "scene")));
                fill.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (expand.isChecked() && fill.isChecked()) {
                            grow.setChecked(true);
                        } else {
                            grow.setChecked(false);
                        }
                    }
                });
                fill.addListener(changeListener);
                
                table.row();
                grow.setChecked(simHorizontalGroup.expand && simHorizontalGroup.fill);
                grow.setProgrammaticChangeEvents(false);
                table.add(grow);
                grow.addListener(handListener);
                grow.addListener((Main.makeTooltip("Sets the widgets to expand and fill.", tooltipManager, skin, "scene")));
                grow.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        expand.setChecked(grow.isChecked());
                        fill.setChecked(grow.isChecked());
                    }
                });
                grow.addListener(changeListener);
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener horizontalGroupPaddingSpacingListener(final DialogSceneComposerEvents events,
                                                                      SimActor simActor) {
        var simHorizontalGroup = (DialogSceneComposerModel.SimHorizontalGroup) simActor;
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
                
                var label = new Label("Padding:", skin, "scene-label-colored");
                table.add(label).colspan(2);
                
                table.row();
                table.defaults().right().spaceRight(5);
                label = new Label("Left:", skin, "scene-label-colored");
                table.add(label);
                
                var spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setValue(simHorizontalGroup.padLeft);
                table.add(spinner);
                spinner.getTextField().addListener(ibeamListener);
                spinner.getButtonMinus().addListener(handListener);
                spinner.getButtonPlus().addListener(handListener);
                spinner.addListener((Main.makeTooltip("The padding to the left of the widgets.", tooltipManager, skin, "scene")));
                spinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.horizontalGroupPadLeft(((Spinner) actor).getValueAsInt());
                    }
                });
                
                table.row();
                label = new Label("Right:", skin, "scene-label-colored");
                table.add(label);
                
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setValue(simHorizontalGroup.padRight);
                spinner.setName("padding-right");
                table.add(spinner);
                spinner.getTextField().addListener(ibeamListener);
                spinner.getButtonMinus().addListener(handListener);
                spinner.getButtonPlus().addListener(handListener);
                spinner.addListener((Main.makeTooltip("The padding to the right of the widgets.", tooltipManager, skin, "scene")));
                spinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.horizontalGroupPadRight(((Spinner) actor).getValueAsInt());
                    }
                });
                
                table.row();
                label = new Label("Top:", skin, "scene-label-colored");
                table.add(label);
                
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setValue(simHorizontalGroup.padTop);
                spinner.setName("padding-top");
                table.add(spinner);
                spinner.getTextField().addListener(ibeamListener);
                spinner.getButtonMinus().addListener(handListener);
                spinner.getButtonPlus().addListener(handListener);
                spinner.addListener((Main.makeTooltip("The padding to the top of the widgets.", tooltipManager, skin, "scene")));
                spinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.horizontalGroupPadTop(((Spinner) actor).getValueAsInt());
                    }
                });
                
                table.row();
                label = new Label("Bottom:", skin, "scene-label-colored");
                table.add(label);
                
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setValue(simHorizontalGroup.padBottom);
                spinner.setName("padding-bottom");
                table.add(spinner);
                spinner.getTextField().addListener(ibeamListener);
                spinner.getButtonMinus().addListener(handListener);
                spinner.getButtonPlus().addListener(handListener);
                spinner.addListener((Main.makeTooltip("The padding to the bottom of the widgets.", tooltipManager, skin, "scene")));
                spinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.horizontalGroupPadBottom(((Spinner) actor).getValueAsInt());
                    }
                });
                
                var image = new Image(skin, "scene-menu-divider");
                popTable.add(image).space(10).growY();
                
                table = new Table();
                popTable.add(table);
                
                table.defaults().right().spaceRight(5);
                label = new Label("Spacing:", skin, "scene-label-colored");
                table.add(label);
                
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setValue(simHorizontalGroup.space);
                spinner.setName("spacing-left");
                table.add(spinner);
                spinner.getTextField().addListener(ibeamListener);
                spinner.getButtonMinus().addListener(handListener);
                spinner.getButtonPlus().addListener(handListener);
                spinner.addListener((Main.makeTooltip("The spacing between the widgets.", tooltipManager, skin, "scene")));
                spinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.horizontalGroupSpace(((Spinner) actor).getValueAsInt());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener horizontalGroupWrapListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simHorizontalGroup = (DialogSceneComposerModel.SimHorizontalGroup) simActor;
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
                
                table.defaults().left().spaceRight(5);
                var imageTextButton = new ImageTextButton("Wrap", skin, "scene-checkbox-colored");
                imageTextButton.setChecked(simHorizontalGroup.wrap);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Whether the widgets will wrap to the next line.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.horizontalGroupWrap(imageTextButton.isChecked());
                    }
                });
                
                table.row();
                var label = new Label("Wrap Space:", skin, "scene-label-colored");
                table.add(label);
    
                var spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setValue(simHorizontalGroup.padRight);
                spinner.setName("padding-right");
                table.add(spinner);
                spinner.getTextField().addListener(ibeamListener);
                spinner.getButtonMinus().addListener(handListener);
                spinner.getButtonPlus().addListener(handListener);
                spinner.addListener((Main.makeTooltip("The vertical space between rows when wrap is enabled.", tooltipManager, skin, "scene")));
                spinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.horizontalGroupWrapSpace(((Spinner) actor).getValueAsInt());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener horizontalGroupAlignmentListener(final DialogSceneComposerEvents events,
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
                var simHorizontalGroup = (DialogSceneComposerModel.SimHorizontalGroup) simActor;
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
                imageTextButton.addListener((Main.makeTooltip("Align the widgets to the top left.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.horizontalGroupAlignment(Align.topLeft);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Top", skin, "scene-checkbox-colored");
                var top = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the widgets to the top center.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.horizontalGroupAlignment(Align.top);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Top-Right", skin, "scene-checkbox-colored");
                var topRight = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the widgets to the top right.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.horizontalGroupAlignment(Align.topRight);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                table.row();
                imageTextButton = new ImageTextButton("Left", skin, "scene-checkbox-colored");
                var left = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the widgets to the middle left.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.horizontalGroupAlignment(Align.left);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Center", skin, "scene-checkbox-colored");
                var center = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the widgets to the center.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.horizontalGroupAlignment(Align.center);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Right", skin, "scene-checkbox-colored");
                var right = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the widgets to the middle right.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.horizontalGroupAlignment(Align.right);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                table.row();
                imageTextButton = new ImageTextButton("Bottom-Left", skin, "scene-checkbox-colored");
                var bottomLeft = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the widgets to the bottom left.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.horizontalGroupAlignment(Align.bottomLeft);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Bottom", skin, "scene-checkbox-colored");
                var bottom = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the widgets to the bottom center.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.horizontalGroupAlignment(Align.bottom);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Bottom-Right", skin, "scene-checkbox-colored");
                var bottomRight = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the widgets to the bottom right.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.horizontalGroupAlignment(Align.bottomRight);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                switch (simHorizontalGroup.alignment) {
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
    
    public static EventListener horizontalGroupRowAlignmentListener(final DialogSceneComposerEvents events,
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
                var simHorizontalGroup = (DialogSceneComposerModel.SimHorizontalGroup) simActor;
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
                imageTextButton.addListener((Main.makeTooltip("Align the widgets to the top left.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.horizontalGroupRowAlignment(Align.topLeft);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Top", skin, "scene-checkbox-colored");
                var top = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the widgets to the top center.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.horizontalGroupRowAlignment(Align.top);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Top-Right", skin, "scene-checkbox-colored");
                var topRight = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the widgets to the top right.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.horizontalGroupRowAlignment(Align.topRight);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                table.row();
                imageTextButton = new ImageTextButton("Left", skin, "scene-checkbox-colored");
                var left = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the widgets to the middle left.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.horizontalGroupRowAlignment(Align.left);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Center", skin, "scene-checkbox-colored");
                var center = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the widgets to the center.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.horizontalGroupRowAlignment(Align.center);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Right", skin, "scene-checkbox-colored");
                var right = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the widgets to the middle right.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.horizontalGroupRowAlignment(Align.right);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                table.row();
                imageTextButton = new ImageTextButton("Bottom-Left", skin, "scene-checkbox-colored");
                var bottomLeft = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the widgets to the bottom left.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.horizontalGroupRowAlignment(Align.bottomLeft);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Bottom", skin, "scene-checkbox-colored");
                var bottom = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the widgets to the bottom center.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.horizontalGroupRowAlignment(Align.bottom);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Bottom-Right", skin, "scene-checkbox-colored");
                var bottomRight = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the widgets to the bottom right.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.horizontalGroupRowAlignment(Align.bottomRight);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                switch (simHorizontalGroup.alignment) {
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
    
    public static EventListener horizontalGroupReverseListener(final DialogSceneComposerEvents events,
                                                               SimActor simActor) {
        var simHorizontalGroup = (DialogSceneComposerModel.SimHorizontalGroup) simActor;
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
                
                table.defaults().left().spaceRight(5);
                var imageTextButton = new ImageTextButton("Reverse", skin, "scene-checkbox-colored");
                imageTextButton.setChecked(simHorizontalGroup.reverse);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Reverse the display order of the widgets.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.horizontalGroupReverse(imageTextButton.isChecked());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static void horizontalGroupAddChild(DialogSceneComposerEvents events, WidgetType widgetType,
                                               PopTable popTable) {
        popTable.hide();
        events.horizontalGroupAddChild(widgetType);
    }
}
