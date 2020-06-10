package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimButton;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimImage;

public class ImageTouchableUndoable implements SceneComposerUndoable {
    private SimImage image;
    private DialogSceneComposer dialog;
    private Touchable touchable;
    private Touchable previousTouchable;
    
    public ImageTouchableUndoable(Touchable touchable) {
        this.touchable = touchable;
        dialog = DialogSceneComposer.dialog;
        image = (SimImage) dialog.simActor;
        previousTouchable = image.touchable;
    }
    
    @Override
    public void undo() {
        image.touchable = previousTouchable;
        
        if (dialog.simActor != image) {
            dialog.simActor = image;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        image.touchable = touchable;
        
        if (dialog.simActor != image) {
            dialog.simActor = image;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Image touchable " + touchable + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Image touchable " + touchable + "\"";
    }
}
