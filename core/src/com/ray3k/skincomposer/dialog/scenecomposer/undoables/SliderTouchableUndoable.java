package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimButton;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimSlider;

public class SliderTouchableUndoable implements SceneComposerUndoable {
    private SimSlider slider;
    private DialogSceneComposer dialog;
    private Touchable touchable;
    private Touchable previousTouchable;
    
    public SliderTouchableUndoable(Touchable touchable) {
        this.touchable = touchable;
        dialog = DialogSceneComposer.dialog;
        slider = (SimSlider) dialog.simActor;
        previousTouchable = slider.touchable;
    }
    
    @Override
    public void undo() {
        slider.touchable = previousTouchable;
        
        if (dialog.simActor != slider) {
            dialog.simActor = slider;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        slider.touchable = touchable;
        
        if (dialog.simActor != slider) {
            dialog.simActor = slider;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Slider touchable " + touchable + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Slider touchable " + touchable + "\"";
    }
}
