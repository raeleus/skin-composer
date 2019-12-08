package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class HorizontalGroupPadTopUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimHorizontalGroup horizontalGroup;
    private DialogSceneComposer dialog;
    private float padTop;
    private float previousPadTop;
    
    public HorizontalGroupPadTopUndoable(float padTop) {
        this.padTop = padTop;
        dialog = DialogSceneComposer.dialog;
        horizontalGroup = (DialogSceneComposerModel.SimHorizontalGroup) dialog.simActor;

        previousPadTop = horizontalGroup.padTop;
    }
    
    @Override
    public void undo() {
        horizontalGroup.padTop = previousPadTop;
        
        if (dialog.simActor != horizontalGroup) {
            dialog.simActor = horizontalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {

        horizontalGroup.padTop = padTop;
        
        if (dialog.simActor != horizontalGroup) {
            dialog.simActor = horizontalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"HorizontalGroup pad top " + padTop + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"HorizontalGroup pad top " + padTop + "\"";
    }
}
