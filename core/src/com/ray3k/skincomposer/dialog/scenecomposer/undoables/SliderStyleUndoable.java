package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class SliderStyleUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimSlider slider;
    private StyleData style;
    private StyleData previousStyle;
    private DialogSceneComposer dialog;
    
    public SliderStyleUndoable(StyleData style) {
        this.style = style;
        dialog = DialogSceneComposer.dialog;
        slider = (DialogSceneComposerModel.SimSlider) dialog.simActor;
        previousStyle = slider.style;
    }
    
    @Override
    public void undo() {
        slider.style = previousStyle;
        
        if (dialog.simActor != slider) {
            dialog.simActor = slider;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        slider.style = style;
        
        if (dialog.simActor != slider) {
            dialog.simActor = slider;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Slider style: " + style.name + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Slider style: " + style.name + "\"";
    }
}
