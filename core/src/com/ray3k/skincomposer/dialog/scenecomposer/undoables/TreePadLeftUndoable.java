package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TreePadLeftUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTree tree;
    private DialogSceneComposer dialog;
    private float padLeft;
    private float previousPadLeft;
    
    public TreePadLeftUndoable(float padLeft) {
        this.padLeft = padLeft;
        dialog = DialogSceneComposer.dialog;
        tree = (DialogSceneComposerModel.SimTree) dialog.simActor;
        previousPadLeft = tree.padLeft;
    }
    
    @Override
    public void undo() {
        tree.padLeft = previousPadLeft;
        
        if (dialog.simActor != tree) {
            dialog.simActor = tree;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        tree.padLeft = padLeft;
        
        if (dialog.simActor != tree) {
            dialog.simActor = tree;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Tree pad left " + padLeft + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Tree pad left " + padLeft + "\"";
    }
}
