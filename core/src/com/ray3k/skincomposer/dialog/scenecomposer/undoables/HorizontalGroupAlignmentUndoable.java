package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class HorizontalGroupAlignmentUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimHorizontalGroup horizontalGroup;
    private DialogSceneComposer dialog;
    private int alignment;
    private int previousAlignment;
    
    public HorizontalGroupAlignmentUndoable(int alignment) {
        this.alignment = alignment;
        dialog = DialogSceneComposer.dialog;
        horizontalGroup = (DialogSceneComposerModel.SimHorizontalGroup) dialog.simActor;
        
        previousAlignment = horizontalGroup.alignment;
    }
    
    @Override
    public void undo() {
        horizontalGroup.alignment = previousAlignment;
        
        if (dialog.simActor != horizontalGroup) {
            dialog.simActor = horizontalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        horizontalGroup.alignment = alignment;
        
        if (dialog.simActor != horizontalGroup) {
            dialog.simActor = horizontalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"HorizontalGroup alignment\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"HorizontalGroup alignment\"";
    }
}
