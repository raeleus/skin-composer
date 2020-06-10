package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimButton;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimTouchable;

public class ButtonTouchableUndoable implements SceneComposerUndoable {
    private SimButton button;
    private DialogSceneComposer dialog;
    private Touchable touchable;
    private Touchable previousTouchable;
    
    public ButtonTouchableUndoable(Touchable touchable) {
        this.touchable = touchable;
        dialog = DialogSceneComposer.dialog;
        button = (SimButton) dialog.simActor;
        previousTouchable = button.touchable;
    }
    
    @Override
    public void undo() {
        button.touchable = previousTouchable;
        
        if (dialog.simActor != button) {
            dialog.simActor = button;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        button.touchable = touchable;
        
        if (dialog.simActor != button) {
            dialog.simActor = button;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Button touchable " + touchable + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Button touchable " + touchable + "\"";
    }
}
