package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextFieldPasswordCharacterUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextField textField;
    private char character;
    private char previousCharacter;
    private DialogSceneComposer dialog;
    
    public TextFieldPasswordCharacterUndoable(char character) {
        this.character = character;
        dialog = DialogSceneComposer.dialog;
        textField = (DialogSceneComposerModel.SimTextField) dialog.simActor;
        previousCharacter = textField.passwordCharacter;
    }
    
    @Override
    public void undo() {
        textField.passwordCharacter = previousCharacter;
    
        if (dialog.simActor != textField) {
            dialog.simActor = textField;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textField.passwordCharacter = character;
    
        if (dialog.simActor != textField) {
            dialog.simActor = textField;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextField password character " + character + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextField password character " + character + "\"";
    }
}
