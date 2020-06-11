package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimLabel;

public class LabelVisibleUndoable implements SceneComposerUndoable {
    private SimLabel label;
    private DialogSceneComposer dialog;
    private boolean visible;
    private boolean previousVisible;
    
    public LabelVisibleUndoable(boolean visible) {
        this.visible = visible;
        dialog = DialogSceneComposer.dialog;
        label = (SimLabel) dialog.simActor;
        previousVisible = label.visible;
    }
    
    @Override
    public void undo() {
        label.visible = previousVisible;
        
        if (dialog.simActor != label) {
            dialog.simActor = label;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        label.visible = visible;
        
        if (dialog.simActor != label) {
            dialog.simActor = label;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Label visible " + visible + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Label visible " + visible + "\"";
    }
}
