package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class SliderResetUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimSlider slider;
    private DialogSceneComposer dialog;
    private String previousName;
    private StyleData previousStyle;
    private boolean previousDisabled;
    public float previousValue;
    public float previousMinimum;
    public float previousMaximum;
    public float previousIncrement;
    public boolean previousVertical;
    public float previousAnimationDuration;
    public DialogSceneComposerModel.Interpol previousAnimateInterpolation;
    public boolean previousRound;
    public DialogSceneComposerModel.Interpol previousVisualInterpolation;
    
    public SliderResetUndoable() {
        dialog = DialogSceneComposer.dialog;
        slider = (DialogSceneComposerModel.SimSlider) dialog.simActor;
        previousName = slider.name;
        previousStyle = slider.style;
        previousDisabled = slider.disabled;
        previousValue = slider.value;
        previousMinimum = slider.minimum;
        previousMaximum = slider.maximum;
        previousIncrement = slider.increment;
        previousVertical = slider.vertical;
        previousAnimationDuration = slider.animationDuration;
        previousAnimateInterpolation = slider.animateInterpolation;
        previousRound = slider.round;
        previousVisualInterpolation = slider.visualInterpolation;
    }
    
    @Override
    public void undo() {
        slider.name = previousName;
        slider.style = previousStyle;
        slider.disabled = previousDisabled;
        slider.value = previousValue;
        slider.minimum = previousMinimum;
        slider.maximum = previousMaximum;
        slider.increment = previousIncrement;
        slider.vertical = previousVertical;
        slider.animationDuration = previousAnimationDuration;
        slider.animateInterpolation = previousAnimateInterpolation;
        slider.round = previousRound;
        slider.visualInterpolation = previousVisualInterpolation;
        
        if (dialog.simActor != slider) {
            dialog.simActor = slider;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        slider.reset();
        
        if (dialog.simActor != slider) {
            dialog.simActor = slider;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Reset Slider\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Reset Slider\"";
    }
}
