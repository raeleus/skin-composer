package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextAreaMaxLengthUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextArea textArea;
    private DialogSceneComposer dialog;
    private int maxLength;
    private int previousMaxLength;
    
    public TextAreaMaxLengthUndoable(int maxLength) {
        this.maxLength = maxLength;
        dialog = DialogSceneComposer.dialog;
        textArea = (DialogSceneComposerModel.SimTextArea) dialog.simActor;
        
        previousMaxLength = textArea.maxLength;
    }
    
    @Override
    public void undo() {
        textArea.maxLength = previousMaxLength;
        
        if (dialog.simActor != textArea) {
            dialog.simActor = textArea;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textArea.maxLength = maxLength;
        
        if (dialog.simActor != textArea) {
            dialog.simActor = textArea;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextArea max length " + maxLength + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextArea max length " + maxLength + "\"";
    }
}
