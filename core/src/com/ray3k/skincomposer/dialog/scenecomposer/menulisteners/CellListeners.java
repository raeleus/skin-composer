package com.ray3k.skincomposer.dialog.scenecomposer.menulisteners;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimTable;
import com.ray3k.stripe.PopTableClickListener;
import com.ray3k.stripe.Spinner;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerEvents;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimActor;

import static com.ray3k.skincomposer.Main.*;

public class CellListeners {
    public static EventListener cellMoveCellListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simCell = (DialogSceneComposerModel.SimCell) simActor;
        var simTable = (SimTable) simActor.parent;
        
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
                
                var label = new Label("Move Cell", skin, "scene-label-colored");
                popTable.add(label);

                popTable.row();
                var subTable = new Table();
                popTable.add(subTable);

                subTable.add();
                
                var button = new Button(skin, "scene-move-up");
                button.setDisabled(simCell.row == 0);
                subTable.add(button);
                button.addListener(handListener);
                button.addListener((Main.makeTooltip("Moves the current cell up.", tooltipManager, skin, "scene")));
                button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.cellMoveCellUp();
                        popTable.hide();
                    }
                });
                
                subTable.add();
                
                subTable.row();
                button = new Button(skin, "scene-move-left");
                button.setDisabled(simCell.column == 0);
                subTable.add(button);
                button.addListener(handListener);
                button.addListener((Main.makeTooltip("Moves the current cell left.", tooltipManager, skin, "scene")));
                button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.cellMoveCellLeft();
                        popTable.hide();
                    }
                });
                
                subTable.add();

                button = new Button(skin, "scene-move-right");
                button.setDisabled(simCell.column == simTable.getColumns(simCell.row) - 1);
                subTable.add(button);
                button.addListener(handListener);
                button.addListener((Main.makeTooltip("Moves the current cell right.", tooltipManager, skin, "scene")));
                button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.cellMoveCellRight();
                        popTable.hide();
                    }
                });
                
                subTable.row();
                subTable.add();
                
                button = new Button(skin, "scene-move-down");
                button.setDisabled(simCell.row == simTable.getRows() - 1);
                subTable.add(button);
                button.addListener(handListener);
                button.addListener((Main.makeTooltip("Moves the current cell down.", tooltipManager, skin, "scene")));
                button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.cellMoveCellDown();
                        popTable.hide();
                    }
                });
                
                subTable.add();
                
                popTable.row();
                label = new Label("Move to New Row", skin, "scene-label-colored");
                popTable.add(label);
    
                popTable.row();
                subTable = new Table();
                popTable.add(subTable);
                
                button = new Button(skin, "scene-add-down-row");
                subTable.add(button);
                button.addListener(handListener);
                button.addListener((Main.makeTooltip("Creates a new row below and moves this cell to it.", tooltipManager, skin, "scene")));
                button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.cellMoveToNewRowBelow();
                        popTable.hide();
                    }
                });
    
                button = new Button(skin, "scene-add-up-row");
                subTable.add(button);
                button.addListener(handListener);
                button.addListener((Main.makeTooltip("Creates a new row above and moves this cell to it.", tooltipManager, skin, "scene")));
                button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.cellMoveToNewRowAbove();
                        popTable.hide();
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener cellAddCellListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simCell = (DialogSceneComposerModel.SimCell) simActor;
        var simTable = (SimTable) simActor.parent;
        
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
                var label = new Label("New Cell", skin, "scene-label-colored");
                table.add(label);
    
                table.row();
                var subTable = new Table();
                table.add(subTable);
                
                subTable.add();
                
                var button = new Button(skin, "scene-add-up");
                button.setDisabled(simCell.row == 0);
                subTable.add(button);
                button.addListener(handListener);
                button.addListener((Main.makeTooltip("Creates a new cell at the end of the row above.", tooltipManager, skin, "scene")));
                button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.cellAddCellAbove();
                        popTable.hide();
                    }
                });
                
                subTable.add();
                
                subTable.row();
                button = new Button(skin, "scene-add-left");
                subTable.add(button);
                button.addListener(handListener);
                button.addListener((Main.makeTooltip("Creates a new cell to the left of the current one.", tooltipManager, skin, "scene")));
                button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.cellAddCellToLeft();
                        popTable.hide();
                    }
                });
                
                subTable.add();
                
                button = new Button(skin, "scene-add-right");
                subTable.add(button);
                button.addListener(handListener);
                button.addListener((Main.makeTooltip("Creates a new cell to the right of the current one.", tooltipManager, skin, "scene")));
                button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.cellAddCellToRight();
                        popTable.hide();
                    }
                });
                
                subTable.row();
                subTable.add();
                
                button = new Button(skin, "scene-add-down");
                button.setDisabled(simCell.row == simTable.getRows() - 1);
                subTable.add(button);
                button.addListener(handListener);
                button.addListener((Main.makeTooltip("Creates a new cell at the end of the row below.", tooltipManager, skin, "scene")));
                button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.cellAddCellBelow();
                        popTable.hide();
                    }
                });
                
                subTable.add();
                
                table.row();
                label = new Label("New Row", skin, "scene-label-colored");
                table.add(label);
    
                table.row();
                subTable = new Table();
                table.add(subTable);
                
                button = new Button(skin, "scene-add-down-row");
                subTable.add(button);
                button.addListener(handListener);
                button.addListener((Main.makeTooltip("Creates a new row below and adds a cell to it.", tooltipManager, skin, "scene")));
                button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.cellAddRowBelow();
                        popTable.hide();
                    }
                });
                
                button = new Button(skin, "scene-add-up-row");
                subTable.add(button);
                button.addListener(handListener);
                button.addListener((Main.makeTooltip("Creates a new row above and adds a cell to it.", tooltipManager, skin, "scene")));
                button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.cellAddRowAbove();
                        popTable.hide();
                    }
                });
    
                var image = new Image(skin, "scene-menu-divider");
                popTable.add(image).space(10).growY();
    
                table = new Table();
                popTable.add(table);
                
                label = new Label("Duplicate Cell", skin, "scene-label-colored");
                table.add(label);
    
                table.row();
                subTable = new Table();
                table.add(subTable);
                
                subTable.add();
    
                button = new Button(skin, "scene-duplicate-up");
                subTable.add(button);
                button.setDisabled(simCell.row == 0);
                button.addListener(handListener);
                button.addListener((Main.makeTooltip("Duplicates the cell to the row above.", tooltipManager, skin, "scene")));
                button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.cellDuplicateCellAbove();
                        popTable.hide();
                    }
                });
                
                subTable.add();
                
                subTable.row();
                button = new Button(skin, "scene-duplicate-left");
                subTable.add(button);
                button.addListener(handListener);
                button.addListener((Main.makeTooltip("Creates a new cell to the left of the current one.", tooltipManager, skin, "scene")));
                button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.cellDuplicateCellLeft();
                        popTable.hide();
                    }
                });
    
                subTable.add();
                
                button = new Button(skin, "scene-duplicate-right");
                subTable.add(button);
                button.addListener(handListener);
                button.addListener((Main.makeTooltip("Creates a new cell to the right of the current one.", tooltipManager, skin, "scene")));
                button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.cellDuplicateCellRight();
                        popTable.hide();
                    }
                });
    
                subTable.row();
                subTable.add();
                
                button = new Button(skin, "scene-duplicate-down");
                subTable.add(button);
                button.setDisabled(simCell.row == simTable.getRows() - 1);
                button.addListener(handListener);
                button.addListener((Main.makeTooltip("Duplicates the cell to the row below.", tooltipManager, skin, "scene")));
                button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.cellDuplicateCellBelow();
                        popTable.hide();
                    }
                });
                
                subTable.add();
    
                table.row();
                label = new Label("Dup. to New Row", skin, "scene-label-colored");
                table.add(label);
    
                table.row();
                subTable = new Table();
                table.add(subTable);
                
                button = new Button(skin, "scene-duplicate-down-row");
                subTable.add(button);
                button.addListener(handListener);
                button.addListener((Main.makeTooltip("Creates a new row below and adds a cell to it.", tooltipManager, skin, "scene")));
                button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.cellDuplicateCellNewRowBelow();
                        popTable.hide();
                    }
                });
    
                button = new Button(skin, "scene-duplicate-up-row");
                subTable.add(button);
                button.addListener(handListener);
                button.addListener((Main.makeTooltip("Creates a new row below and adds a cell to it.", tooltipManager, skin, "scene")));
                button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.cellDuplicateCellNewRowAbove();
                        popTable.hide();
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener cellUniformListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simCell = (DialogSceneComposerModel.SimCell) simActor;
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
    
                var changeListener = new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        ImageTextButton uniformX = popTable.findActor("uniform-x");
                        ImageTextButton uniformY = popTable.findActor("uniform-y");
                        events.cellUniform(uniformX.isChecked(), uniformY.isChecked());
                    }
                };
    
                var table = new Table();
                popTable.add(table);
    
                table.defaults().left().spaceRight(5);
                var imageTextButton = new ImageTextButton("Uniform X", skin, "scene-checkbox-colored");
                imageTextButton.setName("uniform-x");
                imageTextButton.setChecked(simCell.uniformX);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("All cells with Uniform X will share the same width.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(changeListener);
    
                imageTextButton = new ImageTextButton("Uniform Y", skin, "scene-checkbox-colored");
                imageTextButton.setName("uniform-y");
                imageTextButton.setChecked(simCell.uniformY);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("All cells with Uniform Y will share the same height.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(changeListener);
            }
        };
        
        popTableClickListener.update();
    
        return popTableClickListener;
    }
    
    public static EventListener cellSizeListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simCell = (DialogSceneComposerModel.SimCell) simActor;
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
    
                var changeListener = new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        Spinner minimumWidth = popTable.findActor("minimum-width");
                        Spinner minimumHeight = popTable.findActor("minimum-height");
                        Spinner maximumWidth = popTable.findActor("maximum-width");
                        Spinner maximumHeight = popTable.findActor("maximum-height");
                        Spinner preferredWidth = popTable.findActor("preferred-width");
                        Spinner preferredHeight = popTable.findActor("preferred-height");
                        Button link = popTable.findActor("link");
            
                        if (!link.isChecked()) {
                            events.cellSize((float) minimumWidth.getValue(), (float) minimumHeight.getValue(),
                                    (float) maximumWidth.getValue(), (float) maximumHeight.getValue(),
                                    (float) preferredWidth.getValue(), (float) preferredHeight.getValue());
                        } else if (actor != link) {
                            var val = 0.0f;
                            if (actor == minimumWidth) val = (float) minimumWidth.getValue();
                            else if (actor == minimumHeight) val = (float) minimumHeight.getValue();
                            else if (actor == maximumWidth) val = (float) maximumWidth.getValue();
                            else if (actor == maximumHeight) val = (float) maximumHeight.getValue();
                            else if (actor == preferredWidth) val = (float) preferredWidth.getValue();
                            else if (actor == preferredHeight) val = (float) preferredHeight.getValue();
                
                            events.cellSize(val, val, val, val, val, val);
                
                            minimumWidth.setValue(val);
                            minimumHeight.setValue(val);
                            maximumWidth.setValue(val);
                            maximumHeight.setValue(val);
                            preferredWidth.setValue(val);
                            preferredHeight.setValue(val);
                        }
                    }
                };
    
                var label = new Label("Set to negative to disable", skin, "scene-label-colored");
                popTable.add(label).colspan(5);
    
                popTable.row();
                var table = new Table();
                popTable.add(table);
                
                label = new Label("Minimum:", skin, "scene-label-colored");
                table.add(label).colspan(2);
    
                table.row();
                table.defaults().right().spaceRight(5);
                label = new Label("Width:", skin, "scene-label-colored");
                table.add(label);
    
                var spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("minimum-width");
                spinner.setValue(simCell.minWidth);
                table.add(spinner);
                spinner.getTextField().addListener(ibeamListener);
                spinner.getButtonMinus().addListener(handListener);
                spinner.getButtonPlus().addListener(handListener);
                spinner.addListener((Main.makeTooltip("The minimum width of the contents of the cell.", tooltipManager, skin, "scene")));
                spinner.addListener(changeListener);
    
                table.row();
                label = new Label("Height:", skin, "scene-label-colored");
                table.add(label);
    
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("minimum-height");
                spinner.setValue(simCell.minHeight);
                table.add(spinner);
                spinner.getTextField().addListener(ibeamListener);
                spinner.getButtonMinus().addListener(handListener);
                spinner.getButtonPlus().addListener(handListener);
                spinner.addListener((Main.makeTooltip("The minimum height of the contents of the cell.", tooltipManager, skin, "scene")));
                spinner.addListener(changeListener);
    
                var image = new Image(skin, "scene-menu-divider");
                popTable.add(image).space(10).growY();
    
                table = new Table();
                popTable.add(table);
    
                label = new Label("Maximum:", skin, "scene-label-colored");
                table.add(label).colspan(2);
    
                table.row();
                table.defaults().right().spaceRight(5);
                label = new Label("Width:", skin, "scene-label-colored");
                table.add(label);
    
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("maximum-width");
                spinner.setValue(simCell.maxWidth);
                table.add(spinner);
                spinner.getTextField().addListener(ibeamListener);
                spinner.getButtonMinus().addListener(handListener);
                spinner.getButtonPlus().addListener(handListener);
                spinner.addListener((Main.makeTooltip("The maximum width of the contents of the cell.", tooltipManager, skin, "scene")));
                spinner.addListener(changeListener);
    
                table.row();
                label = new Label("Height:", skin, "scene-label-colored");
                table.add(label);
    
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("maximum-height");
                spinner.setValue(simCell.maxHeight);
                table.add(spinner);
                spinner.getTextField().addListener(ibeamListener);
                spinner.getButtonMinus().addListener(handListener);
                spinner.getButtonPlus().addListener(handListener);
                spinner.addListener((Main.makeTooltip("The maximum height of the contents of the cell.", tooltipManager, skin, "scene")));
                spinner.addListener(changeListener);
    
                image = new Image(skin, "scene-menu-divider");
                popTable.add(image).space(10).growY();
    
                table = new Table();
                popTable.add(table);
    
                label = new Label("Preferred:", skin, "scene-label-colored");
                table.add(label).colspan(2);
    
                table.row();
                table.defaults().right().spaceRight(5);
                label = new Label("Width:", skin, "scene-label-colored");
                table.add(label);
    
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("preferred-width");
                spinner.setValue(simCell.preferredWidth);
                table.add(spinner);
                spinner.getTextField().addListener(ibeamListener);
                spinner.getButtonMinus().addListener(handListener);
                spinner.getButtonPlus().addListener(handListener);
                spinner.addListener((Main.makeTooltip("The preferred width of the contents of the cell.", tooltipManager, skin, "scene")));
                spinner.addListener(changeListener);
    
                table.row();
                label = new Label("Height:", skin, "scene-label-colored");
                table.add(label);
    
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("preferred-height");
                spinner.setValue(simCell.preferredHeight);
                table.add(spinner);
                spinner.getTextField().addListener(ibeamListener);
                spinner.getButtonMinus().addListener(handListener);
                spinner.getButtonPlus().addListener(handListener);
                spinner.addListener((Main.makeTooltip("The preferred height of the contents of the cell.", tooltipManager, skin, "scene")));
                spinner.addListener(changeListener);
                
                popTable.row();
                var button = new Button(skin, "scene-link");
                button.setName("link");
                popTable.add(button).colspan(5).right();
                button.addListener(Main.handListener);
                button.addListener(changeListener);
                button.addListener((Main.makeTooltip("Click to modify all values at once.", tooltipManager, skin, "scene")));
            }
        };
        
        popTableClickListener.update();
    
        return popTableClickListener;
    }
    
    public static EventListener cellAlignmentListener(final DialogSceneComposerEvents events, final SimActor simActor) {
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
                var simCell = (DialogSceneComposerModel.SimCell) simActor;
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
                imageTextButton.addListener((Main.makeTooltip("Align the contents of the cell to the top left.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.cellAlignment(Align.topLeft);
                    }
                });
                buttonGroup.add(imageTextButton);
    
                imageTextButton = new ImageTextButton("Top", skin, "scene-checkbox-colored");
                var top = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the contents of the cell to the top center.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.cellAlignment(Align.top);
                    }
                });
                buttonGroup.add(imageTextButton);
    
                imageTextButton = new ImageTextButton("Top-Right", skin, "scene-checkbox-colored");
                var topRight = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the contents of the cell to the top right.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.cellAlignment(Align.topRight);
                    }
                });
                buttonGroup.add(imageTextButton);
    
                table.row();
                imageTextButton = new ImageTextButton("Left", skin, "scene-checkbox-colored");
                var left = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the contents of the cell to the middle left.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.cellAlignment(Align.left);
                    }
                });
                buttonGroup.add(imageTextButton);
    
                imageTextButton = new ImageTextButton("Center", skin, "scene-checkbox-colored");
                var center = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the contents of the cell to the center.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.cellAlignment(Align.center);
                    }
                });
                buttonGroup.add(imageTextButton);
    
                imageTextButton = new ImageTextButton("Right", skin, "scene-checkbox-colored");
                var right = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the contents of the cell to the middle right.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.cellAlignment(Align.right);
                    }
                });
                buttonGroup.add(imageTextButton);
    
                table.row();
                imageTextButton = new ImageTextButton("Bottom-Left", skin, "scene-checkbox-colored");
                var bottomLeft = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the contents of the cell to the bottom left.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.cellAlignment(Align.bottomLeft);
                    }
                });
                buttonGroup.add(imageTextButton);
    
                imageTextButton = new ImageTextButton("Bottom", skin, "scene-checkbox-colored");
                var bottom = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the contents of the cell to the bottom center.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.cellAlignment(Align.bottom);
                    }
                });
                buttonGroup.add(imageTextButton);
    
                imageTextButton = new ImageTextButton("Bottom-Right", skin, "scene-checkbox-colored");
                var bottomRight = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Align the contents of the cell to the bottom right.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.cellAlignment(Align.bottomRight);
                    }
                });
                buttonGroup.add(imageTextButton);
    
                switch (simCell.alignment) {
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
    
    public static EventListener cellExpandFillGrowListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simCell = (DialogSceneComposerModel.SimCell) simActor;
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
                
                var expandX = new ImageTextButton("Expand X", skin, "scene-checkbox-colored");
                var expandY = new ImageTextButton("Expand Y", skin, "scene-checkbox-colored");
                var fillX = new ImageTextButton("Fill X", skin, "scene-checkbox-colored");
                var fillY = new ImageTextButton("Fill Y", skin, "scene-checkbox-colored");
                var growX = new ImageTextButton("Grow X", skin, "scene-checkbox-colored");
                var growY = new ImageTextButton("Grow Y", skin, "scene-checkbox-colored");
    
                var changeListener = new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.cellExpandFillGrow(expandX.isChecked(), expandY.isChecked(), fillX.isChecked(), fillY.isChecked(), growX.isChecked(), growY.isChecked());
                    }
                };
    
                var table = new Table();
                popTable.add(table);
    
                table.defaults().left().spaceRight(5);
                expandX.setChecked(simCell.expandX);
                expandX.setProgrammaticChangeEvents(false);
                table.add(expandX);
                expandX.addListener(handListener);
                expandX.addListener((Main.makeTooltip("Expands the width of the cell to the available space.", tooltipManager, skin, "scene")));
                expandX.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (expandX.isChecked() && fillX.isChecked()) {
                            growX.setChecked(true);
                        } else {
                            growX.setChecked(false);
                        }
                    }
                });
                expandX.addListener(changeListener);

                expandY.setChecked(simCell.expandY);
                expandY.setProgrammaticChangeEvents(false);
                table.add(expandY);
                expandY.addListener(handListener);
                expandY.addListener((Main.makeTooltip("Expands the height of the cell to the available space.", tooltipManager, skin, "scene")));
                expandY.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (expandY.isChecked() && fillY.isChecked()) {
                            growY.setChecked(true);
                        } else {
                            growY.setChecked(false);
                        }
                    }
                });
                expandY.addListener(changeListener);
    
                table.row();
                fillX.setChecked(simCell.fillX);
                fillX.setProgrammaticChangeEvents(false);
                table.add(fillX);
                fillX.addListener(handListener);
                fillX.addListener((Main.makeTooltip("Stretches the contents to fill the width of the cell.", tooltipManager, skin, "scene")));
                fillX.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (expandX.isChecked() && fillX.isChecked()) {
                            growX.setChecked(true);
                        } else {
                            growX.setChecked(false);
                        }
                    }
                });
                fillX.addListener(changeListener);
    
                fillY.setChecked(simCell.fillY);
                fillY.setProgrammaticChangeEvents(false);
                table.add(fillY);
                fillY.addListener(handListener);
                fillY.addListener((Main.makeTooltip("Stretches the contents to fill the height of the cell.", tooltipManager, skin, "scene")));
                fillY.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (expandY.isChecked() && fillY.isChecked()) {
                            growY.setChecked(true);
                        } else {
                            growY.setChecked(false);
                        }
                    }
                });
                fillY.addListener(changeListener);
    
                table.row();
                growX.setChecked(simCell.growX);
                growX.setProgrammaticChangeEvents(false);
                table.add(growX);
                growX.addListener(handListener);
                growX.addListener((Main.makeTooltip("Sets the cell to expand and fill across the available width.", tooltipManager, skin, "scene")));
                growX.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        expandX.setChecked(growX.isChecked());
                        fillX.setChecked(growX.isChecked());
                    }
                });
                growX.addListener(changeListener);
    
                growY.setChecked(simCell.growY);
                growY.setProgrammaticChangeEvents(false);
                table.add(growY);
                growY.addListener(handListener);
                growY.addListener((Main.makeTooltip("Sets the cell to expand and fill across the available height.", tooltipManager, skin, "scene")));
                growY.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        expandY.setChecked(growY.isChecked());
                        fillY.setChecked(growY.isChecked());
                    }
                });
                growY.addListener(changeListener);
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener cellColSpanListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simCell = (DialogSceneComposerModel.SimCell) simActor;
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
                
                var label = new Label("Column Span:", skin, "scene-label-colored");
                table.add(label).colspan(2);
                
                table.row();
                
                var spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setValue(simCell.colSpan);
                spinner.setMinimum(1);
                table.add(spinner);
                spinner.getTextField().addListener(ibeamListener);
                spinner.getButtonMinus().addListener(handListener);
                spinner.getButtonPlus().addListener(handListener);
                spinner.addListener((Main.makeTooltip("The column span of the cell.", tooltipManager, skin, "scene")));
                spinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.cellColSpan(spinner.getValueAsInt());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener cellPaddingSpacingListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simCell = (DialogSceneComposerModel.SimCell) simActor;
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
    
                var changeListener = new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        Spinner paddingLeftSpinner = popTable.findActor("padding-left");
                        Spinner paddingRightSpinner = popTable.findActor("padding-right");
                        Spinner paddingTopSpinner = popTable.findActor("padding-top");
                        Spinner paddingBottomSpinner = popTable.findActor("padding-bottom");
                        Spinner spacingLeftSpinner = popTable.findActor("spacing-left");
                        Spinner spacingRightSpinner = popTable.findActor("spacing-right");
                        Spinner spacingTopSpinner = popTable.findActor("spacing-top");
                        Spinner spacingBottomSpinner = popTable.findActor("spacing-bottom");
                        Button paddingLinkButton = popTable.findActor("padding-link");
                        Button spacingLinkButton = popTable.findActor("spacing-link");
    
                        if (paddingLinkButton.isChecked() && actor instanceof Spinner) {
                            var val = (float) ((Spinner) actor).getValue();
        
                            paddingLeftSpinner.setValue(val);
                            paddingRightSpinner.setValue(val);
                            paddingTopSpinner.setValue(val);
                            paddingBottomSpinner.setValue(val);
                        }
    
                        if (spacingLinkButton.isChecked() && actor instanceof Spinner) {
                            var val = (float) ((Spinner) actor).getValue();
        
                            spacingLeftSpinner.setValue(val);
                            spacingRightSpinner.setValue(val);
                            spacingTopSpinner.setValue(val);
                            spacingBottomSpinner.setValue(val);
                        }
    
                        var paddingLeft = (float) paddingLeftSpinner.getValue();
                        var paddingRight = (float) paddingRightSpinner.getValue();
                        var paddingTop = (float) paddingTopSpinner.getValue();
                        var paddingBottom = (float) paddingBottomSpinner.getValue();
                        var spacingLeft = (float) spacingLeftSpinner.getValue();
                        var spacingRight = (float) spacingRightSpinner.getValue();
                        var spacingTop = (float) spacingTopSpinner.getValue();
                        var spacingBottom = (float) spacingBottomSpinner.getValue();
                        
                        events.cellPaddingSpacing(paddingLeft, paddingRight, paddingTop, paddingBottom, spacingLeft,
                                spacingRight, spacingTop, spacingBottom);
                    }
                };
    
                var table = new Table();
                popTable.add(table);
    
                var label = new Label("Padding:", skin, "scene-label-colored");
                table.add(label).colspan(2);
    
                table.row();
                table.defaults().right().spaceRight(5);
                label = new Label("Left:", skin, "scene-label-colored");
                table.add(label);
    
                var spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setValue(simCell.padLeft);
                spinner.setName("padding-left");
                table.add(spinner);
                spinner.getTextField().addListener(ibeamListener);
                spinner.getButtonMinus().addListener(handListener);
                spinner.getButtonPlus().addListener(handListener);
                spinner.addListener((Main.makeTooltip("The padding to the left of the cell. Stacks with other cell padding.", tooltipManager, skin, "scene")));
                spinner.addListener(changeListener);
    
                table.row();
                label = new Label("Right:", skin, "scene-label-colored");
                table.add(label);
    
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setValue(simCell.padRight);
                spinner.setName("padding-right");
                table.add(spinner);
                spinner.getTextField().addListener(ibeamListener);
                spinner.getButtonMinus().addListener(handListener);
                spinner.getButtonPlus().addListener(handListener);
                spinner.addListener((Main.makeTooltip("The padding to the right of the cell. Stacks with other cell padding.", tooltipManager, skin, "scene")));
                spinner.addListener(changeListener);
    
                table.row();
                label = new Label("Top:", skin, "scene-label-colored");
                table.add(label);
    
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setValue(simCell.padTop);
                spinner.setName("padding-top");
                table.add(spinner);
                spinner.getTextField().addListener(ibeamListener);
                spinner.getButtonMinus().addListener(handListener);
                spinner.getButtonPlus().addListener(handListener);
                spinner.addListener((Main.makeTooltip("The padding to the top of the cell. Stacks with other cell padding.", tooltipManager, skin, "scene")));
                spinner.addListener(changeListener);
    
                table.row();
                label = new Label("Bottom:", skin, "scene-label-colored");
                table.add(label);
    
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setValue(simCell.padBottom);
                spinner.setName("padding-bottom");
                table.add(spinner);
                spinner.getTextField().addListener(ibeamListener);
                spinner.getButtonMinus().addListener(handListener);
                spinner.getButtonPlus().addListener(handListener);
                spinner.addListener((Main.makeTooltip("The padding to the bottom of the cell. Stacks with other cell padding.", tooltipManager, skin, "scene")));
                spinner.addListener(changeListener);
                
                table.row();
                var button = new Button(skin, "scene-link");
                button.setName("padding-link");
                table.add(button).right().colspan(2);
                button.addListener(Main.handListener);
                button.addListener(changeListener);
                button.addListener((Main.makeTooltip("Click to modify all padding values at once.", tooltipManager, skin, "scene")));
    
                var image = new Image(skin, "scene-menu-divider");
                popTable.add(image).space(10).growY();
    
                table = new Table();
                popTable.add(table);
    
                label = new Label("Spacing:", skin, "scene-label-colored");
                table.add(label).colspan(2);
    
                table.row();
                table.defaults().right().spaceRight(5);
                label = new Label("Left:", skin, "scene-label-colored");
                table.add(label);
    
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setValue(simCell.spaceLeft);
                spinner.setName("spacing-left");
                spinner.setMinimum(0);
                table.add(spinner);
                spinner.getTextField().addListener(ibeamListener);
                spinner.getButtonMinus().addListener(handListener);
                spinner.getButtonPlus().addListener(handListener);
                spinner.addListener((Main.makeTooltip("The spacing to the left of the cell. Does not stack with other cell spacing.", tooltipManager, skin, "scene")));
                spinner.addListener(changeListener);
    
                table.row();
                label = new Label("Right:", skin, "scene-label-colored");
                table.add(label);
    
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setValue(simCell.spaceRight);
                spinner.setName("spacing-right");
                spinner.setMinimum(0);
                table.add(spinner);
                spinner.getTextField().addListener(ibeamListener);
                spinner.getButtonMinus().addListener(handListener);
                spinner.getButtonPlus().addListener(handListener);
                spinner.addListener((Main.makeTooltip("The spacing to the right of the cell. Does not stack with other cell spacing.", tooltipManager, skin, "scene")));
                spinner.addListener(changeListener);
    
                table.row();
                label = new Label("Top:", skin, "scene-label-colored");
                table.add(label);
    
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setValue(simCell.spaceTop);
                spinner.setName("spacing-top");
                spinner.setMinimum(0);
                table.add(spinner);
                spinner.getTextField().addListener(ibeamListener);
                spinner.getButtonMinus().addListener(handListener);
                spinner.getButtonPlus().addListener(handListener);
                spinner.addListener((Main.makeTooltip("The spacing to the top of the cell. Does not stack with other cell spacing.", tooltipManager, skin, "scene")));
                spinner.addListener(changeListener);
    
                table.row();
                label = new Label("Bottom:", skin, "scene-label-colored");
                table.add(label);
    
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setValue(simCell.spaceBottom);
                spinner.setName("spacing-bottom");
                spinner.setMinimum(0);
                table.add(spinner);
                spinner.getTextField().addListener(ibeamListener);
                spinner.getButtonMinus().addListener(handListener);
                spinner.getButtonPlus().addListener(handListener);
                spinner.addListener((Main.makeTooltip("The spacing to the bottom of the cell. Does not stack with other cell spacing.", tooltipManager, skin, "scene")));
                spinner.addListener(changeListener);
    
                table.row();
                button = new Button(skin, "scene-link");
                button.setName("spacing-link");
                table.add(button).right().colspan(2);
                button.addListener(Main.handListener);
                button.addListener(changeListener);
                button.addListener((Main.makeTooltip("Click to modify all spacing values at once.", tooltipManager, skin, "scene")));
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
}
