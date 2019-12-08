package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TreeStyleUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTree tree;
    private StyleData style;
    private StyleData previousStyle;
    private DialogSceneComposer dialog;
    
    public TreeStyleUndoable(StyleData style) {
        this.style = style;
        dialog = DialogSceneComposer.dialog;
        tree = (DialogSceneComposerModel.SimTree) dialog.simActor;
        previousStyle = tree.style;
    }
    
    @Override
    public void undo() {
        tree.style = previousStyle;
        
        if (dialog.simActor != tree) {
            dialog.simActor = tree;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        tree.style = style;
        
        if (dialog.simActor != tree) {
            dialog.simActor = tree;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Tree style: " + style.name + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Tree style: " + style.name + "\"";
    }
}
