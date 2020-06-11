package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimTable;

public class TableVisibleUndoable implements SceneComposerUndoable {
    private SimTable table;
    private DialogSceneComposer dialog;
    private boolean visible;
    private boolean previousVisible;
    
    public TableVisibleUndoable(boolean visible) {
        this.visible = visible;
        dialog = DialogSceneComposer.dialog;
        table = (SimTable) dialog.simActor;
        previousVisible = table.visible;
    }
    
    @Override
    public void undo() {
        table.visible = previousVisible;
        
        if (dialog.simActor != table) {
            dialog.simActor = table;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        table.visible = visible;
        
        if (dialog.simActor != table) {
            dialog.simActor = table;
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
