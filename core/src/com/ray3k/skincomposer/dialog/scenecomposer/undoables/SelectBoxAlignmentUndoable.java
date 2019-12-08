package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class SelectBoxAlignmentUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimSelectBox selectBox;
    private DialogSceneComposer dialog;
    private int alignment;
    private int previousAlignment;
    
    public SelectBoxAlignmentUndoable(int alignment) {
        this.alignment = alignment;
        dialog = DialogSceneComposer.dialog;
        selectBox = (DialogSceneComposerModel.SimSelectBox) dialog.simActor;
        
        previousAlignment = selectBox.alignment;
    }
    
    @Override
    public void undo() {
        selectBox.alignment = previousAlignment;
        
        if (dialog.simActor != selectBox) {
            dialog.simActor = selectBox;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        selectBox.alignment = alignment;
        
        if (dialog.simActor != selectBox) {
            dialog.simActor = selectBox;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"SelectBox alignment\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"SelectBox alignment\"";
    }
}
