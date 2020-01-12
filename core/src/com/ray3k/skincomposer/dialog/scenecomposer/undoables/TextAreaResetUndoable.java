package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextAreaResetUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextArea textArea;
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
    
    public TextAreaResetUndoable() {
        dialog = DialogSceneComposer.dialog;
        textArea = (DialogSceneComposerModel.SimTextArea) dialog.simActor;
        previousName = textArea.name;
        previousStyle = textArea.style;
        previousText = textArea.text;
        previousPasswordCharacter = textArea.passwordCharacter;
        previousPasswordMode = textArea.passwordMode;
        previousAlignment = textArea.alignment;
        previousDisabled = textArea.disabled;
        previousCursorPosition = textArea.cursorPosition;
        previousSelectionStart = textArea.selectionStart;
        previousSelectionEnd = textArea.selectionEnd;
        previousSelectAll = textArea.selectAll;
        previousFocusTraversal = textArea.focusTraversal;
        previousMaxLength = textArea.maxLength;
        previousMessageText = textArea.messageText;
        previousPreferredRows = textArea.preferredRows;
    }
    
    @Override
    public void undo() {
        textArea.name = previousName;
        textArea.style = previousStyle;
        textArea.text = previousText;
        textArea.passwordCharacter = previousPasswordCharacter;
        textArea.passwordMode = previousPasswordMode;
        textArea.alignment = previousAlignment;
        textArea.disabled = previousDisabled;
        textArea.cursorPosition = previousCursorPosition;
        textArea.selectionStart = previousSelectionStart;
        textArea.selectionEnd = previousSelectionEnd;
        textArea.selectAll = previousSelectAll;
        textArea.focusTraversal = previousFocusTraversal;
        textArea.maxLength = previousMaxLength;
        textArea.messageText = previousMessageText;
        textArea.preferredRows = previousPreferredRows;
        
        if (dialog.simActor != textArea) {
            dialog.simActor = textArea;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textArea.reset();
        
        if (dialog.simActor != textArea) {
            dialog.simActor = textArea;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Reset TextArea\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Reset TextArea\"";
    }
}
