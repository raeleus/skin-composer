package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextFieldSelectionEndUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextField textField;
    private DialogSceneComposer dialog;
    private int selectionEnd;
    private int previousSelectionEnd;
    
    public TextFieldSelectionEndUndoable(int selectionEnd) {
        this.selectionEnd = selectionEnd;
        dialog = DialogSceneComposer.dialog;
        textField = (DialogSceneComposerModel.SimTextField) dialog.simActor;
        
        previousSelectionEnd = textField.selectionEnd;
    }
    
    @Override
    public void undo() {
        textField.selectionEnd = previousSelectionEnd;
        
        if (dialog.simActor != textField) {
            dialog.simActor = textField;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textField.selectionEnd = selectionEnd;
        
        if (dialog.simActor != textField) {
            dialog.simActor = textField;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextField selection end\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextField selection end\"";
    }
}
