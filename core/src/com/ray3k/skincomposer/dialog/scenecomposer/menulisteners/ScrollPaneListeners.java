package com.ray3k.skincomposer.dialog.scenecomposer.menulisteners;

import com.badlogic.gdx.Input;
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
import com.ray3k.skincomposer.dialog.scenecomposer.StyleSelectorPopTable;

public class ScrollPaneListeners {
    public static EventListener scrollPaneNameListener(final DialogSceneComposer dialogSceneComposer) {
        var simScrollPane = (DialogSceneComposerModel.SimScrollPane) dialogSceneComposer.simActor;
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
                textField.setText(simScrollPane.name);
                popTable.add(textField).minWidth(150);
                textField.addListener(DialogSceneComposer.main.getIbeamListener());
                textField.addListener(new TextTooltip("The name of the ScrollPane to allow for convenient searching via Group#findActor().", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        dialogSceneComposer.events.scrollPaneName(textField.getText());
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
    
    public static EventListener scrollPaneStyleListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simScrollPane = (DialogSceneComposerModel.SimScrollPane) simActor;
        var popTableClickListener = new StyleSelectorPopTable(ScrollPane.class, simScrollPane.style == null ? "default" : simScrollPane.style.name) {
            @Override
            public void accepted(StyleData styleData) {
                events.scrollPaneStyle(styleData);
            }
        };
        
        return popTableClickListener;
    }
    
    public static EventListener scrollPaneKnobsListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simScrollPane = (DialogSceneComposerModel.SimScrollPane) simActor;
        var popTableClickListener = new PopTableClickListener(DialogSceneComposer.skin) {
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
                popTable.add(table).space(5).left();
                
                table.defaults().left().expandX();
                var imageTextButton = new ImageTextButton("Variable Size Knobs", DialogSceneComposer.skin, "scene-checkbox-colored");
                imageTextButton.setChecked(simScrollPane.variableSizeKnobs);
                table.add(imageTextButton);
                imageTextButton.addListener(DialogSceneComposer.main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Scroll knobs are sized based on getMaxX() or getMaxY()", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.scrollPaneVariableSizeKnobs(((ImageTextButton) actor).isChecked());
                    }
                });
                
                table.row();
                imageTextButton = new ImageTextButton("Fade Scroll Bars", DialogSceneComposer.skin, "scene-checkbox-colored");
                imageTextButton.setChecked(simScrollPane.fadeScrollBars);
                table.add(imageTextButton);
                imageTextButton.addListener(DialogSceneComposer.main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Scrollbars don't reduce the scrollable size and fade out if not used.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.scrollPaneFadeScrollBars(((ImageTextButton) actor).isChecked());
                    }
                });
                
                table.row();
                imageTextButton = new ImageTextButton("Visible", DialogSceneComposer.skin, "scene-checkbox-colored");
                imageTextButton.setChecked(simScrollPane.scrollBarsVisible);
                table.add(imageTextButton);
                imageTextButton.addListener(DialogSceneComposer.main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Shows or hides the scrollbars when Fade Scroll Bars is activated.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.scrollPaneScrollBarsVisible(((ImageTextButton) actor).isChecked());
                    }
                });
                
                table.row();
                imageTextButton = new ImageTextButton("Scroll Bars On Top", DialogSceneComposer.skin, "scene-checkbox-colored");
                imageTextButton.setChecked(simScrollPane.scrollBarsOnTop);
                table.add(imageTextButton);
                imageTextButton.addListener(DialogSceneComposer.main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Scrollbars don't reduce the scrollable size and fade out if not used.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.scrollPaneScrollBarsOnTop(((ImageTextButton) actor).isChecked());
                    }
                });
    
                table.row();
                imageTextButton = new ImageTextButton("Force Horizontal Scroll", DialogSceneComposer.skin, "scene-checkbox-colored");
                imageTextButton.setChecked(simScrollPane.forceScrollX);
                table.add(imageTextButton);
                imageTextButton.addListener(DialogSceneComposer.main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Forces the horizontal scroll bar to be enabled.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.scrollPaneForceScrollX(((ImageTextButton) actor).isChecked());
                    }
                });
    
                table.row();
                imageTextButton = new ImageTextButton("Force Vertical Scroll", DialogSceneComposer.skin, "scene-checkbox-colored");
                imageTextButton.setChecked(simScrollPane.forceScrollY);
                table.add(imageTextButton);
                imageTextButton.addListener(DialogSceneComposer.main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Forces the vertical scroll bar to be enabled.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.scrollPaneForceScrollY(((ImageTextButton) actor).isChecked());
                    }
                });
    
                table.row();
                imageTextButton = new ImageTextButton("Horizontal Scrolling Disabled", DialogSceneComposer.skin, "scene-checkbox-colored");
                imageTextButton.setChecked(simScrollPane.scrollingDisabledX);
                table.add(imageTextButton);
                imageTextButton.addListener(DialogSceneComposer.main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Horizontal Scrolling is disabled.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.scrollPaneScrollingDisabledX(((ImageTextButton) actor).isChecked());
                    }
                });
    
                table.row();
                imageTextButton = new ImageTextButton("Vertical Scrolling Disabled", DialogSceneComposer.skin, "scene-checkbox-colored");
                imageTextButton.setChecked(simScrollPane.scrollingDisabledY);
                table.add(imageTextButton);
                imageTextButton.addListener(DialogSceneComposer.main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Vertical scrolling is disabled.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.scrollPaneScrollingDisabledY(((ImageTextButton) actor).isChecked());
                    }
                });
                
                popTable.row();
                table = new Table();
                popTable.add(table);
                
                table.defaults().space(5).fillX();
                var label = new Label("Horizontal Scroll Position: ", DialogSceneComposer.skin, "scene-label-colored");
                table.add(label);
                
                var selectBox = new SelectBox<String>(DialogSceneComposer.skin, "scene");
                selectBox.setItems("Top", "Bottom");
                selectBox.setSelectedIndex(simScrollPane.scrollBarBottom ? 1 : 0);
                table.add(selectBox);
                selectBox.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.scrollPaneScrollBarBottom(((SelectBox) actor).getSelectedIndex() == 1);
                    }
                });
                
                table.row();
                label = new Label("Vertical Scroll Position: ", DialogSceneComposer.skin, "scene-label-colored");
                table.add(label);
                
                selectBox = new SelectBox<>(DialogSceneComposer.skin, "scene");
                selectBox.setItems("Left", "Right");
                selectBox.setSelectedIndex(simScrollPane.scrollBarRight ? 1 : 0);
                table.add(selectBox);
                selectBox.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.scrollPaneScrollBarRight(((SelectBox) actor).getSelectedIndex() == 1);
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener scrollPaneScrollListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simScrollPane = (DialogSceneComposerModel.SimScrollPane) simActor;
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
                
                table.defaults().left().spaceRight(5);
                var imageTextButton = new ImageTextButton("Clamp", DialogSceneComposer.skin, "scene-checkbox-colored");
                imageTextButton.setChecked(simScrollPane.clamp);
                table.add(imageTextButton);
                imageTextButton.addListener(DialogSceneComposer.main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Prevents scrolling out of the widget's bounds when using flick Scroll.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.scrollPaneClamp(((ImageTextButton) actor).isChecked());
                    }
                });
                
                table.row();
                imageTextButton = new ImageTextButton("Flick Scroll", DialogSceneComposer.skin, "scene-checkbox-colored");
                imageTextButton.setChecked(simScrollPane.flickScroll);
                table.add(imageTextButton);
                imageTextButton.addListener(DialogSceneComposer.main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Allow users to scroll by flicking the contents of the ScrollPanel", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.scrollPaneFlickScroll(((ImageTextButton) actor).isChecked());
                    }
                });
    
                table.row();
                var subTable = new Table();
                table.add(subTable);
                
                var label = new Label("Fling Time:", DialogSceneComposer.skin, "scene-label-colored");
                subTable.add(label).space(5);
                
                var spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, DialogSceneComposer.skin, "scene");
                spinner.setName("pad-left");
                spinner.setValue(simScrollPane.flingTime);
                subTable.add(spinner);
                spinner.getTextField().addListener(DialogSceneComposer.main.getIbeamListener());
                spinner.getButtonMinus().addListener(DialogSceneComposer.main.getHandListener());
                spinner.getButtonPlus().addListener(DialogSceneComposer.main.getHandListener());
                spinner.addListener(new TextTooltip("The amount of time in seconds that a fling will continue to scroll.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                spinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.scrollPaneFlingTime((float) ((Spinner) actor).getValue());
                    }
                });
                
                table.row();
                imageTextButton = new ImageTextButton("Overscroll X", DialogSceneComposer.skin, "scene-checkbox-colored");
                imageTextButton.setChecked(simScrollPane.overScrollX);
                table.add(imageTextButton);
                imageTextButton.addListener(DialogSceneComposer.main.getHandListener());
                imageTextButton.addListener(new TextTooltip("The widget can be scrolled passed the bounds horizontally and will snap back into place if flick scroll is enabled.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.scrollPaneOverScrollX(((ImageTextButton) actor).isChecked());
                    }
                });
    
                table.row();
                imageTextButton = new ImageTextButton("Overscroll Y", DialogSceneComposer.skin, "scene-checkbox-colored");
                imageTextButton.setChecked(simScrollPane.overScrollY);
                table.add(imageTextButton);
                imageTextButton.addListener(DialogSceneComposer.main.getHandListener());
                imageTextButton.addListener(new TextTooltip("The widget can be scrolled passed the bounds vertically and will snap back into place if flick scroll is enabled.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.scrollPaneOverScrollY(((ImageTextButton) actor).isChecked());
                    }
                });
    
                table.row();
                subTable = new Table();
                table.add(subTable);
    
                label = new Label("Overscroll Distance:", DialogSceneComposer.skin, "scene-label-colored");
                subTable.add(label).spaceRight(5);
    
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, DialogSceneComposer.skin, "scene");
                spinner.setName("pad-left");
                spinner.setValue(simScrollPane.overScrollDistance);
                subTable.add(spinner);
                spinner.getTextField().addListener(DialogSceneComposer.main.getIbeamListener());
                spinner.getButtonMinus().addListener(DialogSceneComposer.main.getHandListener());
                spinner.getButtonPlus().addListener(DialogSceneComposer.main.getHandListener());
                spinner.addListener(new TextTooltip("The distance in pixels that the user is allowed to scroll beyond the bounds if overscroll is enabled.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                spinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.scrollPaneOverScrollDistance((float) ((Spinner) actor).getValue());
                    }
                });
    
                subTable.row();
                label = new Label("Overscroll Speed Min:", DialogSceneComposer.skin, "scene-label-colored");
                subTable.add(label).spaceRight(5);
    
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, DialogSceneComposer.skin, "scene");
                spinner.setName("pad-left");
                spinner.setValue(simScrollPane.overScrollSpeedMin);
                subTable.add(spinner);
                spinner.getTextField().addListener(DialogSceneComposer.main.getIbeamListener());
                spinner.getButtonMinus().addListener(DialogSceneComposer.main.getHandListener());
                spinner.getButtonPlus().addListener(DialogSceneComposer.main.getHandListener());
                spinner.addListener(new TextTooltip("The minimum speed that scroll returns to the widget bounds when overscroll is enabled.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                spinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.scrollPaneOverScrollSpeedMin((float) ((Spinner) actor).getValue());
                    }
                });
    
                subTable.row();
                label = new Label("Overscroll Speed Max:", DialogSceneComposer.skin, "scene-label-colored");
                subTable.add(label).spaceRight(5);
    
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, DialogSceneComposer.skin, "scene");
                spinner.setName("pad-left");
                spinner.setValue(simScrollPane.overScrollSpeedMax);
                subTable.add(spinner);
                spinner.getTextField().addListener(DialogSceneComposer.main.getIbeamListener());
                spinner.getButtonMinus().addListener(DialogSceneComposer.main.getHandListener());
                spinner.getButtonPlus().addListener(DialogSceneComposer.main.getHandListener());
                spinner.addListener(new TextTooltip("The maximum speed that scroll returns to the widget bounds when overscroll is enabled.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                spinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.scrollPaneOverScrollSpeedMax((float) ((Spinner) actor).getValue());
                    }
                });
    
                table.row();
                imageTextButton = new ImageTextButton("Smooth Scrolling", DialogSceneComposer.skin, "scene-checkbox-colored");
                imageTextButton.setChecked(simScrollPane.smoothScrolling);
                table.add(imageTextButton);
                imageTextButton.addListener(DialogSceneComposer.main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Scrolling is interpolated instead of jumping to position immediately.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.scrollPaneSmoothScrolling(((ImageTextButton) actor).isChecked());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static Dialog showConfirmScrollPaneSetWidgetDialog(final DialogSceneComposer dialogSceneComposer,
                                                              WidgetType widgetType, PopTable popTable) {
        var simScrollPane = (DialogSceneComposerModel.SimScrollPane) dialogSceneComposer.simActor;
        if (simScrollPane.child == null) {
            popTable.hide();
            dialogSceneComposer.events.scrollPaneSetWidget(widgetType);
            return null;
        } else {
            var dialog = new Dialog("", DialogSceneComposer.skin, "scene-dialog") {
                @Override
                protected void result(Object object) {
                    if ((Boolean) object) {
                        popTable.hide();
                        dialogSceneComposer.events.scrollPaneSetWidget(widgetType);
                    }
                }
            };
            
            var root = dialog.getTitleTable();
            root.clear();
            
            root.add().uniform();
            
            var label = new Label("Confirm Overwrite Widget", DialogSceneComposer.skin, "scene-title");
            root.add(label).expandX();
            
            var button = new Button(DialogSceneComposer.skin, "scene-close");
            root.add(button).uniform();
            button.addListener(DialogSceneComposer.main.getHandListener());
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    dialog.hide();
                }
            });
            
            root = dialog.getContentTable();
            root.pad(10);
            
            label = new Label("This will overwrite the existing widget in the ScrollPane.\nAre you okay with that?", DialogSceneComposer.skin, "scene-label-colored");
            label.setWrap(true);
            label.setAlignment(Align.center);
            root.add(label).growX();
            
            dialog.getButtonTable().defaults().uniformX();
            var textButton = new TextButton("OK", DialogSceneComposer.skin, "scene-med");
            dialog.button(textButton, true);
            textButton.addListener(DialogSceneComposer.main.getHandListener());
            
            textButton = new TextButton("Cancel", DialogSceneComposer.skin, "scene-med");
            dialog.button(textButton, false);
            textButton.addListener(DialogSceneComposer.main.getHandListener());
            
            dialog.key(Input.Keys.ENTER, true).key(Input.Keys.SPACE, true);
            dialog.key(Input.Keys.ESCAPE, false);
            
            dialog.show(dialogSceneComposer.getStage());
            dialog.setSize(500, 200);
            dialog.setPosition((int) (dialogSceneComposer.getStage().getWidth() / 2f - dialog.getWidth() / 2f), (int) (dialogSceneComposer.getStage().getHeight() / 2f - dialog.getHeight() / 2f));
            
            return dialog;
        }
    }
}
