package com.ray3k.skincomposer.dialog.scenecomposer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.data.StyleProperty;
import com.ray3k.skincomposer.dialog.DialogListener;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.*;
import com.ray3k.skincomposer.dialog.scenecomposer.menulisteners.*;
import com.ray3k.skincomposer.utils.IntPair;
import com.ray3k.stripe.PopTable;
import com.ray3k.stripe.PopTableClickListener;
import com.ray3k.stripe.StripeMenuBar;
import com.ray3k.stripe.StripeMenuBar.KeyboardShortcut;

import static com.ray3k.skincomposer.Main.*;
import static com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.*;

public class DialogSceneComposer extends Dialog {
    public static DialogSceneComposer dialog;
    public enum View {
        LIVE, EDIT, OUTLINE
    }
    public View view;
    public DialogSceneComposerEvents events;
    public DialogSceneComposerModel model;
    private TextTooltip undoTooltip;
    private TextTooltip redoTooltip;
    private TextButton undoButton;
    private TextButton redoButton;
    public DialogSceneComposerModel.SimActor simActor;
    private Table propertiesTable;
    private Table pathTable;
    public Table previewTable;
    public Image liveImage;
    public Image editImage;
    public Image outlineImage;
    private Label propertiesLabel;
    
    public DialogSceneComposer() {
        super("", Main.skin, "scene");
        dialog = this;
        events = new DialogSceneComposerEvents();
        model = new DialogSceneComposerModel();
        
        view = View.EDIT;
        simActor = rootActor;
        
        setFillParent(true);
        
        populate();
        
        addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.F5) {
                    populate();
                }
                return super.keyDown(event, keycode);
            }
        });
    }
    
    private void populate() {
        getCell(getButtonTable()).space(0);
        
        var root = getContentTable();
        getCell(root).space(0);
        root.clear();
        root.defaults().reset();
        
        var table = new Table();
        table.setBackground(skin.getDrawable("scene-title-bar-ten"));
        root.add(table).growX();
    
        var label = new Label("Scene Composer", skin, "scene-title");
        table.add(label);
        
        root.row();
        var bar = new StripeMenuBar(stage, skin);
        root.add(bar).growX();
        
        bar.menu("File", handListener)
                .item("Save", new KeyboardShortcut("Ctrl+S", Keys.S, Keys.CONTROL_LEFT), handListener, new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        mainListener.saveFile(null);
                    }
                })
                .item("Save as...", new KeyboardShortcut("Ctrl+Shift+S", Keys.S, Keys.CONTROL_LEFT, Keys.SHIFT_LEFT), handListener, new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        mainListener.saveAsFile(null);
                    }
                })
                .item("Import", handListener, new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        var file = desktopWorker.openDialog("Import JSON...", projectData.getLastSceneComposerJson(), "json", "JSON Files (*.json)");
                
                        if (file != null) {
                            events.importTemplate(new FileHandle(file));
                        }
                    }
                })
                .item("Export", new KeyboardShortcut("Ctrl+E", Keys.E, Keys.CONTROL_LEFT), handListener, new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        showExportDialog();
                    }
                })
                .item("Settings", handListener, new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        showSettingsDialog();
                    }
                })
                .item("Quit", handListener, new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.menuQuit();
                    }
                });
        
        bar.menu("Scene", handListener)
                .item("Find by name...", new KeyboardShortcut("Ctrl+F", Keys.F, Keys.CONTROL_LEFT), handListener, new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.menuFind();
                    }
                })
                .item("Clear", handListener, new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.menuClear();
                    }
                })
                .item("Undo", new KeyboardShortcut("Ctrl+Z", Keys.Z, Keys.CONTROL_LEFT), handListener, new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.menuUndo();
                    }
                })
                .item("Redo", new KeyboardShortcut("Ctrl+R", Keys.R, Keys.CONTROL_LEFT), handListener, new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.menuRedo();
                    }
                });
        
        bar.menu("View", handListener)
                .item("Live", handListener, new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.menuView(View.LIVE);
                    }
                })
                .item("Edit", handListener, new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.menuView(View.EDIT);
                    }
                })
                .item("Outline", handListener, new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.menuView(View.OUTLINE);
                    }
                });
    
        var textButton = bar.findMenu("View").findButton("Live");
        textButton.clearChildren();
        liveImage = new Image(skin, "scene-menu-radio-invisible");
        textButton.add(liveImage).space(10);
        textButton.add(textButton.getLabel()).expandX().left();
    
        textButton = bar.findMenu("View").findButton("Edit");
        textButton.clearChildren();
        editImage = new Image(skin, "scene-menu-radio-invisible");
        textButton.add(editImage).space(10);
        textButton.add(textButton.getLabel()).expandX().left();
        
        textButton = bar.findMenu("View").findButton("Outline");
        textButton.clearChildren();
        outlineImage = new Image(skin, "scene-menu-radio-invisible");
        textButton.add(outlineImage).space(10);
        textButton.add(textButton.getLabel()).expandX().left();
        
        updateMenuView();
        
        bar.item("?", handListener, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                events.menuHelp();
            }
        });
        
        undoButton = bar.findMenu("Scene").findButton("Undo");
        undoTooltip = (Main.makeTooltip("", tooltipManager, skin, "scene"));
        redoButton = bar.findMenu("Scene").findButton("Redo");
        redoTooltip = (Main.makeTooltip("", tooltipManager, skin, "scene"));
        
        bar.findCell("?").expandX().right();
        
        root.row();
        previewTable = new Table();
        previewTable.setTouchable(Touchable.enabled);
        previewTable.setBackground(skin.getDrawable("white"));
        if (rootActor.backgroundColor != null) previewTable.setColor(rootActor.backgroundColor.color);
        
        previewTable.add(model.preview).grow().minSize(0).prefSize(0);
        
        var bottom = new Table() {
            @Override
            public float getMinHeight() {
                return 0;
            }
        };
        bottom.setTouchable(Touchable.enabled);
        bottom.setBackground(skin.getDrawable("scene-bg"));

        var splitPane = new SplitPane(previewTable, bottom, true, skin, "scene-vertical");
        splitPane.setMinSplitAmount(0);
        root.add(splitPane).grow();
        splitPane.addListener(verticalResizeArrowListener);
    
        table = new Table();
        table.setClip(true);
        bottom.add(table).growX().minHeight(0);
        
        propertiesLabel = new Label("Properties", skin, "scene-title-colored");
        table.add(propertiesLabel);
    
        bottom.row();
        table = new Table();
        propertiesTable = table;
        var scrollPane = new ScrollPane(table, skin, "scene");
        scrollPane.setName("scroll-properties");
        scrollPane.setFlickScroll(false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setForceScroll(false,  true);
        bottom.add(scrollPane).grow();
        scrollPane.addListener(scrollFocusListener);
        
        populateProperties();
    
        bottom.row();
        var image = new Image(skin, "scene-path-border");
        bottom.add(image).growX();
        
        bottom.row();
        table = new Table();
        pathTable = table;
        scrollPane = new ScrollPane(table, skin, "scene");
        bottom.add(scrollPane).growX().minHeight(0).space(3);
        
        populatePath();
        
        updateMenuUndoRedo();
        model.updatePreview();
    }
    
    public void updateMenuView() {
        liveImage.setDrawable(skin, view == View.LIVE ? "scene-menu-radio" : "scene-menu-radio-invisible");
        editImage.setDrawable(skin, view == View.EDIT ? "scene-menu-radio" : "scene-menu-radio-invisible");
        outlineImage.setDrawable(skin, view == View.OUTLINE ? "scene-menu-radio" : "scene-menu-radio-invisible");
    }
    
    public void updateMenuUndoRedo() {
        if (model.undoables.size > 0) {
            undoButton.setDisabled(false);
            undoTooltip.getActor().setText(model.undoables.peek().getUndoString());
            undoTooltip.getContainer().pack();
            undoButton.addListener(undoTooltip);
            undoButton.addListener(handListener);
        } else {
            undoButton.setDisabled(true);
            undoTooltip.hide();
            undoButton.removeListener(undoTooltip);
            undoButton.removeListener(handListener);
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        }
    
        if (model.redoables.size > 0) {
            redoButton.setDisabled(false);
            redoTooltip.getActor().setText(model.redoables.peek().getRedoString());
            redoTooltip.getContainer().pack();
            redoButton.addListener(redoTooltip);
            redoButton.addListener(handListener);
        } else {
            redoButton.setDisabled(true);
            redoTooltip.hide();
            redoButton.removeListener(redoTooltip);
            redoButton.removeListener(handListener);
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        }
    }
    
    public void populateProperties() {
        var root = propertiesTable;
        root.clear();
        
        var horizontalGroup = new HorizontalGroup();
        horizontalGroup.wrap();
        horizontalGroup.align(Align.top);
        root.add(horizontalGroup).grow();
    
        if (simActor instanceof SimRootGroup) {
            propertiesLabel.setText("Properties");
            
            var textButton = new TextButton("Add Table", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(rootAddTableListener());
            textButton.addListener((Main.makeTooltip("Creates a table with the specified number of rows and columns.", tooltipManager, skin, "scene")));
        } else if (simActor instanceof DialogSceneComposerModel.SimTable) {
            propertiesLabel.setText("Table Properties");
            
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TableListeners.tableNameListener(this));
            textButton.addListener((Main.makeTooltip("Sets the name of the table to allow for convenient searching via Group#findActor().", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Touchable", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.touchableListener(simActor, events::tableTouchable));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Visible", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.visibleListener(simActor, events::tableVisible));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Background", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.selectDrawableListener(((SimTable) simActor).background, "The background image for the table.",events::tableBackground));
            textButton.addListener((Main.makeTooltip("Sets the background drawable for the table.", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Color", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TableListeners.tableColorListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets the color of the table background and of the table contents.", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Padding", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TableListeners.tablePaddingListener(events, simActor));
            textButton.addListener((Main.makeTooltip("The padding around all of the contents inside the table.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Align", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TableListeners.tableAlignListener(events, simActor));
            textButton.addListener((Main.makeTooltip("The alignment of the entire contents inside the table.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Set Cells", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TableListeners.tableSetCellsListener(events));
            textButton.addListener((Main.makeTooltip("Sets the cells for this table. This will erase the existing contents.", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetResetListener("Table", events::tableReset));
            textButton.addListener((Main.makeTooltip("Resets all options back to their defaults.", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetDeleteListener("Table", events::tableDelete));
            textButton.addListener((Main.makeTooltip("Removes this widget from its parent.", tooltipManager, skin, "scene")));
        } else if (simActor instanceof DialogSceneComposerModel.SimCell) {
            propertiesLabel.setText("Cell Properties");
    
            var textButton = new TextButton("Set Widget", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.setWidgetListener(
                    this, (widgetType, popTable) -> TableListeners.showConfirmCellSetWidgetDialog(DialogSceneComposer.this, widgetType,
                            popTable)));
            textButton.addListener((Main.makeTooltip("Creates a new widget and sets it as the contents of this cell.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Add Cell...", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(CellListeners.cellAddCellListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Adds a new cell.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Move Cell...", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(CellListeners.cellMoveCellListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Moves the cell in a given direction.", tooltipManager, skin, "scene")));
            
            var table = new Table();
            horizontalGroup.addActor(table);
        
            textButton = new TextButton("Column Span", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(CellListeners.cellColSpanListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets the column span of the current cell.", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Alignment", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(CellListeners.cellAlignmentListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets the alignment of the contents.", tooltipManager, skin, "scene")));
            
            textButton = new TextButton("Padding / Spacing", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(CellListeners.cellPaddingSpacingListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets the padding and/or spacing of the current cell.", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Expand / Fill / Grow", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(CellListeners.cellExpandFillGrowListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets how the current cell and its contents are sized.", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Size", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(CellListeners.cellSizeListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets the specific sizes of the contents in the cell.", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Uniform", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(CellListeners.cellUniformListener(events, simActor));
            textButton.addListener((Main.makeTooltip("All cells set to to uniform = true will share the same size.", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetResetListener("Cell", events::cellReset));
            textButton.addListener((Main.makeTooltip("Resets all of the settings of the cell to their defaults.", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Delete Cell", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetDeleteListener("Cell", events::cellDelete));
            textButton.addListener((Main.makeTooltip("Deletes the cell and its contents.", tooltipManager, skin, "scene")));
        } else if (simActor instanceof DialogSceneComposerModel.SimTextButton) {
            propertiesLabel.setText("TextButton Properties");
    
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TextButtonListeners.textButtonNameListener(this));
            textButton.addListener((Main.makeTooltip("Sets the name of the table to allow for convenient searching via Group#findActor().", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Text", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TextButtonListeners.textButtonTextListener(this));
            textButton.addListener((Main.makeTooltip("Sets the text inside of the button.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Touchable", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.touchableListener(simActor, events::textButtonTouchable));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Visible", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.visibleListener(simActor, events::textButtonVisible));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TextButtonListeners.textButtonStyleListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets the style that controls the appearance of the text button.", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Checked", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TextButtonListeners.textButtonCheckedListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets whether the button is checked initially.", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Disabled", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TextButtonListeners.textButtonDisabledListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets whether the button is disabled initially.", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Color", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TextButtonListeners.textButtonColorListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets the color of the table background and of the table contents.", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Padding", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TextButtonListeners.textButtonPaddingListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets the padding of the contents of the button.", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetResetListener("TextButton", events::textButtonReset));
            textButton.addListener((Main.makeTooltip("Resets the settings of the TextButton to their defaults.", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetDeleteListener("TextButton", events::textButtonDelete));
            textButton.addListener((Main.makeTooltip("Removes this widget from its parent.", tooltipManager, skin, "scene")));
        } else if (simActor instanceof DialogSceneComposerModel.SimButton) {
            propertiesLabel.setText("Button Properties");
    
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ButtonListeners.buttonNameListener(this));
            textButton.addListener((Main.makeTooltip("Sets the name of the table to allow for convenient searching via Group#findActor().", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ButtonListeners.buttonStyleListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets the style that controls the appearance of the text button.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Touchable", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.touchableListener(simActor, events::buttonTouchable));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Visible", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.visibleListener(simActor, events::buttonVisible));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Checked", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ButtonListeners.buttonCheckedListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets whether the button is checked initially.", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Disabled", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ButtonListeners.buttonDisabledListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets whether the button is disabled initially.", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Color", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ButtonListeners.buttonColorListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets the color of the table background and of the table contents.", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Padding", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ButtonListeners.buttonPaddingListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets the padding of the contents of the button.", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetResetListener("Button", events::buttonReset));
            textButton.addListener((Main.makeTooltip("Resets the settings of the TextButton to their defaults.", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetDeleteListener("Button", events::buttonDelete));
            textButton.addListener((Main.makeTooltip("Removes this widget from its parent.", tooltipManager, skin, "scene")));
        } else if (simActor instanceof DialogSceneComposerModel.SimImageButton) {
            propertiesLabel.setText("ImageButton Properties");
    
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ImageButtonListeners.imageButtonNameListener(this));
            textButton.addListener((Main.makeTooltip("Sets the name of the table to allow for convenient searching via Group#findActor().", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ImageButtonListeners.imageButtonStyleListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets the style that controls the appearance of the text button.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Touchable", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.touchableListener(simActor, events::imageButtonTouchable));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Visible", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.visibleListener(simActor, events::imageButtonVisible));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Checked", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ImageButtonListeners.imageButtonCheckedListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets whether the button is checked initially.", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Disabled", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ImageButtonListeners.imageButtonDisabledListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets whether the button is disabled initially.", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Color", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ImageButtonListeners.imageButtonColorListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets the color of the table background and of the table contents.", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Padding", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ImageButtonListeners.imageButtonPaddingListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets the padding of the contents of the button.", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetResetListener("ImageButton", events::imageButtonReset));
            textButton.addListener((Main.makeTooltip("Resets the settings of the TextButton to their defaults.", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetDeleteListener("ImageButton", events::imageButtonDelete));
            textButton.addListener((Main.makeTooltip("Removes this widget from its parent.", tooltipManager, skin, "scene")));
        } else if (simActor instanceof DialogSceneComposerModel.SimImageTextButton) {
            propertiesLabel.setText("ImageTextButton Properties");
    
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ImageTextButtonListeners.imageTextButtonNameListener(this));
            textButton.addListener((Main.makeTooltip("Sets the name of the table to allow for convenient searching via Group#findActor().", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Text", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ImageTextButtonListeners.imageTextButtonTextListener(this));
            textButton.addListener((Main.makeTooltip("Sets the text inside of the button.", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ImageTextButtonListeners.imageTextButtonStyleListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets the style that controls the appearance of the text button.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Touchable", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.touchableListener(simActor, events::imageTextButtonTouchable));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Visible", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.visibleListener(simActor, events::imageTextButtonVisible));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
            
            textButton = new TextButton("Checked", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ImageTextButtonListeners.imageTextButtonCheckedListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets whether the button is checked initially.", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Disabled", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ImageTextButtonListeners.imageTextButtonDisabledListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets whether the button is disabled initially.", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Color", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ImageTextButtonListeners.imageTextButtonColorListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets the color of the table background and of the table contents.", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Padding", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ImageTextButtonListeners.imageTextButtonPaddingListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets the padding of the contents of the button.", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetResetListener("ImageTextButton", events::imageTextButtonReset));
            textButton.addListener((Main.makeTooltip("Resets the settings of the TextButton to their defaults.", tooltipManager, skin, "scene")));
        
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetDeleteListener("ImageTextButton", events::imageTextButtonDelete));
            textButton.addListener((Main.makeTooltip("Removes this widget from its parent.", tooltipManager, skin, "scene")));
        } else if (simActor instanceof DialogSceneComposerModel.SimCheckBox) {
            propertiesLabel.setText("CheckBox Properties");
    
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(CheckBoxListeners.checkBoxNameListener(this));
            textButton.addListener((Main.makeTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(CheckBoxListeners.checkBoxStyleListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets the style that controls the appearance of the CheckBox.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Touchable", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.touchableListener(simActor, events::checkBoxTouchable));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Visible", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.visibleListener(simActor, events::checkBoxVisible));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
            
            textButton = new TextButton("Text", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(CheckBoxListeners.checkBoxTextListener(this));
            textButton.addListener((Main.makeTooltip("Sets the text inside of the CheckBox.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Checked", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(CheckBoxListeners.checkBoxCheckedListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets whether the CheckBox is checked initially.", tooltipManager, skin, "scene")));
            
            textButton = new TextButton("Color", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(CheckBoxListeners.checkBoxColorListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets the color of the CheckBox.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Padding", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(CheckBoxListeners.checkBoxPaddingListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets the padding of the contents of the CheckBox.", tooltipManager, skin, "scene")));
            
            textButton = new TextButton("Disabled", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(CheckBoxListeners.checkBoxDisabledListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets whether the CheckBox is disabled initially.", tooltipManager, skin, "scene")));
            
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetResetListener("CheckBox", events::checkBoxReset));
            textButton.addListener((Main.makeTooltip("Resets the settings of the widget to its defaults.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetDeleteListener("CheckBox", events::checkBoxDelete));
            textButton.addListener((Main.makeTooltip("Removes this widget from its parent.", tooltipManager, skin, "scene")));
        } else if (simActor instanceof DialogSceneComposerModel.SimImage) {
            propertiesLabel.setText("Image Properties");
    
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ImageListeners.imageNameListener(this));
            textButton.addListener((Main.makeTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Drawable", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.selectDrawableListener(((SimImage) simActor).drawable, "The selected drawable for the image.",events::imageDrawable));
            textButton.addListener((Main.makeTooltip("Sets the drawable to be drawn as the Image.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Touchable", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.touchableListener(simActor, events::imageTouchable));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Visible", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.visibleListener(simActor, events::imageVisible));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
            
            textButton = new TextButton("Scaling", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ImageListeners.imageScalingListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets the scaling strategy of the Image when it's stretched or squeezed.", tooltipManager, skin, "scene")));
            
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetResetListener("Image", events::imageReset));
            textButton.addListener((Main.makeTooltip("Resets the settings of the widget to its defaults.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetDeleteListener("Image", events::imageDelete));
            textButton.addListener((Main.makeTooltip("Removes this widget from its parent.", tooltipManager, skin, "scene")));
        } else if (simActor instanceof DialogSceneComposerModel.SimLabel) {
            propertiesLabel.setText("Label Properties");
    
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(LabelListeners.labelNameListener(this));
            textButton.addListener((Main.makeTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(LabelListeners.labelStyleListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets the style that controls the appearance of the Label.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Touchable", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.touchableListener(simActor, events::labelTouchable));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Visible", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.visibleListener(simActor, events::labelVisible));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Text", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(LabelListeners.labelTextListener(this));
            textButton.addListener((Main.makeTooltip("Sets the text inside of the Label.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Color", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(LabelListeners.labelColorListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Changes the color of the text in the Label.", tooltipManager, skin, "scene")));
            
            textButton = new TextButton("Text Alignment", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(LabelListeners.labelTextAlignmentListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets the alignment of the text when the Label is larger than it's minimum size.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Ellipsis", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(LabelListeners.labelEllipsisListener(this));
            textButton.addListener((Main.makeTooltip("Enabling ellipsis allows the Label to be shortened and appends ellipsis characters (eg. \"...\")", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Wrap", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(LabelListeners.labelWrapListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Allows the text to be wrapped to the next line if it exceeds the width of the Label.", tooltipManager, skin, "scene")));
            
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetResetListener("Label", events::labelReset));
            textButton.addListener((Main.makeTooltip("Resets the settings of the widget to its defaults.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetDeleteListener("Label", events::labelDelete));
            textButton.addListener((Main.makeTooltip("Removes this widget from its parent.", tooltipManager, skin, "scene")));
        } else if (simActor instanceof DialogSceneComposerModel.SimList) {
            propertiesLabel.setText("List Properties");
    
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ListListeners.listNameListener(this));
            textButton.addListener((Main.makeTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ListListeners.listStyleListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets the style that controls the appearance of the List.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Touchable", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.touchableListener(simActor, events::listTouchable));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Visible", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.visibleListener(simActor, events::listVisible));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
            
            textButton = new TextButton("Text List", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ListListeners.listTextListListener(this));
            textButton.addListener((Main.makeTooltip("Set the text entries for the List.", tooltipManager, skin, "scene")));
            
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetResetListener("List", events::listReset));
            textButton.addListener((Main.makeTooltip("Resets the settings of the widget to its defaults.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetDeleteListener("List", events::listDelete));
            textButton.addListener((Main.makeTooltip("Removes this widget from its parent.", tooltipManager, skin, "scene")));
        } else if (simActor instanceof DialogSceneComposerModel.SimProgressBar) {
            propertiesLabel.setText("ProgressBar Properties");
    
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ProgressBarListeners.progressBarNameListener(this));
            textButton.addListener((Main.makeTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ProgressBarListeners.progressBarStyleListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets the style that controls the appearance of the ProgressBar.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Touchable", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.touchableListener(simActor, events::progressBarTouchable));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Visible", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.visibleListener(simActor, events::progressBarVisible));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Value Settings", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ProgressBarListeners.progressBarValueSettingsListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Set the value, minimum, maximum, and increment of the ProgressBar.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Orientation", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ProgressBarListeners.progressBarOrientationListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Change the orientation of the ProgressBar.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Animation", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ProgressBarListeners.progressBarAnimationListener(this));
            textButton.addListener((Main.makeTooltip("Change the progress animation as it increases or decreases.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Round", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ProgressBarListeners.progressBarRoundListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Rounds the drawable positions to integers.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Disabled", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ProgressBarListeners.progressBarDisabledListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets whether the ProgressBar is disabled initially.", tooltipManager, skin, "scene")));
            
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetResetListener("ProgressBar", events::progressBarReset));
            textButton.addListener((Main.makeTooltip("Resets the settings of the widget to its defaults.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetDeleteListener("ProgressBar", events::progressBarDelete));
            textButton.addListener((Main.makeTooltip("Removes this widget from its parent.", tooltipManager, skin, "scene")));
        } else if (simActor instanceof DialogSceneComposerModel.SimSelectBox) {
            propertiesLabel.setText("SelectBox Properties");
    
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(SelectBoxListeners.selectBoxNameListener(this));
            textButton.addListener((Main.makeTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(SelectBoxListeners.selectBoxStyleListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets the style that controls the appearance of the SelectBox.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Touchable", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.touchableListener(simActor, events::selectBoxTouchable));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Visible", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.visibleListener(simActor, events::selectBoxVisible));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Text List", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(SelectBoxListeners.selectBoxTextListListener(this));
            textButton.addListener((Main.makeTooltip("Set the text entries for the SelectBox.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Max List Count", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(SelectBoxListeners.selectBoxMaxListCountListener(events, simActor));
            textButton.addListener((Main.makeTooltip("The maximum visible entries.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Alignment", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(SelectBoxListeners.selectBoxAlignmentListener(events, simActor));
            textButton.addListener((Main.makeTooltip("The alignment of the text in the SelectBox.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Scrolling", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(SelectBoxListeners.selectBoxScrollingListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Choose if scrolling is enabled.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Disabled", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(SelectBoxListeners.selectBoxDisabledListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets whether the SelectBox is disabled initially.", tooltipManager, skin, "scene")));
            
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetResetListener("SelectBox", events::selectBoxReset));
            textButton.addListener((Main.makeTooltip("Resets the settings of the widget to its defaults.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetDeleteListener("SelectBox", events::selectBoxDelete));
            textButton.addListener((Main.makeTooltip("Removes this widget from its parent.", tooltipManager, skin, "scene")));
        } else if (simActor instanceof DialogSceneComposerModel.SimSlider) {
            propertiesLabel.setText("Slider Properties");
    
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(SliderListeners.sliderNameListener(this));
            textButton.addListener((Main.makeTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(SliderListeners.sliderStyleListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets the style that controls the appearance of the Slider.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Touchable", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.touchableListener(simActor, events::sliderTouchable));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Visible", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.visibleListener(simActor, events::sliderVisible));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Value Settings", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(SliderListeners.sliderValueSettingsListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Set the value, minimum, maximum, and increment of the Slider.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Orientation", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(SliderListeners.sliderOrientationListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Change the orientation of the Slider.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Animation", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(SliderListeners.sliderAnimationListener(this));
            textButton.addListener((Main.makeTooltip("Change the progress animation as it increases or decreases.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Round", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(SliderListeners.sliderRoundListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Rounds the drawable positions to integers.", tooltipManager, skin, "scene")));
            
            textButton = new TextButton("Disabled", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(SliderListeners.sliderDisabledListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets whether the button is disabled initially.", tooltipManager, skin, "scene")));
            
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetResetListener("Slider", events::sliderReset));
            textButton.addListener((Main.makeTooltip("Resets the settings of the widget to its defaults.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetDeleteListener("Slider", events::sliderDelete));
            textButton.addListener((Main.makeTooltip("Removes this widget from its parent.", tooltipManager, skin, "scene")));
        } else if (simActor instanceof DialogSceneComposerModel.SimTextArea) {
            propertiesLabel.setText("TextArea Properties");
    
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TextAreaListeners.textAreaNameListener(this));
            textButton.addListener((Main.makeTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TextAreaListeners.textAreaStyleListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets the style that controls the appearance of the TextArea.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Touchable", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.touchableListener(simActor, events::textAreaTouchable));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Visible", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.visibleListener(simActor, events::textAreaVisible));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Text", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TextAreaListeners.textAreaTextListener(this));
            textButton.addListener((Main.makeTooltip("Sets the text inside of the TextArea.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Message Text", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TextAreaListeners.textAreaMessageTextListener(this));
            textButton.addListener((Main.makeTooltip("The text to be shown while there is no text, and the TextArea is not focused.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Password", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TextAreaListeners.textAreaPasswordListener(this));
            textButton.addListener((Main.makeTooltip("Enable password mode and set the password character.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Selection", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TextAreaListeners.textAreaSelectionListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Set the cursor position and selected range.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Alignment", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TextAreaListeners.textAreaAlignmentListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Set the alignment of the typed text.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Focus Traversal", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TextAreaListeners.textAreaFocusTraversalListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Enable traversal to the next TextArea by using the TAB key.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Max Length", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TextAreaListeners.textAreaMaxLengthListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets the maximum length of the typed text.", tooltipManager, skin, "scene")));
            
            textButton = new TextButton("Preferred Rows", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TextAreaListeners.textAreaPreferredRowsListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Set the preferred number of lines.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Disabled", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TextAreaListeners.textAreaDisabledListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets whether the TextArea is disabled initially.", tooltipManager, skin, "scene")));
            
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetResetListener("TextArea", events::textAreaReset));
            textButton.addListener((Main.makeTooltip("Resets the settings of the widget to its defaults.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetDeleteListener("TextArea", events::textAreaDelete));
            textButton.addListener((Main.makeTooltip("Removes this widget from its parent.", tooltipManager, skin, "scene")));
        } else if (simActor instanceof DialogSceneComposerModel.SimTextField) {
            propertiesLabel.setText("TextField Properties");
    
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TextFieldListeners.textFieldNameListener(this));
            textButton.addListener((Main.makeTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TextFieldListeners.textFieldStyleListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets the style that controls the appearance of the TextField.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Touchable", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.touchableListener(simActor, events::textFieldTouchable));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Visible", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.visibleListener(simActor, events::textFieldVisible));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Text", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TextFieldListeners.textFieldTextListener(this));
            textButton.addListener((Main.makeTooltip("Sets the text inside of the TextField.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Message Text", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TextFieldListeners.textFieldMessageTextListener(this));
            textButton.addListener((Main.makeTooltip("The text to be shown while there is no text, and the TextField is not focused.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Password", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TextFieldListeners.textFieldPasswordListener(this));
            textButton.addListener((Main.makeTooltip("Enable password mode and set the password character.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Selection", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TextFieldListeners.textFieldSelectionListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Set the cursor position and selected range.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Alignment", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TextFieldListeners.textFieldAlignmentListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Set the alignment of the typed text.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Focus Traversal", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TextFieldListeners.textFieldFocusTraversalListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Enable traversal to the next TextField by using the TAB key.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Max Length", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TextFieldListeners.textFieldMaxLengthListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets the maximum length of the typed text.", tooltipManager, skin, "scene")));
            
            textButton = new TextButton("Disabled", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TextFieldListeners.textFieldDisabledListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets whether the TextField is disabled initially.", tooltipManager, skin, "scene")));
            
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetResetListener("TextField", events::textFieldReset));
            textButton.addListener((Main.makeTooltip("Resets the settings of the widget to its defaults.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetDeleteListener("TextField", events::textFieldDelete));
            textButton.addListener((Main.makeTooltip("Removes this widget from its parent.", tooltipManager, skin, "scene")));
        } else if (simActor instanceof DialogSceneComposerModel.SimTouchPad) {
            propertiesLabel.setText("TouchPad Properties");
    
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TouchPadListeners.touchPadNameListener(this));
            textButton.addListener((Main.makeTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TouchPadListeners.touchPadStyleListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets the style that controls the appearance of the TouchPad.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Touchable", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.touchableListener(simActor, events::touchPadTouchable));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Visible", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.visibleListener(simActor, events::touchPadVisible));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Dead Zone", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TouchPadListeners.touchPadDeadZoneListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Change the dead zone that does not react to user input.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Reset on Touch Up", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TouchPadListeners.touchPadResetOnTouchUpListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Enable the reset of the touch pad position upon the release of the widget.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetResetListener("TouchPad", events::touchPadReset));
            textButton.addListener((Main.makeTooltip("Resets the settings of the widget to its defaults.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetDeleteListener("TouchPad", events::touchPadDelete));
            textButton.addListener((Main.makeTooltip("Removes this widget from its parent.", tooltipManager, skin, "scene")));
        } else if (simActor instanceof DialogSceneComposerModel.SimContainer) {
            propertiesLabel.setText("Container Properties");
    
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ContainerListeners.containerNameListener(this));
            textButton.addListener((Main.makeTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Set Widget", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.setWidgetListener(
                    this, (widgetType, popTable) -> ContainerListeners.showConfirmContainerSetWidgetDialog(DialogSceneComposer.this, widgetType,
                            popTable)));
            textButton.addListener((Main.makeTooltip("Set the widget assigned to this Container.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Touchable", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.touchableListener(simActor, events::containerTouchable));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Visible", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.visibleListener(simActor, events::containerVisible));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
            
            textButton = new TextButton("Background", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.selectDrawableListener(((SimContainer) simActor).background, "The background image of the Container.",events::containerBackground));
            textButton.addListener((Main.makeTooltip("Set the background of the Container.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Fill", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ContainerListeners.containerFillListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Set the fill of the widget.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Size", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ContainerListeners.containerSizeListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Set the size of the widget.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Padding", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ContainerListeners.containerPaddingListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Set the padding of the widget.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Alignment", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ContainerListeners.containerAlignmentListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Set the alignment of the widget.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetResetListener("Container", events::containerReset));
            textButton.addListener((Main.makeTooltip("Resets the settings of the widget to its defaults.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetDeleteListener("Container", events::containerDelete));
            textButton.addListener((Main.makeTooltip("Removes this widget from its parent.", tooltipManager, skin, "scene")));
        } else if (simActor instanceof DialogSceneComposerModel.SimHorizontalGroup) {
            propertiesLabel.setText("HorizontalGroup Properties");
    
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(HorizontalGroupListeners.horizontalGroupNameListener(this));
            textButton.addListener((Main.makeTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Add Child", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.setWidgetListener(
                    this, (widgetType, popTable) -> HorizontalGroupListeners.horizontalGroupAddChild(events, widgetType, popTable)));
            textButton.addListener((Main.makeTooltip("Adds a widget to the HorizontalGroup", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Touchable", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.touchableListener(simActor, events::horizontalGroupTouchable));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Visible", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.visibleListener(simActor, events::horizontalGroupVisible));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Expand", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(HorizontalGroupListeners.horizontalGroupExpandFillGrowListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Set the widgets to expand to the available space", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Padding/Spacing", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(HorizontalGroupListeners.horizontalGroupPaddingSpacingListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Set the padding of the widgets.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Wrap", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(HorizontalGroupListeners.horizontalGroupWrapListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Set whether widgets will wrap to the next line when the width is decreased.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Alignment", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(HorizontalGroupListeners.horizontalGroupAlignmentListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Set the alignment of the widgets", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Row Alignment", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(HorizontalGroupListeners.horizontalGroupRowAlignmentListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Set the alignment of the widgets", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Reverse", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(HorizontalGroupListeners.horizontalGroupReverseListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Reverse the display order of the widgets.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetResetListener("HorizontalGroup", events::horizontalGroupReset));
            textButton.addListener((Main.makeTooltip("Resets the settings of the widget to its defaults.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetDeleteListener("HorizontalGroup", events::horizontalGroupDelete));
            textButton.addListener((Main.makeTooltip("Removes this widget from its parent.", tooltipManager, skin, "scene")));
        } else if (simActor instanceof DialogSceneComposerModel.SimScrollPane) {
            propertiesLabel.setText("ScrollPane Properties");
    
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ScrollPaneListeners.scrollPaneNameListener(this));
            textButton.addListener((Main.makeTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ScrollPaneListeners.scrollPaneStyleListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets the style that controls the appearance of the ScrollPane.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Set Widget", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.setWidgetListener(
                    this, (widgetType, popTable) -> ScrollPaneListeners.showConfirmScrollPaneSetWidgetDialog(DialogSceneComposer.this, widgetType,
                            popTable)));
            textButton.addListener((Main.makeTooltip("Set the widget assigned to this ScrollPane.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Touchable", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.touchableListener(simActor, events::scrollPaneTouchable));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Visible", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.visibleListener(simActor, events::scrollPaneVisible));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
            
            textButton = new TextButton("Knobs", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ScrollPaneListeners.scrollPaneKnobsListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Knob and Scroll Bar settings.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Scrolling", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(ScrollPaneListeners.scrollPaneScrollListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Scroll settings.", tooltipManager, skin, "scene")));
            
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetResetListener("ScrollPane", events::scrollPaneReset));
            textButton.addListener((Main.makeTooltip("Resets the settings of the widget to its defaults.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetDeleteListener("ScrollPane", events::scrollPaneDelete));
            textButton.addListener((Main.makeTooltip("Removes this widget from its parent.", tooltipManager, skin, "scene")));
        } else if (simActor instanceof DialogSceneComposerModel.SimSplitPane) {
            propertiesLabel.setText("SplitPane Properties");
    
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(SplitPaneListeners.splitPaneNameListener(this));
            textButton.addListener((Main.makeTooltip("Set the name of the widget to allow for convenient searching via Group#findActor().", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(SplitPaneListeners.splitPaneStyleListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Set the style that controls the appearance of the SplitPane.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Set First Widget", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.setWidgetListener(this, (widgetType, popTable) -> {
                SplitPaneListeners.showConfirmSplitPaneSetWidgetDialog(this, widgetType, popTable, true);
            }));
            textButton.addListener((Main.makeTooltip("Set the first widget applied to the SplitPane", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Set Second Widget", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.setWidgetListener(this, (widgetType, popTable) -> {
                SplitPaneListeners.showConfirmSplitPaneSetWidgetDialog(this, widgetType, popTable, false);
            }));
            textButton.addListener((Main.makeTooltip("Set the second widget applied to the SplitPane", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Touchable", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.touchableListener(simActor, events::splitPaneTouchable));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Visible", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.visibleListener(simActor, events::splitPaneVisible));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
            
            textButton = new TextButton("Orientation", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(SplitPaneListeners.splitPaneOrientationListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Set the orientation of the SplitPane.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Split", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(SplitPaneListeners.splitPaneSplitListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Set the split, splitMin, and splitMax values.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetResetListener("SplitPane", events::splitPaneReset));
            textButton.addListener((Main.makeTooltip("Resets the settings of the widget to its defaults.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetDeleteListener("SplitPane", events::splitPaneDelete));
            textButton.addListener((Main.makeTooltip("Removes this widget from its parent.", tooltipManager, skin, "scene")));
        } else if (simActor instanceof DialogSceneComposerModel.SimStack) {
            propertiesLabel.setText("Stack Properties");
    
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(StackListeners.stackNameListener(this));
            textButton.addListener((Main.makeTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Add Child", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.setWidgetListener(
                    this, (widgetType, popTable) -> StackListeners.stackAddChild(events, widgetType, popTable)));
            textButton.addListener((Main.makeTooltip("Add a child to the Stack.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Touchable", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.touchableListener(simActor, events::stackTouchable));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Visible", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.visibleListener(simActor, events::stackVisible));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
            
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetResetListener("Stack", events::stackReset));
            textButton.addListener((Main.makeTooltip("Resets the settings of the widget to its defaults.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetDeleteListener("Stack", events::stackDelete));
            textButton.addListener((Main.makeTooltip("Removes this widget from its parent.", tooltipManager, skin, "scene")));
        } else if (simActor instanceof DialogSceneComposerModel.SimNode) {
            propertiesLabel.setText("Node Properties");
    
            var textButton = new TextButton("Set Widget", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.setWidgetListener(
                    this, (widgetType, popTable) -> TreeListeners.showConfirmNodeSetWidgetDialog(DialogSceneComposer.this, widgetType,
                            popTable)));
            textButton.addListener((Main.makeTooltip("Set the widget applied to this Node.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Add Node", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    events.nodeAddNode();
                }
            });
            textButton.addListener((Main.makeTooltip("Adds a new child Node to this Node.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Icon", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.selectDrawableListener(((SimNode) simActor).icon, "The selected drawable for the icon.",events::nodeIcon));
            textButton.addListener((Main.makeTooltip("Select the Drawable applied as an icon to the Node.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Options", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TreeListeners.nodeOptionsListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Change the expanded and selected values.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetResetListener("Node", events::nodeReset));
            textButton.addListener((Main.makeTooltip("Resets the settings of the widget to its defaults.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetDeleteListener("Node", events::nodeDelete));
            textButton.addListener((Main.makeTooltip("Removes this widget from its parent.", tooltipManager, skin, "scene")));
        } else if (simActor instanceof SimTree) {
            propertiesLabel.setText("Tree Properties");
    
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TreeListeners.treeNameListener(this));
            textButton.addListener((Main.makeTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TreeListeners.treeStyleListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets the style that controls the appearance of the Tree.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Add Node", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    events.treeAddNode();
                }
            });
            textButton.addListener((Main.makeTooltip("Adds a new node to this tree.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Touchable", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.touchableListener(simActor, events::treeTouchable));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Visible", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.visibleListener(simActor, events::treeVisible));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
            
            textButton = new TextButton("Padding", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TreeListeners.treePaddingListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets the padding for the tree.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Spacing", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(TreeListeners.treeSpacingListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Sets the spacing for the tree nodes.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetResetListener("Tree", events::treeReset));
            textButton.addListener((Main.makeTooltip("Resets the settings of the widget to its defaults.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetDeleteListener("Tree", events::treeDelete));
            textButton.addListener((Main.makeTooltip("Removes this widget from its parent.", tooltipManager, skin, "scene")));
        } else if (simActor instanceof DialogSceneComposerModel.SimVerticalGroup) {
            propertiesLabel.setText("VerticalGroup Properties");
    
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(VerticalGroupListeners.verticalGroupNameListener(this));
            textButton.addListener((Main.makeTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", tooltipManager, skin, "scene")));
            
            textButton = new TextButton("Add Child", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.setWidgetListener(
                    this,
                    (widgetType, popTable) -> VerticalGroupListeners.verticalGroupAddChild(events, widgetType, popTable)));
            textButton.addListener((Main.makeTooltip("Adds a widget to the VerticalGroup.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Touchable", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.touchableListener(simActor, events::verticalGroupTouchable));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Visible", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.visibleListener(simActor, events::verticalGroupVisible));
            textButton.addListener((Main.makeTooltip("Sets whether this widget can be clicked on.", tooltipManager, skin, "scene")));
            
            textButton = new TextButton("Expand", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(VerticalGroupListeners.verticalGroupExpandFillGrowListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Set the widgets to expand to the available space.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Padding/Spacing", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(VerticalGroupListeners.verticalGroupPaddingSpacingListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Set the padding/spacing of the widgets.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Wrap", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(VerticalGroupListeners.verticalGroupWrapListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Set whether widgets will wrap to the next line when the height is decreased.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Alignment", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(VerticalGroupListeners.verticalGroupAlignmentListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Set the alignment of the widgets", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Column Alignment", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(VerticalGroupListeners.verticalGroupColumnAlignmentListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Set the alignment of the widgets", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Reverse", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(VerticalGroupListeners.verticalGroupReverseListener(events, simActor));
            textButton.addListener((Main.makeTooltip("Reverse the display order of the widgets.", tooltipManager, skin, "scene")));
            
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetResetListener("VerticalGroup", events::verticalGroupReset));
            textButton.addListener((Main.makeTooltip("Resets the settings of the widget to its defaults.", tooltipManager, skin, "scene")));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(handListener);
            textButton.addListener(GeneralListeners.widgetDeleteListener("VerticalGroup", events::verticalGroupDelete));
            textButton.addListener((Main.makeTooltip("Removes this widget from its parent.", tooltipManager, skin, "scene")));
        }
    }
    
    private EventListener rootAddTableListener() {
        var popTableClickListener = new PopTableClickListener(skin, "dark");
        var popTable = popTableClickListener.getPopTable();
        popTable.key(Keys.ESCAPE, popTable::hide);
        
        var label = new Label("New Table:", skin, "scene-label");
        popTable.add(label);
        label.addListener((Main.makeTooltip("Creates a base Table and adds it directly to the stage. This will serve as the basis for the rest of your UI layout and will fill the entire screen.", tooltipManager, skin, "scene")));
    
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
                        events.rootAddTable(intPair.x + 1, intPair.y + 1);
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
    
    private EventListener rootBackgroundColorListener() {
        var popTableClickListener = new PopTableClickListener(skin) {
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
                imageButton.getImage().setColor(rootActor.backgroundColor == null ? Color.WHITE : rootActor.backgroundColor.color);
                popTable.add(imageButton).minWidth(100);
                imageButton.addListener(handListener);
                imageButton.addListener((Main.makeTooltip("Select the color of the background.", tooltipManager, skin, "scene")));
                imageButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        popTable.hide();
                        dialogFactory.showDialogColors(new StyleProperty(), (colorData, pressedCancel) -> {
                            if (!pressedCancel) {
                                events.rootBackgroundColor(colorData);
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
                label = new Label(rootActor.backgroundColor == null ? "white" : rootActor.backgroundColor.getName(), skin, "scene-label-colored");
                popTable.add(label);
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public void populatePath() {
        var root = pathTable;
        root.clear();
        
        var objects = new Array<DialogSceneComposerModel.SimActor>();
        objects.add(simActor);
        
        var object = simActor;
        
        while (object != null) {
            if (object.parent != null) {
                object = object.parent;
                objects.add(object);
            } else {
                object = null;
            }
        }
        
        while (objects.size > 0) {
            object = objects.pop();
            
            var textButton = new TextButton(object.toString(), skin, object == simActor? "scene-small-highlighted" : "scene-small");
            textButton.setUserObject(object);
            root.add(textButton);
            textButton.addListener(handListener);
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    simActor = (DialogSceneComposerModel.SimActor) textButton.getUserObject();
                    populateProperties();
                    model.updatePreview();
                    populatePath();
                }
            });
            
            if (objects.size > 0) {
                var image = new Image(skin, "scene-icon-path-seperator");
                root.add(image);
            }
        }
    
        var popTableClickListener = new PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        var popSubTable = new Table();
        var scrollPane = new ScrollPane(popSubTable, skin, "scene");
        popTable.add(scrollPane).grow();
        
        if (simActor instanceof DialogSceneComposerModel.SimTable) {
            var table = (DialogSceneComposerModel.SimTable) simActor;
            if (table.cells.size > 0) {
                var image = new Image(skin, "scene-icon-path-child-seperator");
                root.add(image);
    
                var textButton = new TextButton("Select Child", skin, "scene-small");
                root.add(textButton);
                textButton.addListener(handListener);
                
                int row = 0;
                for (var cell : table.cells) {
                    var textButton1 = new TextButton(cell.toString(), skin, "scene-small");
                    if (cell.row > row) {
                        popSubTable.row();
                        row++;
                    }
                    popSubTable.add(textButton1).colspan(cell.colSpan).fillX();
                    textButton1.addListener(handListener);
                    textButton1.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            simActor = cell;
                            populateProperties();
                            model.updatePreview();
                            populatePath();
                            popTable.hide();
                        }
                    });
                }
                textButton.addListener(popTableClickListener);
            }
        } else if (simActor instanceof SimSingleChild) {
            var simSingleChild = (SimSingleChild) simActor;
            if (simSingleChild.getChild() != null) {
                var image = new Image(skin, "scene-icon-path-child-seperator");
                root.add(image);
        
                var textButton = new TextButton(simSingleChild.getChild().toString(), skin, "scene-small");
                root.add(textButton);
                textButton.addListener(handListener);
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        simActor = simSingleChild.getChild();
                        populateProperties();
                        model.updatePreview();
                        populatePath();
                    }
                });
            }
        } else if (simActor instanceof SimMultipleChildren) {
            var simMultipleChildren = (SimMultipleChildren) simActor;
            if (simMultipleChildren.getChildren().size > 0) {
                var image = new Image(skin, "scene-icon-path-child-seperator");
                root.add(image);
        
                if (simMultipleChildren.getChildren().size == 1) {
                    var textButton = new TextButton(simMultipleChildren.getChildren().first().toString(), skin, "scene-small");
                    root.add(textButton);
                    textButton.addListener(handListener);
                    textButton.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            simActor = simMultipleChildren.getChildren().first();
                            populateProperties();
                            model.updatePreview();
                            populatePath();
                        }
                    });
                } else {
                    var textButton = new TextButton("Select Child", skin, "scene-small");
                    root.add(textButton);
                    textButton.addListener(handListener);
    
                    for (var child : simMultipleChildren.getChildren()) {
                        var textButton1 = new TextButton(child.toString(), skin, "scene-small");
                        popSubTable.add(textButton1).row();
                        textButton1.addListener(handListener);
                        textButton1.addListener(new ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, Actor actor) {
                                simActor = child;
                                populateProperties();
                                populatePath();
                                model.updatePreview();
                                popTable.hide();
                            }
                        });
                    }
                    textButton.addListener(popTableClickListener);
                }
            }
        }
    
        root.add().growX();
    }
    
    @Override
    public Dialog show(Stage stage, Action action) {
        super.show(stage, action);
        stage.setScrollFocus(findActor("scroll-properties"));
        return this;
    }
    
    public PopTable showHelpDialog() {
        var root = new PopTable(skin);
        root.setHideOnUnfocus(true);
        root.setModal(true);
        root.setKeepSizedWithinStage(true);
        root.setKeepCenteredInWindow(true);
        root.pad(20);
        root.addListener(handListener);
        root.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                root.hide();
            }
        });
    
        var label = new Label("About Scene Composer", skin, "scene-title-bg");
        label.setAlignment(Align.center);
        root.add(label).growX().padBottom(10);
        
        root.row();
        label = new Label("Scene Composer is a live scenegraph editor for Scene2D.UI. The primary goal is to allow " +
                "Skin Composer users to test out their skins quickly in simple, reusable layouts. \n\nAs a consequence, " +
                "Scene Composer is not capable of making complex UI's. The user is encouraged to learn the nuances of " +
                "Scene2D and create their layouts via code. The export options are included as a convenience, not a " +
                "replacement for learning proper libGDX techniques.", skin, "scene-label-colored");
        label.setWrap(true);
        root.add(label).growX();
    
        root.row();
        var horizontalGroup = new HorizontalGroup();
        root.add(horizontalGroup).padTop(20).left();
    
        label = new Label("Documentation on creating scenes is available ", skin, "scene-label-colored");
        horizontalGroup.addActor(label);
    
        var textButton = new TextButton("here", skin, "scene-link");
        horizontalGroup.addActor(textButton);
        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.net.openURI("https://github.com/raeleus/skin-composer/wiki/Scene-Composer");
            }
        });
    
        root.row();
        horizontalGroup = new HorizontalGroup();
        root.add(horizontalGroup).padTop(20).left();
    
        label = new Label("Building scene JSON widgets requires ", skin, "scene-label-colored");
        horizontalGroup.addActor(label);
    
        textButton = new TextButton("Stripe Widgets", skin, "scene-link");
        horizontalGroup.addActor(textButton);
        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.net.openURI("https://github.com/raeleus/stripe/blob/master/README.md");
            }
        });
        
        label = new Label(".", skin, "scene-label-colored");
        horizontalGroup.addActor(label);
        
        root.show(getStage());
        
        return root;
    }
    
    public PopTable showExportDialog() {
        var root = new PopTable(skin);
        root.setHideOnUnfocus(true);
        root.setModal(true);
        root.setKeepSizedWithinStage(true);
        root.setKeepCenteredInWindow(true);
        root.pad(20);
        
        var label = new Label("Export", skin, "scene-title-bg");
        label.setAlignment(Align.center);
        root.add(label).growX().pad(10);
        
        root.row();
        var table = new Table();
        root.add(table);
        
        table.defaults().space(10f);
        var textButton = new TextButton("Save JSON", skin, "scene-med");
        table.add(textButton).fillX();
        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                var file = desktopWorker.saveDialog("Export JSON...", projectData.getLastSceneComposerJson(), "json", "JSON Files (*.json)");
                if (file != null) {
                    events.exportTemplate(new FileHandle(file));
                }
            
                if (file != null) {
                    root.hide();
                }
            }
        });
    
        var horizontalGroup = new HorizontalGroup();
        table.add(horizontalGroup).expandX().left();
        
        label = new Label("A JSON file compatible with ", skin, "scene-label-colored");
        horizontalGroup.addActor(label);
        
        textButton = new TextButton("SceneComposerStageBuilder", skin, "scene-link");
        horizontalGroup.addActor(textButton);
        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.net.openURI("https://github.com/raeleus/stripe/blob/master/README.md");
            }
        });
    
        table.row();
        textButton = new TextButton("Save to JAVA", skin, "scene-med");
        table.add(textButton).fillX();
        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                var file = desktopWorker.saveDialog("Export Java...", projectData.getLastSceneComposerJson(), "java", "Java Files (*.java)");
                var fileHandle = new FileHandle(file);
                if (file != null) {
                    if (!fileHandle.extension().equalsIgnoreCase("java")) fileHandle = fileHandle.sibling(fileHandle.name() + ".java");
                    events.exportJava(fileHandle);
                    root.hide();
                }
            }
        });
    
        label = new Label("A file to be added directly into your project", skin, "scene-label-colored");
        table.add(label).expandX().left();
    
        table.row();
        textButton = new TextButton("Copy to Clipboard", skin, "scene-med");
        table.add(textButton).fillX();
        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                events.exportClipboard();
                root.hide();
            }
        });
    
        label = new Label("A minimal version to be pasted into your existing code", skin, "scene-label-colored");
        table.add(label).expandX().left();

        root.show(getStage());
        return root;
    }
    
    public PopTable showFindDialog() {
        var root = new PopTable(skin);
        
        root.setHideOnUnfocus(true);
        root.setModal(true);
        root.setKeepSizedWithinStage(true);
        root.setKeepCenteredInWindow(true);
        root.pad(20);
        
        defaults().space(15);
        var label = new Label("Find by Name", skin, "scene-title-bg");
        label.setAlignment(Align.center);
        root.add(label).growX().pad(10);
        
        root.row();
        var table = new Table();
        root.add(table);
        
        table.defaults().space(5);
        label = new Label("Name:", skin, "scene-label-colored");
        table.add(label);
        
        var textField = new TextField("", skin, "scene");
        table.add(textField).minWidth(300);
        textField.addListener(ibeamListener);
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                var simActor = model.findSimActorByName(textField.getText());
                Label label = root.findActor("found-label");
                TextButton textButton = root.findActor("find-button");
                if (simActor != null) {
                    label.setText("Actor found (" + simActor.getClass().getSimpleName() + ")");
                    textButton.setDisabled(false);
                    textButton.setUserObject(simActor);
                } else {
                    label.setText("Actor not found");
                    textButton.setDisabled(true);
                }
            }
        });
        textField.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                TextButton textButton = root.findActor("find-button");
                if (!textButton.isDisabled() && (keycode == Keys.ENTER || keycode == Keys.NUMPAD_ENTER)) {
                    textButton.setChecked(true);
                    return true;
                } else {
                    return false;
                }
            }
        });
        
        root.row();
        label = new Label(" ", skin, "scene-label-colored");
        label.setName("found-label");
        root.add(label);
        
        root.row();
        table = new Table();
        root.add(table);
        
        table.defaults().space(10);
        var textButton = new TextButton("Select Widget", skin, "scene-med");
        textButton.setName("find-button");
        table.add(textButton);
        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                simActor = ((SimActor) actor.getUserObject());
                dialog.populateProperties();
                dialog.populatePath();
                dialog.model.updatePreview();
                root.hide();
            }
        });
    
        textButton = new TextButton("Cancel", skin, "scene-med");
        table.add(textButton);
        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                root.hide();
            }
        });
        
        root.addListener(new PopTable.TableShowHideListener() {
            @Override
            public void tableShown(Event event) {
                getStage().setKeyboardFocus(textField);
            }
    
            @Override
            public void tableHidden(Event event) {
        
            }
        });
        
        root.show(getStage());
        return root;
    }
    
    public PopTable showSettingsDialog() {
        var root = new PopTable(skin);
        root.setHideOnUnfocus(true);
        root.setModal(true);
        root.setKeepSizedWithinStage(true);
        root.setKeepCenteredInWindow(true);
        root.pad(20);
        
        var label = new Label("Settings", skin, "scene-title-bg");
        label.setAlignment(Align.center);
        root.add(label).growX().padBottom(10);
        
        root.row();
        var table = new Table();
        root.add(table).growX();
        
        table.defaults().left().space(5);
        label = new Label("Package", skin, "scene-label-colored");
        table.add(label);
        
        var textField = new TextField(rootActor.packageString, skin, "scene");
        var keyboardFocus = textField;
        table.add(textField).width(300).uniformX();
        textField.addListener(ibeamListener);
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                events.rootPackage(((TextField) actor).getText());
            }
        });
    
        table.row();
        label = new Label("Class", skin, "scene-label-colored");
        table.add(label);
    
        textField = new TextField(rootActor.classString, skin, "scene");
        table.add(textField).uniformX().fillX();
        textField.addListener(ibeamListener);
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                events.rootClass(((TextField) actor).getText());
            }
        });
    
        table.row();
        label = new Label("Skin Path", skin, "scene-label-colored");
        table.add(label);
    
        textField = new TextField(rootActor.skinPath, skin, "scene");
        table.add(textField).uniformX().fillX();
        textField.addListener(ibeamListener);
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                events.rootSkinPath(((TextField) actor).getText());
            }
        });
    
        table.row();
        label = new Label("Background Color", skin, "scene-label-colored");
        table.add(label);
    
        var imageButton = new ImageButton(new ImageButton.ImageButtonStyle(skin.get("scene-color", ImageButton.ImageButtonStyle.class)));
        imageButton.getImage().setColor(rootActor.backgroundColor == null ? Color.WHITE : rootActor.backgroundColor.color);
        table.add(imageButton).left();
        imageButton.addListener(handListener);
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dialogFactory.showDialogColors(new StyleProperty(), (colorData, pressedCancel) -> {
                    if (!pressedCancel) {
                        events.rootBackgroundColor(colorData);
                        imageButton.getImage().setColor(colorData == null ? Color.WHITE : colorData.color);
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
        
        root.show(getStage());
        getStage().setKeyboardFocus(keyboardFocus);
        
        return root;
    }
    
    public static boolean isShowing() {
        return dialog != null && stage.getRoot().getChildren().contains(dialog, true);
    }
}