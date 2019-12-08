package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class SliderNameUndoable implements  SceneComposerUndoable {
    private DialogSceneComposerModel.SimSlider slider;
    private String name;
    private String previousName;
    private DialogSceneComposer dialog;
    
    public SliderNameUndoable(String name) {
        dialog = DialogSceneComposer.dialog;
        slider = (DialogSceneComposerModel.SimSlider) dialog.simActor;
        if (name != null && name.equals("")) {
            name = null;
        }
        this.name = name;
        previousName = slider.name;
    }
    
    @Override
    public void undo() {
        slider.name = previousName;
        
        if (dialog.simActor != slider) {
            dialog.simActor = slider;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        slider.name = name;
        
        if (dialog.simActor != slider) {
            dialog.simActor = slider;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Slider Name: " + name + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Slider Name: " + name + "\"";
    }
}
