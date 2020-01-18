package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ProgressBarMaximumUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimProgressBar progressBar;
    private DialogSceneComposer dialog;
    private float maximum;
    private float previousMaximum;
    private float value;
    private float previousValue;
    
    public ProgressBarMaximumUndoable(float maximum) {
        this.maximum = maximum;
        dialog = DialogSceneComposer.dialog;
        progressBar = (DialogSceneComposerModel.SimProgressBar) dialog.simActor;
        previousMaximum = progressBar.maximum;
        previousValue = progressBar.value;
        value = previousValue < maximum ? previousValue : maximum;
    }
    
    @Override
    public void undo() {
        progressBar.maximum = previousMaximum;
        progressBar.value = previousValue;
        
        if (dialog.simActor != progressBar) {
            dialog.simActor = progressBar;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        progressBar.maximum = maximum;
        progressBar.value = value;
        
        if (dialog.simActor != progressBar) {
            dialog.simActor = progressBar;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ProgressBar maximum " + maximum + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ProgressBar maximum " + maximum + "\"";
    }
}
