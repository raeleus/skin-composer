package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class SliderAnimateInterpolationUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimSlider slider;
    private DialogSceneComposer dialog;
    private DialogSceneComposerModel.Interpol animateDuration;
    private DialogSceneComposerModel.Interpol previousAnimateDuration;
    
    public SliderAnimateInterpolationUndoable(DialogSceneComposerModel.Interpol animateDuration) {
        this.animateDuration = animateDuration;
        dialog = DialogSceneComposer.dialog;
        slider = (DialogSceneComposerModel.SimSlider) dialog.simActor;
        previousAnimateDuration = slider.animateInterpolation;
    }
    
    @Override
    public void undo() {
        slider.animateInterpolation = previousAnimateDuration;
        
        if (dialog.simActor != slider) {
            dialog.simActor = slider;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        slider.animateInterpolation = animateDuration;
        
        if (dialog.simActor != slider) {
            dialog.simActor = slider;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Slider animation interpolation " + animateDuration + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Slider animation interpolation " + animateDuration + "\"";
    }
}
