package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimNode;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimTree;

public class NodeAddNodeUndoable implements SceneComposerUndoable {
    private SimNode node;
    private SimNode child;
    private DialogSceneComposer dialog;
    
    public NodeAddNodeUndoable() {
        dialog = DialogSceneComposer.dialog;
        node = (SimNode) dialog.simActor;
        child = new SimNode();
        child.parent = node;
    }
    
    @Override
    public void undo() {
        node.nodes.removeValue(child, true);
        
        if (dialog.simActor != node) {
            dialog.simActor = node;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        node.nodes.add(child);
        
        if (dialog.simActor != child) {
            dialog.simActor = child;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Add node to node.\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Add node to node.\"";
    }
}
