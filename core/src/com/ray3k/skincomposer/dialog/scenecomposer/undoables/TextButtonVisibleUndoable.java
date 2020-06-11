package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimTextButton;

public class TextButtonVisibleUndoable implements SceneComposerUndoable {
    private SimTextButton textButton;
    private DialogSceneComposer dialog;
    private boolean visible;
    private boolean previousVisible;
    
    public TextButtonVisibleUndoable(boolean visible) {
        this.visible = visible;
        dialog = DialogSceneComposer.dialog;
        textButton = (SimTextButton) dialog.simActor;
        previousVisible = textButton.visible;
    }
    
    @Override
    public void undo() {
        textButton.visible = previousVisible;
        
        if (dialog.simActor != textButton) {
            dialog.simActor = textButton;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textButton.visible = visible;
        
        if (dialog.simActor != textButton) {
            dialog.simActor = textButton;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextButton visible " + visible + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextButton visible " + visible + "\"";
    }
}
