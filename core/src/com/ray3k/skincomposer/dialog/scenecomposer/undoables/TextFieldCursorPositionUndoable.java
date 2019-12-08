package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextFieldCursorPositionUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextField textField;
    private DialogSceneComposer dialog;
    private int cursorPosition;
    private int previousCursorPosition;
    
    public TextFieldCursorPositionUndoable(int cursorPosition) {
        this.cursorPosition = cursorPosition;
        dialog = DialogSceneComposer.dialog;
        textField = (DialogSceneComposerModel.SimTextField) dialog.simActor;
        
        previousCursorPosition = textField.cursorPosition;
    }
    
    @Override
    public void undo() {
        textField.cursorPosition = previousCursorPosition;
        
        if (dialog.simActor != textField) {
            dialog.simActor = textField;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textField.cursorPosition = cursorPosition;
        
        if (dialog.simActor != textField) {
            dialog.simActor = textField;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextField cursor position\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextField cursor position\"";
    }
}
