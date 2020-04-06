package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimRootGroup;

public class TableDeleteUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTable table;
    private DialogSceneComposerModel.SimActor parent;
    private DialogSceneComposer dialog;
    
    public TableDeleteUndoable() {
        dialog = DialogSceneComposer.dialog;
        table = (DialogSceneComposerModel.SimTable) dialog.simActor;
        parent = table.parent;
    }
    
    @Override
    public void undo() {
        if (parent instanceof DialogSceneComposerModel.SimCell) {
            ((DialogSceneComposerModel.SimCell) parent).child = table;
        } else if (parent instanceof SimRootGroup) {
            ((SimRootGroup) parent).children.add(table);
        }
        
        if (dialog.simActor != table) {
            dialog.simActor = table;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        if (parent instanceof DialogSceneComposerModel.SimCell) {
            ((DialogSceneComposerModel.SimCell) parent).child = null;
        } else if (parent instanceof SimRootGroup) {
            ((SimRootGroup) parent).children.removeValue(table, true);
        }
        
        if (dialog.simActor != parent) {
            dialog.simActor = parent;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Delete Table\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Delete Table\"";
    }
}
