package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ProgressBarVerticalUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimProgressBar progressBar;
    private DialogSceneComposer dialog;
    private boolean vertical;
    private boolean previousVertical;
    
    public ProgressBarVerticalUndoable(boolean vertical) {
        this.vertical = vertical;
        dialog = DialogSceneComposer.dialog;
        progressBar = (DialogSceneComposerModel.SimProgressBar) dialog.simActor;
        previousVertical = progressBar.vertical;
    }
    
    @Override
    public void undo() {
        progressBar.vertical = previousVertical;
        
        if (dialog.simActor != progressBar) {
            dialog.simActor = progressBar;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        progressBar.vertical = vertical;
        
        if (dialog.simActor != progressBar) {
            dialog.simActor = progressBar;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ProgressBar " + (vertical ? "vertical" : "horizontal") + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ProgressBar " + (vertical ? "vertical" : "horizontal") + "\"";
    }
}
