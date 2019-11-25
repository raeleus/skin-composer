package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class CellAlignmentUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimCell cell;
    private DialogSceneComposer dialog;
    private int alignment;
    private int previousAlignment;
    
    public CellAlignmentUndoable(int alignment) {
        this.alignment = alignment;
        dialog = DialogSceneComposer.dialog;
        cell = (DialogSceneComposerModel.SimCell) dialog.simActor;
        
        previousAlignment = cell.alignment;
    }
    
    @Override
    public void undo() {
        cell.alignment = previousAlignment;
        
        if (dialog.simActor != cell) {
            dialog.simActor = cell;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        cell.alignment = alignment;
        
        if (dialog.simActor != cell) {
            dialog.simActor = cell;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Cell Alignment\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Cell Alignment\"";
    }
}
