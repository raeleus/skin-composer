package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class StackChildrenUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimStack stack;
    private Array<DialogSceneComposerModel.SimActor> children;
    private Array<DialogSceneComposerModel.SimActor> previousChildren;
    private DialogSceneComposer dialog;
    
    public StackChildrenUndoable(Array<DialogSceneComposerModel.SimActor> children) {
        this.children = new Array<>(children);
        dialog = DialogSceneComposer.dialog;
        stack = (DialogSceneComposerModel.SimStack) dialog.simActor;
        if (children != null && children.equals("")) {
            this.children = null;
        }
        previousChildren = new Array<>(stack.children);
    }
    
    @Override
    public void undo() {
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
        stack.children.clear();
        stack.children.addAll(children);
    
        if (dialog.simActor != stack) {
            dialog.simActor = stack;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Stack children\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Stack children\"";
    }
}
