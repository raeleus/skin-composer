package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.ray3k.skincomposer.dialog.DialogSceneComposer;

public class TableAlignmentUndoable implements SceneComposerUndoable {
    private Table table;
    private int alignment;
    private int previousAlignment;
    
    public TableAlignmentUndoable(int alignment) {
        this.alignment = alignment;
        var dialog = DialogSceneComposer.dialog;
        table = (Table) dialog.selectedObject;
        previousAlignment = table.getAlign();
    }
    
    @Override
    public void undo() {
        table.align(previousAlignment);
    }
    
    @Override
    public void redo() {
        table.align(alignment);
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Table Alignment: " + Align.toString(alignment) + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Table Alignment: " + Align.toString(alignment) + "\"";
    }
}
