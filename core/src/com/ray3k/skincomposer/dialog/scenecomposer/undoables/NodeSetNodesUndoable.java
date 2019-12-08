package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class NodeSetNodesUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimNode node;
    private Array<DialogSceneComposerModel.SimNode> nodes;
    private Array<DialogSceneComposerModel.SimNode> previousNodes;
    private DialogSceneComposer dialog;
    
    public NodeSetNodesUndoable(Array<DialogSceneComposerModel.SimNode> nodes) {
        this.nodes = new Array<>(nodes);
        dialog = DialogSceneComposer.dialog;
        node = (DialogSceneComposerModel.SimNode) dialog.simActor;
        if (nodes != null && nodes.equals("")) {
            this.nodes = null;
        }
        previousNodes = new Array<>(node.nodes);
    }
    
    @Override
    public void undo() {
        node.nodes.clear();
        node.nodes.addAll(previousNodes);
    
        if (dialog.simActor != node) {
            dialog.simActor = node;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        node.nodes.clear();
        node.nodes.addAll(nodes);
    
        if (dialog.simActor != node) {
            dialog.simActor = node;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"HorizontalGroup children\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"HorizontalGroup children\"";
    }
}
