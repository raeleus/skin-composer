package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimButton;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimImageButton;

public class ImageButtonTouchableUndoable implements SceneComposerUndoable {
    private SimImageButton imageButton;
    private DialogSceneComposer dialog;
    private Touchable touchable;
    private Touchable previousTouchable;
    
    public ImageButtonTouchableUndoable(Touchable touchable) {
        this.touchable = touchable;
        dialog = DialogSceneComposer.dialog;
        imageButton = (SimImageButton) dialog.simActor;
        previousTouchable = imageButton.touchable;
    }
    
    @Override
    public void undo() {
        imageButton.touchable = previousTouchable;
        
        if (dialog.simActor != imageButton) {
            dialog.simActor = imageButton;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        imageButton.touchable = touchable;
        
        if (dialog.simActor != imageButton) {
            dialog.simActor = imageButton;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ImageButton touchable " + touchable + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ImageButton touchable " + touchable + "\"";
    }
}
