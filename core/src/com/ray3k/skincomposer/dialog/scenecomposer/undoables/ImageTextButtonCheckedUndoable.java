package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ImageTextButtonCheckedUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimImageTextButton button;
    private DialogSceneComposer dialog;
    private boolean checked;
    private boolean previousChecked;
    
    public ImageTextButtonCheckedUndoable(boolean checked) {
        this.checked = checked;
        dialog = DialogSceneComposer.dialog;
        button = (DialogSceneComposerModel.SimImageTextButton) dialog.simActor;
        previousChecked = button.checked;
    }
    
    @Override
    public void undo() {
        button.checked = previousChecked;
        
        if (dialog.simActor != button) {
            dialog.simActor = button;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        button.checked = checked;
        
        if (dialog.simActor != button) {
            dialog.simActor = button;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ImageTextButton checked " + checked + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ImageTextButton checked " + checked + "\"";
    }
}
