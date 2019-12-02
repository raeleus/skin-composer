package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ImageButtonCheckedUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimImageButton button;
    private DialogSceneComposer dialog;
    private boolean checked;
    private boolean previousChecked;
    
    public ImageButtonCheckedUndoable(boolean checked) {
        this.checked = checked;
        dialog = DialogSceneComposer.dialog;
        button = (DialogSceneComposerModel.SimImageButton) dialog.simActor;
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
        return "Redo \"ImageButton checked " + checked + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ImageButton checked " + checked + "\"";
    }
}
