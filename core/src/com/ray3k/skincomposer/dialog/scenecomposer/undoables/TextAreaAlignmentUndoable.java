package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextAreaAlignmentUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextArea textArea;
    private DialogSceneComposer dialog;
    private int alignment;
    private int previousAlignment;
    
    public TextAreaAlignmentUndoable(int alignment) {
        this.alignment = alignment;
        dialog = DialogSceneComposer.dialog;
        textArea = (DialogSceneComposerModel.SimTextArea) dialog.simActor;
        
        previousAlignment = textArea.alignment;
    }
    
    @Override
    public void undo() {
        textArea.alignment = previousAlignment;
        
        if (dialog.simActor != textArea) {
            dialog.simActor = textArea;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textArea.alignment = alignment;
        
        if (dialog.simActor != textArea) {
            dialog.simActor = textArea;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextArea alignment\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextArea alignment\"";
    }
}
