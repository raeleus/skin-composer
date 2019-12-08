package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class VerticalGroupPadBottomUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimVerticalGroup verticalGroup;
    private DialogSceneComposer dialog;
    private float padBottom;
    private float previousPadBottom;
    
    public VerticalGroupPadBottomUndoable(float padBottom) {
        this.padBottom = padBottom;
        dialog = DialogSceneComposer.dialog;
        verticalGroup = (DialogSceneComposerModel.SimVerticalGroup) dialog.simActor;

        previousPadBottom = verticalGroup.padBottom;
    }
    
    @Override
    public void undo() {
        verticalGroup.padBottom = previousPadBottom;
        
        if (dialog.simActor != verticalGroup) {
            dialog.simActor = verticalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {

        verticalGroup.padBottom = padBottom;
        
        if (dialog.simActor != verticalGroup) {
            dialog.simActor = verticalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"VerticalGroup pad bottom " + padBottom + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"VerticalGroup pad bottom " + padBottom + "\"";
    }
}
