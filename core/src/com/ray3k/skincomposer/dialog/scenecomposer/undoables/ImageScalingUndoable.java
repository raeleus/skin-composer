package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.utils.Scaling;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ImageScalingUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimImage image;
    private String scaling;
    private String previousScaling;
    private DialogSceneComposer dialog;
    
    public ImageScalingUndoable(String scaling) {
        this.scaling = scaling;
        dialog = DialogSceneComposer.dialog;
        image = (DialogSceneComposerModel.SimImage) dialog.simActor;
        if (scaling != null && scaling.equals("")) {
            this.scaling = null;
        }
        previousScaling = image.scaling;
    }
    
    @Override
    public void undo() {
        image.scaling = previousScaling;
    
        if (dialog.simActor != image) {
            dialog.simActor = image;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        image.scaling = scaling;
    
        if (dialog.simActor != image) {
            dialog.simActor = image;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Image scaling " + scaling + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Image scaling " + scaling + "\"";
    }
}
