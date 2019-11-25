package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextButtonDisabledUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextButton textButton;
    private DialogSceneComposer dialog;
    private boolean disabled;
    private boolean previousDisabled;
    
    public TextButtonDisabledUndoable(boolean disabled) {
        this.disabled = disabled;
        dialog = DialogSceneComposer.dialog;
        textButton = (DialogSceneComposerModel.SimTextButton) dialog.simActor;
        previousDisabled = textButton.disabled;
    }
    
    @Override
    public void undo() {
        textButton.disabled = previousDisabled;
        
        if (dialog.simActor != textButton) {
            dialog.simActor = textButton;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textButton.disabled = disabled;
        
        if (dialog.simActor != textButton) {
            dialog.simActor = textButton;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextButton disabled " + disabled + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextButton disabled " + disabled + "\"";
    }
}
