package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TouchPadPaddingUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTouchPad touchPad;
    private float deadZone;
    private float previousDeadZone;
    private DialogSceneComposer dialog;
    
    public TouchPadPaddingUndoable(float deadZone) {
        this.deadZone = deadZone;

        dialog = DialogSceneComposer.dialog;
        touchPad = (DialogSceneComposerModel.SimTouchPad) dialog.simActor;
        previousDeadZone = touchPad.deadZone;
    }
    
    @Override
    public void undo() {
        touchPad.deadZone = previousDeadZone;
        
        if (dialog.simActor != touchPad) {
            dialog.simActor = touchPad;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        touchPad.deadZone = deadZone;
        
        if (dialog.simActor != touchPad) {
            dialog.simActor = touchPad;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TouchPad dead zone " + deadZone + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TouchPad dead zone " + deadZone + "\"";
    }
}
