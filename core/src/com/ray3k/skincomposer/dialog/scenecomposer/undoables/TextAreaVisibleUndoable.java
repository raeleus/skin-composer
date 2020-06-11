package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimTextArea;

public class TextAreaVisibleUndoable implements SceneComposerUndoable {
    private SimTextArea textArea;
    private DialogSceneComposer dialog;
    private boolean visible;
    private boolean previousVisible;
    
    public TextAreaVisibleUndoable(boolean visible) {
        this.visible = visible;
        dialog = DialogSceneComposer.dialog;
        textArea = (SimTextArea) dialog.simActor;
        previousVisible = textArea.visible;
    }
    
    @Override
    public void undo() {
        textArea.visible = previousVisible;
        
        if (dialog.simActor != textArea) {
            dialog.simActor = textArea;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textArea.visible = visible;
        
        if (dialog.simActor != textArea) {
            dialog.simActor = textArea;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextArea visible " + visible + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextArea visible " + visible + "\"";
    }
}
