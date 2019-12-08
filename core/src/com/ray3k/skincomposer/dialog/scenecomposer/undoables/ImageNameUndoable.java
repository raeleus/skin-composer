package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ImageNameUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimImage image;
    private String name;
    private String previousName;
    private DialogSceneComposer dialog;
    
    public ImageNameUndoable(String name) {
        this.name = name;
        dialog = DialogSceneComposer.dialog;
        image = (DialogSceneComposerModel.SimImage) dialog.simActor;
        if (name != null && name.equals("")) {
            this.name = null;
        }
        previousName = image.name;
    }
    
    @Override
    public void undo() {
        image.name = previousName;
    
        if (dialog.simActor != image) {
            dialog.simActor = image;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        image.name = name;
    
        if (dialog.simActor != image) {
            dialog.simActor = image;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Image name " + name + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Image name " + name + "\"";
    }
}
