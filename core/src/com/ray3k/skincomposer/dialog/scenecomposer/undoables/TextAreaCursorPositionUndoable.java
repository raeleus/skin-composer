package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextAreaCursorPositionUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextArea textArea;
    private DialogSceneComposer dialog;
    private int cursorPosition;
    private int previousCursorPosition;
    
    public TextAreaCursorPositionUndoable(int cursorPosition) {
        this.cursorPosition = cursorPosition;
        dialog = DialogSceneComposer.dialog;
        textArea = (DialogSceneComposerModel.SimTextArea) dialog.simActor;
        
        previousCursorPosition = textArea.cursorPosition;
    }
    
    @Override
    public void undo() {
        textArea.cursorPosition = previousCursorPosition;
        
        if (dialog.simActor != textArea) {
            dialog.simActor = textArea;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textArea.cursorPosition = cursorPosition;
        
        if (dialog.simActor != textArea) {
            dialog.simActor = textArea;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextArea cursor position\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextArea cursor position\"";
    }
}
