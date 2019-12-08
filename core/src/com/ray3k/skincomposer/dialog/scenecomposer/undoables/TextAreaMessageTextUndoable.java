package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextAreaMessageTextUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextArea textArea;
    private String messageText;
    private String previousMessageText;
    private DialogSceneComposer dialog;
    
    public TextAreaMessageTextUndoable(String messageText) {
        this.messageText = messageText;
        dialog = DialogSceneComposer.dialog;
        textArea = (DialogSceneComposerModel.SimTextArea) dialog.simActor;
        if (messageText != null && messageText.equals("")) {
            this.messageText = null;
        }
        previousMessageText = textArea.messageText;
    }
    
    @Override
    public void undo() {
        textArea.messageText = previousMessageText;
        
        if (dialog.simActor != textArea) {
            dialog.simActor = textArea;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textArea.messageText = messageText;
        
        if (dialog.simActor != textArea) {
            dialog.simActor = textArea;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextArea message text " + messageText + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextArea message text " + messageText + "\"";
    }
}
