package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class StackResetUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimStack stack;
    private DialogSceneComposer dialog;
    private String previousName;
    private Array<DialogSceneComposerModel.SimActor> previousChildren;
    
    public StackResetUndoable() {
        dialog = DialogSceneComposer.dialog;
        stack = (DialogSceneComposerModel.SimStack) dialog.simActor;
        previousName = stack.name;
        previousChildren = new Array<>(stack.children);
    }
    
    @Override
    public void undo() {
        stack.name = previousName;
        stack.children.clear();
        stack.children.addAll(previousChildren);
        
        if (dialog.simActor != stack) {
            dialog.simActor = stack;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        stack.reset();
        
        if (dialog.simActor != stack) {
            dialog.simActor = stack;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Reset Stack\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Reset Stack\"";
    }
}
