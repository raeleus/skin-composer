package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ImageButtonDeleteUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimImageButton button;
    private DialogSceneComposerModel.SimActor parent;
    private DialogSceneComposer dialog;
    
    public ImageButtonDeleteUndoable() {
        dialog = DialogSceneComposer.dialog;
        button = (DialogSceneComposerModel.SimImageButton) dialog.simActor;
        parent = button.parent;
    }
    
    @Override
    public void undo() {
        if (parent instanceof DialogSceneComposerModel.SimCell) {
            ((DialogSceneComposerModel.SimCell) parent).child = button;
        } else if (parent instanceof DialogSceneComposerModel.SimGroup) {
            ((DialogSceneComposerModel.SimGroup) parent).children.add(button);
        }
        
        if (dialog.simActor != button) {
            dialog.simActor = button;
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
            ((DialogSceneComposerModel.SimGroup) parent).children.removeValue(button, true);
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
        return "Redo \"Delete ImageButton\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Delete ImageButton\"";
    }
}
