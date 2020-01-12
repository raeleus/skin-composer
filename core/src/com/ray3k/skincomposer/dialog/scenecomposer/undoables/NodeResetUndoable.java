package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class NodeResetUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimNode node;
    private DialogSceneComposer dialog;
    private DialogSceneComposerModel.SimActor previousActor;
    private Array<DialogSceneComposerModel.SimNode> previousNodes;
    private boolean previousExpanded;
    private DrawableData previousIcon;
    private boolean previousSelectable;
    
    public NodeResetUndoable() {
        dialog = DialogSceneComposer.dialog;
        node = (DialogSceneComposerModel.SimNode) dialog.simActor;
        previousActor = node.actor;
        previousNodes = new Array<>(node.nodes);
        previousExpanded = node.expanded;
        previousIcon = node.icon;
        previousSelectable = node.selectable;
    }
    
    @Override
    public void undo() {
        node.actor = previousActor;
        node.nodes.clear();
        node.nodes.addAll(previousNodes);
        node.expanded = previousExpanded;
        node.icon = previousIcon;
        node.selectable = previousSelectable;
        
        if (dialog.simActor != node) {
            dialog.simActor = node;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        node.reset();
        
        if (dialog.simActor != node) {
            dialog.simActor = node;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Reset Node\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Reset Node\"";
    }
}
