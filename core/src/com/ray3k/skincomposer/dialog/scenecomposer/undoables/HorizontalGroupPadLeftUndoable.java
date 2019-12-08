package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class HorizontalGroupPadLeftUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimHorizontalGroup horizontalGroup;
    private DialogSceneComposer dialog;
    private float padLeft;
    private float previousPadLeft;
    
    public HorizontalGroupPadLeftUndoable(float padLeft) {
        this.padLeft = padLeft;
        dialog = DialogSceneComposer.dialog;
        horizontalGroup = (DialogSceneComposerModel.SimHorizontalGroup) dialog.simActor;

        previousPadLeft = horizontalGroup.padLeft;
    }
    
    @Override
    public void undo() {
        horizontalGroup.padLeft = previousPadLeft;
        
        if (dialog.simActor != horizontalGroup) {
            dialog.simActor = horizontalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {

        horizontalGroup.padLeft = padLeft;
        
        if (dialog.simActor != horizontalGroup) {
            dialog.simActor = horizontalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"HorizontalGroup pad left " + padLeft + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"HorizontalGroup pad left " + padLeft + "\"";
    }
}
