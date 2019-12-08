package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TreeChildrenUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTree tree;
    private Array<DialogSceneComposerModel.SimNode> children;
    private Array<DialogSceneComposerModel.SimNode> previousChildren;
    private DialogSceneComposer dialog;
    
    public TreeChildrenUndoable(Array<DialogSceneComposerModel.SimNode> children) {
        this.children = new Array<>(children);
        dialog = DialogSceneComposer.dialog;
        tree = (DialogSceneComposerModel.SimTree) dialog.simActor;
        if (children != null && children.equals("")) {
            this.children = null;
        }
        previousChildren = new Array<>(tree.children);
    }
    
    @Override
    public void undo() {
        tree.children.clear();
        tree.children.addAll(previousChildren);
    
        if (dialog.simActor != tree) {
            dialog.simActor = tree;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        tree.children.clear();
        tree.children.addAll(children);
    
        if (dialog.simActor != tree) {
            dialog.simActor = tree;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Tree children\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Tree children\"";
    }
}
