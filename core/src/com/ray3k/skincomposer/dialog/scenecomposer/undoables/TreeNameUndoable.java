package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TreeNameUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTree tree;
    private String name;
    private String previousName;
    private DialogSceneComposer dialog;
    
    public TreeNameUndoable(String name) {
        this.name = name;
        dialog = DialogSceneComposer.dialog;
        tree = (DialogSceneComposerModel.SimTree) dialog.simActor;
        if (name != null && name.equals("")) {
            this.name = null;
        }
        previousName = tree.name;
    }
    
    @Override
    public void undo() {
        tree.name = previousName;
    
        if (dialog.simActor != tree) {
            dialog.simActor = tree;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        tree.name = name;
    
        if (dialog.simActor != tree) {
            dialog.simActor = tree;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Tree name " + name + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Tree name " + name + "\"";
    }
}
