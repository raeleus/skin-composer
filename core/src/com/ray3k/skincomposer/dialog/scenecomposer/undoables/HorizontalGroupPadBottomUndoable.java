package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class HorizontalGroupPadBottomUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimHorizontalGroup horizontalGroup;
    private DialogSceneComposer dialog;
    private float padBottom;
    private float previousPadBottom;
    
    public HorizontalGroupPadBottomUndoable(float padBottom) {
        this.padBottom = padBottom;
        dialog = DialogSceneComposer.dialog;
        horizontalGroup = (DialogSceneComposerModel.SimHorizontalGroup) dialog.simActor;

        previousPadBottom = horizontalGroup.padBottom;
    }
    
    @Override
    public void undo() {
        horizontalGroup.padBottom = previousPadBottom;
        
        if (dialog.simActor != horizontalGroup) {
            dialog.simActor = horizontalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {

        horizontalGroup.padBottom = padBottom;
        
        if (dialog.simActor != horizontalGroup) {
            dialog.simActor = horizontalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"HorizontalGroup pad bottom " + padBottom + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"HorizontalGroup pad bottom " + padBottom + "\"";
    }
}
