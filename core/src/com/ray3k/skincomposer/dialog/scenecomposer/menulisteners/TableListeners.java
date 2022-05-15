package com.ray3k.skincomposer.dialog.scenecomposer.menulisteners;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.ray3k.stripe.PopTable;
import com.ray3k.stripe.PopTable.KeyListener;
import com.ray3k.stripe.PopTableClickListener;
import com.ray3k.stripe.Spinner;
import com.ray3k.skincomposer.data.StyleProperty;
import com.ray3k.skincomposer.dialog.DialogListener;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerEvents;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerEvents.WidgetType;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimActor;
import com.ray3k.skincomposer.utils.IntPair;
import static com.ray3k.skincomposer.Main.*;

import static com.ray3k.skincomposer.dialog.scenecomposer.menulisteners.ListenersUtils.TEXT_FIELD_WIDTH;
import com.ray3k.skincomposer.Main;

public class TableListeners {
    public static EventListener tableNameListener(final DialogSceneComposer dialogSceneComposer) {
        var simTable = (DialogSceneComposerModel.SimTable) dialogSceneComposer.simActor;
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
    
                var label = new Label("Name:", skin, "scene-label-colored");
                popTable.add(label);
    
                popTable.row();
                var textField = new TextField("", skin, "scene");
                textField.setText(simTable.name);
                popTable.add(textField).minWidth(TEXT_FIELD_WIDTH);
                textField.addListener(ibeamListener);
                textField.addListener((Main.makeTooltip("The name of the table to allow for convenient searching via Group#findActor().", tooltipManager, skin, "scene")));
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
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        dialogSceneComposer.events.tableName(textField.getText());
                    }
                });
    
                dialogSceneComposer.getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener tableColorListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simTable = (DialogSceneComposerModel.SimTable) simActor;
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
                imageButton.getImage().setColor(simTable.color == null ? Color.WHITE : simTable.color.color);
                popTable.add(imageButton).minWidth(100);
                imageButton.addListener(handListener);
                imageButton.addListener((Main.makeTooltip("Select the color of the table background and of the table contents.", tooltipManager, skin, "scene")));
    
                popTable.row();
                var colorLabel = new Label(simTable.color == null ? "No Color" : simTable.color.getName(), skin, "scene-label-colored");
                popTable.add(colorLabel);
    
                imageButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        popTable.hide();
                        dialogFactory.showDialogColors(new StyleProperty(), (colorData, pressedCancel) -> {
                            if (!pressedCancel) {
                                events.tableColor(colorData);
                                imageButton.getImage().setColor(colorData == null ? Color.WHITE : colorData.color);
                                colorLabel.setText(colorData == null ? "No Color" : colorData.getName());
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
            }
        };
        
        popTableClickListener.update();
    
        return popTableClickListener;
    }
    
    public static EventListener tablePaddingListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simTable = (DialogSceneComposerModel.SimTable) simActor;
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
                popTable.defaults().reset();
    
                var changeListener = new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        TextButton paddingButton = popTable.findActor("padding-button");
                        paddingButton.setText(paddingButton.isChecked() ? "Enabled" : "Disabled");
                        
                        Spinner padLeft = popTable.findActor("pad-left");
                        padLeft.setTouchable(paddingButton.isChecked() ? Touchable.enabled : Touchable.disabled);
                        padLeft.getTextField().setDisabled(!paddingButton.isChecked());
                        padLeft.getButtonMinus().setDisabled(!paddingButton.isChecked());
                        padLeft.getButtonPlus().setDisabled(!paddingButton.isChecked());
                        
                        Spinner padRight = popTable.findActor("pad-right");
                        padRight.setTouchable(paddingButton.isChecked() ? Touchable.enabled : Touchable.disabled);
                        padRight.getTextField().setDisabled(!paddingButton.isChecked());
                        padRight.getButtonMinus().setDisabled(!paddingButton.isChecked());
                        padRight.getButtonPlus().setDisabled(!paddingButton.isChecked());
                        
                        Spinner padTop = popTable.findActor("pad-top");
                        padTop.setTouchable(paddingButton.isChecked() ? Touchable.enabled : Touchable.disabled);
                        padTop.getTextField().setDisabled(!paddingButton.isChecked());
                        padTop.getButtonMinus().setDisabled(!paddingButton.isChecked());
                        padTop.getButtonPlus().setDisabled(!paddingButton.isChecked());
                        
                        Spinner padBottom = popTable.findActor("pad-bottom");
                        padBottom.setTouchable(paddingButton.isChecked() ? Touchable.enabled : Touchable.disabled);
                        padBottom.getTextField().setDisabled(!paddingButton.isChecked());
                        padBottom.getButtonMinus().setDisabled(!paddingButton.isChecked());
                        padBottom.getButtonPlus().setDisabled(!paddingButton.isChecked());
                        
                        events.tablePadding(paddingButton.isChecked(), (float) padLeft.getValue(), (float)  padRight.getValue(), (float)  padTop.getValue(), (float)  padBottom.getValue());
                    }
                };
    
                var label = new Label("Padding", skin, "scene-label-colored");
                popTable.add(label).colspan(2);
    
                popTable.row();
                popTable.defaults().right().spaceRight(5);
                var textButton = new TextButton(simTable.paddingEnabled ? "Enabled" : "Disabled", skin, "scene-small");
                textButton.setChecked(simTable.paddingEnabled);
                textButton.setName("padding-button");
                popTable.add(textButton).minWidth(150).colspan(2);
                textButton.addListener(handListener);
                textButton.addListener((Main.makeTooltip("Whether the button is checked initially.", tooltipManager, skin, "scene")));
                textButton.addListener(changeListener);
                
                popTable.row();
                label = new Label("Left:", skin, "scene-label-colored");
                popTable.add(label);
    
                var spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("pad-left");
                spinner.setValue(simTable.padLeft);
                spinner.setTouchable(simTable.paddingEnabled ? Touchable.enabled : Touchable.disabled);
                spinner.getTextField().setDisabled(!simTable.paddingEnabled);
                spinner.getButtonMinus().setDisabled(!simTable.paddingEnabled);
                spinner.getButtonPlus().setDisabled(!simTable.paddingEnabled);
                popTable.add(spinner);
                spinner.getTextField().addListener(ibeamListener);
                spinner.getButtonMinus().addListener(handListener);
                spinner.getButtonPlus().addListener(handListener);
                spinner.addListener((Main.makeTooltip("The padding on the left of the contents.", tooltipManager, skin, "scene")));
                spinner.addListener(changeListener);
    
                popTable.row();
                label = new Label("Right:", skin, "scene-label-colored");
                popTable.add(label);
    
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("pad-right");
                spinner.setValue(simTable.padRight);
                spinner.setTouchable(simTable.paddingEnabled ? Touchable.enabled : Touchable.disabled);
                spinner.getTextField().setDisabled(!simTable.paddingEnabled);
                spinner.getButtonMinus().setDisabled(!simTable.paddingEnabled);
                spinner.getButtonPlus().setDisabled(!simTable.paddingEnabled);
                popTable.add(spinner);
                spinner.getTextField().addListener(ibeamListener);
                spinner.getButtonMinus().addListener(handListener);
                spinner.getButtonPlus().addListener(handListener);
                spinner.addListener((Main.makeTooltip("The padding on the right of the contents.", tooltipManager, skin, "scene")));
                spinner.addListener(changeListener);
    
                popTable.row();
                label = new Label("Top:", skin, "scene-label-colored");
                popTable.add(label);
    
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("pad-top");
                spinner.setValue(simTable.padTop);
                spinner.setTouchable(simTable.paddingEnabled ? Touchable.enabled : Touchable.disabled);
                spinner.getTextField().setDisabled(!simTable.paddingEnabled);
                spinner.getButtonMinus().setDisabled(!simTable.paddingEnabled);
                spinner.getButtonPlus().setDisabled(!simTable.paddingEnabled);
                popTable.add(spinner);
                spinner.getTextField().addListener(ibeamListener);
                spinner.getButtonMinus().addListener(handListener);
                spinner.getButtonPlus().addListener(handListener);
                spinner.addListener((Main.makeTooltip("The padding on the top of the contents.", tooltipManager, skin, "scene")));
                spinner.addListener(changeListener);
    
                popTable.row();
                label = new Label("Bottom:", skin, "scene-label-colored");
                popTable.add(label);
    
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("pad-bottom");
                spinner.setValue(simTable.padBottom);
                spinner.setTouchable(simTable.paddingEnabled ? Touchable.enabled : Touchable.disabled);
                spinner.getTextField().setDisabled(!simTable.paddingEnabled);
                spinner.getButtonMinus().setDisabled(!simTable.paddingEnabled);
                spinner.getButtonPlus().setDisabled(!simTable.paddingEnabled);
                popTable.add(spinner);
                spinner.getTextField().addListener(ibeamListener);
                spinner.getButtonMinus().addListener(handListener);
                spinner.getButtonPlus().addListener(handListener);
                spinner.addListener((Main.makeTooltip("The padding on the bottom of the contents.", tooltipManager, skin, "scene")));
                spinner.addListener(changeListener);
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener tableAlignListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simTable = (DialogSceneComposerModel.SimTable) simActor;
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
                imageTextButton.addListener((Main.makeTooltip("Align the contents to the top left.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.tableAlignment(Align.topLeft);
                    }
                });
                buttonGroup.add(imageTextButton);
    
                imageTextButton = new ImageTextButton("Top", skin, "scene-checkbox-colored");
                var top = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the contents to the top center.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.tableAlignment(Align.top);
                    }
                });
                buttonGroup.add(imageTextButton);
    
                imageTextButton = new ImageTextButton("Top-Right", skin, "scene-checkbox-colored");
                var topRight = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the contents to the top right.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.tableAlignment(Align.topRight);
                    }
                });
                buttonGroup.add(imageTextButton);
    
                table.row();
                imageTextButton = new ImageTextButton("Left", skin, "scene-checkbox-colored");
                var left = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the contents to the middle left.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.tableAlignment(Align.left);
                    }
                });
                buttonGroup.add(imageTextButton);
    
                imageTextButton = new ImageTextButton("Center", skin, "scene-checkbox-colored");
                var center = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the contents to the center.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.tableAlignment(Align.center);
                    }
                });
                buttonGroup.add(imageTextButton);
    
                imageTextButton = new ImageTextButton("Right", skin, "scene-checkbox-colored");
                var right = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the contents to the middle right.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.tableAlignment(Align.right);
                    }
                });
                buttonGroup.add(imageTextButton);
    
                table.row();
                imageTextButton = new ImageTextButton("Bottom-Left", skin, "scene-checkbox-colored");
                var bottomLeft = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the contents to the bottom left.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.tableAlignment(Align.bottomLeft);
                    }
                });
                buttonGroup.add(imageTextButton);
    
                imageTextButton = new ImageTextButton("Bottom", skin, "scene-checkbox-colored");
                var bottom = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the contents to the bottom center.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.tableAlignment(Align.bottom);
                    }
                });
                buttonGroup.add(imageTextButton);
    
                imageTextButton = new ImageTextButton("Bottom-Right", skin, "scene-checkbox-colored");
                var bottomRight = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the contents to the bottom right.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.tableAlignment(Align.bottomRight);
                    }
                });
                buttonGroup.add(imageTextButton);
    
                switch (simTable.alignment) {
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
    
    public static EventListener tableSetCellsListener(final DialogSceneComposerEvents events) {
        var popTableClickListener = new PopTableClickListener(skin, "dark");
        var popTable = popTableClickListener.getPopTable();
        popTable.key(Keys.ESCAPE, popTable::hide);
        
        var label = new Label("Erase contents\nand create\nnew cells:", skin, "scene-label");
        label.setAlignment(Align.center);
        popTable.add(label);
        label.addListener((Main.makeTooltip("Sets the cells for this table. This will erase the existing contents.", tooltipManager, skin, "scene")));
        
        popTable.row();
        var table = new Table();
        popTable.add(table);
        
        table.pad(10).padTop(0);
        var buttons = new Button[6][6];
        for (int j = 0; j < 6; j++) {
            table.row();
            for (int i = 0; i < 6; i++) {
                var textButton = new Button(skin, "scene-table");
                textButton.setProgrammaticChangeEvents(false);
                textButton.setUserObject(new IntPair(i, j));
                table.add(textButton);
                buttons[i][j] = textButton;
                textButton.addListener(handListener);
                textButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        popTable.hide();
                        var intPair = (IntPair) textButton.getUserObject();
                        events.tableSetCells(intPair.x + 1, intPair.y + 1);
                    }
                });
                textButton.addListener(new InputListener() {
                    @Override
                    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                        var intPair = (IntPair) textButton.getUserObject();
                        for (int y1 = 0; y1 < 6; y1++) {
                            for (int x1 = 0; x1 < 6; x1++) {
                                buttons[x1][y1].setChecked(y1 <= intPair.y && x1 <= intPair.x);
                            }
                        }
                        textButton.setChecked(true);
                    }
                });
            }
        }
        
        table.setTouchable(Touchable.enabled);
        table.addListener(new InputListener() {
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                for (int y1 = 0; y1 < 6; y1++) {
                    for (int x1 = 0; x1 < 6; x1++) {
                        buttons[x1][y1].setChecked(false);
                    }
                }
            }
        });
        return popTableClickListener;
    }
    
    public static Dialog showConfirmCellSetWidgetDialog(final DialogSceneComposer dialogSceneComposer,
                                                        WidgetType widgetType,
                                                        PopTable popTable) {
        var simCell = (DialogSceneComposerModel.SimCell) dialogSceneComposer.simActor;
        if (simCell.child == null) {
            popTable.hide();
            dialogSceneComposer.events.cellSetWidget(widgetType);
            return null;
        } else {
            var dialog = new Dialog("", skin, "scene-dialog") {
                @Override
                protected void result(Object object) {
                    if ((Boolean) object) {
                        popTable.hide();
                        dialogSceneComposer.events.cellSetWidget(widgetType);
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
