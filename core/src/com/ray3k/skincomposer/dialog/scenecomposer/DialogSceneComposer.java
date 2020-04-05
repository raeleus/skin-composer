package com.ray3k.skincomposer.dialog.scenecomposer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.ray3k.skincomposer.*;
import com.ray3k.skincomposer.PopTable.PopTableClickListener;
import com.ray3k.skincomposer.RangeSlider.ValueBeginChangeEvent;
import com.ray3k.skincomposer.RangeSlider.ValueBeginChangeListener;
import com.ray3k.skincomposer.RangeSlider.ValueEndChangeEvent;
import com.ray3k.skincomposer.RangeSlider.ValueEndChangeListener;
import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.data.StyleProperty;
import com.ray3k.skincomposer.dialog.DialogDrawables;
import com.ray3k.skincomposer.dialog.DialogListener;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerEvents.WidgetType;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.*;
import com.ray3k.skincomposer.utils.IntPair;
import space.earlygrey.shapedrawer.GraphDrawer;
import space.earlygrey.shapedrawer.scene2d.GraphDrawerDrawable;

public class DialogSceneComposer extends Dialog {
    public static DialogSceneComposer dialog;
    private Skin skin;
    private GraphDrawer graphDrawer;
    private Main main;
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
    
    public DialogSceneComposer() {
        super("", Main.main.getSkin(), "scene");
        dialog = this;
        main = Main.main;
        skin = main.getSkin();
        graphDrawer = main.getGraphDrawer();
        events = new DialogSceneComposerEvents();
        model = new DialogSceneComposerModel();
        
        view = View.LIVE;
        simActor = model.root;
        
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
        table = new Table();
        table.setBackground(skin.getDrawable("scene-menu-bar-ten"));
        root.add(table).growX();
        
        var textButton = new TextButton("Import", skin, "scene-menu-button");
        table.add(textButton);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showImportDialog();
            }
        });
    
        textButton = new TextButton("Export", skin, "scene-menu-button");
        table.add(textButton);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showExportDialog();
            }
        });
    
        textButton = new TextButton("Settings", skin, "scene-menu-button");
        table.add(textButton);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showSettingsDialog();
            }
        });
    
        textButton = new TextButton("Quit", skin, "scene-menu-button");
        table.add(textButton);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                events.menuQuit();
            }
        });
        
        var image = new Image(skin, "scene-menu-divider");
        table.add(image).space(10);
    
        textButton = new TextButton("Refresh", skin, "scene-menu-button");
        table.add(textButton);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                events.menuRefresh();
            }
        });
    
        textButton = new TextButton("Clear", skin, "scene-menu-button");
        table.add(textButton);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                events.menuClear();
            }
        });
        
        image = new Image(skin, "scene-menu-divider");
        table.add(image).space(10);
    
        textButton = new TextButton("Undo", skin, "scene-menu-button");
        undoButton = textButton;
        table.add(textButton);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                events.menuUndo();
            }
        });
        undoTooltip = new TextTooltip("", main.getTooltipManager(), skin, "scene");
    
        textButton = new TextButton("Redo", skin, "scene-menu-button");
        redoButton = textButton;
        table.add(textButton);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                events.menuRedo();
            }
        });
        redoTooltip = new TextTooltip("", main.getTooltipManager(), skin, "scene");
    
        image = new Image(skin, "scene-menu-divider");
        table.add(image).space(10);
    
        textButton = new TextButton("", skin, "scene-menu-button");
        viewButton = textButton;
        table.add(textButton).expandX().right().space(5);
        textButton.addListener(main.getHandListener());
        textButton.addListener(menuViewListener());
    
        textButton = new TextButton("?", skin, "scene-menu-button");
        table.add(textButton);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                events.menuHelp();
            }
        });
        
        root.row();
        var top = new Table();
        top.setTouchable(Touchable.enabled);
        top.setBackground(skin.getDrawable("white"));
        
        top.add(model.preview).grow();
        
        var bottom = new Table() {
            @Override
            public float getMinHeight() {
                return 0;
            }
        };
        bottom.setTouchable(Touchable.enabled);
        bottom.setBackground(skin.getDrawable("scene-bg"));

        var splitPane = new SplitPane(top, bottom, true, skin, "scene-vertical");
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
        image = new Image(skin, "scene-path-border");
        bottom.add(image).growX();
        
        bottom.row();
        table = new Table();
        pathTable = table;
        scrollPane = new ScrollPane(table, skin, "scene");
        bottom.add(scrollPane).growX().minHeight(0).space(3);
        
        populatePath();
        
        updateMenuUndoRedo();
        updateMenuView();
    }
    
    private EventListener menuViewListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        var label = new Label("Choose a view:", skin, "scene-label-colored");
        popTable.add(label);
    
        popTable.row();
        var textButton = new TextButton("Edit", skin, "scene-med");
        popTable.add(textButton);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Widget highlights on mouse over. Clicks resolve widget selection.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.menuView(View.EDIT);
            }
        });
    
        popTable.row();
        textButton = new TextButton("Live", skin, "scene-med");
        popTable.add(textButton);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Widgets behave exactly as they do in a live libGDX project.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.menuView(View.LIVE);
            }
        });
    
        popTable.row();
        textButton = new TextButton("Outline", skin, "scene-med");
        popTable.add(textButton);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Debug outlines are enabled. Widget highlights on mouse over. Clicks resolve widget selection.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.menuView(View.OUTLINE);
            }
        });
        return popTableClickListener;
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
    
        if (simActor instanceof DialogSceneComposerModel.SimGroup) {
            var textButton = new TextButton("Add Table", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(rootAddTableListener());
            textButton.addListener(new TextTooltip("Creates a table with the specified number of rows and columns.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimTable) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(tableNameListener());
            textButton.addListener(new TextTooltip("Sets the name of the table to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Background", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(tableBackgroundListener());
            textButton.addListener(new TextTooltip("Sets the background drawable for the table.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Color", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(tableColorListener());
            textButton.addListener(new TextTooltip("Sets the color of the table background and of the table contents.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Padding", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(tablePaddingListener());
            textButton.addListener(new TextTooltip("The padding around all of the contents inside the table.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Align", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(tableAlignListener());
            textButton.addListener(new TextTooltip("The alignment of the entire contents inside the table.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Set Cells", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(tableSetCellsListener());
            textButton.addListener(new TextTooltip("Sets the cells for this table. This will erase the existing contents.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(tableResetListener());
            textButton.addListener(new TextTooltip("Resets all options back to their defaults.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(tableDeleteListener());
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimCell) {
            var textButton = new TextButton("Set Widget", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(setWidgetListener(
                    this::showConfirmCellSetWidgetDialog));
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
            textButton.addListener(cellColSpanListener());
            textButton.addListener(new TextTooltip("Sets the column span of the current cell.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Alignment", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(cellAlignmentListener());
            textButton.addListener(new TextTooltip("Sets the alignment of the contents.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Padding / Spacing", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(cellPaddingSpacingListener());
            textButton.addListener(new TextTooltip("Sets the padding and/or spacing of the current cell.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Expand / Fill / Grow", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(cellExpandFillGrowListener());
            textButton.addListener(new TextTooltip("Sets how the current cell and its contents are sized.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Size", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(cellSizeListener());
            textButton.addListener(new TextTooltip("Sets the specific sizes of the contents in the cell.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Uniform", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(cellUniformListener());
            textButton.addListener(new TextTooltip("All cells set to to uniform = true will share the same size.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(cellResetListener());
            textButton.addListener(new TextTooltip("Resets all of the settings of the cell to their defaults.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Delete Cell", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(cellDeleteListener());
            textButton.addListener(new TextTooltip("Deletes the cell and its contents.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimTextButton) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(textButtonNameListener());
            textButton.addListener(new TextTooltip("Sets the name of the table to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Text", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(textButtonTextListener());
            textButton.addListener(new TextTooltip("Sets the text inside of the button.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(textButtonStyleListener());
            textButton.addListener(new TextTooltip("Sets the style that controls the appearance of the text button.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Checked", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(textButtonCheckedListener());
            textButton.addListener(new TextTooltip("Sets whether the button is checked initially.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Disabled", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(textButtonDisabledListener());
            textButton.addListener(new TextTooltip("Sets whether the button is disabled initially.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Color", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(textButtonColorListener());
            textButton.addListener(new TextTooltip("Sets the color of the table background and of the table contents.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Padding", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(textButtonPaddingListener());
            textButton.addListener(new TextTooltip("Sets the padding of the contents of the button.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(textButtonResetListener());
            textButton.addListener(new TextTooltip("Resets the settings of the TextButton to their defaults.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(textButtonDeleteListener());
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimButton) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(buttonNameListener());
            textButton.addListener(new TextTooltip("Sets the name of the table to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(buttonStyleListener());
            textButton.addListener(new TextTooltip("Sets the style that controls the appearance of the text button.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Checked", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(buttonCheckedListener());
            textButton.addListener(new TextTooltip("Sets whether the button is checked initially.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Disabled", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(buttonDisabledListener());
            textButton.addListener(new TextTooltip("Sets whether the button is disabled initially.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Color", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(buttonColorListener());
            textButton.addListener(new TextTooltip("Sets the color of the table background and of the table contents.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Padding", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(buttonPaddingListener());
            textButton.addListener(new TextTooltip("Sets the padding of the contents of the button.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(buttonResetListener());
            textButton.addListener(new TextTooltip("Resets the settings of the TextButton to their defaults.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(buttonDeleteListener());
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimImageButton) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(imageButtonNameListener());
            textButton.addListener(new TextTooltip("Sets the name of the table to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(imageButtonStyleListener());
            textButton.addListener(new TextTooltip("Sets the style that controls the appearance of the text button.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Checked", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(imageButtonCheckedListener());
            textButton.addListener(new TextTooltip("Sets whether the button is checked initially.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Disabled", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(imageButtonDisabledListener());
            textButton.addListener(new TextTooltip("Sets whether the button is disabled initially.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Color", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(imageButtonColorListener());
            textButton.addListener(new TextTooltip("Sets the color of the table background and of the table contents.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Padding", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(imageButtonPaddingListener());
            textButton.addListener(new TextTooltip("Sets the padding of the contents of the button.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(imageButtonResetListener());
            textButton.addListener(new TextTooltip("Resets the settings of the TextButton to their defaults.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(imageButtonDeleteListener());
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimImageTextButton) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(imageTextButtonNameListener());
            textButton.addListener(new TextTooltip("Sets the name of the table to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Text", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(imageTextButtonTextListener());
            textButton.addListener(new TextTooltip("Sets the text inside of the button.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(imageTextButtonStyleListener());
            textButton.addListener(new TextTooltip("Sets the style that controls the appearance of the text button.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Checked", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(imageTextButtonCheckedListener());
            textButton.addListener(new TextTooltip("Sets whether the button is checked initially.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Disabled", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(imageTextButtonDisabledListener());
            textButton.addListener(new TextTooltip("Sets whether the button is disabled initially.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Color", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(imageTextButtonColorListener());
            textButton.addListener(new TextTooltip("Sets the color of the table background and of the table contents.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Padding", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(imageTextButtonPaddingListener());
            textButton.addListener(new TextTooltip("Sets the padding of the contents of the button.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(imageTextButtonResetListener());
            textButton.addListener(new TextTooltip("Resets the settings of the TextButton to their defaults.", main.getTooltipManager(), skin, "scene"));
        
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(imageTextButtonDeleteListener());
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimCheckBox) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(checkBoxNameListener());
            textButton.addListener(new TextTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(checkBoxStyleListener());
            textButton.addListener(new TextTooltip("Sets the style that controls the appearance of the CheckBox.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Text", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(checkBoxTextListener());
            textButton.addListener(new TextTooltip("Sets the text inside of the CheckBox.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Checked", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(checkBoxCheckedListener());
            textButton.addListener(new TextTooltip("Sets whether the CheckBox is checked initially.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Color", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(checkBoxColorListener());
            textButton.addListener(new TextTooltip("Sets the color of the CheckBox.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Padding", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(checkBoxPaddingListener());
            textButton.addListener(new TextTooltip("Sets the padding of the contents of the CheckBox.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Disabled", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(checkBoxDisabledListener());
            textButton.addListener(new TextTooltip("Sets whether the CheckBox is disabled initially.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(checkBoxResetListener());
            textButton.addListener(new TextTooltip("Resets the settings of the widget to its defaults.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(checkBoxDeleteListener());
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimImage) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(imageNameListener());
            textButton.addListener(new TextTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Drawable", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(imageDrawableListener());
            textButton.addListener(new TextTooltip("Sets the drawable to be drawn as the Image.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Scaling", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(imageScalingListener());
            textButton.addListener(new TextTooltip("Sets the scaling strategy of the Image when it's stretched or squeezed.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(imageResetListener());
            textButton.addListener(new TextTooltip("Resets the settings of the widget to its defaults.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(imageDeleteListener());
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimLabel) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(labelNameListener());
            textButton.addListener(new TextTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(labelStyleListener());
            textButton.addListener(new TextTooltip("Sets the style that controls the appearance of the Label.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Text", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(labelTextListener());
            textButton.addListener(new TextTooltip("Sets the text inside of the Label.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Color", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(labelColorListener());
            textButton.addListener(new TextTooltip("Changes the color of the text in the Label.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Text Alignment", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(labelTextAlignmentListener());
            textButton.addListener(new TextTooltip("Sets the alignment of the text when the Label is larger than it's minimum size.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Ellipsis", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(labelEllipsisListener());
            textButton.addListener(new TextTooltip("Enabling ellipsis allows the Label to be shortened and appends ellipsis characters (eg. \"...\")", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Wrap", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(labelWrapListener());
            textButton.addListener(new TextTooltip("Allows the text to be wrapped to the next line if it exceeds the width of the Label.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(labelResetListener());
            textButton.addListener(new TextTooltip("Resets the settings of the widget to its defaults.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(labelDeleteListener());
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimList) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(listNameListener());
            textButton.addListener(new TextTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(listStyleListener());
            textButton.addListener(new TextTooltip("Sets the style that controls the appearance of the List.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Text List", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(listTextListListener());
            textButton.addListener(new TextTooltip("Set the text entries for the List.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(listButtonResetListener());
            textButton.addListener(new TextTooltip("Resets the settings of the widget to its defaults.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(listButtonDeleteListener());
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimProgressBar) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(progressBarNameListener());
            textButton.addListener(new TextTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(progressBarStyleListener());
            textButton.addListener(new TextTooltip("Sets the style that controls the appearance of the ProgressBar.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Value Settings", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(progressBarValueSettingsListener());
            textButton.addListener(new TextTooltip("Set the value, minimum, maximum, and increment of the ProgressBar.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Orientation", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(progressBarOrientationListener());
            textButton.addListener(new TextTooltip("Change the orientation of the ProgressBar.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Animation", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(progressBarAnimationListener());
            textButton.addListener(new TextTooltip("Change the progress animation as it increases or decreases.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Round", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(progressBarRoundListener());
            textButton.addListener(new TextTooltip("Rounds the drawable positions to integers.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Disabled", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(progressBarDisabledListener());
            textButton.addListener(new TextTooltip("Sets whether the ProgressBar is disabled initially.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(progressBarResetListener());
            textButton.addListener(new TextTooltip("Resets the settings of the widget to its defaults.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(progressBarDeleteListener());
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimSelectBox) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(selectBoxNameListener());
            textButton.addListener(new TextTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(selectBoxStyleListener());
            textButton.addListener(new TextTooltip("Sets the style that controls the appearance of the SelectBox.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Text List", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(selectBoxTextListListener());
            textButton.addListener(new TextTooltip("Set the text entries for the SelectBox.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Max List Count", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(selectBoxMaxListCountListener());
            textButton.addListener(new TextTooltip("The maximum visible entries.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Alignment", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(selectBoxAlignmentListener());
            textButton.addListener(new TextTooltip("The alignment of the text in the SelectBox.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Scrolling", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(selectBoxScrollingListener());
            textButton.addListener(new TextTooltip("Choose if scrolling is enabled.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Disabled", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(selectBoxDisabledListener());
            textButton.addListener(new TextTooltip("Sets whether the SelectBox is disabled initially.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(selectBoxResetListener());
            textButton.addListener(new TextTooltip("Resets the settings of the widget to its defaults.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(selectBoxDeleteListener());
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimSlider) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(sliderNameListener());
            textButton.addListener(new TextTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(sliderStyleListener());
            textButton.addListener(new TextTooltip("Sets the style that controls the appearance of the Slider.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Value Settings", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(sliderValueSettingsListener());
            textButton.addListener(new TextTooltip("Set the value, minimum, maximum, and increment of the Slider.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Orientation", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(sliderOrientationListener());
            textButton.addListener(new TextTooltip("Change the orientation of the Slider.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Animation", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(sliderAnimationListener());
            textButton.addListener(new TextTooltip("Change the progress animation as it increases or decreases.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Round", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(sliderRoundListener());
            textButton.addListener(new TextTooltip("Rounds the drawable positions to integers.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Disabled", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(sliderDisabledListener());
            textButton.addListener(new TextTooltip("Sets whether the button is disabled initially.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(sliderResetListener());
            textButton.addListener(new TextTooltip("Resets the settings of the widget to its defaults.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(sliderDeleteListener());
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimTextArea) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(textAreaNameListener());
            textButton.addListener(new TextTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(textAreaStyleListener());
            textButton.addListener(new TextTooltip("Sets the style that controls the appearance of the TextArea.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Text", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(textAreaTextListener());
            textButton.addListener(new TextTooltip("Sets the text inside of the TextArea.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Message Text", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(textAreaMessageTextListener());
            textButton.addListener(new TextTooltip("The text to be shown while there is no text, and the TextArea is not focused.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Password", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(textAreaPasswordListener());
            textButton.addListener(new TextTooltip("Enable password mode and set the password character.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Selection", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(textAreaSelectionListener());
            textButton.addListener(new TextTooltip("Set the cursor position and selected range.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Alignment", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(textAreaAlignmentListener());
            textButton.addListener(new TextTooltip("Set the alignment of the typed text.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Focus Traversal", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(textAreaFocusTraversalListener());
            textButton.addListener(new TextTooltip("Enable traversal to the next TextArea by using the TAB key.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Max Length", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(textAreaMaxLengthListener());
            textButton.addListener(new TextTooltip("Sets the maximum length of the typed text.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Preferred Rows", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(textAreaPreferredRowsListener());
            textButton.addListener(new TextTooltip("Set the preferred number of lines.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Disabled", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(textAreaDisabledListener());
            textButton.addListener(new TextTooltip("Sets whether the TextArea is disabled initially.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(textAreaResetListener());
            textButton.addListener(new TextTooltip("Resets the settings of the widget to its defaults.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(textAreaDeleteListener());
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimTextField) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(textFieldNameListener());
            textButton.addListener(new TextTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(textFieldStyleListener());
            textButton.addListener(new TextTooltip("Sets the style that controls the appearance of the TextField.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Text", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(textFieldTextListener());
            textButton.addListener(new TextTooltip("Sets the text inside of the TextField.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Message Text", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(textFieldMessageTextListener());
            textButton.addListener(new TextTooltip("The text to be shown while there is no text, and the TextField is not focused.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Password", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(textFieldPasswordListener());
            textButton.addListener(new TextTooltip("Enable password mode and set the password character.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Selection", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(textFieldSelectionListener());
            textButton.addListener(new TextTooltip("Set the cursor position and selected range.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Alignment", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(textFieldAlignmentListener());
            textButton.addListener(new TextTooltip("Set the alignment of the typed text.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Focus Traversal", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(textFieldFocusTraversalListener());
            textButton.addListener(new TextTooltip("Enable traversal to the next TextField by using the TAB key.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Max Length", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(textFieldMaxLengthListener());
            textButton.addListener(new TextTooltip("Sets the maximum length of the typed text.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Disabled", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(textFieldDisabledListener());
            textButton.addListener(new TextTooltip("Sets whether the TextField is disabled initially.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(textFieldResetListener());
            textButton.addListener(new TextTooltip("Resets the settings of the widget to its defaults.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(textFieldDeleteListener());
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimTouchPad) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(touchPadNameListener());
            textButton.addListener(new TextTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(touchPadStyleListener());
            textButton.addListener(new TextTooltip("Sets the style that controls the appearance of the TouchPad.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Dead Zone", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(touchPadDeadZoneListener());
            textButton.addListener(new TextTooltip("Change the dead zone that does not react to user input.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Reset on Touch Up", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(touchPadResetOnTouchUpListener());
            textButton.addListener(new TextTooltip("Enable the reset of the touch pad position upon the release of the widget.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(touchPadResetListener());
            textButton.addListener(new TextTooltip("Resets the settings of the widget to its defaults.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(touchPadDeleteListener());
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimContainer) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(containerNameListener());
            textButton.addListener(new TextTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Set Widget", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(setWidgetListener(this::showConfirmContainerSetWidgetDialog));
            textButton.addListener(new TextTooltip("Set the widget assigned to this Container.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Background", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(containerBackgroundListener());
            textButton.addListener(new TextTooltip("Set the background of the Container.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Fill", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(containerFillListener());
            textButton.addListener(new TextTooltip("Set the fill of the widget.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Size", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(containerSizeListener());
            textButton.addListener(new TextTooltip("Set the size of the widget.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Padding", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(containerPaddingListener());
            textButton.addListener(new TextTooltip("Set the padding of the widget.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Alignment", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(containerAlignmentListener());
            textButton.addListener(new TextTooltip("Set the alignment of the widget.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(containerResetListener());
            textButton.addListener(new TextTooltip("Resets the settings of the widget to its defaults.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(containerDeleteListener());
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimHorizontalGroup) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(horizontalGroupNameListener());
            textButton.addListener(new TextTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Add Child", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(setWidgetListener(this::horizontalGroupAddChild));
            textButton.addListener(new TextTooltip("Adds a widget to the HorizontalGroup", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Expand", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(horizontalGroupExpandFillGrowListener());
            textButton.addListener(new TextTooltip("Set the widgets to expand to the available space", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Padding", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(horizontalGroupPaddingSpacingListener());
            textButton.addListener(new TextTooltip("Set the padding of the widgets.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Wrap", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(horizontalGroupWrapListener());
            textButton.addListener(new TextTooltip("Set whether widgets will wrap to the next line when the width is decreased.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Alignment", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(horizontalGroupAlignmentListener());
            textButton.addListener(new TextTooltip("Set the alignment of the widgets", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Row Alignment", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(horizontalGroupRowAlignmentListener());
            textButton.addListener(new TextTooltip("Set the alignment of the widgets", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Reverse", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(horizontalGroupReverseListener());
            textButton.addListener(new TextTooltip("Reverse the display order of the widgets.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(horizontalGroupResetListener());
            textButton.addListener(new TextTooltip("Resets the settings of the widget to its defaults.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(horizontalGroupDeleteListener());
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimScrollPane) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(scrollPaneNameListener());
            textButton.addListener(new TextTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(scrollPaneStyleListener());
            textButton.addListener(new TextTooltip("Sets the style that controls the appearance of the ScrollPane.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Set Widget", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(setWidgetListener(this::showConfirmScrollPaneSetWidgetDialog));
            textButton.addListener(new TextTooltip("Set the widget assigned to this ScrollPane.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Knobs", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(scrollPaneKnobsListener());
            textButton.addListener(new TextTooltip("Knob and Scroll Bar settings.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Scrolling", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(scrollPaneScrollListener());
            textButton.addListener(new TextTooltip("Scroll settings.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(scrollPaneResetListener());
            textButton.addListener(new TextTooltip("Resets the settings of the widget to its defaults.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(scrollPaneDeleteListener());
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimSplitPane) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(splitPaneNameListener());
            textButton.addListener(new TextTooltip("Set the name of the widget to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(splitPaneStyleListener());
            textButton.addListener(new TextTooltip("Set the style that controls the appearance of the SplitPane.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Set First Widget", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(setWidgetListener((widgetType, popTable) -> {
                showConfirmSplitPaneSetWidgetDialog(widgetType, popTable, true);
            }));
            textButton.addListener(new TextTooltip("Set the first widget applied to the SplitPane", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Set Second Widget", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(setWidgetListener((widgetType, popTable) -> {
                showConfirmSplitPaneSetWidgetDialog(widgetType, popTable, false);
            }));
            textButton.addListener(new TextTooltip("Set the second widget applied to the SplitPane", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Orientation", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(splitPaneOrientationListener());
            textButton.addListener(new TextTooltip("Set the orientation of the SplitPane.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Split", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(splitPaneSplitListener());
            textButton.addListener(new TextTooltip("Set the split, splitMin, and splitMax values.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(splitPaneResetListener());
            textButton.addListener(new TextTooltip("Resets the settings of the widget to its defaults.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(splitPaneDeleteListener());
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimStack) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(stackNameListener());
            textButton.addListener(new TextTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Add Child", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(setWidgetListener(this::stackAddChild));
            textButton.addListener(new TextTooltip("Add a child to the Stack.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(stackResetListener());
            textButton.addListener(new TextTooltip("Resets the settings of the widget to its defaults.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(stackDeleteListener());
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimNode) {
            var textButton = new TextButton("Set Widget", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(new TextTooltip("Set the widget applied to this Node.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Add Node", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(new TextTooltip("Adds a new child Node to this Node.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Icon", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(new TextTooltip("Select the Drawable applied as an icon to the Node.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Options", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(new TextTooltip("Change the expanded and selected values.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(new TextTooltip("Resets the settings of the widget to its defaults.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimTree) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(new TextTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Style", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(new TextTooltip("Sets the style that controls the appearance of the Tree.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Add Node", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(new TextTooltip("", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Padding", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(new TextTooltip("", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Spacing", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(new TextTooltip("", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(new TextTooltip("Resets the settings of the widget to its defaults.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        } else if (simActor instanceof DialogSceneComposerModel.SimVerticalGroup) {
            var textButton = new TextButton("Name", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(verticalGroupNameListener());
            textButton.addListener(new TextTooltip("Sets the name of the widget to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Add Child", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(setWidgetListener(this::verticalGroupAddChild));
            textButton.addListener(new TextTooltip("Adds a widget to the VerticalGroup.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Expand", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(verticalGroupExpandFillGrowListener());
            textButton.addListener(new TextTooltip("Set the widgets to expand to the available space.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Padding", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(verticalGroupPaddingSpacingListener());
            textButton.addListener(new TextTooltip("Set the padding of the widgets.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Wrap", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(verticalGroupWrapListener());
            textButton.addListener(new TextTooltip("Set whether widgets will wrap to the next line when the height is decreased.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Alignment", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(verticalGroupAlignmentListener());
            textButton.addListener(new TextTooltip("Set the alignment of the widgets", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Column Alignment", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(verticalGroupColumnAlignmentListener());
            textButton.addListener(new TextTooltip("Set the alignment of the widgets", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Reverse", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(verticalGroupReverseListener());
            textButton.addListener(new TextTooltip("Reverse the display order of the widgets.", main.getTooltipManager(), skin, "scene"));
            
            textButton = new TextButton("Reset", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(verticalGroupResetListener());
            textButton.addListener(new TextTooltip("Resets the settings of the widget to its defaults.", main.getTooltipManager(), skin, "scene"));
    
            textButton = new TextButton("Delete", skin, "scene-med");
            horizontalGroup.addActor(textButton);
            textButton.addListener(main.getHandListener());
            textButton.addListener(verticalGroupDeleteListener());
            textButton.addListener(new TextTooltip("Removes this widget from its parent.", main.getTooltipManager(), skin, "scene"));
        }
    }
    
    private EventListener buttonNameListener() {
        var simButton = (DialogSceneComposerModel.SimButton) simActor;
        var textField = new TextField("", skin, "scene");
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
                
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Name:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setText(simButton.name);
                popTable.add(textField).minWidth(150);
                textField.addListener(main.getIbeamListener());
                textField.addListener(new TextTooltip("The name of the button to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.buttonName(textField.getText());
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
                
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener buttonStyleListener() {
        var simButton = (DialogSceneComposerModel.SimButton) simActor;
        var popTableClickListener = new StyleSelectorPopTable(TextButton.class, simButton.style == null ? "default" : simButton.style.name) {
            @Override
            public void accepted(StyleData styleData) {
                events.buttonStyle(styleData);
            }
        };
        
        return popTableClickListener;
    }
    
    private EventListener buttonCheckedListener() {
        var simButton = (DialogSceneComposerModel.SimButton) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Checked:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var textButton = new TextButton(simButton.checked ? "TRUE" : "FALSE", skin, "scene-small");
                textButton.setChecked(simButton.checked);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(main.getHandListener());
                textButton.addListener(new TextTooltip("Whether the button is checked initially.", main.getTooltipManager(), skin, "scene"));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        events.buttonChecked(textButton.isChecked());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener buttonDisabledListener() {
        var simButton = (DialogSceneComposerModel.SimButton) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Disabled:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var textButton = new TextButton(simButton.disabled ? "TRUE" : "FALSE", skin, "scene-small");
                textButton.setChecked(simButton.disabled);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(main.getHandListener());
                textButton.addListener(new TextTooltip("Whether the button is disabled initially.", main.getTooltipManager(), skin, "scene"));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        events.buttonDisabled(textButton.isChecked());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener buttonColorListener() {
        var simButton = (DialogSceneComposerModel.SimButton) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                imageButton.getImage().setColor(simButton.color == null ? Color.WHITE : simButton.color.color);
                popTable.add(imageButton).minWidth(100);
                imageButton.addListener(main.getHandListener());
                imageButton.addListener(new TextTooltip("Select the color of the button.", main.getTooltipManager(), skin, "scene"));
                imageButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        popTable.hide();
                        main.getDialogFactory().showDialogColors(new StyleProperty(), (colorData, pressedCancel) -> {
                            if (!pressedCancel) {
                                events.buttonColor(colorData);
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
                label = new Label(simButton.color == null ? "white" : simButton.color.getName(), skin, "scene-label-colored");
                popTable.add(label);
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener buttonPaddingListener() {
        var simButton = (DialogSceneComposerModel.SimButton) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                        Spinner padLeft = popTable.findActor("pad-left");
                        Spinner padRight = popTable.findActor("pad-right");
                        Spinner padTop = popTable.findActor("pad-top");
                        Spinner padBottom = popTable.findActor("pad-bottom");
                        events.buttonPadding((float) padLeft.getValue(), (float) padRight.getValue(), (float) padTop.getValue(), (float) padBottom.getValue());
                    }
                };
                
                var label = new Label("Padding:", skin, "scene-label-colored");
                popTable.add(label).colspan(2);
                
                popTable.row();
                popTable.defaults().right().spaceRight(5);
                label = new Label("Left:", skin, "scene-label-colored");
                popTable.add(label);
                
                var spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("pad-left");
                spinner.setValue(simButton.padLeft);
                popTable.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding on the left of the contents.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
                
                popTable.row();
                label = new Label("Right:", skin, "scene-label-colored");
                popTable.add(label);
                
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("pad-right");
                spinner.setValue(simButton.padRight);
                popTable.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding on the right of the contents.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
                
                popTable.row();
                label = new Label("Top:", skin, "scene-label-colored");
                popTable.add(label);
                
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("pad-top");
                spinner.setValue(simButton.padTop);
                popTable.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding on the top of the contents.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
                
                popTable.row();
                label = new Label("Bottom:", skin, "scene-label-colored");
                popTable.add(label);
                
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("pad-bottom");
                spinner.setValue(simButton.padBottom);
                popTable.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding on the bottom of the contents.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener buttonResetListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to reset this Button?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("RESET", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Resets the settings of the Button to their defaults.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.buttonReset();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener buttonDeleteListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to delete this Button?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("DELETE", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Removes this Button from its parent.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.buttonDelete();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener imageButtonNameListener() {
        var simImageButton = (DialogSceneComposerModel.SimImageButton) simActor;
        var textField = new TextField("", skin, "scene");
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
                
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Name:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setText(simImageButton.name);
                popTable.add(textField).minWidth(150);
                textField.addListener(main.getIbeamListener());
                textField.addListener(new TextTooltip("The name of the button to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.imageButtonName(textField.getText());
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
                
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener imageButtonStyleListener() {
        var simImageButton = (DialogSceneComposerModel.SimImageButton) simActor;
        var popTableClickListener = new StyleSelectorPopTable(TextButton.class, simImageButton.style == null ? "default" : simImageButton.style.name) {
            @Override
            public void accepted(StyleData styleData) {
                events.imageButtonStyle(styleData);
            }
        };
        
        return popTableClickListener;
    }
    
    private EventListener imageButtonCheckedListener() {
        var simImageButton = (DialogSceneComposerModel.SimImageButton) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Checked:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var textButton = new TextButton(simImageButton.checked ? "TRUE" : "FALSE", skin, "scene-small");
                textButton.setChecked(simImageButton.checked);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(main.getHandListener());
                textButton.addListener(new TextTooltip("Whether the button is checked initially.", main.getTooltipManager(), skin, "scene"));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        events.imageButtonChecked(textButton.isChecked());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener imageButtonDisabledListener() {
        var simImageButton = (DialogSceneComposerModel.SimImageButton) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Disabled:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var textButton = new TextButton(simImageButton.disabled ? "TRUE" : "FALSE", skin, "scene-small");
                textButton.setChecked(simImageButton.disabled);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(main.getHandListener());
                textButton.addListener(new TextTooltip("Whether the button is disabled initially.", main.getTooltipManager(), skin, "scene"));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        events.imageButtonDisabled(textButton.isChecked());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener imageButtonColorListener() {
        var simImageButton = (DialogSceneComposerModel.SimImageButton) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                imageButton.getImage().setColor(simImageButton.color == null ? Color.WHITE : simImageButton.color.color);
                popTable.add(imageButton).minWidth(100);
                imageButton.addListener(main.getHandListener());
                imageButton.addListener(new TextTooltip("Select the color of the button.", main.getTooltipManager(), skin, "scene"));
                imageButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        popTable.hide();
                        main.getDialogFactory().showDialogColors(new StyleProperty(), (colorData, pressedCancel) -> {
                            if (!pressedCancel) {
                                events.imageButtonColor(colorData);
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
                label = new Label(simImageButton.color == null ? "white" : simImageButton.color.getName(), skin, "scene-label-colored");
                popTable.add(label);
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener imageButtonPaddingListener() {
        var simImageButton = (DialogSceneComposerModel.SimImageButton) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                        Spinner padLeft = popTable.findActor("pad-left");
                        Spinner padRight = popTable.findActor("pad-right");
                        Spinner padTop = popTable.findActor("pad-top");
                        Spinner padBottom = popTable.findActor("pad-bottom");
                        events.imageButtonPadding((float) padLeft.getValue(), (float) padRight.getValue(), (float) padTop.getValue(), (float) padBottom.getValue());
                    }
                };
                
                var label = new Label("Padding:", skin, "scene-label-colored");
                popTable.add(label).colspan(2);
                
                popTable.row();
                popTable.defaults().right().spaceRight(5);
                label = new Label("Left:", skin, "scene-label-colored");
                popTable.add(label);
                
                var spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("pad-left");
                spinner.setValue(simImageButton.padLeft);
                popTable.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding on the left of the contents.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
                
                popTable.row();
                label = new Label("Right:", skin, "scene-label-colored");
                popTable.add(label);
                
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("pad-right");
                spinner.setValue(simImageButton.padRight);
                popTable.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding on the right of the contents.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
                
                popTable.row();
                label = new Label("Top:", skin, "scene-label-colored");
                popTable.add(label);
                
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("pad-top");
                spinner.setValue(simImageButton.padTop);
                popTable.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding on the top of the contents.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
                
                popTable.row();
                label = new Label("Bottom:", skin, "scene-label-colored");
                popTable.add(label);
                
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("pad-bottom");
                spinner.setValue(simImageButton.padBottom);
                popTable.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding on the bottom of the contents.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener imageButtonResetListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to reset this button?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("RESET", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Resets the settings of the TextButton to their defaults.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.imageButtonReset();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener imageButtonDeleteListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to delete this ImageButton?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("DELETE", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Removes this TextButton from its parent.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.imageButtonDelete();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener imageTextButtonNameListener() {
        var simImageTextButton = (DialogSceneComposerModel.SimImageTextButton) simActor;
        var textField = new TextField("", skin, "scene");
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
                
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Name:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setText(simImageTextButton.name);
                popTable.add(textField).minWidth(150);
                textField.addListener(main.getIbeamListener());
                textField.addListener(new TextTooltip("The name of the button to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.imageTextButtonName(textField.getText());
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
                
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener imageTextButtonTextListener() {
        var simImageTextButton = (DialogSceneComposerModel.SimImageTextButton) simActor;
        var textField = new TextField("", skin, "scene");
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Text:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setText(simImageTextButton.text);
                popTable.add(textField).minWidth(150);
                textField.addListener(main.getIbeamListener());
                textField.addListener(new TextTooltip("The text inside of the button.", main.getTooltipManager(), skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.imageTextButtonText(textField.getText());
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
                
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener imageTextButtonStyleListener() {
        var imageTextButton = (DialogSceneComposerModel.SimImageTextButton) simActor;
        var popTableClickListener = new StyleSelectorPopTable(TextButton.class, imageTextButton.style == null ? "default" : imageTextButton.style.name) {
            @Override
            public void accepted(StyleData styleData) {
                events.imageTextButtonStyle(styleData);
            }
        };
        
        return popTableClickListener;
    }
    
    private EventListener imageTextButtonCheckedListener() {
        var simImageTextButton = (DialogSceneComposerModel.SimImageTextButton) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Checked:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var textButton = new TextButton(simImageTextButton.checked ? "TRUE" : "FALSE", skin, "scene-small");
                textButton.setChecked(simImageTextButton.checked);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(main.getHandListener());
                textButton.addListener(new TextTooltip("Whether the button is checked initially.", main.getTooltipManager(), skin, "scene"));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        events.imageTextButtonChecked(textButton.isChecked());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener imageTextButtonDisabledListener() {
        var simImageTextButton = (DialogSceneComposerModel.SimImageTextButton) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Disabled:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var textButton = new TextButton(simImageTextButton.disabled ? "TRUE" : "FALSE", skin, "scene-small");
                textButton.setChecked(simImageTextButton.disabled);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(main.getHandListener());
                textButton.addListener(new TextTooltip("Whether the button is disabled initially.", main.getTooltipManager(), skin, "scene"));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        events.imageTextButtonDisabled(textButton.isChecked());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener imageTextButtonColorListener() {
        var simImageTextButton = (DialogSceneComposerModel.SimImageTextButton) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                imageButton.getImage().setColor(simImageTextButton.color == null ? Color.WHITE : simImageTextButton.color.color);
                popTable.add(imageButton).minWidth(100);
                imageButton.addListener(main.getHandListener());
                imageButton.addListener(new TextTooltip("Select the color of the button.", main.getTooltipManager(), skin, "scene"));
                imageButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        popTable.hide();
                        main.getDialogFactory().showDialogColors(new StyleProperty(), (colorData, pressedCancel) -> {
                            if (!pressedCancel) {
                                events.imageTextButtonColor(colorData);
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
                label = new Label(simImageTextButton.color == null ? "white" : simImageTextButton.color.getName(), skin, "scene-label-colored");
                popTable.add(label);
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener imageTextButtonPaddingListener() {
        var simImageTextButton = (DialogSceneComposerModel.SimImageTextButton) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                        Spinner padLeft = popTable.findActor("pad-left");
                        Spinner padRight = popTable.findActor("pad-right");
                        Spinner padTop = popTable.findActor("pad-top");
                        Spinner padBottom = popTable.findActor("pad-bottom");
                        events.imageTextButtonPadding((float) padLeft.getValue(), (float) padRight.getValue(), (float) padTop.getValue(), (float) padBottom.getValue());
                    }
                };
                
                var label = new Label("Padding:", skin, "scene-label-colored");
                popTable.add(label).colspan(2);
                
                popTable.row();
                popTable.defaults().right().spaceRight(5);
                label = new Label("Left:", skin, "scene-label-colored");
                popTable.add(label);
                
                var spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("pad-left");
                spinner.setValue(simImageTextButton.padLeft);
                popTable.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding on the left of the contents.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
                
                popTable.row();
                label = new Label("Right:", skin, "scene-label-colored");
                popTable.add(label);
                
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("pad-right");
                spinner.setValue(simImageTextButton.padRight);
                popTable.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding on the right of the contents.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
                
                popTable.row();
                label = new Label("Top:", skin, "scene-label-colored");
                popTable.add(label);
                
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("pad-top");
                spinner.setValue(simImageTextButton.padTop);
                popTable.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding on the top of the contents.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
                
                popTable.row();
                label = new Label("Bottom:", skin, "scene-label-colored");
                popTable.add(label);
                
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("pad-bottom");
                spinner.setValue(simImageTextButton.padBottom);
                popTable.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding on the bottom of the contents.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener imageTextButtonResetListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to reset this cell?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("RESET", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Resets the settings of the ImageTextButton to their defaults.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.imageTextButtonReset();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener imageTextButtonDeleteListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to delete this textButton?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("DELETE", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Removes this ImageTextButton from its parent.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.imageTextButtonDelete();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener textButtonNameListener() {
        var simTextButton = (DialogSceneComposerModel.SimTextButton) simActor;
        var textField = new TextField("", skin, "scene");
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
                
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Name:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setText(simTextButton.name);
                popTable.add(textField).minWidth(150);
                textField.addListener(main.getIbeamListener());
                textField.addListener(new TextTooltip("The name of the button to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textButtonName(textField.getText());
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
                
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener textButtonTextListener() {
        var simTextButton = (DialogSceneComposerModel.SimTextButton) simActor;
        var textField = new TextField("", skin, "scene");
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Text:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setText(simTextButton.text);
                popTable.add(textField).minWidth(150);
                textField.addListener(main.getIbeamListener());
                textField.addListener(new TextTooltip("The text inside of the button.", main.getTooltipManager(), skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textButtonText(textField.getText());
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
                
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener textButtonStyleListener() {
        var simTextButton = (DialogSceneComposerModel.SimTextButton) simActor;
        var popTableClickListener = new StyleSelectorPopTable(TextButton.class, simTextButton.style == null ? "default" : simTextButton.style.name) {
            @Override
            public void accepted(StyleData styleData) {
                events.textButtonStyle(styleData);
            }
        };
        
        return popTableClickListener;
    }
    
    private EventListener textButtonCheckedListener() {
        var simTextButton = (DialogSceneComposerModel.SimTextButton) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Checked:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var textButton = new TextButton(simTextButton.checked ? "TRUE" : "FALSE", skin, "scene-small");
                textButton.setChecked(simTextButton.checked);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(main.getHandListener());
                textButton.addListener(new TextTooltip("Whether the button is checked initially.", main.getTooltipManager(), skin, "scene"));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        events.textButtonChecked(textButton.isChecked());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener textButtonDisabledListener() {
        var simTextButton = (DialogSceneComposerModel.SimTextButton) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Disabled:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var textButton = new TextButton(simTextButton.disabled ? "TRUE" : "FALSE", skin, "scene-small");
                textButton.setChecked(simTextButton.disabled);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(main.getHandListener());
                textButton.addListener(new TextTooltip("Whether the button is disabled initially.", main.getTooltipManager(), skin, "scene"));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        events.textButtonDisabled(textButton.isChecked());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener textButtonColorListener() {
        var simTextButton = (DialogSceneComposerModel.SimTextButton) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                imageButton.getImage().setColor(simTextButton.color == null ? Color.WHITE : simTextButton.color.color);
                popTable.add(imageButton).minWidth(100);
                imageButton.addListener(main.getHandListener());
                imageButton.addListener(new TextTooltip("Select the color of the button.", main.getTooltipManager(), skin, "scene"));
                imageButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        popTable.hide();
                        main.getDialogFactory().showDialogColors(new StyleProperty(), (colorData, pressedCancel) -> {
                            if (!pressedCancel) {
                                events.textButtonColor(colorData);
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
                label = new Label(simTextButton.color == null ? "white" : simTextButton.color.getName(), skin, "scene-label-colored");
                popTable.add(label);
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener textButtonPaddingListener() {
        var simTextButton = (DialogSceneComposerModel.SimTextButton) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                        Spinner padLeft = popTable.findActor("pad-left");
                        Spinner padRight = popTable.findActor("pad-right");
                        Spinner padTop = popTable.findActor("pad-top");
                        Spinner padBottom = popTable.findActor("pad-bottom");
                        events.textButtonPadding((float) padLeft.getValue(), (float) padRight.getValue(), (float) padTop.getValue(), (float) padBottom.getValue());
                    }
                };
                
                var label = new Label("Padding:", skin, "scene-label-colored");
                popTable.add(label).colspan(2);
                
                popTable.row();
                popTable.defaults().right().spaceRight(5);
                label = new Label("Left:", skin, "scene-label-colored");
                popTable.add(label);
                
                var spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("pad-left");
                spinner.setValue(simTextButton.padLeft);
                popTable.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding on the left of the contents.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
                
                popTable.row();
                label = new Label("Right:", skin, "scene-label-colored");
                popTable.add(label);
                
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("pad-right");
                spinner.setValue(simTextButton.padRight);
                popTable.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding on the right of the contents.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
                
                popTable.row();
                label = new Label("Top:", skin, "scene-label-colored");
                popTable.add(label);
                
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("pad-top");
                spinner.setValue(simTextButton.padTop);
                popTable.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding on the top of the contents.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
                
                popTable.row();
                label = new Label("Bottom:", skin, "scene-label-colored");
                popTable.add(label);
                
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("pad-bottom");
                spinner.setValue(simTextButton.padBottom);
                popTable.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding on the bottom of the contents.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener textButtonResetListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to reset this TextButton?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("RESET", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Resets the settings of the TextButton to their defaults.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.textButtonReset();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener textButtonDeleteListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to delete this textButton?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("DELETE", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Removes this TextButton from its parent.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.textButtonDelete();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener tableNameListener() {
        var simTable = (DialogSceneComposerModel.SimTable) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                popTable.add(textField).minWidth(150);
                textField.addListener(main.getIbeamListener());
                textField.addListener(new TextTooltip("The name of the table to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
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
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.tableName(textField.getText());
                    }
                });
    
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener tableBackgroundListener() {
        var simTable = (DialogSceneComposerModel.SimTable) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
    
                var label = new Label("Background:", skin, "scene-label-colored");
                popTable.add(label);
    
                popTable.row();
                var stack = new Stack();
                popTable.add(stack).minSize(100).maxSize(300).grow();
                var background = new Image(skin, "scene-tile-ten");
                stack.add(background);
                Image image;
                if (simTable.background != null) {
                    image = new Image(main.getAtlasData().drawablePairs.get(simTable.background));
                } else {
                    image = new Image((Drawable) null);
                }
                stack.add(image);
    
                popTable.row();
                var textButton = new TextButton("Select Drawable", skin, "scene-small");
                popTable.add(textButton).minWidth(100);
                textButton.addListener(main.getHandListener());
                textButton.addListener(new TextTooltip("The background drawable for the table.", main.getTooltipManager(), skin, "scene"));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        popTable.hide();
                        main.getDialogFactory().showDialogDrawables(true, new DialogDrawables.DialogDrawablesListener() {
                            @Override
                            public void confirmed(DrawableData drawable, DialogDrawables dialog) {
                                events.tableBackground(drawable);
                                image.setDrawable(main.getAtlasData().drawablePairs.get(drawable));
                            }
                
                            @Override
                            public void emptied(DialogDrawables dialog) {
                                events.tableBackground(null);
                                image.setDrawable(null);
                            }
                
                            @Override
                            public void cancelled(DialogDrawables dialog) {
                    
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
    
    private EventListener tableColorListener() {
        var simTable = (DialogSceneComposerModel.SimTable) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                imageButton.addListener(main.getHandListener());
                imageButton.addListener(new TextTooltip("Select the color of the table background and of the table contents.", main.getTooltipManager(), skin, "scene"));
    
                popTable.row();
                var colorLabel = new Label(simTable.color == null ? "No Color" : simTable.color.getName(), skin, "scene-label-colored");
                popTable.add(colorLabel);
    
                imageButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        popTable.hide();
                        main.getDialogFactory().showDialogColors(new StyleProperty(), (colorData, pressedCancel) -> {
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
    
    private EventListener tablePaddingListener() {
        var simTable = (DialogSceneComposerModel.SimTable) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                        Spinner padLeft = popTable.findActor("pad-left");
                        Spinner padRight = popTable.findActor("pad-right");
                        Spinner padTop = popTable.findActor("pad-top");
                        Spinner padBottom = popTable.findActor("pad-bottom");
                        events.tablePadding((float) padLeft.getValue(), (float)  padRight.getValue(), (float)  padTop.getValue(), (float)  padBottom.getValue());
                    }
                };
    
                var label = new Label("Padding:", skin, "scene-label-colored");
                popTable.add(label).colspan(2);
    
                popTable.row();
                popTable.defaults().right().spaceRight(5);
                label = new Label("Left:", skin, "scene-label-colored");
                popTable.add(label);
    
                var spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("pad-left");
                spinner.setValue(simTable.padLeft);
                popTable.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding on the left of the contents.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
    
                popTable.row();
                label = new Label("Right:", skin, "scene-label-colored");
                popTable.add(label);
    
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("pad-right");
                spinner.setValue(simTable.padRight);
                popTable.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding on the right of the contents.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
    
                popTable.row();
                label = new Label("Top:", skin, "scene-label-colored");
                popTable.add(label);
    
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("pad-top");
                spinner.setValue(simTable.padTop);
                popTable.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding on the top of the contents.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
    
                popTable.row();
                label = new Label("Bottom:", skin, "scene-label-colored");
                popTable.add(label);
    
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("pad-bottom");
                spinner.setValue(simTable.padBottom);
                popTable.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding on the bottom of the contents.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener tableAlignListener() {
        var simTable = (DialogSceneComposerModel.SimTable) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents to the top left.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents to the top center.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents to the top right.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents to the middle left.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents to the center.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents to the middle right.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents to the bottom left.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents to the bottom center.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents to the bottom right.", main.getTooltipManager(), skin, "scene"));
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
    
    private EventListener tableSetCellsListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin, "dark");
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Erase contents\nand create\nnew cells:", skin, "scene-label");
        label.setAlignment(Align.center);
        popTable.add(label);
        label.addListener(new TextTooltip("Sets the cells for this table. This will erase the existing contents.", main.getTooltipManager(), skin, "scene"));
        
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
    
    private EventListener tableResetListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to reset this table?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("RESET", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Resets all options back to their defaults.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.tableReset();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener tableDeleteListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to delete this table?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("DELETE", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Removes this table from its parent.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.tableDelete();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener rootAddTableListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin, "dark");
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
    
    private EventListener cellDeleteListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var table = popTableClickListener.getPopTable();
    
        var label = new Label("Are you sure you want to delete this cell?", skin, "scene-label-colored");
        table.add(label);
    
        table.row();
        var textButton = new TextButton("DELETE", skin, "scene-small");
        table.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Deletes the cell and its contents.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                table.hide();
                events.cellDelete();
            }
        });
    
        return popTableClickListener;
    }
    
    private EventListener cellResetListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var table = popTableClickListener.getPopTable();
    
        var label = new Label("Are you sure you want to reset this cell?", skin, "scene-label-colored");
        table.add(label);
    
        table.row();
        var textButton = new TextButton("RESET", skin, "scene-small");
        table.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Resets all of the settings of the cell to their defaults.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                table.hide();
                events.cellReset();
            }
        });
    
        return popTableClickListener;
    }
    
    private EventListener cellUniformListener() {
        var simCell = (DialogSceneComposerModel.SimCell) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("All cells with Uniform X will share the same width.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(changeListener);
    
                imageTextButton = new ImageTextButton("Uniform Y", skin, "scene-checkbox-colored");
                imageTextButton.setName("uniform-y");
                imageTextButton.setChecked(simCell.uniformY);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("All cells with Uniform Y will share the same height.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(changeListener);
            }
        };
        
        popTableClickListener.update();
    
        return popTableClickListener;
    }
    
    private EventListener cellSizeListener() {
        var simCell = (DialogSceneComposerModel.SimCell) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
    
                var changeListener = new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        Spinner minimumWidth = popTable.findActor("minimum-width");
                        Spinner minimumHeight = popTable.findActor("minimum-height");
                        Spinner maximumWidth = popTable.findActor("maximum-width");
                        Spinner maximumHeight = popTable.findActor("maximum-height");
                        Spinner preferredWidth = popTable.findActor("preferred-width");
                        Spinner preferredHeight = popTable.findActor("preferred-height");
                        events.cellSize((float) minimumWidth.getValue(), (float) minimumHeight.getValue(), (float) maximumWidth.getValue(), (float) maximumHeight.getValue(), (float) preferredWidth.getValue(), (float) preferredHeight.getValue());
                    }
                };
    
                var label = new Label("Minimum:", skin, "scene-label-colored");
                table.add(label).colspan(2);
    
                table.row();
                table.defaults().right().spaceRight(5);
                label = new Label("Width:", skin, "scene-label-colored");
                table.add(label);
    
                var spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("minimum-width");
                spinner.setValue(simCell.minWidth);
                table.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The minimum width of the contents of the cell.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
    
                table.row();
                label = new Label("Height:", skin, "scene-label-colored");
                table.add(label);
    
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("minimum-height");
                spinner.setValue(simCell.minHeight);
                table.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The minimum height of the contents of the cell.", main.getTooltipManager(), skin, "scene"));
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
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The maximum width of the contents of the cell.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
    
                table.row();
                label = new Label("Height:", skin, "scene-label-colored");
                table.add(label);
    
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("maximum-height");
                spinner.setValue(simCell.maxHeight);
                table.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The maximum height of the contents of the cell.", main.getTooltipManager(), skin, "scene"));
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
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The preferred width of the contents of the cell.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
    
                table.row();
                label = new Label("Height:", skin, "scene-label-colored");
                table.add(label);
    
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("preferred-height");
                spinner.setValue(simCell.preferredHeight);
                table.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The preferred height of the contents of the cell.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
            }
        };
        
        popTableClickListener.update();
    
        return popTableClickListener;
    }
    
    private EventListener cellAlignmentListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the cell to the top left.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the cell to the top center.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the cell to the top right.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the cell to the middle left.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the cell to the center.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the cell to the middle right.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the cell to the bottom left.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the cell to the bottom center.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the cell to the bottom right.", main.getTooltipManager(), skin, "scene"));
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
    
    private EventListener cellExpandFillGrowListener() {
        var simCell = (DialogSceneComposerModel.SimCell) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                expandX.addListener(main.getHandListener());
                expandX.addListener(new TextTooltip("Expands the width of the cell to the available space.", main.getTooltipManager(), skin, "scene"));
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
                expandY.addListener(main.getHandListener());
                expandY.addListener(new TextTooltip("Expands the height of the cell to the available space.", main.getTooltipManager(), skin, "scene"));
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
                fillX.addListener(main.getHandListener());
                fillX.addListener(new TextTooltip("Stretches the contents to fill the width of the cell.", main.getTooltipManager(), skin, "scene"));
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
                fillY.addListener(main.getHandListener());
                fillY.addListener(new TextTooltip("Stretches the contents to fill the height of the cell.", main.getTooltipManager(), skin, "scene"));
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
                growX.addListener(main.getHandListener());
                growX.addListener(new TextTooltip("Sets the cell to expand and fill across the available width.", main.getTooltipManager(), skin, "scene"));
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
                growY.addListener(main.getHandListener());
                growY.addListener(new TextTooltip("Sets the cell to expand and fill across the available height.", main.getTooltipManager(), skin, "scene"));
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
    
    private EventListener cellColSpanListener() {
        var simCell = (DialogSceneComposerModel.SimCell) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The column span of the cell.", main.getTooltipManager(), skin, "scene"));
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
    
    private EventListener cellPaddingSpacingListener() {
        var simCell = (DialogSceneComposerModel.SimCell) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                        Spinner paddingLeft = popTable.findActor("padding-left");
                        Spinner paddingRight = popTable.findActor("padding-right");
                        Spinner paddingTop = popTable.findActor("padding-top");
                        Spinner paddingBottom = popTable.findActor("padding-bottom");
                        Spinner spacingLeft = popTable.findActor("spacing-left");
                        Spinner spacingRight = popTable.findActor("spacing-right");
                        Spinner spacingTop = popTable.findActor("spacing-top");
                        Spinner spacingBottom = popTable.findActor("spacing-bottom");
            
                        events.cellPaddingSpacing((float) paddingLeft.getValue(), (float) paddingRight.getValue(), (float) paddingTop.getValue(), (float) paddingBottom.getValue(), (float) spacingLeft.getValue(), (float) spacingRight.getValue(), (float) spacingTop.getValue(), (float) spacingBottom.getValue());
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
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding to the left of the cell. Stacks with other cell padding.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
    
                table.row();
                label = new Label("Right:", skin, "scene-label-colored");
                table.add(label);
    
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setValue(simCell.padRight);
                spinner.setName("padding-right");
                table.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding to the right of the cell. Stacks with other cell padding.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
    
                table.row();
                label = new Label("Top:", skin, "scene-label-colored");
                table.add(label);
    
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setValue(simCell.padTop);
                spinner.setName("padding-top");
                table.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding to the top of the cell. Stacks with other cell padding.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
    
                table.row();
                label = new Label("Bottom:", skin, "scene-label-colored");
                table.add(label);
    
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setValue(simCell.padBottom);
                spinner.setName("padding-bottom");
                table.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding to the bottom of the cell. Stacks with other cell padding.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
    
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
                table.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The spacing to the left of the cell. Does not stack with other cell spacing.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
    
                table.row();
                label = new Label("Right:", skin, "scene-label-colored");
                table.add(label);
    
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setValue(simCell.spaceRight);
                spinner.setName("spacing-right");
                table.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The spacing to the right of the cell. Does not stack with other cell spacing.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
    
                table.row();
                label = new Label("Top:", skin, "scene-label-colored");
                table.add(label);
    
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setValue(simCell.spaceTop);
                spinner.setName("spacing-top");
                table.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The spacing to the top of the cell. Does not stack with other cell spacing.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
    
                table.row();
                label = new Label("Bottom:", skin, "scene-label-colored");
                table.add(label);
    
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setValue(simCell.spaceBottom);
                spinner.setName("spacing-bottom");
                table.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The spacing to the bottom of the cell. Does not stack with other cell spacing.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static interface WidgetSelectedListener {
        public void widgetSelected(WidgetType widgetType, PopTable popTable);
    }
    
    private PopTable.PopTableClickListener setWidgetListener(WidgetSelectedListener widgetSelectedListener) {
        var table = new Table();
        var scrollPane = new ScrollPane(table, skin, "scene");
        var scrollFocus = scrollPane;
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFlickScroll(false);
        scrollPane.addListener(main.getScrollFocusListener());
        
        
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
    
                var popTable = getPopTable();
                popTable.setWidth(popTable.getPrefWidth() + 50);
                popTable.validate();
                
                getStage().setScrollFocus(scrollFocus);
            }
        };
    
        var popTable = popTableClickListener.getPopTable();
        var label = new Label("Widgets:", skin, "scene-label-colored");
        popTable.add(label);
        label.addListener(new TextTooltip("Widgets are interactive components of your UI.", main.getTooltipManager(), skin, "scene"));
    
        label = new Label("Layout:", skin, "scene-label-colored");
        popTable.add(label);
        label.addListener(new TextTooltip("Layout widgets help organize the components of your UI and make it more adaptable to varying screen size.", main.getTooltipManager(), skin, "scene"));
    
        popTable.row();
        popTable.defaults().top();
        popTable.add(scrollPane).grow();
    
        var textButton = new TextButton("Button", skin, "scene-med");
        var valid = main.getJsonData().classHasValidStyles(Button.class);
        textButton.setDisabled(!valid);
        table.add(textButton);
        if (valid) textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Buttons are the most basic component to UI design. These are clickable widgets that can perform a certain action such as starting a game or activating a power.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.BUTTON, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("CheckBox", skin, "scene-med");
        valid = main.getJsonData().classHasValidStyles(CheckBox.class);
        textButton.setDisabled(!valid);
        table.add(textButton);
        if (valid) textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("CheckBoxes are great for setting/displaying boolean values for an options screen.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.CHECK_BOX, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("Image", skin, "scene-med");
        table.add(textButton);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Images are not directly interactable elements of a layout, but are necessary to showcase graphics or pictures in your UI. Scaling options make them a very powerful tool.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.IMAGE, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("ImageButton", skin, "scene-med");
        valid = main.getJsonData().classHasValidStyles(ImageButton.class);
        textButton.setDisabled(!valid);
        table.add(textButton);
        if (valid) textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("A Button with an image graphic in it. The image can change depending on the state of the button.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.IMAGE_BUTTON, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("ImageTextButton", skin, "scene-med");
        valid = main.getJsonData().classHasValidStyles(ImageTextButton.class);
        textButton.setDisabled(!valid);
        table.add(textButton);
        if (valid) textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("A Button with an image graphic followed by text in it. The image and text color can change depending on the state of the button.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.IMAGE_TEXT_BUTTON, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("Label", skin, "scene-med");
        valid = main.getJsonData().classHasValidStyles(Label.class);
        textButton.setDisabled(!valid);
        table.add(textButton);
        if (valid) textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("The most common way to display text in your layouts. Wrapping and ellipses options help mitigate sizing issues in small spaces.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.LABEL, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("List", skin, "scene-med");
        valid = main.getJsonData().classHasValidStyles(List.class);
        textButton.setDisabled(!valid);
        table.add(textButton);
        if (valid) textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("List presents text options in a clickable menu.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.LIST, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("ProgressBar", skin, "scene-med");
        valid = main.getJsonData().classHasValidStyles(ProgressBar.class);
        textButton.setDisabled(!valid);
        table.add(textButton);
        if (valid) textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Commonly used to display loading progress or as a health/mana indicator in HUD's.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.PROGRESS_BAR, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("SelectBox", skin, "scene-med");
        valid = main.getJsonData().classHasValidStyles(SelectBox.class);
        textButton.setDisabled(!valid);
        table.add(textButton);
        if (valid) textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("SelectBox is a kind of button that displays a selectable option list when opened.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.SELECT_BOX, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("Slider", skin, "scene-med");
        valid = main.getJsonData().classHasValidStyles(Slider.class);
        textButton.setDisabled(!valid);
        table.add(textButton);
        if (valid) textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Slider is a kind of user interactable ProgressBar that allows a user to select a value along a sliding scale.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.SLIDER, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("TextButton", skin, "scene-med");
        valid = main.getJsonData().classHasValidStyles(TextButton.class);
        textButton.setDisabled(!valid);
        table.add(textButton);
        if (valid) textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("A kind of button that contains a text element inside of it. The text color can change depending on the state of the button.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.TEXT_BUTTON, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("TextField", skin, "scene-med");
        valid = main.getJsonData().classHasValidStyles(TextField.class);
        textButton.setDisabled(!valid);
        table.add(textButton);
        if (valid) textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("TextFields are the primary way of getting text input from the user.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.TEXT_FIELD, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("TextArea", skin, "scene-med");
        valid = main.getJsonData().classHasValidStyles(TextField.class);
        textButton.setDisabled(!valid);
        table.add(textButton);
        if (valid) textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("TextAreas are a multiline version of a TextField.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.TEXT_AREA, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("Touchpad", skin, "scene-med");
        valid = main.getJsonData().classHasValidStyles(Touchpad.class);
        textButton.setDisabled(!valid);
        table.add(textButton);
        if (valid) textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Touchpad is a UI element common to mobile games. It is used lieu of keyboard input, for example.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.TOUCH_PAD, popTable);
            }
        });
    
        table = new Table();
        scrollPane = new ScrollPane(table, skin, "scene");
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFlickScroll(false);
        popTable.add(scrollPane);
        scrollPane.addListener(main.getScrollFocusListener());
    
        table.row();
        textButton = new TextButton("Container", skin, "scene-med");
        table.add(textButton);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Container is like a lightweight, single cell version of Table.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.CONTAINER, popTable);
            }
        });
        
        table.row();
        textButton = new TextButton("HorizontalGroup", skin, "scene-med");
        table.add(textButton);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Allows layout of multiple elements horizontally. It is most useful for its wrap functionality, which cannot be achieved with a Table.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.HORIZONTAL_GROUP, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("ScrollPane", skin, "scene-med");
        table.add(textButton);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Creates a scrollable layout for your widgets. It is commonly used to adapt the UI to variable content and screen sizes.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.SCROLL_PANE, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("Stack", skin, "scene-med");
        table.add(textButton);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Allows stacking of elements on top of each other.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.STACK, popTable);
            }
        });
        
        table.row();
        textButton = new TextButton("SplitPane", skin, "scene-med");
        valid = main.getJsonData().classHasValidStyles(SplitPane.class);
        textButton.setDisabled(!valid);
        table.add(textButton);
        if (valid) textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("An organizational layout that allows the user to adjust the width or height of two widgets next to each other.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.SPLIT_PANE, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("Table", skin, "scene-med");
        table.add(textButton);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("The most powerful layout widget available. Consisting of a series of configurable cells, it organizes elements in rows and columns. It serves as the basis of all layout design in Scene2D.UI.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.TABLE, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("Tree", skin, "scene-med");
        valid = main.getJsonData().classHasValidStyles(Tree.class);
        textButton.setDisabled(!valid);
        table.add(textButton);
        if (valid) textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Tree is an organizational widget that allows collapsing and expanding elements like file structures.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.TREE, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("VerticalGroup", skin, "scene-med");
        table.add(textButton);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Allows layout of multiple elements vertically. It is most useful for its wrap functionality, which cannot be achieved with a Table.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.VERTICAL_GROUP, popTable);
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener checkBoxNameListener() {
        var simCheckBox = (DialogSceneComposerModel.SimCheckBox) simActor;
        var textField = new TextField("", skin, "scene");
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
                
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Name:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setText(simCheckBox.name);
                popTable.add(textField).minWidth(150);
                textField.addListener(main.getIbeamListener());
                textField.addListener(new TextTooltip("The name of the CheckBox to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.checkBoxName(textField.getText());
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
                
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener checkBoxStyleListener() {
        var simCheckBox = (DialogSceneComposerModel.SimCheckBox) simActor;
        var popTableClickListener = new StyleSelectorPopTable(CheckBox.class, simCheckBox.style == null ? "default" : simCheckBox.style.name) {
            @Override
            public void accepted(StyleData styleData) {
                events.checkBoxStyle(styleData);
            }
        };
        
        return popTableClickListener;
    }
    
    private EventListener checkBoxTextListener() {
        var simCheckBox = (DialogSceneComposerModel.SimCheckBox) simActor;
        var textField = new TextField("", skin, "scene");
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Text:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setText(simCheckBox.text);
                popTable.add(textField).minWidth(150);
                textField.addListener(main.getIbeamListener());
                textField.addListener(new TextTooltip("The text inside of the CheckBox.", main.getTooltipManager(), skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.checkBoxText(textField.getText());
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
                
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener checkBoxCheckedListener() {
        var simCheckBox = (DialogSceneComposerModel.SimCheckBox) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Checked:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var textButton = new TextButton(simCheckBox.checked ? "TRUE" : "FALSE", skin, "scene-small");
                textButton.setChecked(simCheckBox.checked);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(main.getHandListener());
                textButton.addListener(new TextTooltip("Whether the CheckBox is checked initially.", main.getTooltipManager(), skin, "scene"));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        events.checkBoxChecked(textButton.isChecked());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener checkBoxColorListener() {
        var simCheckBox = (DialogSceneComposerModel.SimCheckBox) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                imageButton.getImage().setColor(simCheckBox.color == null ? Color.WHITE : simCheckBox.color.color);
                popTable.add(imageButton).minWidth(100);
                imageButton.addListener(main.getHandListener());
                imageButton.addListener(new TextTooltip("Select the color of the CheckBox.", main.getTooltipManager(), skin, "scene"));
                imageButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        popTable.hide();
                        main.getDialogFactory().showDialogColors(new StyleProperty(), (colorData, pressedCancel) -> {
                            if (!pressedCancel) {
                                events.checkBoxColor(colorData);
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
                label = new Label(simCheckBox.color == null ? "white" : simCheckBox.color.getName(), skin, "scene-label-colored");
                popTable.add(label);
            }
        };
    
        popTableClickListener.update();
    
        return popTableClickListener;
    }
    
    private EventListener checkBoxPaddingListener() {
        var simCheckBox = (DialogSceneComposerModel.SimCheckBox) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                        Spinner padLeft = popTable.findActor("pad-left");
                        Spinner padRight = popTable.findActor("pad-right");
                        Spinner padTop = popTable.findActor("pad-top");
                        Spinner padBottom = popTable.findActor("pad-bottom");
                        events.checkBoxPadding((float) padLeft.getValue(), (float) padRight.getValue(), (float) padTop.getValue(), (float) padBottom.getValue());
                    }
                };
                
                var label = new Label("Padding:", skin, "scene-label-colored");
                popTable.add(label).colspan(2);
                
                popTable.row();
                popTable.defaults().right().spaceRight(5);
                label = new Label("Left:", skin, "scene-label-colored");
                popTable.add(label);
                
                var spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("pad-left");
                spinner.setValue(simCheckBox.padLeft);
                popTable.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding on the left of the contents.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
                
                popTable.row();
                label = new Label("Right:", skin, "scene-label-colored");
                popTable.add(label);
                
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("pad-right");
                spinner.setValue(simCheckBox.padRight);
                popTable.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding on the right of the contents.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
                
                popTable.row();
                label = new Label("Top:", skin, "scene-label-colored");
                popTable.add(label);
                
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("pad-top");
                spinner.setValue(simCheckBox.padTop);
                popTable.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding on the top of the contents.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
                
                popTable.row();
                label = new Label("Bottom:", skin, "scene-label-colored");
                popTable.add(label);
                
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("pad-bottom");
                spinner.setValue(simCheckBox.padBottom);
                popTable.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding on the bottom of the contents.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener checkBoxDisabledListener() {
        var simCheckBox = (DialogSceneComposerModel.SimCheckBox) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
    
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
        
                var label = new Label("Disabled:", skin, "scene-label-colored");
                popTable.add(label);
        
                popTable.row();
                var textButton = new TextButton(simCheckBox.disabled ? "TRUE" : "FALSE", skin, "scene-small");
                textButton.setChecked(simCheckBox.disabled);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(main.getHandListener());
                textButton.addListener(new TextTooltip("Whether the CheckBox is disabled initially.", main.getTooltipManager(), skin, "scene"));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        events.checkBoxDisabled(textButton.isChecked());
                    }
                });
            }
        };
    
        popTableClickListener.update();
    
        return popTableClickListener;
    }
    
    private EventListener checkBoxResetListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to reset this CheckBox?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("RESET", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Resets the settings of the TextButton to their defaults.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.checkBoxReset();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener checkBoxDeleteListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to delete this CheckBox?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("DELETE", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Removes this CheckBox from its parent.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.checkBoxDelete();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener imageNameListener() {
        var simImage = (DialogSceneComposerModel.SimImage) simActor;
        var textField = new TextField("", skin, "scene");
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
                
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Name:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setText(simImage.name);
                popTable.add(textField).minWidth(150);
                textField.addListener(main.getIbeamListener());
                textField.addListener(new TextTooltip("The name of the Image to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.imageName(textField.getText());
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
                
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener imageDrawableListener() {
        var simImage = (DialogSceneComposerModel.SimImage) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
    
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
        
                var label = new Label("Drawable:", skin, "scene-label-colored");
                popTable.add(label);
        
                popTable.row();
                var stack = new Stack();
                popTable.add(stack).minSize(100).maxSize(300).grow();
                var background = new Image(skin, "scene-tile-ten");
                stack.add(background);
                Image image;
                if (simImage.drawable != null) {
                    image = new Image(main.getAtlasData().drawablePairs.get(simImage.drawable));
                } else {
                    image = new Image((Drawable) null);
                }
                stack.add(image);
        
                popTable.row();
                var textButton = new TextButton("Select Drawable", skin, "scene-small");
                popTable.add(textButton).minWidth(100);
                textButton.addListener(main.getHandListener());
                textButton.addListener(new TextTooltip("The background drawable for the table.", main.getTooltipManager(), skin, "scene"));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        popTable.hide();
                        main.getDialogFactory().showDialogDrawables(true, new DialogDrawables.DialogDrawablesListener() {
                            @Override
                            public void confirmed(DrawableData drawable, DialogDrawables dialog) {
                                events.imageDrawable(drawable);
                                image.setDrawable(main.getAtlasData().drawablePairs.get(drawable));
                            }
    
                            @Override
                            public void emptied(DialogDrawables dialog) {
                                events.imageDrawable(null);
                                image.setDrawable(null);
                            }
    
                            @Override
                            public void cancelled(DialogDrawables dialog) {
        
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
    
    private EventListener imageScalingListener() {
        var simImage = (DialogSceneComposerModel.SimImage) simActor;
        var selectBox = new SelectBox<Scaling>(skin, "scene");
        selectBox.setItems(Scaling.none, Scaling.fill, Scaling.fillX, Scaling.fillY, Scaling.fit, Scaling.stretch, Scaling.stretchX, Scaling.stretchY);
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Scaling:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                selectBox.setSelected(simImage.scaling);
                popTable.add(selectBox);
                selectBox.addListener(main.getHandListener());
                selectBox.addListener(new TextTooltip("The scaling strategy applied to the image.", main.getTooltipManager(), skin, "scene"));
                selectBox.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.imageScaling(selectBox.getSelected());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener imageResetListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to reset this Image?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("RESET", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Resets the settings of the Image to their defaults.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.imageReset();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener imageDeleteListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to delete this Image?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("DELETE", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Removes this Image from its parent.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.imageDelete();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener labelNameListener() {
        var simLabel = (DialogSceneComposerModel.SimLabel) simActor;
        var textField = new TextField("", skin, "scene");
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getStage().setKeyboardFocus(textField);
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
                popTable.add(textField).minWidth(150);
                textField.addListener(main.getIbeamListener());
                textField.addListener(new TextTooltip("The name of the Label to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.labelName(textField.getText());
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
                
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener labelTextListener() {
        var simLabel = (DialogSceneComposerModel.SimLabel) simActor;
        var textField = new TextField("", skin, "scene");
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getStage().setKeyboardFocus(textField);
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
                popTable.add(textField).minWidth(150);
                textField.addListener(main.getIbeamListener());
                textField.addListener(new TextTooltip("The text for the Label.", main.getTooltipManager(), skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.labelText(textField.getText());
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
                
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener labelStyleListener() {
        var simLabel = (DialogSceneComposerModel.SimLabel) simActor;
        var popTableClickListener = new StyleSelectorPopTable(Label.class, simLabel.style == null ? "default" : simLabel.style.name) {
            @Override
            public void accepted(StyleData styleData) {
                events.labelStyle(styleData);
            }
        };
        
        return popTableClickListener;
    }
    
    private EventListener labelColorListener() {
        var simLabel = (DialogSceneComposerModel.SimLabel) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                imageButton.addListener(main.getHandListener());
                imageButton.addListener(new TextTooltip("Select the color of the Label.", main.getTooltipManager(), skin, "scene"));
                imageButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        popTable.hide();
                        main.getDialogFactory().showDialogColors(new StyleProperty(), (colorData, pressedCancel) -> {
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
    
    private EventListener labelTextAlignmentListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the text of the label to the top left.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the text of the label to the top center.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the text of the label to the top right.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the text of the label to the middle left.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the text of the label to the center.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the text of the label to the middle right.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the text of the label the bottom left.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the text of the label to the bottom center.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the text of the label to the bottom right.", main.getTooltipManager(), skin, "scene"));
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
    
    private EventListener labelEllipsisListener() {
        var simLabel = (DialogSceneComposerModel.SimLabel) simActor;
        var textField = new TextField("", skin, "scene");
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getStage().setKeyboardFocus(textField);
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
                textButton.addListener(main.getHandListener());
                textButton.addListener(new TextTooltip("Whether ellipsis mode is activated.", main.getTooltipManager(), skin, "scene"));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        textField.setDisabled(!textButton.isChecked());
                        events.labelEllipsis(textButton.isChecked(), textField.getText());
                    }
                });
                
                label = new Label("Ellipsis String:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setDisabled(!textButton.isChecked());
                textField.setText(simLabel.ellipsisString);
                popTable.add(textField).minWidth(150);
                textField.addListener(main.getIbeamListener());
                textField.addListener(new TextTooltip("The String to punctuate the label with when the text is too long.", main.getTooltipManager(), skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.labelEllipsis(textButton.isChecked(), textField.getText());
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
                
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener labelWrapListener() {
        var simLabel = (DialogSceneComposerModel.SimLabel) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                textButton.addListener(main.getHandListener());
                textButton.addListener(new TextTooltip("Whether the Label wraps when the the text exceeds the width of the label.", main.getTooltipManager(), skin, "scene"));
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
    
    private EventListener labelResetListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to reset this Label?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("RESET", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Resets the settings of the Label to their defaults.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.labelReset();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener labelDeleteListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to delete this Label?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("DELETE", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Removes this TextButton from its parent.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.labelDelete();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener listNameListener() {
        var simList = (DialogSceneComposerModel.SimList) simActor;
        var textField = new TextField("", skin, "scene");
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
                
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Name:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setText(simList.name);
                popTable.add(textField).minWidth(150);
                textField.addListener(main.getIbeamListener());
                textField.addListener(new TextTooltip("The name of the List to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.listName(textField.getText());
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
                
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener listStyleListener() {
        var simList = (DialogSceneComposerModel.SimList) simActor;
        var popTableClickListener = new StyleSelectorPopTable(List.class, simList.style == null ? "default" : simList.style.name) {
            @Override
            public void accepted(StyleData styleData) {
                events.listStyle(styleData);
            }
        };
        
        return popTableClickListener;
    }
    
    private EventListener listTextListListener() {
        var simList = (DialogSceneComposerModel.SimList) simActor;
        var textField = new TextField("", skin, "scene");
        textField.setFocusTraversal(false);
        var draggableTextList = new DraggableTextList(true, skin, "scene-unchecked");
        draggableTextList.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                events.listList(draggableTextList.getTexts());
            }
        });
        var scrollPane = new ScrollPane(draggableTextList, skin, "scene");
        scrollPane.setFadeScrollBars(false);
        scrollPane.addListener(main.getScrollFocusListener());
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            {
                popTable.setAutomaticallyResized(true);
                
                textField.addListener(main.getIbeamListener());
                textField.addListener(new TextTooltip("The text to add to the list.", main.getTooltipManager(), skin, "scene"));
                textField.addListener(new InputListener() {
                    @Override
                    public boolean keyDown(InputEvent event, int keycode) {
                        if (keycode == Input.Keys.ENTER) {
                            addItem(textField.getText());
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
            }
            
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("List Entries:\n(Drag to change order)", skin, "scene-label-colored");
                label.setAlignment(Align.center);
                popTable.add(label).colspan(2);
                
                popTable.row();
                draggableTextList.clearChildren();
                draggableTextList.addAllTexts(simList.list);
                popTable.add(scrollPane).colspan(2).minHeight(150).growX();
    
                popTable.row();
                label = new Label("Add New Item:", skin, "scene-label-colored");
                popTable.add(label).colspan(2).padTop(10);
                
                popTable.row();
                textField.setText("");
                popTable.add(textField).minWidth(150);
                
                var textButton = new Button(skin, "scene-plus");
                popTable.add(textButton);
                textButton.addListener(main.getHandListener());
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        addItem(textField.getText());
                    }
                });
                
                getStage().setKeyboardFocus(textField);
                getStage().setScrollFocus(scrollPane);
            }
            
            public void addItem(String item) {
                draggableTextList.addText(item);
                events.listList(draggableTextList.getTexts());
                update();
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener listButtonResetListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to reset this List?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("RESET", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Resets the settings of the List to their defaults.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.listReset();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener listButtonDeleteListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to delete this List?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("DELETE", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Removes this List from its parent.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.listDelete();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener progressBarNameListener() {
        var simList = (DialogSceneComposerModel.SimProgressBar) simActor;
        var textField = new TextField("", skin, "scene");
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
                
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Name:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setText(simList.name);
                popTable.add(textField).minWidth(150);
                textField.addListener(main.getIbeamListener());
                textField.addListener(new TextTooltip("The name of the ProgressBar to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.progressBarName(textField.getText());
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
                
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener progressBarStyleListener() {
        var simProgressBar = (DialogSceneComposerModel.SimProgressBar) simActor;
        var popTableClickListener = new StyleSelectorPopTable(ProgressBar.class, simProgressBar.style == null ? "default-horizontal" : simProgressBar.style.name) {
            @Override
            public void accepted(StyleData styleData) {
                events.progressBarStyle(styleData);
            }
        };
        
        return popTableClickListener;
    }
    
    private EventListener progressBarValueSettingsListener() {
        var simProgressBar = (DialogSceneComposerModel.SimProgressBar) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                
                var label = new Label("Value:", skin, "scene-label-colored");
                table.add(label);
                
                var valueSpinner = new Spinner(simProgressBar.value, simProgressBar.increment, false, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                valueSpinner.setMinimum(simProgressBar.minimum);
                valueSpinner.setMaximum(simProgressBar.maximum);
                table.add(valueSpinner).width(100).uniformX();
                valueSpinner.getTextField().addListener(main.getIbeamListener());
                valueSpinner.getButtonMinus().addListener(main.getHandListener());
                valueSpinner.getButtonPlus().addListener(main.getHandListener());
                valueSpinner.addListener(new TextTooltip("The value of the ProgressBar.", main.getTooltipManager(), skin, "scene"));
                valueSpinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.progressBarValue((float) valueSpinner.getValue());
                    }
                });
    
                table.row();
                label = new Label("Minimum:", skin, "scene-label-colored");
                table.add(label);
    
                var minimumSpinner = new Spinner(simProgressBar.minimum, 1, false, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                minimumSpinner.setMaximum(simProgressBar.maximum);
                var maximumSpinner = new Spinner(simProgressBar.maximum, 1, false, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                maximumSpinner.setMinimum(simProgressBar.minimum);
                table.add(minimumSpinner).uniformX().fillX();
                minimumSpinner.getTextField().addListener(main.getIbeamListener());
                minimumSpinner.getButtonMinus().addListener(main.getHandListener());
                minimumSpinner.getButtonPlus().addListener(main.getHandListener());
                minimumSpinner.addListener(new TextTooltip("The minimum value of the ProgressBar.", main.getTooltipManager(), skin, "scene"));
                minimumSpinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.progressBarMinimum((float) minimumSpinner.getValue());
                        if (valueSpinner.getValue() < minimumSpinner.getValue()) {
                            valueSpinner.setValue(simProgressBar.minimum);
                        }
                        maximumSpinner.setMinimum(simProgressBar.minimum);
                    }
                });
    
                table.row();
                label = new Label("Maximum:", skin, "scene-label-colored");
                table.add(label);
    
                maximumSpinner.setValue(simProgressBar.maximum);
                table.add(maximumSpinner).uniformX().fillX();
                maximumSpinner.getTextField().addListener(main.getIbeamListener());
                maximumSpinner.getButtonMinus().addListener(main.getHandListener());
                maximumSpinner.getButtonPlus().addListener(main.getHandListener());
                maximumSpinner.addListener(new TextTooltip("The maximum value of the ProgressBar.", main.getTooltipManager(), skin, "scene"));
                maximumSpinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.progressBarMaximum((float) maximumSpinner.getValue());
                        if (valueSpinner.getValue() > maximumSpinner.getValue()) {
                            valueSpinner.setValue(simProgressBar.maximum);
                        }
                        minimumSpinner.setMaximum(simProgressBar.maximum);
                    }
                });
    
                table.row();
                label = new Label("Increment:", skin, "scene-label-colored");
                table.add(label);
    
                var incrementSpinner = new Spinner(0, 1, false, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                incrementSpinner.setValue(simProgressBar.increment);
                table.add(incrementSpinner).uniformX().fillX();
                incrementSpinner.getTextField().addListener(main.getIbeamListener());
                incrementSpinner.getButtonMinus().addListener(main.getHandListener());
                incrementSpinner.getButtonPlus().addListener(main.getHandListener());
                incrementSpinner.addListener(new TextTooltip("The increment value of the ProgressBar.", main.getTooltipManager(), skin, "scene"));
                incrementSpinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.progressBarIncrement((float) incrementSpinner.getValue());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener progressBarOrientationListener() {
        var simProgressBar = (DialogSceneComposerModel.SimProgressBar) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Orientation:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var draggableTextList = new DraggableTextList(true, skin, "scene");
                draggableTextList.setDraggable(false);
                draggableTextList.addAllTexts("Horizontal", "Vertical");
                draggableTextList.setSelected(simProgressBar.vertical ? 1 : 0);
                popTable.add(draggableTextList);
                draggableTextList.addListener(main.getHandListener());
                draggableTextList.addListener(new TextTooltip("The orientation of the ProgressBar.", main.getTooltipManager(), skin, "scene"));
                draggableTextList.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.progressBarVertical(draggableTextList.getSelectedIndex() == 1);
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener progressBarAnimationListener() {
        var simProgressBar = (DialogSceneComposerModel.SimProgressBar) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                
                table.defaults().spaceRight(5);
                var textButton = new TextButton("Animate Interpolation", skin, "scene-med");
                table.add(textButton).uniformX().fillX().colspan(2);
                textButton.addListener(main.getHandListener());
                textButton.addListener(interpolationListener(selection -> events.progressBarAnimateInterpolation(selection)));
                
                table.row();
                textButton = new TextButton("Visual Interpolation", skin, "scene-med");
                table.add(textButton).uniformX().fillX().colspan(2);
                textButton.addListener(main.getHandListener());
                textButton.addListener(interpolationListener(selection -> events.progressBarVisualInterpolation(selection)));
                
                table.row();
                var label = new Label("Animation Duration:", skin, "scene-label-colored");
                table.add(label).right();
                
                var durationSpinner = new Spinner(simProgressBar.animationDuration, 1, false, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                durationSpinner.setMinimum(0);
                table.add(durationSpinner).left();
                durationSpinner.getTextField().addListener(main.getIbeamListener());
                durationSpinner.getButtonMinus().addListener(main.getHandListener());
                durationSpinner.getButtonPlus().addListener(main.getHandListener());
                durationSpinner.addListener(new TextTooltip("The animation duration of the ProgressBar as the value changes.", main.getTooltipManager(), skin, "scene"));
                durationSpinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.progressBarAnimationDuration((float) durationSpinner.getValue());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private PopTableClickListener interpolationListener(InterpolationSelected interpolationSelected) {
        var graphDrawerDrawables = new Array<GraphDrawerDrawable>();
        
        var table = new Table();
        var scrollPane = new ScrollPane(table, skin, "scene");
        var listener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void tableShown(Event event) {
                getStage().setScrollFocus(scrollPane);
                for (var graphDrawerDrawable : graphDrawerDrawables) {
                    graphDrawerDrawable.setColor(Color.BLACK);
                }
            }
    
            @Override
            public void tableHidden(Event event) {
                for (var graphDrawerDrawable : graphDrawerDrawables) {
                    graphDrawerDrawable.setColor(Color.CLEAR);
                }
            }
        };
        var popTable = listener.getPopTable();
        
        scrollPane.setFadeScrollBars(false);
        popTable.add(scrollPane);
        
        table.defaults().space(5);
        for (Interpol interpol : Interpol.values()) {
            var button = new Button(skin, "scene-med");
            table.add(button).growX();
            
            var stack = new Stack();
            button.add(stack).size(50);
            
            var image = new Image(skin.getDrawable("white"));
            stack.add(image);
            
            var graphDrawerDrawable = new GraphDrawerDrawable(graphDrawer);
            graphDrawerDrawable.setColor(Color.BLACK);
            graphDrawerDrawable.setInterpolation(interpol.interpolation);
            graphDrawerDrawable.setSamples(10);
            graphDrawerDrawables.add(graphDrawerDrawable);
            image = new Image(graphDrawerDrawable);
            var container = new Container(image);
            container.pad(5).fill();
            stack.add(container);
            
            var label = new Label(interpol.toString(), skin, "scene-label");
            button.add(label).expandX().left().space(5);
            button.addListener(main.getHandListener());
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    popTable.hide();
                    interpolationSelected.selected(interpol);
                }
            });
            
            table.row();
        }
        
        getStage().setScrollFocus(scrollPane);
        
        return listener;
    }
    
    private interface InterpolationSelected {
        public void selected(Interpol selection);
    }
    
    private EventListener progressBarRoundListener() {
        var simProgressBar = (DialogSceneComposerModel.SimProgressBar) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Round:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var textButton = new TextButton(simProgressBar.round ? "TRUE" : "FALSE", skin, "scene-small");
                textButton.setChecked(simProgressBar.round);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(main.getHandListener());
                textButton.addListener(new TextTooltip("Whether the ProgressBar inner positions are rounded to integer.", main.getTooltipManager(), skin, "scene"));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        events.progressBarRound(textButton.isChecked());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener progressBarDisabledListener() {
        var simProgressBar = (DialogSceneComposerModel.SimProgressBar) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Disabled:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var textButton = new TextButton(simProgressBar.disabled ? "TRUE" : "FALSE", skin, "scene-small");
                textButton.setChecked(simProgressBar.disabled);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(main.getHandListener());
                textButton.addListener(new TextTooltip("Whether the ProgressBar is disabled initially.", main.getTooltipManager(), skin, "scene"));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        events.progressBarDisabled(textButton.isChecked());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener progressBarResetListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to reset this List?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("RESET", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Resets the settings of the ProgressBar to their defaults.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.progressBarReset();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener progressBarDeleteListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to delete this ProgressBar?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("DELETE", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Removes this ProgressBar from its parent.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.progressBarDelete();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener selectBoxNameListener() {
        var simSelectBox = (DialogSceneComposerModel.SimSelectBox) simActor;
        var textField = new TextField("", skin, "scene");
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
                
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Name:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setText(simSelectBox.name);
                popTable.add(textField).minWidth(150);
                textField.addListener(main.getIbeamListener());
                textField.addListener(new TextTooltip("The name of the SelectBox to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.selectBoxName(textField.getText());
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
                
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener selectBoxStyleListener() {
        var simSelectBox = (DialogSceneComposerModel.SimSelectBox) simActor;
        var popTableClickListener = new StyleSelectorPopTable(SelectBox.class, simSelectBox.style == null ? "default" : simSelectBox.style.name) {
            @Override
            public void accepted(StyleData styleData) {
                events.selectBoxStyle(styleData);
            }
        };
        
        return popTableClickListener;
    }
    
    private EventListener selectBoxTextListListener() {
        var simSelectBox = (DialogSceneComposerModel.SimSelectBox) simActor;
        var textField = new TextField("", skin, "scene");
        textField.setFocusTraversal(false);
        var draggableTextList = new DraggableTextList(true, skin, "scene");
        draggableTextList.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                events.selectBoxSelected(draggableTextList.getSelectedIndex());
                events.selectBoxList(draggableTextList.getTexts());
            }
        });
        var scrollPane = new ScrollPane(draggableTextList, skin, "scene");
        scrollPane.setFadeScrollBars(false);
        scrollPane.addListener(main.getScrollFocusListener());
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            {
                popTable.setAutomaticallyResized(true);
                
                textField.addListener(main.getIbeamListener());
                textField.addListener(new TextTooltip("The text to add to the list.", main.getTooltipManager(), skin, "scene"));
                textField.addListener(new InputListener() {
                    @Override
                    public boolean keyDown(InputEvent event, int keycode) {
                        if (keycode == Input.Keys.ENTER) {
                            addItem(textField.getText());
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
            }
            
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("List Entries:\nDrag to change order\nClick the default option", skin, "scene-label-colored");
                label.setAlignment(Align.center);
                popTable.add(label).colspan(2);
                
                popTable.row();
                draggableTextList.clearChildren();
                draggableTextList.addAllTexts(simSelectBox.list);
                if (draggableTextList.getTexts().size > 0) {
                    draggableTextList.setSelected(simSelectBox.selected);
                }
                popTable.add(scrollPane).colspan(2).minHeight(150).growX();
                
                popTable.row();
                label = new Label("Add New Item:", skin, "scene-label-colored");
                popTable.add(label).colspan(2).padTop(10);
                
                popTable.row();
                textField.setText("");
                popTable.add(textField).minWidth(150);
                
                var textButton = new Button(skin, "scene-plus");
                popTable.add(textButton);
                textButton.addListener(main.getHandListener());
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        addItem(textField.getText());
                    }
                });
                
                getStage().setKeyboardFocus(textField);
                getStage().setScrollFocus(scrollPane);
            }
            
            public void addItem(String item) {
                draggableTextList.addText(item);
                events.selectBoxList(draggableTextList.getTexts());
                update();
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener selectBoxMaxListCountListener() {
        var simSelectBox = (DialogSceneComposerModel.SimSelectBox) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                
                var label = new Label("Max List Count:", skin, "scene-label-colored");
                table.add(label).right();
                
                var valueSpinner = new Spinner(simSelectBox.maxListCount, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                valueSpinner.setMinimum(0);
                table.add(valueSpinner).width(100).left();
                valueSpinner.getTextField().addListener(main.getIbeamListener());
                valueSpinner.getButtonMinus().addListener(main.getHandListener());
                valueSpinner.getButtonPlus().addListener(main.getHandListener());
                valueSpinner.addListener(new TextTooltip("The maximum visible entries in the list.", main.getTooltipManager(), skin, "scene"));
                valueSpinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.selectBoxMaxListCount(valueSpinner.getValueAsInt());
                    }
                });
                
                table.row();
                label = new Label("(Set to 0 to show as many as possible)", skin, "scene-label-colored");
                table.add(label).colspan(2);
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener selectBoxAlignmentListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var simSelectBox = (DialogSceneComposerModel.SimSelectBox) simActor;
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the SelectBox to the top left.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.selectBoxAlignment(Align.topLeft);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Top", skin, "scene-checkbox-colored");
                var top = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the SelectBox to the top center.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.selectBoxAlignment(Align.top);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Top-Right", skin, "scene-checkbox-colored");
                var topRight = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the SelectBox to the top right.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.selectBoxAlignment(Align.topRight);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                table.row();
                imageTextButton = new ImageTextButton("Left", skin, "scene-checkbox-colored");
                var left = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the SelectBox to the middle left.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.selectBoxAlignment(Align.left);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Center", skin, "scene-checkbox-colored");
                var center = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the SelectBox to the center.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.selectBoxAlignment(Align.center);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Right", skin, "scene-checkbox-colored");
                var right = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the SelectBox to the middle right.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.selectBoxAlignment(Align.right);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                table.row();
                imageTextButton = new ImageTextButton("Bottom-Left", skin, "scene-checkbox-colored");
                var bottomLeft = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the SelectBox to the bottom left.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.selectBoxAlignment(Align.bottomLeft);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Bottom", skin, "scene-checkbox-colored");
                var bottom = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the SelectBox to the bottom center.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.selectBoxAlignment(Align.bottom);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Bottom-Right", skin, "scene-checkbox-colored");
                var bottomRight = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the SelectBox to the bottom right.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.selectBoxAlignment(Align.bottomRight);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                switch (simSelectBox.alignment) {
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
    
    private EventListener selectBoxScrollingListener() {
        var simSelectBox = (DialogSceneComposerModel.SimSelectBox) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Scrolling Disabled:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var textButton = new TextButton(simSelectBox.scrollingDisabled ? "TRUE" : "FALSE", skin, "scene-small");
                textButton.setChecked(simSelectBox.scrollingDisabled);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(main.getHandListener());
                textButton.addListener(new TextTooltip("Whether the SelectBox scrolling is diabled", main.getTooltipManager(), skin, "scene"));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        events.selectBoxScrollingDisabled(textButton.isChecked());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener selectBoxDisabledListener() {
        var simSelectBox = (DialogSceneComposerModel.SimSelectBox) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Disabled:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var textButton = new TextButton(simSelectBox.disabled ? "TRUE" : "FALSE", skin, "scene-small");
                textButton.setChecked(simSelectBox.disabled);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(main.getHandListener());
                textButton.addListener(new TextTooltip("Whether the SelectBox is disabled initially.", main.getTooltipManager(), skin, "scene"));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        events.selectBoxDisabled(textButton.isChecked());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener selectBoxResetListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to reset this SelectBox?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("RESET", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Resets the settings of the SelectBox to their defaults.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.selectBoxReset();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener selectBoxDeleteListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to delete this SelectBox?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("DELETE", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Removes this SelectBox from its parent.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.selectBoxDelete();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener sliderNameListener() {
        var simSlider = (DialogSceneComposerModel.SimSlider) simActor;
        var textField = new TextField("", skin, "scene");
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
                
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Name:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setText(simSlider.name);
                popTable.add(textField).minWidth(150);
                textField.addListener(main.getIbeamListener());
                textField.addListener(new TextTooltip("The name of the Slider to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.sliderName(textField.getText());
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
                
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener sliderStyleListener() {
        var simSlider = (DialogSceneComposerModel.SimSlider) simActor;
        var popTableClickListener = new StyleSelectorPopTable(Slider.class, simSlider.style == null ? "default-horizontal" : simSlider.style.name) {
            @Override
            public void accepted(StyleData styleData) {
                events.sliderStyle(styleData);
            }
        };
        
        return popTableClickListener;
    }
    
    private EventListener sliderValueSettingsListener() {
        var simSlider = (DialogSceneComposerModel.SimSlider) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                
                var label = new Label("Value:", skin, "scene-label-colored");
                table.add(label);
                
                var valueSpinner = new Spinner(simSlider.value, simSlider.increment, false, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                valueSpinner.setMinimum(simSlider.minimum);
                valueSpinner.setMaximum(simSlider.maximum);
                table.add(valueSpinner).width(100).uniformX();
                valueSpinner.getTextField().addListener(main.getIbeamListener());
                valueSpinner.getButtonMinus().addListener(main.getHandListener());
                valueSpinner.getButtonPlus().addListener(main.getHandListener());
                valueSpinner.addListener(new TextTooltip("The value of the Slider.", main.getTooltipManager(), skin, "scene"));
                valueSpinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.sliderValue((float) valueSpinner.getValue());
                    }
                });
                
                table.row();
                label = new Label("Minimum:", skin, "scene-label-colored");
                table.add(label);
                
                var minimumSpinner = new Spinner(simSlider.minimum, 1, false, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                minimumSpinner.setMaximum(simSlider.maximum);
                var maximumSpinner = new Spinner(simSlider.maximum, 1, false, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                maximumSpinner.setMinimum(simSlider.minimum);
                table.add(minimumSpinner).uniformX().fillX();
                minimumSpinner.getTextField().addListener(main.getIbeamListener());
                minimumSpinner.getButtonMinus().addListener(main.getHandListener());
                minimumSpinner.getButtonPlus().addListener(main.getHandListener());
                minimumSpinner.addListener(new TextTooltip("The minimum value of the Slider.", main.getTooltipManager(), skin, "scene"));
                minimumSpinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.sliderMinimum((float) minimumSpinner.getValue());
                        if (valueSpinner.getValue() < minimumSpinner.getValue()) {
                            valueSpinner.setValue(simSlider.minimum);
                        }
                        maximumSpinner.setMinimum(simSlider.minimum);
                    }
                });
                
                table.row();
                label = new Label("Maximum:", skin, "scene-label-colored");
                table.add(label);
                
                maximumSpinner.setValue(simSlider.maximum);
                table.add(maximumSpinner).uniformX().fillX();
                maximumSpinner.getTextField().addListener(main.getIbeamListener());
                maximumSpinner.getButtonMinus().addListener(main.getHandListener());
                maximumSpinner.getButtonPlus().addListener(main.getHandListener());
                maximumSpinner.addListener(new TextTooltip("The maximum value of the Slider.", main.getTooltipManager(), skin, "scene"));
                maximumSpinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.sliderMaximum((float) maximumSpinner.getValue());
                        if (valueSpinner.getValue() > maximumSpinner.getValue()) {
                            valueSpinner.setValue(simSlider.maximum);
                        }
                        minimumSpinner.setMaximum(simSlider.maximum);
                    }
                });
                
                table.row();
                label = new Label("Increment:", skin, "scene-label-colored");
                table.add(label);
                
                var incrementSpinner = new Spinner(0, 1, false, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                incrementSpinner.setValue(simSlider.increment);
                table.add(incrementSpinner).uniformX().fillX();
                incrementSpinner.getTextField().addListener(main.getIbeamListener());
                incrementSpinner.getButtonMinus().addListener(main.getHandListener());
                incrementSpinner.getButtonPlus().addListener(main.getHandListener());
                incrementSpinner.addListener(new TextTooltip("The increment value of the Slider.", main.getTooltipManager(), skin, "scene"));
                incrementSpinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.sliderIncrement((float) incrementSpinner.getValue());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener sliderOrientationListener() {
        var simSlider = (DialogSceneComposerModel.SimSlider) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Orientation:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var draggableTextList = new DraggableTextList(true, skin, "scene");
                draggableTextList.setDraggable(false);
                draggableTextList.addAllTexts("Horizontal", "Vertical");
                draggableTextList.setSelected(simSlider.vertical ? 1 : 0);
                popTable.add(draggableTextList);
                draggableTextList.addListener(main.getHandListener());
                draggableTextList.addListener(new TextTooltip("The orientation of the Slider.", main.getTooltipManager(), skin, "scene"));
                draggableTextList.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.sliderVertical(draggableTextList.getSelectedIndex() == 1);
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener sliderAnimationListener() {
        var simSlider = (DialogSceneComposerModel.SimSlider) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                
                table.defaults().spaceRight(5);
                var textButton = new TextButton("Animate Interpolation", skin, "scene-med");
                table.add(textButton).uniformX().fillX().colspan(2);
                textButton.addListener(main.getHandListener());
                textButton.addListener(interpolationListener(selection -> events.sliderAnimateInterpolation(selection)));
                
                table.row();
                textButton = new TextButton("Visual Interpolation", skin, "scene-med");
                table.add(textButton).uniformX().fillX().colspan(2);
                textButton.addListener(main.getHandListener());
                textButton.addListener(interpolationListener(selection -> events.sliderVisualInterpolation(selection)));
                
                table.row();
                var label = new Label("Animation Duration:", skin, "scene-label-colored");
                table.add(label).right();
                
                var durationSpinner = new Spinner(simSlider.animationDuration, 1, false, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                durationSpinner.setMinimum(0);
                table.add(durationSpinner).left();
                durationSpinner.getTextField().addListener(main.getIbeamListener());
                durationSpinner.getButtonMinus().addListener(main.getHandListener());
                durationSpinner.getButtonPlus().addListener(main.getHandListener());
                durationSpinner.addListener(new TextTooltip("The animation duration of the Slider as the value changes.", main.getTooltipManager(), skin, "scene"));
                durationSpinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.sliderAnimationDuration((float) durationSpinner.getValue());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener sliderRoundListener() {
        var simSlider = (DialogSceneComposerModel.SimSlider) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Round:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var textButton = new TextButton(simSlider.round ? "TRUE" : "FALSE", skin, "scene-small");
                textButton.setChecked(simSlider.round);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(main.getHandListener());
                textButton.addListener(new TextTooltip("Whether the Slider inner positions are rounded to integer.", main.getTooltipManager(), skin, "scene"));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        events.sliderRound(textButton.isChecked());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener sliderDisabledListener() {
        var simSlider = (DialogSceneComposerModel.SimSlider) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Disabled:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var textButton = new TextButton(simSlider.disabled ? "TRUE" : "FALSE", skin, "scene-small");
                textButton.setChecked(simSlider.disabled);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(main.getHandListener());
                textButton.addListener(new TextTooltip("Whether the Slider is disabled initially.", main.getTooltipManager(), skin, "scene"));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        events.sliderDisabled(textButton.isChecked());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener sliderResetListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to reset this List?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("RESET", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Resets the settings of the Slider to their defaults.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.sliderReset();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener sliderDeleteListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to delete this ProgressBar?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("DELETE", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Removes this Slider from its parent.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.sliderDelete();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener textFieldNameListener() {
        var simTextField = (DialogSceneComposerModel.SimTextField) simActor;
        var textField = new TextField("", skin, "scene");
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
                
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Name:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setText(simTextField.name);
                popTable.add(textField).minWidth(150);
                textField.addListener(main.getIbeamListener());
                textField.addListener(new TextTooltip("The name of the TextField to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textFieldName(textField.getText());
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
                
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener textFieldStyleListener() {
        var simTextField = (DialogSceneComposerModel.SimTextField) simActor;
        var popTableClickListener = new StyleSelectorPopTable(TextField.class, simTextField.style == null ? "default" : simTextField.style.name) {
            @Override
            public void accepted(StyleData styleData) {
                events.textFieldStyle(styleData);
            }
        };
        
        return popTableClickListener;
    }
    
    private EventListener textFieldTextListener() {
        var simTextField = (DialogSceneComposerModel.SimTextField) simActor;
        var textField = new TextField("", skin, "scene");
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Text:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setText(simTextField.text);
                popTable.add(textField).minWidth(150);
                textField.addListener(main.getIbeamListener());
                textField.addListener(new TextTooltip("The default text inside the TextField.", main.getTooltipManager(), skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textFieldText(textField.getText());
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
                
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener textFieldMessageTextListener() {
        var simTextField = (DialogSceneComposerModel.SimTextField) simActor;
        var textField = new TextField("", skin, "scene");
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Message Text:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setText(simTextField.messageText);
                popTable.add(textField).minWidth(150);
                textField.addListener(main.getIbeamListener());
                textField.addListener(new TextTooltip("The message inside the field when nothing is inputted and the field does not have focus.", main.getTooltipManager(), skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textFieldMessageText(textField.getText());
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
                
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener textFieldPasswordListener() {
        var simTextField = (DialogSceneComposerModel.SimTextField) simActor;
        var textField = new TextField("", skin, "scene");
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Password Character:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setText(Character.toString(simTextField.passwordCharacter));
                textField.setMaxLength(1);
                popTable.add(textField).minWidth(150);
                textField.addListener(main.getIbeamListener());
                textField.addListener(new TextTooltip("The character used to obscure text when password mode is enabled.", main.getTooltipManager(), skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (textField.getText().length() > 0) {
                            events.textFieldPasswordCharacter(textField.getText().charAt(0));
                        } else {
                            events.textFieldPasswordCharacter('*');
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
                label = new Label("Password Mode:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var textButton = new TextButton(simTextField.passwordMode ? "TRUE" : "FALSE", skin, "scene-small");
                textButton.setChecked(simTextField.passwordMode);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(main.getHandListener());
                textButton.addListener(new TextTooltip("Whether password mode is enabled for the TextField.", main.getTooltipManager(), skin, "scene"));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        events.textFieldPasswordMode(textButton.isChecked());
                    }
                });
                
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener textFieldSelectionListener() {
        var simTextField = (DialogSceneComposerModel.SimTextField) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                
                var positionLabel = new Label("Cursor Position: (" + simTextField.cursorPosition + ")", skin, "scene-label-colored");
                table.add(positionLabel);
                
                table.row();
                var slider = new Slider(0, simTextField.text == null ? 0 : simTextField.text.length(), 1, false, skin, "scene");
                slider.setValue(simTextField.cursorPosition);
                table.add(slider).minWidth(200);
                slider.addListener(main.getHandListener());
                slider.addListener(new TextTooltip("The cursor position when the textfield has keyboard focus.", main.getTooltipManager(), skin, "scene"));
                slider.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textFieldCursorPosition(MathUtils.round(slider.getValue()));
                        positionLabel.setText("Cursor Position: (" + simTextField.cursorPosition + ")");
                    }
                });
                
                table.row();
                var selectionLabel = new Label("Selection: (" + simTextField.selectionStart + ", " + simTextField.selectionEnd + ")", skin, "scene-label-colored");
                table.add(selectionLabel);
                
                table.row();
                var rangeSlider = new RangeSlider(skin, "scene");
                rangeSlider.setMinimum(0);
                rangeSlider.setMaximum(simTextField.text == null ? 0 : simTextField.text.length());
                rangeSlider.setIncrement(1);
                rangeSlider.setValueBegin(simTextField.selectionStart);
                rangeSlider.setValueEnd(simTextField.selectionEnd);
                rangeSlider.setDisabled(simTextField.selectAll);
                table.add(rangeSlider).minWidth(200);
                rangeSlider.getKnobBegin().addListener(main.getHandListener());
                rangeSlider.getKnobEnd().addListener(main.getHandListener());
                rangeSlider.addListener(new TextTooltip("The text range to be selected if this textField has keyboard focus.", main.getTooltipManager(), skin, "scene"));
                rangeSlider.addListener(new ValueBeginChangeListener() {
                    @Override
                    public void changed(ValueBeginChangeEvent event, float value, Actor actor) {
                        events.textFieldSelectionStart(MathUtils.round(value));
                    }
                });
                rangeSlider.addListener(new ValueEndChangeListener() {
                    @Override
                    public void changed(ValueEndChangeEvent event, float value, Actor actor) {
                        events.textFieldSelectionEnd(MathUtils.round(value));
                    }
                });
                rangeSlider.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        selectionLabel.setText("Selection: (" + simTextField.selectionStart + ", " + simTextField.selectionEnd + ")");
                    }
                });
                
                table.row();
                var label = new Label("Select All: ", skin, "scene-label-colored");
                table.add(label);
                
                table.row();
                var textButton = new TextButton(simTextField.selectAll ? "TRUE" : "FALSE", skin, "scene-small");
                textButton.setChecked(simTextField.selectAll);
                table.add(textButton).minWidth(100);
                textButton.addListener(main.getHandListener());
                textButton.addListener(new TextTooltip("Convenience option to select all text.", main.getTooltipManager(), skin, "scene"));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        events.textFieldSelectAll(textButton.isChecked());
                        rangeSlider.setDisabled(textButton.isChecked());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener textFieldAlignmentListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var simTextField = (DialogSceneComposerModel.SimTextField) simActor;
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the TextField to the top left.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textFieldAlignment(Align.topLeft);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Top", skin, "scene-checkbox-colored");
                var top = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the TextField to the top center.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textFieldAlignment(Align.top);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Top-Right", skin, "scene-checkbox-colored");
                var topRight = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the TextField to the top right.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textFieldAlignment(Align.topRight);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                table.row();
                imageTextButton = new ImageTextButton("Left", skin, "scene-checkbox-colored");
                var left = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the TextField to the middle left.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textFieldAlignment(Align.left);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Center", skin, "scene-checkbox-colored");
                var center = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the TextField to the center.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textFieldAlignment(Align.center);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Right", skin, "scene-checkbox-colored");
                var right = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the TextField to the middle right.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textFieldAlignment(Align.right);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                table.row();
                imageTextButton = new ImageTextButton("Bottom-Left", skin, "scene-checkbox-colored");
                var bottomLeft = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the TextField to the bottom left.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textFieldAlignment(Align.bottomLeft);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Bottom", skin, "scene-checkbox-colored");
                var bottom = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the TextField to the bottom center.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textFieldAlignment(Align.bottom);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Bottom-Right", skin, "scene-checkbox-colored");
                var bottomRight = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the TextField to the bottom right.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textFieldAlignment(Align.bottomRight);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                switch (simTextField.alignment) {
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
    
    private EventListener textFieldFocusTraversalListener() {
        var simTextField = (DialogSceneComposerModel.SimTextField) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Focus Traversal:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var textButton = new TextButton(simTextField.focusTraversal ? "TRUE" : "FALSE", skin, "scene-small");
                textButton.setChecked(simTextField.focusTraversal);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(main.getHandListener());
                textButton.addListener(new TextTooltip("Whether the text field allows for the use of focus traversal keys.", main.getTooltipManager(), skin, "scene"));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        events.textFieldFocusTraversal(textButton.isChecked());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener textFieldMaxLengthListener() {
        var simTextField = (DialogSceneComposerModel.SimTextField) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                
                var label = new Label("Max Length:", skin, "scene-label-colored");
                table.add(label).right();
                
                var valueSpinner = new Spinner(simTextField.maxLength, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                valueSpinner.setMinimum(0);
                table.add(valueSpinner).width(100).left();
                valueSpinner.getTextField().addListener(main.getIbeamListener());
                valueSpinner.getButtonMinus().addListener(main.getHandListener());
                valueSpinner.getButtonPlus().addListener(main.getHandListener());
                valueSpinner.addListener(new TextTooltip("The maximum length of characters allowed in the TextField.", main.getTooltipManager(), skin, "scene"));
                valueSpinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textFieldMaxLength(valueSpinner.getValueAsInt());
                    }
                });
                
                table.row();
                label = new Label("(Set to 0 to show as many as possible)", skin, "scene-label-colored");
                table.add(label).colspan(2);
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener textFieldDisabledListener() {
        var simTextField = (DialogSceneComposerModel.SimTextField) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Disabled:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var textButton = new TextButton(simTextField.disabled ? "TRUE" : "FALSE", skin, "scene-small");
                textButton.setChecked(simTextField.disabled);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(main.getHandListener());
                textButton.addListener(new TextTooltip("Whether the TextField is disabled initially.", main.getTooltipManager(), skin, "scene"));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        events.textFieldDisabled(textButton.isChecked());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener textFieldResetListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to reset this TextField?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("RESET", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Resets the settings of the TextField to their defaults.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.textFieldReset();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener textFieldDeleteListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to delete this TextField?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("DELETE", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Removes this TextField from its parent.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.textFieldDelete();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener textAreaNameListener() {
        var simTextArea = (DialogSceneComposerModel.SimTextArea) simActor;
        var textField = new TextField("", skin, "scene");
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
                
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Name:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setText(simTextArea.name);
                popTable.add(textField).minWidth(150);
                textField.addListener(main.getIbeamListener());
                textField.addListener(new TextTooltip("The name of the TextArea to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textAreaName(textField.getText());
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
                
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener textAreaStyleListener() {
        var simTextArea = (DialogSceneComposerModel.SimTextArea) simActor;
        var popTableClickListener = new StyleSelectorPopTable(TextField.class, simTextArea.style == null ? "default" : simTextArea.style.name) {
            @Override
            public void accepted(StyleData styleData) {
                events.textAreaStyle(styleData);
            }
        };
        
        return popTableClickListener;
    }
    
    private EventListener textAreaTextListener() {
        var simTextArea = (DialogSceneComposerModel.SimTextArea) simActor;
        var textField = new TextField("", skin, "scene");
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Text:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setText(simTextArea.text);
                popTable.add(textField).minWidth(150);
                textField.addListener(main.getIbeamListener());
                textField.addListener(new TextTooltip("The default text inside the TextArea.", main.getTooltipManager(), skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textAreaText(textField.getText());
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
                
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener textAreaMessageTextListener() {
        var simTextField = (DialogSceneComposerModel.SimTextArea) simActor;
        var textField = new TextField("", skin, "scene");
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Message Text:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setText(simTextField.messageText);
                popTable.add(textField).minWidth(150);
                textField.addListener(main.getIbeamListener());
                textField.addListener(new TextTooltip("The message inside the TextArea when nothing is inputted and the TextArea does not have focus.", main.getTooltipManager(), skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textAreaMessageText(textField.getText());
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
                
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener textAreaPasswordListener() {
        var simTextArea = (DialogSceneComposerModel.SimTextArea) simActor;
        var textField = new TextField("", skin, "scene");
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Password Character:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setText(Character.toString(simTextArea.passwordCharacter));
                textField.setMaxLength(1);
                popTable.add(textField).minWidth(150);
                textField.addListener(main.getIbeamListener());
                textField.addListener(new TextTooltip("The character used to obscure text when password mode is enabled.", main.getTooltipManager(), skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (textField.getText().length() > 0) {
                            events.textAreaPasswordCharacter(textField.getText().charAt(0));
                        } else {
                            events.textAreaPasswordCharacter('*');
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
                label = new Label("Password Mode:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var textButton = new TextButton(simTextArea.passwordMode ? "TRUE" : "FALSE", skin, "scene-small");
                textButton.setChecked(simTextArea.passwordMode);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(main.getHandListener());
                textButton.addListener(new TextTooltip("Whether password mode is enabled for the TextArea.", main.getTooltipManager(), skin, "scene"));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        textButton.setText(textButton.isChecked() ? "TRUE" : "FALSE");
                        events.textAreaPasswordMode(textButton.isChecked());
                    }
                });
                
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener textAreaSelectionListener() {
        var simTextArea = (DialogSceneComposerModel.SimTextArea) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                
                var positionLabel = new Label("Cursor Position: (" + simTextArea.cursorPosition + ")", skin, "scene-label-colored");
                table.add(positionLabel);
                
                table.row();
                var slider = new Slider(0, simTextArea.text == null ? 0 : simTextArea.text.length(), 1, false, skin, "scene");
                slider.setValue(simTextArea.cursorPosition);
                table.add(slider).minWidth(200);
                slider.addListener(main.getHandListener());
                slider.addListener(new TextTooltip("The cursor position when the TextArea has keyboard focus.", main.getTooltipManager(), skin, "scene"));
                slider.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textAreaCursorPosition(MathUtils.round(slider.getValue()));
                        positionLabel.setText("Cursor Position: (" + simTextArea.cursorPosition + ")");
                    }
                });
                
                table.row();
                var selectionLabel = new Label("Selection: (" + simTextArea.selectionStart + ", " + simTextArea.selectionEnd + ")", skin, "scene-label-colored");
                table.add(selectionLabel);
                
                table.row();
                var rangeSlider = new RangeSlider(skin, "scene");
                rangeSlider.setMinimum(0);
                rangeSlider.setMaximum(simTextArea.text == null ? 0 : simTextArea.text.length());
                rangeSlider.setIncrement(1);
                rangeSlider.setValueBegin(simTextArea.selectionStart);
                rangeSlider.setValueEnd(simTextArea.selectionEnd);
                rangeSlider.setDisabled(simTextArea.selectAll);
                table.add(rangeSlider).minWidth(200);
                rangeSlider.getKnobBegin().addListener(main.getHandListener());
                rangeSlider.getKnobEnd().addListener(main.getHandListener());
                rangeSlider.addListener(new TextTooltip("The text range to be selected if this TextArea has keyboard focus.", main.getTooltipManager(), skin, "scene"));
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
                var label = new Label("Select All: ", skin, "scene-label-colored");
                table.add(label);
                
                table.row();
                var textButton = new TextButton(simTextArea.selectAll ? "TRUE" : "FALSE", skin, "scene-small");
                textButton.setChecked(simTextArea.selectAll);
                table.add(textButton).minWidth(100);
                textButton.addListener(main.getHandListener());
                textButton.addListener(new TextTooltip("Convenience option to select all text.", main.getTooltipManager(), skin, "scene"));
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
    
    private EventListener textAreaAlignmentListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                
                var label = new Label("Alignment:", skin, "scene-label-colored");
                table.add(label).colspan(3);
                
                table.row();
                table.defaults().space(10).left().uniformX();
                var buttonGroup = new ButtonGroup<ImageTextButton>();
                var imageTextButton = new ImageTextButton("Top-Left", skin, "scene-checkbox-colored");
                var topLeft = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the TextArea to the top left.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textAreaAlignment(Align.topLeft);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Top", skin, "scene-checkbox-colored");
                var top = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the TextArea to the top center.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textAreaAlignment(Align.top);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Top-Right", skin, "scene-checkbox-colored");
                var topRight = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the TextArea to the top right.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textAreaAlignment(Align.topRight);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                table.row();
                imageTextButton = new ImageTextButton("Left", skin, "scene-checkbox-colored");
                var left = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the TextArea to the middle left.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textAreaAlignment(Align.left);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Center", skin, "scene-checkbox-colored");
                var center = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the TextArea to the center.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textAreaAlignment(Align.center);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Right", skin, "scene-checkbox-colored");
                var right = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the TextArea to the middle right.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textAreaAlignment(Align.right);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                table.row();
                imageTextButton = new ImageTextButton("Bottom-Left", skin, "scene-checkbox-colored");
                var bottomLeft = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the TextArea to the bottom left.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textAreaAlignment(Align.bottomLeft);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Bottom", skin, "scene-checkbox-colored");
                var bottom = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the TextArea to the bottom center.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textAreaAlignment(Align.bottom);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Bottom-Right", skin, "scene-checkbox-colored");
                var bottomRight = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the TextArea to the bottom right.", main.getTooltipManager(), skin, "scene"));
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
    
    private EventListener textAreaFocusTraversalListener() {
        var simTextArea = (DialogSceneComposerModel.SimTextArea) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Focus Traversal:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var textButton = new TextButton(simTextArea.focusTraversal ? "TRUE" : "FALSE", skin, "scene-small");
                textButton.setChecked(simTextArea.focusTraversal);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(main.getHandListener());
                textButton.addListener(new TextTooltip("Whether the TextArea allows for the use of focus traversal keys.", main.getTooltipManager(), skin, "scene"));
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
    
    private EventListener textAreaMaxLengthListener() {
        var simTextArea = (DialogSceneComposerModel.SimTextArea) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                
                var label = new Label("Max Length:", skin, "scene-label-colored");
                table.add(label).right();
                
                var valueSpinner = new Spinner(simTextArea.maxLength, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                valueSpinner.setMinimum(0);
                table.add(valueSpinner).width(100).left();
                valueSpinner.getTextField().addListener(main.getIbeamListener());
                valueSpinner.getButtonMinus().addListener(main.getHandListener());
                valueSpinner.getButtonPlus().addListener(main.getHandListener());
                valueSpinner.addListener(new TextTooltip("The maximum length of characters allowed in the TextArea.", main.getTooltipManager(), skin, "scene"));
                valueSpinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.textAreaMaxLength(valueSpinner.getValueAsInt());
                    }
                });
                
                table.row();
                label = new Label("(Set to 0 to show as many as possible)", skin, "scene-label-colored");
                table.add(label).colspan(2);
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener textAreaPreferredRowsListener() {
        var simTextArea = (DialogSceneComposerModel.SimTextArea) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                
                var label = new Label("Preferred Rows:", skin, "scene-label-colored");
                table.add(label).right();
                
                var valueSpinner = new Spinner(simTextArea.preferredRows, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                valueSpinner.setMinimum(1);
                table.add(valueSpinner).width(100).left();
                valueSpinner.getTextField().addListener(main.getIbeamListener());
                valueSpinner.getButtonMinus().addListener(main.getHandListener());
                valueSpinner.getButtonPlus().addListener(main.getHandListener());
                valueSpinner.addListener(new TextTooltip("The number of lines of text to help determine preferred height.", main.getTooltipManager(), skin, "scene"));
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
    
    private EventListener textAreaDisabledListener() {
        var simTextArea = (DialogSceneComposerModel.SimTextArea) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Disabled:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var textButton = new TextButton(simTextArea.disabled ? "TRUE" : "FALSE", skin, "scene-small");
                textButton.setChecked(simTextArea.disabled);
                popTable.add(textButton).minWidth(100);
                textButton.addListener(main.getHandListener());
                textButton.addListener(new TextTooltip("Whether the TextArea is disabled initially.", main.getTooltipManager(), skin, "scene"));
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
    
    private EventListener textAreaResetListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to reset this TextArea?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("RESET", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Resets the settings of the TextArea to their defaults.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.textAreaReset();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener textAreaDeleteListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to delete this TextArea?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("DELETE", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Removes this TextArea from its parent.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.textFieldDelete();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener touchPadNameListener() {
        var simTouchPad = (DialogSceneComposerModel.SimTouchPad) simActor;
        var textField = new TextField("", skin, "scene");
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
                
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Name:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setText(simTouchPad.name);
                popTable.add(textField).minWidth(150);
                textField.addListener(main.getIbeamListener());
                textField.addListener(new TextTooltip("The name of the TouchPad to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.touchPadName(textField.getText());
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
                
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener touchPadStyleListener() {
        var simTouchPad = (DialogSceneComposerModel.SimTouchPad) simActor;
        var popTableClickListener = new StyleSelectorPopTable(Touchpad.class, simTouchPad.style == null ? "default" : simTouchPad.style.name) {
            @Override
            public void accepted(StyleData styleData) {
                events.touchPadStyle(styleData);
            }
        };
        
        return popTableClickListener;
    }
    
    private EventListener touchPadDeadZoneListener() {
        var simTouchPad = (DialogSceneComposerModel.SimTouchPad) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                
                table.defaults().right().spaceRight(5);
                var label = new Label("Dead Zone:", skin, "scene-label-colored");
                table.add(label);
                
                var spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setValue(simTouchPad.deadZone);
                table.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The dead zone that does not react to user input.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.touchPadDeadZone(spinner.getValueAsInt());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener touchPadResetOnTouchUpListener() {
        var simTouchPad = (DialogSceneComposerModel.SimTouchPad) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                var imageTextButton = new ImageTextButton("Reset on Touch Up", skin, "scene-checkbox-colored");
                imageTextButton.setChecked(simTouchPad.resetOnTouchUp);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Reset the position of the TouchPad on release of the widget.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.touchPadResetOnTouchUp(imageTextButton.isChecked());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener touchPadResetListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to reset this TouchPad?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("RESET", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Resets the settings of the TouchPad to their defaults.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.touchPadReset();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener touchPadDeleteListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to delete this TouchPad?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("DELETE", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Removes this TouchPad from its parent.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.touchPadDelete();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener containerNameListener() {
        var simContainer = (DialogSceneComposerModel.SimContainer) simActor;
        var textField = new TextField("", skin, "scene");
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
                
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Name:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setText(simContainer.name);
                popTable.add(textField).minWidth(150);
                textField.addListener(main.getIbeamListener());
                textField.addListener(new TextTooltip("The name of the Container to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.containerName(textField.getText());
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
                
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener containerBackgroundListener() {
        var simContainer = (DialogSceneComposerModel.SimContainer) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Background:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                var stack = new Stack();
                popTable.add(stack).minSize(100).maxSize(300).grow();
                var background = new Image(skin, "scene-tile-ten");
                stack.add(background);
                Image image;
                if (simContainer.background != null) {
                    image = new Image(main.getAtlasData().drawablePairs.get(simContainer.background));
                } else {
                    image = new Image((Drawable) null);
                }
                stack.add(image);
                
                popTable.row();
                var textButton = new TextButton("Select Drawable", skin, "scene-small");
                popTable.add(textButton).minWidth(100);
                textButton.addListener(main.getHandListener());
                textButton.addListener(new TextTooltip("The background drawable for the container.", main.getTooltipManager(), skin, "scene"));
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        popTable.hide();
                        main.getDialogFactory().showDialogDrawables(true, new DialogDrawables.DialogDrawablesListener() {
                            @Override
                            public void confirmed(DrawableData drawable, DialogDrawables dialog) {
                                events.containerBackground(drawable);
                                image.setDrawable(main.getAtlasData().drawablePairs.get(drawable));
                            }
                            
                            @Override
                            public void emptied(DialogDrawables dialog) {
                                events.containerBackground(null);
                                image.setDrawable(null);
                            }
                            
                            @Override
                            public void cancelled(DialogDrawables dialog) {
                            
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
    
    private EventListener containerFillListener() {
        var simContainer = (DialogSceneComposerModel.SimContainer) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var fillX = new ImageTextButton("Fill X", skin, "scene-checkbox-colored");
                var fillY = new ImageTextButton("Fill Y", skin, "scene-checkbox-colored");
                
                var changeListener = new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.containerFill(fillX.isChecked(), fillY.isChecked());
                    }
                };
                
                var table = new Table();
                popTable.add(table);
                
                table.defaults().left().spaceRight(5);
                fillX.setChecked(simContainer.fillX);
                fillX.setProgrammaticChangeEvents(false);
                table.add(fillX);
                fillX.addListener(main.getHandListener());
                fillX.addListener(new TextTooltip("Stretches the contents to fill the width of the cell.", main.getTooltipManager(), skin, "scene"));
                fillX.addListener(changeListener);
                
                fillY.setChecked(simContainer.fillY);
                fillY.setProgrammaticChangeEvents(false);
                table.add(fillY);
                fillY.addListener(main.getHandListener());
                fillY.addListener(new TextTooltip("Stretches the contents to fill the height of the container.", main.getTooltipManager(), skin, "scene"));
                fillY.addListener(changeListener);
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener containerSizeListener() {
        var simContainer = (DialogSceneComposerModel.SimContainer) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                
                var changeListener = new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        Spinner minimumWidth = popTable.findActor("minimum-width");
                        Spinner minimumHeight = popTable.findActor("minimum-height");
                        Spinner maximumWidth = popTable.findActor("maximum-width");
                        Spinner maximumHeight = popTable.findActor("maximum-height");
                        Spinner preferredWidth = popTable.findActor("preferred-width");
                        Spinner preferredHeight = popTable.findActor("preferred-height");
                        events.containerSize((float) minimumWidth.getValue(), (float) minimumHeight.getValue(), (float) maximumWidth.getValue(), (float) maximumHeight.getValue(), (float) preferredWidth.getValue(), (float) preferredHeight.getValue());
                    }
                };
                
                var label = new Label("Minimum:", skin, "scene-label-colored");
                table.add(label).colspan(2);
                
                table.row();
                table.defaults().right().spaceRight(5);
                label = new Label("Width:", skin, "scene-label-colored");
                table.add(label);
                
                var spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("minimum-width");
                spinner.setValue(simContainer.minWidth);
                table.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The minimum width of the contents of the container.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
                
                table.row();
                label = new Label("Height:", skin, "scene-label-colored");
                table.add(label);
                
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("minimum-height");
                spinner.setValue(simContainer.minHeight);
                table.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The minimum height of the contents of the container.", main.getTooltipManager(), skin, "scene"));
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
                spinner.setValue(simContainer.maxWidth);
                table.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The maximum width of the contents of the container.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
                
                table.row();
                label = new Label("Height:", skin, "scene-label-colored");
                table.add(label);
                
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("maximum-height");
                spinner.setValue(simContainer.maxHeight);
                table.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The maximum height of the contents of the container.", main.getTooltipManager(), skin, "scene"));
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
                spinner.setValue(simContainer.preferredWidth);
                table.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The preferred width of the contents of the container.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
                
                table.row();
                label = new Label("Height:", skin, "scene-label-colored");
                table.add(label);
                
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("preferred-height");
                spinner.setValue(simContainer.preferredHeight);
                table.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The preferred height of the contents of the container.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener containerPaddingListener() {
        var simContainer = (DialogSceneComposerModel.SimContainer) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                        Spinner paddingLeft = popTable.findActor("padding-left");
                        Spinner paddingRight = popTable.findActor("padding-right");
                        Spinner paddingTop = popTable.findActor("padding-top");
                        Spinner paddingBottom = popTable.findActor("padding-bottom");
                        
                        events.containerPadding((float) paddingLeft.getValue(), (float) paddingRight.getValue(), (float) paddingTop.getValue(), (float) paddingBottom.getValue());
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
                spinner.setValue(simContainer.padLeft);
                spinner.setName("padding-left");
                table.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding on the left of the container.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
                
                table.row();
                label = new Label("Right:", skin, "scene-label-colored");
                table.add(label);
                
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setValue(simContainer.padRight);
                spinner.setName("padding-right");
                table.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding on the right of the container.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
                
                table.row();
                label = new Label("Top:", skin, "scene-label-colored");
                table.add(label);
                
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setValue(simContainer.padTop);
                spinner.setName("padding-top");
                table.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding on the top of the container.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
                
                table.row();
                label = new Label("Bottom:", skin, "scene-label-colored");
                table.add(label);
                
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setValue(simContainer.padBottom);
                spinner.setName("padding-bottom");
                table.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding on the bottom of the container.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(changeListener);
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener containerAlignmentListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var simContainer = (DialogSceneComposerModel.SimContainer) simActor;
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the container to the top left.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.containerAlignment(Align.topLeft);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Top", skin, "scene-checkbox-colored");
                var top = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the container to the top center.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.containerAlignment(Align.top);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Top-Right", skin, "scene-checkbox-colored");
                var topRight = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the container to the top right.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.containerAlignment(Align.topRight);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                table.row();
                imageTextButton = new ImageTextButton("Left", skin, "scene-checkbox-colored");
                var left = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the container to the middle left.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.containerAlignment(Align.left);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Center", skin, "scene-checkbox-colored");
                var center = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the container to the center.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.containerAlignment(Align.center);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Right", skin, "scene-checkbox-colored");
                var right = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the container to the middle right.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.containerAlignment(Align.right);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                table.row();
                imageTextButton = new ImageTextButton("Bottom-Left", skin, "scene-checkbox-colored");
                var bottomLeft = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the container to the bottom left.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.containerAlignment(Align.bottomLeft);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Bottom", skin, "scene-checkbox-colored");
                var bottom = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the container to the bottom center.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.containerAlignment(Align.bottom);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Bottom-Right", skin, "scene-checkbox-colored");
                var bottomRight = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the contents of the container to the bottom right.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.containerAlignment(Align.bottomRight);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                switch (simContainer.alignment) {
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
    
    private EventListener containerResetListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var table = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to reset this container?", skin, "scene-label-colored");
        table.add(label);
        
        table.row();
        var textButton = new TextButton("RESET", skin, "scene-small");
        table.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Resets all of the settings of the container to their defaults.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                table.hide();
                events.containerReset();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener containerDeleteListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var table = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to delete this container?", skin, "scene-label-colored");
        table.add(label);
        
        table.row();
        var textButton = new TextButton("DELETE", skin, "scene-small");
        table.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Deletes the container and its contents.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                table.hide();
                events.containerDelete();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener horizontalGroupNameListener() {
        var simHorizontalGroup = (DialogSceneComposerModel.SimHorizontalGroup) simActor;
        var textField = new TextField("", skin, "scene");
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getStage().setKeyboardFocus(textField);
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
                popTable.add(textField).minWidth(150);
                textField.addListener(main.getIbeamListener());
                textField.addListener(new TextTooltip("The name of the HorizontalGroup to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.horizontalGroupName(textField.getText());
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
                
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener horizontalGroupExpandFillGrowListener() {
        var simHorizontalGroup = (DialogSceneComposerModel.SimHorizontalGroup) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                expand.addListener(main.getHandListener());
                expand.addListener(new TextTooltip("Set the widgets to expand to the available space.", main.getTooltipManager(), skin, "scene"));
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
                fill.addListener(main.getHandListener());
                fill.addListener(new TextTooltip("Sets the widgets to fill the entire space.", main.getTooltipManager(), skin, "scene"));
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
                grow.addListener(main.getHandListener());
                grow.addListener(new TextTooltip("Sets the widgets to expand and fill.", main.getTooltipManager(), skin, "scene"));
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
    
    private EventListener horizontalGroupPaddingSpacingListener() {
        var simHorizontalGroup = (DialogSceneComposerModel.SimHorizontalGroup) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding to the left of the widgets.", main.getTooltipManager(), skin, "scene"));
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
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding to the right of the widgets.", main.getTooltipManager(), skin, "scene"));
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
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding to the top of the widgets.", main.getTooltipManager(), skin, "scene"));
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
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding to the bottom of the widgets.", main.getTooltipManager(), skin, "scene"));
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
                
                label = new Label("Spacing:", skin, "scene-label-colored");
                table.add(label).colspan(2);
                
                table.row();
                table.defaults().right().spaceRight(5);
                label = new Label("Left:", skin, "scene-label-colored");
                table.add(label);
                
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setValue(simHorizontalGroup.space);
                spinner.setName("spacing-left");
                table.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The spacing between the widgets.", main.getTooltipManager(), skin, "scene"));
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
    
    private EventListener horizontalGroupWrapListener() {
        var simHorizontalGroup = (DialogSceneComposerModel.SimHorizontalGroup) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Whether the widgets will wrap to the next line.", main.getTooltipManager(), skin, "scene"));
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
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The vertical space between rows when wrap is enabled.", main.getTooltipManager(), skin, "scene"));
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
    
    private EventListener horizontalGroupAlignmentListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the widgets to the top left.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the widgets to the top center.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the widgets to the top right.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the widgets to the middle left.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the widgets to the center.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the widgets to the middle right.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the widgets to the bottom left.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the widgets to the bottom center.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the widgets to the bottom right.", main.getTooltipManager(), skin, "scene"));
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
    
    private EventListener horizontalGroupRowAlignmentListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the widgets to the top left.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the widgets to the top center.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the widgets to the top right.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the widgets to the middle left.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the widgets to the center.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the widgets to the middle right.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the widgets to the bottom left.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the widgets to the bottom center.", main.getTooltipManager(), skin, "scene"));
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the widgets to the bottom right.", main.getTooltipManager(), skin, "scene"));
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
    
    private EventListener horizontalGroupReverseListener() {
        var simHorizontalGroup = (DialogSceneComposerModel.SimHorizontalGroup) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Reverse the display order of the widgets.", main.getTooltipManager(), skin, "scene"));
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
    
    private EventListener horizontalGroupResetListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to reset this TouchPad?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("RESET", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Resets the settings of the HorizontalGroup to their defaults.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.horizontalGroupReset();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener horizontalGroupDeleteListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to delete this HorizontalGroup?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("DELETE", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Removes this HorizontalGroup from its parent.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.horizontalGroupDelete();
            }
        });
        
        return popTableClickListener;
    }
    
    public void horizontalGroupAddChild(WidgetType widgetType, PopTable popTable) {
        popTable.hide();
        events.horizontalGroupAddChild(widgetType);
    }
    
    private EventListener verticalGroupNameListener() {
        var simVerticalGroup = (DialogSceneComposerModel.SimVerticalGroup) simActor;
        var textField = new TextField("", skin, "scene");
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
                
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Name:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setText(simVerticalGroup.name);
                popTable.add(textField).minWidth(150);
                textField.addListener(main.getIbeamListener());
                textField.addListener(new TextTooltip("The name of the VerticalGroup to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.verticalGroupName(textField.getText());
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
                
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener verticalGroupExpandFillGrowListener() {
        var simVerticalGroup = (DialogSceneComposerModel.SimVerticalGroup) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                        events.verticalGroupExpand(expand.isChecked());
                        events.verticalGroupFill(fill.isChecked());
                    }
                };
                
                var table = new Table();
                popTable.add(table);
                
                table.defaults().left().spaceRight(5);
                expand.setChecked(simVerticalGroup.expand);
                expand.setProgrammaticChangeEvents(false);
                table.add(expand);
                expand.addListener(main.getHandListener());
                expand.addListener(new TextTooltip("Set the widgets to expand to the available space.", main.getTooltipManager(), skin, "scene"));
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
                fill.setChecked(simVerticalGroup.fill);
                fill.setProgrammaticChangeEvents(false);
                table.add(fill);
                fill.addListener(main.getHandListener());
                fill.addListener(new TextTooltip("Sets the widgets to fill the entire space.", main.getTooltipManager(), skin, "scene"));
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
                grow.setChecked(simVerticalGroup.expand && simVerticalGroup.fill);
                grow.setProgrammaticChangeEvents(false);
                table.add(grow);
                grow.addListener(main.getHandListener());
                grow.addListener(new TextTooltip("Sets the widgets to expand and fill.", main.getTooltipManager(), skin, "scene"));
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
    
    private EventListener verticalGroupPaddingSpacingListener() {
        var simVerticalGroup = (DialogSceneComposerModel.SimVerticalGroup) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                spinner.setValue(simVerticalGroup.padLeft);
                table.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding to the left of the widgets.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.verticalGroupPadLeft(((Spinner) actor).getValueAsInt());
                    }
                });
                
                table.row();
                label = new Label("Right:", skin, "scene-label-colored");
                table.add(label);
                
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setValue(simVerticalGroup.padRight);
                spinner.setName("padding-right");
                table.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding to the right of the widgets.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.verticalGroupPadRight(((Spinner) actor).getValueAsInt());
                    }
                });
                
                table.row();
                label = new Label("Top:", skin, "scene-label-colored");
                table.add(label);
                
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setValue(simVerticalGroup.padTop);
                spinner.setName("padding-top");
                table.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding to the top of the widgets.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.verticalGroupPadTop(((Spinner) actor).getValueAsInt());
                    }
                });
                
                table.row();
                label = new Label("Bottom:", skin, "scene-label-colored");
                table.add(label);
                
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setValue(simVerticalGroup.padBottom);
                spinner.setName("padding-bottom");
                table.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The padding to the bottom of the widgets.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.verticalGroupPadBottom(((Spinner) actor).getValueAsInt());
                    }
                });
                
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
                spinner.setValue(simVerticalGroup.space);
                spinner.setName("spacing-left");
                table.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The spacing between the widgets.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.verticalGroupSpace(((Spinner) actor).getValueAsInt());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener verticalGroupWrapListener() {
        var simVerticalGroup = (DialogSceneComposerModel.SimVerticalGroup) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                imageTextButton.setChecked(simVerticalGroup.wrap);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Whether the widgets will wrap to the next line.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.verticalGroupWrap(imageTextButton.isChecked());
                    }
                });
                
                table.row();
                var label = new Label("Wrap Space:", skin, "scene-label-colored");
                table.add(label);
                
                var spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setValue(simVerticalGroup.padRight);
                spinner.setName("padding-right");
                table.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The vertical space between rows when wrap is enabled.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.verticalGroupWrapSpace(((Spinner) actor).getValueAsInt());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener verticalGroupAlignmentListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var simVerticalGroup = (DialogSceneComposerModel.SimVerticalGroup) simActor;
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the widgets to the top left.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.verticalGroupAlignment(Align.topLeft);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Top", skin, "scene-checkbox-colored");
                var top = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the widgets to the top center.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.verticalGroupAlignment(Align.top);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Top-Right", skin, "scene-checkbox-colored");
                var topRight = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the widgets to the top right.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.verticalGroupAlignment(Align.topRight);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                table.row();
                imageTextButton = new ImageTextButton("Left", skin, "scene-checkbox-colored");
                var left = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the widgets to the middle left.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.verticalGroupAlignment(Align.left);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Center", skin, "scene-checkbox-colored");
                var center = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the widgets to the center.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.verticalGroupAlignment(Align.center);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Right", skin, "scene-checkbox-colored");
                var right = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the widgets to the middle right.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.verticalGroupAlignment(Align.right);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                table.row();
                imageTextButton = new ImageTextButton("Bottom-Left", skin, "scene-checkbox-colored");
                var bottomLeft = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the widgets to the bottom left.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.verticalGroupAlignment(Align.bottomLeft);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Bottom", skin, "scene-checkbox-colored");
                var bottom = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the widgets to the bottom center.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.verticalGroupAlignment(Align.bottom);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Bottom-Right", skin, "scene-checkbox-colored");
                var bottomRight = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the widgets to the bottom right.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.verticalGroupAlignment(Align.bottomRight);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                switch (simVerticalGroup.alignment) {
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
    
    private EventListener verticalGroupColumnAlignmentListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var simVerticalGroup = (DialogSceneComposerModel.SimVerticalGroup) simActor;
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
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the widgets to the top left.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.verticalGroupColumnAlignment(Align.topLeft);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Top", skin, "scene-checkbox-colored");
                var top = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the widgets to the top center.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.verticalGroupColumnAlignment(Align.top);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Top-Right", skin, "scene-checkbox-colored");
                var topRight = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the widgets to the top right.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.verticalGroupColumnAlignment(Align.topRight);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                table.row();
                imageTextButton = new ImageTextButton("Left", skin, "scene-checkbox-colored");
                var left = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the widgets to the middle left.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.verticalGroupColumnAlignment(Align.left);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Center", skin, "scene-checkbox-colored");
                var center = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the widgets to the center.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.verticalGroupColumnAlignment(Align.center);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Right", skin, "scene-checkbox-colored");
                var right = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the widgets to the middle right.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.verticalGroupColumnAlignment(Align.right);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                table.row();
                imageTextButton = new ImageTextButton("Bottom-Left", skin, "scene-checkbox-colored");
                var bottomLeft = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the widgets to the bottom left.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.verticalGroupColumnAlignment(Align.bottomLeft);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Bottom", skin, "scene-checkbox-colored");
                var bottom = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the widgets to the bottom center.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.verticalGroupColumnAlignment(Align.bottom);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                imageTextButton = new ImageTextButton("Bottom-Right", skin, "scene-checkbox-colored");
                var bottomRight = imageTextButton;
                imageTextButton.setProgrammaticChangeEvents(false);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Align the widgets to the bottom right.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.verticalGroupColumnAlignment(Align.bottomRight);
                    }
                });
                buttonGroup.add(imageTextButton);
                
                switch (simVerticalGroup.alignment) {
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
    
    private EventListener verticalGroupReverseListener() {
        var simVerticalGroup = (DialogSceneComposerModel.SimVerticalGroup) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                imageTextButton.setChecked(simVerticalGroup.reverse);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Reverse the display order of the widgets.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.verticalGroupReverse(imageTextButton.isChecked());
                    }
                });
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener verticalGroupResetListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to reset this TouchPad?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("RESET", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Resets the settings of the VerticalGroup to their defaults.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.verticalGroupReset();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener verticalGroupDeleteListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to delete this VerticalGroup?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("DELETE", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Removes this HorizontalGroup from its parent.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.verticalGroupDelete();
            }
        });
        
        return popTableClickListener;
    }
    
    public void verticalGroupAddChild(WidgetType widgetType, PopTable popTable) {
        popTable.hide();
        events.verticalGroupAddChild(widgetType);
    }
    
    private EventListener scrollPaneNameListener() {
        var simScrollPane = (DialogSceneComposerModel.SimScrollPane) simActor;
        var textField = new TextField("", skin, "scene");
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
                
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Name:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setText(simScrollPane.name);
                popTable.add(textField).minWidth(150);
                textField.addListener(main.getIbeamListener());
                textField.addListener(new TextTooltip("The name of the ScrollPane to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.scrollPaneName(textField.getText());
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
                
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener scrollPaneStyleListener() {
        var simScrollPane = (DialogSceneComposerModel.SimScrollPane) simActor;
        var popTableClickListener = new StyleSelectorPopTable(ScrollPane.class, simScrollPane.style == null ? "default" : simScrollPane.style.name) {
            @Override
            public void accepted(StyleData styleData) {
                events.scrollPaneStyle(styleData);
            }
        };
        
        return popTableClickListener;
    }
    
    private EventListener scrollPaneKnobsListener() {
        var simScrollPane = (DialogSceneComposerModel.SimScrollPane) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                var imageTextButton = new ImageTextButton("Variable Size Knobs", skin, "scene-checkbox-colored");
                imageTextButton.setChecked(simScrollPane.variableSizeKnobs);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Scroll knobs are sized based on getMaxX() or getMaxY()", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.scrollPaneVariableSizeKnobs(((ImageTextButton) actor).isChecked());
                    }
                });
                
                table.row();
                imageTextButton = new ImageTextButton("Fade Scroll Bars", skin, "scene-checkbox-colored");
                imageTextButton.setChecked(simScrollPane.fadeScrollBars);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Scrollbars don't reduce the scrollable size and fade out if not used.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.scrollPaneFadeScrollBars(((ImageTextButton) actor).isChecked());
                    }
                });
                
                table.row();
                imageTextButton = new ImageTextButton("Visible", skin, "scene-checkbox-colored");
                imageTextButton.setChecked(simScrollPane.scrollBarsVisible);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Shows or hides the scrollbars when Fade Scroll Bars is activated.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.scrollPaneScrollBarsOnTop(((ImageTextButton) actor).isChecked());
                    }
                });
                
                table.row();
                imageTextButton = new ImageTextButton("Scroll Bars On Top", skin, "scene-checkbox-colored");
                imageTextButton.setChecked(simScrollPane.scrollBarsOnTop);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Scrollbars don't reduce the scrollable size and fade out if not used.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.scrollPaneScrollBarsOnTop(((ImageTextButton) actor).isChecked());
                    }
                });
    
                table.row();
                imageTextButton = new ImageTextButton("Force Horizontal Scroll", skin, "scene-checkbox-colored");
                imageTextButton.setChecked(simScrollPane.forceScrollX);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Forces the horizontal scroll bar to be enabled.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.scrollPaneForceScrollX(((ImageTextButton) actor).isChecked());
                    }
                });
    
                table.row();
                imageTextButton = new ImageTextButton("Force Vertical Scroll", skin, "scene-checkbox-colored");
                imageTextButton.setChecked(simScrollPane.forceScrollY);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Forces the vertical scroll bar to be enabled.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.scrollPaneForceScrollY(((ImageTextButton) actor).isChecked());
                    }
                });
    
                table.row();
                imageTextButton = new ImageTextButton("Horizontal Scrolling Disabled", skin, "scene-checkbox-colored");
                imageTextButton.setChecked(simScrollPane.scrollingDisabledX);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Horizontal Scrolling is disabled.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.scrollPaneScrollingDisabledX(((ImageTextButton) actor).isChecked());
                    }
                });
    
                table.row();
                imageTextButton = new ImageTextButton("Vertical Scrolling Disabled", skin, "scene-checkbox-colored");
                imageTextButton.setChecked(simScrollPane.scrollingDisabledY);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Vertical scrolling is disabled.", main.getTooltipManager(), skin, "scene"));
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
                var label = new Label("Horizontal Scroll Position: ", skin, "scene-label-colored");
                table.add(label);
                
                var selectBox = new SelectBox<String>(skin, "scene");
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
                label = new Label("Vertical Scroll Position: ", skin, "scene-label-colored");
                table.add(label);
                
                selectBox = new SelectBox<>(skin, "scene");
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
    
    private EventListener scrollPaneScrollListener() {
        var simScrollPane = (DialogSceneComposerModel.SimScrollPane) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                var imageTextButton = new ImageTextButton("Clamp", skin, "scene-checkbox-colored");
                imageTextButton.setChecked(simScrollPane.clamp);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Prevents scrolling out of the widget's bounds when using flick Scroll.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.scrollPaneClamp(((ImageTextButton) actor).isChecked());
                    }
                });
                
                table.row();
                imageTextButton = new ImageTextButton("Flick Scroll", skin, "scene-checkbox-colored");
                imageTextButton.setChecked(simScrollPane.flickScroll);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Allow users to scroll by flicking the contents of the ScrollPanel", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.scrollPaneFlickScroll(((ImageTextButton) actor).isChecked());
                    }
                });
    
                table.row();
                var subTable = new Table();
                table.add(subTable);
                
                var label = new Label("Fling Time:", skin, "scene-label-colored");
                subTable.add(label).space(5);
                
                var spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("pad-left");
                spinner.setValue(simScrollPane.flingTime);
                subTable.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The amount of time in seconds that a fling will continue to scroll.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.scrollPaneFlingTime((float) ((Spinner) actor).getValue());
                    }
                });
                
                table.row();
                imageTextButton = new ImageTextButton("Overscroll X", skin, "scene-checkbox-colored");
                imageTextButton.setChecked(simScrollPane.overScrollX);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("The widget can be scrolled passed the bounds horizontally and will snap back into place if flick scroll is enabled.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.scrollPaneOverScrollX(((ImageTextButton) actor).isChecked());
                    }
                });
    
                table.row();
                imageTextButton = new ImageTextButton("Overscroll Y", skin, "scene-checkbox-colored");
                imageTextButton.setChecked(simScrollPane.overScrollY);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("The widget can be scrolled passed the bounds vertically and will snap back into place if flick scroll is enabled.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.scrollPaneOverScrollY(((ImageTextButton) actor).isChecked());
                    }
                });
    
                table.row();
                subTable = new Table();
                table.add(subTable);
    
                label = new Label("Overscroll Distance:", skin, "scene-label-colored");
                subTable.add(label).spaceRight(5);
    
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("pad-left");
                spinner.setValue(simScrollPane.overScrollDistance);
                subTable.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The distance in pixels that the user is allowed to scroll beyond the bounds if overscroll is enabled.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.scrollPaneOverScrollDistance((float) ((Spinner) actor).getValue());
                    }
                });
    
                subTable.row();
                label = new Label("Overscroll Speed Min:", skin, "scene-label-colored");
                subTable.add(label).spaceRight(5);
    
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("pad-left");
                spinner.setValue(simScrollPane.overScrollSpeedMin);
                subTable.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The minimum speed that scroll returns to the widget bounds when overscroll is enabled.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.scrollPaneOverScrollSpeedMin((float) ((Spinner) actor).getValue());
                    }
                });
    
                subTable.row();
                label = new Label("Overscroll Speed Max:", skin, "scene-label-colored");
                subTable.add(label).spaceRight(5);
    
                spinner = new Spinner(0, 1, true, Spinner.Orientation.RIGHT_STACK, skin, "scene");
                spinner.setName("pad-left");
                spinner.setValue(simScrollPane.overScrollSpeedMax);
                subTable.add(spinner);
                spinner.getTextField().addListener(main.getIbeamListener());
                spinner.getButtonMinus().addListener(main.getHandListener());
                spinner.getButtonPlus().addListener(main.getHandListener());
                spinner.addListener(new TextTooltip("The maximum speed that scroll returns to the widget bounds when overscroll is enabled.", main.getTooltipManager(), skin, "scene"));
                spinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.scrollPaneOverScrollSpeedMax((float) ((Spinner) actor).getValue());
                    }
                });
    
                table.row();
                imageTextButton = new ImageTextButton("Smooth Scrolling", skin, "scene-checkbox-colored");
                imageTextButton.setChecked(simScrollPane.smoothScrolling);
                table.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Scrolling is interpolated instead of jumping to position immediately.", main.getTooltipManager(), skin, "scene"));
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
    
    private EventListener scrollPaneResetListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to reset this ScrollPane?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("RESET", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Resets the settings of the ScrollPane to their defaults.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.scrollPaneReset();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener scrollPaneDeleteListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to delete this ScrollPane?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("DELETE", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Removes this TouchPad from its parent.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.scrollPaneDelete();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener stackNameListener() {
        var simStack = (SimStack) simActor;
        var textField = new TextField("", skin, "scene");
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
                
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Name:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setText(simStack.name);
                popTable.add(textField).minWidth(150);
                textField.addListener(main.getIbeamListener());
                textField.addListener(new TextTooltip("The name of the Stack to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.stackName(textField.getText());
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
                
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public void stackAddChild(WidgetType widgetType, PopTable popTable) {
        popTable.hide();
        events.stackAddChild(widgetType);
    }
    
    private EventListener stackResetListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to reset this Stack?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("RESET", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Resets the settings of the Stack to their defaults.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.stackReset();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener stackDeleteListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to delete this Stack?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("DELETE", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Removes this Stack from its parent.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.scrollPaneDelete();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener splitPaneNameListener() {
        var simSplitPane = (DialogSceneComposerModel.SimSplitPane) simActor;
        var textField = new TextField("", skin, "scene");
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getStage().setKeyboardFocus(textField);
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
                popTable.add(textField).minWidth(150);
                textField.addListener(main.getIbeamListener());
                textField.addListener(new TextTooltip("The name of the SplitPane to allow for convenient searching via Group#findActor().", main.getTooltipManager(), skin, "scene"));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        events.splitPaneName(textField.getText());
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
                
                getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    private EventListener splitPaneStyleListener() {
        var simSplitPane = (DialogSceneComposerModel.SimSplitPane) simActor;
        var popTableClickListener = new StyleSelectorPopTable(SplitPane.class, simSplitPane.style == null ? "default-horizontal" : simSplitPane.style.name) {
            @Override
            public void accepted(StyleData styleData) {
                events.splitPaneStyle(styleData);
            }
        };
        
        return popTableClickListener;
    }
    
    private EventListener splitPaneOrientationListener() {
        var simSplitPane = (DialogSceneComposerModel.SimSplitPane) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                imageTextButton.setChecked(!simSplitPane.vertical);
                table.add(imageTextButton);
                buttonGroup.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Horizontal orientation of the widgets.", main.getTooltipManager(), skin, "scene"));
                imageTextButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (true) events.splitPaneVertical(false);
                    }
                });
                
                table.row();
                imageTextButton = new ImageTextButton("Vertical", skin, "scene-checkbox-colored");
                imageTextButton.setChecked(simSplitPane.vertical);
                table.add(imageTextButton);
                buttonGroup.add(imageTextButton);
                imageTextButton.addListener(main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Vertical orientation of the widgets.", main.getTooltipManager(), skin, "scene"));
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
    
    private EventListener splitPaneSplitListener() {
        var simSplitPane = (DialogSceneComposerModel.SimSplitPane) simActor;
        var popTableClickListener = new PopTable.PopTableClickListener(skin) {
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
                splitSpinner.getTextField().addListener(main.getIbeamListener());
                splitSpinner.getButtonMinus().addListener(main.getHandListener());
                splitSpinner.getButtonPlus().addListener(main.getHandListener());
                splitSpinner.addListener(new TextTooltip("The distance in pixels that the user is allowed to scroll beyond the bounds if overscroll is enabled.", main.getTooltipManager(), skin, "scene"));
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
                splitMinSpinner.getTextField().addListener(main.getIbeamListener());
                splitMinSpinner.getButtonMinus().addListener(main.getHandListener());
                splitMinSpinner.getButtonPlus().addListener(main.getHandListener());
                splitMinSpinner.addListener(new TextTooltip("The minimum speed that scroll returns to the widget bounds when overscroll is enabled.", main.getTooltipManager(), skin, "scene"));
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
                splitMaxSpinner.getTextField().addListener(main.getIbeamListener());
                splitMaxSpinner.getButtonMinus().addListener(main.getHandListener());
                splitMaxSpinner.getButtonPlus().addListener(main.getHandListener());
                splitMaxSpinner.addListener(new TextTooltip("The maximum speed that scroll returns to the widget bounds when overscroll is enabled.", main.getTooltipManager(), skin, "scene"));
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
    
    private EventListener splitPaneResetListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to reset this SplitPane?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("RESET", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Resets the settings of the SplitPane to their defaults.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.splitPaneReset();
            }
        });
        
        return popTableClickListener;
    }
    
    private EventListener splitPaneDeleteListener() {
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to delete this ScrollPane?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("DELETE", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Removes this TouchPad from its parent.", main.getTooltipManager(), skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                events.scrollPaneDelete();
            }
        });
        
        return popTableClickListener;
    }
    
    public Dialog showConfirmCellSetWidgetDialog(WidgetType widgetType, PopTable popTable) {
        var simCell = (DialogSceneComposerModel.SimCell) simActor;
        if (simCell.child == null) {
            popTable.hide();
            events.cellSetWidget(widgetType);
            return null;
        } else {
            var dialog = new Dialog("", skin, "scene-dialog") {
                @Override
                protected void result(Object object) {
                    if ((Boolean) object) {
                        popTable.hide();
                        events.cellSetWidget(widgetType);
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
            button.addListener(main.getHandListener());
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
            textButton.addListener(main.getHandListener());
    
            textButton = new TextButton("Cancel", skin, "scene-med");
            dialog.button(textButton, false);
            textButton.addListener(main.getHandListener());
            
            dialog.key(Input.Keys.ENTER, true).key(Input.Keys.SPACE, true);
            dialog.key(Input.Keys.ESCAPE, false);
    
            dialog.show(getStage());
            dialog.setSize(500, 200);
            dialog.setPosition((int) (getStage().getWidth() / 2f - dialog.getWidth() / 2f), (int) (getStage().getHeight() / 2f - dialog.getHeight() / 2f));
    
            return dialog;
        }
    }
    
    public Dialog showConfirmContainerSetWidgetDialog(WidgetType widgetType, PopTable popTable) {
        var simContainer = (DialogSceneComposerModel.SimContainer) simActor;
        if (simContainer.child == null) {
            popTable.hide();
            events.containerSetWidget(widgetType);
            return null;
        } else {
            var dialog = new Dialog("", skin, "scene-dialog") {
                @Override
                protected void result(Object object) {
                    if ((Boolean) object) {
                        popTable.hide();
                        events.containerSetWidget(widgetType);
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
            button.addListener(main.getHandListener());
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    dialog.hide();
                }
            });
            
            root = dialog.getContentTable();
            root.pad(10);
            
            label = new Label("This will overwrite the existing widget in the container.\nAre you okay with that?", skin, "scene-label-colored");
            label.setWrap(true);
            label.setAlignment(Align.center);
            root.add(label).growX();
            
            dialog.getButtonTable().defaults().uniformX();
            var textButton = new TextButton("OK", skin, "scene-med");
            dialog.button(textButton, true);
            textButton.addListener(main.getHandListener());
            
            textButton = new TextButton("Cancel", skin, "scene-med");
            dialog.button(textButton, false);
            textButton.addListener(main.getHandListener());
            
            dialog.key(Input.Keys.ENTER, true).key(Input.Keys.SPACE, true);
            dialog.key(Input.Keys.ESCAPE, false);
            
            dialog.show(getStage());
            dialog.setSize(500, 200);
            dialog.setPosition((int) (getStage().getWidth() / 2f - dialog.getWidth() / 2f), (int) (getStage().getHeight() / 2f - dialog.getHeight() / 2f));
            
            return dialog;
        }
    }
    
    public Dialog showConfirmScrollPaneSetWidgetDialog(WidgetType widgetType, PopTable popTable) {
        var simScrollPane = (DialogSceneComposerModel.SimScrollPane) simActor;
        if (simScrollPane.child == null) {
            popTable.hide();
            events.scrollPaneSetWidget(widgetType);
            return null;
        } else {
            var dialog = new Dialog("", skin, "scene-dialog") {
                @Override
                protected void result(Object object) {
                    if ((Boolean) object) {
                        popTable.hide();
                        events.scrollPaneSetWidget(widgetType);
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
            button.addListener(main.getHandListener());
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    dialog.hide();
                }
            });
            
            root = dialog.getContentTable();
            root.pad(10);
            
            label = new Label("This will overwrite the existing widget in the ScrollPane.\nAre you okay with that?", skin, "scene-label-colored");
            label.setWrap(true);
            label.setAlignment(Align.center);
            root.add(label).growX();
            
            dialog.getButtonTable().defaults().uniformX();
            var textButton = new TextButton("OK", skin, "scene-med");
            dialog.button(textButton, true);
            textButton.addListener(main.getHandListener());
            
            textButton = new TextButton("Cancel", skin, "scene-med");
            dialog.button(textButton, false);
            textButton.addListener(main.getHandListener());
            
            dialog.key(Input.Keys.ENTER, true).key(Input.Keys.SPACE, true);
            dialog.key(Input.Keys.ESCAPE, false);
            
            dialog.show(getStage());
            dialog.setSize(500, 200);
            dialog.setPosition((int) (getStage().getWidth() / 2f - dialog.getWidth() / 2f), (int) (getStage().getHeight() / 2f - dialog.getHeight() / 2f));
            
            return dialog;
        }
    }
    
    public Dialog showConfirmSplitPaneSetWidgetDialog(WidgetType widgetType, PopTable popTable, boolean firstWidget) {
        var simSplitPane = (SimSplitPane) simActor;
        if (firstWidget && simSplitPane.childFirst == null) {
            popTable.hide();
            events.splitPaneChildFirst(widgetType);
            return null;
        } else if (!firstWidget && simSplitPane.childSecond == null){
            popTable.hide();
            events.splitPaneChildSecond(widgetType);
            return null;
        } else {
            var dialog = new Dialog("", skin, "scene-dialog") {
                @Override
                protected void result(Object object) {
                    if ((Boolean) object) {
                        popTable.hide();
                        if (firstWidget) {
                            events.splitPaneChildFirst(widgetType);
                        } else {
                            events.splitPaneChildSecond(widgetType);
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
            button.addListener(main.getHandListener());
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
            textButton.addListener(main.getHandListener());
            
            textButton = new TextButton("Cancel", skin, "scene-med");
            dialog.button(textButton, false);
            textButton.addListener(main.getHandListener());
            
            dialog.key(Input.Keys.ENTER, true).key(Input.Keys.SPACE, true);
            dialog.key(Input.Keys.ESCAPE, false);
            
            dialog.show(getStage());
            dialog.setSize(500, 200);
            dialog.setPosition((int) (getStage().getWidth() / 2f - dialog.getWidth() / 2f), (int) (getStage().getHeight() / 2f - dialog.getHeight() / 2f));
            
            return dialog;
        }
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
    
        var popTableClickListener = new PopTable.PopTableClickListener(skin);
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
                events.dialogExportSaveTemplate(new FileHandle(file));
    
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
                events.dialogExportSaveJava(new FileHandle(file));
                
                if (file != null) {
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
        
        var textField = new TextField("", skin, "scene");
        var keyboardFocus = textField;
        table.add(textField).width(300).uniformX();
        textField.addListener(main.getIbeamListener());
    
        table.row();
        label = new Label("Class", skin, "scene-label-colored");
        table.add(label);
    
        textField = new TextField("", skin, "scene");
        table.add(textField).uniformX().fillX();
        textField.addListener(main.getIbeamListener());
    
        table.row();
        label = new Label("Skin Path", skin, "scene-label-colored");
        table.add(label);
    
        textField = new TextField("", skin, "scene");
        table.add(textField).uniformX().fillX();
        textField.addListener(main.getIbeamListener());
    
        table.row();
        label = new Label("Background Color", skin, "scene-label-colored");
        table.add(label);
    
        var imageButton = new ImageButton(new ImageButton.ImageButtonStyle(skin.get("scene-color", ImageButton.ImageButtonStyle.class)));
        table.add(imageButton).left();
        imageButton.addListener(main.getHandListener());
        
        dialog.getContentTable().row();
        table = new Table();
        root.add(table);
        
        dialog.show(getStage());
        
        getStage().setKeyboardFocus(keyboardFocus);
        
        return dialog;
    }
}