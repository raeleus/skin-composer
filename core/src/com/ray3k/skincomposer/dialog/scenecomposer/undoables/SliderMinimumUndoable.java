package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class SliderMinimumUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimSlider slider;
    private DialogSceneComposer dialog;
    private float minimum;
    private float previousMinimum;
    
    public SliderMinimumUndoable(float minimum) {
        this.minimum = minimum;
        dialog = DialogSceneComposer.dialog;
        slider = (DialogSceneComposerModel.SimSlider) dialog.simActor;
        previousMinimum = slider.minimum;
    }
    
    @Override
    public void undo() {
        slider.minimum = previousMinimum;
        
        if (dialog.simActor != slider) {
            dialog.simActor = slider;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        slider.minimum = minimum;
        
        if (dialog.simActor != slider) {
            dialog.simActor = slider;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Slider minimum " + minimum + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Slider minimum " + minimum + "\"";
    }
}
