package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class VerticalGroupChildrenUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimVerticalGroup verticalGroup;
    private Array<DialogSceneComposerModel.SimActor> children;
    private Array<DialogSceneComposerModel.SimActor> previousChildren;
    private DialogSceneComposer dialog;
    
    public VerticalGroupChildrenUndoable(Array<DialogSceneComposerModel.SimActor> children) {
        this.children = new Array<>(children);
        dialog = DialogSceneComposer.dialog;
        verticalGroup = (DialogSceneComposerModel.SimVerticalGroup) dialog.simActor;
        if (children != null && children.equals("")) {
            this.children = null;
        }
        previousChildren = new Array<>(verticalGroup.children);
    }
    
    @Override
    public void undo() {
        verticalGroup.children.clear();
        verticalGroup.children.addAll(previousChildren);
    
        if (dialog.simActor != verticalGroup) {
            dialog.simActor = verticalGroup;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        verticalGroup.children.clear();
        verticalGroup.children.addAll(children);
    
        if (dialog.simActor != verticalGroup) {
            dialog.simActor = verticalGroup;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"VerticalGroup children\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"VerticalGroup children\"";
    }
}
