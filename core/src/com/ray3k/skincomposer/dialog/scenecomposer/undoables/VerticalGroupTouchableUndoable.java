package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimButton;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimVerticalGroup;

public class VerticalGroupTouchableUndoable implements SceneComposerUndoable {
    private SimVerticalGroup verticalGroup;
    private DialogSceneComposer dialog;
    private Touchable touchable;
    private Touchable previousTouchable;
    
    public VerticalGroupTouchableUndoable(Touchable touchable) {
        this.touchable = touchable;
        dialog = DialogSceneComposer.dialog;
        verticalGroup = (SimVerticalGroup) dialog.simActor;
        previousTouchable = verticalGroup.touchable;
    }
    
    @Override
    public void undo() {
        verticalGroup.touchable = previousTouchable;
        
        if (dialog.simActor != verticalGroup) {
            dialog.simActor = verticalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        verticalGroup.touchable = touchable;
        
        if (dialog.simActor != verticalGroup) {
            dialog.simActor = verticalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"VerticalGroup touchable " + touchable + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"VerticalGroup touchable " + touchable + "\"";
    }
}
