package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ScrollPaneFlingTimeUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimScrollPane scrollPane;
    private DialogSceneComposer dialog;
    private float flingTime;
    private float previousFlingTime;
    
    public ScrollPaneFlingTimeUndoable(float flingTime) {
        this.flingTime = flingTime;
        dialog = DialogSceneComposer.dialog;
        scrollPane = (DialogSceneComposerModel.SimScrollPane) dialog.simActor;
        previousFlingTime = scrollPane.flingTime;
    }
    
    @Override
    public void undo() {
        scrollPane.flingTime = previousFlingTime;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        scrollPane.flingTime = flingTime;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ScrollPane fling time " + flingTime + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ScrollPane fling time " + flingTime + "\"";
    }
}
