package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextAreaPasswordCharacterUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextArea textArea;
    private char character;
    private char previousCharacter;
    private DialogSceneComposer dialog;
    
    public TextAreaPasswordCharacterUndoable(char character) {
        this.character = character;
        dialog = DialogSceneComposer.dialog;
        textArea = (DialogSceneComposerModel.SimTextArea) dialog.simActor;
        previousCharacter = textArea.passwordCharacter;
    }
    
    @Override
    public void undo() {
        textArea.passwordCharacter = previousCharacter;
    
        if (dialog.simActor != textArea) {
            dialog.simActor = textArea;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textArea.passwordCharacter = character;
    
        if (dialog.simActor != textArea) {
            dialog.simActor = textArea;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextArea password character " + character + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextArea password character " + character + "\"";
    }
}
