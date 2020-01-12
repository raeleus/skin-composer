package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ProgressBarResetUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimProgressBar progressBar;
    private DialogSceneComposer dialog;
    private String previousName;
    private StyleData previousStyle;
    private boolean previousDisabled;
    private float previousValue;
    private float previousMinimum;
    private float previousMaximum;
    private float previousIncrement;
    private boolean previousVertical;
    private float previousAnimationDuration;
    private DialogSceneComposerModel.Interpol previousAnimateInterpolation;
    private boolean previousRound;
    private DialogSceneComposerModel.Interpol previousVisualInterpolation;
    
    public ProgressBarResetUndoable() {
        dialog = DialogSceneComposer.dialog;
        progressBar = (DialogSceneComposerModel.SimProgressBar) dialog.simActor;
        previousName = progressBar.name;
        previousStyle = progressBar.style;
        previousDisabled = progressBar.disabled;
        previousValue = progressBar.value;
        previousMinimum = progressBar.minimum;
        previousMaximum = progressBar.maximum;
        previousIncrement = progressBar.increment;
        previousVertical = progressBar.vertical;
        previousAnimationDuration = progressBar.animationDuration;
        previousAnimateInterpolation = progressBar.animateInterpolation;
        previousRound = progressBar.round;
        previousVisualInterpolation = progressBar.visualInterpolation;
    }
    
    @Override
    public void undo() {
        progressBar.name = previousName;
        progressBar.style = previousStyle;
        progressBar.disabled = previousDisabled;
        progressBar.value = previousValue;
        progressBar.minimum = previousMinimum;
        progressBar.maximum = previousMaximum;
        progressBar.increment = previousIncrement;
        progressBar.vertical = previousVertical;
        progressBar.animationDuration = previousAnimationDuration;
        progressBar.animateInterpolation = previousAnimateInterpolation;
        progressBar.round = previousRound;
        progressBar.visualInterpolation = previousVisualInterpolation;
        
        if (dialog.simActor != progressBar) {
            dialog.simActor = progressBar;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        progressBar.reset();
        
        if (dialog.simActor != progressBar) {
            dialog.simActor = progressBar;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Reset ProgressBar\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Reset ProgressBar\"";
    }
}
