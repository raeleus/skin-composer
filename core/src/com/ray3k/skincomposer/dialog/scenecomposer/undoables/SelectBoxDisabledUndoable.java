package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class SelectBoxDisabledUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimSelectBox selectBox;
    private DialogSceneComposer dialog;
    private boolean disabled;
    private boolean previousDisabled;
    
    public SelectBoxDisabledUndoable(boolean disabled) {
        this.disabled = disabled;
        dialog = DialogSceneComposer.dialog;
        selectBox = (DialogSceneComposerModel.SimSelectBox) dialog.simActor;
        previousDisabled = selectBox.disabled;
    }
    
    @Override
    public void undo() {
        selectBox.disabled = previousDisabled;
        
        if (dialog.simActor != selectBox) {
            dialog.simActor = selectBox;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        selectBox.disabled = disabled;
        
        if (dialog.simActor != selectBox) {
            dialog.simActor = selectBox;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"SelectBox disabled " + disabled + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"SelectBox disabled " + disabled + "\"";
    }
}
