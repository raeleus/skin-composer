package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class CheckBoxNameUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimCheckBox checkBox;
    private String name;
    private String previousName;
    private DialogSceneComposer dialog;
    
    public CheckBoxNameUndoable(String name) {
        this.name = name;
        dialog = DialogSceneComposer.dialog;
        checkBox = (DialogSceneComposerModel.SimCheckBox) dialog.simActor;
        if (name != null && name.equals("")) {
            this.name = null;
        }
        previousName = checkBox.name;
    }
    
    @Override
    public void undo() {
        checkBox.name = previousName;
    
        if (dialog.simActor != checkBox) {
            dialog.simActor = checkBox;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        checkBox.name = name;
    
        if (dialog.simActor != checkBox) {
            dialog.simActor = checkBox;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"CheckBox name " + name + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"CheckBox name " + name + "\"";
    }
}
