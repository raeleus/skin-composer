package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TreeIconSpaceRightUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTree tree;
    private DialogSceneComposer dialog;
    private float iconSpaceRight;
    private float previousIconSpaceRight;
    
    public TreeIconSpaceRightUndoable(float iconSpaceRight) {
        this.iconSpaceRight = iconSpaceRight;
        dialog = DialogSceneComposer.dialog;
        tree = (DialogSceneComposerModel.SimTree) dialog.simActor;
        previousIconSpaceRight = tree.iconSpaceRight;
    }
    
    @Override
    public void undo() {
        tree.iconSpaceRight = previousIconSpaceRight;
        
        if (dialog.simActor != tree) {
            dialog.simActor = tree;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        tree.iconSpaceRight = iconSpaceRight;
        
        if (dialog.simActor != tree) {
            dialog.simActor = tree;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Tree icon space right " + iconSpaceRight + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Tree icon space right " + iconSpaceRight + "\"";
    }
}
