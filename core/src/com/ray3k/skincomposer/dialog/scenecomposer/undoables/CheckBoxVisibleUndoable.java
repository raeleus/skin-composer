package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimCheckBox;

public class CheckBoxVisibleUndoable implements SceneComposerUndoable {
    private SimCheckBox checkBox;
    private DialogSceneComposer dialog;
    private boolean visible;
    private boolean previousVisible;
    
    public CheckBoxVisibleUndoable(boolean visible) {
        this.visible = visible;
        dialog = DialogSceneComposer.dialog;
        checkBox = (SimCheckBox) dialog.simActor;
        previousVisible = checkBox.visible;
    }
    
    @Override
    public void undo() {
        checkBox.visible = previousVisible;
        
        if (dialog.simActor != checkBox) {
            dialog.simActor = checkBox;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        checkBox.visible = visible;
        
        if (dialog.simActor != checkBox) {
            dialog.simActor = checkBox;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"CheckBox visible " + visible + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"CheckBox visible " + visible + "\"";
    }
}
