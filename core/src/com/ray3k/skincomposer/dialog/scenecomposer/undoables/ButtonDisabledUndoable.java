package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ButtonDisabledUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimButton button;
    private DialogSceneComposer dialog;
    private boolean disabled;
    private boolean previousDisabled;
    
    public ButtonDisabledUndoable(boolean disabled) {
        this.disabled = disabled;
        dialog = DialogSceneComposer.dialog;
        button = (DialogSceneComposerModel.SimButton) dialog.simActor;
        previousDisabled = button.disabled;
    }
    
    @Override
    public void undo() {
        button.disabled = previousDisabled;
        
        if (dialog.simActor != button) {
            dialog.simActor = button;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        button.disabled = disabled;
        
        if (dialog.simActor != button) {
            dialog.simActor = button;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Button disabled " + disabled + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Button disabled " + disabled + "\"";
    }
}
