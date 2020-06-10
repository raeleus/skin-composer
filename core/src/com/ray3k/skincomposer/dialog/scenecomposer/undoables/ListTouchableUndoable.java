package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimButton;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimList;

public class ListTouchableUndoable implements SceneComposerUndoable {
    private SimList list;
    private DialogSceneComposer dialog;
    private Touchable touchable;
    private Touchable previousTouchable;
    
    public ListTouchableUndoable(Touchable touchable) {
        this.touchable = touchable;
        dialog = DialogSceneComposer.dialog;
        list = (SimList) dialog.simActor;
        previousTouchable = list.touchable;
    }
    
    @Override
    public void undo() {
        list.touchable = previousTouchable;
        
        if (dialog.simActor != list) {
            dialog.simActor = list;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        list.touchable = touchable;
        
        if (dialog.simActor != list) {
            dialog.simActor = list;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"List touchable " + touchable + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"List touchable " + touchable + "\"";
    }
}
