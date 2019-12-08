package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class CheckBoxTextUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimCheckBox checkBox;
    private String text;
    private String previousText;
    private DialogSceneComposer dialog;
    
    public CheckBoxTextUndoable(String text) {
        this.text = text;
        dialog = DialogSceneComposer.dialog;
        checkBox = (DialogSceneComposerModel.SimCheckBox) dialog.simActor;
        if (text != null && text.equals("")) {
            this.text = null;
        }
        previousText = checkBox.text;
    }
    
    @Override
    public void undo() {
        checkBox.text = previousText;
        
        if (dialog.simActor != checkBox) {
            dialog.simActor = checkBox;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        checkBox.text = text;
        
        if (dialog.simActor != checkBox) {
            dialog.simActor = checkBox;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"CheckBox text " + text + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"CheckBox text " + text + "\"";
    }
}
