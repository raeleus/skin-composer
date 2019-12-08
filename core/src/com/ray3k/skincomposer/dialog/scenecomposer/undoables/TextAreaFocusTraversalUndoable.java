package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextAreaFocusTraversalUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextArea textArea;
    private DialogSceneComposer dialog;
    private boolean focusTraversal;
    private boolean previousFocusTraversal;
    
    public TextAreaFocusTraversalUndoable(boolean focusTraversal) {
        this.focusTraversal = focusTraversal;
        dialog = DialogSceneComposer.dialog;
        textArea = (DialogSceneComposerModel.SimTextArea) dialog.simActor;
        previousFocusTraversal = textArea.focusTraversal;
    }
    
    @Override
    public void undo() {
        textArea.focusTraversal = previousFocusTraversal;
        
        if (dialog.simActor != textArea) {
            dialog.simActor = textArea;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textArea.focusTraversal = focusTraversal;
        
        if (dialog.simActor != textArea) {
            dialog.simActor = textArea;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextArea focus traversal " + focusTraversal + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextArea focus traversal " + focusTraversal + "\"";
    }
}
