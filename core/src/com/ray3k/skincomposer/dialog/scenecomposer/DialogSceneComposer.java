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
import com.ray3k.stripe.PopTableClickListener;
import com.ray3k.stripe.StripeMenuBar;
import com.ray3k.stripe.StripeMenuBar.KeyboardShortcut;
import com.ray3k.stripe.StripeMenuBar.MenuBarEvent;
import com.ray3k.stripe.StripeMenuBar.MenuBarListener;
import space.earlygrey.shapedrawer.GraphDrawer;

import static com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.*;

public class DialogSceneComposer extends Dialog {
    public static DialogSceneComposer dialog;
    public static Skin skin;
    public GraphDrawer graphDrawer;
    public static Main main;
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
    private TextButton viewButton;
    public DialogSceneComposerModel.SimActor simActor;
    private Table propertiesTable;
    private Table pathTable;
    public Table previewTable;
    
    public DialogSceneComposer() {
        super("", Main.main.getSkin(), "scene");
        dialog = this;
        main = Main.main;
        skin = main.getSkin();
        graphDrawer = main.getGraphDrawer();
        events = new DialogSceneComposerEvents();
        model = new DialogSceneComposerModel();
        
        view = View.LIVE;
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
        var bar = new StripeMenuBar(main.getStage(), skin);
        root.add(bar).growX();
        
        bar.menu("File")
                .item("Import", new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        showImportDialog();
                    }
                })
                .item("Export", new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        showExportDialog();
                    }
                })
                .item("Settings", new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        showSettingsDialog();
                    }
                })
                .item("Quit", new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.menuQuit();
                    }
                });
        
        bar.menu("Scene")
                .item("Refresh", new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.menuRefresh();
                    }
                })
                .item("Clear", new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.menuClear();
                    }
                })
                .item("Undo", new KeyboardShortcut("Ctrl+Z", Keys.Z, Keys.CONTROL_LEFT), new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.menuUndo();
                    }
                })
                .item("Redo", new KeyboardShortcut("Ctrl+R", Keys.R, Keys.CONTROL_LEFT), new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.menuRedo();
                    }
                });
        
        bar.menu("View")
                .item("Live", new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.menuView(View.LIVE);
                    }
                })
                .item("Edit", new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.menuView(View.EDIT);
                    }
                })
                .item("Outline", new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.menuView(View.OUTLINE);
                    }
                });
        
        bar.item("?", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                events.menuHelp();
            }
        });
        
        undoButton = bar.findMenu("Scene").findButton("Undo");
        undoTooltip = new TextTooltip("", main.getTooltipManager(), skin, "scene");
        redoButton = bar.findMenu("Scene").findButton("Redo");
        redoTooltip = new TextTooltip("", main.getTooltipManager(), skin, "scene");
        viewButton = bar.findButton("View");
        
        root.row();
        previewTable = new Table();
        previewTable.setTouchable(Touchable.enabled);
        previewTable.setBackground(skin.getDrawable("white"));
        
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
        splitPane.addListener(main.getVerticalResizeArrowListener());
    
        table = new Table();
        table.setClip(true);
        bottom.add(table).growX().minHeight(0);
        
        label = new Label("Properties", skin, "scene-title-colored");
        table.add(label);
    
        bottom.row();
        table = new Table();
        propertiesTable = table;
        var scrollPane = new ScrollPane(table, skin, "scene");
        scrollPane.setName("scroll-properties");
        scrollPane.setFlickScroll(false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setForceScroll(false,  true);
        bottom.add(scrollPane).grow();
        scrollPane.addListener(main.getScrollFocusListener());
        
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
        updateMenuView();
        model.updatePreview();
    }
    
    public void updateMenuView() {
        switch(view) {
            case EDIT:
                viewButton.setText("View: Edit");
                break;
            case LIVE:
                viewButton.setText("View: Live");
                break;
            case OUTLINE:
                viewButton.setText("View: Outline");
                break;
        }
    }
    
    public void updateMenuUndoRedo() {
        if (model.undoables.size > 0) {
            undoButton.setDisabled(false);
            undoTooltip.getActor().setText(model.undoables.peek().getUndoString());
            undoTooltip.getContainer().pack();
            undoButton.addListener(undoTooltip);
            undoButton.addListener(main.getHandListener());
        } else {
            undoButton.setDisabled(true);
            undoTooltip.hide();
            undoButton.removeListener(undoTooltip);
            undoButton.removeListener(main.getHandListener());
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        }
    
        if (model.redoables.size > 0) {
            redoButton.setDisabled(false);
            redoTooltip.getActor().setText(model.redoables.peek().getRedoString());
            redoTooltip.getContainer().pack();
            redoButton.addListener(redoTooltip);
            redoButton.addListener(main.getHandListener());
        } else {
            redoButton.setDisabled(true);
            redoTooltip.hide();
            redoButton.removeListener(redoTooltip);
            redoButton.removeListener(main.getHandListener());
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
            var textButton = new TextButton("Add Table", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(rootAddTableListener());
            textButton.addListener(new TextTooltip("Creates a table with the specified number of rows and columns.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimTable) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TableListeners.tableNameListener(this));
            textButton.addListener(new TextTooltip("Sets the name of the table to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Background", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.selectDrawableListener(((SimTable) simActor).background, "The background image for the table.",events::tableBackground));
            textButton.addListener(new TextTooltip("Sets the background drawable for the table.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Color", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TableListeners.tableColorListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets the color of the table background and of the table contents.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Padding", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TableListeners.tablePaddingListener(events, simActor));
            textButton.addListener(new TextTooltip("The padding around all of the contents inside the table.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Align", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TableListeners.tableAlignListener(events, simActor));
            textButton.addListener(new TextTooltip("The alignment of the entire contents inside the table.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Set Cells", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TableListeners.tableSetCellsListener(events));
            textButton.addListener(new TextTooltip("Sets the cells for this table. This will erase the existing contents.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetResetListener("Table", events::tableReset));
            textButton.addListener(new TextTooltip("Resets all options back to their defaults.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetDeleteListener("Table", events::tableDelete));
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimCell) {
            var textButton = new TextButton("Set Widget", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.setWidgetListener(
                    this, (widgetType, popTable) -> TableListeners.showConfirmCellSetWidgetDialog(DialogSceneComposer.this, widgetType,
                            popTable)));
            textButton.addListener(new TextTooltip("Creates a new widget and sets it as the contents of this cell.", main.getTooltipManager(), skin, "scene"));
        
            var table = new Table();
            horizontalGroup.addActor(table);
        
            textButton = new TextButton("Add Cell to Left", skin, "scene-med");
            table.add(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    events.cellAddCellToLeft();
                }
            });
            textButton.addListener(new TextTooltip("Creates a new cell to the left of the current one.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Add Cell to Right", skin, "scene-med");
            table.add(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    events.cellAddCellToRight();
                }
            });
            textButton.addListener(new TextTooltip("Creates a new cell to the right of the current one.", main.getTooltipManager(), skin, "scene"));
        
            table = new Table();
            horizontalGroup.addActor(table);
        
            textButton = new TextButton("Add Row Above", skin, "scene-med");
            table.add(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    events.cellAddRowAbove();
                }
            });
            textButton.addListener(new TextTooltip("Adds a new row above the currently selected one.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Add Row Below", skin, "scene-med");
            table.add(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    events.cellAddRowBelow();
                }
            });
            textButton.addListener(new TextTooltip("Adds a new row below the currently selected one.", main.getTooltipManager(), skin, "scene"));
        
            table = new Table();
            horizontalGroup.addActor(table);
        
            textButton = new TextButton("Column Span", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(CellListeners.cellColSpanListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets the column span of the current cell.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Alignment", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(CellListeners.cellAlignmentListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets the alignment of the contents.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Padding / Spacing", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(CellListeners.cellPaddingSpacingListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets the padding and/or spacing of the current cell.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Expand / Fill / Grow", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(CellListeners.cellExpandFillGrowListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets how the current cell and its contents are sized.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Size", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(CellListeners.cellSizeListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets the specific sizes of the contents in the cell.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Uniform", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(CellListeners.cellUniformListener(events, simActor));
            textButton.addListener(new TextTooltip("All cells set to to uniform = true will share the same size.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetResetListener("Cell", events::cellReset));
            textButton.addListener(new TextTooltip("Resets all of the settings of the cell to their defaults.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Delete Cell", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetDeleteListener("Cell", events::cellDelete));
            textButton.addListener(new TextTooltip("Deletes the cell and its contents.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimTextButton) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TextButtonListeners.textButtonNameListener(this));
            textButton.addListener(new TextTooltip("Sets the name of the table to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Text", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TextButtonListeners.textButtonTextListener(this));
            textButton.addListener(new TextTooltip("Sets the text inside of the button.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TextButtonListeners.textButtonStyleListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets the style that controls the appearance of the text button.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Checked", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TextButtonListeners.textButtonCheckedListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets whether the button is checked initially.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Disabled", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TextButtonListeners.textButtonDisabledListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets whether the button is disabled initially.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Color", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TextButtonListeners.textButtonColorListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets the color of the table background and of the table contents.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Padding", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TextButtonListeners.textButtonPaddingListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets the padding of the contents of the button.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetResetListener("TextButton", events::textButtonReset));
            textButton.addListener(new TextTooltip("Resets the settings of the TextButton to their defaults.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetDeleteListener("TextButton", events::textButtonDelete));
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimButton) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ButtonListeners.buttonNameListener(this));
            textButton.addListener(new TextTooltip("Sets the name of the table to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ButtonListeners.buttonStyleListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets the style that controls the appearance of the text button.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Checked", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ButtonListeners.buttonCheckedListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets whether the button is checked initially.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Disabled", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ButtonListeners.buttonDisabledListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets whether the button is disabled initially.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Color", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ButtonListeners.buttonColorListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets the color of the table background and of the table contents.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Padding", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ButtonListeners.buttonPaddingListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets the padding of the contents of the button.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetResetListener("Button", events::buttonReset));
            textButton.addListener(new TextTooltip("Resets the settings of the TextButton to their defaults.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetDeleteListener("Button", events::buttonDelete));
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimImageButton) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ImageButtonListeners.imageButtonNameListener(this));
            textButton.addListener(new TextTooltip("Sets the name of the table to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ImageButtonListeners.imageButtonStyleListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets the style that controls the appearance of the text button.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Checked", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ImageButtonListeners.imageButtonCheckedListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets whether the button is checked initially.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Disabled", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ImageButtonListeners.imageButtonDisabledListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets whether the button is disabled initially.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Color", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ImageButtonListeners.imageButtonColorListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets the color of the table background and of the table contents.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Padding", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ImageButtonListeners.imageButtonPaddingListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets the padding of the contents of the button.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetResetListener("ImageButton", events::imageButtonReset));
            textButton.addListener(new TextTooltip("Resets the settings of the TextButton to their defaults.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetDeleteListener("ImageButton", events::imageButtonDelete));
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimImageTextButton) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ImageTextButtonListeners.imageTextButtonNameListener(this));
            textButton.addListener(new TextTooltip("Sets the name of the table to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Text", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ImageTextButtonListeners.imageTextButtonTextListener(this));
            textButton.addListener(new TextTooltip("Sets the text inside of the button.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ImageTextButtonListeners.imageTextButtonStyleListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets the style that controls the appearance of the text button.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Checked", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ImageTextButtonListeners.imageTextButtonCheckedListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets whether the button is checked initially.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Disabled", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ImageTextButtonListeners.imageTextButtonDisabledListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets whether the button is disabled initially.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Color", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ImageTextButtonListeners.imageTextButtonColorListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets the color of the table background and of the table contents.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Padding", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ImageTextButtonListeners.imageTextButtonPaddingListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets the padding of the contents of the button.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetResetListener("ImageTextButton", events::imageTextButtonReset));
            textButton.addListener(new TextTooltip("Resets the settings of the TextButton to their defaults.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetDeleteListener("ImageTextButton", events::imageButtonDelete));
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimCheckBox) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(CheckBoxListeners.checkBoxNameListener(this));
            textButton.addListener(new TextTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(CheckBoxListeners.checkBoxStyleListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets the style that controls the appearance of the CheckBox.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Text", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(CheckBoxListeners.checkBoxTextListener(this));
            textButton.addListener(new TextTooltip("Sets the text inside of the CheckBox.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Checked", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(CheckBoxListeners.checkBoxCheckedListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets whether the CheckBox is checked initially.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Color", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(CheckBoxListeners.checkBoxColorListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets the color of the CheckBox.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Padding", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(CheckBoxListeners.checkBoxPaddingListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets the padding of the contents of the CheckBox.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Disabled", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(CheckBoxListeners.checkBoxDisabledListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets whether the CheckBox is disabled initially.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetResetListener("CheckBox", events::checkBoxReset));
            textButton.addListener(new TextTooltip("Resets the settings of the widget to its defaults.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetDeleteListener("CheckBox", events::checkBoxDelete));
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimImage) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ImageListeners.imageNameListener(this));
            textButton.addListener(new TextTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Drawable", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.selectDrawableListener(((SimImage) simActor).drawable, "The selected drawable for the image.",events::imageDrawable));
            textButton.addListener(new TextTooltip("Sets the drawable to be drawn as the Image.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Scaling", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ImageListeners.imageScalingListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets the scaling strategy of the Image when it's stretched or squeezed.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetResetListener("Image", events::imageReset));
            textButton.addListener(new TextTooltip("Resets the settings of the widget to its defaults.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetDeleteListener("Image", events::imageDelete));
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimLabel) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(LabelListeners.labelNameListener(this));
            textButton.addListener(new TextTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(LabelListeners.labelStyleListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets the style that controls the appearance of the Label.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Text", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(LabelListeners.labelTextListener(this));
            textButton.addListener(new TextTooltip("Sets the text inside of the Label.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Color", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(LabelListeners.labelColorListener(events, simActor));
            textButton.addListener(new TextTooltip("Changes the color of the text in the Label.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Text Alignment", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(LabelListeners.labelTextAlignmentListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets the alignment of the text when the Label is larger than it's minimum size.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Ellipsis", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(LabelListeners.labelEllipsisListener(this));
            textButton.addListener(new TextTooltip("Enabling ellipsis allows the Label to be shortened and appends ellipsis characters (eg. \"...\")", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Wrap", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(LabelListeners.labelWrapListener(events, simActor));
            textButton.addListener(new TextTooltip("Allows the text to be wrapped to the next line if it exceeds the width of the Label.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetResetListener("Label", events::labelReset));
            textButton.addListener(new TextTooltip("Resets the settings of the widget to its defaults.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetDeleteListener("Label", events::labelDelete));
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimList) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ListListeners.listNameListener(this));
            textButton.addListener(new TextTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ListListeners.listStyleListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets the style that controls the appearance of the List.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Text List", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ListListeners.listTextListListener(this));
            textButton.addListener(new TextTooltip("Set the text entries for the List.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetResetListener("List", events::listReset));
            textButton.addListener(new TextTooltip("Resets the settings of the widget to its defaults.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetDeleteListener("List", events::listDelete));
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimProgressBar) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ProgressBarListeners.progressBarNameListener(this));
            textButton.addListener(new TextTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ProgressBarListeners.progressBarStyleListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets the style that controls the appearance of the ProgressBar.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Value Settings", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ProgressBarListeners.progressBarValueSettingsListener(events, simActor));
            textButton.addListener(new TextTooltip("Set the value, minimum, maximum, and increment of the ProgressBar.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Orientation", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ProgressBarListeners.progressBarOrientationListener(events, simActor));
            textButton.addListener(new TextTooltip("Change the orientation of the ProgressBar.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Animation", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ProgressBarListeners.progressBarAnimationListener(this));
            textButton.addListener(new TextTooltip("Change the progress animation as it increases or decreases.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Round", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ProgressBarListeners.progressBarRoundListener(events, simActor));
            textButton.addListener(new TextTooltip("Rounds the drawable positions to integers.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Disabled", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ProgressBarListeners.progressBarDisabledListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets whether the ProgressBar is disabled initially.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetResetListener("ProgressBar", events::progressBarReset));
            textButton.addListener(new TextTooltip("Resets the settings of the widget to its defaults.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetDeleteListener("ProgressBar", events::progressBarDelete));
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimSelectBox) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(SelectBoxListeners.selectBoxNameListener(this));
            textButton.addListener(new TextTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(SelectBoxListeners.selectBoxStyleListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets the style that controls the appearance of the SelectBox.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Text List", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(SelectBoxListeners.selectBoxTextListListener(this));
            textButton.addListener(new TextTooltip("Set the text entries for the SelectBox.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Max List Count", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(SelectBoxListeners.selectBoxMaxListCountListener(events, simActor));
            textButton.addListener(new TextTooltip("The maximum visible entries.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Alignment", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(SelectBoxListeners.selectBoxAlignmentListener(events, simActor));
            textButton.addListener(new TextTooltip("The alignment of the text in the SelectBox.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Scrolling", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(SelectBoxListeners.selectBoxScrollingListener(events, simActor));
            textButton.addListener(new TextTooltip("Choose if scrolling is enabled.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Disabled", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(SelectBoxListeners.selectBoxDisabledListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets whether the SelectBox is disabled initially.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetResetListener("SelectBox", events::selectBoxReset));
            textButton.addListener(new TextTooltip("Resets the settings of the widget to its defaults.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetDeleteListener("SelectBox", events::selectBoxDelete));
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimSlider) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(SliderListeners.sliderNameListener(this));
            textButton.addListener(new TextTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(SliderListeners.sliderStyleListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets the style that controls the appearance of the Slider.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Value Settings", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(SliderListeners.sliderValueSettingsListener(events, simActor));
            textButton.addListener(new TextTooltip("Set the value, minimum, maximum, and increment of the Slider.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Orientation", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(SliderListeners.sliderOrientationListener(events, simActor));
            textButton.addListener(new TextTooltip("Change the orientation of the Slider.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Animation", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(SliderListeners.sliderAnimationListener(this));
            textButton.addListener(new TextTooltip("Change the progress animation as it increases or decreases.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Round", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(SliderListeners.sliderRoundListener(events, simActor));
            textButton.addListener(new TextTooltip("Rounds the drawable positions to integers.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Disabled", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(SliderListeners.sliderDisabledListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets whether the button is disabled initially.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetResetListener("Slider", events::sliderReset));
            textButton.addListener(new TextTooltip("Resets the settings of the widget to its defaults.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetDeleteListener("Slider", events::sliderDelete));
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimTextArea) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TextAreaListeners.textAreaNameListener(this));
            textButton.addListener(new TextTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TextAreaListeners.textAreaStyleListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets the style that controls the appearance of the TextArea.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Text", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TextAreaListeners.textAreaTextListener(this));
            textButton.addListener(new TextTooltip("Sets the text inside of the TextArea.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Message Text", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TextAreaListeners.textAreaMessageTextListener(this));
            textButton.addListener(new TextTooltip("The text to be shown while there is no text, and the TextArea is not focused.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Password", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TextAreaListeners.textAreaPasswordListener(this));
            textButton.addListener(new TextTooltip("Enable password mode and set the password character.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Selection", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TextAreaListeners.textAreaSelectionListener(events, simActor));
            textButton.addListener(new TextTooltip("Set the cursor position and selected range.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Alignment", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TextAreaListeners.textAreaAlignmentListener(events, simActor));
            textButton.addListener(new TextTooltip("Set the alignment of the typed text.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Focus Traversal", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TextAreaListeners.textAreaFocusTraversalListener(events, simActor));
            textButton.addListener(new TextTooltip("Enable traversal to the next TextArea by using the TAB key.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Max Length", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TextAreaListeners.textAreaMaxLengthListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets the maximum length of the typed text.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Preferred Rows", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TextAreaListeners.textAreaPreferredRowsListener(events, simActor));
            textButton.addListener(new TextTooltip("Set the preferred number of lines.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Disabled", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TextAreaListeners.textAreaDisabledListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets whether the TextArea is disabled initially.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetResetListener("TextArea", events::textAreaReset));
            textButton.addListener(new TextTooltip("Resets the settings of the widget to its defaults.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetDeleteListener("TextArea", events::textAreaDelete));
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimTextField) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TextFieldListeners.textFieldNameListener(this));
            textButton.addListener(new TextTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TextFieldListeners.textFieldStyleListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets the style that controls the appearance of the TextField.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Text", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TextFieldListeners.textFieldTextListener(this));
            textButton.addListener(new TextTooltip("Sets the text inside of the TextField.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Message Text", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TextFieldListeners.textFieldMessageTextListener(this));
            textButton.addListener(new TextTooltip("The text to be shown while there is no text, and the TextField is not focused.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Password", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TextFieldListeners.textFieldPasswordListener(this));
            textButton.addListener(new TextTooltip("Enable password mode and set the password character.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Selection", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TextFieldListeners.textFieldSelectionListener(events, simActor));
            textButton.addListener(new TextTooltip("Set the cursor position and selected range.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Alignment", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TextFieldListeners.textFieldAlignmentListener(events, simActor));
            textButton.addListener(new TextTooltip("Set the alignment of the typed text.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Focus Traversal", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TextFieldListeners.textFieldFocusTraversalListener(events, simActor));
            textButton.addListener(new TextTooltip("Enable traversal to the next TextField by using the TAB key.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Max Length", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TextFieldListeners.textFieldMaxLengthListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets the maximum length of the typed text.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Disabled", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TextFieldListeners.textFieldDisabledListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets whether the TextField is disabled initially.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetResetListener("TextField", events::textFieldReset));
            textButton.addListener(new TextTooltip("Resets the settings of the widget to its defaults.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetDeleteListener("TextField", events::textFieldDelete));
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimTouchPad) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TouchPadListeners.touchPadNameListener(this));
            textButton.addListener(new TextTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TouchPadListeners.touchPadStyleListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets the style that controls the appearance of the TouchPad.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Dead Zone", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TouchPadListeners.touchPadDeadZoneListener(events, simActor));
            textButton.addListener(new TextTooltip("Change the dead zone that does not react to user input.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Reset on Touch Up", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TouchPadListeners.touchPadResetOnTouchUpListener(events, simActor));
            textButton.addListener(new TextTooltip("Enable the reset of the touch pad position upon the release of the widget.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetResetListener("TouchPad", events::touchPadReset));
            textButton.addListener(new TextTooltip("Resets the settings of the widget to its defaults.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetDeleteListener("TouchPad", events::touchPadDelete));
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimContainer) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ContainerListeners.containerNameListener(this));
            textButton.addListener(new TextTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Set Widget", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.setWidgetListener(
                    this, (widgetType, popTable) -> ContainerListeners.showConfirmContainerSetWidgetDialog(DialogSceneComposer.this, widgetType,
                            popTable)));
            textButton.addListener(new TextTooltip("Set the widget assigned to this Container.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Background", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.selectDrawableListener(((SimContainer) simActor).background, "The background image of the Container.",events::containerBackground));
            textButton.addListener(new TextTooltip("Set the background of the Container.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Fill", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ContainerListeners.containerFillListener(events, simActor));
            textButton.addListener(new TextTooltip("Set the fill of the widget.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Size", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ContainerListeners.containerSizeListener(events, simActor));
            textButton.addListener(new TextTooltip("Set the size of the widget.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Padding", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ContainerListeners.containerPaddingListener(events, simActor));
            textButton.addListener(new TextTooltip("Set the padding of the widget.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Alignment", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ContainerListeners.containerAlignmentListener(events, simActor));
            textButton.addListener(new TextTooltip("Set the alignment of the widget.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetResetListener("Container", events::containerReset));
            textButton.addListener(new TextTooltip("Resets the settings of the widget to its defaults.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetDeleteListener("Container", events::containerDelete));
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimHorizontalGroup) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(HorizontalGroupListeners.horizontalGroupNameListener(this));
            textButton.addListener(new TextTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Add Child", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.setWidgetListener(
                    this, (widgetType, popTable) -> HorizontalGroupListeners.horizontalGroupAddChild(events, widgetType, popTable)));
            textButton.addListener(new TextTooltip("Adds a widget to the HorizontalGroup", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Expand", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(HorizontalGroupListeners.horizontalGroupExpandFillGrowListener(events, simActor));
            textButton.addListener(new TextTooltip("Set the widgets to expand to the available space", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Padding/Spacing", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(HorizontalGroupListeners.horizontalGroupPaddingSpacingListener(events, simActor));
            textButton.addListener(new TextTooltip("Set the padding of the widgets.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Wrap", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(HorizontalGroupListeners.horizontalGroupWrapListener(events, simActor));
            textButton.addListener(new TextTooltip("Set whether widgets will wrap to the next line when the width is decreased.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Alignment", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(HorizontalGroupListeners.horizontalGroupAlignmentListener(events, simActor));
            textButton.addListener(new TextTooltip("Set the alignment of the widgets", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Row Alignment", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(HorizontalGroupListeners.horizontalGroupRowAlignmentListener(events, simActor));
            textButton.addListener(new TextTooltip("Set the alignment of the widgets", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Reverse", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(HorizontalGroupListeners.horizontalGroupReverseListener(events, simActor));
            textButton.addListener(new TextTooltip("Reverse the display order of the widgets.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetResetListener("HorizontalGroup", events::horizontalGroupReset));
            textButton.addListener(new TextTooltip("Resets the settings of the widget to its defaults.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetDeleteListener("HorizontalGroup", events::horizontalGroupDelete));
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimScrollPane) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ScrollPaneListeners.scrollPaneNameListener(this));
            textButton.addListener(new TextTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ScrollPaneListeners.scrollPaneStyleListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets the style that controls the appearance of the ScrollPane.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Set Widget", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.setWidgetListener(
                    this, (widgetType, popTable) -> ScrollPaneListeners.showConfirmScrollPaneSetWidgetDialog(DialogSceneComposer.this, widgetType,
                            popTable)));
            textButton.addListener(new TextTooltip("Set the widget assigned to this ScrollPane.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Knobs", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ScrollPaneListeners.scrollPaneKnobsListener(events, simActor));
            textButton.addListener(new TextTooltip("Knob and Scroll Bar settings.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Scrolling", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(ScrollPaneListeners.scrollPaneScrollListener(events, simActor));
            textButton.addListener(new TextTooltip("Scroll settings.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetResetListener("ScrollPane", events::scrollPaneReset));
            textButton.addListener(new TextTooltip("Resets the settings of the widget to its defaults.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetDeleteListener("ScrollPane", events::scrollPaneDelete));
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimSplitPane) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(SplitPaneListeners.splitPaneNameListener(this));
            textButton.addListener(new TextTooltip("Set the name of the widget to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(SplitPaneListeners.splitPaneStyleListener(events, simActor));
            textButton.addListener(new TextTooltip("Set the style that controls the appearance of the SplitPane.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Set First Widget", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.setWidgetListener(this, (widgetType, popTable) -> {
                SplitPaneListeners.showConfirmSplitPaneSetWidgetDialog(this, widgetType, popTable, true);
            }));
            textButton.addListener(new TextTooltip("Set the first widget applied to the SplitPane", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Set Second Widget", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.setWidgetListener(this, (widgetType, popTable) -> {
                SplitPaneListeners.showConfirmSplitPaneSetWidgetDialog(this, widgetType, popTable, false);
            }));
            textButton.addListener(new TextTooltip("Set the second widget applied to the SplitPane", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Orientation", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(SplitPaneListeners.splitPaneOrientationListener(events, simActor));
            textButton.addListener(new TextTooltip("Set the orientation of the SplitPane.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Split", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(SplitPaneListeners.splitPaneSplitListener(events, simActor));
            textButton.addListener(new TextTooltip("Set the split, splitMin, and splitMax values.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetResetListener("SplitPane", events::splitPaneReset));
            textButton.addListener(new TextTooltip("Resets the settings of the widget to its defaults.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetDeleteListener("SplitPane", events::splitPaneDelete));
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimStack) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(StackListeners.stackNameListener(this));
            textButton.addListener(new TextTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Add Child", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.setWidgetListener(
                    this, (widgetType, popTable) -> StackListeners.stackAddChild(events, widgetType, popTable)));
            textButton.addListener(new TextTooltip("Add a child to the Stack.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetResetListener("Stack", events::stackReset));
            textButton.addListener(new TextTooltip("Resets the settings of the widget to its defaults.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetDeleteListener("Stack", events::stackDelete));
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimNode) {
            var textButton = new TextButton("Set Widget", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.setWidgetListener(
                    this, (widgetType, popTable) -> TreeListeners.showConfirmNodeSetWidgetDialog(DialogSceneComposer.this, widgetType,
                            popTable)));
            textButton.addListener(new TextTooltip("Set the widget applied to this Node.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Add Node", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    events.nodeAddNode();
                }
            });
            textButton.addListener(new TextTooltip("Adds a new child Node to this Node.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Icon", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.selectDrawableListener(((SimNode) simActor).icon, "The selected drawable for the icon.",events::nodeIcon));
            textButton.addListener(new TextTooltip("Select the Drawable applied as an icon to the Node.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Options", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TreeListeners.nodeOptionsListener(events, simActor));
            textButton.addListener(new TextTooltip("Change the expanded and selected values.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetResetListener("Node", events::nodeReset));
            textButton.addListener(new TextTooltip("Resets the settings of the widget to its defaults.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetDeleteListener("Node", events::nodeDelete));
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof SimTree) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TreeListeners.treeNameListener(this));
            textButton.addListener(new TextTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TreeListeners.treeStyleListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets the style that controls the appearance of the Tree.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Add Node", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    events.treeAddNode();
                }
            });
            textButton.addListener(new TextTooltip("Adds a new node to this tree.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Padding", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TreeListeners.treePaddingListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets the padding for the tree.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Spacing", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(TreeListeners.treeSpacingListener(events, simActor));
            textButton.addListener(new TextTooltip("Sets the spacing for the tree nodes.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetResetListener("Tree", events::treeReset));
            textButton.addListener(new TextTooltip("Resets the settings of the widget to its defaults.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetDeleteListener("Tree", events::treeDelete));
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimVerticalGroup) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(VerticalGroupListeners.verticalGroupNameListener(this));
            textButton.addListener(new TextTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Add Child", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.setWidgetListener(
                    this,
                    (widgetType, popTable) -> VerticalGroupListeners.verticalGroupAddChild(events, widgetType, popTable)));
            textButton.addListener(new TextTooltip("Adds a widget to the VerticalGroup.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Expand", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(VerticalGroupListeners.verticalGroupExpandFillGrowListener(events, simActor));
            textButton.addListener(new TextTooltip("Set the widgets to expand to the available space.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Padding/Spacing", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(VerticalGroupListeners.verticalGroupPaddingSpacingListener(events, simActor));
            textButton.addListener(new TextTooltip("Set the padding/spacing of the widgets.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Wrap", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(VerticalGroupListeners.verticalGroupWrapListener(events, simActor));
            textButton.addListener(new TextTooltip("Set whether widgets will wrap to the next line when the height is decreased.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Alignment", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(VerticalGroupListeners.verticalGroupAlignmentListener(events, simActor));
            textButton.addListener(new TextTooltip("Set the alignment of the widgets", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Column Alignment", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(VerticalGroupListeners.verticalGroupColumnAlignmentListener(events, simActor));
            textButton.addListener(new TextTooltip("Set the alignment of the widgets", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Reverse", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(VerticalGroupListeners.verticalGroupReverseListener(events, simActor));
            textButton.addListener(new TextTooltip("Reverse the display order of the widgets.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetResetListener("VerticalGroup", events::verticalGroupReset));
            textButton.addListener(new TextTooltip("Resets the settings of the widget to its defaults.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(GeneralListeners.widgetDeleteListener("VerticalGroup", events::verticalGroupDelete));
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        }
    }
    
    private EventListener rootAddTableListener() {
        var popTableClickListener = new PopTableClickListener(skin, "dark");
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("New Table:", skin, "scene-label");
        popTable.add(label);
        label.addListener(new TextTooltip("Creates a base Table and adds it directly to the stage. This will serve as the basis for the rest of your UI layout and will fill the entire screen.", main.getTooltipManager(), skin, "scene"));
    
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
                textButton.addListener(main.getHandListener());
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
                imageButton.addListener(main.getHandListener());
                imageButton.addListener(new TextTooltip("Select the color of the background.", main.getTooltipManager(), skin, "scene"));
                imageButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        popTable.hide();
                        main.getDialogFactory().showDialogColors(new StyleProperty(), (colorData, pressedCancel) -> {
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
            textButton.addListener(main.getHandListener());
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    simActor = (DialogSceneComposerModel.SimActor) textButton.getUserObject();
                    populateProperties();
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
                textButton.addListener(main.getHandListener());
                
                int row = 0;
                for (var cell : table.cells) {
                    var textButton1 = new TextButton(cell.toString(), skin, "scene-small");
                    if (cell.row > row) {
                        popSubTable.row();
                        row++;
                    }
                    popSubTable.add(textButton1).colspan(cell.colSpan).fillX();
                    textButton1.addListener(main.getHandListener());
                    textButton1.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            simActor = cell;
                            populateProperties();
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
                textButton.addListener(main.getHandListener());
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        simActor = simSingleChild.getChild();
                        populateProperties();
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
                    textButton.addListener(main.getHandListener());
                    textButton.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            simActor = simMultipleChildren.getChildren().first();
                            populateProperties();
                            populatePath();
                        }
                    });
                } else {
                    var textButton = new TextButton("Select Child", skin, "scene-small");
                    root.add(textButton);
                    textButton.addListener(main.getHandListener());
    
                    for (var child : simMultipleChildren.getChildren()) {
                        var textButton1 = new TextButton(child.toString(), skin, "scene-small");
                        popSubTable.add(textButton1).row();
                        textButton1.addListener(main.getHandListener());
                        textButton1.addListener(new ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, Actor actor) {
                                simActor = child;
                                populateProperties();
                                populatePath();
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
    
    public Dialog showHelpDialog() {
        var dialog = new Dialog("", skin, "scene-dialog");
    
        var root = dialog.getTitleTable();
        root.clear();
    
        root.add().uniform();
    
        var label = new Label("About Scene Composer", skin, "scene-title");
        root.add(label).expandX();
    
        var button = new Button(skin, "scene-close");
        root.add(button).uniform();
        button.addListener(main.getHandListener());
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dialog.hide();
            }
        });
    
        root = dialog.getContentTable();
        root.pad(10);
        
        label = new Label("Scene Composer is a live scenegraph editor for Scene2D.UI. The primary goal is to allow " +
                "Skin Composer users to test out their skins quickly in simple, reusable layouts. \n\nAs a consequence, " +
                "Scene Composer is not capable of making complex UI's. The user is encouraged to learn the nuances of " +
                "Scene2D and create their layouts via code. The export options are included as a convenience, not a " +
                "replacement for learning proper libGDX techniques.", skin, "scene-label-colored");
        label.setWrap(true);
        root.add(label).growX();
        
        var textButton = new TextButton("OK", skin, "scene-med");
        dialog.button(textButton);
        textButton.addListener(main.getHandListener());
        
        dialog.show(getStage());
        dialog.setSize(500, 350);
        dialog.setPosition((int) (getStage().getWidth() / 2f - dialog.getWidth() / 2f), (int) (getStage().getHeight() / 2f - dialog.getHeight() / 2f));
        
        return dialog;
    }
    
    public Dialog showImportDialog() {
        var dialog = new Dialog("", skin, "scene-dialog");
        
        var root = dialog.getTitleTable();
        root.clear();
        
        root.add().uniform();
        
        var label = new Label("Import", skin, "scene-title");
        root.add(label).expandX();
        
        var button = new Button(skin, "scene-close");
        root.add(button).uniform();
        button.addListener(main.getHandListener());
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dialog.hide();
            }
        });
        
        root = dialog.getContentTable();
        root.pad(10);
        
        var table = new Table();
        root.add(table).growX();
        
        var textField = new TextField("", skin, "scene");
        table.add(textField).growX();
        textField.addListener(main.getIbeamListener());
        
        var textButton = new TextButton("Browse", skin, "scene-small");
        table.add(textButton);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                var file = main.getDesktopWorker().openDialog("Import Template...", main.getProjectData().getLastImportExportPath(), new String[] {"*.json"}, "JSON Files (*.json)");
            
                if (file != null) {
                    textField.setText(file.getPath());
                    textField.setCursorPosition(textField.getText().length());
                }
            }
        });
        
        dialog.getContentTable().row();
        table = new Table();
        root.add(table);
        
        table.defaults().space(5);
        textButton = new TextButton("Import Template", skin, "scene-med");
        table.add(textButton);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                events.dialogImportImportTemplate(Gdx.files.absolute(textField.getText()));
                dialog.hide();
            }
        });
        
        label = new Label("Import JSON Template File", skin, "scene-label-colored");
        table.add(label).expandX().left();
        
        dialog.show(getStage());
        
        getStage().setKeyboardFocus(textField);
        
        return dialog;
    }
    
    public Dialog showExportDialog() {
        var dialog = new Dialog("", skin, "scene-dialog");
    
        var root = dialog.getTitleTable();
        root.clear();
    
        root.add().uniform();
    
        var label = new Label("Export", skin, "scene-title");
        root.add(label).expandX();
    
        var button = new Button(skin, "scene-close");
        root.add(button).uniform();
        button.addListener(main.getHandListener());
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dialog.hide();
            }
        });
        
        root = dialog.getContentTable();
        root.pad(10);
        
        dialog.getContentTable().row();
        var table = new Table();
        root.add(table);
        
        table.defaults().space(5);
        var textButton = new TextButton("Save Template", skin, "scene-med");
        table.add(textButton).uniformX().fillX();
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                var file = main.getDesktopWorker().saveDialog("Export Template...", main.getProjectData().getLastImportExportPath(), new String[] {"*.json"}, "JSON Files (*.json)");
                if (file != null) {
                    events.dialogExportSaveTemplate(new FileHandle(file));
                }
    
                if (file != null) {
                    dialog.hide();
                }
            }
        });
        
        label = new Label("A template to be imported into Scene Composer", skin, "scene-label-colored");
        table.add(label).expandX().left();
    
        table.row();
        textButton = new TextButton("Save to JAVA", skin, "scene-med");
        table.add(textButton).uniformX().fillX();
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                var file = main.getDesktopWorker().saveDialog("Export Java...", main.getProjectData().getLastImportExportPath(), new String[] {"*.java"}, "Java Files (*.java)");
                var fileHandle = new FileHandle(file);
                if (file != null) {
                    if (!fileHandle.extension().equalsIgnoreCase("java")) fileHandle = fileHandle.sibling(fileHandle.name() + ".java");
                    events.dialogExportSaveJava(fileHandle);
                    dialog.hide();
                }
            }
        });
    
        label = new Label("A file to be added directly into your project", skin, "scene-label-colored");
        table.add(label).expandX().left();
    
        table.row();
        textButton = new TextButton("Copy to Clipboard", skin, "scene-med");
        table.add(textButton).uniformX().fillX();
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                events.dialogExportClipboard();
                dialog.hide();
            }
        });
    
        label = new Label("A minimal version to be pasted into your existing code", skin, "scene-label-colored");
        table.add(label).expandX().left();
        
        dialog.show(getStage());
        
        return dialog;
    }
    
    public Dialog showSettingsDialog() {
        var dialog = new Dialog("", skin, "scene-dialog");
        
        var root = dialog.getTitleTable();
        root.clear();
        
        root.add().uniform();
        
        var label = new Label("Settings", skin, "scene-title");
        root.add(label).expandX();
        
        var button = new Button(skin, "scene-close");
        root.add(button).uniform();
        button.addListener(main.getHandListener());
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dialog.hide();
            }
        });
        
        root = dialog.getContentTable();
        root.pad(10);
        
        var table = new Table();
        root.add(table).growX();
        
        table.defaults().left().space(5);
        label = new Label("Package", skin, "scene-label-colored");
        table.add(label);
        
        var textField = new TextField(rootActor.packageString, skin, "scene");
        var keyboardFocus = textField;
        table.add(textField).width(300).uniformX();
        textField.addListener(main.getIbeamListener());
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
        textField.addListener(main.getIbeamListener());
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
        textField.addListener(main.getIbeamListener());
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
        imageButton.addListener(main.getHandListener());
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                main.getDialogFactory().showDialogColors(new StyleProperty(), (colorData, pressedCancel) -> {
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
        
        dialog.getContentTable().row();
        table = new Table();
        root.add(table);
        
        dialog.show(getStage());
        
        getStage().setKeyboardFocus(keyboardFocus);
        
        return dialog;
    }
}