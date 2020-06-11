package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimTextField;

public class TextFieldVisibleUndoable implements SceneComposerUndoable {
    private SimTextField textField;
    private DialogSceneComposer dialog;
    private boolean visible;
    private boolean previousVisible;
    
    public TextFieldVisibleUndoable(boolean visible) {
        this.visible = visible;
        dialog = DialogSceneComposer.dialog;
        textField = (SimTextField) dialog.simActor;
        previousVisible = textField.visible;
    }
    
    @Override
    public void undo() {
        textField.visible = previousVisible;
        
        if (dialog.simActor != textField) {
            dialog.simActor = textField;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textField.visible = visible;
        
        if (dialog.simActor != textField) {
            dialog.simActor = textField;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextField visible " + visible + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextField visible " + visible + "\"";
    }
}
