package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimList;

public class ListVisibleUndoable implements SceneComposerUndoable {
    private SimList list;
    private DialogSceneComposer dialog;
    private boolean visible;
    private boolean previousVisible;
    
    public ListVisibleUndoable(boolean visible) {
        this.visible = visible;
        dialog = DialogSceneComposer.dialog;
        list = (SimList) dialog.simActor;
        previousVisible = list.visible;
    }
    
    @Override
    public void undo() {
        list.visible = previousVisible;
        
        if (dialog.simActor != list) {
            dialog.simActor = list;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        list.visible = visible;
        
        if (dialog.simActor != list) {
            dialog.simActor = list;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"List visible " + visible + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"List visible " + visible + "\"";
    }
}
