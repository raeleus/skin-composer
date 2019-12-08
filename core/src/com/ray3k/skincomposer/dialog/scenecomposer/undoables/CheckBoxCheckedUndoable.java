package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class CheckBoxCheckedUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimCheckBox checkBox;
    private DialogSceneComposer dialog;
    private boolean checked;
    private boolean previousChecked;
    
    public CheckBoxCheckedUndoable(boolean checked) {
        this.checked = checked;
        dialog = DialogSceneComposer.dialog;
        checkBox = (DialogSceneComposerModel.SimCheckBox) dialog.simActor;
        previousChecked = checkBox.checked;
    }
    
    @Override
    public void undo() {
        checkBox.checked = previousChecked;
        
        if (dialog.simActor != checkBox) {
            dialog.simActor = checkBox;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        checkBox.checked = checked;
        
        if (dialog.simActor != checkBox) {
            dialog.simActor = checkBox;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"CheckBox checked " + checked + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"CheckBox checked " + checked + "\"";
    }
}
