package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimHorizontalGroup;

public class HorizontalGroupVisibleUndoable implements SceneComposerUndoable {
    private SimHorizontalGroup horizontalGroup;
    private DialogSceneComposer dialog;
    private boolean visible;
    private boolean previousVisible;
    
    public HorizontalGroupVisibleUndoable(boolean visible) {
        this.visible = visible;
        dialog = DialogSceneComposer.dialog;
        horizontalGroup = (SimHorizontalGroup) dialog.simActor;
        previousVisible = horizontalGroup.visible;
    }
    
    @Override
    public void undo() {
        horizontalGroup.visible = previousVisible;
        
        if (dialog.simActor != horizontalGroup) {
            dialog.simActor = horizontalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        horizontalGroup.visible = visible;
        
        if (dialog.simActor != horizontalGroup) {
            dialog.simActor = horizontalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"HorizontalGroup visible " + visible + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"HorizontalGroup visible " + visible + "\"";
    }
}
