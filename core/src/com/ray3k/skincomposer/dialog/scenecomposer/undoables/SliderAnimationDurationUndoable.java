package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class SliderAnimationDurationUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimSlider slider;
    private DialogSceneComposer dialog;
    private float animationDuration;
    private float previousAnimationDuration;
    
    public SliderAnimationDurationUndoable(float animationDuration) {
        this.animationDuration = animationDuration;
        dialog = DialogSceneComposer.dialog;
        slider = (DialogSceneComposerModel.SimSlider) dialog.simActor;
        previousAnimationDuration = slider.animationDuration;
    }
    
    @Override
    public void undo() {
        slider.animationDuration = previousAnimationDuration;
        
        if (dialog.simActor != slider) {
            dialog.simActor = slider;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        slider.animationDuration = animationDuration;
        
        if (dialog.simActor != slider) {
            dialog.simActor = slider;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Slider animation duration " + animationDuration + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Slider animation duration " + animationDuration + "\"";
    }
}
