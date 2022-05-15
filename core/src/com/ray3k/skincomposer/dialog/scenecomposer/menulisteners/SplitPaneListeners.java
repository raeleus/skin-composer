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
import com.ray3k.stripe.PopTable;
import com.ray3k.stripe.PopTableClickListener;
import com.ray3k.stripe.Spinner;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerEvents;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerEvents.WidgetType;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimActor;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimSplitPane;
import com.ray3k.skincomposer.dialog.scenecomposer.StyleSelectorPopTable;
import static com.ray3k.skincomposer.Main.*;

import static com.ray3k.skincomposer.dialog.scenecomposer.menulisteners.ListenersUtils.TEXT_FIELD_WIDTH;
import com.ray3k.skincomposer.Main;

public class SplitPaneListeners {
    public static EventListener splitPaneNameListener(final DialogSceneComposer dialogSceneComposer) {
        var simSplitPane = (DialogSceneComposerModel.SimSplitPane) dialogSceneComposer.simActor;
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
                textField.setText(simSplitPane.name);
                popTable.add(textField).minWidth(TEXT_FIELD_WIDTH);
                textField.addListener(ibeamListener);
                textField.addListener((Main.makeTooltip("The name of the SplitPane to allow for convenient searching via Group#findActor().", tooltipManager, skin, "scene")));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        dialogSceneComposer.events.splitPaneName(textField.getText());
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
    
    public static EventListener splitPaneStyleListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simSplitPane = (DialogSceneComposerModel.SimSplitPane) simActor;
        var popTableClickListener = new StyleSelectorPopTable(SplitPane.class, simSplitPane.style == null ? "default-horizontal" : simSplitPane.style.name) {
            @Override
            public void accepted(StyleData styleData) {
                events.splitPaneStyle(styleData);
            }
        };
        
        return popTableClickListener;
    }
    
    public static EventListener splitPaneOrientationListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simSplitPane = (DialogSceneComposerModel.SimSplitPane) simActor;
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
                
                popTable.pad(10);
                var table = new Table();
                popTable.add(table).space(5);
                
