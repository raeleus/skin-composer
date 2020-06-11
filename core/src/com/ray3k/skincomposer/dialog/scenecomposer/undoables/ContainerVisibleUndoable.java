package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimContainer;

public class ContainerVisibleUndoable implements SceneComposerUndoable {
    private SimContainer container;
    private DialogSceneComposer dialog;
    private boolean visible;
    private boolean previousVisible;
    
    public ContainerVisibleUndoable(boolean visible) {
        this.visible = visible;
        dialog = DialogSceneComposer.dialog;
        container = (SimContainer) dialog.simActor;
        previousVisible = container.visible;
    }
    
    @Override
    public void undo() {
        container.visible = previousVisible;
        
        if (dialog.simActor != container) {
            dialog.simActor = container;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        container.visible = visible;
        
        if (dialog.simActor != container) {
            dialog.simActor = container;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Container visible " + visible + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Container visible " + visible + "\"";
    }
}
