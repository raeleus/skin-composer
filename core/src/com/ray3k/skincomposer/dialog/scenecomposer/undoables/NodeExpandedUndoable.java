package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class NodeExpandedUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimNode node;
    private DialogSceneComposer dialog;
    private boolean expanded;
    private boolean previousExpanded;
    
    public NodeExpandedUndoable(boolean expanded) {
        this.expanded = expanded;
        dialog = DialogSceneComposer.dialog;
        node = (DialogSceneComposerModel.SimNode) dialog.simActor;
        previousExpanded = node.expanded;
    }
    
    @Override
    public void undo() {
        node.expanded = previousExpanded;
        
        if (dialog.simActor != node) {
            dialog.simActor = node;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        node.expanded = expanded;
        
        if (dialog.simActor != node) {
            dialog.simActor = node;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Node expanded " + expanded + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Node expanded " + expanded + "\"";
    }
}
