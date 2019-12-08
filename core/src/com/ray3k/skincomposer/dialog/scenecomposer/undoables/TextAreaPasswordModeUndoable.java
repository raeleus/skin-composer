package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextAreaPasswordModeUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextArea textArea;
    private DialogSceneComposer dialog;
    private boolean passwordMode;
    private boolean previousPasswordMode;
    
    public TextAreaPasswordModeUndoable(boolean passwordMode) {
        this.passwordMode = passwordMode;
        dialog = DialogSceneComposer.dialog;
        textArea = (DialogSceneComposerModel.SimTextArea) dialog.simActor;
        previousPasswordMode = textArea.passwordMode;
    }
    
    @Override
    public void undo() {
        textArea.passwordMode = previousPasswordMode;
        
        if (dialog.simActor != textArea) {
            dialog.simActor = textArea;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textArea.passwordMode = passwordMode;
        
        if (dialog.simActor != textArea) {
            dialog.simActor = textArea;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextArea password mode " + passwordMode + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextArea password mode " + passwordMode + "\"";
    }
}
