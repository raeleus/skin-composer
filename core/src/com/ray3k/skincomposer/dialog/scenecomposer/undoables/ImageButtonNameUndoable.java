package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ImageButtonNameUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimImageButton button;
    private String name;
    private String previousName;
    private DialogSceneComposer dialog;
    
    public ImageButtonNameUndoable(String name) {
        this.name = name;
        dialog = DialogSceneComposer.dialog;
        button = (DialogSceneComposerModel.SimImageButton) dialog.simActor;
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
        return "Redo \"ImageButton name " + name + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ImageButton name " + name + "\"";
    }
}
