package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TreeIconSpaceLeftUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTree tree;
    private DialogSceneComposer dialog;
    private float iconSpaceLeft;
    private float previousIconSpaceLeft;
    
    public TreeIconSpaceLeftUndoable(float iconSpaceLeft) {
        this.iconSpaceLeft = iconSpaceLeft;
        dialog = DialogSceneComposer.dialog;
        tree = (DialogSceneComposerModel.SimTree) dialog.simActor;
        previousIconSpaceLeft = tree.iconSpaceLeft;
    }
    
    @Override
    public void undo() {
        tree.iconSpaceLeft = previousIconSpaceLeft;
        
        if (dialog.simActor != tree) {
            dialog.simActor = tree;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        tree.iconSpaceLeft = iconSpaceLeft;
        
        if (dialog.simActor != tree) {
            dialog.simActor = tree;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Tree icon space left " + iconSpaceLeft + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Tree icon space left " + iconSpaceLeft + "\"";
    }
}
