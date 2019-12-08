package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class VerticalGroupPadLeftUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimVerticalGroup verticalGroup;
    private DialogSceneComposer dialog;
    private float padLeft;
    private float previousPadLeft;
    
    public VerticalGroupPadLeftUndoable(float padLeft) {
        this.padLeft = padLeft;
        dialog = DialogSceneComposer.dialog;
        verticalGroup = (DialogSceneComposerModel.SimVerticalGroup) dialog.simActor;

        previousPadLeft = verticalGroup.padLeft;
    }
    
    @Override
    public void undo() {
        verticalGroup.padLeft = previousPadLeft;
        
        if (dialog.simActor != verticalGroup) {
            dialog.simActor = verticalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {

        verticalGroup.padLeft = padLeft;
        
        if (dialog.simActor != verticalGroup) {
            dialog.simActor = verticalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"VerticalGroup pad left " + padLeft + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"VerticalGroup pad left " + padLeft + "\"";
    }
}
