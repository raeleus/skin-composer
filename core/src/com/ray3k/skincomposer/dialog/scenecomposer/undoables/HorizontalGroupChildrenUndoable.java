package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class HorizontalGroupChildrenUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimHorizontalGroup horizontalGroup;
    private Array<DialogSceneComposerModel.SimActor> children;
    private Array<DialogSceneComposerModel.SimActor> previousChildren;
    private DialogSceneComposer dialog;
    
    public HorizontalGroupChildrenUndoable(Array<DialogSceneComposerModel.SimActor> children) {
        this.children = new Array<>(children);
        dialog = DialogSceneComposer.dialog;
        horizontalGroup = (DialogSceneComposerModel.SimHorizontalGroup) dialog.simActor;
        if (children != null && children.equals("")) {
            this.children = null;
        }
        previousChildren = new Array<>(horizontalGroup.children);
    }
    
    @Override
    public void undo() {
        horizontalGroup.children.clear();
        horizontalGroup.children.addAll(previousChildren);
    
        if (dialog.simActor != horizontalGroup) {
            dialog.simActor = horizontalGroup;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        horizontalGroup.children.clear();
        horizontalGroup.children.addAll(children);
    
        if (dialog.simActor != horizontalGroup) {
            dialog.simActor = horizontalGroup;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"HorizontalGroup children\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"HorizontalGroup children\"";
    }
}
