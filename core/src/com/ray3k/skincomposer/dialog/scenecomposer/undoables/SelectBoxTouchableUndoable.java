package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimButton;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimSelectBox;

public class SelectBoxTouchableUndoable implements SceneComposerUndoable {
    private SimSelectBox selectBox;
    private DialogSceneComposer dialog;
    private Touchable touchable;
    private Touchable previousTouchable;
    
    public SelectBoxTouchableUndoable(Touchable touchable) {
        this.touchable = touchable;
        dialog = DialogSceneComposer.dialog;
        selectBox = (SimSelectBox) dialog.simActor;
        previousTouchable = selectBox.touchable;
    }
    
    @Override
    public void undo() {
        selectBox.touchable = previousTouchable;
        
        if (dialog.simActor != selectBox) {
            dialog.simActor = selectBox;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        selectBox.touchable = touchable;
        
        if (dialog.simActor != selectBox) {
            dialog.simActor = selectBox;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"SelectBox touchable " + touchable + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"SelectBox touchable " + touchable + "\"";
    }
}
