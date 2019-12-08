package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextFieldPasswordModeUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextField textField;
    private DialogSceneComposer dialog;
    private boolean passwordMode;
    private boolean previousPasswordMode;
    
    public TextFieldPasswordModeUndoable(boolean passwordMode) {
        this.passwordMode = passwordMode;
        dialog = DialogSceneComposer.dialog;
        textField = (DialogSceneComposerModel.SimTextField) dialog.simActor;
        previousPasswordMode = textField.passwordMode;
    }
    
    @Override
    public void undo() {
        textField.passwordMode = previousPasswordMode;
        
        if (dialog.simActor != textField) {
            dialog.simActor = textField;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textField.passwordMode = passwordMode;
        
        if (dialog.simActor != textField) {
            dialog.simActor = textField;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextField password mode " + passwordMode + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextField password mode " + passwordMode + "\"";
    }
}
