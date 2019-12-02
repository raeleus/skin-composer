package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ImageButtonDisabledUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimImageButton button;
    private DialogSceneComposer dialog;
    private boolean disabled;
    private boolean previousDisabled;
    
    public ImageButtonDisabledUndoable(boolean disabled) {
        this.disabled = disabled;
        dialog = DialogSceneComposer.dialog;
        button = (DialogSceneComposerModel.SimImageButton) dialog.simActor;
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
        return "Redo \"ImageButton disabled " + disabled + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ImageButton disabled " + disabled + "\"";
    }
}
