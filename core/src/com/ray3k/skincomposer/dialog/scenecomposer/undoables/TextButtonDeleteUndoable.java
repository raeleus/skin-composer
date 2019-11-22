package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextButtonDeleteUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextButton textButton;
    private DialogSceneComposerModel.SimActor parent;
    private DialogSceneComposer dialog;
    
    public TextButtonDeleteUndoable() {
        dialog = DialogSceneComposer.dialog;
        textButton = (DialogSceneComposerModel.SimTextButton) dialog.simActor;
        parent = textButton.parent;
    }
    
    @Override
    public void undo() {
        if (parent instanceof DialogSceneComposerModel.SimCell) {
            ((DialogSceneComposerModel.SimCell) parent).child = textButton;
        } else if (parent instanceof DialogSceneComposerModel.SimGroup) {
            ((DialogSceneComposerModel.SimGroup) parent).children.add(textButton);
        }
        
        if (dialog.simActor != textButton) {
            dialog.simActor = textButton;
            dialog.populateProperties();
            dialog.populatePath();
        }
    }
    
    @Override
    public void redo() {
        if (parent instanceof DialogSceneComposerModel.SimCell) {
            ((DialogSceneComposerModel.SimCell) parent).child = null;
        } else if (parent instanceof DialogSceneComposerModel.SimGroup) {
            ((DialogSceneComposerModel.SimGroup) parent).children.removeValue(textButton, true);
        }
        
        if (dialog.simActor != parent) {
            dialog.simActor = parent;
            dialog.populateProperties();
            dialog.populatePath();
        }
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Delete TextButton\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Delete TextButton\"";
    }
}
