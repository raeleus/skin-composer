package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ProgressBarAnimateInterpolationUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimProgressBar progressBar;
    private DialogSceneComposer dialog;
    private DialogSceneComposerModel.Interpol animateDuration;
    private DialogSceneComposerModel.Interpol previousAnimateDuration;
    
    public ProgressBarAnimateInterpolationUndoable(DialogSceneComposerModel.Interpol animateDuration) {
        this.animateDuration = animateDuration;
        dialog = DialogSceneComposer.dialog;
        progressBar = (DialogSceneComposerModel.SimProgressBar) dialog.simActor;
        previousAnimateDuration = progressBar.animateInterpolation;
    }
    
    @Override
    public void undo() {
        progressBar.animateInterpolation = previousAnimateDuration;
        
        if (dialog.simActor != progressBar) {
            dialog.simActor = progressBar;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        progressBar.animateInterpolation = animateDuration;
        
        if (dialog.simActor != progressBar) {
            dialog.simActor = progressBar;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ProgressBar animation interpolation " + animateDuration + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ProgressBar animation interpolation " + animateDuration + "\"";
    }
}
