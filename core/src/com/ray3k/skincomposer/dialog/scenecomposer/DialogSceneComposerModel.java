package com.ray3k.skincomposer.dialog.scenecomposer;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.undoables.SceneComposerUndoable;

public class DialogSceneComposerModel {
    private DialogSceneComposer dialog;
    private Main main;
    public Array<SceneComposerUndoable> undoables;
    public Array<SceneComposerUndoable> redoables;
    public SimGroup root;
    public Group preview;
    
    public DialogSceneComposerModel(DialogSceneComposer dialog) {
        this.dialog = dialog;
        undoables = new Array<>();
        redoables = new Array<>();
        root = new SimGroup();
        preview = new Group();
        main = Main.main;
    }
    
    public void undo() {
        if (undoables.size > 0) {
            var undoable = undoables.pop();
            redoables.add(undoable);
            
            undoable.undo();
        }
    }
    
    public void redo() {
        if (redoables.size > 0) {
            var undoable = redoables.pop();
            undoables.add(undoable);
    
            undoable.redo();
        }
    }
    
    public void updatePreview() {
        preview.clearChildren();
        
        for (var simActor : root.children) {
            var actor = createPreviewWidget(simActor);
            if (actor != null) {
                preview.addActor(actor);
            }
        }
//        preview.debugAll();
    }
    
    private Actor createPreviewWidget(SimActor simActor) {
        Actor actor = null;
    
        if (simActor instanceof SimTable) {
            var simTable = (SimTable) simActor;
            var table = new Table();
            actor = table;
        
            table.setName(simTable.name);
        
            if (simTable.background != null) {
                table.setBackground(main.getAtlasData().getDrawablePairs().get(simTable.background));
            }
        
            if (simTable.color != null) {
                table.setColor(simTable.color.color);
            }
            
            table.pad(simTable.padTop, simTable.padLeft, simTable.padBottom, simTable.padRight);
            
            table.align(simTable.alignment);
            
            if (simTable.parent == root) {
                table.setFillParent(true);
            }
            
            int row = 0;
            for (var simCell : simTable.cells) {
                if (simCell.row > row) {
                    table.row();
                    row = simCell.row;
                }
                
                var child = createPreviewWidget(simCell.child);
                var cell = table.add(child).fill(simCell.fillX, simCell.fillY).expand(simCell.expandX, simCell.expandY);
                cell.pad(simCell.padTop, simCell.padLeft, simCell.padBottom, simCell.padRight);
                cell.space(simCell.spaceTop, simCell.spaceLeft, simCell.spaceBottom, simCell.spaceRight);
                cell.align(simCell.alignment).uniform(simCell.uniformX, simCell.uniformY).colspan(simCell.colSpan);
    
                if (simCell.minWidth >= 0) {
                    cell.minWidth(simCell.minWidth);
                }
    
                if (simCell.minHeight >= 0) {
                    cell.minWidth(simCell.minHeight);
                }
    
                if (simCell.maxWidth >= 0) {
                    cell.maxWidth(simCell.maxWidth);
                }
    
                if (simCell.maxHeight >= 0) {
                    cell.maxWidth(simCell.maxHeight);
                }
    
                if (simCell.preferredWidth >= 0) {
                    cell.prefWidth(simCell.preferredWidth);
                }
    
                if (simCell.preferredHeight >= 0) {
                    cell.prefHeight(simCell.preferredHeight);
                }
            }
        } else if (simActor instanceof SimTextButton) {
            var simTextButton = (SimTextButton) simActor;
            if (simTextButton.style != null && simTextButton.style.hasMandatoryFields()) {
                var style = main.getRootTable().createPreviewStyle(TextButton.TextButtonStyle.class, simTextButton.style);
                var textButton = new TextButton(simTextButton.text == null ? "" : simTextButton.text, style);
                textButton.addListener(main.getHandListener());
                actor = textButton;
            }
        }
        
        return actor;
    }
    
    public static class SimActor {
        public SimActor parent;
    }
    
    public static class SimGroup extends SimActor {
        public Array<SimActor> children = new Array<>();
    
        @Override
        public String toString() {
            return "Group";
        }
        
        public void reset() {
            children.clear();
        }
    }
    
    public static class SimTable extends SimActor {
        public Array<SimCell> cells = new Array<>();
        public String name;
        public DrawableData background;
        public ColorData color;
        public float padLeft;
        public float padRight;
        public float padTop;
        public float padBottom;
        public int alignment = Align.center;
    
        @Override
        public String toString() {
            return name == null ? "Table" : name + " (Table)";
        }
    
        public void reset() {
            cells.clear();
            name = null;
            background = null;
            color = null;
            padLeft = 0;
            padRight = 0;
            padTop = 0;
            padBottom = 0;
            alignment = Align.center;
        }
        
        public void sort() {
            cells.sort((o1, o2) -> {
                if (o2.row < o1.row) {
                    return 1;
                } else if (o2.row > o1.row) {
                    return -1;
                } else if (o2.column < o1.column) {
                    return 1;
                } else if (o2.column > o1.column) {
                    return -1;
                } else {
                    return 0;
                }
            });
        }
    }
    
    public static class SimCell extends SimActor {
        public SimActor child;
        public int row;
        public int column;
        public float padLeft;
        public float padRight;
        public float padTop;
        public float padBottom;
        public float spaceLeft;
        public float spaceRight;
        public float spaceTop;
        public float spaceBottom;
        public boolean expandX;
        public boolean expandY;
        public boolean fillX;
        public boolean fillY;
        public boolean growX;
        public boolean growY;
        public int alignment = Align.center;
        public float minWidth = -1;
        public float minHeight = -1;
        public float maxWidth = -1;
        public float maxHeight = -1;
        public float preferredWidth = -1;
        public float preferredHeight = -1;
        public boolean uniformX;
        public boolean uniformY;
        public int colSpan = 1;
    
        @Override
        public String toString() {
            return "Cell (" + column + "," + row + ")";
        }
        
        public void reset() {
            child = null;
            row = 0;
            column = 0;
            padLeft = 0;
            padRight = 0;
            padTop = 0;
            padBottom = 0;
            spaceLeft = 0;
            spaceRight = 0;
            spaceTop = 0;
            spaceBottom = 0;
            expandX = false;
            expandY = false;
            fillX = false;
            fillY = false;
            growX = false;
            growY = false;
            alignment = Align.center;
            minWidth = -1;
            minHeight = -1;
            maxWidth = -1;
            maxHeight = -1;
            preferredWidth = -1;
            preferredHeight = -1;
            uniformX = false;
            uniformY = false;
            colSpan = 1;
        }
    }
    
    public static class SimTextButton extends SimActor {
        public String name;
        public String text;
        public StyleData style;
        public boolean checked;
        public boolean disabled;
        public ColorData color;
        public float padLeft;
        public float padRight;
        public float padTop;
        public float padBottom;
        public int alignment = Align.center;
    
        public SimTextButton() {
            var styles = Main.main.getJsonData().getClassStyleMap().get(TextButton.class);
            for (var style : styles) {
                if (style.name.equals("default")) {
                    if (style.hasMandatoryFields() && !style.hasAllNullFields()) {
                        this.style = style;
                    }
                }
            }
        }
    
        @Override
        public String toString() {
            return name == null ? "TextButton" : name + " (TextButton)";
        }
        
        public void reset() {
            name = null;
            text = null;
            style = null;
            checked = false;
            disabled = false;
            color = null;
            padLeft = 0;
            padRight = 0;
            padTop = 0;
            padBottom = 0;
            alignment = Align.center;
        }
    }
}
