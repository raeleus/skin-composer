package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimSelectBox;

public class SelectBoxVisibleUndoable implements SceneComposerUndoable {
    private SimSelectBox selectBox;
    private DialogSceneComposer dialog;
    private boolean visible;
    private boolean previousVisible;
    
    public SelectBoxVisibleUndoable(boolean visible) {
        this.visible = visible;
        dialog = DialogSceneComposer.dialog;
        selectBox = (SimSelectBox) dialog.simActor;
        previousVisible = selectBox.visible;
    }
    
    @Override
    public void undo() {
        selectBox.visible = previousVisible;
        
        if (dialog.simActor != selectBox) {
            dialog.simActor = selectBox;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        selectBox.visible = visible;
        
        if (dialog.simActor != selectBox) {
            dialog.simActor = selectBox;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"SelectBox visible " + visible + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"SelectBox visible " + visible + "\"";
    }
}
