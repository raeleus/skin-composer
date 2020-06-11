package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimProgressBar;

public class ProgressBarVisibleUndoable implements SceneComposerUndoable {
    private SimProgressBar progressBar;
    private DialogSceneComposer dialog;
    private boolean visible;
    private boolean previousVisible;
    
    public ProgressBarVisibleUndoable(boolean visible) {
        this.visible = visible;
        dialog = DialogSceneComposer.dialog;
        progressBar = (SimProgressBar) dialog.simActor;
        previousVisible = progressBar.visible;
    }
    
    @Override
    public void undo() {
        progressBar.visible = previousVisible;
        
        if (dialog.simActor != progressBar) {
            dialog.simActor = progressBar;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        progressBar.visible = visible;
        
        if (dialog.simActor != progressBar) {
            dialog.simActor = progressBar;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ProgressBar visible " + visible + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ProgressBar visible " + visible + "\"";
    }
}
