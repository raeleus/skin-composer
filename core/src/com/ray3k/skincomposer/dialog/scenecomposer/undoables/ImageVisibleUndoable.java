package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimImage;

public class ImageVisibleUndoable implements SceneComposerUndoable {
    private SimImage image;
    private DialogSceneComposer dialog;
    private boolean visible;
    private boolean previousVisible;
    
    public ImageVisibleUndoable(boolean visible) {
        this.visible = visible;
        dialog = DialogSceneComposer.dialog;
        image = (SimImage) dialog.simActor;
        previousVisible = image.visible;
    }
    
    @Override
    public void undo() {
        image.visible = previousVisible;
        
        if (dialog.simActor != image) {
            dialog.simActor = image;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        image.visible = visible;
        
        if (dialog.simActor != image) {
            dialog.simActor = image;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Image visible " + visible + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Image visible " + visible + "\"";
    }
}