                table.defaults().left().expandX();
                ButtonGroup buttonGroup = new ButtonGroup();
                var imageTextButton = new ImageTextButton("Horizontal", skin, "scene-checkbox-colored");
                imageTextButton.setProgrammaticChangeEvents(false);
                imageTextButton.setChecked(!simSplitPane.vertical);
                table.add(imageTextButton);
                buttonGroup.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Horizontal orientation of the widgets.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (true) events.splitPaneVertical(false);
                    }
                });
                
                table.row();
                imageTextButton = new ImageTextButton("Vertical", skin, "scene-checkbox-colored");
                imageTextButton.setProgrammaticChangeEvents(false);
                imageTextButton.setChecked(simSplitPane.vertical);
                table.add(imageTextButton);
                buttonGroup.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Vertical orientation of the widgets.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (true) events.splitPaneVertical(true);
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener splitPaneSplitListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simSplitPane = (DialogSceneComposerModel.SimSplitPane) simActor;
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
                
                var label = new Label("Split:", skin, "scene-label-colored");
                popTable.add(label).spaceRight(5);
                
                var splitSpinner = new Spinner(0, .1f, false, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                var splitMinSpinner = new Spinner(0, .1f, false, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                var splitMaxSpinner = new Spinner(0, .1f, false, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                splitSpinner.setName("pad-left");
                splitSpinner.setValue(simSplitPane.split);
                splitSpinner.setMinimum((double) simSplitPane.splitMin);
                splitSpinner.setMaximum((double) simSplitPane.splitMax);
                popTable.add(splitSpinner);
                splitSpinner.getTextField().addListener(ibeamListener);
                splitSpinner.getButtonMinus().addListener(handListener);
                splitSpinner.getButtonPlus().addListener(handListener);
                splitSpinner.addListener((Main.makeTooltip("The distance in pixels that the user is allowed to scroll beyond the bounds if overscroll is enabled.", tooltipManager, skin, "scene")));
                splitSpinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.splitPaneSplit((float) ((Spinner) actor).getValue());
                    }
                });
    
                popTable.row();
                label = new Label("Split Min:", skin, "scene-label-colored");
                popTable.add(label).spaceRight(5);
                
                splitMinSpinner.setName("pad-left");
                splitMinSpinner.setValue(simSplitPane.splitMin);
                splitMinSpinner.setMinimum(0);
                splitMinSpinner.setMaximum(simSplitPane.splitMax);
                popTable.add(splitMinSpinner);
                splitMinSpinner.getTextField().addListener(ibeamListener);
                splitMinSpinner.getButtonMinus().addListener(handListener);
                splitMinSpinner.getButtonPlus().addListener(handListener);
                splitMinSpinner.addListener((Main.makeTooltip("The minimum speed that scroll returns to the widget bounds when overscroll is enabled.", tooltipManager, skin, "scene")));
                splitMinSpinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.splitPaneSplitMin((float) ((Spinner) actor).getValue());
                        splitSpinner.setMinimum(splitMinSpinner.getValue());
                        splitMaxSpinner.setMinimum(splitMinSpinner.getValue());
                    }
                });
    
                popTable.row();
                label = new Label("Split Max:", skin, "scene-label-colored");
                popTable.add(label).spaceRight(5);
                
                splitMaxSpinner.setName("pad-left");
                splitMaxSpinner.setValue(simSplitPane.splitMax);
                splitMaxSpinner.setMinimum(simSplitPane.splitMin);
                splitMaxSpinner.setMaximum(1);
                popTable.add(splitMaxSpinner);
                splitMaxSpinner.getTextField().addListener(ibeamListener);
                splitMaxSpinner.getButtonMinus().addListener(handListener);
                splitMaxSpinner.getButtonPlus().addListener(handListener);
                splitMaxSpinner.addListener((Main.makeTooltip("The maximum speed that scroll returns to the widget bounds when overscroll is enabled.", tooltipManager, skin, "scene")));
                splitMaxSpinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.splitPaneSplitMax((float) ((Spinner) actor).getValue());
                        splitSpinner.setMaximum(splitMaxSpinner.getValue());
                        splitMinSpinner.setMaximum(splitMaxSpinner.getValue());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static Dialog showConfirmSplitPaneSetWidgetDialog(final DialogSceneComposer dialogSceneComposer,
                                                             WidgetType widgetType, PopTable popTable,
                                                             boolean firstWidget) {
        var simSplitPane = (SimSplitPane) dialogSceneComposer.simActor;
        if (firstWidget && simSplitPane.childFirst == null) {
            popTable.hide();
            dialogSceneComposer.events.splitPaneChildFirst(widgetType);
            return null;
        } else if (!firstWidget && simSplitPane.childSecond == null){
            popTable.hide();
            dialogSceneComposer.events.splitPaneChildSecond(widgetType);
            return null;
        } else {
            var dialog = new Dialog("", skin, "scene-dialog") {
                @Override
                protected void result(Object object) {
                    if ((Boolean) object) {
                        popTable.hide();
                        if (firstWidget) {
                            dialogSceneComposer.events.splitPaneChildFirst(widgetType);
                        } else {
                            dialogSceneComposer.events.splitPaneChildSecond(widgetType);
                        }
                    }
                }
            };
            
            var root = dialog.getTitleTable();
            root.clear();
            
            root.add().uniform();
            
            var label = new Label("Confirm Overwrite Widget", skin, "scene-title");
            root.add(label).expandX();
            
            var button = new Button(skin, "scene-close");
            root.add(button).uniform();
            button.addListener(handListener);
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    dialog.hide();
                }
            });
            
            root = dialog.getContentTable();
            root.pad(10);
            
            label = new Label("This will overwrite the existing widget in the cell.\nAre you okay with that?", skin, "scene-label-colored");
            label.setWrap(true);
            label.setAlignment(Align.center);
            root.add(label).growX();
            
            dialog.getButtonTable().defaults().uniformX();
            var textButton = new TextButton("OK", skin, "scene-med");
            dialog.button(textButton, true);
            textButton.addListener(handListener);
            
            textButton = new TextButton("Cancel", skin, "scene-med");
            dialog.button(textButton, false);
            textButton.addListener(handListener);
            
            dialog.key(Input.Keys.ENTER, true).key(Keys.NUMPAD_ENTER, true).key(Input.Keys.SPACE, true);
            dialog.key(Input.Keys.ESCAPE, false);
            
            dialog.show(dialogSceneComposer.getStage());
            dialog.setSize(500, 200);
            dialog.setPosition((int) (dialogSceneComposer.getStage().getWidth() / 2f - dialog.getWidth() / 2f), (int) (dialogSceneComposer.getStage().getHeight() / 2f - dialog.getHeight() / 2f));
            
            return dialog;
        }
    }
}
