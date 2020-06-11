package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimSlider;

public class SliderVisibleUndoable implements SceneComposerUndoable {
    private SimSlider slider;
    private DialogSceneComposer dialog;
    private boolean visible;
    private boolean previousVisible;
    
    public SliderVisibleUndoable(boolean visible) {
        this.visible = visible;
        dialog = DialogSceneComposer.dialog;
        slider = (SimSlider) dialog.simActor;
        previousVisible = slider.visible;
    }
    
    @Override
    public void undo() {
        slider.visible = previousVisible;
        
        if (dialog.simActor != slider) {
            dialog.simActor = slider;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        slider.visible = visible;
        
        if (dialog.simActor != slider) {
            dialog.simActor = slider;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Slider visible " + visible + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Slider visible " + visible + "\"";
    }
}
