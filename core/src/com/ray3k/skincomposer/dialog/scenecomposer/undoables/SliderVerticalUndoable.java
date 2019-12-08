package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class SliderVerticalUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimSlider slider;
    private DialogSceneComposer dialog;
    private boolean vertical;
    private boolean previousVertical;
    
    public SliderVerticalUndoable(boolean vertical) {
        this.vertical = vertical;
        dialog = DialogSceneComposer.dialog;
        slider = (DialogSceneComposerModel.SimSlider) dialog.simActor;
        previousVertical = slider.vertical;
    }
    
    @Override
    public void undo() {
        slider.vertical = previousVertical;
        
        if (dialog.simActor != slider) {
            dialog.simActor = slider;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        slider.vertical = vertical;
        
        if (dialog.simActor != slider) {
            dialog.simActor = slider;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Slider " + (vertical ? "vertical" : "horizontal") + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Slider " + (vertical ? "vertical" : "horizontal") + "\"";
    }
}
