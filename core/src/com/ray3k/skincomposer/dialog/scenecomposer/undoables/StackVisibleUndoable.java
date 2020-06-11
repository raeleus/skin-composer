package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimStack;

public class StackVisibleUndoable implements SceneComposerUndoable {
    private SimStack stack;
    private DialogSceneComposer dialog;
    private boolean visible;
    private boolean previousVisible;
    
    public StackVisibleUndoable(boolean visible) {
        this.visible = visible;
        dialog = DialogSceneComposer.dialog;
        stack = (SimStack) dialog.simActor;
        previousVisible = stack.visible;
    }
    
    @Override
    public void undo() {
        stack.visible = previousVisible;
        
        if (dialog.simActor != stack) {
            dialog.simActor = stack;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        stack.visible = visible;
        
        if (dialog.simActor != stack) {
            dialog.simActor = stack;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Stack visible " + visible + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Stack visible " + visible + "\"";
    }
}
