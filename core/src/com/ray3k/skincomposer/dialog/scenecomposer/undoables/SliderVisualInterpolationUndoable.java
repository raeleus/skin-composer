package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class SliderVisualInterpolationUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimSlider slider;
    private DialogSceneComposer dialog;
    private DialogSceneComposerModel.Interpol visualInterpolation;
    private DialogSceneComposerModel.Interpol previousVisualInterpolation;
    
    public SliderVisualInterpolationUndoable(DialogSceneComposerModel.Interpol visualInterpolation) {
        this.visualInterpolation = visualInterpolation;
        dialog = DialogSceneComposer.dialog;
        slider = (DialogSceneComposerModel.SimSlider) dialog.simActor;
        previousVisualInterpolation = slider.visualInterpolation;
    }
    
    @Override
    public void undo() {
        slider.visualInterpolation = previousVisualInterpolation;
        
        if (dialog.simActor != slider) {
            dialog.simActor = slider;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        slider.visualInterpolation = visualInterpolation;
        
        if (dialog.simActor != slider) {
            dialog.simActor = slider;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Slider visual interpolation " + visualInterpolation + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Slider visual interpolation " + visualInterpolation + "\"";
    }
}
