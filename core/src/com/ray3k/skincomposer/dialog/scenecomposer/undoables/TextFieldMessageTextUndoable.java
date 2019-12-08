package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextFieldMessageTextUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextField textField;
    private String messageText;
    private String previousMessageText;
    private DialogSceneComposer dialog;
    
    public TextFieldMessageTextUndoable(String messageText) {
        this.messageText = messageText;
        dialog = DialogSceneComposer.dialog;
        textField = (DialogSceneComposerModel.SimTextField) dialog.simActor;
        if (messageText != null && messageText.equals("")) {
            this.messageText = null;
        }
        previousMessageText = textField.messageText;
    }
    
    @Override
    public void undo() {
        textField.messageText = previousMessageText;
        
        if (dialog.simActor != textField) {
            dialog.simActor = textField;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textField.messageText = messageText;
        
        if (dialog.simActor != textField) {
            dialog.simActor = textField;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextField message text " + messageText + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextField message text " + messageText + "\"";
    }
}
