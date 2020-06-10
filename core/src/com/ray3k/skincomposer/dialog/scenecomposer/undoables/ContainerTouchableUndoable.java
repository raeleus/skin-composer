package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimButton;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimContainer;

public class ContainerTouchableUndoable implements SceneComposerUndoable {
    private SimContainer container;
    private DialogSceneComposer dialog;
    private Touchable touchable;
    private Touchable previousTouchable;
    
    public ContainerTouchableUndoable(Touchable touchable) {
        this.touchable = touchable;
        dialog = DialogSceneComposer.dialog;
        container = (SimContainer) dialog.simActor;
        previousTouchable = container.touchable;
    }
    
    @Override
    public void undo() {
        container.touchable = previousTouchable;
        
        if (dialog.simActor != container) {
            dialog.simActor = container;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        container.touchable = touchable;
        
        if (dialog.simActor != container) {
            dialog.simActor = container;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Button touchable " + touchable + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Button touchable " + touchable + "\"";
    }
}
