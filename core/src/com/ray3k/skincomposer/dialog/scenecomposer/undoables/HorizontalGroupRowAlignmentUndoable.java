package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class HorizontalGroupRowAlignmentUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimHorizontalGroup horizontalGroup;
    private DialogSceneComposer dialog;
    private int rowAlignment;
    private int previousRowAlignment;
    
    public HorizontalGroupRowAlignmentUndoable(int rowAlignment) {
        this.rowAlignment = rowAlignment;
        dialog = DialogSceneComposer.dialog;
        horizontalGroup = (DialogSceneComposerModel.SimHorizontalGroup) dialog.simActor;
        
        previousRowAlignment = horizontalGroup.rowAlignment;
    }
    
    @Override
    public void undo() {
        horizontalGroup.rowAlignment = previousRowAlignment;
        
        if (dialog.simActor != horizontalGroup) {
            dialog.simActor = horizontalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        horizontalGroup.rowAlignment = rowAlignment;
        
        if (dialog.simActor != horizontalGroup) {
            dialog.simActor = horizontalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"HorizontalGroup row alignment\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"HorizontalGroup row alignment\"";
    }
}
