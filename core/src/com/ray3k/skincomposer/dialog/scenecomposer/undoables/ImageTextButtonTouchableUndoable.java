package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimButton;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimImageTextButton;

public class ImageTextButtonTouchableUndoable implements SceneComposerUndoable {
    private SimImageTextButton imageTextButton;
    private DialogSceneComposer dialog;
    private Touchable touchable;
    private Touchable previousTouchable;
    
    public ImageTextButtonTouchableUndoable(Touchable touchable) {
        this.touchable = touchable;
        dialog = DialogSceneComposer.dialog;
        imageTextButton = (SimImageTextButton) dialog.simActor;
        previousTouchable = imageTextButton.touchable;
    }
    
    @Override
    public void undo() {
        imageTextButton.touchable = previousTouchable;
        
        if (dialog.simActor != imageTextButton) {
            dialog.simActor = imageTextButton;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        imageTextButton.touchable = touchable;
        
        if (dialog.simActor != imageTextButton) {
            dialog.simActor = imageTextButton;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ImageTextButton touchable " + touchable + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ImageTextButton touchable " + touchable + "\"";
    }
}
