package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextAreaPreferredRowUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextArea textArea;
    private DialogSceneComposer dialog;
    private int preferredRows;
    private int previousPreferredRows;
    
    public TextAreaPreferredRowUndoable(int preferredRows) {
        this.preferredRows = preferredRows;
        dialog = DialogSceneComposer.dialog;
        textArea = (DialogSceneComposerModel.SimTextArea) dialog.simActor;
        
        previousPreferredRows = textArea.preferredRows;
    }
    
    @Override
    public void undo() {
        textArea.preferredRows = previousPreferredRows;
        
        if (dialog.simActor != textArea) {
            dialog.simActor = textArea;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textArea.preferredRows = preferredRows;
        
        if (dialog.simActor != textArea) {
            dialog.simActor = textArea;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextArea preferred rows " + preferredRows + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextArea preferred rows " + preferredRows + "\"";
    }
}
