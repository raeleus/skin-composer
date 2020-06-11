package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimVerticalGroup;

public class VerticalGroupVisibleUndoable implements SceneComposerUndoable {
    private SimVerticalGroup verticalGroup;
    private DialogSceneComposer dialog;
    private boolean visible;
    private boolean previousVisible;
    
    public VerticalGroupVisibleUndoable(boolean visible) {
        this.visible = visible;
        dialog = DialogSceneComposer.dialog;
        verticalGroup = (SimVerticalGroup) dialog.simActor;
        previousVisible = verticalGroup.visible;
    }
    
    @Override
    public void undo() {
        verticalGroup.visible = previousVisible;
        
        if (dialog.simActor != verticalGroup) {
            dialog.simActor = verticalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        verticalGroup.visible = visible;
        
        if (dialog.simActor != verticalGroup) {
            dialog.simActor = verticalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"VerticalGroup visible " + visible + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"VerticalGroup visible " + visible + "\"";
    }
}
