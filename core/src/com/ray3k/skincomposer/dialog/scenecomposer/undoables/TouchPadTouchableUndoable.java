package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimButton;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimTouchPad;

public class TouchPadTouchableUndoable implements SceneComposerUndoable {
    private SimTouchPad touchPad;
    private DialogSceneComposer dialog;
    private Touchable touchable;
    private Touchable previousTouchable;
    
    public TouchPadTouchableUndoable(Touchable touchable) {
        this.touchable = touchable;
        dialog = DialogSceneComposer.dialog;
        touchPad = (SimTouchPad) dialog.simActor;
        previousTouchable = touchPad.touchable;
    }
    
    @Override
    public void undo() {
        touchPad.touchable = previousTouchable;
        
        if (dialog.simActor != touchPad) {
            dialog.simActor = touchPad;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        touchPad.touchable = touchable;
        
        if (dialog.simActor != touchPad) {
            dialog.simActor = touchPad;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TouchPad touchable " + touchable + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TouchPad touchable " + touchable + "\"";
    }
}
