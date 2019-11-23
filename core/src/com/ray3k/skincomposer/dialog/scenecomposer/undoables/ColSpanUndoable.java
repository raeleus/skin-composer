package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ColSpanUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimCell cell;
    private DialogSceneComposer dialog;
    private int colSpan;
    private int previousColSpan;
    
    public ColSpanUndoable(int colSpan) {
        this.colSpan = colSpan;
        dialog = DialogSceneComposer.dialog;
        cell = (DialogSceneComposerModel.SimCell) dialog.simActor;
        
        previousColSpan = cell.colSpan;
    }
    
    @Override
    public void undo() {
        cell.colSpan = previousColSpan;
        
        if (dialog.simActor != cell) {
            dialog.simActor = cell;
            dialog.populateProperties();
        }
        dialog.populatePath();
    }
    
    @Override
    public void redo() {
        cell.colSpan = colSpan;
        
        if (dialog.simActor != cell) {
            dialog.simActor = cell;
            dialog.populateProperties();
        }
        dialog.populatePath();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Column Span\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Column Span\"";
    }
}
