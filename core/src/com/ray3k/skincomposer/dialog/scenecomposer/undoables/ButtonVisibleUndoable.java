package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimButton;

public class ButtonVisibleUndoable implements SceneComposerUndoable {
    private SimButton button;
    private DialogSceneComposer dialog;
    private boolean visible;
    private boolean previousVisible;
    
    public ButtonVisibleUndoable(boolean visible) {
        this.visible = visible;
        dialog = DialogSceneComposer.dialog;
        button = (DialogSceneComposerModel.SimButton) dialog.simActor;
        previousVisible = button.visible;
    }
    
    @Override
    public void undo() {
        button.visible = previousVisible;
        
        if (dialog.simActor != button) {
            dialog.simActor = button;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        button.visible = visible;
        
        if (dialog.simActor != button) {
            dialog.simActor = button;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Button visible " + visible + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Button visible " + visible + "\"";
    }
}
