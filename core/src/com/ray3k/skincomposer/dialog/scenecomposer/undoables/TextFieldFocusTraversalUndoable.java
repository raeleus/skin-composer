package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextFieldFocusTraversalUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextField textField;
    private DialogSceneComposer dialog;
    private boolean focusTraversal;
    private boolean previousFocusTraversal;
    
    public TextFieldFocusTraversalUndoable(boolean focusTraversal) {
        this.focusTraversal = focusTraversal;
        dialog = DialogSceneComposer.dialog;
        textField = (DialogSceneComposerModel.SimTextField) dialog.simActor;
        previousFocusTraversal = textField.focusTraversal;
    }
    
    @Override
    public void undo() {
        textField.focusTraversal = previousFocusTraversal;
        
        if (dialog.simActor != textField) {
            dialog.simActor = textField;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textField.focusTraversal = focusTraversal;
        
        if (dialog.simActor != textField) {
            dialog.simActor = textField;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextField focus traversal " + focusTraversal + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextField focus traversal " + focusTraversal + "\"";
    }
}
