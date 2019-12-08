package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class SliderMaximumUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimSlider slider;
    private DialogSceneComposer dialog;
    private float maximum;
    private float previousMaximum;
    
    public SliderMaximumUndoable(float maximum) {
        this.maximum = maximum;
        dialog = DialogSceneComposer.dialog;
        slider = (DialogSceneComposerModel.SimSlider) dialog.simActor;
        previousMaximum = slider.maximum;
    }
    
    @Override
    public void undo() {
        slider.maximum = previousMaximum;
        
        if (dialog.simActor != slider) {
            dialog.simActor = slider;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        slider.maximum = maximum;
        
        if (dialog.simActor != slider) {
            dialog.simActor = slider;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Slider maximum " + maximum + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Slider maximum " + maximum + "\"";
    }
}
