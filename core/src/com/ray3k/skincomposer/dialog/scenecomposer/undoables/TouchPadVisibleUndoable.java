package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimTouchPad;

public class TouchPadVisibleUndoable implements SceneComposerUndoable {
    private SimTouchPad touchPad;
    private DialogSceneComposer dialog;
    private boolean visible;
    private boolean previousVisible;
    
    public TouchPadVisibleUndoable(boolean visible) {
        this.visible = visible;
        dialog = DialogSceneComposer.dialog;
        touchPad = (SimTouchPad) dialog.simActor;
        previousVisible = touchPad.visible;
    }
    
    @Override
    public void undo() {
        touchPad.visible = previousVisible;
        
        if (dialog.simActor != touchPad) {
            dialog.simActor = touchPad;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        touchPad.visible = visible;
        
        if (dialog.simActor != touchPad) {
            dialog.simActor = touchPad;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TouchPad visible " + visible + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TouchPad visible " + visible + "\"";
    }
}
