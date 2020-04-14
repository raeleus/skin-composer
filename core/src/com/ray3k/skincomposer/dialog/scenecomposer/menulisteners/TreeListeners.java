package com.ray3k.skincomposer.dialog.scenecomposer.menulisteners;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.*;
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
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimActor;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimNode;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimTree;
import com.ray3k.skincomposer.dialog.scenecomposer.StyleSelectorPopTable;

public class TreeListeners {
    public static EventListener treeNameListener(final DialogSceneComposer dialogSceneComposer) {
        var simTree = (SimTree) dialogSceneComposer.simActor;
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
                textField.setText(simTree.name);
                popTable.add(textField).minWidth(150);
                textField.addListener(DialogSceneComposer.main.getIbeamListener());
                textField.addListener(new TextTooltip("The name of the Tree to allow for convenient searching via Group#findActor().", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        dialogSceneComposer.events.treeName(textField.getText());
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
    
    public static EventListener treeStyleListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simTree = (SimTree) simActor;
        var popTableClickListener = new StyleSelectorPopTable(Tree.class, simTree.style == null ? "default" : simTree.style.name) {
            @Override
            public void accepted(StyleData styleData) {
                events.treeStyle(styleData);
            }
        };
        
        return popTableClickListener;
    }
    
    public static EventListener treePaddingListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simTree = (SimTree) simActor;
        var popTableClickListener = new PopTableClickListener(DialogSceneComposer.skin) {
            @Override
            public void tableShown(Event event) {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var table = new Table();
                popTable.add(table);
                
                table.defaults().right().spaceRight(5);
                var label = new Label("Left:", DialogSceneComposer.skin, "scene-label-colored");
                table.add(label);
                
                var spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, DialogSceneComposer.skin, "scene");
                spinner.setValue(simTree.padLeft);
                table.add(spinner);
                spinner.getTextField().addListener(DialogSceneComposer.main.getIbeamListener());
                spinner.getButtonMinus().addListener(DialogSceneComposer.main.getHandListener());
                spinner.getButtonPlus().addListener(DialogSceneComposer.main.getHandListener());
                spinner.addListener(new TextTooltip("The padding on the left of the Tree.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                spinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.treePadLeft((float)((Spinner) actor).getValue());
                    }
                });
                
                table.row();
                label = new Label("Right:", DialogSceneComposer.skin, "scene-label-colored");
                table.add(label);
                
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, DialogSceneComposer.skin, "scene");
                spinner.setValue(simTree.padRight);
                table.add(spinner);
                spinner.getTextField().addListener(DialogSceneComposer.main.getIbeamListener());
                spinner.getButtonMinus().addListener(DialogSceneComposer.main.getHandListener());
                spinner.getButtonPlus().addListener(DialogSceneComposer.main.getHandListener());
                spinner.addListener(new TextTooltip("The padding on the right of the Tree.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                spinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.treePadLeft((float)((Spinner) actor).getValue());
                    }
                });
            }
        };
        return popTableClickListener;
    }
    
    public static EventListener treeSpacingListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simTree = (SimTree) simActor;
        var popTableClickListener = new PopTableClickListener(DialogSceneComposer.skin) {
            @Override
            public void tableShown(Event event) {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var table = new Table();
                popTable.add(table);
                
                table.defaults().right().spaceRight(5);
                var label = new Label("Icon Space Left:", DialogSceneComposer.skin, "scene-label-colored");
                table.add(label);
                
                var spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, DialogSceneComposer.skin, "scene");
                spinner.setValue(simTree.iconSpaceLeft);
                table.add(spinner);
                spinner.getTextField().addListener(DialogSceneComposer.main.getIbeamListener());
                spinner.getButtonMinus().addListener(DialogSceneComposer.main.getHandListener());
                spinner.getButtonPlus().addListener(DialogSceneComposer.main.getHandListener());
                spinner.addListener(new TextTooltip("The spacing on the left of the icon.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                spinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.treeIconSpaceLeft((float)((Spinner) actor).getValue());
                    }
                });
    
                table.row();
                label = new Label("Icon Space Right:", DialogSceneComposer.skin, "scene-label-colored");
                table.add(label);
    
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, DialogSceneComposer.skin, "scene");
                spinner.setValue(simTree.iconSpaceRight);
                table.add(spinner);
                spinner.getTextField().addListener(DialogSceneComposer.main.getIbeamListener());
                spinner.getButtonMinus().addListener(DialogSceneComposer.main.getHandListener());
                spinner.getButtonPlus().addListener(DialogSceneComposer.main.getHandListener());
                spinner.addListener(new TextTooltip("The spacing on the right of the icon.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                spinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.treeIconSpaceRight((float)((Spinner) actor).getValue());
                    }
                });
    
                table.row();
                label = new Label("Indent Spacing:", DialogSceneComposer.skin, "scene-label-colored");
                table.add(label);
    
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, DialogSceneComposer.skin, "scene");
                spinner.setValue(simTree.indentSpacing);
                table.add(spinner);
                spinner.getTextField().addListener(DialogSceneComposer.main.getIbeamListener());
                spinner.getButtonMinus().addListener(DialogSceneComposer.main.getHandListener());
                spinner.getButtonPlus().addListener(DialogSceneComposer.main.getHandListener());
                spinner.addListener(new TextTooltip("The indentation space for each indented node.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                spinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.treeIndentSpacing((float)((Spinner) actor).getValue());
                    }
                });
    
                table.row();
                label = new Label("Y Spacing:", DialogSceneComposer.skin, "scene-label-colored");
                table.add(label);
    
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, DialogSceneComposer.skin, "scene");
                spinner.setValue(simTree.ySpacing);
                table.add(spinner);
                spinner.getTextField().addListener(DialogSceneComposer.main.getIbeamListener());
                spinner.getButtonMinus().addListener(DialogSceneComposer.main.getHandListener());
                spinner.getButtonPlus().addListener(DialogSceneComposer.main.getHandListener());
                spinner.addListener(new TextTooltip("The vertical spacing between each node.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                spinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.treeYSpacing((float)((Spinner) actor).getValue());
                    }
                });
            }
        };
        return popTableClickListener;
    }
    
    public static Dialog showConfirmNodeSetWidgetDialog(final DialogSceneComposer dialogSceneComposer,
                                                        WidgetType widgetType,
                                                        PopTable popTable) {
        var simNode = (SimNode) dialogSceneComposer.simActor;
        if (simNode.actor == null) {
            popTable.hide();
            dialogSceneComposer.events.nodeSetWidget(widgetType);
            return null;
        } else {
            var dialog = new Dialog("", DialogSceneComposer.skin, "scene-dialog") {
                @Override
                protected void result(Object object) {
                    if ((Boolean) object) {
                        popTable.hide();
                        dialogSceneComposer.events.nodeSetWidget(widgetType);
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
            
            label = new Label("This will overwrite the existing widget in the node.\nAre you okay with that?", DialogSceneComposer.skin, "scene-label-colored");
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
    
    public static EventListener nodeOptionsListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simNode = (SimNode) simActor;
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
                popTable.add(table).space(5);
                
                table.defaults().left().expandX();
                var imageTextButton = new ImageTextButton("Expanded", DialogSceneComposer.skin, "scene-checkbox-colored");
                imageTextButton.setChecked(simNode.expanded);
                table.add(imageTextButton);
                imageTextButton.addListener(DialogSceneComposer.main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Sets whether the children are expanded and visible.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.nodeExpanded(((ImageTextButton) actor).isChecked());
                    }
                });
                
                table.row();
                imageTextButton = new ImageTextButton("Selectable", DialogSceneComposer.skin, "scene-checkbox-colored");
                imageTextButton.setChecked(simNode.selectable);
                table.add(imageTextButton);
                imageTextButton.addListener(DialogSceneComposer.main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Sets whether this node can be selected.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.nodeSelectable(((ImageTextButton) actor).isChecked());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
}
