package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TreeYSpacingUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTree tree;
    private DialogSceneComposer dialog;
    private float ySpacing;
    private float previousYSpacing;
    
    public TreeYSpacingUndoable(float ySpacing) {
        this.ySpacing = ySpacing;
        dialog = DialogSceneComposer.dialog;
        tree = (DialogSceneComposerModel.SimTree) dialog.simActor;
        previousYSpacing = tree.ySpacing;
    }
    
    @Override
    public void undo() {
        tree.ySpacing = previousYSpacing;
        
        if (dialog.simActor != tree) {
            dialog.simActor = tree;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        tree.ySpacing = ySpacing;
        
        if (dialog.simActor != tree) {
            dialog.simActor = tree;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Tree y spacing " + ySpacing + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Tree y spacing " + ySpacing + "\"";
    }
}
