package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextFieldMaxLengthUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextField textField;
    private DialogSceneComposer dialog;
    private int maxLength;
    private int previousMaxLength;
    
    public TextFieldMaxLengthUndoable(int maxLength) {
        this.maxLength = maxLength;
        dialog = DialogSceneComposer.dialog;
        textField = (DialogSceneComposerModel.SimTextField) dialog.simActor;
        
        previousMaxLength = textField.maxLength;
    }
    
    @Override
    public void undo() {
        textField.maxLength = previousMaxLength;
        
        if (dialog.simActor != textField) {
            dialog.simActor = textField;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textField.maxLength = maxLength;
        
        if (dialog.simActor != textField) {
            dialog.simActor = textField;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextField max length " + maxLength + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextField max length " + maxLength + "\"";
    }
}
