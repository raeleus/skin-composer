package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimButton;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimHorizontalGroup;

public class HorizontalGroupTouchableUndoable implements SceneComposerUndoable {
    private SimHorizontalGroup horizontalGroup;
    private DialogSceneComposer dialog;
    private Touchable touchable;
    private Touchable previousTouchable;
    
    public HorizontalGroupTouchableUndoable(Touchable touchable) {
        this.touchable = touchable;
        dialog = DialogSceneComposer.dialog;
        horizontalGroup = (SimHorizontalGroup) dialog.simActor;
        previousTouchable = horizontalGroup.touchable;
    }
    
    @Override
    public void undo() {
        horizontalGroup.touchable = previousTouchable;
        
        if (dialog.simActor != horizontalGroup) {
            dialog.simActor = horizontalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        horizontalGroup.touchable = touchable;
        
        if (dialog.simActor != horizontalGroup) {
            dialog.simActor = horizontalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"HorizontalGroup touchable " + touchable + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"HorizontalGroup touchable " + touchable + "\"";
    }
}
