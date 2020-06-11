package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimImageTextButton;

public class ImageTextButtonVisibleUndoable implements SceneComposerUndoable {
    private SimImageTextButton imageTextButton;
    private DialogSceneComposer dialog;
    private boolean visible;
    private boolean previousVisible;
    
    public ImageTextButtonVisibleUndoable(boolean visible) {
        this.visible = visible;
        dialog = DialogSceneComposer.dialog;
        imageTextButton = (SimImageTextButton) dialog.simActor;
        previousVisible = imageTextButton.visible;
    }
    
    @Override
    public void undo() {
        imageTextButton.visible = previousVisible;
        
        if (dialog.simActor != imageTextButton) {
            dialog.simActor = imageTextButton;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        imageTextButton.visible = visible;
        
        if (dialog.simActor != imageTextButton) {
            dialog.simActor = imageTextButton;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ImageTextButton visible " + visible + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ImageTextButton visible " + visible + "\"";
    }
}
