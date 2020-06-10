package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimButton;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimStack;

public class StackTouchableUndoable implements SceneComposerUndoable {
    private SimStack stack;
    private DialogSceneComposer dialog;
    private Touchable touchable;
    private Touchable previousTouchable;
    
    public StackTouchableUndoable(Touchable touchable) {
        this.touchable = touchable;
        dialog = DialogSceneComposer.dialog;
        stack = (SimStack) dialog.simActor;
        previousTouchable = stack.touchable;
    }
    
    @Override
    public void undo() {
        stack.touchable = previousTouchable;
        
        if (dialog.simActor != stack) {
            dialog.simActor = stack;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        stack.touchable = touchable;
        
        if (dialog.simActor != stack) {
            dialog.simActor = stack;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Stack touchable " + touchable + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Stack touchable " + touchable + "\"";
    }
}
