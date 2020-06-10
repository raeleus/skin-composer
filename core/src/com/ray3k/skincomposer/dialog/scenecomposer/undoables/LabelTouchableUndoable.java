package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimButton;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimLabel;

public class LabelTouchableUndoable implements SceneComposerUndoable {
    private SimLabel label;
    private DialogSceneComposer dialog;
    private Touchable touchable;
    private Touchable previousTouchable;
    
    public LabelTouchableUndoable(Touchable touchable) {
        this.touchable = touchable;
        dialog = DialogSceneComposer.dialog;
        label = (SimLabel) dialog.simActor;
        previousTouchable = label.touchable;
    }
    
    @Override
    public void undo() {
        label.touchable = previousTouchable;
        
        if (dialog.simActor != label) {
            dialog.simActor = label;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        label.touchable = touchable;
        
        if (dialog.simActor != label) {
            dialog.simActor = label;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Label touchable " + touchable + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Label touchable " + touchable + "\"";
    }
}
