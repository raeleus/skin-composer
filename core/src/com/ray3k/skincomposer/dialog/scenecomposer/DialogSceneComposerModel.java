package com.ray3k.skincomposer.dialog.scenecomposer;

import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.undoables.SceneComposerUndoable;

import java.util.Comparator;

public class DialogSceneComposerModel {
    private DialogSceneComposer dialog;
    public Array<SceneComposerUndoable> undoables;
    public Array<SceneComposerUndoable> redoables;
    public SimGroup root;
    
    public DialogSceneComposerModel(DialogSceneComposer dialog) {
        this.dialog = dialog;
        undoables = new Array<>();
        redoables = new Array<>();
        root = new SimGroup();
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
            cells.sort(new Comparator<SimCell>() {
                @Override
                public int compare(SimCell o1, SimCell o2) {
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
                }
            });
            System.out.println(cells);
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
        public float minWidth;
        public float minHeight;
        public float maxWidth;
        public float maxHeight;
        public float preferredWidth;
        public float preferredHeight;
        public boolean uniformX;
        public boolean uniformY;
    
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
            minWidth = 0;
            minHeight = 0;
            maxWidth = 0;
            maxHeight = 0;
            preferredWidth = 0;
            preferredHeight = 0;
            uniformX = false;
            uniformY = false;
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
