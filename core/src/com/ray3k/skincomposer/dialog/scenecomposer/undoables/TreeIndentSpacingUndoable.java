package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TreeIndentSpacingUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTree tree;
    private DialogSceneComposer dialog;
    private float indentSpacing;
    private float previousIndentSpacing;
    
    public TreeIndentSpacingUndoable(float indentSpacing) {
        this.indentSpacing = indentSpacing;
        dialog = DialogSceneComposer.dialog;
        tree = (DialogSceneComposerModel.SimTree) dialog.simActor;
        previousIndentSpacing = tree.indentSpacing;
    }
    
    @Override
    public void undo() {
        tree.indentSpacing = previousIndentSpacing;
        
        if (dialog.simActor != tree) {
            dialog.simActor = tree;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        tree.indentSpacing = indentSpacing;
        
        if (dialog.simActor != tree) {
            dialog.simActor = tree;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Tree indent spacing " + indentSpacing + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Tree indent spacing " + indentSpacing + "\"";
    }
}
