package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TouchPadStyleUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTouchPad touchPad;
    private StyleData style;
    private StyleData previousStyle;
    private DialogSceneComposer dialog;
    
    public TouchPadStyleUndoable(StyleData style) {
        this.style = style;
        dialog = DialogSceneComposer.dialog;
        touchPad = (DialogSceneComposerModel.SimTouchPad) dialog.simActor;
        previousStyle = touchPad.style;
    }
    
    @Override
    public void undo() {
        touchPad.style = previousStyle;
        
        if (dialog.simActor != touchPad) {
            dialog.simActor = touchPad;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        touchPad.style = style;
        
        if (dialog.simActor != touchPad) {
            dialog.simActor = touchPad;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TouchPad style: " + style.name + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TouchPad style: " + style.name + "\"";
    }
}
