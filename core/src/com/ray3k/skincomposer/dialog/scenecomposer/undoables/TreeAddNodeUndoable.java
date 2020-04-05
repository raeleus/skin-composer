package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimNode;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimTree;

public class TreeAddNodeUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTree tree;
    private SimNode child;
    private DialogSceneComposer dialog;
    
    public TreeAddNodeUndoable() {
        dialog = DialogSceneComposer.dialog;
        tree = (SimTree) dialog.simActor;
        child = new SimNode();
        child.parent = tree;
    }
    
    @Override
    public void undo() {
        tree.children.removeValue(child, true);
    
        if (dialog.simActor != tree) {
            dialog.simActor = tree;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        tree.children.add(child);
    
        if (dialog.simActor != tree) {
            dialog.simActor = tree;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Add node to Tree\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Add node to Tree\"";
    }
}
