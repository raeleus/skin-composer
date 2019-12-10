package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ActorDeleteUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimActor actor;
    private DialogSceneComposerModel.SimActor parent;
    private DialogSceneComposer dialog;
    
    public ActorDeleteUndoable() {
        dialog = DialogSceneComposer.dialog;
        actor = dialog.simActor;
        parent = actor.parent;
    }
    
    @Override
    public void undo() {
        if (parent instanceof DialogSceneComposerModel.SimCell) {
            ((DialogSceneComposerModel.SimCell) parent).child = actor;
        } else if (parent instanceof DialogSceneComposerModel.SimGroup) {
            ((DialogSceneComposerModel.SimGroup) parent).children.add(actor);
        }
        
        if (dialog.simActor != actor) {
            dialog.simActor = actor;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        if (parent instanceof DialogSceneComposerModel.SimCell) {
            ((DialogSceneComposerModel.SimCell) parent).child = null;
        } else if (parent instanceof DialogSceneComposerModel.SimGroup) {
            ((DialogSceneComposerModel.SimGroup) parent).children.removeValue(actor, true);
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
        return "Redo \"Delete Actor\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Delete Actor\"";
    }
}
