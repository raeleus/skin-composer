package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ProgressBarIncrementUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimProgressBar progressBar;
    private DialogSceneComposer dialog;
    private float increment;
    private float previousIncrement;
    
    public ProgressBarIncrementUndoable(float increment) {
        this.increment = increment;
        dialog = DialogSceneComposer.dialog;
        progressBar = (DialogSceneComposerModel.SimProgressBar) dialog.simActor;
        previousIncrement = progressBar.increment;
    }
    
    @Override
    public void undo() {
        progressBar.increment = previousIncrement;
        
        if (dialog.simActor != progressBar) {
            dialog.simActor = progressBar;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        progressBar.increment = increment;
        
        if (dialog.simActor != progressBar) {
            dialog.simActor = progressBar;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ProgressBar increment " + increment + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ProgressBar increment " + increment + "\"";
    }
}
