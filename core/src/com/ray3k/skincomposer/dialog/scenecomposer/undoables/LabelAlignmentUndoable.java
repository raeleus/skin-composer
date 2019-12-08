package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class LabelAlignmentUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimLabel label;
    private DialogSceneComposer dialog;
    private int alignment;
    private int previousAlignment;
    
    public LabelAlignmentUndoable(int alignment) {
        this.alignment = alignment;
        dialog = DialogSceneComposer.dialog;
        label = (DialogSceneComposerModel.SimLabel) dialog.simActor;
        
        previousAlignment = label.textAlignment;
    }
    
    @Override
    public void undo() {
        label.textAlignment = previousAlignment;
        
        if (dialog.simActor != label) {
            dialog.simActor = label;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        label.textAlignment = alignment;
        
        if (dialog.simActor != label) {
            dialog.simActor = label;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Label text alignment\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Label text alignment\"";
    }
}
