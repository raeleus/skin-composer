package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TouchPadNameUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTouchPad touchPad;
    private String name;
    private String previousName;
    private DialogSceneComposer dialog;
    
    public TouchPadNameUndoable(String name) {
        this.name = name;
        dialog = DialogSceneComposer.dialog;
        touchPad = (DialogSceneComposerModel.SimTouchPad) dialog.simActor;
        if (name != null && name.equals("")) {
            this.name = null;
        }
        previousName = touchPad.name;
    }
    
    @Override
    public void undo() {
        touchPad.name = previousName;
    
        if (dialog.simActor != touchPad) {
            dialog.simActor = touchPad;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        touchPad.name = name;
    
        if (dialog.simActor != touchPad) {
            dialog.simActor = touchPad;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TouchPad name " + name + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TouchPad name " + name + "\"";
    }
}
