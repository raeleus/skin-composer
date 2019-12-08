package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ProgressBarVisualInterpolationUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimProgressBar progressBar;
    private DialogSceneComposer dialog;
    private DialogSceneComposerModel.Interpol visualInterpolation;
    private DialogSceneComposerModel.Interpol previousVisualInterpolation;
    
    public ProgressBarVisualInterpolationUndoable(DialogSceneComposerModel.Interpol visualInterpolation) {
        this.visualInterpolation = visualInterpolation;
        dialog = DialogSceneComposer.dialog;
        progressBar = (DialogSceneComposerModel.SimProgressBar) dialog.simActor;
        previousVisualInterpolation = progressBar.visualInterpolation;
    }
    
    @Override
    public void undo() {
        progressBar.visualInterpolation = previousVisualInterpolation;
        
        if (dialog.simActor != progressBar) {
            dialog.simActor = progressBar;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        progressBar.visualInterpolation = visualInterpolation;
        
        if (dialog.simActor != progressBar) {
            dialog.simActor = progressBar;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ProgressBar visual interpolation " + visualInterpolation + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ProgressBar visual interpolation " + visualInterpolation + "\"";
    }
}
