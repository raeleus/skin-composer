package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextAreaSelectAllUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextArea textArea;
    private DialogSceneComposer dialog;
    private boolean selectAll;
    private boolean previousSelectAll;
    
    public TextAreaSelectAllUndoable(boolean selectAll) {
        this.selectAll = selectAll;
        dialog = DialogSceneComposer.dialog;
        textArea = (DialogSceneComposerModel.SimTextArea) dialog.simActor;
        previousSelectAll = textArea.selectAll;
    }
    
    @Override
    public void undo() {
        textArea.selectAll = previousSelectAll;
        
        if (dialog.simActor != textArea) {
            dialog.simActor = textArea;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textArea.selectAll = selectAll;
        
        if (dialog.simActor != textArea) {
            dialog.simActor = textArea;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextArea select all " + selectAll + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextArea select all " + selectAll + "\"";
    }
}
