package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class StackNameUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimStack stack;
    private String name;
    private String previousName;
    private DialogSceneComposer dialog;
    
    public StackNameUndoable(String name) {
        this.name = name;
        dialog = DialogSceneComposer.dialog;
        stack = (DialogSceneComposerModel.SimStack) dialog.simActor;
        if (name != null && name.equals("")) {
            this.name = null;
        }
        previousName = stack.name;
    }
    
    @Override
    public void undo() {
        stack.name = previousName;
    
        if (dialog.simActor != stack) {
            dialog.simActor = stack;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        stack.name = name;
    
        if (dialog.simActor != stack) {
            dialog.simActor = stack;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Stack name " + name + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Stack name " + name + "\"";
    }
}
