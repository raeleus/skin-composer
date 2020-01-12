package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TouchPadResetUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTouchPad touchPad;
    private DialogSceneComposer dialog;
    private String previousName;
    private StyleData previousStyle;
    private float previousDeadZone;
    private boolean previousResetOnTouchUp;
    
    public TouchPadResetUndoable() {
        dialog = DialogSceneComposer.dialog;
        touchPad = (DialogSceneComposerModel.SimTouchPad) dialog.simActor;
        previousName = touchPad.name;
        previousStyle = touchPad.style;
        previousDeadZone = touchPad.deadZone;
        previousResetOnTouchUp = touchPad.resetOnTouchUp;
    }
    
    @Override
    public void undo() {
        touchPad.name = previousName;
        touchPad.style = previousStyle;
        touchPad.deadZone = previousDeadZone;
        touchPad.resetOnTouchUp = previousResetOnTouchUp;
        
        if (dialog.simActor != touchPad) {
            dialog.simActor = touchPad;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        touchPad.reset();
        
        if (dialog.simActor != touchPad) {
            dialog.simActor = touchPad;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Reset TouchPad\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Reset TouchPad\"";
    }
}
