package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class NodeSelectableUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimNode node;
    private DialogSceneComposer dialog;
    private boolean selectable;
    private boolean previousSelectable;
    
    public NodeSelectableUndoable(boolean selectable) {
        this.selectable = selectable;
        dialog = DialogSceneComposer.dialog;
        node = (DialogSceneComposerModel.SimNode) dialog.simActor;
        previousSelectable = node.selectable;
    }
    
    @Override
    public void undo() {
        node.selectable = previousSelectable;
        
        if (dialog.simActor != node) {
            dialog.simActor = node;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        node.selectable = selectable;
        
        if (dialog.simActor != node) {
            dialog.simActor = node;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Node selectable " + selectable + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Node selectable " + selectable + "\"";
    }
}
