package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextFieldNameUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextField textField;
    private String name;
    private String previousName;
    private DialogSceneComposer dialog;
    
    public TextFieldNameUndoable(String name) {
        this.name = name;
        dialog = DialogSceneComposer.dialog;
        textField = (DialogSceneComposerModel.SimTextField) dialog.simActor;
        if (name != null && name.equals("")) {
            this.name = null;
        }
        previousName = textField.name;
    }
    
    @Override
    public void undo() {
        textField.name = previousName;
    
        if (dialog.simActor != textField) {
            dialog.simActor = textField;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textField.name = name;
    
        if (dialog.simActor != textField) {
            dialog.simActor = textField;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextField name " + name + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextField name " + name + "\"";
    }
}
