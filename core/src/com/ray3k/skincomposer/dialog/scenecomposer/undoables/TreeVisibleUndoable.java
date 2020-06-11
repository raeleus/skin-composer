package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimTree;

public class TreeVisibleUndoable implements SceneComposerUndoable {
    private SimTree tree;
    private DialogSceneComposer dialog;
    private boolean visible;
    private boolean previousVisible;
    
    public TreeVisibleUndoable(boolean visible) {
        this.visible = visible;
        dialog = DialogSceneComposer.dialog;
        tree = (SimTree) dialog.simActor;
        previousVisible = tree.visible;
    }
    
    @Override
    public void undo() {
        tree.visible = previousVisible;
        
        if (dialog.simActor != tree) {
            dialog.simActor = tree;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        tree.visible = visible;
        
        if (dialog.simActor != tree) {
            dialog.simActor = tree;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Tree visible " + visible + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Tree visible " + visible + "\"";
    }
}
