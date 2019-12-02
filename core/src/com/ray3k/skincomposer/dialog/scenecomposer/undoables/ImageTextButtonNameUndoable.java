package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ImageTextButtonNameUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimImageTextButton button;
    private String name;
    private String previousName;
    private DialogSceneComposer dialog;
    
    public ImageTextButtonNameUndoable(String name) {
        this.name = name;
        dialog = DialogSceneComposer.dialog;
        button = (DialogSceneComposerModel.SimImageTextButton) dialog.simActor;
        if (name != null && name.equals("")) {
            this.name = null;
        }
        previousName = button.name;
    }
    
    @Override
    public void undo() {
        button.name = previousName;
    
        if (dialog.simActor != button) {
            dialog.simActor = button;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        button.name = name;
    
        if (dialog.simActor != button) {
            dialog.simActor = button;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ImageTextButton name " + name + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ImageTextButton name " + name + "\"";
    }
}
