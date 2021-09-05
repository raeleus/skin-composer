package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.utils.Scaling;
import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ImageResetUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimImage image;
    private DialogSceneComposer dialog;
    private String previousName;
    private DrawableData previousDrawable;
    private String previousScaling;
    
    public ImageResetUndoable() {
        dialog = DialogSceneComposer.dialog;
        image = (DialogSceneComposerModel.SimImage) dialog.simActor;
        
        previousName = image.name;
        previousDrawable = image.drawable;
        previousScaling = image.scaling;
    }
    
    @Override
    public void undo() {
        image.name = previousName;
        image.drawable = previousDrawable;
        image.scaling = previousScaling;
    
        if (dialog.simActor != image) {
            dialog.simActor = image;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        image.reset();
        
        if (dialog.simActor != image) {
            dialog.simActor = image;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Reset Image\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Reset Image\"";
    }
}
