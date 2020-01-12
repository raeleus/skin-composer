package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextFieldResetUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextField textField;
    private DialogSceneComposer dialog;
    private String previousName;
    private StyleData previousStyle;
    private String previousText;
    private char previousPasswordCharacter;
    private boolean previousPasswordMode;
    private int previousAlignment;
    private boolean previousDisabled;
    private int previousCursorPosition;
    private int previousSelectionStart;
    private int previousSelectionEnd;
    private boolean previousSelectAll;
    private boolean previousFocusTraversal;
    private int previousMaxLength;
    private String previousMessageText;
    private int previousPreferredRows;
    
    public TextFieldResetUndoable() {
        dialog = DialogSceneComposer.dialog;
        textField = (DialogSceneComposerModel.SimTextField) dialog.simActor;
        previousName = textField.name;
        previousStyle = textField.style;
        previousText = textField.text;
        previousPasswordCharacter = textField.passwordCharacter;
        previousPasswordMode = textField.passwordMode;
        previousAlignment = textField.alignment;
        previousDisabled = textField.disabled;
        previousCursorPosition = textField.cursorPosition;
        previousSelectionStart = textField.selectionStart;
        previousSelectionEnd = textField.selectionEnd;
        previousSelectAll = textField.selectAll;
        previousFocusTraversal = textField.focusTraversal;
        previousMaxLength = textField.maxLength;
        previousMessageText = textField.messageText;
    }
    
    @Override
    public void undo() {
        textField.name = previousName;
        textField.style = previousStyle;
        textField.text = previousText;
        textField.passwordCharacter = previousPasswordCharacter;
        textField.passwordMode = previousPasswordMode;
        textField.alignment = previousAlignment;
        textField.disabled = previousDisabled;
        textField.cursorPosition = previousCursorPosition;
        textField.selectionStart = previousSelectionStart;
        textField.selectionEnd = previousSelectionEnd;
        textField.selectAll = previousSelectAll;
        textField.focusTraversal = previousFocusTraversal;
        textField.maxLength = previousMaxLength;
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
        textField.reset();
        
        if (dialog.simActor != textField) {
            dialog.simActor = textField;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Reset TextField\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Reset TextField\"";
    }
}
