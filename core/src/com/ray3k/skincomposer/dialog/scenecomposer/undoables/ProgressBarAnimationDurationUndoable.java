package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ProgressBarAnimationDurationUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimProgressBar progressBar;
    private DialogSceneComposer dialog;
    private float animationDuration;
    private float previousAnimationDuration;
    
    public ProgressBarAnimationDurationUndoable(float animationDuration) {
        this.animationDuration = animationDuration;
        dialog = DialogSceneComposer.dialog;
        progressBar = (DialogSceneComposerModel.SimProgressBar) dialog.simActor;
        previousAnimationDuration = progressBar.animationDuration;
    }
    
    @Override
    public void undo() {
        progressBar.animationDuration = previousAnimationDuration;
        
        if (dialog.simActor != progressBar) {
            dialog.simActor = progressBar;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        progressBar.animationDuration = animationDuration;
        
        if (dialog.simActor != progressBar) {
            dialog.simActor = progressBar;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ProgressBar animation duration " + animationDuration + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ProgressBar animation duration " + animationDuration + "\"";
    }
}
