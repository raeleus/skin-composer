package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextFieldSelectionStartUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextField textField;
    private DialogSceneComposer dialog;
    private int selectionStart;
    private int previousSelectionStart;
    
    public TextFieldSelectionStartUndoable(int selectionStart) {
        this.selectionStart = selectionStart;
        dialog = DialogSceneComposer.dialog;
        textField = (DialogSceneComposerModel.SimTextField) dialog.simActor;
        
        previousSelectionStart = textField.selectionStart;
    }
    
    @Override
    public void undo() {
        textField.selectionStart = previousSelectionStart;
        
        if (dialog.simActor != textField) {
            dialog.simActor = textField;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textField.selectionStart = selectionStart;
        
        if (dialog.simActor != textField) {
            dialog.simActor = textField;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextField selection start\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextField selection start\"";
    }
}
