package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimScrollPane;

public class ScrollPaneVisibleUndoable implements SceneComposerUndoable {
    private SimScrollPane scrollPane;
    private DialogSceneComposer dialog;
    private boolean visible;
    private boolean previousVisible;
    
    public ScrollPaneVisibleUndoable(boolean visible) {
        this.visible = visible;
        dialog = DialogSceneComposer.dialog;
        scrollPane = (SimScrollPane) dialog.simActor;
        previousVisible = scrollPane.visible;
    }
    
    @Override
    public void undo() {
        scrollPane.visible = previousVisible;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        scrollPane.visible = visible;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ScrollPane visible " + visible + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ScrollPane visible " + visible + "\"";
    }
}
