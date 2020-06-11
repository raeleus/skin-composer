package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimImageButton;

public class ImageButtonVisibleUndoable implements SceneComposerUndoable {
    private SimImageButton imageButton;
    private DialogSceneComposer dialog;
    private boolean visible;
    private boolean previousVisible;
    
    public ImageButtonVisibleUndoable(boolean visible) {
        this.visible = visible;
        dialog = DialogSceneComposer.dialog;
        imageButton = (SimImageButton) dialog.simActor;
        previousVisible = imageButton.visible;
    }
    
    @Override
    public void undo() {
        imageButton.visible = previousVisible;
        
        if (dialog.simActor != imageButton) {
            dialog.simActor = imageButton;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        imageButton.visible = visible;
        
        if (dialog.simActor != imageButton) {
            dialog.simActor = imageButton;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ImageButton visible " + visible + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ImageButton visible " + visible + "\"";
    }
}
