package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TouchPadResetOnTouchUpUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTouchPad touchPad;
    private DialogSceneComposer dialog;
    private boolean resetOnTouchUp;
    private boolean previousResetOnTouchUp;
    
    public TouchPadResetOnTouchUpUndoable(boolean resetOnTouchUp) {
        this.resetOnTouchUp = resetOnTouchUp;
        dialog = DialogSceneComposer.dialog;
        touchPad = (DialogSceneComposerModel.SimTouchPad) dialog.simActor;
        previousResetOnTouchUp = touchPad.resetOnTouchUp;
    }
    
    @Override
    public void undo() {
        touchPad.resetOnTouchUp = previousResetOnTouchUp;
        
        if (dialog.simActor != touchPad) {
            dialog.simActor = touchPad;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        touchPad.resetOnTouchUp = resetOnTouchUp;
        
        if (dialog.simActor != touchPad) {
            dialog.simActor = touchPad;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TouchPad resetOnTouchUp " + resetOnTouchUp + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TouchPad resetOnTouchUp " + resetOnTouchUp + "\"";
    }
}
