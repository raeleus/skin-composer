package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextAreaSelectionEndUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextArea textArea;
    private DialogSceneComposer dialog;
    private int selectionEnd;
    private int previousSelectionEnd;
    
    public TextAreaSelectionEndUndoable(int selectionEnd) {
        this.selectionEnd = selectionEnd;
        dialog = DialogSceneComposer.dialog;
        textArea = (DialogSceneComposerModel.SimTextArea) dialog.simActor;
        
        previousSelectionEnd = textArea.selectionEnd;
    }
    
    @Override
    public void undo() {
        textArea.selectionEnd = previousSelectionEnd;
        
        if (dialog.simActor != textArea) {
            dialog.simActor = textArea;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textArea.selectionEnd = selectionEnd;
        
        if (dialog.simActor != textArea) {
            dialog.simActor = textArea;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextArea selection end\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextArea selection end\"";
    }
}
