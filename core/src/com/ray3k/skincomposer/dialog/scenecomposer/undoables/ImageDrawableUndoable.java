package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ImageDrawableUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimImage image;
    private DrawableData drawable;
    private DrawableData previousDrawable;
    private DialogSceneComposer dialog;
    
    public ImageDrawableUndoable(DrawableData drawable) {
        dialog = DialogSceneComposer.dialog;
        image = (DialogSceneComposerModel.SimImage) dialog.simActor;
        this.drawable = drawable;
        previousDrawable = image.drawable;
    }
    
    @Override
    public void undo() {
        image.drawable = previousDrawable;
        
        if (dialog.simActor != image) {
            dialog.simActor = image;
            dialog.populatePath();
        }
        dialog.populateProperties();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        image.drawable = drawable;
        
        if (dialog.simActor != image) {
            dialog.simActor = image;
            dialog.populatePath();
        }
        dialog.populateProperties();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Image Drawable: " + (drawable == null ? "null" : drawable.name) + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Image Drawable: " + (drawable == null ? "null" : drawable.name) + "\"";
    }
}
