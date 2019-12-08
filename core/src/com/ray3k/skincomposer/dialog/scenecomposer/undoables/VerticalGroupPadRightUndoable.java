package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class VerticalGroupPadRightUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimVerticalGroup verticalGroup;
    private DialogSceneComposer dialog;
    private float padRight;
    private float previousPadRight;
    
    public VerticalGroupPadRightUndoable(float padRight) {
        this.padRight = padRight;
        dialog = DialogSceneComposer.dialog;
        verticalGroup = (DialogSceneComposerModel.SimVerticalGroup) dialog.simActor;

        previousPadRight = verticalGroup.padRight;
    }
    
    @Override
    public void undo() {
        verticalGroup.padRight = previousPadRight;
        
        if (dialog.simActor != verticalGroup) {
            dialog.simActor = verticalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {

        verticalGroup.padRight = padRight;
        
        if (dialog.simActor != verticalGroup) {
            dialog.simActor = verticalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"VerticalGroup pad right " + padRight + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"VerticalGroup pad right " + padRight + "\"";
    }
}
