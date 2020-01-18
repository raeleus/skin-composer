package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ProgressBarMinimumUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimProgressBar progressBar;
    private DialogSceneComposer dialog;
    private float minimum;
    private float previousMinimum;
    private float value;
    private float previousValue;
    
    public ProgressBarMinimumUndoable(float minimum) {
        this.minimum = minimum;
        dialog = DialogSceneComposer.dialog;
        progressBar = (DialogSceneComposerModel.SimProgressBar) dialog.simActor;
        previousMinimum = progressBar.minimum;
        previousValue = progressBar.value;
        value = previousValue > minimum ? previousValue : minimum;
    }
    
    @Override
    public void undo() {
        progressBar.minimum = previousMinimum;
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
        progressBar.minimum = minimum;
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
        return "Redo \"ProgressBar minimum " + minimum + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ProgressBar minimum " + minimum + "\"";
    }
}
