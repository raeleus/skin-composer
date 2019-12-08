package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextAreaSelectionStartUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextArea textArea;
    private DialogSceneComposer dialog;
    private int selectionStart;
    private int previousSelectionStart;
    
    public TextAreaSelectionStartUndoable(int selectionStart) {
        this.selectionStart = selectionStart;
        dialog = DialogSceneComposer.dialog;
        textArea = (DialogSceneComposerModel.SimTextArea) dialog.simActor;
        
        previousSelectionStart = textArea.selectionStart;
    }
    
    @Override
    public void undo() {
        textArea.selectionStart = previousSelectionStart;
        
        if (dialog.simActor != textArea) {
            dialog.simActor = textArea;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textArea.selectionStart = selectionStart;
        
        if (dialog.simActor != textArea) {
            dialog.simActor = textArea;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextArea selection start\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextArea selection start\"";
    }
}
